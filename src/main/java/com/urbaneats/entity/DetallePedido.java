package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoDetalle")
    private Integer codigoDetalle;
    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario;
    
    @ManyToOne
    @JoinColumn(name = "CodigoPedido", nullable = false)
    private Pedido pedido;

   }