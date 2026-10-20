package com.indiedex.dto;

import com.indiedex.model.Rol;
import com.indiedex.model.Usuario;

import java.time.LocalDateTime;

/** Datos de la cuenta (solo para el propio usuario o un administrador). Sin contraseña. */
public record UsuarioResponse(
        Long id,
        String nombreUsuario,
        String email,
        Rol rol,
        String biografia,
        LocalDateTime fechaRegistro
) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombreUsuario(), u.getEmail(), u.getRol(),
                u.getBiografia(), u.getFechaRegistro());
    }
}
