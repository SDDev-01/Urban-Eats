package com.urbaneats.service;

/**
 * Respuesta que el frontend del Payment Brick espera tras iniciar un pago.
 * codigoPedido viaja para que el controller lo guarde en sesion como "pedido_activo".
 */
public record ResultadoPago(String status, Long id, String mensajeError, Integer codigoPedido) {
}
