package com.urbaneats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad de la tabla Alergeno: una fila por cada alergeno de un plato.
 * Antes mapeaba una columna Descripcion que la tabla no tiene.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alergeno")
public class Alergeno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoAlergeno")
    private Integer alergenoId;

    @Column(name = "Nombre", length = 50)
    private String nombre;

    // Cardinalidad
    @ManyToOne
    @JoinColumn(name = "CodigoPlato")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Plato plato;
}
