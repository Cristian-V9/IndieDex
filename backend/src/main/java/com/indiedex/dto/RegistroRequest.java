package com.indiedex.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos para crear una cuenta. El rol NO se recibe: toda cuenta nueva es USER. */
public record RegistroRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 24, message = "El nombre de usuario debe tener entre 3 y 24 caracteres")
        @Pattern(regexp = "^[A-Za-z0-9_.-]+$",
                message = "El nombre de usuario solo puede tener letras, números, punto, guion y guion bajo")
        String nombreUsuario,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @Size(max = 120)
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password
) {
}
