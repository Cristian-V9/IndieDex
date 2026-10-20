package com.indiedex.repository;

import com.indiedex.model.EstadoSugerencia;
import com.indiedex.model.Sugerencia;
import com.indiedex.model.TipoSugerencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SugerenciaRepository extends JpaRepository<Sugerencia, Long> {

    /** Lista de sugerencias filtrada por estados, tipo y juego (opcionales). */
    @Query("""
            select s from Sugerencia s left join s.juego j
            where s.estado in :estados
              and (:tipo is null or s.tipo = :tipo)
              and (:juegoId is null or j.id = :juegoId)
            """)
    Page<Sugerencia> buscar(@Param("estados") List<EstadoSugerencia> estados,
                            @Param("tipo") TipoSugerencia tipo,
                            @Param("juegoId") Long juegoId,
                            Pageable pagina);

    long countByEstado(EstadoSugerencia estado);

    long countByEstadoIn(List<EstadoSugerencia> estados);

    long countByEstadoAndFechaRevisionGreaterThanEqual(EstadoSugerencia estado, LocalDateTime desde);

    Page<Sugerencia> findByAutorIdOrderByFechaCreacionDesc(Long autorId, Pageable pagina);

    long countByAutorId(Long autorId);

    long countByAutorIdAndEstado(Long autorId, EstadoSugerencia estado);

    @Query("select distinct s.autor.id from Sugerencia s where s.juego.id = :juegoId")
    List<Long> autoresDeJuego(@Param("juegoId") Long juegoId);

    @Query("select distinct s.autor.id from Sugerencia s")
    List<Long> autores();

    /** Antes de borrar un juego, sus sugerencias quedan sin juego asociado. */
    @Modifying
    @Query("update Sugerencia s set s.juego = null where s.juego.id = :juegoId")
    int desvincularJuego(@Param("juegoId") Long juegoId);
}
