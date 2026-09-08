package com.urbaneats.controller;

import java.math.BigDecimal;
import java.util.List;

/** Forma que rastreo.js espera en window.PEDIDO_BD. */
public record PedidoRastreoData(Integer codigo, String estado, String fecha, String restaurante,
                                 List<PedidoRastreoItem> items, BigDecimal total) {
}
