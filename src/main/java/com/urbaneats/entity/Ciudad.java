package com.urbaneats.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad de la tabla Ciudad.
 * El codigo NO es autoincremental: se inserta manualmente (codigo DANE).
 */
@Entity
@Table(name = "Ciudad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ciudad {

    @Id
    @Column(name = "CodigoCiudad", nullable = false)
    private Integer codigoCiudad;

    @Column(name = "Nombre", length = 255)
    private String nombre;

    @Column(name = "Latitud", precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(name = "Longitud", precision = 10, scale = 8)
    private BigDecimal longitud;

    // ----- Relaciones -----

    /** Muchas ciudades pertenecen a un departamento. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoDepartamento", nullable = false)
    @ToString.Exclude
    private Departamento departamento;
}
