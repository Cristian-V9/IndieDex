package com.indiedex.controller;

import com.indiedex.model.Objeto;
import com.indiedex.model.Personaje;
import com.indiedex.repository.ObjetoRepository;
import com.indiedex.repository.PersonajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ObjetoPersonajeController {

    @Autowired
    private ObjetoRepository objetoRepository;

    @Autowired
    private PersonajeRepository personajeRepository;

    // --- OBJETOS ---
    @GetMapping("/objetos")
    public List<Objeto> listarObjetos() {
        return objetoRepository.findAll();
    }

    @PostMapping("/objetos")
    public Objeto crearObjeto(@RequestBody Objeto objeto) {
        return objetoRepository.save(objeto);
    }

    // --- PERSONAJES ---
    @GetMapping("/personajes")
    public List<Personaje> listarPersonajes() {
        return personajeRepository.findAll();
    }

    @PostMapping("/personajes")
    public Personaje crearPersonaje(@RequestBody Personaje personaje) {
        return personajeRepository.save(personaje);
    }
}