package com.urbaneats.entity;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

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
    private List<Rol> roles;

    @OneToMany(mappedBy = "usuario")
    private List<Direccion> direcciones;

    @OneToMany(mappedBy = "usuario")
    private List<Telefono> telefonos;

    @OneToOne(mappedBy = "usuario")
    private Gerente gerente;
    
    @OneToOne(mappedBy = "usuario")
    private Repartidor repartidor;

    @OneToOne(mappedBy = "usuario")
    private Cliente cliente;

}
