package com.urbaneats.service;

import com.urbaneats.entity.Cliente;
import com.urbaneats.entity.Pago;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrato de la capa de servicio para Pago.
 * Aqui solo se declara QUE se puede hacer; el COMO vive en PagoService.
 */
public interface IPagoService {

    List<Pago> listarTodos();

    Optional<Pago> buscarPorId(Integer id);

    Pago guardar(Pago pago);

    Pago actualizar(Integer id, Pago pago);

    void eliminar(Integer id);

    /**
     * Valida el carrito en el servidor, cobra (MercadoPago o modo demo) y, SOLO si
     * el pago queda aprobado, arma el envio y el pedido. Regla: pago antes del pedido.
     *
     * @param cliente         el cliente logueado que esta pagando
     * @param codigoUsuario   su usuario (para el historial del pedido)
     * @param datosPago       los datos que entrega el Payment Brick (token, payment_method_id, payer, etc.)
     * @param items           el carrito: cada item trae id (CodigoPlato) y cantidad
     * @param codigoDireccion la direccion de entrega elegida (puede venir null: se usa la primera)
     */
    ResultadoPago iniciarPago(Cliente cliente, Integer codigoUsuario, Map<String, Object> datosPago,
                              List<Map<String, Object>> items, Integer codigoDireccion);
}
