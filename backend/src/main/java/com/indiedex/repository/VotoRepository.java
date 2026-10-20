package com.indiedex.repository;

import com.indiedex.model.Voto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByUsuarioIdAndSugerenciaId(Long usuarioId, Long sugerenciaId);

    Optional<Voto> findByUsuarioIdAndSugerenciaId(Long usuarioId, Long sugerenciaId);

    long countByUsuarioId(Long usuarioId);

    Page<Voto> findByUsuarioIdOrderByFechaDesc(Long usuarioId, Pageable pagina);

    /** De una lista de sugerencias, cuáles votó el usuario (para marcar "Has votado"). */
    @Query("select v.sugerencia.id from Voto v where v.usuario.id = :usuarioId and v.sugerencia.id in :ids")
    List<Long> sugerenciasVotadas(@Param("usuarioId") Long usuarioId, @Param("ids") Collection<Long> ids);
}
