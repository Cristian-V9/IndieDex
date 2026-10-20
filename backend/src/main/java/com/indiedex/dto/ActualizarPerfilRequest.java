package com.indiedex.dto;

import jakarta.validation.constraints.Size;

public record ActualizarPerfilRequest(
        @Size(max = 280, message = "La biografía admite máximo 280 caracteres")
        String biografia
) {
}
