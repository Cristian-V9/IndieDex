package com.indiedex.controller;

import com.indiedex.dto.LogroRequest;
import com.indiedex.dto.LogroResponse;
import com.indiedex.service.ContenidoWikiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** La lista por juego está en GET /api/juegos/{slug}/logros. */
@Tag(name = "5. Logros", description = "Leer: público. Crear, editar y borrar: EDITOR o ADMIN")
@RestController
@RequestMapping("/api/logros")
@RequiredArgsConstructor
public class LogroController {

    private final ContenidoWikiService contenidoWikiService;

    @Operation(summary = "Ver uno")
    @GetMapping("/{id}")
    public LogroResponse obtener(@PathVariable Long id) {
        return contenidoWikiService.obtenerLogro(id);
    }

    @Operation(summary = "Crear")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogroResponse crear(@Valid @RequestBody LogroRequest req) {
        return contenidoWikiService.guardarLogro(null, req);
    }

    @Operation(summary = "Editar")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PutMapping("/{id}")
    public LogroResponse actualizar(@PathVariable Long id, @Valid @RequestBody LogroRequest req) {
        return contenidoWikiService.guardarLogro(id, req);
    }

    @Operation(summary = "Borrar")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        contenidoWikiService.eliminarLogro(id);
    }
}
