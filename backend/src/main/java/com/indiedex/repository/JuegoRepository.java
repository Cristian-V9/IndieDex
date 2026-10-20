package com.indiedex.repository;

import com.indiedex.model.EstadoWiki;
import com.indiedex.model.Juego;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JuegoRepository extends JpaRepository<Juego, Long> {

    Optional<Juego> findBySlug(String slug);

    boolean existsBySlug(String slug);

    long countByEstadoWiki(EstadoWiki estadoWiki);

    /** Catálogo con filtros opcionales: texto, género y estado de la wiki. */
    @Query("""
            select j from Juego j
            where (:buscar is null
                   or lower(j.titulo) like lower(concat('%', :buscar, '%'))
                   or lower(j.desarrollador) like lower(concat('%', :buscar, '%')))
              and (:genero is null or lower(j.generos) like lower(concat('%', :genero, '%')))
              and (:estado is null or j.estadoWiki = :estado)
            """)
    List<Juego> buscar(@Param("buscar") String buscar,
                       @Param("genero") String genero,
                       @Param("estado") EstadoWiki estado,
                       Sort orden);
}
