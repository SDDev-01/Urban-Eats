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
@Table(name = "Transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaccion_id")
    private String transaccionId;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime fechaTransaccion;

    @Column(name = "resultado", nullable = false, length = 50)
    private String resultado;
        
    //Cardinalidad
    @OneToOne
    @JoinColumn(name = "pago_id", nullable = false, unique = true)
    private Pago pago;

}