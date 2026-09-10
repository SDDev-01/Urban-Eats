package com.urbaneats.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbaneats.entity.Gerente;

public interface GerenteRepository extends JpaRepository<Gerente, Integer> {

    /**
     * Se busca "el primero" y no "el unico" a proposito: la tabla gerente no tiene
     * restriccion UNIQUE sobre CodigoUsuario, y un doble envio del formulario puede
     * dejar dos filas para el mismo usuario. Con resultado unico, el perfil del
     * restaurante estallaba con NonUniqueResultException.
     */
    Optional<Gerente> findFirstByUsuario_CodigoUsuarioOrderByCodigoGerenteAsc(Integer codigoUsuario);

    /** Nombre corto que usan los controladores. */
    default Optional<Gerente> findByUsuario_CodigoUsuario(Integer codigoUsuario) {
        return findFirstByUsuario_CodigoUsuarioOrderByCodigoGerenteAsc(codigoUsuario);
    }
}
