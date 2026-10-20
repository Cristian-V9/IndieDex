package com.indiedex.dto;

import com.indiedex.model.Articulo;
import com.indiedex.model.EstadoArticulo;

import java.time.LocalDateTime;

/** Tarjeta de artículo para listados. */
public record ArticuloResumenResponse(
        Long id,
        String titulo,
        String resumen,
        String categoria,
        String imagen,
        Integer minutosLectura,
        EstadoArticulo estado,
        String juegoTitulo,
        String juegoSlug,
        UsuarioResumen autor,
        LocalDateTime fechaActualizacion
) {
    public static ArticuloResumenResponse desde(Articulo a) {
        return new ArticuloResumenResponse(a.getId(), a.getTitulo(), a.getResumen(), a.getCategoria(),
                a.getImagen(), a.getMinutosLectura(), a.getEstado(), a.getJuego().getTitulo(),
                a.getJuego().getSlug(), UsuarioResumen.desde(a.getAutor()), a.getFechaActualizacion());
    }
}
