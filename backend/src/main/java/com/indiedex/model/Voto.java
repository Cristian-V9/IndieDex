package com.indiedex.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Voto de un usuario a una sugerencia. Un usuario solo puede votar una vez cada sugerencia. */
@Entity
@Table(name = "votos",
        uniqueConstraints = @UniqueConstraint(name = "uk_voto_usuario_sugerencia",
                columnNames = {"usuario_id", "sugerencia_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sugerencia_id", nullable = false)
    private Sugerencia sugerencia;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    /** Pone la fecha actual si no se indicó otra (los datos de ejemplo usan fechas propias). */
    @PrePersist
    void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fecha == null) fecha = ahora;
    }
}
