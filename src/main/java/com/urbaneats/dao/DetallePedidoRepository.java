package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbaneats.entity.DetallePedido;


public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
}