package com.indiedex.controller;

import com.indiedex.dto.ObjetoRequest;
import com.indiedex.dto.ObjetoResponse;
import com.indiedex.service.ContenidoWikiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** La lista por juego está en GET /api/juegos/{slug}/objetos. */
@Tag(name = "6. Objetos", description = "Leer: público. Crear, editar y borrar: EDITOR o ADMIN")
@RestController
@RequestMapping("/api/objetos")
@RequiredArgsConstructor
public class ObjetoController {

    private final ContenidoWikiService contenidoWikiService;

    @Operation(summary = "Ver uno")
    @GetMapping("/{id}")
    public ObjetoResponse obtener(@PathVariable Long id) {
        return contenidoWikiService.obtenerObjeto(id);
    }

    @Operation(summary = "Crear")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObjetoResponse crear(@Valid @RequestBody ObjetoRequest req) {
        return contenidoWikiService.guardarObjeto(null, req);
    }

    @Operation(summary = "Editar")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PutMapping("/{id}")
    public ObjetoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ObjetoRequest req) {
        return contenidoWikiService.guardarObjeto(id, req);
    }

    @Operation(summary = "Borrar")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        contenidoWikiService.eliminarObjeto(id);
    }
}
