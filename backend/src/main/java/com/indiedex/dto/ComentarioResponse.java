package com.indiedex.dto;

import com.indiedex.model.Comentario;

import java.time.LocalDateTime;
import java.util.List;

/** Comentario con sus respuestas anidadas. */
public record ComentarioResponse(
        Long id,
        String contenido,
        UsuarioResumen autor,
        LocalDateTime fechaCreacion,
        Long padreId,
        List<ComentarioResponse> respuestas
) {
    public static ComentarioResponse desde(Comentario c) {
        return new ComentarioResponse(
                c.getId(),
                c.getContenido(),
                UsuarioResumen.desde(c.getAutor()),
                c.getFechaCreacion(),
                c.getPadre() == null ? null : c.getPadre().getId(),
                c.getRespuestas() == null ? List.of()
                        : c.getRespuestas().stream().map(ComentarioResponse::desde).toList());
    }
}
