package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gerente")
public class Gerente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoGerente")
    private Integer codigoGerente;

    /** Se excluye de toString/equals/hashCode para no entrar en ciclo con Usuario.gerente. */
    @OneToOne
    @JoinColumn(name = "CodigoUsuario", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;
}
