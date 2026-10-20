package com.indiedex.dto;

import com.indiedex.model.Logro;

public record LogroResponse(Long id, String nombre, String descripcion, String comoConseguirlo,
                            String icono, Long juegoId, String juegoSlug) {

    public static LogroResponse desde(Logro l) {
        return new LogroResponse(l.getId(), l.getNombre(), l.getDescripcion(), l.getComoConseguirlo(),
                l.getIcono(), l.getJuego().getId(), l.getJuego().getSlug());
    }
}
