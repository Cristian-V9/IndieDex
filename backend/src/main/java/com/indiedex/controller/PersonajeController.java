package com.indiedex.controller;

import com.indiedex.dto.PersonajeRequest;
import com.indiedex.dto.PersonajeResponse;
import com.indiedex.service.ContenidoWikiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** La lista por juego está en GET /api/juegos/{slug}/personajes. */
@Tag(name = "7. Personajes", description = "Leer: público. Crear, editar y borrar: EDITOR o ADMIN")
@RestController
@RequestMapping("/api/personajes")
@RequiredArgsConstructor
public class PersonajeController {

    private final ContenidoWikiService contenidoWikiService;

    @Operation(summary = "Ver uno")
    @GetMapping("/{id}")
    public PersonajeResponse obtener(@PathVariable Long id) {
        return contenidoWikiService.obtenerPersonaje(id);
    }

    @Operation(summary = "Crear")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonajeResponse crear(@Valid @RequestBody PersonajeRequest req) {
        return contenidoWikiService.guardarPersonaje(null, req);
    }

    @Operation(summary = "Editar")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PutMapping("/{id}")
    public PersonajeResponse actualizar(@PathVariable Long id, @Valid @RequestBody PersonajeRequest req) {
        return contenidoWikiService.guardarPersonaje(id, req);
    }

    @Operation(summary = "Borrar")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        contenidoWikiService.eliminarPersonaje(id);
    }
}
