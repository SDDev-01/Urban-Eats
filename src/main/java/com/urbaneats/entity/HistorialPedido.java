package com.urbaneats.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad de la tabla historial_pedido.
 * Cada fila es un cambio de estado: de cual a cual, quien lo hizo y a que hora.
 * Con estas filas se calculan los tiempos de entrega en los reportes.
 */
@Getter
@Setter
@Entity
@Table(name = "historial_pedido")
public class HistorialPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoHistorial")
    private Integer codigoHistorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoPedido", nullable = false)
    private Pedido pedido;

    /** NULL cuando el pedido acaba de nacer. */
    @Column(name = "EstadoAnterior", length = 30)
    private String estadoAnterior;

    @Column(name = "EstadoNuevo", nullable = false, length = 30)
    private String estadoNuevo;

    /** Quien hizo el cambio (cliente, gerente o repartidor). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoUsuario")
    private Usuario usuario;

    @Column(name = "Comentario", length = 300)
    private String comentario;

    @Column(name = "Fecha", nullable = false)
    private LocalDateTime fecha;
}
