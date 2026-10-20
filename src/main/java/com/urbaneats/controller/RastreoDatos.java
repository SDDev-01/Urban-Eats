package com.urbaneats.controller;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lo que rastreo.js recibe en /rastreo/{id}/datos (JSON).
 * Se arma en RastreoController a partir del pedido, su envio y su historial.
 *
 * paso: 1 Solicitado, 2 En preparacion, 3 Listo para envio, 4 En camino, 5 Entregado, 0 Cancelado.
 * vista: como lo ve quien consulta ("cliente", "cocina", "repartidor" o "tecnico").
 */
public record RastreoDatos(Integer codigo,
                           String estado,
                           String estadoEtiqueta,
                           int paso,
                           String fecha,
                           String hora,
                           String horaEstimada,
                           Punto cocina,
                           Punto destino,
                           RepartidorInfo repartidor,
                           List<PedidoRastreoItem> items,
                           BigDecimal total,
                           boolean puedeCancelar,
                           String vista,
                           List<Hito> historial) {

    /** Un punto del mapa: la cocina o la direccion de entrega. */
    public record Punto(String nombre, String direccion, BigDecimal lat, BigDecimal lng) {
    }

    /** El repartidor y su ultima posicion conocida (null si aun no la ha compartido). */
    public record RepartidorInfo(String nombre, String vehiculo, BigDecimal lat, BigDecimal lng, String actualizado) {
    }

    /** Una fila del historial del pedido. */
    public record Hito(String estado, String hora, String comentario) {
    }
}
