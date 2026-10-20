package com.indiedex.service;

import com.indiedex.dto.ArticuloDetalleResponse;
import com.indiedex.dto.ArticuloRequest;
import com.indiedex.dto.ArticuloResumenResponse;
import com.indiedex.dto.PaginaResponse;
import com.indiedex.exception.PermisoDenegadoException;
import com.indiedex.exception.RecursoNoEncontradoException;
import com.indiedex.model.*;
import com.indiedex.repository.ArticuloRepository;
import com.indiedex.repository.ComentarioRepository;
import com.indiedex.repository.UsuarioRepository;
import com.indiedex.security.UsuarioPrincipal;
import com.indiedex.util.Paginas;
import com.indiedex.util.Textos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticuloService {

    private static final Sort MAS_RECIENTES = Sort.by(Sort.Direction.DESC, "fechaActualizacion");

    private final ArticuloRepository articuloRepository;
    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final JuegoService juegoService;

    /** Guías publicadas de un juego (pestaña "Guías"), con búsqueda y paginación. */
    public PaginaResponse<ArticuloResumenResponse> listarPorJuego(String slug, String buscar, int pagina, int tamano) {
        juegoService.buscarPorSlug(slug); // 404 si el juego no existe
        var page = articuloRepository.buscarPorJuego(slug, List.of(EstadoArticulo.PUBLICADO),
                Textos.limpiar(buscar), Paginas.de(pagina, tamano, MAS_RECIENTES));
        return PaginaResponse.desde(page, ArticuloResumenResponse::desde);
    }

    /**
     * Lista general. Por defecto solo publicados; ver borradores o en revisión
     * es exclusivo del equipo.
     */
    public PaginaResponse<ArticuloResumenResponse> listar(List<EstadoArticulo> estados, int pagina, int tamano,
                                                          UsuarioPrincipal yo) {
        List<EstadoArticulo> filtro = (estados == null || estados.isEmpty())
                ? List.of(EstadoArticulo.PUBLICADO) : estados;
        boolean pideNoPublicados = filtro.stream().anyMatch(e -> e != EstadoArticulo.PUBLICADO);
        if (pideNoPublicados && (yo == null || !yo.esEquipo())) {
            throw new PermisoDenegadoException("Solo el equipo puede ver borradores y artículos en revisión");
        }
        var page = articuloRepository.findByEstadoIn(filtro, Paginas.de(pagina, tamano, MAS_RECIENTES));
        return PaginaResponse.desde(page, ArticuloResumenResponse::desde);
    }

    /** "Recién llegados a la wiki" del inicio. */
    public List<ArticuloResumenResponse> recientes(int limite) {
        var page = articuloRepository.findByEstadoIn(List.of(EstadoArticulo.PUBLICADO),
                PageRequest.of(0, Math.min(Math.max(limite, 1), 20), MAS_RECIENTES));
        return page.map(ArticuloResumenResponse::desde).getContent();
    }

    public ArticuloDetalleResponse obtener(Long id, UsuarioPrincipal yo) {
        Articulo articulo = buscarVisible(id, yo);
        return ArticuloDetalleResponse.desde(articulo, comentarioRepository.countByArticuloId(id));
    }

    @Transactional
    public ArticuloDetalleResponse crear(ArticuloRequest req, UsuarioPrincipal yo) {
        Articulo articulo = new Articulo();
        articulo.setAutor(usuarioRepository.getReferenceById(yo.getId()));
        copiarDatos(req, articulo);
        articulo.setEstado(req.estado() != null ? req.estado() : EstadoArticulo.BORRADOR);
        abrirWikiSiSePublica(articulo);
        Articulo guardado = articuloRepository.save(articulo);
        return ArticuloDetalleResponse.desde(guardado, 0);
    }

    @Transactional
    public ArticuloDetalleResponse actualizar(Long id, ArticuloRequest req) {
        Articulo articulo = buscarEntidad(id);
        copiarDatos(req, articulo);
        if (req.estado() != null) {
            articulo.setEstado(req.estado());
        }
        abrirWikiSiSePublica(articulo);
        return ArticuloDetalleResponse.desde(articulo, comentarioRepository.countByArticuloId(id));
    }

    @Transactional
    public ArticuloDetalleResponse cambiarEstado(Long id, EstadoArticulo estado) {
        Articulo articulo = buscarEntidad(id);
        articulo.setEstado(estado);
        abrirWikiSiSePublica(articulo);
        return ArticuloDetalleResponse.desde(articulo, comentarioRepository.countByArticuloId(id));
    }

    @Transactional
    public void eliminar(Long id) {
        articuloRepository.delete(buscarEntidad(id));
    }

    // ---------- Métodos de apoyo ----------

    public Articulo buscarEntidad(Long id) {
        return articuloRepository.findById(id)
                .orElseThrow(() -> RecursoNoEncontradoException.de("Artículo", id));
    }

    /** Un artículo no publicado solo lo ve el equipo; para los demás "no existe". */
    public Articulo buscarVisible(Long id, UsuarioPrincipal yo) {
        Articulo articulo = buscarEntidad(id);
        boolean publicado = articulo.getEstado() == EstadoArticulo.PUBLICADO;
        if (!publicado && (yo == null || !yo.esEquipo())) {
            throw RecursoNoEncontradoException.de("Artículo", id);
        }
        return articulo;
    }

    /** Al publicar el primer artículo de un juego, su wiki pasa a "Wiki abierta". */
    private void abrirWikiSiSePublica(Articulo articulo) {
        Juego juego = articulo.getJuego();
        if (articulo.getEstado() == EstadoArticulo.PUBLICADO && juego.getEstadoWiki() == EstadoWiki.EN_PREPARACION) {
            juego.setEstadoWiki(EstadoWiki.ABIERTA);
        }
    }

    private void copiarDatos(ArticuloRequest req, Articulo articulo) {
        articulo.setJuego(juegoService.buscarEntidad(req.juegoId()));
        articulo.setTitulo(req.titulo().trim());
        articulo.setResumen(req.resumen());
        articulo.setContenido(req.contenido());
        articulo.setCategoria(req.categoria());
        articulo.setImagen(req.imagen());
        articulo.setImagenDescripcion(req.imagenDescripcion());
        articulo.setMinutosLectura(req.minutosLectura());
    }
}
