package com.indiedex.dto;

/** Cifras de la cabecera del panel de revisión. */
public record ResumenRevisionResponse(
        long pendientes,
        long enRevision,
        long publicadasEsteMes,
        long rechazadas
) {
}
