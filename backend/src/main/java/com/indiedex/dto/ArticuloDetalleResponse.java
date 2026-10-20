package com.indiedex.dto;

import com.indiedex.model.Articulo;
import com.indiedex.model.EstadoArticulo;

import java.time.LocalDateTime;

/** Artículo completo para su página. */
public record ArticuloDetalleResponse(
        Long id,
        String titulo,
        String resumen,
        String contenido,
        String categoria,
        String imagen,
        String imagenDescripcion,
        Integer minutosLectura,
        EstadoArticulo estado,
        Long juegoId,
        String juegoTitulo,
        String juegoSlug,
        UsuarioResumen autor,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        long totalComentarios
) {
    public static ArticuloDetalleResponse desde(Articulo a, long totalComentarios) {
        return new ArticuloDetalleResponse(a.getId(), a.getTitulo(), a.getResumen(), a.getContenido(),
                a.getCategoria(), a.getImagen(), a.getImagenDescripcion(), a.getMinutosLectura(),
                a.getEstado(), a.getJuego().getId(), a.getJuego().getTitulo(), a.getJuego().getSlug(),
                UsuarioResumen.desde(a.getAutor()), a.getFechaCreacion(), a.getFechaActualizacion(),
                totalComentarios);
    }
}
