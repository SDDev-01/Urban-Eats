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
    private Integer CodigoUsuario;
    
    @Column(nullable = false, unique = true)
    private String Correo;
    @Column(nullable = false)
    private String Password;
    private String Nombres;
    private String Apellidos;

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

    @OneToOne
    private Gerente gerente;
    
    @OneToOne
    private Repartidor repartidor;

    @OneToOne
    private Cliente cliente;

}
