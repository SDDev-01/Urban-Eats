package com.urbaneats.controller;

import com.urbaneats.service.IChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/chatbot")
public class ChatbotController {

    private final IChatbotService chatbotService;

    public ChatbotController(IChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/responder")
    @ResponseBody
    public ResponseEntity<?> responder(@RequestBody Map<String, String> payload) {
        
        return ResponseEntity.ok(Map.of("respuesta", ""));
    }
}