package com.urbaneats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @GetMapping
    public String index() {
        return "cliente/pedidos";
    }

    @GetMapping("/crear")
    public String crear() {
        return "cliente/crear-pedido";
    }
}