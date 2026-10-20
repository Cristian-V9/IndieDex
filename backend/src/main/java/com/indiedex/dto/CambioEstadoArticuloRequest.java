package com.indiedex.dto;

import com.indiedex.model.EstadoArticulo;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoArticuloRequest(
        @NotNull(message = "Indica el estado: BORRADOR, EN_REVISION o PUBLICADO")
        EstadoArticulo estado
) {
}
