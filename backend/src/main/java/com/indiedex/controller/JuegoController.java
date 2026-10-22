package com.indiedex.controller;

import com.indiedex.dto.*;
import com.indiedex.model.EstadoWiki;
import com.indiedex.service.ArticuloService;
import com.indiedex.service.ContenidoWikiService;
import com.indiedex.service.JuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.indiedex.security.UsuarioPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "2. Juegos", description = "Catálogo y wiki de cada juego")
@RestController
@RequestMapping("/api/juegos")
@RequiredArgsConstructor
public class JuegoController {

    private final JuegoService juegoService;
    private final ArticuloService articuloService;
    private final ContenidoWikiService contenidoWikiService;

    @Operation(summary = "Catálogo de juegos",
            description = "Filtros opcionales. orden: destacados (por defecto), articulos o nombre")
    @GetMapping
    public List<JuegoResumenResponse> listar(@RequestParam(required = false) String buscar,
                                             @RequestParam(required = false) String genero,
                                             @RequestParam(required = false) EstadoWiki estado,
                                             @RequestParam(defaultValue = "destacados") String orden) {
        return juegoService.listar(buscar, genero, estado, orden);
    }

    @Operation(summary = "Ficha de un juego por su slug (ej: dredge)")
    @GetMapping("/{slug}")
    public JuegoDetalleResponse obtener(@PathVariable String slug) {
        return juegoService.obtenerPorSlug(slug);
    }

    @Operation(summary = "Guías de un juego (pestaña Guías)",
            description = "Público: solo publicadas. Con sesión de EDITOR/ADMIN incluye borradores y en revisión")
    @GetMapping("/{slug}/articulos")
    public PaginaResponse<ArticuloResumenResponse> articulos(@PathVariable String slug,
                                                             @RequestParam(required = false) String buscar,
                                                             @RequestParam(defaultValue = "0") int pagina,
                                                             @RequestParam(defaultValue = "10") int tamano,
                                                             @AuthenticationPrincipal UsuarioPrincipal yo) {
        return articuloService.listarPorJuego(slug, buscar, pagina, tamano, yo);
    }

    @Operation(summary = "Logros de un juego (pestaña Logros)")
    @GetMapping("/{slug}/logros")
    public List<LogroResponse> logros(@PathVariable String slug) {
        return contenidoWikiService.logrosDeJuego(slug);
    }

    @Operation(summary = "Objetos de un juego (pestaña Objetos)")
    @GetMapping("/{slug}/objetos")
    public List<ObjetoResponse> objetos(@PathVariable String slug) {
        return contenidoWikiService.objetosDeJuego(slug);
    }

    @Operation(summary = "Personajes de un juego (pestaña Personajes)")
    @GetMapping("/{slug}/personajes")
    public List<PersonajeResponse> personajes(@PathVariable String slug) {
        return contenidoWikiService.personajesDeJuego(slug);
    }

    @Operation(summary = "Agregar un juego al catálogo", description = "Rol EDITOR o ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JuegoDetalleResponse crear(@Valid @RequestBody JuegoRequest req) {
        return juegoService.crear(req);
    }

    @Operation(summary = "Editar un juego", description = "Rol EDITOR o ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PutMapping("/{id}")
    public JuegoDetalleResponse actualizar(@PathVariable Long id, @Valid @RequestBody JuegoRequest req) {
        return juegoService.actualizar(id, req);
    }

    @Operation(summary = "Borrar un juego y toda su wiki", description = "Solo ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        juegoService.eliminar(id);
    }
}
