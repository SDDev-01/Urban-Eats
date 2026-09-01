package com.urbaneats.entity;

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
 * Entidad de la tabla Restaurante.
 */
@Entity
@Table(name = "Restaurante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoRestaurante", nullable = false)
    private Integer codigoRestaurante;

    @Column(name = "Nombre", length = 150)
    private String nombre;

    @Column(name = "Direccion", length = 200)
    private String direccion;

    @Column(name = "Horario", length = 100)
    private String horario;

    // ----- Relaciones -----

    /** Muchos restaurantes estan en una ciudad. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoCiudad", nullable = false)
    @ToString.Exclude
    private Ciudad ciudad;

    /** Muchos restaurantes son administrados por un gerente. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoGerente", nullable = false)
    @ToString.Exclude
    private Gerente gerente;
}
