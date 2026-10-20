package com.indiedex.dto;

import com.indiedex.model.EstadoWiki;
import com.indiedex.model.Juego;

/** Tarjeta de juego para el catálogo y el inicio. */
public record JuegoResumenResponse(
        Long id,
        String titulo,
        String slug,
        String desarrollador,
        String imagenPortada,
        String generos,
        EstadoWiki estadoWiki,
        long totalArticulos
) {
    public static JuegoResumenResponse desde(Juego j, long totalArticulos) {
        return new JuegoResumenResponse(j.getId(), j.getTitulo(), j.getSlug(), j.getDesarrollador(),
                j.getImagenPortada(), j.getGeneros(), j.getEstadoWiki(), totalArticulos);
    }
}
