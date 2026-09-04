package com.urbaneats.controller;

import com.urbaneats.entity.Vehiculo;
import com.urbaneats.service.IVehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private IVehiculoService vehiculoService;

    @GetMapping
    public List<Vehiculo> listarTodos() {
        return vehiculoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Vehiculo> buscarPorId(@PathVariable Integer id) {
        return vehiculoService.buscarPorId(id);
    }

    @PostMapping
    public Vehiculo guardar(@RequestBody Vehiculo vehiculo) {
        return vehiculoService.guardar(vehiculo);
    }

    @PutMapping("/{id}")
    public Vehiculo actualizar(@PathVariable Integer id, @RequestBody Vehiculo vehiculo) {
        return vehiculoService.actualizar(id, vehiculo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        vehiculoService.eliminar(id);
    }
}