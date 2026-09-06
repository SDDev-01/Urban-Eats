package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.Pedido;


public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
}