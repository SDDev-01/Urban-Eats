package com.urbaneats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/pago")
public class PagoController {

    @GetMapping
    public String mostrarPasarela(Model model) {
        return "cliente/pago";
    }

    @PostMapping("/procesar")
    @ResponseBody
    public ResponseEntity<?> procesarPago(@RequestBody Map<String, Object> datosPago) {
        // Recibe y procesa la transaccion del pago
        return ResponseEntity.ok(Map.of("status", "exitoso", "redirect", "/rastreo"));
    }
}