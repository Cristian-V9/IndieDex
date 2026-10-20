package com.indiedex.repository;

import com.indiedex.model.Articulo;
import com.indiedex.model.EstadoArticulo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

    /** Artículos de un juego en un estado, con búsqueda opcional por texto. */
    @Query("""
            select a from Articulo a
            where a.juego.slug = :slug
              and a.estado in :estados
              and (:buscar is null
                   or lower(a.titulo) like lower(concat('%', :buscar, '%'))
                   or lower(a.resumen) like lower(concat('%', :buscar, '%'))
                   or lower(a.categoria) like lower(concat('%', :buscar, '%')))
            """)
    Page<Articulo> buscarPorJuego(@Param("slug") String slug,
                                  @Param("estados") List<EstadoArticulo> estados,
                                  @Param("buscar") String buscar,
                                  Pageable pagina);

    Page<Articulo> findByEstadoIn(List<EstadoArticulo> estados, Pageable pagina);

    @Query("""
            select a from Articulo a
            where a.estado = com.indiedex.model.EstadoArticulo.PUBLICADO
              and (lower(a.titulo) like lower(concat('%', :texto, '%'))
                   or lower(a.resumen) like lower(concat('%', :texto, '%')))
            """)
    List<Articulo> buscarPublicados(@Param("texto") String texto, Pageable pagina);

    long countByEstado(EstadoArticulo estado);

    long countByJuegoIdAndEstado(Long juegoId, EstadoArticulo estado);

    /** Número de artículos publicados por juego: filas [juegoId, total]. */
    @Query("""
            select a.juego.id, count(a) from Articulo a
            where a.estado = com.indiedex.model.EstadoArticulo.PUBLICADO
            group by a.juego.id
            """)
    List<Object[]> contarPublicadosPorJuego();

    /** Autores de artículos publicados de un juego. */
    @Query("""
            select distinct a.autor.id from Articulo a
            where a.juego.id = :juegoId and a.estado = com.indiedex.model.EstadoArticulo.PUBLICADO
            """)
    List<Long> autoresDeJuego(@Param("juegoId") Long juegoId);

    @Query("select distinct a.autor.id from Articulo a where a.estado = com.indiedex.model.EstadoArticulo.PUBLICADO")
    List<Long> autoresPublicados();
}
