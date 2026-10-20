    package com.indiedex.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "personajes")
@Data
public class Personaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(columnDefinition = "TEXT")
    private String historia;
    private String rol;
    
    @ManyToOne
    @JoinColumn(name = "juego_id")
    private Juego juego;
}