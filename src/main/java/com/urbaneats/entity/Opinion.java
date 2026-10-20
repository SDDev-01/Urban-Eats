package com.urbaneats.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad de la tabla Opinion.
 * Antes mapeaba columnas que no existian (calificacion, comentario, usuario_id);
 * ahora sigue las columnas reales del Schema.sql mas las que agrego la fase 6.
 * Regla de negocio: solo se opina sobre un pedido Entregado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "opinion")
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoOpinion")
    private Integer codigoOpinion;

    @Column(name = "Opinion", length = 300)
    private String opinion;

    @Column(name = "Fecha")
    private LocalDate fecha;

    /** De 1 a 5 estrellas (columna agregada en la fase 6). */
    @Column(name = "Calificacion")
    private Integer calificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoPlato")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Plato plato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoCliente")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoRepartidor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Repartidor repartidor;

    /** El pedido calificado (columna agregada en la fase 6). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodigoPedido")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pedido pedido;
}
