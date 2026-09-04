package com.urbaneats.controller;

import com.urbaneats.entity.Pedido;
import com.urbaneats.service.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private IPedidoService pedidoService;

    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Pedido> buscarPorId(@PathVariable Integer id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping
    public Pedido guardar(@RequestBody Pedido pedido) {
        return pedidoService.guardar(pedido);
    }

    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Integer id, @RequestBody Pedido pedido) {
        return pedidoService.actualizar(id, pedido);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        pedidoService.eliminar(id);
    }
}