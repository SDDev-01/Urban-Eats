package com.urbaneats.service;

import org.springframework.stereotype.Service;

/**
 * Implementacion provisional de IChatbotService.
 *
 * La interfaz existia sin implementacion, asi que Spring no podia construir
 * ChatbotController y la aplicacion no arrancaba. Esta clase solo devuelve un
 * mensaje fijo: la logica real del chatbot esta pendiente.
 */
@Service
public class ChatbotService implements IChatbotService {

    @Override
    public String procesarMensaje(String mensaje) {
        return "El chatbot todavia no esta disponible.";
    }
}
