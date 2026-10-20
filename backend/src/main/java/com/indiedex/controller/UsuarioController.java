package com.indiedex.controller;

import com.indiedex.dto.*;
import com.indiedex.security.UsuarioPrincipal;
import com.indiedex.service.ComentarioService;
import com.indiedex.service.SugerenciaService;
import com.indiedex.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "9. Usuarios", description = "Mi perfil y administración de usuarios")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SugerenciaService sugerenciaService;
    private final ComentarioService comentarioService;

    @Operation(summary = "Mi perfil con contadores")
    @GetMapping("/me")
    public PerfilResponse perfil(@AuthenticationPrincipal UsuarioPrincipal yo) {
        return usuarioService.perfil(yo);
    }

    @Operation(summary = "Editar mi biografía")
    @PutMapping("/me")
    public UsuarioResponse actualizarPerfil(@AuthenticationPrincipal UsuarioPrincipal yo,
                                            @Valid @RequestBody ActualizarPerfilRequest req) {
        return usuarioService.actualizarPerfil(yo, req);
    }

    @Operation(summary = "Mis sugerencias")
    @GetMapping("/me/sugerencias")
    public PaginaResponse<SugerenciaResponse> misSugerencias(@AuthenticationPrincipal UsuarioPrincipal yo,
                                                             @RequestParam(defaultValue = "0") int pagina,
                                                             @RequestParam(defaultValue = "10") int tamano) {
        return sugerenciaService.deUsuario(yo.getId(), pagina, tamano, yo);
    }

    @Operation(summary = "Propuestas que voté")
    @GetMapping("/me/votos")
    public PaginaResponse<SugerenciaResponse> misVotos(@AuthenticationPrincipal UsuarioPrincipal yo,
                                                       @RequestParam(defaultValue = "0") int pagina,
                                                       @RequestParam(defaultValue = "10") int tamano) {
        return sugerenciaService.votadasPorUsuario(yo.getId(), pagina, tamano);
    }

    @Operation(summary = "Mis comentarios")
    @GetMapping("/me/comentarios")
    public PaginaResponse<ComentarioPerfilResponse> misComentarios(@AuthenticationPrincipal UsuarioPrincipal yo,
                                                                   @RequestParam(defaultValue = "0") int pagina,
                                                                   @RequestParam(defaultValue = "10") int tamano) {
        return comentarioService.deUsuario(yo.getId(), pagina, tamano);
    }

    @Operation(summary = "Listar usuarios", description = "Solo ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PaginaResponse<UsuarioResponse> listar(@RequestParam(defaultValue = "0") int pagina,
                                                  @RequestParam(defaultValue = "20") int tamano) {
        return usuarioService.listar(pagina, tamano);
    }

    @Operation(summary = "Cambiar el rol de un usuario", description = "Solo ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/rol")
    public UsuarioResponse cambiarRol(@PathVariable Long id, @Valid @RequestBody CambioRolRequest req,
                                      @AuthenticationPrincipal UsuarioPrincipal yo) {
        return usuarioService.cambiarRol(id, req, yo);
    }
}
