package com.urbaneats.controller;

import com.urbaneats.dao.ClienteRepository;
import com.urbaneats.entity.Cliente;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.service.IPagoService;
import com.urbaneats.service.ResultadoPago;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de la pasarela de pago.
 * Equivale a PagoController::mostrarPagina y PagoController::iniciarPago de la rama php-Laravel.
 */
@Controller
@RequiredArgsConstructor
public class PagoController {

    private final IPagoService pagoService;
    private final ClienteRepository clienteRepository;

    @Value("${mercadopago.public-key}")
    private String mpPublicKey;

    /** Devuelve el usuario que inicio sesion, o null si no hay sesion. */
    private CustomUserDetails getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    @GetMapping("/pago")
    public String mostrarPagina(Model model) {
        model.addAttribute("mpPublicKey", mpPublicKey);
        return "pago";
    }

    @PostMapping("/process_payment")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> iniciarPago(@RequestBody Map<String, Object> datosPago, HttpSession session) {
        CustomUserDetails usuarioActual = getUsuarioActual();
        if (usuarioActual == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Sesión inválida"));
        }

        Cliente cliente = clienteRepository.findByUsuario_CodigoUsuario(usuarioActual.getCodigoUsuario())
                .orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Sesión inválida"));
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) datosPago.getOrDefault("items", List.of());
        Map<String, Object> datosSinItems = new HashMap<>(datosPago);
        datosSinItems.remove("items");

        ResultadoPago resultado = pagoService.iniciarPago(cliente, datosSinItems, items);

        if (resultado.codigoPedido() != null) {
            session.setAttribute("pedido_activo", resultado.codigoPedido());
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("status", resultado.status());
        respuesta.put("id", resultado.id());
        respuesta.put("mensaje_error", resultado.mensajeError());
        return ResponseEntity.ok(respuesta);
    }
}
