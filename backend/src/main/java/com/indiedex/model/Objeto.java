package com.indiedex.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "objetos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Objeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Tipo de objeto: Caña, Motor, Luz, Reliquia... */
    @Column(length = 50)
    private String tipo;

    @Column(length = 30)
    private String rareza;

    /** Dónde se consigue. */
    @Column(length = 150)
    private String ubicacion;

    @Column(length = 255)
    private String imagen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "juego_id", nullable = false)
    private Juego juego;
}
