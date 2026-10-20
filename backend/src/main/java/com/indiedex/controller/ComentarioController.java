package com.indiedex.controller;

import com.indiedex.dto.ComentarioRequest;
import com.indiedex.dto.ComentarioResponse;
import com.indiedex.security.UsuarioPrincipal;
import com.indiedex.service.ComentarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "4. Comentarios", description = "Conversación en cada artículo")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @Operation(summary = "Comentarios de un artículo (con sus respuestas)")
    @GetMapping("/articulos/{articuloId}/comentarios")
    public List<ComentarioResponse> listar(@PathVariable Long articuloId,
                                           @AuthenticationPrincipal UsuarioPrincipal yo) {
        return comentarioService.listar(articuloId, yo);
    }

    @Operation(summary = "Comentar o responder (padreId)", description = "Requiere sesión")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/articulos/{articuloId}/comentarios")
    @ResponseStatus(HttpStatus.CREATED)
    public ComentarioResponse crear(@PathVariable Long articuloId,
                                    @Valid @RequestBody ComentarioRequest req,
                                    @AuthenticationPrincipal UsuarioPrincipal yo) {
        return comentarioService.crear(articuloId, req, yo);
    }

    @Operation(summary = "Borrar un comentario", description = "Su autor o el equipo")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/comentarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal yo) {
        comentarioService.eliminar(id, yo);
    }
}
