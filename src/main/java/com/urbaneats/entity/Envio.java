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
    @Column(name = "CodigoEnvio")
    private Integer codigoEnvio;

    @ManyToOne
    @JoinColumn(name = "Codigorepartidor")
    private Repartidor repartidor;

    @Column(nullable = false, length = 50)
    private String estadoEnvio; // Ej: Asignado, En camino, Entregado

    @Column(name = "FechaEntrega")
    private LocalDateTime fechaEntrega;
  @OneToOne
    @JoinColumn(name = "CodigoPedido", nullable = false)
    private Pedido pedido;
    }