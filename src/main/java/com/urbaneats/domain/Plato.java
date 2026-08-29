package com.urbaneats.domain;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "plato")
public class Plato implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plato")
    private Long idPlato;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column
    private Double precio;

    // Constructor vacío
    public Plato() {
    }

    // Constructor con parámetros
    public Plato(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    // Getters y Setters
    public Long getIdPlato() {
        return idPlato;
    }

    public void setIdPlato(Long idPlato) {
        this.idPlato = idPlato;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}