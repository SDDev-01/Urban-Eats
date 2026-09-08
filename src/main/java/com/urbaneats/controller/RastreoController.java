package com.urbaneats.controller;

import java.math.BigDecimal;
import java.util.List;

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
                model.addAttribute("pedidoData", aRastreoData(pedido));
            }
        }

        return "rastreo";
    }

    private PedidoRastreoData aRastreoData(Pedido pedido) {
        List<PedidoRastreoItem> items = pedido.getDetalles().stream()
                .map(detalle -> {
                    // PrecioUnitario es DECIMAL en la base, por eso se multiplica con BigDecimal.
                    BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                    return new PedidoRastreoItem(
                            detalle.getPlato() != null ? detalle.getPlato().getNombre() : "",
                            detalle.getPlato() != null ? detalle.getPlato().getDescripcion() : "",
                            detalle.getCantidad(),
                            detalle.getPrecioUnitario(),
                            subtotal
                    );
                })
                .toList();

        BigDecimal total = items.stream().map(PedidoRastreoItem::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoRastreoData(
                pedido.getCodigoPedido(),
                pedido.getEstado(),
                pedido.getFechaPedido() != null ? pedido.getFechaPedido().toString() : null,
                pedido.getRestaurante() != null ? pedido.getRestaurante().getNombre() : "",
                items,
                total
        );
    }
}
