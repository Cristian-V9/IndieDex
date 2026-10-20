package com.indiedex.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indiedex.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/** Respuestas JSON para 401 (sin sesión) y 403 (sin permiso) a nivel de filtros. */
@Component
@RequiredArgsConstructor
public class RespuestasSeguridad {

    private final ObjectMapper objectMapper;

    public AuthenticationEntryPoint sinAutenticar() {
        return (request, response, ex) -> escribir(response, HttpStatus.UNAUTHORIZED,
                "Necesitas iniciar sesión para realizar esta acción", request.getRequestURI());
    }

    public AccessDeniedHandler sinPermiso() {
        return (request, response, ex) -> escribir(response, HttpStatus.FORBIDDEN,
                "No tienes permiso para realizar esta acción", request.getRequestURI());
    }

    private void escribir(HttpServletResponse response, HttpStatus estado, String mensaje, String ruta)
            throws java.io.IOException {
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(),
                new ErrorResponse(estado.value(), estado.getReasonPhrase(), mensaje, ruta));
    }
}
