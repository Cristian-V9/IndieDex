package com.indiedex.config;

import com.indiedex.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contraseña propia para la cuenta "admin" en la página publicada.
 *
 * Las cuentas de prueba usan "indiedex123", que aparece en el repositorio público.
 * Si existe la variable de entorno ADMIN_PASSWORD (por ejemplo en Render), al arrancar
 * se le pone esa contraseña a "admin". Sin la variable (en local) no cambia nada.
 *
 * Se ejecuta después de DatosEjemplo (@Order 2), así también aplica la primera vez.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class ClaveAdmin implements CommandLineRunner {

    private static final String ADMIN = "admin";
    private static final int LARGO_MINIMO = 8;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${indiedex.admin-password:}")
    private String claveNueva;

    @Override
    @Transactional
    public void run(String... args) {
        if (claveNueva == null || claveNueva.isBlank()) {
            return; // Sin ADMIN_PASSWORD: se mantiene la contraseña actual
        }
        if (claveNueva.length() < LARGO_MINIMO) {
            log.warn("ADMIN_PASSWORD tiene menos de {} caracteres: no se aplicó", LARGO_MINIMO);
            return;
        }
        usuarioRepository.findByNombreUsuarioIgnoreCase(ADMIN).ifPresent(admin -> {
            if (!passwordEncoder.matches(claveNueva, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(claveNueva));
                log.info("Contraseña de la cuenta '{}' actualizada desde ADMIN_PASSWORD", ADMIN);
            }
        });
    }
}
