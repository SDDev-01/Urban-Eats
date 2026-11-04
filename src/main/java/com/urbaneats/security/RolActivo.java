package com.urbaneats.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * El rol con el que el usuario esta trabajando en esta sesion.
 *
 * Un mismo usuario puede ser cliente y cocina, o cliente y repartidor. Al entrar elige
 * "Como usuario", "Como cocina", "Como repartidor" o "Como tecnico" y se guarda aqui,
 * en la sesion (atributo "rol_activo"). Con eso la barra de navegacion muestra solo las
 * opciones de ese rol y RolActivoInterceptor bloquea las paginas de cliente a la cocina
 * y al repartidor.
 */
public final class RolActivo {

    public static final String ATRIBUTO = "rol_activo";
    /** Quien eligio la cocina guardada en "restaurante_activo" (codigo de usuario). */
    public static final String DUENO_COCINA = "restaurante_activo_usuario";

    public static final String CLIENTE = "CLIENTE";
    public static final String GERENTE = "GERENTE";
    public static final String REPARTIDOR = "REPARTIDOR";
    public static final String TECNICO = "TECNICO";

    private RolActivo() {
    }

    /** El rol guardado en la sesion, o null si todavia no ha elegido. */
    public static String de(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion != null ? (String) sesion.getAttribute(ATRIBUTO) : null;
    }

    public static void guardar(HttpServletRequest request, String rol) {
        request.getSession().setAttribute(ATRIBUTO, rol);
    }

    /** True si el rol activo es de trabajo (cocina, repartidor o tecnico). */
    public static boolean esDeTrabajo(String rol) {
        return GERENTE.equals(rol) || REPARTIDOR.equals(rol) || TECNICO.equals(rol);
    }

    /** La pagina principal de cada rol. */
    public static String inicio(String rol) {
        if (GERENTE.equals(rol)) return "/perfilRestaurante";
        if (REPARTIDOR.equals(rol)) return "/perfilRepartidor";
        if (TECNICO.equals(rol)) return "/tecnico/solicitudes";
        return "/catalogo";
    }
}
