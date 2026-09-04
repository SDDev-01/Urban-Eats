package com.urbaneats.service;

import com.urbaneats.entity.Pedido;
import java.util.List;
import java.util.Optional;

public interface IPedidoService {
    List<Pedido> listarTodos();
    Optional<Pedido> buscarPorId(Integer id);
    Pedido guardar(Pedido pedido);
    Pedido actualizar(Integer id, Pedido pedido);
    void eliminar(Integer id);
}