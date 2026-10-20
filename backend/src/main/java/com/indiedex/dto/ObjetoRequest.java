package com.indiedex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ObjetoRequest(
        @NotNull(message = "Indica el juego (juegoId)") Long juegoId,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 120) String nombre,
        String descripcion,
        @Size(max = 50) String tipo,
        @Size(max = 30) String rareza,
        @Size(max = 150) String ubicacion,
        @Size(max = 255) String imagen
) {
}
