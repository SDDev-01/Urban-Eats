package com.urbaneats.controller;

import com.urbaneats.entity.Pedido;
import com.urbaneats.service.IPedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rastreo")
public class RastreoController {

    private final IPedidoService pedidoService;

    public RastreoController(IPedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String mostrar(HttpSession session, Model model) {
        Integer codigoPedido = (Integer) session.getAttribute("pedido_activo");

        if (codigoPedido != null) {
            Pedido pedido = pedidoService.buscarPorId(codigoPedido).orElse(null);

            if (pedido != null) {
                double total = pedido.getDetalles().stream()
                        .mapToDouble(detalle -> detalle.getCantidad() * detalle.getPrecioUnitario())
                        .sum();

                model.addAttribute("pedidoData", pedido);
                model.addAttribute("totalPedido", total);
            }
        }

        return "rastreo";
    }
}