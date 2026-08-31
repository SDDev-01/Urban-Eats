package com.urbaneats.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
@Data
@Entity
@Table(name = "vehiculo")
public class Vehiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Integer idVehiculo;

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

}