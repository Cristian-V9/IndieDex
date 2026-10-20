package com.indiedex.dto;

import com.indiedex.model.EstadoSugerencia;
import com.indiedex.model.Sugerencia;
import com.indiedex.model.TipoSugerencia;

import java.time.LocalDateTime;

public record SugerenciaResponse(
        Long id,
        String titulo,
        String descripcion,
        TipoSugerencia tipo,
        EstadoSugerencia estado,
        Long juegoId,
        String juegoTitulo,
        String juegoSlug,
        UsuarioResumen autor,
        int totalVotos,
        boolean votadaPorMi,
        LocalDateTime fechaCreacion,
        String notaRevision,
        UsuarioResumen revisor,
        LocalDateTime fechaRevision
) {
    public static SugerenciaResponse desde(Sugerencia s, boolean votadaPorMi) {
        var juego = s.getJuego();
        return new SugerenciaResponse(
                s.getId(), s.getTitulo(), s.getDescripcion(), s.getTipo(), s.getEstado(),
                juego == null ? null : juego.getId(),
                juego == null ? null : juego.getTitulo(),
                juego == null ? null : juego.getSlug(),
                UsuarioResumen.desde(s.getAutor()), s.getTotalVotos(), votadaPorMi, s.getFechaCreacion(),
                s.getNotaRevision(), UsuarioResumen.desde(s.getRevisor()), s.getFechaRevision());
    }
}
