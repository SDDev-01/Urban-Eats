package com.urbaneats.dao;

import com.urbaneats.domain.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepartidorDao extends JpaRepository<Repartidor, Long> {
}