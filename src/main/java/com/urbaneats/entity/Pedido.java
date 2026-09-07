package com.urbaneats.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import java.util.List;
@Data
@Entity
@Table(name = "pedido")
public class Pedido  {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoPedido")
    private Integer idPedido;

    @Column(name = "FechaPedido", nullable = false)
    private LocalDateTime fechaPedido;

    @Column(name = "Estado", nullable = false, length = 50)
    private String estado; // Ej: Pendiente, En camino, Entregado

    @Column(name = "Total", nullable = false)
    private Double total;
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Envio envio;
    
 // Relación ManyToOne con Cliente (Muchos pedidos pueden pertenecer a un cliente)
    @ManyToOne
    @JoinColumn(name = "CodigoCliente", nullable = false)
    private Cliente cliente;
}