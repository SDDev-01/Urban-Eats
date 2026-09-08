package com.urbaneats.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Pedido;


public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByRestaurante_CodigoRestauranteAndFechaPedido(Integer codigoRestaurante, LocalDate fechaPedido);
}