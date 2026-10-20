package com.indiedex.dto;

import com.indiedex.model.EstadoWiki;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Crear o editar un juego. Si no se envía slug, se genera desde el título. */
public record JuegoRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 120)
        String titulo,

        @Size(max = 120)
        @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
                message = "El slug solo puede tener minúsculas, números y guiones (ej: hollow-knight)")
        String slug,

        @Size(max = 255) String eslogan,
        String descripcion,
        @Size(max = 255) String imagenPortada,
        @Size(max = 255) String imagenBanner,
        @Size(max = 120) String desarrollador,
        @Size(max = 120) String editor,
        LocalDate fechaLanzamiento,
        @Size(max = 150) String generos,
        @Size(max = 80) String modoJuego,
        @Size(max = 150) String plataformas,

        @NotNull(message = "El estado de la wiki es obligatorio (ABIERTA o EN_PREPARACION)")
        EstadoWiki estadoWiki
) {
}
