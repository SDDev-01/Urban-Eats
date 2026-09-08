package com.urbaneats.service;

import com.urbaneats.dao.MenuRepository;
import com.urbaneats.dao.RestauranteRepository;
import com.urbaneats.entity.Menu;
import com.urbaneats.entity.Plato;
import com.urbaneats.entity.Restaurante;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatbotServiceImpl implements IChatbotService {

    // Lee la variable desde el .env. Si no existe, asigna un string vacío.
    @Value("${ANTHROPIC_API_KEY:}")
    private String apiKey;

    private static final List<String> HORARIO_KEYWORDS = Arrays.asList("horario", "hora", "abre", "cierra", "disponible", "cuando", "abierto");
    private static final List<String> UBICACION_KEYWORDS = Arrays.asList("donde", "ubicación", "ubicacion", "direccion", "dirección", "lugar", "mapa");

    private final RestauranteRepository restauranteRepository;
    private final MenuRepository menuRepository;
    private final IPlatoService platoService;
    private final RestTemplate restTemplate;

    public ChatbotServiceImpl(RestauranteRepository restauranteRepository,
                               MenuRepository menuRepository,
                               IPlatoService platoService) {
        this.restauranteRepository = restauranteRepository;
        this.menuRepository = menuRepository;
        this.platoService = platoService;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(20).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(20).toMillis());
        this.restTemplate = new RestTemplate(requestFactory);
    }

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
        List<Restaurante> restaurantes = restauranteRepository.findAll();
        if (restaurantes.isEmpty()) {
            return "Actualmente no tenemos información de horarios disponible. ¡Contáctanos para más detalles!";
        }

        String lineas = restaurantes.stream()
                .map(r -> "• **" + r.getNombre() + "**: " + r.getHorario())
                .collect(Collectors.joining("\n"));

        return "¡Claro! Aquí están los horarios de nuestros restaurantes:\n\n" + lineas
                + "\n\n¿Hay algo más en lo que pueda ayudarte?";
    }

    private String respuestaUbicaciones() {
        List<Restaurante> restaurantes = restauranteRepository.findAll();
        if (restaurantes.isEmpty()) {
            return "No tenemos información de ubicaciones disponible en este momento.";
        }

        String lineas = restaurantes.stream()
                .map(r -> "• **" + r.getNombre() + "**: " + r.getDireccion())
                .collect(Collectors.joining("\n"));

        return "¡Con gusto! Estas son nuestras ubicaciones:\n\n" + lineas
                + "\n\n¿Necesitas ayuda con algo más?";
    }

    private String respuestaIA(String mensaje) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Lo siento, el asistente de IA no está disponible en este momento. ¿Puedo ayudarte con horarios o ubicaciones?";
        }

        String url = "https://api.anthropic.com/v1/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        String systemPrompt = "Eres UrbanBot, el asistente virtual de Urban Eats, una plataforma de comida saludable en Bogotá.\n"
                + "Responde siempre en español, de forma amigable, concisa y útil. No inventes información.\n"
                + "Si no puedes responder con los datos disponibles, sugiere al usuario que visite la sección de restaurantes.\n\n"
                + "INFORMACIÓN ACTUAL DE URBAN EATS:\n" + construirContexto();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-haiku-4-5-20251001");
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

    /** Arma un resumen de restaurantes -> menus -> platos para que la IA responda con datos reales. */
    private String construirContexto() {
        List<Restaurante> restaurantes = restauranteRepository.findAll();
        if (restaurantes.isEmpty()) {
            return "No hay restaurantes registrados actualmente.";
        }

        StringBuilder sb = new StringBuilder();
        for (Restaurante restaurante : restaurantes) {
            sb.append("RESTAURANTE: ").append(restaurante.getNombre()).append('\n');
            sb.append("  Dirección: ").append(restaurante.getDireccion()).append('\n');
            sb.append("  Horario: ").append(restaurante.getHorario()).append('\n');

            List<Menu> menus = menuRepository.findByRestaurante_CodigoRestaurante(restaurante.getCodigoRestaurante());
            for (Menu menu : menus) {
                sb.append("  Categoría de menú: ").append(menu.getCategoria()).append('\n');

                List<Plato> platos = platoService.listarPlatosPorMenu(menu.getCodigoMenu());
                for (Plato plato : platos) {
                    sb.append("    - ").append(plato.getNombre())
                            .append(" | $").append(plato.getPrecio())
                            .append(" | ").append(plato.getTipoComida())
                            .append(" | ").append(plato.getDisponibilidad())
                            .append('\n');

                    if (plato.getDescripcion() != null && !plato.getDescripcion().isBlank()) {
                        sb.append("      Descripción: ").append(plato.getDescripcion()).append('\n');
                    }
                }
            }
            sb.append('\n');
        }

        return sb.toString();
    }
}
