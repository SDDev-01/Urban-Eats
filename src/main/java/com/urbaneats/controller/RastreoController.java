package com.urbaneats.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.urbaneats.dao.HistorialPedidoRepository;
import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.entity.Direccion;
import com.urbaneats.entity.Envio;
import com.urbaneats.entity.EstadoPedido;
import com.urbaneats.entity.Pedido;
import com.urbaneats.entity.Repartidor;
import com.urbaneats.entity.Restaurante;
import com.urbaneats.integracion.ServicioPythonCliente;
import com.urbaneats.integracion.ServicioPythonException;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.SesionHelper;
import com.urbaneats.service.PedidoAcceso;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Seguimiento de un pedido.
 *
 * GET /rastreo             lleva al ultimo pedido de la sesion (o a "Mis pedidos")
 * GET /rastreo/{id}        pagina con el mapa y los pasos del pedido
 * GET /rastreo/{id}/datos  JSON que rastreo.js consulta cada pocos segundos
 *
 * Lo pueden ver el cliente que lo pidio, la cocina, el repartidor asignado y el tecnico.
 */
@Controller
@RequestMapping("/rastreo")
public class RastreoController {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PedidoRepository pedidoRepository;
    private final HistorialPedidoRepository historialRepository;
    private final PedidoAcceso pedidoAcceso;
    private final ServicioPythonCliente python;

    public RastreoController(PedidoRepository pedidoRepository,
                             HistorialPedidoRepository historialRepository,
                             PedidoAcceso pedidoAcceso,
                             ServicioPythonCliente python) {
        this.pedidoRepository = pedidoRepository;
        this.historialRepository = historialRepository;
        this.pedidoAcceso = pedidoAcceso;
        this.python = python;
    }

    @GetMapping
    public String ultimoPedido(HttpSession session) {
        Object codigo = session.getAttribute("pedido_activo");
        return codigo instanceof Integer ? "redirect:/rastreo/" + codigo : "redirect:/mis-pedidos";
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String mostrar(@PathVariable Integer id, Model model) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        String vista = pedidoAcceso.vistaDe(pedido, usuario.getCodigoUsuario());
        if (vista == null) {
            return "redirect:/mis-pedidos";
        }
        model.addAttribute("codigoPedido", id);
        model.addAttribute("vista", vista);
        return "rastreo";
    }

    @GetMapping("/{id}/datos")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<?> datos(@PathVariable Integer id) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        String vista = pedidoAcceso.vistaDe(pedido, usuario.getCodigoUsuario());
        if (vista == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Pedido no encontrado"));
        }
        return ResponseEntity.ok(armarDatos(pedido, vista));
    }

    /**
     * Ruta por calles de la cocina al cliente. La calcula el servicio Python con
     * OpenRouteService (la API key queda en el servidor, nunca en el navegador).
     */
    @GetMapping("/{id}/ruta")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<?> ruta(@PathVariable Integer id) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedidoAcceso.vistaDe(pedido, usuario.getCodigoUsuario()) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Pedido no encontrado"));
        }
        try {
            return ResponseEntity.ok(python.ruta(pedido.getEnvio().getCodigoEnvio()));
        } catch (ServicioPythonException e) {
            return ResponseEntity.status(e.getEstadoHttp()).body(Map.of("error", e.getMessage()));
        }
    }

    /** Convierte el pedido en lo que necesita la pantalla de seguimiento. */
    private RastreoDatos armarDatos(Pedido pedido, String vista) {
        Envio envio = pedido.getEnvio();
        Restaurante cocina = pedido.getRestaurante();
        Direccion direccion = envio.getDireccion();
        Repartidor repartidor = envio.getRepartidor();

        List<PedidoRastreoItem> items = pedido.getDetalles().stream()
                .map(detalle -> new PedidoRastreoItem(
                        detalle.getPlato() != null ? detalle.getPlato().getNombre() : "",
                        detalle.getPlato() != null ? detalle.getPlato().getDescripcion() : "",
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()))))
                .toList();
        BigDecimal total = items.stream().map(PedidoRastreoItem::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        RastreoDatos.Punto puntoCocina = new RastreoDatos.Punto(cocina.getNombre(), cocina.getDireccion(),
                cocina.getLatitud(), cocina.getLongitud());
        RastreoDatos.Punto puntoDestino = direccion == null ? null : new RastreoDatos.Punto("Entrega",
                direccion.getDireccion(), direccion.getLatitud(), direccion.getLongitud());

        RastreoDatos.RepartidorInfo infoRepartidor = null;
        if (repartidor != null) {
            String vehiculo = repartidor.getVehiculo() != null
                    ? repartidor.getVehiculo().getTipoVehiculo() + " · " + repartidor.getVehiculo().getPlaca() : "";
            infoRepartidor = new RastreoDatos.RepartidorInfo(
                    repartidor.getUsuario().getNombreCompleto(), vehiculo,
                    envio.getLatitudRepartidor(), envio.getLongitudRepartidor(),
                    envio.getFechaUbicacion() != null ? envio.getFechaUbicacion().format(HORA) : null);
        }

        List<RastreoDatos.Hito> historial = historialRepository
                .findByPedido_CodigoPedidoOrderByFechaAscCodigoHistorialAsc(pedido.getCodigoPedido()).stream()
                .map(fila -> new RastreoDatos.Hito(
                        EstadoPedido.desdeBd(fila.getEstadoNuevo()).getEtiqueta(),
                        fila.getFecha().format(HORA),
                        fila.getComentario()))
                .toList();

        EstadoPedido estado = pedido.getEstado();
        return new RastreoDatos(
                pedido.getCodigoPedido(),
                estado.getValorBd(),
                estado.getEtiqueta(),
                paso(estado),
                pedido.getFechaPedido() != null ? pedido.getFechaPedido().format(FECHA) : "",
                pedido.getFechaHora() != null ? pedido.getFechaHora().format(HORA) : "",
                envio.getHoraEntrega() != null ? envio.getHoraEntrega().format(HORA) : null,
                puntoCocina,
                puntoDestino,
                infoRepartidor,
                items,
                total,
                PedidoAcceso.CLIENTE.equals(vista) && estado == EstadoPedido.SOLICITADO,
                vista,
                historial);
    }

    private int paso(EstadoPedido estado) {
        return switch (estado) {
            case SOLICITADO -> 1;
            case EN_PREPARACION -> 2;
            case LISTO_PARA_ENVIO -> 3;
            case EN_CAMINO -> 4;
            case ENTREGADO -> 5;
            case CANCELADO -> 0;
        };
    }
}
