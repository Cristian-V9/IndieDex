package com.indiedex.repository;

import com.indiedex.model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    /** Comentarios principales de un artículo (las respuestas cuelgan de cada uno). */
    List<Comentario> findByArticuloIdAndPadreIsNullOrderByFechaCreacionAsc(Long articuloId);

    long countByArticuloId(Long articuloId);

    Page<Comentario> findByAutorIdOrderByFechaCreacionDesc(Long autorId, Pageable pagina);

    long countByAutorId(Long autorId);

    /** Usuarios que comentaron artículos de un juego. */
    @Query("select distinct c.autor.id from Comentario c where c.articulo.juego.id = :juegoId")
    List<Long> autoresDeJuego(@Param("juegoId") Long juegoId);

    @Query("select distinct c.autor.id from Comentario c")
    List<Long> autores();
}
