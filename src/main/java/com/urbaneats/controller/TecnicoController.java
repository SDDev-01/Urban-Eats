package com.urbaneats.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbaneats.integracion.ServicioPythonCliente;
import com.urbaneats.integracion.ServicioPythonException;
import com.urbaneats.security.SesionHelper;

/**
 * Panel del tecnico de Urban Eats: revisa las solicitudes de cocinas y decide.
 * Solo usuarios con rol TECNICO (SecurityConfig protege /tecnico/**).
 */
@Controller
public class TecnicoController {

    private final ServicioPythonCliente python;

    public TecnicoController(ServicioPythonCliente python) {
        this.python = python;
    }

    /** Lista con filtros combinables, ordenamiento y paginacion (los hace el servicio Python). */
    @GetMapping("/tecnico/solicitudes")
    public String listar(@RequestParam(required = false) String estado,
                         @RequestParam(required = false) String localidad,
                         @RequestParam(required = false) String tipo,
                         @RequestParam(required = false) String q,
                         @RequestParam(defaultValue = "fecha_desc") String orden,
                         @RequestParam(defaultValue = "1") int pagina,
                         Model model) {
        MultiValueMap<String, String> filtros = new LinkedMultiValueMap<>();
        filtros.add("estado", estado);
        filtros.add("localidad", localidad);
        filtros.add("tipo", tipo);
        filtros.add("q", q);
        filtros.add("orden", orden);
        filtros.add("pagina", String.valueOf(pagina));
        filtros.add("tamano", "15");
        try {
            Map<String, Object> resultado = python.listarSolicitudes(filtros);
            model.addAttribute("pagina", resultado);
            int total = ((Number) resultado.get("total")).intValue();
            model.addAttribute("totalPaginas", Math.max(1, (total + 14) / 15));
        } catch (ServicioPythonException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pagina", Map.of("resultados", List.of(), "total", 0, "pagina", 1));
            model.addAttribute("totalPaginas", 1);
        }
        Map<String, Object> filtrosActuales = new HashMap<>();
        filtrosActuales.put("estado", estado);
        filtrosActuales.put("localidad", localidad);
        filtrosActuales.put("tipo", tipo);
        filtrosActuales.put("q", q);
        filtrosActuales.put("orden", orden);
        model.addAttribute("filtros", filtrosActuales);
        model.addAttribute("localidades", SolicitudCocinaController.LOCALIDADES);
        return "tecnico/solicitudes";
    }

    @GetMapping("/tecnico/solicitudes/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("solicitud", python.obtenerSolicitud(id));
            model.addAttribute("cumplimiento", python.cumplimiento(id));
            model.addAttribute("tiposDocumento", SolicitudCocinaController.TIPOS_DOCUMENTO);
            model.addAttribute("vistaTecnico", true);
            return "solicitudDetalle";
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/tecnico/solicitudes";
        }
    }

    /** Decision: En revision, Requiere ajustes, Aprobada (crea la cocina) o Rechazada. */
    @PostMapping("/tecnico/solicitudes/{id}/estado")
    public String cambiarEstado(@PathVariable int id, @RequestParam String estado,
                                @RequestParam(required = false) String observaciones,
                                RedirectAttributes redirect) {
        Map<String, Object> cambio = new HashMap<>();
        cambio.put("estado", estado);
        cambio.put("observaciones", observaciones == null || observaciones.isBlank() ? null : observaciones.trim());
        cambio.put("codigo_revisor", SesionHelper.usuarioActual().getCodigoUsuario());
        try {
            Map<String, Object> resultado = python.cambiarEstado(id, cambio);
            String mensaje = "Aprobada".equals(estado)
                    ? "Solicitud aprobada: se creó la cocina #" + resultado.get("codigo_restaurante") + " y ya está publicada."
                    : "Solicitud actualizada a \"" + estado + "\".";
            redirect.addFlashAttribute("exito", mensaje);
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMensajeCompleto());
        }
        return "redirect:/tecnico/solicitudes/" + id;
    }

    @PostMapping("/tecnico/documentos/{codigo}/revision")
    public String revisarDocumento(@PathVariable int codigo, @RequestParam int solicitud,
                                   @RequestParam("estado_revision") String estadoRevision,
                                   @RequestParam(required = false) String observacion,
                                   RedirectAttributes redirect) {
        Map<String, Object> revision = new HashMap<>();
        revision.put("estado_revision", estadoRevision);
        revision.put("observacion", observacion == null || observacion.isBlank() ? null : observacion.trim());
        try {
            python.revisarDocumento(codigo, revision);
            redirect.addFlashAttribute("exito", "Documento marcado como " + estadoRevision + ".");
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tecnico/solicitudes/" + solicitud;
    }

    /** El PDF pasa por Java: el navegador nunca habla directo con el servicio Python. */
    @GetMapping("/tecnico/documentos/{codigo}/archivo")
    public ResponseEntity<byte[]> descargar(@PathVariable int codigo) {
        try {
            ResponseEntity<byte[]> archivo = python.descargarDocumento(codigo);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"documento-" + codigo + ".pdf\"")
                    .body(archivo.getBody());
        } catch (ServicioPythonException e) {
            return ResponseEntity.status(e.getEstadoHttp()).body(e.getMessage().getBytes());
        }
    }
}
