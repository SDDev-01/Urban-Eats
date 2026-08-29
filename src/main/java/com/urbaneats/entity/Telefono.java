package com.urbaneats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
