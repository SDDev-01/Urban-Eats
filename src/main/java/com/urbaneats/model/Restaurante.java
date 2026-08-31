package com.urbaneats.model;

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
 * Entidad de la tabla `restaurante`.
 */
@Entity
@Table(name = "restaurante")
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoCiudad", nullable = false)
    @ToString.Exclude
    private Ciudad ciudad;

    /*
     * `gerente` le toca a otra persona del equipo.
     * Mientras esa entidad no exista, la FK queda mapeada como columna simple.
     * Cuando Gerente.java este listo, reemplazar por:
     *
     * @ManyToOne(fetch = FetchType.LAZY, optional = false)
     * @JoinColumn(name = "CodigoGerente", nullable = false)
     * private Gerente gerente;
     */
    @Column(name = "CodigoGerente", nullable = false)
    private Integer codigoGerente;

    @Column(name = "Nombre", length = 150)
    private String nombre;

    @Column(name = "Direccion", length = 200)
    private String direccion;

    @Column(name = "Horario", length = 100)
    private String horario;
}
