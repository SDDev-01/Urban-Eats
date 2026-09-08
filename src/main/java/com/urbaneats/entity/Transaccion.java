package com.urbaneats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaccion")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransaccionID")
    private String transaccionID;

    @Column(name = "FechaTransaccion", nullable = false)
    private LocalDateTime fechaTransaccion;

    @Column(name = "Resultado", nullable = false, length = 50)
    private String resultado;
        
    //Cardinalidad
    @OneToOne
    @JoinColumn(name = "CodigoPago", nullable = false, unique = true)
    private Pago pago;

}