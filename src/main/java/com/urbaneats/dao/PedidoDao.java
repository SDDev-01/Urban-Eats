package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Pedido;


public interface PedidoDao extends JpaRepository<Pedido, Integer> {
}