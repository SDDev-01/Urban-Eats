package com.urbaneats.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
@Data
@Entity
@Table(name = "envio")
public class Envio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Integer idEnvio;

    @OneToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_repartidor")
    private Repartidor repartidor;

    @Column(nullable = false, length = 50)
    private String estadoEnvio; // Ej: Asignado, En camino, Entregado

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    }