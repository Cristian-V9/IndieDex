package com.indiedex.repository;

import com.indiedex.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

    Optional<Usuario> findByNombreUsuarioIgnoreCase(String nombreUsuario);

    /** Busca por correo o por nombre de usuario (el login acepta ambos). */
    @Query("select u from Usuario u where lower(u.email) = lower(:login) or lower(u.nombreUsuario) = lower(:login)")
    Optional<Usuario> buscarPorLogin(@Param("login") String login);
}
