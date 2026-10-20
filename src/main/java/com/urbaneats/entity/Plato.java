package com.urbaneats.entity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

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
 * Entidad de la tabla Plato.
 * En el Schema.sql CodigoMenu admite NULL, por eso la relacion es opcional.
 */
@Entity
@Table(name = "plato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Plato {

    /** El unico valor de Disponibilidad que permite agregar el plato al carrito. */
    public static final String DISPONIBLE = "Disponible";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoPlato", nullable = false)
    private Integer codigoPlato;

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

    /** Calorias aproximadas: informacion orientativa, no es consejo medico. */
    @Column(name = "Calorias")
    private Integer calorias;

    /** Etiquetas separadas por comas: "Balanceado,Alto en proteína". */
    @Column(name = "Etiquetas", length = 200)
    private String etiquetas;

    // ----- Relaciones -----

    /** Muchos platos pertenecen a un menu. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoMenu")
    @ToString.Exclude
    private Menu menu;

    /** Las etiquetas como lista, sin espacios sobrantes ni vacias. */
    public List<String> getListaEtiquetas() {
        if (etiquetas == null || etiquetas.isBlank()) {
            return List.of();
        }
        return Arrays.stream(etiquetas.split(","))
                .map(String::trim)
                .filter(etiqueta -> !etiqueta.isEmpty())
                .toList();
    }

    /** True si el plato tiene el sello "Balanceado". */
    public boolean isBalanceado() {
        return getListaEtiquetas().stream().anyMatch(etiqueta -> etiqueta.equalsIgnoreCase("Balanceado"));
    }

    /** True si el plato se puede pedir ahora. */
    public boolean isDisponible() {
        return DISPONIBLE.equalsIgnoreCase(disponibilidad);
    }
}
