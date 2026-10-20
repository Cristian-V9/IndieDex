package com.indiedex.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Propuesta de la comunidad: nueva wiki, nueva guía, mejora o corrección. */
@Entity
@Table(name = "sugerencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sugerencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoSugerencia tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private EstadoSugerencia estado = EstadoSugerencia.SUGERENCIA;

    /** Juego al que se refiere (opcional). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juego_id")
    private Juego juego;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    /** Contador de votos (se mantiene sincronizado con la tabla votos). */
    @Column(name = "total_votos", nullable = false)
    @Builder.Default
    private int totalVotos = 0;

    /** Nota que deja el equipo al revisar. */
    @Column(name = "nota_revision", length = 500)
    private String notaRevision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisor_id")
    private Usuario revisor;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "sugerencia", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Voto> votos = new ArrayList<>();

    /** Pone la fecha actual si no se indicó otra (los datos de ejemplo usan fechas propias). */
    @PrePersist
    void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaCreacion == null) fechaCreacion = ahora;
    }
}
