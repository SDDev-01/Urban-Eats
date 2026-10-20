package com.urbaneats.controller;

import com.urbaneats.dao.ClienteRepository;
import com.urbaneats.dao.DireccionRepository;
import com.urbaneats.entity.Cliente;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.SesionHelper;
import com.urbaneats.service.PagoService;
import com.urbaneats.service.PedidoDesdeCarritoService;
import com.urbaneats.service.PedidoDesdeCarritoService.CarritoValidado;
import com.urbaneats.service.ReglaNegocioException;
import com.urbaneats.service.ResultadoPago;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de la pasarela de pago.
 *
 * GET  /pago             pagina de pago (direcciones del cliente y llave publica de MercadoPago)
 * POST /pago/resumen     valida el carrito en el servidor y devuelve el total real
 * POST /process_payment  cobra y, si el pago se aprueba, crea el pedido
 */
@Controller
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final PedidoDesdeCarritoService pedidoDesdeCarritoService;
    private final ClienteRepository clienteRepository;
    private final DireccionRepository direccionRepository;

    @Value("${mercadopago.public-key:}")
    private String mpPublicKey;

    @GetMapping("/pago")
    public String mostrarPagina(Model model) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        model.addAttribute("mpPublicKey", mpPublicKey);
        model.addAttribute("pagosModoDemo", pagoService.isPagosModoDemo());
        model.addAttribute("direcciones", direccionRepository.findByUsuario_CodigoUsuario(usuario.getCodigoUsuario()));
        return "pago";
    }

    /** El navegador manda el carrito y el servidor responde con los precios y el total reales. */
    @PostMapping("/pago/resumen")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> resumen(@RequestBody Map<String, Object> cuerpo) {
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) cuerpo.getOrDefault("items", List.of());
            CarritoValidado carrito = pedidoDesdeCarritoService.validarCarrito(items);

            List<Map<String, Object>> lineas = carrito.lineas().stream()
                    .map(linea -> Map.<String, Object>of(
                            "id", linea.plato().getCodigoPlato(),
                            "nombre", linea.plato().getNombre(),
                            "cantidad", linea.cantidad(),
                            "precio", linea.plato().getPrecio(),
                            "subtotal", linea.subtotal()))
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "cocina", carrito.restaurante().getNombre(),
                    "lineas", lineas,
                    "total", carrito.total()));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/process_payment")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> iniciarPago(@RequestBody Map<String, Object> datosPago, HttpSession session) {
        CustomUserDetails usuarioActual = SesionHelper.usuarioActual();
        if (usuarioActual == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Sesión inválida"));
        }

        Cliente cliente = clienteRepository.findByUsuario_CodigoUsuario(usuarioActual.getCodigoUsuario())
                .orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No se encontró tu perfil de cliente."));
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) datosPago.getOrDefault("items", List.of());
        Integer codigoDireccion = aEntero(datosPago.get("codigoDireccion"));
        Map<String, Object> datosSinItems = new HashMap<>(datosPago);
        datosSinItems.remove("items");
        datosSinItems.remove("codigoDireccion");

        Map<String, Object> respuesta = new HashMap<>();
        try {
            ResultadoPago resultado = pagoService.iniciarPago(cliente, usuarioActual.getCodigoUsuario(),
                    datosSinItems, items, codigoDireccion);

            if (resultado.codigoPedido() != null) {
                session.setAttribute("pedido_activo", resultado.codigoPedido());
            }
            respuesta.put("status", resultado.status());
            respuesta.put("id", resultado.id());
            respuesta.put("pedido", resultado.codigoPedido());
            respuesta.put("mensaje_error", resultado.mensajeError());
            return ResponseEntity.ok(respuesta);
        } catch (ReglaNegocioException e) {
            respuesta.put("status", "rejected");
            respuesta.put("mensaje_error", e.getMessage());
            return ResponseEntity.unprocessableEntity().body(respuesta);
        }
    }

    private Integer aEntero(Object valor) {
        try {
            return valor == null ? null : new BigDecimal(String.valueOf(valor)).intValue();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
