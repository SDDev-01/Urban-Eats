package com.urbaneats.controller;

import com.urbaneats.service.IChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private final IChatbotService chatbotService;

    public ChatbotController(IChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/responder")
    public ResponseEntity<Map<String, String>> responder(@RequestBody Map<String, String> payload) {
        String mensaje = payload.get("mensaje");
        
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("respuesta", "El mensaje es obligatorio"));
        }
        
        String respuestaTexto = chatbotService.procesarMensaje(mensaje);
        return ResponseEntity.ok(Map.of("respuesta", respuestaTexto));
    }
}