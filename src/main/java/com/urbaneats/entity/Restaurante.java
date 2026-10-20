package com.urbaneats.entity;

import java.math.BigDecimal;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad de la tabla Restaurante (en la interfaz se le dice "cocina").
 */
@Entity
@Table(name = "restaurante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CodigoRestaurante", nullable = false)
    private Integer codigoRestaurante;

    @Column(name = "Nombre", length = 150)
    private String nombre;

    @Column(name = "Direccion", length = 200)
    private String direccion;

    /** Horario en texto para mostrar, por ejemplo "9:00 p. m. – 6:00 a. m.". */
    @Column(name = "Horario", length = 100)
    private String horario;

    @Column(name = "Latitud", precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(name = "Longitud", precision = 10, scale = 7)
    private BigDecimal longitud;

    /** Si HoraCierre es menor que HoraApertura, la cocina cierra al dia siguiente. */
    @Column(name = "HoraApertura")
    private LocalTime horaApertura;

    @Column(name = "HoraCierre")
    private LocalTime horaCierre;

    @Column(name = "Categoria", length = 60)
    private String categoria;

    /** Ruta de la imagen dentro de /static, por ejemplo /images/restaurantes/las-brasas.png. */
    @Column(name = "Imagen", length = 200)
    private String imagen;

    /** El gerente la apaga cuando quiere pausar los pedidos (boton Abierto/Cerrado). */
    @Column(name = "AceptaPedidos", nullable = false)
    private boolean aceptaPedidos = true;

    /** Falso si la cocina fue suspendida (por ejemplo, por documentos vencidos). */
    @Column(name = "Activo", nullable = false)
    private boolean activo = true;

    // ----- Relaciones -----

    /** Muchos restaurantes estan en una ciudad. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoCiudad", nullable = false)
    @ToString.Exclude
    private Ciudad ciudad;

    /** Muchos restaurantes son administrados por un gerente. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CodigoGerente", nullable = false)
    @ToString.Exclude
    private Gerente gerente;

    /**
     * Dice si la cocina esta dentro de su horario a una hora dada.
     * Ejemplo nocturno: abre 21:00 y cierra 06:00 -> esta abierta a las 23:00 y a las 02:00.
     * Si no tiene horario registrado, se considera abierta.
     */
    public boolean abiertoA(LocalTime hora) {
        if (horaApertura == null || horaCierre == null) {
            return true;
        }
        if (horaApertura.isBefore(horaCierre)) {
            return !hora.isBefore(horaApertura) && hora.isBefore(horaCierre);
        }
        // Cruza la medianoche: abierto desde la apertura hasta las 23:59 y desde las 00:00 hasta el cierre.
        return !hora.isBefore(horaApertura) || hora.isBefore(horaCierre);
    }

    /** True si la cocina tiene su punto en el mapa. */
    public boolean tieneCoordenadas() {
        return latitud != null && longitud != null;
    }
}
