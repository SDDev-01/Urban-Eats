package com.urbaneats.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbaneats.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Se busca "el primero" y no "el unico" a proposito: la tabla cliente no tiene
     * restriccion UNIQUE sobre CodigoUsuario. Si el trigger crear_cliente_automaticamente
     * llegara a dejar dos filas para un usuario, con resultado unico la consulta
     * estallaria con NonUniqueResultException.
     */
    Optional<Cliente> findFirstByUsuario_CodigoUsuarioOrderByCodigoClienteAsc(Integer codigoUsuario);

    /** Nombre corto que usan los servicios y controladores. */
    default Optional<Cliente> findByUsuario_CodigoUsuario(Integer codigoUsuario) {
        return findFirstByUsuario_CodigoUsuarioOrderByCodigoClienteAsc(codigoUsuario);
    }
}
