package com.indiedex.model;

/**
 * Flujo colaborativo de la presentación:
 * SUGERENCIA -> VOTOS -> REVISION -> PUBLICADO (o RECHAZADA en cualquier punto).
 */
public enum EstadoSugerencia {
    /** Recién enviada por un usuario. */
    SUGERENCIA,
    /** Alcanzó el mínimo de votos de la comunidad. */
    VOTOS,
    /** El equipo la está revisando. */
    REVISION,
    /** El contenido ya llegó a la wiki. */
    PUBLICADO,
    /** El equipo la descartó (con una nota para su autor). */
    RECHAZADA;

    /** Siguiente paso del flujo, o null si ya terminó. */
    public EstadoSugerencia siguiente() {
        return switch (this) {
            case SUGERENCIA -> VOTOS;
            case VOTOS -> REVISION;
            case REVISION -> PUBLICADO;
            case PUBLICADO, RECHAZADA -> null;
        };
    }

    /** Una sugerencia está abierta mientras no se haya publicado ni rechazado. */
    public boolean estaAbierta() {
        return this != PUBLICADO && this != RECHAZADA;
    }
}
