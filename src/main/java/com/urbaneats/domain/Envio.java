package com.urbaneats.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "envio")
public class Envio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Long idEnvio;

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

    // Constructor vacío
    public Envio() {
    }

    // Constructor con parámetros
    public Envio(Pedido pedido, Repartidor repartidor, String estadoEnvio, LocalDateTime fechaEntrega) {
        this.pedido = pedido;
        this.repartidor = repartidor;
        this.estadoEnvio = estadoEnvio;
        this.fechaEntrega = fechaEntrega;
    }

    // Getters y Setters
    public Long getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(Long idEnvio) {
        this.idEnvio = idEnvio;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public String getEstadoEnvio() {
        return estadoEnvio;
    }

    public void setEstadoEnvio(String estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
}