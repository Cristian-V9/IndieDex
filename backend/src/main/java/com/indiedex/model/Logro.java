package com.indiedex.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "logros")
@Data
public class Logro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private String icono;
    
    @ManyToOne
    @JoinColumn(name = "juego_id")
    private Juego juego;
}