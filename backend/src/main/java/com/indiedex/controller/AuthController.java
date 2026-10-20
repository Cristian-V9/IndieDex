package com.indiedex.controller;

import com.indiedex.dto.AuthResponse;
import com.indiedex.dto.LoginRequest;
import com.indiedex.dto.RegistroRequest;
import com.indiedex.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Autenticación", description = "Registro e inicio de sesión (devuelven el token JWT)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Crear una cuenta (rol USER) e iniciar sesión")
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrar(@Valid @RequestBody RegistroRequest req) {
        return authService.registrar(req);
    }

    @Operation(summary = "Iniciar sesión con correo o nombre de usuario")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
