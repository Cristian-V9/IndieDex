package com.indiedex.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Documentación automática de la API en http://localhost:8080/swagger-ui.html
 * Botón "Authorize": pegar el token que devuelve /api/auth/login.
 */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "IndieDex API",
        version = "1.0",
        description = "API REST de IndieDex, la wiki de los juegos indie en español"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
