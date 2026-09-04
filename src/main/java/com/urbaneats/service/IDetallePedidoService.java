package com.urbaneats.service;

import com.urbaneats.entity.DetallePedido;
import java.util.List;
import java.util.Optional;

public interface IDetallePedidoService {
    List<DetallePedido> listarTodos();
    Optional<DetallePedido> buscarPorId(Integer id);
    DetallePedido guardar(DetallePedido detallePedido);
    void eliminar(Integer id);
}