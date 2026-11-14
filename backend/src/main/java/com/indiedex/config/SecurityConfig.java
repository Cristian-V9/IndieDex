package com.indiedex.config;

import com.indiedex.security.JwtAuthFilter;
import com.indiedex.security.RespuestasSeguridad;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Reglas de acceso de la API.
 *
 *  - Cualquiera puede LEER el contenido público (juegos, artículos, sugerencias...).
 *  - Para comentar, sugerir y votar hay que iniciar sesión (rol USER o superior).
 *  - Crear y editar contenido de la wiki y revisar sugerencias: EDITOR o ADMIN.
 *  - Gestionar usuarios y borrar juegos: solo ADMIN.
 *
 * Las reglas finas por rol están en los controladores con @PreAuthorize.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final RespuestasSeguridad respuestasSeguridad;

    @Value("${indiedex.cors.origenes}")
    private String origenesCors;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API sin estado: no hay cookies de sesión, cada petición trae su token
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight de CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Registro e inicio de sesión
                        .requestMatchers("/api/auth/**").permitAll()
                        // Documentación Swagger
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Panel de revisión: solo el equipo
                        .requestMatchers(HttpMethod.GET, "/api/sugerencias/resumen").hasAnyRole("EDITOR", "ADMIN")
                        // Lectura pública
                        .requestMatchers(HttpMethod.GET,
                                "/api/juegos/**",
                                "/api/articulos/**",
                                "/api/logros/**",
                                "/api/objetos/**",
                                "/api/personajes/**",
                                "/api/sugerencias/**",
                                "/api/estadisticas",
                                "/api/buscar").permitAll()
                        .requestMatchers("/error").permitAll()
                        // El resto de la API requiere sesión
                        .requestMatchers("/api/**").authenticated()
                        // Fuera de /api están las páginas de Angular (se sirven desde aquí al desplegar)
                        .anyRequest().permitAll())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(respuestasSeguridad.sinAutenticar())
                        .accessDeniedHandler(respuestasSeguridad.sinPermiso()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Las contraseñas se guardan cifradas con BCrypt. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /** Permite que el frontend Angular (localhost:4200) llame a la API. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.stream(origenesCors.split(",")).map(String::trim).toList());
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cors);
        return source;
    }
}
