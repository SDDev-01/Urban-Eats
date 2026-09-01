package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.urbaneats.entity.Repartidor;


public interface RepartidorDao extends JpaRepository<Repartidor, Integer> {
}