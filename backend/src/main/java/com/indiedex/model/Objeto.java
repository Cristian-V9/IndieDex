package com.indiedex.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "objetos")
@Data
public class Objeto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private String rareza;
    
    @ManyToOne
    @JoinColumn(name = "juego_id")
    private Juego juego;
}