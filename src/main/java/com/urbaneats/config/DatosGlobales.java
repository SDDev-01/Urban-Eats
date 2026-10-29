package com.urbaneats.config;

import com.urbaneats.security.RolActivo;
import com.urbaneats.security.SesionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Datos que necesitan todas las vistas:
 *   - modoDemo: para mostrar la franja "Entorno de demostración".
 *   - rolActivo: con que rol esta trabajando el usuario (la barra de navegacion
 *     muestra solo las opciones de ese rol). Se lee de la sesion en cada peticion.
 *   - puedeCambiarRol: si tiene mas de un rol, para mostrar "Cambiar rol".
 */
@ControllerAdvice
public class DatosGlobales {

    private static final List<String> ROLES = List.of(
            RolActivo.CLIENTE, RolActivo.GERENTE, RolActivo.REPARTIDOR, RolActivo.TECNICO);

    @Value("${urbaneats.modo-demo:false}")
    private boolean modoDemo;

    @ModelAttribute("modoDemo")
    public boolean modoDemo() {
        return modoDemo;
    }

    /** CLIENTE, GERENTE, REPARTIDOR o TECNICO; null si no hay sesion. */
    @ModelAttribute("rolActivo")
    public String rolActivo(HttpServletRequest request) {
        if (SesionHelper.usuarioActual() == null) {
            return null;
        }
        String elegido = RolActivo.de(request);
        if (elegido != null) {
            return elegido;
        }
        // Aun no elige: se muestra como cliente si lo es; si no, con su primer rol de trabajo.
        return ROLES.stream().filter(SesionHelper::tieneRol).findFirst().orElse(RolActivo.CLIENTE);
    }

    @ModelAttribute("puedeCambiarRol")
    public boolean puedeCambiarRol() {
        return ROLES.stream().filter(SesionHelper::tieneRol).count() > 1;
    }
}
