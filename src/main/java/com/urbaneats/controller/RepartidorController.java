package com.urbaneats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.urbaneats.service.IRepartidorService;

@Controller
public class RepartidorController {

    public RepartidorController(IRepartidorService repartidorService) {
    }

    @GetMapping("/repartidor")
    public String mostrarFormulario(Model model) {
        return "repartidor";
    }

    @PostMapping("/repartidor/registrar")
    public String registrar(
            @RequestParam String tipoVehiculo,
            @RequestParam String placa,
            @RequestParam(required = false) String soat,
            @RequestParam(required = false) String seguroVehiculo) {
        
        return "redirect:/perfilRepartidor";
    }

    @GetMapping("/perfilRepartidor")
    public String mostrarPerfil(Model model) {
        return "perfilRepartidor";
    }

    @PostMapping("/repartidor/tomar-pedido/{envioId}")
    public String tomarPedido(@PathVariable Integer envioId) {
        
        return "redirect:/perfilRepartidor";
    }

    @PostMapping("/repartidor/marcar-entregado/{envioId}")
    public String marcarEntregado(@PathVariable Integer envioId) {
        
        return "redirect:/perfilRepartidor";
    }
}