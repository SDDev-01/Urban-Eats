package com.urbaneats.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
@Data
@Entity
@Table(name = "envio")
public class Envio  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Integer idEnvio;

    @ManyToOne
    @JoinColumn(name = "id_repartidor")
    private Repartidor repartidor;

    @Column(nullable = false, length = 50)
    private String estadoEnvio; // Ej: Asignado, En camino, Entregado

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
  @OneToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    }