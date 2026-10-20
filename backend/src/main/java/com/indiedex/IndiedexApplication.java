package com.indiedex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST de IndieDex.
 *
 * Arquitectura por capas:
 *   controller  -> recibe las peticiones HTTP y responde JSON (DTOs)
 *   service     -> lógica de negocio y reglas de la aplicación
 *   repository  -> acceso a la base de datos con Spring Data JPA
 *   model       -> entidades JPA (tablas de MySQL)
 */
@SpringBootApplication
public class IndiedexApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndiedexApplication.class, args);
    }
}
