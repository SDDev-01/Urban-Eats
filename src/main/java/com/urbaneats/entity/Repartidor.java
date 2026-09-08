package com.urbaneats.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidad de la tabla Repartidor.
 * En el Schema.sql la tabla solo tiene CodigoRepartidor y CodigoUsuario:
 * los datos personales viven en Usuario y los del vehiculo en Vehiculo.
 */
@Data
@Entity
@Table(name = "repartidor")
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoRepartidor", nullable = false)
    private Integer codigoRepartidor;

    // ----- Relaciones -----

    /** Un repartidor corresponde a un usuario del sistema. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoUsuario", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    /** El vehiculo con el que reparte. */
    @OneToOne(mappedBy = "repartidor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Vehiculo vehiculo;

    /** Los envios que ha tomado. */
    @OneToMany(mappedBy = "repartidor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Envio> envios;
}
