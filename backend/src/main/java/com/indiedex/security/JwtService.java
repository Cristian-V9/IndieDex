package com.indiedex.security;

import com.indiedex.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/** Crea y valida los tokens JWT que el frontend envía en cada petición. */
@Service
public class JwtService {

    private final SecretKey clave;
    private final Duration duracion;

    public JwtService(@Value("${indiedex.jwt.secreto}") String secreto,
                      @Value("${indiedex.jwt.expiracion-minutos}") long minutos) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("indiedex.jwt.secreto debe tener al menos 32 caracteres");
        }
        this.clave = Keys.hmacShaKeyFor(bytes);
        this.duracion = Duration.ofMinutes(minutos);
    }

    /** Genera un token cuyo "subject" es el nombre de usuario. */
    public String generarToken(Usuario usuario, Instant expiraEn) {
        return Jwts.builder()
                .subject(usuario.getNombreUsuario())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .issuedAt(new Date())
                .expiration(Date.from(expiraEn))
                .signWith(clave)
                .compact();
    }

    public Instant calcularExpiracion() {
        return Instant.now().plus(duracion);
    }

    /** Devuelve el nombre de usuario si el token es válido y no ha expirado. */
    public Optional<String> validarYObtenerUsuario(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
