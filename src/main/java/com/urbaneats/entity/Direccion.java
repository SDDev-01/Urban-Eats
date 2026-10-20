package com.urbaneats.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "direccion")
@Data
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoDireccion")
    private Integer codigoDireccion;

    @Column(name = "Direccion", length = 200)
    private String direccion;

    /** Coordenadas del pin en el mapa; con ellas se calcula la ruta del domicilio. */
    @Column(name = "Latitud", precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(name = "Longitud", precision = 10, scale = 7)
    private BigDecimal longitud;

    @ManyToOne
    @JoinColumn(name = "CodigoUsuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    /** True si la direccion tiene su punto en el mapa. */
    public boolean tieneCoordenadas() {
        return latitud != null && longitud != null;
    }
}
