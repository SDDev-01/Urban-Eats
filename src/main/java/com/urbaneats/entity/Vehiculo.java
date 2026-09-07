package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vehiculo")
public class Vehiculo  {

    // Placa ahora es la llave primaria, sin @GeneratedValue porque es un String manual
    @Id
    @Column(name = "Placa", length = 20)
    private String placa;

    @Column(name = "TipoVehiculo", nullable = false)
    private String tipoVehiculo; 

    @Column(name = "SeguroVehiculo", length = 100)
    private String seguroVehiculo;

    @Column(name = "SOAT", length = 100)
    private String soat;

    @OneToOne
    @JoinColumn(name = "CodigoRepartidor") // Coincide con tu script SQL
    private Repartidor repartidor;

}