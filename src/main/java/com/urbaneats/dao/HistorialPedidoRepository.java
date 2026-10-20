package com.urbaneats.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbaneats.entity.HistorialPedido;

public interface HistorialPedidoRepository extends JpaRepository<HistorialPedido, Integer> {

    /** La linea de tiempo de un pedido, en el orden en que ocurrio. */
    List<HistorialPedido> findByPedido_CodigoPedidoOrderByFechaAscCodigoHistorialAsc(Integer codigoPedido);
}
