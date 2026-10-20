package com.indiedex.util;

import java.text.Normalizer;
import java.util.Locale;

/** Utilidades de texto. */
public final class Textos {

    private Textos() {
    }

    /** Convierte "" o "   " en null y recorta espacios (para filtros opcionales). */
    public static String limpiar(String texto) {
        if (texto == null) {
            return null;
        }
        String recortado = texto.trim();
        return recortado.isEmpty() ? null : recortado;
    }

    /** "Hollow Knight: Silksong" -> "hollow-knight-silksong" */
    public static String slug(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
