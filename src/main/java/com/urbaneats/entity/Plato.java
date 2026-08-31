package com.urbaneats.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
@Data
@Entity
@Table(name = "plato")
public class Plato implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plato")
    private Integer idPlato;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column
    private Double precio;

    }