package com.indiedex.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Artículo o guía de la wiki de un juego. */
@Entity
@Table(name = "articulos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    /** Subtítulo o resumen que aparece en las tarjetas. */
    @Column(length = 300)
    private String resumen;

    /** Cuerpo del artículo en HTML. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    /** Categoría de la guía: Principiantes, Progresión, Historia, Pesca... */
    @Column(length = 50)
    private String categoria;

    @Column(length = 255)
    private String imagen;

    /** Texto alternativo / pie de la imagen principal. */
    @Column(name = "imagen_descripcion", length = 255)
    private String imagenDescripcion;

    @Column(name = "minutos_lectura")
    private Integer minutosLectura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private EstadoArticulo estado = EstadoArticulo.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "juego_id", nullable = false)
    private Juego juego;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Comentario> comentarios = new ArrayList<>();

    /** Pone la fecha actual si no se indicó otra (los datos de ejemplo usan fechas propias). */
    @PrePersist
    void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaCreacion == null) fechaCreacion = ahora;
        if (fechaActualizacion == null) fechaActualizacion = ahora;
    }

    @PreUpdate
    void alActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }
}
