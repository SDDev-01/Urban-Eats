package com.urbaneats.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

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

    @ManyToMany(mappedBy = "roles")
    private List<Usuario> usuarios;
}
