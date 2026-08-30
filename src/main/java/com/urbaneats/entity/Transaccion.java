package com.urbaneats.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transacciones")
public class Transaccion {

    @Id
    @Column(name = "transaccion_id", length = 100)
    private String transaccionId;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime fechaTransaccion;

    @Column(name = "resultado", nullable = false, length = 50)
    private String resultado;

    @ManyToOne
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago;

    public Transaccion() {
    }

    public Transaccion(String transaccionId, LocalDateTime fechaTransaccion, String resultado, Pago pago) {
        this.transaccionId = transaccionId;
        this.fechaTransaccion = fechaTransaccion;
        this.resultado = resultado;
        this.pago = pago;
    }

    public String getTransaccionId() {
        return transaccionId;
    }

    public void setTransaccionId(String transaccionId) {
        this.transaccionId = transaccionId;
    }

    public LocalDateTime getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDateTime fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
}