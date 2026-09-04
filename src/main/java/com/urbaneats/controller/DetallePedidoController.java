package com.urbaneats.controller;

import com.urbaneats.entity.DetallePedido;
import com.urbaneats.service.IDetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/detalles-pedido")
public class DetallePedidoController {

    @Autowired
    private IDetallePedidoService detallePedidoService;

    @GetMapping
    public List<DetallePedido> listarTodos() {
        return detallePedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<DetallePedido> buscarPorId(@PathVariable Integer id) {
        return detallePedidoService.buscarPorId(id);
    }

    @PostMapping
    public DetallePedido guardar(@RequestBody DetallePedido detallePedido) {
        return detallePedidoService.guardar(detallePedido);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        detallePedidoService.eliminar(id);
    }
}