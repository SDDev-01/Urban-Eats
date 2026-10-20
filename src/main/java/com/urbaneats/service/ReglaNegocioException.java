package com.urbaneats.service;

/**
 * Se lanza cuando una accion rompe una regla del negocio, por ejemplo:
 * aceptar un pedido que ya fue cancelado o pedir platos de dos cocinas a la vez.
 * El mensaje esta escrito para mostrarselo tal cual al usuario.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
