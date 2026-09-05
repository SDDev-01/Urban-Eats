package com.urbaneats.controller;

import com.urbaneats.service.IPedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final IPedidoService pedidoService;

    public PedidoController(IPedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    
    @PostMapping("/confirmar")
    @ResponseBody
    public ResponseEntity<?> confirmar(@RequestBody Map<String, Object> payload) {
    
        return ResponseEntity.ok(Map.of("redirect", "/rastreo"));
    }

  
    @GetMapping("/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> estado(@PathVariable Integer id) {

        return ResponseEntity.ok(Map.of("estado", "Iniciando"));
    }

  
    @PostMapping("/{id}/cancelar")
    @ResponseBody
    public ResponseEntity<?> cancelar(@PathVariable Integer id) {
   
        return ResponseEntity.ok(Map.of("ok", true));
    }
}