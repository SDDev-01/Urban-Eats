package com.urbaneats.integracion;

import java.util.Map;

/**
 * Error que devuelve el servicio Python (o que no responde).
 * El mensaje viene en espanol listo para mostrarlo; "campos" trae los errores
 * de validacion por campo cuando el servicio responde 422.
 */
public class ServicioPythonException extends RuntimeException {

    private final int estadoHttp;
    private final Map<String, Object> campos;

    public ServicioPythonException(String mensaje, int estadoHttp, Map<String, Object> campos) {
        super(mensaje);
        this.estadoHttp = estadoHttp;
        this.campos = campos == null ? Map.of() : campos;
    }

    public int getEstadoHttp() {
        return estadoHttp;
    }

    public Map<String, Object> getCampos() {
        return campos;
    }

    /** Mensaje con el detalle de cada campo, para mostrar en un solo aviso. */
    public String getMensajeCompleto() {
        if (campos.isEmpty()) {
            return getMessage();
        }
        StringBuilder texto = new StringBuilder(getMessage());
        campos.values().forEach(valor -> texto.append(" ").append(valor));
        return texto.toString();
    }
}
