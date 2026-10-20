package com.indiedex.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personajes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Personaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String historia;

    /** Papel en el juego: Comerciante, Protagonista, Misterioso... */
    @Column(length = 60)
    private String rol;

    /** Dónde se le encuentra. */
    @Column(length = 150)
    private String ubicacion;

    @Column(length = 255)
    private String imagen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "juego_id", nullable = false)
    private Juego juego;
}
