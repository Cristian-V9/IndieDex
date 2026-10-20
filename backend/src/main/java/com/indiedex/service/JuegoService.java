package com.indiedex.service;

import com.indiedex.dto.JuegoDetalleResponse;
import com.indiedex.dto.JuegoRequest;
import com.indiedex.dto.JuegoResumenResponse;
import com.indiedex.exception.ConflictoException;
import com.indiedex.exception.RecursoNoEncontradoException;
import com.indiedex.model.EstadoArticulo;
import com.indiedex.model.EstadoWiki;
import com.indiedex.model.Juego;
import com.indiedex.repository.*;
import com.indiedex.util.Textos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JuegoService {

    private final JuegoRepository juegoRepository;
    private final ArticuloRepository articuloRepository;
    private final ComentarioRepository comentarioRepository;
    private final SugerenciaRepository sugerenciaRepository;
    private final LogroRepository logroRepository;
    private final ObjetoRepository objetoRepository;
    private final PersonajeRepository personajeRepository;

    /**
     * Catálogo con filtros.
     * @param orden "destacados" (wikis abiertas primero), "articulos" (más artículos) o "nombre" (A-Z)
     */
    public List<JuegoResumenResponse> listar(String buscar, String genero, EstadoWiki estado, String orden) {
        String criterio = orden == null ? "destacados" : orden.toLowerCase(Locale.ROOT);
        Sort sort = switch (criterio) {
            case "nombre" -> Sort.by("titulo");
            // EnumType.STRING ordena alfabéticamente: ABIERTA antes que EN_PREPARACION
            default -> Sort.by("estadoWiki").and(Sort.by("titulo"));
        };

        List<Juego> juegos = juegoRepository.buscar(Textos.limpiar(buscar), Textos.limpiar(genero), estado, sort);
        Map<Long, Long> articulosPorJuego = contarArticulosPorJuego();

        List<JuegoResumenResponse> resultado = juegos.stream()
                .map(j -> JuegoResumenResponse.desde(j, articulosPorJuego.getOrDefault(j.getId(), 0L)))
                .collect(Collectors.toCollection(ArrayList::new));

        if (criterio.equals("articulos")) {
            resultado.sort(Comparator.comparingLong(JuegoResumenResponse::totalArticulos).reversed()
                    .thenComparing(JuegoResumenResponse::titulo));
        }
        return resultado;
    }

    public JuegoDetalleResponse obtenerPorSlug(String slug) {
        return detalle(buscarPorSlug(slug));
    }

    @Transactional
    public JuegoDetalleResponse crear(JuegoRequest req) {
        String slug = req.slug() != null ? req.slug() : Textos.slug(req.titulo());
        if (juegoRepository.existsBySlug(slug)) {
            throw new ConflictoException("Ya existe un juego con el slug '" + slug + "'");
        }
        Juego juego = new Juego();
        copiarDatos(req, juego);
        juego.setSlug(slug);
        return detalle(juegoRepository.save(juego));
    }

    @Transactional
    public JuegoDetalleResponse actualizar(Long id, JuegoRequest req) {
        Juego juego = buscarEntidad(id);
        String slug = req.slug() != null ? req.slug() : juego.getSlug();
        if (!slug.equals(juego.getSlug()) && juegoRepository.existsBySlug(slug)) {
            throw new ConflictoException("Ya existe un juego con el slug '" + slug + "'");
        }
        copiarDatos(req, juego);
        juego.setSlug(slug);
        return detalle(juego);
    }

    /** Borra el juego y todo su contenido. Las sugerencias se conservan sin juego. */
    @Transactional
    public void eliminar(Long id) {
        Juego juego = buscarEntidad(id);
        sugerenciaRepository.desvincularJuego(id);
        juegoRepository.delete(juego);
    }

    // ---------- Métodos de apoyo usados por otros servicios ----------

    public Juego buscarEntidad(Long id) {
        return juegoRepository.findById(id)
                .orElseThrow(() -> RecursoNoEncontradoException.de("Juego", id));
    }

    public Juego buscarPorSlug(String slug) {
        return juegoRepository.findBySlug(slug)
                .orElseThrow(() -> RecursoNoEncontradoException.de("Juego", slug));
    }

    public Map<Long, Long> contarArticulosPorJuego() {
        Map<Long, Long> mapa = new HashMap<>();
        for (Object[] fila : articuloRepository.contarPublicadosPorJuego()) {
            mapa.put((Long) fila[0], (Long) fila[1]);
        }
        return mapa;
    }

    private JuegoDetalleResponse detalle(Juego j) {
        Long id = j.getId();
        // Colaboradores: quienes escribieron, comentaron o sugirieron algo sobre este juego
        Set<Long> colaboradores = new HashSet<>(articuloRepository.autoresDeJuego(id));
        colaboradores.addAll(comentarioRepository.autoresDeJuego(id));
        colaboradores.addAll(sugerenciaRepository.autoresDeJuego(id));

        return JuegoDetalleResponse.desde(j,
                articuloRepository.countByJuegoIdAndEstado(id, EstadoArticulo.PUBLICADO),
                colaboradores.size(),
                logroRepository.countByJuegoId(id),
                objetoRepository.countByJuegoId(id),
                personajeRepository.countByJuegoId(id));
    }

    private void copiarDatos(JuegoRequest req, Juego juego) {
        juego.setTitulo(req.titulo().trim());
        juego.setEslogan(req.eslogan());
        juego.setDescripcion(req.descripcion());
        juego.setImagenPortada(req.imagenPortada());
        juego.setImagenBanner(req.imagenBanner());
        juego.setDesarrollador(req.desarrollador());
        juego.setEditor(req.editor());
        juego.setFechaLanzamiento(req.fechaLanzamiento());
        juego.setGeneros(req.generos());
        juego.setModoJuego(req.modoJuego());
        juego.setPlataformas(req.plataformas());
        juego.setEstadoWiki(req.estadoWiki());
    }
}
