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
     * Crea el registro de pago, lo procesa contra MercadoPago y, si el pago
     * fue exitoso (o MercadoPago no respondio), arma el envio/pedido con los items del carrito.
     *
     * @param cliente   el cliente logueado que esta pagando
     * @param datosPago los datos que entrega el Payment Brick (token, payment_method_id, transaction_amount, payer, etc.)
     * @param items     el carrito: cada item trae id (CodigoPlato), cantidad, precio y opcionalmente restaurante_id
     */
    ResultadoPago iniciarPago(Cliente cliente, Map<String, Object> datosPago, List<Map<String, Object>> items);
}
