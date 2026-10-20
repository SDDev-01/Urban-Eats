package com.urbaneats.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.urbaneats.integracion.ServicioPythonCliente;
import com.urbaneats.integracion.ServicioPythonException;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.SesionHelper;

import jakarta.servlet.http.HttpSession;

/**
 * Chatbot: Java decide QUIEN pregunta (usuario y rol, que ya conoce por la sesion)
 * y Python + Ollama arman la respuesta con los datos reales de la base.
 *
 * Antes este servicio llamaba a una API de pago (Anthropic) que no tenia clave, y
 * ademas Spring inyectaba una clase de relleno, por eso el chat nunca respondia.
 */
@Service
public class ChatbotServiceImpl implements IChatbotService {

    private static final Logger log = LoggerFactory.getLogger(ChatbotServiceImpl.class);
    private static final String CLAVE_HISTORIAL = "chat_historial";
    private static final int TURNOS_MAXIMOS = 6;

    private final ServicioPythonCliente python;

    public ChatbotServiceImpl(ServicioPythonCliente python) {
        this.python = python;
    }

    @Override
    public Map<String, Object> procesarMensaje(String mensaje, String pagina, HttpSession session) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        List<Map<String, String>> historial = historial(session);

        Map<String, Object> entrada = new HashMap<>();
        entrada.put("mensaje", mensaje);
        entrada.put("rol", rolSegunPagina(pagina, session));
        entrada.put("historial", historial);
        if (usuario != null) {
            entrada.put("codigo_usuario", usuario.getCodigoUsuario());
            entrada.put("nombre", usuario.getNombres());
        }
        if (session.getAttribute("restaurante_activo") instanceof Integer cocina) {
            entrada.put("codigo_restaurante", cocina);
        }

        try {
            Map<String, Object> salida = python.chat(entrada);
            String respuesta = String.valueOf(salida.get("respuesta"));
            guardarTurno(historial, mensaje, respuesta);
            return Map.of("respuesta", respuesta, "modo", String.valueOf(salida.get("modo")));
        } catch (ServicioPythonException e) {
            log.warn("Chatbot sin servicio Python: {}", e.getMessage());
            return Map.of("respuesta", "Ahora no puedo responder: el asistente está fuera de servicio. "
                    + "Puedes seguir tus pedidos en \"Mis pedidos\".", "modo", "basico");
        }
    }

    /**
     * El rol con el que pregunta. Se toma de la pagina, pero solo si el usuario
     * de verdad tiene ese rol (asi nadie se hace pasar por cocina cambiando la pagina).
     */
    private String rolSegunPagina(String pagina, HttpSession session) {
        if (SesionHelper.usuarioActual() == null) {
            return "anonimo";
        }
        String ruta = pagina == null ? "" : pagina;
        if ((ruta.startsWith("/perfilRestaurante") || ruta.startsWith("/reportes"))
                && SesionHelper.tieneRol("GERENTE") && session.getAttribute("restaurante_activo") != null) {
            return "cocina";
        }
        if (ruta.startsWith("/perfilRepartidor") && SesionHelper.tieneRol("REPARTIDOR")) {
            return "repartidor";
        }
        if (ruta.startsWith("/tecnico") && SesionHelper.tieneRol("TECNICO")) {
            return "tecnico";
        }
        return "cliente";
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> historial(HttpSession session) {
        Object guardado = session.getAttribute(CLAVE_HISTORIAL);
        if (guardado instanceof List<?> lista) {
            return (List<Map<String, String>>) lista;
        }
        List<Map<String, String>> nuevo = new ArrayList<>();
        session.setAttribute(CLAVE_HISTORIAL, nuevo);
        return nuevo;
    }

    /** Guarda la pregunta y la respuesta; solo se conservan los ultimos turnos. */
    private void guardarTurno(List<Map<String, String>> historial, String mensaje, String respuesta) {
        historial.add(Map.of("rol", "usuario", "texto", mensaje));
        historial.add(Map.of("rol", "asistente", "texto", respuesta.length() > 1500 ? respuesta.substring(0, 1500) : respuesta));
        while (historial.size() > TURNOS_MAXIMOS) {
            historial.remove(0);
        }
    }
}
