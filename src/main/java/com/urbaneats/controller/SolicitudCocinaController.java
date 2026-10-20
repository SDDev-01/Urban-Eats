package com.urbaneats.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbaneats.integracion.ServicioPythonCliente;
import com.urbaneats.integracion.ServicioPythonException;
import com.urbaneats.security.CustomUserDetails;
import com.urbaneats.security.SesionHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Vinculacion de una cocina (lado del solicitante).
 *
 * Antes, cualquier usuario creaba un restaurante con 3 campos y quedaba publicado al
 * instante. Ahora llena una solicitud con datos legales, ubicacion, horario nocturno y
 * documentos sanitarios; el servicio Python la valida y un tecnico la aprueba.
 * Solo al aprobarla se crea la cocina. Este controlador solo reenvia al servicio Python
 * (ServicioPythonCliente): las reglas viven alla.
 */
@Controller
public class SolicitudCocinaController {

    /** Documentos que se piden en el formulario, en este orden. */
    public static final List<String> TIPOS_DOCUMENTO = List.of(
            "RUT", "Camara de Comercio", "Concepto sanitario", "Certificado manipulacion", "Certificado medico");

    public static final List<String> LOCALIDADES = List.of(
            "Usaquén", "Chapinero", "Santa Fe", "San Cristóbal", "Usme", "Tunjuelito", "Bosa", "Kennedy",
            "Fontibón", "Engativá", "Suba", "Barrios Unidos", "Teusaquillo", "Los Mártires", "Antonio Nariño",
            "Puente Aranda", "La Candelaria", "Rafael Uribe Uribe", "Ciudad Bolívar", "Sumapaz");

    public static final List<String> CATEGORIAS = List.of(
            "Casera", "Saludable / balanceada", "Casera colombiana", "Parrilla", "Asiática",
            "Pizzas y pastas", "Comida rápida", "Panadería y cafetería");

    private static final List<String> CAMPOS_SOLICITUD = List.of(
            "nombre_comercial", "razon_social", "tipo_persona", "nit", "digito_verificacion", "matricula_mercantil",
            "tipo_establecimiento", "categoria", "telefono", "correo", "direccion", "localidad", "latitud",
            "longitud", "hora_apertura", "hora_cierre");

    private final ServicioPythonCliente python;
    private final SesionHelper sesionHelper;

    public SolicitudCocinaController(ServicioPythonCliente python, SesionHelper sesionHelper) {
        this.python = python;
        this.sesionHelper = sesionHelper;
    }

    /** La antigua pagina de "crear restaurante" ahora lleva a la solicitud. */
    @GetMapping("/restaurante")
    public String antiguoFormulario() {
        return "redirect:/restaurante/solicitud/nueva";
    }

    @GetMapping("/restaurante/solicitud/nueva")
    public String nueva(Model model) {
        if (!model.containsAttribute("datos")) {
            model.addAttribute("datos", Map.of());
        }
        prepararFormulario(model, null);
        return "solicitudCocina";
    }

    /** Create: datos de la solicitud + los documentos que se hayan adjuntado. */
    @PostMapping("/restaurante/solicitud")
    public String crear(MultipartHttpServletRequest request, RedirectAttributes redirect) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        Map<String, Object> datos = leerCampos(request);
        datos.put("codigo_usuario", usuario.getCodigoUsuario());
        datos.put("autoriza_datos", "on".equals(request.getParameter("autoriza_datos")));
        datos.put("enviar", !"borrador".equals(request.getParameter("accion")));

        Integer codigo;
        try {
            codigo = entero(python.crearSolicitud(datos).get("codigo_solicitud"));
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("erroresCampos", e.getCampos());
            redirect.addFlashAttribute("datos", datos);
            return "redirect:/restaurante/solicitud/nueva";
        }

        // Los documentos se envian uno por uno; si alguno falla, la solicitud ya existe
        // y el usuario puede volver a subirlo desde el detalle.
        StringBuilder avisos = new StringBuilder();
        for (int i = 0; i < TIPOS_DOCUMENTO.size(); i++) {
            String expedicion = request.getParameter("doc_expedicion_" + i);
            if (expedicion == null || expedicion.isBlank()) {
                continue; // documento no diligenciado
            }
            try {
                python.agregarDocumento(codigo, camposDocumento(request, i, TIPOS_DOCUMENTO.get(i)),
                        request.getFile("doc_archivo_" + i));
            } catch (ServicioPythonException e) {
                avisos.append(TIPOS_DOCUMENTO.get(i)).append(": ").append(e.getMensajeCompleto()).append(" ");
            }
        }
        redirect.addFlashAttribute("exito", "Solicitud #" + codigo + " registrada. Un técnico de Urban Eats la revisará.");
        if (!avisos.isEmpty()) {
            redirect.addFlashAttribute("error", "Algunos documentos no se guardaron: " + avisos);
        }
        return "redirect:/restaurante/solicitud/" + codigo;
    }

    /** Read: las solicitudes del usuario. */
    @GetMapping("/restaurante/solicitudes")
    public String misSolicitudes(Model model) {
        MultiValueMap<String, String> filtros = new LinkedMultiValueMap<>();
        filtros.add("codigo_usuario", String.valueOf(SesionHelper.usuarioActual().getCodigoUsuario()));
        filtros.add("tamano", "50");
        try {
            model.addAttribute("pagina", python.listarSolicitudes(filtros));
        } catch (ServicioPythonException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pagina", Map.of("resultados", List.of(), "total", 0));
        }
        return "misSolicitudes";
    }

    /** Read: una solicitud con documentos, historial y checklist de cumplimiento. */
    @GetMapping("/restaurante/solicitud/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirect) {
        try {
            Map<String, Object> solicitud = solicitudPropia(id);
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("cumplimiento", python.cumplimiento(id));
            model.addAttribute("tiposDocumento", TIPOS_DOCUMENTO);
            model.addAttribute("vistaTecnico", false);
            return "solicitudDetalle";
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/restaurante/solicitudes";
        }
    }

    @GetMapping("/restaurante/solicitud/{id}/editar")
    public String editar(@PathVariable int id, Model model, RedirectAttributes redirect) {
        try {
            Map<String, Object> solicitud = solicitudPropia(id);
            if (!model.containsAttribute("datos")) {
                model.addAttribute("datos", solicitud);
            }
            prepararFormulario(model, id);
            return "solicitudCocina";
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/restaurante/solicitudes";
        }
    }

    /** Update: corrige los datos y (si se pide) la reenvia para revision. */
    @PostMapping("/restaurante/solicitud/{id}/editar")
    public String actualizar(@PathVariable int id, HttpServletRequest request, RedirectAttributes redirect) {
        Map<String, Object> datos = leerCampos(request);
        datos.put("enviar", !"borrador".equals(request.getParameter("accion")));
        try {
            python.actualizarSolicitud(id, datos, SesionHelper.usuarioActual().getCodigoUsuario());
            redirect.addFlashAttribute("exito", "Solicitud actualizada.");
            return "redirect:/restaurante/solicitud/" + id;
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("erroresCampos", e.getCampos());
            redirect.addFlashAttribute("datos", datos);
            return "redirect:/restaurante/solicitud/" + id + "/editar";
        }
    }

    /** Agregar un documento a una solicitud existente (por ejemplo, tras "Requiere ajustes"). */
    @PostMapping("/restaurante/solicitud/{id}/documentos")
    public String agregarDocumento(@PathVariable int id, MultipartHttpServletRequest request, RedirectAttributes redirect) {
        try {
            solicitudPropia(id);
            String tipo = request.getParameter("tipo_documento");
            MultipartFile archivo = request.getFile("archivo");
            Map<String, String> campos = new LinkedHashMap<>();
            campos.put("tipo_documento", tipo);
            campos.put("numero_documento", request.getParameter("numero_documento"));
            campos.put("titular", request.getParameter("titular"));
            campos.put("resultado", request.getParameter("resultado"));
            campos.put("horas_capacitacion", request.getParameter("horas_capacitacion"));
            campos.put("fecha_expedicion", request.getParameter("fecha_expedicion"));
            campos.put("fecha_vencimiento", request.getParameter("fecha_vencimiento"));
            python.agregarDocumento(id, campos, archivo);
            redirect.addFlashAttribute("exito", "Documento agregado.");
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMensajeCompleto());
        }
        return "redirect:/restaurante/solicitud/" + id;
    }

    /** Delete: solo borradores o rechazadas (lo valida el servicio Python). */
    @PostMapping("/restaurante/solicitud/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            python.eliminarSolicitud(id, SesionHelper.usuarioActual().getCodigoUsuario());
            redirect.addFlashAttribute("exito", "Solicitud eliminada.");
            return "redirect:/restaurante/solicitudes";
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/restaurante/solicitud/" + id;
        }
    }

    /** Tras la aprobacion, el usuario ya tiene el rol Gerente: se recarga la sesion y entra a su cocina. */
    @PostMapping("/restaurante/solicitud/{id}/entrar")
    public String entrarALaCocina(@PathVariable int id, HttpServletRequest request, HttpServletResponse response) {
        sesionHelper.recargarRoles(request, response);
        return "redirect:/seleccion-restaurante";
    }

    // ===================== AUXILIARES =====================

    /** Trae la solicitud y verifica que sea del usuario actual. */
    private Map<String, Object> solicitudPropia(int id) {
        Map<String, Object> solicitud = python.obtenerSolicitud(id);
        Integer duenio = entero(solicitud.get("codigo_usuario"));
        if (!SesionHelper.usuarioActual().getCodigoUsuario().equals(duenio)) {
            throw new ServicioPythonException("Esa solicitud no es tuya.", 403, null);
        }
        return solicitud;
    }

    private void prepararFormulario(Model model, Integer codigoEdicion) {
        model.addAttribute("localidades", LOCALIDADES);
        model.addAttribute("categorias", CATEGORIAS);
        model.addAttribute("tiposDocumento", TIPOS_DOCUMENTO);
        model.addAttribute("codigoEdicion", codigoEdicion);
        if (!model.containsAttribute("erroresCampos")) {
            model.addAttribute("erroresCampos", Map.of());
        }
    }

    private Map<String, Object> leerCampos(HttpServletRequest request) {
        Map<String, Object> datos = new LinkedHashMap<>();
        for (String campo : CAMPOS_SOLICITUD) {
            String valor = request.getParameter(campo);
            datos.put(campo, valor == null || valor.isBlank() ? null : valor.trim());
        }
        return datos;
    }

    private Map<String, String> camposDocumento(HttpServletRequest request, int i, String tipo) {
        Map<String, String> campos = new LinkedHashMap<>();
        campos.put("tipo_documento", tipo);
        campos.put("numero_documento", request.getParameter("doc_numero_" + i));
        campos.put("titular", request.getParameter("doc_titular_" + i));
        campos.put("resultado", request.getParameter("doc_resultado_" + i));
        campos.put("horas_capacitacion", request.getParameter("doc_horas_" + i));
        campos.put("fecha_expedicion", request.getParameter("doc_expedicion_" + i));
        campos.put("fecha_vencimiento", request.getParameter("doc_vencimiento_" + i));
        return campos;
    }

    static Integer entero(Object valor) {
        return valor == null ? null : Integer.valueOf(String.valueOf(valor).replace(".0", ""));
    }
}
