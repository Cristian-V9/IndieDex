package com.indiedex.dto;

import com.indiedex.model.Rol;
import jakarta.validation.constraints.NotNull;

public record CambioRolRequest(
        @NotNull(message = "Indica el rol: USER, EDITOR o ADMIN")
        Rol rol
) {
}
