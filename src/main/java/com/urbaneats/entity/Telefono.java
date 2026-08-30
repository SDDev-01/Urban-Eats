package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "telefono")
@Data
public class Telefono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoTelefono")
    private Integer codigoTelefono;

    @Column(name = "Telefono")
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "CodigoUsuario")
    private Usuario usuario;

}
