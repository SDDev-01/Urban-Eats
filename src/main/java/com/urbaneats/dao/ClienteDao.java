package com.urbaneats.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.urbaneats.entity.Cliente;


public interface ClienteDao extends JpaRepository<Cliente, Integer > {
    // Aquí Spring Data JPA ya nos provee métodos como save, findAll, findById, delete, etc.
}