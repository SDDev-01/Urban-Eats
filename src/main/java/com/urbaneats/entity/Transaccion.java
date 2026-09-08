package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidad de la tabla Transaccion.
 * El TransaccionID no se autogenera: es el id que devuelve la pasarela de pago
 * (MercadoPago) al procesar la transaccion, por eso la PK es un VARCHAR asignado a mano.
 */
@Data
@Entity
@Table(name = "transaccion")
public class Transaccion {

    @Id
    @Column(name = "TransaccionID", length = 50)
    private String transaccionID;

    @Column(name = "MetodoPago", length = 50)
    private String metodoPago;

    @Column(name = "BancoNombre", length = 50)
    private String bancoNombre;

    @Column(name = "CUS", length = 50)
    private String cus;

    @Column(name = "CodigoRespuesta", length = 50)
    private String codigoRespuesta;

    // ----- Relaciones -----

    @OneToOne
    @JoinColumn(name = "CodigoPago", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pago pago;
}
