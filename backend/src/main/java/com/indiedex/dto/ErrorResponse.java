package com.indiedex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/** Formato único de error para toda la API. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int estado,
        String error,
        String mensaje,
        String ruta,
        LocalDateTime fecha,
        Map<String, String> campos
) {
    public ErrorResponse(int estado, String error, String mensaje, String ruta) {
        this(estado, error, mensaje, ruta, LocalDateTime.now(), null);
    }
}
