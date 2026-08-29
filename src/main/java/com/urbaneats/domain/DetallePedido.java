package com.urbaneats.domain;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    // Nota: Como la tabla 'plato' pertenece a la Persona 2, asegurate de que 
    // la entidad Plato exista o haz referencia a ella según el modelo de tu equipo.
    @ManyToOne
    @JoinColumn(name = "id_plato", nullable = false)
    private Plato plato; 

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario;

    // Constructor vacío
    public DetallePedido() {
    }

    // Constructor con parámetros
    public DetallePedido(Pedido pedido, Plato plato, Integer cantidad, Double precioUnitario) {
        this.pedido = pedido;
        this.plato = plato;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters y Setters
    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}