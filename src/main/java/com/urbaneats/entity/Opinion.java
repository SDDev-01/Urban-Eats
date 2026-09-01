package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Opiniones")
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opinion_id")
    private Integer opinionId;

    @Column(name = "calificacion", nullable = false)
    private Integer calificacion;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}