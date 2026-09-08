package com.urbaneats.controller;

import com.urbaneats.dao.ClienteRepository;
import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.entity.Cliente;
import com.urbaneats.entity.Pago;
import com.urbaneats.entity.Pedido;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.service.PedidoDesdeCarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Confirmacion de pedido sin pasarela de pago, consulta de estado y cancelacion.
 * Equivale a PedidoController de la rama php-Laravel (rutas bajo /pedido, no /pedidos).
 */
@Controller
public class PedidoController {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoDesdeCarritoService pedidoDesdeCarritoService;

    public PedidoController(ClienteRepository clienteRepository,
                             PedidoRepository pedidoRepository,
                             PedidoDesdeCarritoService pedidoDesdeCarritoService) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.pedidoDesdeCarritoService = pedidoDesdeCarritoService;
    }

    private CustomUserDetails getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    @PostMapping("/pedido/confirmar")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> confirmar(@RequestBody Map<String, Object> payload, HttpSession session) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) payload.getOrDefault("items", List.of());
        if (items.isEmpty()) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", "El carrito está vacío."));
        }

        Cliente cliente = clienteRepository.findByUsuario_CodigoUsuario(getUsuarioActual().getCodigoUsuario())
                .orElse(null);
        if (cliente == null) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", "No se encontró el perfil de cliente."));
        }

        Integer codigoRestaurante = pedidoDesdeCarritoService.resolverCodigoRestaurante(items);
        if (codigoRestaurante == null) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", "No se pudo determinar el restaurante."));
        }

        BigDecimal total = items.stream()
                .map(item -> {
                    BigDecimal precio = item.get("precio") != null
                            ? new BigDecimal(String.valueOf(item.get("precio"))) : BigDecimal.ZERO;
                    int cantidad = item.get("cantidad") != null ? Integer.parseInt(String.valueOf(item.get("cantidad"))) : 1;
                    return precio.multiply(BigDecimal.valueOf(cantidad));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try {
            Pago pago = new Pago();
            pago.setCliente(cliente);
            pago.setMonto(total);
            pago.setFechaPago(LocalDate.now());
            pago.setHoraPago(LocalTime.now());
            pago.setEstadoPago("Aceptado");

            Integer codigoPedido = pedidoDesdeCarritoService.crear(cliente, codigoRestaurante, items, pago);
            session.setAttribute("pedido_activo", codigoPedido);

            return ResponseEntity.ok(Map.of("redirect", "/rastreo"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al procesar el pedido. Inténtalo nuevamente."));
        }
    }

    @GetMapping("/pedido/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> estado(@PathVariable Integer id) {
        return pedidoRepository.findById(id)
                .map(pedido -> ResponseEntity.ok(Map.of("estado", pedido.getEstado())))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("estado", "")));
    }

    @PostMapping("/pedido/{id}/cancelar")
    @ResponseBody
    public ResponseEntity<?> cancelar(@PathVariable Integer id) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Pedido no encontrado"));
        }
        if (!"Iniciando".equals(pedido.getEstado())) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", "El pedido no puede cancelarse en su estado actual"));
        }

        pedido.setEstado("Cancelado");
        pedidoRepository.save(pedido);

        return ResponseEntity.ok(Map.of("ok", true));
    }
}
