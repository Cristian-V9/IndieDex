package com.indiedex.dto;

import jakarta.validation.constraints.NotBlank;

/** Inicio de sesión con correo o nombre de usuario. */
public record LoginRequest(
        @NotBlank(message = "Escribe tu correo o nombre de usuario")
        String usuario,

        @NotBlank(message = "Escribe tu contraseña")
        String password
) {
}
