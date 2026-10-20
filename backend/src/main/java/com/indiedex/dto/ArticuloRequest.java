package com.indiedex.dto;

import com.indiedex.model.EstadoArticulo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Crear o editar un artículo. Si no se indica estado, queda como BORRADOR. */
public record ArticuloRequest(
        @NotNull(message = "Indica el juego (juegoId)")
        Long juegoId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150)
        String titulo,

        @Size(max = 300) String resumen,

        @NotBlank(message = "El contenido es obligatorio")
        String contenido,

        @Size(max = 50) String categoria,
        @Size(max = 255) String imagen,
        @Size(max = 255) String imagenDescripcion,

        @Min(value = 1, message = "El tiempo de lectura mínimo es 1 minuto")
        @Max(value = 180, message = "El tiempo de lectura máximo es 180 minutos")
        Integer minutosLectura,

        EstadoArticulo estado
) {
}
