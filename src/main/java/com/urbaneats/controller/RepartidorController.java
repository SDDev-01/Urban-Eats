package com.urbaneats.controller;

import com.urbaneats.entity.Repartidor;
import com.urbaneats.service.IRepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/repartidores")
public class RepartidorController {

    @Autowired
    private IRepartidorService repartidorService;

    @GetMapping
    public List<Repartidor> listarTodos() {
        return repartidorService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Repartidor> buscarPorId(@PathVariable Integer id) {
        return repartidorService.buscarPorId(id);
    }

    @PostMapping
    public Repartidor guardar(@RequestBody Repartidor repartidor) {
        return repartidorService.guardar(repartidor);
    }

    @PutMapping("/{id}")
    public Repartidor actualizar(@PathVariable Integer id, @RequestBody Repartidor repartidor) {
        return repartidorService.actualizar(id, repartidor);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        repartidorService.eliminar(id);
    }
}