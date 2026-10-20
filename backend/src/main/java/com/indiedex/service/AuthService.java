package com.indiedex.service;

import com.indiedex.dto.AuthResponse;
import com.indiedex.dto.LoginRequest;
import com.indiedex.dto.RegistroRequest;
import com.indiedex.dto.UsuarioResponse;
import com.indiedex.exception.ConflictoException;
import com.indiedex.exception.RecursoNoEncontradoException;
import com.indiedex.model.Rol;
import com.indiedex.model.Usuario;
import com.indiedex.repository.UsuarioRepository;
import com.indiedex.security.JwtService;
import com.indiedex.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/** Registro e inicio de sesión. */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /** Crea la cuenta (siempre con rol USER) y devuelve un token para entrar directamente. */
    @Transactional
    public AuthResponse registrar(RegistroRequest req) {
        String nombre = req.nombreUsuario().trim();
        String email = req.email().trim().toLowerCase();

        if (usuarioRepository.existsByNombreUsuarioIgnoreCase(nombre)) {
            throw new ConflictoException("Ese nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictoException("Ya existe una cuenta con ese correo");
        }

        Usuario usuario = Usuario.builder()
                .nombreUsuario(nombre)
                .email(email)
                .password(passwordEncoder.encode(req.password()))
                .rol(Rol.USER)
                .build();
        usuarioRepository.save(usuario);
        return respuestaConToken(usuario);
    }

    /** Valida correo/usuario + contraseña con Spring Security y genera el token. */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.usuario().trim(), req.password()));
        UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
        Usuario usuario = usuarioRepository.findById(principal.getId())
                .orElseThrow(() -> RecursoNoEncontradoException.de("Usuario", principal.getId()));
        return respuestaConToken(usuario);
    }

    private AuthResponse respuestaConToken(Usuario usuario) {
        Instant expiraEn = jwtService.calcularExpiracion();
        String token = jwtService.generarToken(usuario, expiraEn);
        return new AuthResponse(token, expiraEn, UsuarioResponse.desde(usuario));
    }
}
