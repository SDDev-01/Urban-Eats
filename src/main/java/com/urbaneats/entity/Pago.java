package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad de la tabla Pago.
 * En el Schema.sql el pago pertenece a un cliente y puede quedar ligado
 * a un envio una vez se arma el pedido (por eso CodigoEnvio admite NULL).
 */
@Data
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoPago")
    private Integer codigoPago;

    @Column(name = "Monto", precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "FechaPago")
    private LocalDate fechaPago;

    @Column(name = "HoraPago")
    private LocalTime horaPago;

    /** Valores del ENUM en BD: pending, approved, rejected, in_process, authorized, cancelled, refunded, charged_back. */
    @Column(name = "EstadoPago", nullable = false, length = 50)
    private String estadoPago;

    // ----- Relaciones -----

    /** El cliente que realiza el pago. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoCliente", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    /** El envio que este pago cubre. Es NULL mientras el pedido aun no se arma. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoEnvio")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Envio envio;

    @OneToOne(mappedBy = "pago", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Transaccion transaccion;
}
