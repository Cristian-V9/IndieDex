package com.indiedex.dto;

import jakarta.validation.constraints.Size;

/** Nota opcional al aprobar (avanzar) una sugerencia. */
public record RevisionRequest(
        @Size(max = 500, message = "La nota admite máximo 500 caracteres")
        String nota
) {
}
