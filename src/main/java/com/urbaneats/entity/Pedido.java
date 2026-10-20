package com.urbaneats.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidad de la tabla Pedido.
 * En el Schema.sql no hay columna de total ni de cliente: el total se calcula
 * sumando el detalle, y el cliente se alcanza a traves del envio.
 *
 * Ojo: el pedido NO se inserta desde Java. Lo crea el trigger
 * crear_pedido_automaticamente al guardar el envio (ver PedidoDesdeCarritoService).
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

    /** Fecha y hora real del pedido. La llena la base (DEFAULT CURRENT_TIMESTAMP). */
    @Column(name = "FechaHora", insertable = false, updatable = false)
    private LocalDateTime fechaHora;

    /** Uno de los seis estados de EstadoPedido; el converter lo guarda como texto. */
    @Convert(converter = EstadoPedidoConverter.class)
    @Column(name = "Estado", nullable = false, length = 30)
    private EstadoPedido estado;

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
