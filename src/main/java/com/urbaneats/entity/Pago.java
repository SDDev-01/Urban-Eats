package com.urbaneats.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_id")
    private Integer pagoId;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "estado_pago", nullable = false, length = 50)
    private String estadoPago;

    public Pago() {
    }

    public Pago(Integer pagoId, String metodoPago, BigDecimal monto, String estadoPago) {
        this.pagoId = pagoId;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.estadoPago = estadoPago;
    }

    public Integer getPagoId() {
        return pagoId;
    }

    public void setPagoId(Integer pagoId) {
        this.pagoId = pagoId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
}