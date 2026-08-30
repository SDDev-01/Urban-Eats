package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "direccion")
@Data
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoDireccion")
    private Integer codigoDireccion;

    @Column(name = "Direccion")
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "CodigoUsuario")
    private Usuario usuario;

}
