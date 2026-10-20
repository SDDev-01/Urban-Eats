package com.urbaneats.dao;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urbaneats.entity.EstadoPedido;
import com.urbaneats.entity.Pedido;


public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByRestaurante_CodigoRestauranteAndFechaPedido(Integer codigoRestaurante, LocalDate fechaPedido);

    /** El pedido que creo el trigger crear_pedido_automaticamente para ese envio. */
    Optional<Pedido> findByEnvio_CodigoEnvio(Integer codigoEnvio);

    /** Pedidos de una cocina en ciertos estados (por ejemplo, los que siguen activos). */
    List<Pedido> findByRestaurante_CodigoRestauranteAndEstadoInOrderByCodigoPedidoDesc(
            Integer codigoRestaurante, Collection<EstadoPedido> estados);

    /** Pedidos de un cliente, del mas reciente al mas antiguo. */
    List<Pedido> findByEnvio_Cliente_CodigoClienteOrderByCodigoPedidoDesc(Integer codigoCliente);
}
