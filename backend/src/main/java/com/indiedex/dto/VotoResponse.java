package com.indiedex.dto;

import com.indiedex.model.EstadoSugerencia;

/** Resultado de votar o retirar un voto. */
public record VotoResponse(Long sugerenciaId, int totalVotos, boolean votadaPorMi, EstadoSugerencia estado) {
}
