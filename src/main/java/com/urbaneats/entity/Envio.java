package com.urbaneats.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "envio")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoEnvio", nullable = false)
    private Integer codigoEnvio;

    @Column(name = "Descripcion", length = 300)
    private String descripcion;

    @Column(name = "FechaEnvio")
    private LocalDate fechaEnvio;

    /** Hora estimada de entrega. */
    @Column(name = "HoraEntrega")
    private LocalTime horaEntrega;

    /** Ultima posicion que reporto el repartidor (GPS o simulacion). */
    @Column(name = "LatitudRepartidor", precision = 10, scale = 7)
    private BigDecimal latitudRepartidor;

    @Column(name = "LongitudRepartidor", precision = 10, scale = 7)
    private BigDecimal longitudRepartidor;

    @Column(name = "FechaUbicacion")
    private LocalDateTime fechaUbicacion;

    // ----- Relaciones -----

    /** El cliente que recibe el envio. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoCliente", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    /** El repartidor asignado. Es NULL mientras ningun repartidor toma el pedido. */
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

    /** La direccion de entrega que eligio el cliente al pagar. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoDireccion")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Direccion direccion;

    /** El pedido asociado. La llave foranea vive en la tabla Pedido. */
    @OneToOne(mappedBy = "envio")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pedido pedido;
}
