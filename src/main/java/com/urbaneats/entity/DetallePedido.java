package com.urbaneats.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidad de la tabla DetallePedido.
 * Cada fila es una linea del pedido: que plato, cuantos y a que precio se vendio.
 */
@Data
@Entity
@Table(name = "DetallePedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoDetalle", nullable = false)
    private Integer codigoDetalle;

    @Column(name = "Cantidad", nullable = false)
    private Integer cantidad;

    /** Precio del plato al momento de la venta, no el precio actual del menu. */
    @Column(name = "PrecioUnitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    // ----- Relaciones -----

    /** El pedido al que pertenece esta linea. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoPedido", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pedido pedido;

    /** El plato vendido en esta linea. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoPlato", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Plato plato;
}
