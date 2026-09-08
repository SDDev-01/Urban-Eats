package com.urbaneats.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entidad de la tabla Cliente.
 * En el Schema.sql la tabla solo tiene CodigoCliente y CodigoUsuario:
 * los datos personales (nombres, correo, telefono) viven en Usuario.
 */
@Data
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoCliente", nullable = false)
    private Integer codigoCliente;

    // ----- Relaciones -----

    /** Un cliente corresponde a un usuario del sistema. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoUsuario", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    /** Los envios que se hicieron a este cliente. */
    @OneToMany(mappedBy = "cliente")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Envio> envios;
}
