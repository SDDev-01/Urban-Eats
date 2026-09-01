package com.urbaneats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad de la tabla `departamento`.
 * El codigo NO es autoincremental: se inserta manualmente (codigo DANE).
 */
@Entity
@Table(name = "departamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Departamento {

    @Id
    @Column(name = "CodigoDepartamento", nullable = false)
    private Integer codigoDepartamento;

    @Column(name = "Nombre", length = 255)
    private String nombre;
}
