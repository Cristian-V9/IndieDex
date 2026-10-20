package com.indiedex.controller;

import com.indiedex.dto.*;
import com.indiedex.model.EstadoArticulo;
import com.indiedex.security.UsuarioPrincipal;
import com.indiedex.service.ArticuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "3. Artículos", description = "Guías de la wiki. Escribir y publicar: EDITOR o ADMIN")
@RestController
@RequestMapping("/api/articulos")
@RequiredArgsConstructor
public class ArticuloController {

    private final ArticuloService articuloService;

    @Operation(summary = "Listar artículos",
            description = "Por defecto solo publicados. Filtrar por BORRADOR o EN_REVISION requiere rol EDITOR/ADMIN")
    @GetMapping
    public PaginaResponse<ArticuloResumenResponse> listar(@RequestParam(required = false) List<EstadoArticulo> estado,
                                                          @RequestParam(defaultValue = "0") int pagina,
                                                          @RequestParam(defaultValue = "10") int tamano,
                                                          @AuthenticationPrincipal UsuarioPrincipal yo) {
        return articuloService.listar(estado, pagina, tamano, yo);
    }

    @Operation(summary = "Últimos artículos publicados (inicio)")
    @GetMapping("/recientes")
    public List<ArticuloResumenResponse> recientes(@RequestParam(defaultValue = "3") int limite) {
        return articuloService.recientes(limite);
    }

    @Operation(summary = "Artículo completo")
    @GetMapping("/{id}")
    public ArticuloDetalleResponse obtener(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal yo) {
        return articuloService.obtener(id, yo);
    }

    @Operation(summary = "Escribir un artículo")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticuloDetalleResponse crear(@Valid @RequestBody ArticuloRequest req,
                                         @AuthenticationPrincipal UsuarioPrincipal yo) {
        return articuloService.crear(req, yo);
    }

    @Operation(summary = "Editar un artículo")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PutMapping("/{id}")
    public ArticuloDetalleResponse actualizar(@PathVariable Long id, @Valid @RequestBody ArticuloRequest req) {
        return articuloService.actualizar(id, req);
    }

    @Operation(summary = "Cambiar el estado (BORRADOR, EN_REVISION, PUBLICADO)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PatchMapping("/{id}/estado")
    public ArticuloDetalleResponse cambiarEstado(@PathVariable Long id,
                                                 @Valid @RequestBody CambioEstadoArticuloRequest req) {
        return articuloService.cambiarEstado(id, req.estado());
    }

    @Operation(summary = "Borrar un artículo y sus comentarios")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        articuloService.eliminar(id);
    }
}
