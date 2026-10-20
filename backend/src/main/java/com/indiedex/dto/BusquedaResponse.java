package com.indiedex.dto;

import java.util.List;

/** Resultado del buscador de la barra de navegación. */
public record BusquedaResponse(
        List<JuegoResumenResponse> juegos,
        List<ArticuloResumenResponse> articulos,
        List<ObjetoResponse> objetos,
        List<PersonajeResponse> personajes
) {
}
