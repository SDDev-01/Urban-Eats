package com.urbaneats.controller;

import com.urbaneats.service.IChatbotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ChatbotController {

    private static final int MENSAJE_LONGITUD_MAXIMA = 500;

    private final IChatbotService chatbotService;

    public ChatbotController(IChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    /** Cuerpo: {"mensaje": "...", "pagina": "/catalogo"}. Respuesta: {"respuesta": "...", "modo": "ia" | "basico"}. */
    @PostMapping("/chatbot")
    public ResponseEntity<Map<String, Object>> responder(@RequestBody Map<String, String> payload, HttpSession session) {
        String mensaje = payload.get("mensaje");

        if (mensaje == null || mensaje.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("respuesta", "El mensaje es obligatorio"));
        }
        if (mensaje.length() > MENSAJE_LONGITUD_MAXIMA) {
            return ResponseEntity.badRequest().body(Map.of("respuesta", "El mensaje es demasiado largo (máximo 500 caracteres)"));
        }

        return ResponseEntity.ok(chatbotService.procesarMensaje(mensaje.trim(), payload.get("pagina"), session));
    }
}
