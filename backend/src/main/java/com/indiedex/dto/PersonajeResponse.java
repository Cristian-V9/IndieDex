package com.indiedex.dto;

import com.indiedex.model.Personaje;

public record PersonajeResponse(Long id, String nombre, String historia, String rol, String ubicacion,
                                String imagen, Long juegoId, String juegoSlug) {

    public static PersonajeResponse desde(Personaje p) {
        return new PersonajeResponse(p.getId(), p.getNombre(), p.getHistoria(), p.getRol(), p.getUbicacion(),
                p.getImagen(), p.getJuego().getId(), p.getJuego().getSlug());
    }
}
