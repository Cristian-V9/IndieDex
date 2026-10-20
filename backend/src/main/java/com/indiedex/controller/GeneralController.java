package com.indiedex.controller;

import com.indiedex.dto.BusquedaResponse;
import com.indiedex.dto.EstadisticasResponse;
import com.indiedex.service.GeneralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "0. General", description = "Cifras del inicio y buscador")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GeneralController {

    private final GeneralService generalService;

    @Operation(summary = "Cifras de IndieDex (artículos, colaboradores, wikis abiertas)")
    @GetMapping("/estadisticas")
    public EstadisticasResponse estadisticas() {
        return generalService.estadisticas();
    }

    @Operation(summary = "Buscar en juegos, artículos, objetos y personajes (mínimo 2 letras)")
    @GetMapping("/buscar")
    public BusquedaResponse buscar(@RequestParam(defaultValue = "") String q) {
        return generalService.buscar(q);
    }
}
