package com.indiedex.dto;

import com.indiedex.model.TipoSugerencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SugerenciaRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 5, max = 150, message = "El título debe tener entre 5 y 150 caracteres")
        String titulo,

        @NotBlank(message = "Cuéntanos tu idea")
        @Size(max = 2000, message = "La descripción admite máximo 2000 caracteres")
        String descripcion,

        @NotNull(message = "Indica el tipo: NUEVA_WIKI, NUEVA_GUIA, MEJORA o CORRECCION")
        TipoSugerencia tipo,

        Long juegoId
) {
}
