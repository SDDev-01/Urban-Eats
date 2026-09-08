package com.urbaneats.controller;

import java.math.BigDecimal;
import java.util.List;

/** Forma que perfil-restaurante.js espera para cada fila de window.PEDIDOS_BD. */
public record PedidoDashboardItem(String id, String cliente, String direccion, String telefono,
                                   List<String> items, BigDecimal total, String estado, String hora) {
}
