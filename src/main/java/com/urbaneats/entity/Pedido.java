package com.urbaneats.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
@Data
@Entity
@Table(name = "pedido")
public class Pedido  {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    // Relación ManyToOne con Cliente (Muchos pedidos pueden pertenecer a un cliente)
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @Column(nullable = false, length = 50)
    private String estado; // Ej: Pendiente, En camino, Entregado

    @Column(nullable = false)
    private Double total;

}