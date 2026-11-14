package com.indiedex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Sirve el frontend Angular desde Spring Boot cuando la aplicación está desplegada.
 *
 * Al construir la imagen Docker, el Angular compilado se copia a src/main/resources/static.
 * Angular maneja sus propias rutas (/juegos/dredge, /perfil...): si alguien abre una de
 * esas direcciones directamente, aquí se devuelve index.html y Angular muestra la página.
 *
 * En desarrollo (sin carpeta static) no hace nada: Angular corre aparte en localhost:4200.
 */
@Configuration
public class FrontendConfig implements WebMvcConfigurer {

    private static final String INDEX = "static/index.html";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String ruta, Resource ubicacion) throws IOException {
                        Resource archivo = ubicacion.createRelative(ruta);
                        if (archivo.exists() && archivo.isReadable()) {
                            return archivo; // un archivo real: imagen, .js, .css...
                        }
                        // Rutas de la API o archivos que no existen: 404 normal
                        if (ruta.startsWith("api/") || ruta.contains(".")) {
                            return null;
                        }
                        // Cualquier otra ruta es una página de Angular
                        Resource index = new ClassPathResource(INDEX);
                        return index.exists() ? index : null;
                    }
                });
    }
}
