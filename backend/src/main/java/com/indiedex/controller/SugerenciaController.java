package com.indiedex.controller;

import com.indiedex.dto.*;
import com.indiedex.model.EstadoSugerencia;
import com.indiedex.model.TipoSugerencia;
import com.indiedex.security.UsuarioPrincipal;
import com.indiedex.service.SugerenciaService;
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

@Tag(name = "8. Sugerencias y votos",
        description = "Flujo: SUGERENCIA → VOTOS → REVISION → PUBLICADO (o RECHAZADA)")
@RestController
@RequestMapping("/api/sugerencias")
@RequiredArgsConstructor
public class SugerenciaController {

    private final SugerenciaService sugerenciaService;

    @Operation(summary = "Listar propuestas",
            description = "estado se puede repetir (?estado=VOTOS&estado=REVISION). Sin estado: todas menos rechazadas. "
                    + "orden: votos (por defecto) o recientes. Con sesión indica votadaPorMi.")
    @GetMapping
    public PaginaResponse<SugerenciaResponse> listar(@RequestParam(required = false) List<EstadoSugerencia> estado,
                                                     @RequestParam(required = false) TipoSugerencia tipo,
                                                     @RequestParam(required = false) Long juegoId,
                                                     @RequestParam(defaultValue = "votos") String orden,
                                                     @RequestParam(defaultValue = "0") int pagina,
                                                     @RequestParam(defaultValue = "10") int tamano,
                                                     @AuthenticationPrincipal UsuarioPrincipal yo) {
        return sugerenciaService.listar(estado, tipo, juegoId, orden, pagina, tamano, yo);
    }

    @Operation(summary = "Cifras del panel de revisión", description = "Rol EDITOR o ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/resumen")
    public ResumenRevisionResponse resumen() {
        return sugerenciaService.resumen();
    }

    @Operation(summary = "Ver una propuesta")
    @GetMapping("/{id}")
    public SugerenciaResponse obtener(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal yo) {
        return sugerenciaService.obtener(id, yo);
    }

    @Operation(summary = "Enviar una propuesta", description = "Requiere sesión. Empieza en estado SUGERENCIA")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SugerenciaResponse crear(@Valid @RequestBody SugerenciaRequest req,
                                    @AuthenticationPrincipal UsuarioPrincipal yo) {
        return sugerenciaService.crear(req, yo);
    }

    @Operation(summary = "Votar una propuesta", description = "Un voto por usuario")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/voto")
    public VotoResponse votar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal yo) {
        return sugerenciaService.votar(id, yo);
    }

    @Operation(summary = "Retirar mi voto")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}/voto")
    public VotoResponse retirarVoto(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal yo) {
        return sugerenciaService.retirarVoto(id, yo);
    }

    @Operation(summary = "Aprobar: pasar al siguiente estado", description = "Rol EDITOR o ADMIN. Nota opcional")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PatchMapping("/{id}/avanzar")
    public SugerenciaResponse avanzar(@PathVariable Long id,
                                      @Valid @RequestBody(required = false) RevisionRequest req,
                                      @AuthenticationPrincipal UsuarioPrincipal yo) {
        return sugerenciaService.avanzar(id, req == null ? null : req.nota(), yo);
    }

    @Operation(summary = "Rechazar con una nota para el autor", description = "Rol EDITOR o ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    @PatchMapping("/{id}/rechazar")
    public SugerenciaResponse rechazar(@PathVariable Long id, @Valid @RequestBody RechazoRequest req,
                                       @AuthenticationPrincipal UsuarioPrincipal yo) {
        return sugerenciaService.rechazar(id, req.nota(), yo);
    }

    @Operation(summary = "Borrar una propuesta", description = "Su autor (si no se ha revisado) o ADMIN")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal yo) {
        sugerenciaService.eliminar(id, yo);
    }
}
