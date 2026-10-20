package com.urbaneats.controller;

import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.entity.Pedido;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.SesionHelper;
import com.urbaneats.service.PedidoAcceso;
import com.urbaneats.service.PedidoEstadoService;
import com.urbaneats.service.ReglaNegocioException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Consulta de estado y cancelacion de un pedido por parte del cliente.
 * Ambas rutas revisan que el usuario tenga relacion con el pedido (PedidoAcceso).
 * (La antigua /pedido/confirmar creaba pedidos sin pagar y se elimino.)
 */
@Controller
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final PedidoEstadoService pedidoEstadoService;
    private final PedidoAcceso pedidoAcceso;

    public PedidoController(PedidoRepository pedidoRepository,
                            PedidoEstadoService pedidoEstadoService,
                            PedidoAcceso pedidoAcceso) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoEstadoService = pedidoEstadoService;
        this.pedidoAcceso = pedidoAcceso;
    }

    @GetMapping("/pedido/{id}/estado")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<?> estado(@PathVariable Integer id) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null || pedidoAcceso.vistaDe(pedido, usuario.getCodigoUsuario()) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Pedido no encontrado"));
        }
        return ResponseEntity.ok(Map.of(
                "estado", pedido.getEstado().getValorBd(),
                "etiqueta", pedido.getEstado().getEtiqueta()));
    }

    @PostMapping("/pedido/{id}/cancelar")
    @ResponseBody
    public ResponseEntity<?> cancelar(@PathVariable Integer id) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        try {
            pedidoEstadoService.cancelarPorCliente(id, usuario.getCodigoUsuario());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }
}
