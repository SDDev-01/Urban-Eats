package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbaneats.entity.DetallePedido;


public interface DetallePedidoDao extends JpaRepository<DetallePedido, Integer> {
}