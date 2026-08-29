package com.urbaneats.controller;

import com.urbaneats.domain.Envio;
import com.urbaneats.service.EnvioService;
import com.urbaneats.service.PedidoService;
import com.urbaneats.service.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/envio")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private RepartidorService repartidorService;

    @GetMapping("/listado")
    public String listarEnvios(Model model) {
        var envios = envioService.listarEnvios();
        model.addAttribute("envios", envios);
        model.addAttribute("totalEnvios", envios.size());
        return "envio/listado";
    }

    @GetMapping("/nuevo")
    public String envioNuevo(Envio envio, Model model) {
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        model.addAttribute("repartidores", repartidorService.listarRepartidores());
        return "envio/modifica";
    }

    @PostMapping("/guardar")
    public String envioGuardar(Envio envio) {
        envioService.guardarEnvio(envio);
        return "redirect:/envio/listado";
    }

    @GetMapping("/eliminar/{idEnvio}")
    public String envioEliminar(Envio envio) {
        envioService.eliminarEnvio(envio.getIdEnvio());
        return "redirect:/envio/listado";
    }

    @GetMapping("/modificar/{idEnvio}")
    public String envioModificar(Envio envio, Model model) {
        envio = envioService.obtenerEnvioPorId(envio.getIdEnvio()).orElse(null);
        model.addAttribute("envio", envio);
        model.addAttribute("pedidos", pedidoService.listarPedidos());
        model.addAttribute("repartidores", repartidorService.listarRepartidores());
        return "envio/modifica";
    }
}