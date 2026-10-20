package com.indiedex.controller;

import com.indiedex.model.Usuario;
import com.indiedex.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/registro")
    public Usuario registrar(@RequestBody Usuario usuario) {
        if (usuario.getRol() == null) {
            usuario.setRol("USER"); // Rol por defecto
        }
        return usuarioRepository.save(usuario);
    }
}