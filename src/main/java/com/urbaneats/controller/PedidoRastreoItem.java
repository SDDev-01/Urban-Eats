package com.urbaneats.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/** Forma que rastreo.js espera para cada item de window.PEDIDO_BD.items. */
public record PedidoRastreoItem(String nombre, String descripcion, Integer cantidad,
                                 @JsonProperty("precio_unitario") BigDecimal precioUnitario,
                                 BigDecimal subtotal) {
}
