package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidad de la tabla Vehiculo.
 * La llave primaria es la Placa, no un codigo autoincremental.
 */
@Data
@Entity
@Table(name = "Vehiculo")
public class Vehiculo {

    @Id
    @Column(name = "Placa", nullable = false, length = 20)
    private String placa;

    @Column(name = "TipoVehiculo", nullable = false, length = 20)
    private String tipoVehiculo;

    @Column(name = "SeguroVehiculo", length = 100)
    private String seguroVehiculo;

    @Column(name = "SOAT", length = 100)
    private String soat;

    // ----- Relaciones -----

    /** El repartidor duenio del vehiculo. En el Schema.sql admite NULL. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoRepartidor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Repartidor repartidor;
}
