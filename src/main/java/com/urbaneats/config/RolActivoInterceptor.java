package com.urbaneats.config;

import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.RolActivo;
import com.urbaneats.security.SesionHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * Aplica el rol activo (ver RolActivo) en cada peticion:
 *
 * 1. Entrar al panel de un rol lo deja como rol activo. Por ejemplo, abrir /perfilRepartidor
 *    pone REPARTIDOR (Spring Security ya comprobo que el usuario tiene ese rol).
 * 2. Con un rol de trabajo activo, las paginas de cliente (restaurantes, carrito, pago,
 *    mis pedidos) no se abren: se vuelve al panel del rol con un aviso. Para pedir comida
 *    hay que elegir "Como usuario" en /seleccion-rol.
 * 3. La cocina elegida (restaurante_activo) solo vale para el usuario que la eligio.
 *    Si otro usuario inicia sesion en el mismo navegador, se descarta.
 */
@Component
public class RolActivoInterceptor implements HandlerInterceptor {

    private static final List<String> PANEL_COCINA = List.of("/perfilRestaurante", "/menu", "/seleccion-restaurante");
    private static final List<String> PANEL_REPARTIDOR = List.of("/perfilRepartidor");
    private static final List<String> PANEL_TECNICO = List.of("/tecnico");

    private static final List<String> PAGINAS_DE_CLIENTE = List.of(
            "/catalogo", "/filtrar-platos", "/restaurantes", "/restauranteDetalle",
            "/carrito", "/pago", "/process_payment", "/mis-pedidos");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        if (usuario == null) {
            return true;
        }
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        descartarCocinaAjena(request.getSession(false), usuario);

        String rolDelPanel = rolDelPanel(ruta);
        if (rolDelPanel != null && SesionHelper.tieneRol(rolDelPanel)) {
            RolActivo.guardar(request, rolDelPanel);
            return true;
        }

        String activo = RolActivo.de(request);
        if (RolActivo.esDeTrabajo(activo) && empiezaCon(ruta, PAGINAS_DE_CLIENTE)) {
            if (esPeticionJson(request)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Estás trabajando con un rol que no hace pedidos. "
                        + "Usa 'Cambiar rol' y elige 'Como usuario' para pedir comida.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + RolActivo.inicio(activo) + "?aviso=rol");
            }
            return false;
        }
        return true;
    }

    private String rolDelPanel(String ruta) {
        if (empiezaCon(ruta, PANEL_COCINA)) return RolActivo.GERENTE;
        if (empiezaCon(ruta, PANEL_REPARTIDOR)) return RolActivo.REPARTIDOR;
        if (empiezaCon(ruta, PANEL_TECNICO)) return RolActivo.TECNICO;
        return null;
    }

    private void descartarCocinaAjena(HttpSession sesion, CustomUserDetails usuario) {
        if (sesion == null || sesion.getAttribute("restaurante_activo") == null) {
            return;
        }
        if (!usuario.getCodigoUsuario().equals(sesion.getAttribute(RolActivo.DUENO_COCINA))) {
            sesion.removeAttribute("restaurante_activo");
            sesion.removeAttribute(RolActivo.DUENO_COCINA);
        }
    }

    private boolean empiezaCon(String ruta, List<String> prefijos) {
        return prefijos.stream().anyMatch(p -> ruta.equals(p) || ruta.startsWith(p + "/"));
    }

    private boolean esPeticionJson(HttpServletRequest request) {
        String acepta = request.getHeader("Accept");
        String tipo = request.getContentType();
        return !"GET".equals(request.getMethod())
                || (acepta != null && acepta.contains("application/json"))
                || (tipo != null && tipo.contains("application/json"));
    }
}
