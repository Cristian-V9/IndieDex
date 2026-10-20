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
    public List<Articulo> listar() {
        return articuloRepository.findAll();
    }

    @PostMapping
    public Articulo crear(@RequestBody Articulo articulo) {
        if (articulo.getEstado() == null) {
            articulo.setEstado("SUGERIDO"); // Por defecto entra al flujo de sugerencias de la comunidad
        }
        return articuloRepository.save(articulo);
    }
}