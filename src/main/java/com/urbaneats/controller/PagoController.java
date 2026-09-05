package com.urbaneats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @GetMapping
    public String index() {
        return "cliente/pago";
    }
}