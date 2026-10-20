package com.indiedex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Al rechazar una sugerencia la nota para su autor es obligatoria. */
public record RechazoRequest(
        @NotBlank(message = "Explica al autor por qué se rechaza")
        @Size(max = 500, message = "La nota admite máximo 500 caracteres")
        String nota
) {
}
