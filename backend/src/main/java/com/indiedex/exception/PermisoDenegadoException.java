package com.indiedex.exception;

/** El usuario está autenticado pero no puede hacer esta acción (responde 403). */
public class PermisoDenegadoException extends RuntimeException {

    public PermisoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
