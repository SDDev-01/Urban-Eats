package com.urbaneats.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urbaneats.entity.Repartidor;

public interface RepartidorRepository extends JpaRepository<Repartidor, Integer> {

    /**
     * Se busca "el primero" y no "el unico" a proposito.
     *
     * La tabla repartidor no tiene restriccion UNIQUE sobre CodigoUsuario, asi que un
     * doble envio del formulario puede dejar dos filas para el mismo usuario. Con una
     * consulta de resultado unico eso hacia estallar el perfil con
     * NonUniqueResultException y la pantalla quedaba inservible.
     */
    Optional<Repartidor> findFirstByUsuario_CodigoUsuarioOrderByCodigoRepartidorAsc(Integer codigoUsuario);

    /** Nombre corto que usan los controladores. */
    default Optional<Repartidor> findByUsuario_CodigoUsuario(Integer codigoUsuario) {
        return findFirstByUsuario_CodigoUsuarioOrderByCodigoRepartidorAsc(codigoUsuario);
    }
}
