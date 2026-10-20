package com.indiedex.dto;

import com.indiedex.model.Rol;
import com.indiedex.model.Usuario;

/** Datos públicos mínimos de un usuario (autor de un artículo, comentario, etc.). */
public record UsuarioResumen(Long id, String nombreUsuario, Rol rol) {

    public static UsuarioResumen desde(Usuario u) {
        return u == null ? null : new UsuarioResumen(u.getId(), u.getNombreUsuario(), u.getRol());
    }
}
