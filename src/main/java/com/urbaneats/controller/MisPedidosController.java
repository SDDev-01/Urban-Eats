package com.urbaneats.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.urbaneats.dao.ClienteRepository;
import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.entity.Pedido;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.SesionHelper;

/**
 * "Mis pedidos": el historial del cliente, del mas reciente al mas antiguo,
 * con un enlace al seguimiento de cada uno.
 */
@Controller
public class MisPedidosController {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

    /** Una fila de la tabla de "Mis pedidos". */
    public record FilaPedido(Integer codigo, String cocina, String fecha, String estado, String estadoCss,
                             String resumen, BigDecimal total, boolean activo) {
    }

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public MisPedidosController(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/mis-pedidos")
    @Transactional(readOnly = true)
    public String listar(Model model) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        List<FilaPedido> filas = clienteRepository.findByUsuario_CodigoUsuario(usuario.getCodigoUsuario())
                .map(cliente -> pedidoRepository.findByEnvio_Cliente_CodigoClienteOrderByCodigoPedidoDesc(
                        cliente.getCodigoCliente()))
                .orElse(List.of())
                .stream()
                .map(this::aFila)
                .toList();
        model.addAttribute("pedidos", filas);
        return "misPedidos";
    }

    private FilaPedido aFila(Pedido pedido) {
        BigDecimal total = pedido.getDetalles().stream()
                .map(detalle -> detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String fecha = pedido.getFechaHora() != null ? pedido.getFechaHora().format(FECHA_HORA)
                : (pedido.getFechaPedido() != null ? pedido.getFechaPedido().toString() : "");
        return new FilaPedido(
                pedido.getCodigoPedido(),
                pedido.getRestaurante().getNombre(),
                fecha,
                pedido.getEstado().getEtiqueta(),
                claseCss(pedido),
                pedido.getEnvio().getDescripcion(),
                total,
                !pedido.getEstado().esFinal());
    }

    /** Clase CSS del distintivo de estado (la usa tambien el panel de la cocina). */
    private String claseCss(Pedido pedido) {
        return switch (pedido.getEstado()) {
            case SOLICITADO -> "nuevo";
            case EN_PREPARACION -> "preparando";
            case LISTO_PARA_ENVIO -> "listo";
            case EN_CAMINO -> "encamino";
            case ENTREGADO -> "entregado";
            case CANCELADO -> "rechazado";
        };
    }
}
