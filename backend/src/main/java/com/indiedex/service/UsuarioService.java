package com.indiedex.service;

import com.indiedex.dto.*;
import com.indiedex.exception.RecursoNoEncontradoException;
import com.indiedex.exception.ReglaNegocioException;
import com.indiedex.model.EstadoSugerencia;
import com.indiedex.model.Usuario;
import com.indiedex.repository.ComentarioRepository;
import com.indiedex.repository.SugerenciaRepository;
import com.indiedex.repository.UsuarioRepository;
import com.indiedex.repository.VotoRepository;
import com.indiedex.security.UsuarioPrincipal;
import com.indiedex.util.Paginas;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SugerenciaRepository sugerenciaRepository;
    private final VotoRepository votoRepository;
    private final ComentarioRepository comentarioRepository;

    /** Mi perfil con los contadores de actividad. */
    public PerfilResponse perfil(UsuarioPrincipal yo) {
        Usuario u = buscarEntidad(yo.getId());
        return new PerfilResponse(
                UsuarioResponse.desde(u),
                sugerenciaRepository.countByAutorId(u.getId()),
                sugerenciaRepository.countByAutorIdAndEstado(u.getId(), EstadoSugerencia.PUBLICADO),
                votoRepository.countByUsuarioId(u.getId()),
                comentarioRepository.countByAutorId(u.getId()));
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(UsuarioPrincipal yo, ActualizarPerfilRequest req) {
        Usuario u = buscarEntidad(yo.getId());
        u.setBiografia(req.biografia() == null ? null : req.biografia().trim());
        return UsuarioResponse.desde(u);
    }

    /** Solo ADMIN. */
    public PaginaResponse<UsuarioResponse> listar(int pagina, int tamano) {
        var page = usuarioRepository.findAll(Paginas.de(pagina, tamano, Sort.by("nombreUsuario")));
        return PaginaResponse.desde(page, UsuarioResponse::desde);
    }

    /** Solo ADMIN: convierte a un usuario en EDITOR, ADMIN o USER. */
    @Transactional
    public UsuarioResponse cambiarRol(Long id, CambioRolRequest req, UsuarioPrincipal yo) {
        if (id.equals(yo.getId())) {
            throw new ReglaNegocioException("No puedes cambiar tu propio rol");
        }
        Usuario u = buscarEntidad(id);
        u.setRol(req.rol());
        return UsuarioResponse.desde(u);
    }

    private Usuario buscarEntidad(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("Usuario", id));
    }
}
