package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "repartidor")
public class Repartidor  {

  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_repartidor")
    private Integer idRepartidor;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, length = 50)
    private String estado; // Ej: Disponible, Ocupado, Inactivo

}