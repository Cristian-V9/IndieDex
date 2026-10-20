package com.indiedex.service;

import com.indiedex.dto.*;
import com.indiedex.exception.ConflictoException;
import com.indiedex.exception.PermisoDenegadoException;
import com.indiedex.exception.RecursoNoEncontradoException;
import com.indiedex.exception.ReglaNegocioException;
import com.indiedex.model.*;
import com.indiedex.repository.SugerenciaRepository;
import com.indiedex.repository.UsuarioRepository;
import com.indiedex.repository.VotoRepository;
import com.indiedex.security.UsuarioPrincipal;
import com.indiedex.util.Paginas;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Flujo colaborativo de la presentación:
 *   1. Sugerencia: un usuario propone algo.
 *   2. Votos: al llegar al umbral de votos pasa sola a este estado.
 *   3. Revisión: el equipo la aprueba para revisarla.
 *   4. Publicado: el contenido llegó a la wiki.
 * En cualquier paso abierto el equipo puede rechazarla dejando una nota.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SugerenciaService {

    private static final List<EstadoSugerencia> VISIBLES_POR_DEFECTO = List.of(
            EstadoSugerencia.SUGERENCIA, EstadoSugerencia.VOTOS,
            EstadoSugerencia.REVISION, EstadoSugerencia.PUBLICADO);

    private final SugerenciaRepository sugerenciaRepository;
    private final VotoRepository votoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JuegoService juegoService;

    @Value("${indiedex.sugerencias.umbral-votos}")
    private int umbralVotos;

    /**
     * Lista filtrada. Sin estados muestra todas menos las rechazadas.
     * @param orden "votos" (más votadas) o "recientes"
     */
    public PaginaResponse<SugerenciaResponse> listar(List<EstadoSugerencia> estados, TipoSugerencia tipo,
                                                     Long juegoId, String orden, int pagina, int tamano,
                                                     UsuarioPrincipal yo) {
        List<EstadoSugerencia> filtro = (estados == null || estados.isEmpty()) ? VISIBLES_POR_DEFECTO : estados;
        Sort sort = "recientes".equalsIgnoreCase(orden)
                ? Sort.by(Sort.Direction.DESC, "fechaCreacion")
                : Sort.by(Sort.Direction.DESC, "totalVotos").and(Sort.by(Sort.Direction.DESC, "fechaCreacion"));

        Page<Sugerencia> page = sugerenciaRepository.buscar(filtro, tipo, juegoId, Paginas.de(pagina, tamano, sort));
        Set<Long> votadas = votadasPor(yo, page.getContent());
        return PaginaResponse.desde(page, s -> SugerenciaResponse.desde(s, votadas.contains(s.getId())));
    }

    public SugerenciaResponse obtener(Long id, UsuarioPrincipal yo) {
        Sugerencia s = buscarEntidad(id);
        boolean votada = yo != null && votoRepository.existsByUsuarioIdAndSugerenciaId(yo.getId(), id);
        return SugerenciaResponse.desde(s, votada);
    }

    @Transactional
    public SugerenciaResponse crear(SugerenciaRequest req, UsuarioPrincipal yo) {
        if (req.tipo() != TipoSugerencia.NUEVA_WIKI && req.juegoId() == null) {
            throw new ReglaNegocioException("Indica a qué juego se refiere la propuesta");
        }
        Sugerencia s = new Sugerencia();
        s.setTitulo(req.titulo().trim());
        s.setDescripcion(req.descripcion().trim());
        s.setTipo(req.tipo());
        s.setEstado(EstadoSugerencia.SUGERENCIA);
        s.setTotalVotos(0);
        s.setJuego(req.juegoId() == null ? null : juegoService.buscarEntidad(req.juegoId()));
        s.setAutor(usuarioRepository.getReferenceById(yo.getId()));
        return SugerenciaResponse.desde(sugerenciaRepository.save(s), false);
    }

    /** Votar. Al llegar al umbral, pasa de "Sugerencia" a "Votos". */
    @Transactional
    public VotoResponse votar(Long id, UsuarioPrincipal yo) {
        Sugerencia s = buscarEntidad(id);
        if (!s.getEstado().estaAbierta()) {
            throw new ReglaNegocioException("Esta propuesta ya no admite votos");
        }
        if (votoRepository.existsByUsuarioIdAndSugerenciaId(yo.getId(), id)) {
            throw new ConflictoException("Ya votaste esta propuesta");
        }

        Voto voto = new Voto();
        voto.setUsuario(usuarioRepository.getReferenceById(yo.getId()));
        voto.setSugerencia(s);
        votoRepository.save(voto);

        s.setTotalVotos(s.getTotalVotos() + 1);
        if (s.getEstado() == EstadoSugerencia.SUGERENCIA && s.getTotalVotos() >= umbralVotos) {
            s.setEstado(EstadoSugerencia.VOTOS);
        }
        return new VotoResponse(id, s.getTotalVotos(), true, s.getEstado());
    }

    @Transactional
    public VotoResponse retirarVoto(Long id, UsuarioPrincipal yo) {
        Sugerencia s = buscarEntidad(id);
        Voto voto = votoRepository.findByUsuarioIdAndSugerenciaId(yo.getId(), id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No has votado esta propuesta"));
        if (!s.getEstado().estaAbierta()) {
            throw new ReglaNegocioException("Esta propuesta ya está cerrada");
        }
        votoRepository.delete(voto);
        s.setTotalVotos(Math.max(0, s.getTotalVotos() - 1));
        return new VotoResponse(id, s.getTotalVotos(), false, s.getEstado());
    }

    /** "Aprobar" en el panel: lleva la sugerencia al siguiente paso del flujo. */
    @Transactional
    public SugerenciaResponse avanzar(Long id, String nota, UsuarioPrincipal yo) {
        Sugerencia s = buscarEntidad(id);
        EstadoSugerencia siguiente = s.getEstado().siguiente();
        if (siguiente == null) {
            throw new ReglaNegocioException("La propuesta ya terminó su recorrido (" + s.getEstado() + ")");
        }
        s.setEstado(siguiente);
        registrarRevision(s, nota, yo);
        return SugerenciaResponse.desde(s, votoRepository.existsByUsuarioIdAndSugerenciaId(yo.getId(), id));
    }

    @Transactional
    public SugerenciaResponse rechazar(Long id, String nota, UsuarioPrincipal yo) {
        Sugerencia s = buscarEntidad(id);
        if (!s.getEstado().estaAbierta()) {
            throw new ReglaNegocioException("La propuesta ya está cerrada (" + s.getEstado() + ")");
        }
        s.setEstado(EstadoSugerencia.RECHAZADA);
        registrarRevision(s, nota, yo);
        return SugerenciaResponse.desde(s, votoRepository.existsByUsuarioIdAndSugerenciaId(yo.getId(), id));
    }

    /** El autor puede borrarla mientras nadie la haya revisado; un ADMIN, siempre. */
    @Transactional
    public void eliminar(Long id, UsuarioPrincipal yo) {
        Sugerencia s = buscarEntidad(id);
        boolean esAutor = s.getAutor().getId().equals(yo.getId());
        boolean sinRevisar = s.getEstado() == EstadoSugerencia.SUGERENCIA;
        if (yo.getRol() != Rol.ADMIN && !(esAutor && sinRevisar)) {
            throw new PermisoDenegadoException("Solo puedes borrar tus propuestas que aún no se han revisado");
        }
        sugerenciaRepository.delete(s);
    }

    /** Cifras de la cabecera del panel de revisión. */
    public ResumenRevisionResponse resumen() {
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return new ResumenRevisionResponse(
                sugerenciaRepository.countByEstadoIn(List.of(EstadoSugerencia.SUGERENCIA, EstadoSugerencia.VOTOS)),
                sugerenciaRepository.countByEstado(EstadoSugerencia.REVISION),
                sugerenciaRepository.countByEstadoAndFechaRevisionGreaterThanEqual(EstadoSugerencia.PUBLICADO, inicioMes),
                sugerenciaRepository.countByEstado(EstadoSugerencia.RECHAZADA));
    }

    // ---------- Perfil ----------

    public PaginaResponse<SugerenciaResponse> deUsuario(Long usuarioId, int pagina, int tamano, UsuarioPrincipal yo) {
        Page<Sugerencia> page = sugerenciaRepository.findByAutorIdOrderByFechaCreacionDesc(usuarioId,
                Paginas.de(pagina, tamano, Sort.unsorted()));
        Set<Long> votadas = votadasPor(yo, page.getContent());
        return PaginaResponse.desde(page, s -> SugerenciaResponse.desde(s, votadas.contains(s.getId())));
    }

    public PaginaResponse<SugerenciaResponse> votadasPorUsuario(Long usuarioId, int pagina, int tamano) {
        Page<Voto> page = votoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId,
                Paginas.de(pagina, tamano, Sort.unsorted()));
        return PaginaResponse.desde(page, v -> SugerenciaResponse.desde(v.getSugerencia(), true));
    }

    // ---------- Apoyo ----------

    private Sugerencia buscarEntidad(Long id) {
        return sugerenciaRepository.findById(id)
                .orElseThrow(() -> RecursoNoEncontradoException.de("Sugerencia", id));
    }

    private void registrarRevision(Sugerencia s, String nota, UsuarioPrincipal yo) {
        s.setRevisor(usuarioRepository.getReferenceById(yo.getId()));
        s.setFechaRevision(LocalDateTime.now());
        if (nota != null && !nota.isBlank()) {
            s.setNotaRevision(nota.trim());
        }
    }

    private Set<Long> votadasPor(UsuarioPrincipal yo, List<Sugerencia> sugerencias) {
        if (yo == null || sugerencias.isEmpty()) {
            return Set.of();
        }
        List<Long> ids = sugerencias.stream().map(Sugerencia::getId).toList();
        return new HashSet<>(votoRepository.sugerenciasVotadas(yo.getId(), ids));
    }
}
