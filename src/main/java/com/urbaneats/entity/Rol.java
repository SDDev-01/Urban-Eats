package com.urbaneats.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "rol")
@Data
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoRol")
    private Integer codigoRol;

    @Column(name = "NombreRol")
    private String nombreRol;
    @Column(name = "DescripcionRol")
    private String descripcionRol;

    /** Se excluye de toString/equals/hashCode para no entrar en ciclo con Usuario.roles. */
    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Usuario> usuarios;
}
