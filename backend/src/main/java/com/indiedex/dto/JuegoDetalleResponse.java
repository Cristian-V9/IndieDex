package com.indiedex.dto;

import com.indiedex.model.EstadoWiki;
import com.indiedex.model.Juego;

import java.time.LocalDate;

/** Página de un juego: banner, ficha técnica y cifras de su wiki. */
public record JuegoDetalleResponse(
        Long id,
        String titulo,
        String slug,
        String eslogan,
        String descripcion,
        String imagenPortada,
        String imagenBanner,
        String desarrollador,
        String editor,
        LocalDate fechaLanzamiento,
        String generos,
        String modoJuego,
        String plataformas,
        EstadoWiki estadoWiki,
        long totalArticulos,
        long totalColaboradores,
        long totalLogros,
        long totalObjetos,
        long totalPersonajes
) {
    public static JuegoDetalleResponse desde(Juego j, long articulos, long colaboradores,
                                             long logros, long objetos, long personajes) {
        return new JuegoDetalleResponse(j.getId(), j.getTitulo(), j.getSlug(), j.getEslogan(),
                j.getDescripcion(), j.getImagenPortada(), j.getImagenBanner(), j.getDesarrollador(),
                j.getEditor(), j.getFechaLanzamiento(), j.getGeneros(), j.getModoJuego(),
                j.getPlataformas(), j.getEstadoWiki(), articulos, colaboradores, logros, objetos, personajes);
    }
}
