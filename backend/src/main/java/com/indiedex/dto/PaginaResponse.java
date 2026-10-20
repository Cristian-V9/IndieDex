package com.indiedex.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/** Respuesta paginada estable (en vez de devolver el objeto Page de Spring). */
public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas,
        boolean ultima
) {
    public static <E, T> PaginaResponse<T> desde(Page<E> page, Function<E, T> mapeo) {
        return new PaginaResponse<>(
                page.getContent().stream().map(mapeo).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
