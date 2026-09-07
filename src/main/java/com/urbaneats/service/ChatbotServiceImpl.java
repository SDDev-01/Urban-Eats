package com.urbaneats.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatbotServiceImpl implements IChatbotService {

    // Lee la variable desde el .env. Si no existe, asigna un string vacío.
    @Value("${ANTHROPIC_API_KEY:}")
    private String apiKey;

    private static final List<String> HORARIO_KEYWORDS = Arrays.asList("horario", "hora", "abre", "cierra", "disponible", "cuando", "abierto");
    private static final List<String> UBICACION_KEYWORDS = Arrays.asList("donde", "ubicación", "ubicacion", "direccion", "dirección", "lugar", "mapa");

    @Override
    public String procesarMensaje(String mensaje) {
        String mensajeLower = mensaje.toLowerCase().trim();

        if (contienePalabras(mensajeLower, HORARIO_KEYWORDS)) {
            return respuestaHorarios();
        }

        if (contienePalabras(mensajeLower, UBICACION_KEYWORDS)) {
            return respuestaUbicaciones();
        }

        return respuestaIA(mensaje);
    }

    private boolean contienePalabras(String texto, List<String> palabras) {
        return palabras.stream().anyMatch(texto::contains);
    }

    private String respuestaHorarios() {
        // TODO: Inyectar RestauranteRepository y armar el string como en Laravel
        return "¡Claro! Aquí están los horarios de nuestros restaurantes...";
    }

    private String respuestaUbicaciones() {
        // TODO: Inyectar RestauranteRepository y armar el string como en Laravel
        return "¡Con gusto! Estas son nuestras ubicaciones...";
    }

    private String respuestaIA(String mensaje) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Lo siento, el asistente de IA no está disponible en este momento. ¿Puedo ayudarte con horarios o ubicaciones?";
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.anthropic.com/v1/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        String systemPrompt = "Eres UrbanBot, el asistente virtual de Urban Eats, una plataforma de comida saludable en Bogotá.\n" +
                              "Responde siempre en español, de forma amigable, concisa y útil. No inventes información.";

        Map<String, Object> requestBody = new HashMap<>();
        // Nota: Ajusté el modelo al nombre real válido de Anthropic
        requestBody.put("model", "claude-3-haiku-20240307"); 
        requestBody.put("max_tokens", 512);
        requestBody.put("system", systemPrompt);
        requestBody.put("messages", Collections.singletonList(
                Map.of("role", "user", "content", mensaje)
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
                if (content != null && !content.isEmpty()) {
                    return (String) content.get(0).get("text");
                }
            }
        } catch (Exception e) {
            System.err.println("Error Anthropic: " + e.getMessage());
        }

        return "Tuve un problema al conectarme. Por favor, intenta de nuevo en un momento.";
    }
}