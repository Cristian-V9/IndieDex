package com.indiedex.repository;

import com.indiedex.model.Logro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogroRepository extends JpaRepository<Logro, Long> {

    List<Logro> findByJuegoSlugOrderByNombreAsc(String slug);

    long countByJuegoId(Long juegoId);
}
