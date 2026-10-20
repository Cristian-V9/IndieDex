package com.indiedex.service;

import com.indiedex.dto.*;
import com.indiedex.model.EstadoArticulo;
import com.indiedex.model.EstadoSugerencia;
import com.indiedex.model.EstadoWiki;
import com.indiedex.repository.*;
import com.indiedex.util.Textos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Cifras del inicio y buscador global. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeneralService {

    private static final int RESULTADOS_POR_TIPO = 5;

    private final JuegoRepository juegoRepository;
    private final ArticuloRepository articuloRepository;
    private final ComentarioRepository comentarioRepository;
    private final SugerenciaRepository sugerenciaRepository;
    private final ObjetoRepository objetoRepository;
    private final PersonajeRepository personajeRepository;
    private final JuegoService juegoService;

    public EstadisticasResponse estadisticas() {
        Set<Long> colaboradores = new HashSet<>(articuloRepository.autoresPublicados());
        colaboradores.addAll(comentarioRepository.autores());
        colaboradores.addAll(sugerenciaRepository.autores());

        return new EstadisticasResponse(
                articuloRepository.countByEstado(EstadoArticulo.PUBLICADO),
                colaboradores.size(),
                juegoRepository.countByEstadoWiki(EstadoWiki.ABIERTA),
                juegoRepository.count(),
                sugerenciaRepository.countByEstadoIn(List.of(
                        EstadoSugerencia.SUGERENCIA, EstadoSugerencia.VOTOS, EstadoSugerencia.REVISION)));
    }

    /** Busca a la vez en juegos, artículos publicados, objetos y personajes. */
    public BusquedaResponse buscar(String q) {
        String texto = Textos.limpiar(q);
        if (texto == null || texto.length() < 2) {
            return new BusquedaResponse(List.of(), List.of(), List.of(), List.of());
        }
        Pageable primeros = PageRequest.of(0, RESULTADOS_POR_TIPO);

        return new BusquedaResponse(
                juegoService.listar(texto, null, null, "destacados").stream()
                        .limit(RESULTADOS_POR_TIPO).toList(),
                articuloRepository.buscarPublicados(texto, primeros).stream()
                        .map(ArticuloResumenResponse::desde).toList(),
                objetoRepository.findByNombreContainingIgnoreCase(texto, primeros).stream()
                        .map(ObjetoResponse::desde).toList(),
                personajeRepository.findByNombreContainingIgnoreCase(texto, primeros).stream()
                        .map(PersonajeResponse::desde).toList());
    }
}
