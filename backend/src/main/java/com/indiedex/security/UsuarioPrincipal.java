package com.indiedex.security;

import com.indiedex.model.Rol;
import com.indiedex.model.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Usuario autenticado tal como lo ve Spring Security.
 * En los controladores se obtiene con @AuthenticationPrincipal UsuarioPrincipal yo.
 */
@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Long id;
    private final String nombreUsuario;
    private final String password;
    private final Rol rol;

    public UsuarioPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.nombreUsuario = usuario.getNombreUsuario();
        this.password = usuario.getPassword();
        this.rol = usuario.getRol();
    }

    /** EDITOR y ADMIN pueden revisar, publicar y moderar. */
    public boolean esEquipo() {
        return rol == Rol.EDITOR || rol == Rol.ADMIN;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring usa el prefijo ROLE_ para hasRole('EDITOR')
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nombreUsuario;
    }
}
