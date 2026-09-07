package com.urbaneats.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidad de la tabla Envio.
 * El envio es el que conoce al cliente, al repartidor y al restaurante;
 * el pedido llega a esos datos a traves de el.
 */
@Data
@Entity
@Table(name = "Envio")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoEnvio", nullable = false)
    private Integer codigoEnvio;

    @Column(name = "Descripcion", length = 300)
    private String descripcion;

    @Column(name = "FechaEnvio")
    private LocalDate fechaEnvio;

    @Column(name = "HoraEntrega")
    private LocalTime horaEntrega;

    // ----- Relaciones -----

    /** El cliente que recibe el envio. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoCliente", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    /** El repartidor asignado. En el Schema.sql admite NULL: un envio puede estar sin tomar. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoRepartidor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Repartidor repartidor;

    /** El restaurante desde donde sale el envio. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoRestaurante", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Restaurante restaurante;

    /** El pedido asociado. La llave foranea vive en la tabla Pedido. */
    @OneToOne(mappedBy = "envio")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pedido pedido;
}
