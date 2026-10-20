package com.indiedex.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Construye la paginación con límites seguros. */
public final class Paginas {

    public static final int TAMANO_MAXIMO = 50;

    private Paginas() {
    }

    public static Pageable de(int pagina, int tamano, Sort orden) {
        int p = Math.max(pagina, 0);
        int t = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO);
        return PageRequest.of(p, t, orden);
    }
}
