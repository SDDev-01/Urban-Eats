package com.urbaneats.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Alergenos")
public class Alergeno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alergeno_id")
    private Integer alergenoId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    public Alergeno() {
    }

    public Alergeno(Integer alergenoId, String nombre, String descripcion) {
        this.alergenoId = alergenoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getAlergenoId() {
        return alergenoId;
    }

    public void setAlergenoId(Integer alergenoId) {
        this.alergenoId = alergenoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}