package com.indiedex.repository;
import com.indiedex.model.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
}package com.indiedex.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "articulos")
@Data
public class Articulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String contenido;
    private String estado; // "SUGERIDO", "EN_REVISION", "PUBLICADO"
    
    @ManyToOne
    @JoinColumn(name = "juego_id")
    private Juego juego;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario autor;
}