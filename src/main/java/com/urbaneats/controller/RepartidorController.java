package com.urbaneats.controller;

import com.urbaneats.domain.Repartidor;
import com.urbaneats.service.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/repartidor")
public class RepartidorController {

    @Autowired
    private RepartidorService repartidorService;

    @GetMapping("/listado")
    public String listarRepartidores(Model model) {
        var repartidores = repartidorService.listarRepartidores();
        model.addAttribute("repartidores", repartidores);
        model.addAttribute("totalRepartidores", repartidores.size());
        return "repartidor/listado";
    }

    @GetMapping("/nuevo")
    public String repartidorNuevo(Repartidor repartidor) {
        return "repartidor/modifica";
    }

    @PostMapping("/guardar")
    public String repartidorGuardar(Repartidor repartidor) {
        repartidorService.guardarRepartidor(repartidor);
        return "redirect:/repartidor/listado";
    }

    @GetMapping("/eliminar/{idRepartidor}")
    public String repartidorEliminar(Repartidor repartidor) {
        repartidorService.eliminarRepartidor(repartidor.getIdRepartidor());
        return "redirect:/repartidor/listado";
    }

    @GetMapping("/modificar/{idRepartidor}")
    public String repartidorModificar(Repartidor repartidor, Model model) {
        repartidor = repartidorService.obtenerRepartidorPorId(repartidor.getIdRepartidor()).orElse(null);
        model.addAttribute("repartidor", repartidor);
        return "repartidor/modifica";
    }
}