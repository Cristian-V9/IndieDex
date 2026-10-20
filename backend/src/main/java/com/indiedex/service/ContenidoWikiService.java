package com.indiedex.service;

import com.indiedex.dto.*;
import com.indiedex.exception.RecursoNoEncontradoException;
import com.indiedex.model.Logro;
import com.indiedex.model.Objeto;
import com.indiedex.model.Personaje;
import com.indiedex.repository.LogroRepository;
import com.indiedex.repository.ObjetoRepository;
import com.indiedex.repository.PersonajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Logros, objetos y personajes: las otras pestañas de la wiki de cada juego. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContenidoWikiService {

    private final LogroRepository logroRepository;
    private final ObjetoRepository objetoRepository;
    private final PersonajeRepository personajeRepository;
    private final JuegoService juegoService;

    // ======================= LOGROS =======================

    public List<LogroResponse> logrosDeJuego(String slug) {
        juegoService.buscarPorSlug(slug);
        return logroRepository.findByJuegoSlugOrderByNombreAsc(slug).stream().map(LogroResponse::desde).toList();
    }

    public LogroResponse obtenerLogro(Long id) {
        return LogroResponse.desde(buscarLogro(id));
    }

    @Transactional
    public LogroResponse guardarLogro(Long id, LogroRequest req) {
        Logro logro = id == null ? new Logro() : buscarLogro(id);
        logro.setJuego(juegoService.buscarEntidad(req.juegoId()));
        logro.setNombre(req.nombre().trim());
        logro.setDescripcion(req.descripcion());
        logro.setComoConseguirlo(req.comoConseguirlo());
        logro.setIcono(req.icono());
        return LogroResponse.desde(logroRepository.save(logro));
    }

    @Transactional
    public void eliminarLogro(Long id) {
        logroRepository.delete(buscarLogro(id));
    }

    private Logro buscarLogro(Long id) {
        return logroRepository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("Logro", id));
    }

    // ======================= OBJETOS =======================

    public List<ObjetoResponse> objetosDeJuego(String slug) {
        juegoService.buscarPorSlug(slug);
        return objetoRepository.findByJuegoSlugOrderByNombreAsc(slug).stream().map(ObjetoResponse::desde).toList();
    }

    public ObjetoResponse obtenerObjeto(Long id) {
        return ObjetoResponse.desde(buscarObjeto(id));
    }

    @Transactional
    public ObjetoResponse guardarObjeto(Long id, ObjetoRequest req) {
        Objeto objeto = id == null ? new Objeto() : buscarObjeto(id);
        objeto.setJuego(juegoService.buscarEntidad(req.juegoId()));
        objeto.setNombre(req.nombre().trim());
        objeto.setDescripcion(req.descripcion());
        objeto.setTipo(req.tipo());
        objeto.setRareza(req.rareza());
        objeto.setUbicacion(req.ubicacion());
        objeto.setImagen(req.imagen());
        return ObjetoResponse.desde(objetoRepository.save(objeto));
    }

    @Transactional
    public void eliminarObjeto(Long id) {
        objetoRepository.delete(buscarObjeto(id));
    }

    private Objeto buscarObjeto(Long id) {
        return objetoRepository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("Objeto", id));
    }

    // ======================= PERSONAJES =======================

    public List<PersonajeResponse> personajesDeJuego(String slug) {
        juegoService.buscarPorSlug(slug);
        return personajeRepository.findByJuegoSlugOrderByNombreAsc(slug).stream()
                .map(PersonajeResponse::desde).toList();
    }

    public PersonajeResponse obtenerPersonaje(Long id) {
        return PersonajeResponse.desde(buscarPersonaje(id));
    }

    @Transactional
    public PersonajeResponse guardarPersonaje(Long id, PersonajeRequest req) {
        Personaje personaje = id == null ? new Personaje() : buscarPersonaje(id);
        personaje.setJuego(juegoService.buscarEntidad(req.juegoId()));
        personaje.setNombre(req.nombre().trim());
        personaje.setHistoria(req.historia());
        personaje.setRol(req.rol());
        personaje.setUbicacion(req.ubicacion());
        personaje.setImagen(req.imagen());
        return PersonajeResponse.desde(personajeRepository.save(personaje));
    }

    @Transactional
    public void eliminarPersonaje(Long id) {
        personajeRepository.delete(buscarPersonaje(id));
    }

    private Personaje buscarPersonaje(Long id) {
        return personajeRepository.findById(id)
                .orElseThrow(() -> RecursoNoEncontradoException.de("Personaje", id));
    }
}
