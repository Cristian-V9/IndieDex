package com.indiedex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PersonajeRequest(
        @NotNull(message = "Indica el juego (juegoId)") Long juegoId,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 120) String nombre,
        String historia,
        @Size(max = 60) String rol,
        @Size(max = 150) String ubicacion,
        @Size(max = 255) String imagen
) {
}
