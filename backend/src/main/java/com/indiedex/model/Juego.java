package com.indiedex.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "juegos")
@Data
public class Juego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private String imagenPortada;
    private String desarrollador;
}