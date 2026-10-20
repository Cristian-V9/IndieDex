package com.indiedex.controller;

import com.indiedex.model.Logro;
import com.indiedex.repository.LogroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logros")
public class LogroController {

    @Autowired
    private LogroRepository logroRepository;

    @GetMapping
    public List<Logro> listar() {
        return logroRepository.findAll();
    }

    @PostMapping
    public Logro crear(@RequestBody Logro logro) {
        return logroRepository.save(logro);
    }
}