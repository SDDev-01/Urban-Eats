package com.urbaneats.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidad de la tabla Pedido.
 * En el Schema.sql no hay columna de total ni de cliente: el total se calcula
 * sumando el detalle, y el cliente se alcanza a traves del envio.
 */
@Data
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoPedido", nullable = false)
    private Integer codigoPedido;

    @Column(name = "FechaPedido")
    private LocalDate fechaPedido;

    /** Valores del ENUM en BD: Iniciando, En Proceso, Entregado, Cancelado. */
    @Column(name = "Estado", nullable = false, length = 20)
    private String estado;

    // ----- Relaciones -----

    /** El envio del pedido. La llave foranea vive en esta tabla y es unica. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoEnvio", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Envio envio;

    /** El restaurante al que se le hizo el pedido. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoRestaurante", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Restaurante restaurante;

    /** Las lineas del pedido: que plato, cuantos y a que precio. */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<DetallePedido> detalles;
}
