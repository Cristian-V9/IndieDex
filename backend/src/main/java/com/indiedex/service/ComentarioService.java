package com.indiedex.service;

import com.indiedex.dto.ComentarioPerfilResponse;
import com.indiedex.dto.ComentarioRequest;
import com.indiedex.dto.ComentarioResponse;
import com.indiedex.dto.PaginaResponse;
import com.indiedex.exception.PermisoDenegadoException;
import com.indiedex.exception.RecursoNoEncontradoException;
import com.indiedex.exception.ReglaNegocioException;
import com.indiedex.model.Articulo;
import com.indiedex.model.Comentario;
import com.indiedex.model.EstadoArticulo;
import com.indiedex.repository.ComentarioRepository;
import com.indiedex.repository.UsuarioRepository;
import com.indiedex.security.UsuarioPrincipal;
import com.indiedex.util.Paginas;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArticuloService articuloService;

    /** Comentarios de un artículo, cada uno con sus respuestas. */
    public List<ComentarioResponse> listar(Long articuloId, UsuarioPrincipal yo) {
        articuloService.buscarVisible(articuloId, yo);
        return comentarioRepository.findByArticuloIdAndPadreIsNullOrderByFechaCreacionAsc(articuloId)
                .stream().map(ComentarioResponse::desde).toList();
    }

    @Transactional
    public ComentarioResponse crear(Long articuloId, ComentarioRequest req, UsuarioPrincipal yo) {
        Articulo articulo = articuloService.buscarVisible(articuloId, yo);
        if (articulo.getEstado() != EstadoArticulo.PUBLICADO) {
            throw new ReglaNegocioException("Solo se puede comentar en artículos publicados");
        }

        Comentario comentario = new Comentario();
        comentario.setArticulo(articulo);
        comentario.setAutor(usuarioRepository.getReferenceById(yo.getId()));
        comentario.setContenido(req.contenido().trim());

        if (req.padreId() != null) {
            Comentario padre = buscarEntidad(req.padreId());
            if (!padre.getArticulo().getId().equals(articuloId)) {
                throw new ReglaNegocioException("El comentario al que respondes es de otro artículo");
            }
            // Solo dos niveles: responder a una respuesta cuelga del comentario principal
            comentario.setPadre(padre.getPadre() != null ? padre.getPadre() : padre);
        }
        return ComentarioResponse.desde(comentarioRepository.save(comentario));
    }

    /** Lo puede borrar su autor o el equipo (moderación). */
    @Transactional
    public void eliminar(Long id, UsuarioPrincipal yo) {
        Comentario comentario = buscarEntidad(id);
        boolean esAutor = comentario.getAutor().getId().equals(yo.getId());
        if (!esAutor && !yo.esEquipo()) {
            throw new PermisoDenegadoException("Solo puedes borrar tus propios comentarios");
        }
        if (comentario.getPadre() != null) {
            comentario.getPadre().getRespuestas().remove(comentario);
        }
        comentarioRepository.delete(comentario);
    }

    public PaginaResponse<ComentarioPerfilResponse> deUsuario(Long usuarioId, int pagina, int tamano) {
        var page = comentarioRepository.findByAutorIdOrderByFechaCreacionDesc(usuarioId,
                Paginas.de(pagina, tamano, Sort.unsorted()));
        return PaginaResponse.desde(page, ComentarioPerfilResponse::desde);
    }

    private Comentario buscarEntidad(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> RecursoNoEncontradoException.de("Comentario", id));
    }
}
