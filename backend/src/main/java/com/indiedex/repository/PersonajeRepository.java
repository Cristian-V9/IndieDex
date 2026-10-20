package com.indiedex.repository;

import com.indiedex.model.Personaje;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonajeRepository extends JpaRepository<Personaje, Long> {

    List<Personaje> findByJuegoSlugOrderByNombreAsc(String slug);

    long countByJuegoId(Long juegoId);

    /** Búsqueda global por nombre. */
    List<Personaje> findByNombreContainingIgnoreCase(String nombre, Pageable pagina);
}
