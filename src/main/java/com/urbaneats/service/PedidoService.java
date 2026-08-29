package com.urbaneats.service;

import com.urbaneats.domain.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoService {
    List<Pedido> listarPedidos();
    Pedido guardarPedido(Pedido pedido);
    Optional<Pedido> obtenerPedidoPorId(Long id);
    void eliminarPedido(Long id);
}