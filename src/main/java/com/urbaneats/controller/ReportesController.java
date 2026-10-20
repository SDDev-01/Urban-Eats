package com.urbaneats.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.urbaneats.integracion.ServicioPythonCliente;
import com.urbaneats.integracion.ServicioPythonException;
import com.urbaneats.security.SesionHelper;
import com.urbaneats.service.IRestauranteService;

import jakarta.servlet.http.HttpSession;

/**
 * Reportes de la operacion. Los calcula el servicio Python (/api/reportes) y aqui solo
 * se muestran o se descargan.
 *   - La COCINA solo ve sus propios datos: el codigo de su cocina se toma de la sesion,
 *     no del formulario, para que no pueda consultar otras cocinas cambiando la URL.
 *   - El TECNICO ve todas las cocinas y puede filtrar por una.
 */
@Controller
public class ReportesController {

    private static final Set<String> REPORTES = Set.of(
            "pedidos", "ventas-por-cocina", "pedidos-por-hora", "tiempos-entrega", "platos-top", "documentos-por-vencer");
    private static final Set<String> FORMATOS = Set.of("csv", "xlsx", "pdf");
    private static final List<String> FILTROS = List.of(
            "desde", "hasta", "estado", "franja", "q", "orden", "monto_min", "monto_max", "etiqueta", "pagina");

    private final ServicioPythonCliente python;
    private final IRestauranteService restauranteService;

    public ReportesController(ServicioPythonCliente python, IRestauranteService restauranteService) {
        this.python = python;
        this.restauranteService = restauranteService;
    }

    @GetMapping("/reportes")
    public String mostrar(@RequestParam MultiValueMap<String, String> parametros, HttpSession session, Model model) {
        MultiValueMap<String, String> filtros = filtrosPermitidos(parametros, session);
        if (filtros == null) {
            return "redirect:/seleccion-restaurante"; // gerente sin cocina elegida
        }
        boolean esTecnico = SesionHelper.tieneRol("TECNICO");
        model.addAttribute("esTecnico", esTecnico);
        model.addAttribute("filtros", filtros.toSingleValueMap());
        // Los mismos filtros en forma de URL, para los botones de descarga.
        model.addAttribute("consulta", java.util.Objects.toString(org.springframework.web.util.UriComponentsBuilder
                .newInstance().queryParams(sinClaves(filtros, "pagina")).build().encode().getQuery(), ""));
        if (esTecnico) {
            model.addAttribute("cocinas", restauranteService.listarRestaurantes());
        } else {
            model.addAttribute("cocinaActiva",
                    restauranteService.buscarRestaurante((Integer) session.getAttribute("restaurante_activo")));
        }

        try {
            MultiValueMap<String, String> paraPedidos = new LinkedMultiValueMap<>(filtros);
            paraPedidos.add("tamano", "25");
            model.addAttribute("pedidos", python.reporte("pedidos", paraPedidos));
            MultiValueMap<String, String> resumen = sinClaves(filtros, "estado", "q", "orden", "monto_min", "monto_max", "pagina", "etiqueta");
            model.addAttribute("porHora", python.reporte("pedidos-por-hora", sinClaves(resumen, "franja")));
            model.addAttribute("ventas", python.reporte("ventas-por-cocina", resumen));
            model.addAttribute("tiempos", python.reporte("tiempos-entrega", sinClaves(resumen, "franja")));
            MultiValueMap<String, String> platos = sinClaves(resumen, "franja");
            if (filtros.getFirst("etiqueta") != null) {
                platos.add("etiqueta", filtros.getFirst("etiqueta"));
            }
            model.addAttribute("platos", python.reporte("platos-top", platos));
            if (esTecnico) {
                model.addAttribute("documentos", python.reporte("documentos-por-vencer", new LinkedMultiValueMap<>()));
            }
        } catch (ServicioPythonException e) {
            model.addAttribute("error", e.getMensajeCompleto());
        }
        return "reportes";
    }

    /** Descarga de cualquier reporte en csv, xlsx o pdf (el archivo lo genera Python). */
    @GetMapping("/reportes/exportar/{reporte}")
    public ResponseEntity<byte[]> exportar(@PathVariable String reporte, @RequestParam String formato,
                                           @RequestParam MultiValueMap<String, String> parametros, HttpSession session) {
        if (!REPORTES.contains(reporte) || !FORMATOS.contains(formato)
                || ("documentos-por-vencer".equals(reporte) && !SesionHelper.tieneRol("TECNICO"))) {
            return ResponseEntity.badRequest().body("Reporte o formato no válido".getBytes());
        }
        MultiValueMap<String, String> filtros = filtrosPermitidos(parametros, session);
        if (filtros == null) {
            return ResponseEntity.status(409).body("Primero elige tu cocina".getBytes());
        }
        filtros.add("formato", formato);
        try {
            ResponseEntity<byte[]> archivo = python.reporteArchivo(reporte, filtros);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, archivo.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
                    .header(HttpHeaders.CONTENT_DISPOSITION, archivo.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                    .body(archivo.getBody());
        } catch (ServicioPythonException e) {
            return ResponseEntity.status(e.getEstadoHttp()).body(e.getMessage().getBytes());
        }
    }

    /**
     * Solo deja pasar los filtros conocidos y fija la cocina segun el rol.
     * Devuelve null si un gerente aun no ha elegido cocina.
     */
    private MultiValueMap<String, String> filtrosPermitidos(MultiValueMap<String, String> parametros, HttpSession session) {
        MultiValueMap<String, String> filtros = new LinkedMultiValueMap<>();
        for (String clave : FILTROS) {
            List<String> valores = parametros.get(clave);
            if (valores != null) {
                valores.stream().filter(v -> v != null && !v.isBlank()).forEach(v -> filtros.add(clave, v.trim()));
            }
        }
        if (SesionHelper.tieneRol("TECNICO")) {
            String cocina = parametros.getFirst("codigo_restaurante");
            if (cocina != null && !cocina.isBlank()) {
                filtros.add("codigo_restaurante", cocina);
            }
        } else {
            Object activa = session.getAttribute("restaurante_activo");
            if (!(activa instanceof Integer)) {
                return null;
            }
            filtros.add("codigo_restaurante", String.valueOf(activa));
        }
        return filtros;
    }

    private MultiValueMap<String, String> sinClaves(MultiValueMap<String, String> origen, String... claves) {
        MultiValueMap<String, String> copia = new LinkedMultiValueMap<>(origen);
        for (String clave : claves) {
            copia.remove(clave);
        }
        return copia;
    }

    /** Para la plantilla: el valor maximo de pedidos por hora (ancho de las barras). */
    public static int maximo(List<Map<String, Object>> filas) {
        return filas.stream().mapToInt(f -> ((Number) f.get("pedidos")).intValue()).max().orElse(1);
    }
}
