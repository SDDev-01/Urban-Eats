package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoPago")
    private Integer codigoPago;

    @Column(name = "MetodoPago", nullable = false, length = 50)
    private String metodoPago;

    @Column(name = "Monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "EstadoPago", nullable = false, length = 50)
    private String estadoPago;

    // Cardinalidades
    @ManyToOne
    @JoinColumn(name = "CodigoUsuario")
    private Usuario usuario;

    @OneToOne(mappedBy = "pago", cascade = CascadeType.ALL)
    private Transaccion transaccion;
}