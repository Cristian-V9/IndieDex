package com.indiedex.exception;

/** Se lanza cuando el dato ya existe, p. ej. un correo registrado (responde 409). */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
