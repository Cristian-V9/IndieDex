package com.indiedex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Nuevo comentario. padreId es opcional: si se envía, es una respuesta. */
public record ComentarioRequest(
        @NotBlank(message = "El comentario no puede estar vacío")
        @Size(max = 2000, message = "El comentario admite máximo 2000 caracteres")
        String contenido,

        Long padreId
) {
}
