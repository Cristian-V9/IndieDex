package com.indiedex.dto;

import com.indiedex.model.Comentario;

import java.time.LocalDateTime;

/** Comentario visto desde el perfil: incluye a qué artículo pertenece. */
public record ComentarioPerfilResponse(Long id, String contenido, LocalDateTime fechaCreacion,
                                       Long articuloId, String articuloTitulo) {

    public static ComentarioPerfilResponse desde(Comentario c) {
        return new ComentarioPerfilResponse(c.getId(), c.getContenido(), c.getFechaCreacion(),
                c.getArticulo().getId(), c.getArticulo().getTitulo());
    }
}
