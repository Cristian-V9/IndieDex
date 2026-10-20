package com.indiedex.dto;

import java.time.Instant;

/** Respuesta de login/registro: el token JWT y los datos del usuario. */
public record AuthResponse(
        String token,
        String tipo,
        Instant expiraEn,
        UsuarioResponse usuario
) {
    public AuthResponse(String token, Instant expiraEn, UsuarioResponse usuario) {
        this(token, "Bearer", expiraEn, usuario);
    }
}
