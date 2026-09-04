package com.urbaneats.controller;

import com.urbaneats.entity.Envio;
import com.urbaneats.service.IEnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    @Autowired
    private IEnvioService envioService;

    @GetMapping
    public List<Envio> listarTodos() {
        return envioService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Envio> buscarPorId(@PathVariable Integer id) {
        return envioService.buscarPorId(id);
    }

    @PostMapping
    public Envio guardar(@RequestBody Envio envio) {
        return envioService.guardar(envio);
    }

    @PutMapping("/{id}")
    public Envio actualizar(@PathVariable Integer id, @RequestBody Envio envio) {
        return envioService.actualizar(id, envio);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        envioService.eliminar(id);
    }
}