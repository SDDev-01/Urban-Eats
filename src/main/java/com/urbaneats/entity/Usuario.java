package com.urbaneats.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidad de la tabla Usuario.
 * Las relaciones se excluyen de toString/equals/hashCode: Usuario apunta a Rol y
 * Rol apunta de vuelta a Usuario, y sin la exclusion Lombok entraria en un ciclo
 * infinito (StackOverflowError) apenas alguien imprimiera el objeto en un log.
 */
@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoUsuario")
    private Integer codigoUsuario;

    @Column(nullable = false, unique = true, name = "Correo")
    private String correo;
    @Column(nullable = false, name = "Password")
    @ToString.Exclude
    private String password;
    @Column(name = "Nombres")
    private String nombres;
    @Column(name = "Apellidos")
    private String apellidos;

    @ManyToMany
    @JoinTable(
        name = "rol_usuario",
        joinColumns = @JoinColumn(name = "CodigoUsuario"),
        inverseJoinColumns = @JoinColumn(name = "CodigoRol")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Rol> roles;

    @OneToMany(mappedBy = "usuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Direccion> direcciones;

    @OneToMany(mappedBy = "usuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Telefono> telefonos;

    @OneToOne(mappedBy = "usuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Gerente gerente;

    @OneToOne(mappedBy = "usuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Repartidor repartidor;

    @OneToOne(mappedBy = "usuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    /** Nombre completo para mostrar en pantallas y reportes. */
    public String getNombreCompleto() {
        String completo = (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
        return completo.trim();
    }

    /** True si el usuario tiene el rol con ese nombre (Cliente, Gerente, Repartidor, Tecnico). */
    public boolean tieneRol(String nombreRol) {
        return roles != null && roles.stream().anyMatch(rol -> nombreRol.equalsIgnoreCase(rol.getNombreRol()));
    }
}
