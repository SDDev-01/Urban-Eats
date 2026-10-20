package com.urbaneats.service;

import java.util.Map;

import jakarta.servlet.http.HttpSession;

public interface IChatbotService {

    /**
     * Responde un mensaje del chat.
     *
     * @param mensaje lo que escribio el usuario
     * @param pagina  la pagina desde donde pregunta (define si actua como cliente, cocina o repartidor)
     * @param session la sesion HTTP: guarda los ultimos turnos de la conversacion
     * @return {"respuesta": texto, "modo": "ia" | "basico"}
     */
    Map<String, Object> procesarMensaje(String mensaje, String pagina, HttpSession session);
}
