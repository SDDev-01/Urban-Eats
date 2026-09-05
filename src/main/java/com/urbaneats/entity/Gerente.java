package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Gerente")
public class Gerente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoGerente")
    private Integer codigoGerente;

    @Column(name = "departamento", nullable = false, length = 100)
    private String departamento;


    //cardinalidad
    @OneToOne
    @JoinColumn(name = "CodigoUsuario", nullable = false, unique = true)
    private Usuario usuario;
}
