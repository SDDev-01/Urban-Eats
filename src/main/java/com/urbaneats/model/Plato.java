package com.urbaneats.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
 * Entidad de la tabla `plato`.
 * OJO: en el Schema.sql `CodigoMenu` admite NULL, por eso la relacion es opcional.
 */
@Entity
@Table(name = "plato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoPlato", nullable = false)
    private Integer codigoPlato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoMenu")
    @ToString.Exclude
    private Menu menu;

    @Column(name = "Nombre", length = 150)
    private String nombre;

    @Column(name = "Descripcion", length = 300)
    private String descripcion;

    @Column(name = "Precio", precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "TipoComida", length = 100)
    private String tipoComida;

    @Column(name = "Disponibilidad", length = 50)
    private String disponibilidad;
}
