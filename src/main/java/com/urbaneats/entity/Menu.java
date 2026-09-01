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
 * Entidad de la tabla Menu.
 */
@Entity
@Table(name = "Menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoMenu", nullable = false)
    private Integer codigoMenu;

    @Column(name = "Categoria", length = 100)
    private String categoria;

    // ----- Relaciones -----

    /** Muchos menus pertenecen a un restaurante. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoRestaurante", nullable = false)
    @ToString.Exclude
    private Restaurante restaurante;
}
