package com.urbaneats.controller;

import java.math.BigDecimal;
import java.util.List;

/**
 * Un pedido tal como lo muestra el panel de la cocina (perfil-restaurante.js).
 * estado es la clave CSS del distintivo: nuevo, preparando, listo, encamino, entregado, rechazado.
 */
public record PedidoDashboardItem(Integer codigo, String cliente, String direccion, String telefono,
                                   List<String> items, BigDecimal total, String estado, String estadoEtiqueta,
                                   String hora, String repartidor) {
}
