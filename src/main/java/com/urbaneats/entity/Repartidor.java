package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "repartidor")
public class Repartidor  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoRepartidor")
    private Integer codigoRepartidor;
    

    @OneToOne
    @JoinColumn(name = "CodigoUsuario")
    private Usuario usuario;

    @OneToOne(mappedBy = "repartidor", cascade = CascadeType.ALL)
    private Vehiculo vehiculo;

    @OneToMany(mappedBy = "repartidor", cascade = CascadeType.ALL)
    private List<Envio> envios;
}