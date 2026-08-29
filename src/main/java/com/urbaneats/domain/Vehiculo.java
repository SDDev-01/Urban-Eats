package com.urbaneats.domain;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "vehiculo")
public class Vehiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    // Relación OneToOne o ManyToOne con Repartidor
    @OneToOne
    @JoinColumn(name = "id_repartidor", nullable = false)
    private Repartidor repartidor;

    @Column(nullable = false, length = 50)
    private String tipo; // Ej: Motocicleta, Bicicleta, Automóvil

    @Column(nullable = false, unique = true, length = 20)
    private String placa;

    @Column(length = 100)
    private String modelo;

    // Constructor vacío
    public Vehiculo() {
    }

    // Constructor con parámetros
    public Vehiculo(Repartidor repartidor, String tipo, String placa, String modelo) {
        this.repartidor = repartidor;
        this.tipo = tipo;
        this.placa = placa;
        this.modelo = modelo;
    }

    // Getters y Setters
    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Repartidor repartidor) {
        this.repartidor = repartidor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
}