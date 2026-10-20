package com.indiedex.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "logros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Logro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Pista para conseguirlo (puede contener destripes). */
    @Column(name = "como_conseguirlo", columnDefinition = "TEXT")
    private String comoConseguirlo;

    @Column(length = 255)
    private String icono;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "juego_id", nullable = false)
    private Juego juego;
}
