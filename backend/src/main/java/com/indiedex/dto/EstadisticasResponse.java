package com.indiedex.dto;

/** Cifras del inicio: "128 artículos · 64 colaboradores · 1 wiki abierta". */
public record EstadisticasResponse(
        long totalArticulos,
        long totalColaboradores,
        long wikisAbiertas,
        long totalJuegos,
        long sugerenciasAbiertas
) {
}
