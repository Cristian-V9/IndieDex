package com.indiedex.controller;

import com.indiedex.model.Articulo;
import com.indiedex.repository.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {

    @Autowired
    private ArticuloRepository articuloRepository;

    @GetMapping
    public List<Articulo> listarArticulos() {
        return articuloRepository.findAll();
    }

    @PostMapping
    public Articulo crearArticulo(@RequestBody Articulo articulo) {
        articulo.setEstado("SUGERIDO"); // Estado inicial por defecto según el flujo colaborativo
        return articuloRepository.save(articulo);
    }
}