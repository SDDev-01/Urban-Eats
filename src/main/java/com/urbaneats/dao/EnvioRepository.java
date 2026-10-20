package com.urbaneats.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.urbaneats.entity.Envio;
import com.urbaneats.entity.EstadoPedido;


public interface EnvioRepository extends JpaRepository<Envio, Integer> {

    /** Envios de un repartidor cuyo pedido esta en un estado (por ejemplo, "En camino"). */
    List<Envio> findByRepartidor_CodigoRepartidorAndPedido_EstadoOrderByFechaEnvioDesc(
            Integer codigoRepartidor, EstadoPedido estado);

    /** Envios que ningun repartidor ha tomado y cuyo pedido esta en un estado (por ejemplo, "Listo para envio"). */
    List<Envio> findByRepartidorIsNullAndPedido_EstadoOrderByCodigoEnvioAsc(EstadoPedido estado);

    /**
     * Asigna el repartidor solo si el envio sigue libre, en una sola sentencia SQL:
     *   UPDATE envio SET CodigoRepartidor = ? WHERE CodigoEnvio = ? AND CodigoRepartidor IS NULL
     * Devuelve cuantas filas cambio: 1 si lo tomo, 0 si otro repartidor se le adelanto.
     * Asi dos repartidores no pueden tomar el mismo pedido al mismo tiempo.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE envio SET CodigoRepartidor = :codigoRepartidor "
            + "WHERE CodigoEnvio = :codigoEnvio AND CodigoRepartidor IS NULL", nativeQuery = true)
    int asignarRepartidorSiLibre(@Param("codigoEnvio") Integer codigoEnvio,
                                 @Param("codigoRepartidor") Integer codigoRepartidor);
}
