package com.indiedex.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre público en la comunidad (se puede usar para iniciar sesión). */
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 30)
    private String nombreUsuario;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /** Contraseña cifrada con BCrypt (nunca en texto plano). */
    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Rol rol = Rol.USER;

    @Column(length = 280)
    private String biografia;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /** Pone la fecha actual si no se indicó otra (los datos de ejemplo usan fechas propias). */
    @PrePersist
    void alCrear() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaRegistro == null) fechaRegistro = ahora;
    }
}
