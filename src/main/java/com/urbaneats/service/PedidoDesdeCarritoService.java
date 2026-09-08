package com.urbaneats.service;

import com.urbaneats.dao.DetallePedidoRepository;
import com.urbaneats.dao.EnvioRepository;
import com.urbaneats.dao.PagoRepository;
import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.dao.PlatoRepository;
import com.urbaneats.dao.RepartidorRepository;
import com.urbaneats.dao.RestauranteRepository;
import com.urbaneats.entity.Cliente;
import com.urbaneats.entity.DetallePedido;
import com.urbaneats.entity.Envio;
import com.urbaneats.entity.Menu;
import com.urbaneats.entity.Pago;
import com.urbaneats.entity.Pedido;
import com.urbaneats.entity.Plato;
import com.urbaneats.entity.Restaurante;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Arma el envio, el pedido y su detalle a partir de los items del carrito.
 * La usan tanto PagoService (tras un pago con MercadoPago) como PedidoController
 * (confirmacion directa, sin pasarela). Vive como bean propio -y no como metodo privado-
 * para que @Transactional funcione: Spring solo intercepta llamadas que pasan por el
 * proxy del bean, nunca una auto-invocacion dentro de la misma clase.
 */
@Service
public class PedidoDesdeCarritoService {

    /** Repartidor por defecto mientras no exista logica de asignacion automatica (igual que en PHP). */
    private static final Integer CODIGO_REPARTIDOR_DEFECTO = 1;

    private final EnvioRepository envioRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PlatoRepository platoRepository;
    private final RepartidorRepository repartidorRepository;
    private final RestauranteRepository restauranteRepository;
    private final PagoRepository pagoRepository;

    public PedidoDesdeCarritoService(EnvioRepository envioRepository,
                                      PedidoRepository pedidoRepository,
                                      DetallePedidoRepository detallePedidoRepository,
                                      PlatoRepository platoRepository,
                                      RepartidorRepository repartidorRepository,
                                      RestauranteRepository restauranteRepository,
                                      PagoRepository pagoRepository) {
        this.envioRepository = envioRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.platoRepository = platoRepository;
        this.repartidorRepository = repartidorRepository;
        this.restauranteRepository = restauranteRepository;
        this.pagoRepository = pagoRepository;
    }

    /**
     * El restaurante viaja en el item si el carrito lo trae; si no, se resuelve
     * a traves del menu del primer plato.
     */
    public Integer resolverCodigoRestaurante(List<Map<String, Object>> items) {
        Map<String, Object> primerItem = items.get(0);
        Object restauranteId = primerItem.get("restaurante_id");
        if (restauranteId != null) {
            return Integer.valueOf(String.valueOf(restauranteId));
        }

        Object codigoPlato = primerItem.get("id");
        if (codigoPlato == null) {
            return null;
        }
        Optional<Plato> plato = platoRepository.findById(Integer.valueOf(String.valueOf(codigoPlato)));
        Menu menu = plato.map(Plato::getMenu).orElse(null);
        return menu != null ? menu.getRestaurante().getCodigoRestaurante() : null;
    }

    /** Todo o nada: si una linea del detalle falla, no debe quedar un envio o pedido huerfano. */
    @Transactional
    public Integer crear(Cliente cliente, Integer codigoRestaurante, List<Map<String, Object>> items, Pago pago) {
        Restaurante restaurante = restauranteRepository.getReferenceById(codigoRestaurante);

        Envio envio = new Envio();
        envio.setCliente(cliente);
        envio.setRepartidor(repartidorRepository.getReferenceById(CODIGO_REPARTIDOR_DEFECTO));
        envio.setRestaurante(restaurante);
        envio.setDescripcion("Pedido Urban Eats");
        envio.setFechaEnvio(LocalDate.now());
        envio.setHoraEntrega(LocalTime.now().plusMinutes(35));
        envio = envioRepository.save(envio);

        Pedido pedido = new Pedido();
        pedido.setEnvio(envio);
        pedido.setRestaurante(restaurante);
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstado("Iniciando");
        pedido = pedidoRepository.save(pedido);

        for (Map<String, Object> item : items) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setPlato(platoRepository.getReferenceById(Integer.valueOf(String.valueOf(item.get("id")))));
            detalle.setCantidad(item.get("cantidad") != null ? Integer.valueOf(String.valueOf(item.get("cantidad"))) : 1);
            detalle.setPrecioUnitario(item.get("precio") != null ? new BigDecimal(String.valueOf(item.get("precio"))) : BigDecimal.ZERO);
            detallePedidoRepository.save(detalle);
        }

        pago.setEnvio(envio);
        pagoRepository.save(pago);

        return pedido.getCodigoPedido();
    }
}
