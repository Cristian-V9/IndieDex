package com.indiedex.dto;

/** Perfil con contadores de actividad. */
public record PerfilResponse(
        UsuarioResponse usuario,
        long totalSugerencias,
        long sugerenciasPublicadas,
        long totalVotos,
        long totalComentarios
) {
}
