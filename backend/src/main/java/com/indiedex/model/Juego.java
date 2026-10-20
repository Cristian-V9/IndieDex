package com.indiedex.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "juegos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    /** Nombre en formato URL: "Dredge" -> "dredge", usado en /juegos/dredge. */
    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    /** Frase corta del banner del juego. */
    @Column(length = 255)
    private String eslogan;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Ruta de la imagen dentro del frontend, p. ej. "images/dredge.jpg". */
    @Column(name = "imagen_portada", length = 255)
    private String imagenPortada;

    @Column(name = "imagen_banner", length = 255)
    private String imagenBanner;

    @Column(length = 120)
    private String desarrollador;

    @Column(length = 120)
    private String editor;

    @Column(name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    /** Géneros separados por coma: "Aventura, pesca, misterio". */
    @Column(length = 150)
    private String generos;

    @Column(name = "modo_juego", length = 80)
    private String modoJuego;

    @Column(length = 150)
    private String plataformas;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_wiki", nullable = false, length = 20)
    @Builder.Default
    private EstadoWiki estadoWiki = EstadoWiki.EN_PREPARACION;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Al borrar un juego se borra todo su contenido de la wiki
    @OneToMany(mappedBy = "juego", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Articulo> articulos = new ArrayList<>();

    @OneToMany(mappedBy = "juego", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Logro> logros = new ArrayList<>();

    @OneToMany(mappedBy = "juego", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Objeto> objetos = new ArrayList<>();

    @OneToMany(mappedBy = "juego", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Personaje> personajes = new ArrayList<>();

    /** Pone la fecha actual si no se indicó otra (los datos de ejemplo usan fechas propias). */
    @PrePersist
    void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaCreacion == null) fechaCreacion = ahora;
    }
}
