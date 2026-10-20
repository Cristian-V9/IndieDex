package com.indiedex.exception;

/** Se lanza cuando la petición rompe una regla de la aplicación (responde 400). */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
