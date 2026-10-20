package com.urbaneats.service;

import com.urbaneats.dao.DetallePedidoRepository;
import com.urbaneats.dao.EnvioRepository;
import com.urbaneats.dao.PagoRepository;
import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.dao.PlatoRepository;
import com.urbaneats.entity.Cliente;
import com.urbaneats.entity.DetallePedido;
import com.urbaneats.entity.Direccion;
import com.urbaneats.entity.Envio;
import com.urbaneats.entity.EstadoPedido;
import com.urbaneats.entity.Pago;
import com.urbaneats.entity.Pedido;
import com.urbaneats.entity.Plato;
import com.urbaneats.entity.Restaurante;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convierte el carrito del navegador en un pedido real.
 *
 * Paso 1 - validarCarrito(): no se confia en lo que manda el navegador. Se busca
 *   cada plato en la base, se usa SU precio, se exige que este disponible y que
 *   todos sean de la misma cocina, y se calcula el total en el servidor.
 * Paso 2 - crear(): con el pago ya aprobado, guarda el envio. Al insertarlo, el
 *   trigger crear_pedido_automaticamente de la base crea el pedido en estado
 *   "Solicitado"; aqui solo se busca ese pedido y se le agrega el detalle.
 *   (Antes Java insertaba un segundo pedido para el mismo envio y la base lo
 *   rechazaba por la llave UNIQUE: por eso ningun pedido llegaba a guardarse.)
 */
@Service
public class PedidoDesdeCarritoService {

    /** Minutos estimados de preparacion + entrega, hasta que el mapa calcule la ruta real. */
    private static final int MINUTOS_ESTIMADOS = 35;
    private static final int CANTIDAD_MAXIMA_POR_PLATO = 20;

    /** Una linea del carrito ya validada: el plato real de la base y la cantidad pedida. */
    public record LineaCarrito(Plato plato, int cantidad) {
        public BigDecimal subtotal() {
            return plato.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        }
    }

    /** El carrito validado: una sola cocina, platos disponibles y el total calculado aqui. */
    public record CarritoValidado(Restaurante restaurante, List<LineaCarrito> lineas, BigDecimal total) {
    }

    private final EnvioRepository envioRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PlatoRepository platoRepository;
    private final PagoRepository pagoRepository;
    private final PedidoEstadoService pedidoEstadoService;
    private final BigDecimal montoMinimo;
    private final boolean modoDemo;

    public PedidoDesdeCarritoService(EnvioRepository envioRepository,
                                     PedidoRepository pedidoRepository,
                                     DetallePedidoRepository detallePedidoRepository,
                                     PlatoRepository platoRepository,
                                     PagoRepository pagoRepository,
                                     PedidoEstadoService pedidoEstadoService,
                                     @Value("${urbaneats.pedido.monto-minimo:10000}") BigDecimal montoMinimo,
                                     @Value("${urbaneats.modo-demo:false}") boolean modoDemo) {
        this.envioRepository = envioRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.platoRepository = platoRepository;
        this.pagoRepository = pagoRepository;
        this.pedidoEstadoService = pedidoEstadoService;
        this.montoMinimo = montoMinimo;
        this.modoDemo = modoDemo;
    }

    /**
     * Revisa el carrito contra la base. Lanza ReglaNegocioException con un mensaje
     * para el usuario si algo no cuadra.
     *
     * @param items lo que manda el navegador: cada item trae "id" (CodigoPlato) y "cantidad"
     */
    @Transactional(readOnly = true)
    public CarritoValidado validarCarrito(List<Map<String, Object>> items) {
        if (items == null || items.isEmpty()) {
            throw new ReglaNegocioException("Tu carrito está vacío.");
        }

        List<LineaCarrito> lineas = new ArrayList<>();
        Restaurante cocina = null;

        for (Map<String, Object> item : items) {
            Integer codigoPlato = aEntero(item.get("id"));
            Integer cantidad = aEntero(item.get("cantidad"));
            if (codigoPlato == null) {
                throw new ReglaNegocioException("Hay un producto inválido en tu carrito.");
            }
            if (cantidad == null || cantidad < 1 || cantidad > CANTIDAD_MAXIMA_POR_PLATO) {
                throw new ReglaNegocioException("La cantidad de cada plato debe estar entre 1 y "
                        + CANTIDAD_MAXIMA_POR_PLATO + ".");
            }

            Plato plato = platoRepository.findById(codigoPlato)
                    .orElseThrow(() -> new ReglaNegocioException("Uno de los platos ya no existe. Revisa tu carrito."));
            if (!plato.isDisponible()) {
                throw new ReglaNegocioException("\"" + plato.getNombre() + "\" no está disponible en este momento.");
            }
            if (plato.getPrecio() == null || plato.getMenu() == null || plato.getMenu().getRestaurante() == null) {
                throw new ReglaNegocioException("\"" + plato.getNombre() + "\" no tiene precio o cocina asignada.");
            }

            Restaurante cocinaDelPlato = plato.getMenu().getRestaurante();
            if (cocina == null) {
                cocina = cocinaDelPlato;
            } else if (!cocina.getCodigoRestaurante().equals(cocinaDelPlato.getCodigoRestaurante())) {
                throw new ReglaNegocioException("Tu carrito tiene platos de varias cocinas. "
                        + "Haz un pedido por cocina para que llegue caliente.");
            }
            lineas.add(new LineaCarrito(plato, cantidad));
        }

        if (!cocina.isActivo() || !cocina.isAceptaPedidos()) {
            throw new ReglaNegocioException(cocina.getNombre() + " no está recibiendo pedidos en este momento.");
        }
        // En modo demostracion se permite pedir fuera del horario, para poder exponer de dia.
        if (!modoDemo && !cocina.abiertoA(LocalTime.now())) {
            throw new ReglaNegocioException(cocina.getNombre() + " está cerrada ahora. Horario: " + cocina.getHorario());
        }

        BigDecimal total = lineas.stream().map(LineaCarrito::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(montoMinimo) < 0) {
            throw new ReglaNegocioException("El pedido mínimo es de $" + String.format("%,.0f", montoMinimo).replace(',', '.') + ".");
        }
        return new CarritoValidado(cocina, lineas, total);
    }

    /**
     * Arma envio + pedido + detalle en una sola transaccion (todo o nada).
     * Solo se llama con el pago ya aprobado: "pago antes del pedido".
     */
    @Transactional
    public Pedido crear(Cliente cliente, CarritoValidado carrito, Direccion direccion, Pago pago, Integer codigoUsuario) {
        Envio envio = new Envio();
        envio.setCliente(cliente);
        envio.setRepartidor(null); // queda libre hasta que un repartidor lo tome
        envio.setRestaurante(carrito.restaurante());
        envio.setDireccion(direccion);
        envio.setDescripcion(describir(carrito.lineas()));
        envio.setFechaEnvio(LocalDate.now());
        envio.setHoraEntrega(LocalTime.now().plusMinutes(MINUTOS_ESTIMADOS));
        envio = envioRepository.saveAndFlush(envio); // este INSERT dispara el trigger que crea el pedido

        Pedido pedido = pedidoRepository.findByEnvio_CodigoEnvio(envio.getCodigoEnvio())
                .orElseThrow(() -> new IllegalStateException(
                        "La base no creo el pedido: revise el trigger crear_pedido_automaticamente."));

        for (LineaCarrito linea : carrito.lineas()) {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setPlato(linea.plato());
            detalle.setCantidad(linea.cantidad());
            detalle.setPrecioUnitario(linea.plato().getPrecio()); // precio de la base, no del navegador
            detallePedidoRepository.save(detalle);
        }

        pago.setEnvio(envio);
        pagoRepository.save(pago);

        pedidoEstadoService.registrarHistorial(pedido, null, EstadoPedido.SOLICITADO, codigoUsuario,
                "Pedido creado con pago aprobado");
        return pedido;
    }

    /** Texto corto del envio, por ejemplo "2× Caldo de costilla, 1× Tinto". */
    private String describir(List<LineaCarrito> lineas) {
        String texto = lineas.stream()
                .map(linea -> linea.cantidad() + "× " + linea.plato().getNombre())
                .collect(Collectors.joining(", "));
        return texto.length() <= 300 ? texto : texto.substring(0, 297) + "...";
    }

    private Integer aEntero(Object valor) {
        if (valor == null) {
            return null;
        }
        try {
            return (int) Double.parseDouble(String.valueOf(valor));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
