package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "plato")
public class Plato {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plato")
    private Integer idPlato;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column
    private Double precio;

    }