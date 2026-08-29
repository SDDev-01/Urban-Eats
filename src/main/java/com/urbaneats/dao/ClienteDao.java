package com.urbaneats.dao;

import com.urbaneats.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteDao extends JpaRepository<Cliente, Long> {
    // Aquí Spring Data JPA ya nos provee métodos como save, findAll, findById, delete, etc.
}