package com.indiedex.repository;

import com.indiedex.model.Objeto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjetoRepository extends JpaRepository<Objeto, Long> {

    List<Objeto> findByJuegoSlugOrderByNombreAsc(String slug);

    long countByJuegoId(Long juegoId);

    /** Búsqueda global por nombre. */
    List<Objeto> findByNombreContainingIgnoreCase(String nombre, Pageable pagina);
}
