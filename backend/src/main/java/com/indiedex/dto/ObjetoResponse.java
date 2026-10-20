package com.indiedex.dto;

import com.indiedex.model.Objeto;

public record ObjetoResponse(Long id, String nombre, String descripcion, String tipo, String rareza,
                             String ubicacion, String imagen, Long juegoId, String juegoSlug) {

    public static ObjetoResponse desde(Objeto o) {
        return new ObjetoResponse(o.getId(), o.getNombre(), o.getDescripcion(), o.getTipo(), o.getRareza(),
                o.getUbicacion(), o.getImagen(), o.getJuego().getId(), o.getJuego().getSlug());
    }
}
