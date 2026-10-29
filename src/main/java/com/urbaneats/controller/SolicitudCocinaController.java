package com.urbaneats.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final String CAMARA = "Camara de Comercio";
    private static final String MANIPULACION = "Certificado manipulacion";
    private static final String MEDICO = "Certificado medico";
    private static final int DIAS_CAMARA = 30;
    private static final int HORAS_MINIMAS = 10;
    private static final long MAX_PDF = 5L * 1024 * 1024;
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    /** El formulario manda las fechas dd/mm/aaaa; STRICT rechaza fechas que no existen, como el 31/02. */
    private static final DateTimeFormatter FECHA_DD_MM_AAAA =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

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

    /**
     * Create: los datos de la solicitud y sus documentos en un solo envio.
     *
     * Para que al tecnico le llegue completa, primero se revisan aqui los documentos;
     * despues se guarda como borrador con sus documentos y solo al final pasa a "Enviada".
     * Si algun documento no se guarda, queda en borrador y el usuario la envia desde el detalle.
     */
    @PostMapping("/restaurante/solicitud")
    public String crear(MultipartHttpServletRequest request, RedirectAttributes redirect) {
        CustomUserDetails usuario = SesionHelper.usuarioActual();
        Map<String, Object> datos = leerCampos(request);
        datos.put("codigo_usuario", usuario.getCodigoUsuario());
        datos.put("autoriza_datos", "on".equals(request.getParameter("autoriza_datos")));
        boolean enviar = !"borrador".equals(request.getParameter("accion"));

        // 1) Los documentos se revisan antes de crear nada.
        boolean juridica = "Juridica".equals(datos.get("tipo_persona"));
        List<Integer> adjuntos = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        for (int i = 0; i < TIPOS_DOCUMENTO.size(); i++) {
            String tipo = TIPOS_DOCUMENTO.get(i);
            if (!documentoDiligenciado(request, i)) {
                if (enviar && esObligatorio(tipo, juridica)) {
                    errores.add(nombreDocumento(tipo) + ": falta adjuntarlo.");
                }
                continue;
            }
            for (String problema : revisarDocumento(request, i, tipo, enviar)) {
                errores.add(nombreDocumento(tipo) + ": " + problema);
            }
            adjuntos.add(i);
        }
        if (!errores.isEmpty()) {
            redirect.addFlashAttribute("error", "La solicitud no se envió. " + String.join(" ", errores)
                    + " Corrige eso y vuelve a adjuntar los PDF.");
            redirect.addFlashAttribute("datos", datos);
            return "redirect:/restaurante/solicitud/nueva";
        }

        // 2) Se guarda como borrador y se le suben los documentos.
        datos.put("enviar", false);
        Integer codigo;
        try {
            codigo = entero(python.crearSolicitud(datos).get("codigo_solicitud"));
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("erroresCampos", e.getCampos());
            redirect.addFlashAttribute("datos", datos);
            return "redirect:/restaurante/solicitud/nueva";
        }
        StringBuilder avisos = new StringBuilder();
        for (int i : adjuntos) {
            try {
                python.agregarDocumento(codigo, camposDocumento(request, i, TIPOS_DOCUMENTO.get(i)),
                        request.getFile("doc_archivo_" + i));
            } catch (ServicioPythonException e) {
                avisos.append(nombreDocumento(TIPOS_DOCUMENTO.get(i))).append(": ")
                      .append(e.getMensajeCompleto()).append(" ");
            }
        }

        // 3) Solo con todo guardado pasa a "Enviada" y le aparece al tecnico.
        if (!enviar) {
            redirect.addFlashAttribute("exito", "Solicitud #" + codigo
                    + " guardada como borrador. Envíala cuando tengas todos los documentos.");
            if (!avisos.isEmpty()) {
                redirect.addFlashAttribute("error", "Algunos documentos no se guardaron: " + avisos);
            }
            return "redirect:/restaurante/solicitud/" + codigo;
        }
        if (!avisos.isEmpty()) {
            redirect.addFlashAttribute("error", "La solicitud #" + codigo + " quedó como borrador porque algunos "
                    + "documentos no se guardaron: " + avisos + "Agrégalos abajo y luego usa «Completar y enviar».");
            return "redirect:/restaurante/solicitud/" + codigo;
        }
        try {
            Map<String, Object> envio = leerCampos(request);
            envio.put("enviar", true);
            python.actualizarSolicitud(codigo, envio, usuario.getCodigoUsuario());
            redirect.addFlashAttribute("exito", "Solicitud #" + codigo
                    + " enviada con sus documentos. Un técnico de Urban Eats la revisará.");
        } catch (ServicioPythonException e) {
            redirect.addFlashAttribute("error", "La solicitud #" + codigo + " quedó como borrador: " + e.getMessage());
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

    /**
     * Update: corrige los datos y (si se pide) la envia para revision.
     * Si faltan documentos obligatorios, guarda las correcciones sin enviarla, para que
     * al tecnico tampoco le llegue incompleta por este camino.
     */
    @PostMapping("/restaurante/solicitud/{id}/editar")
    public String actualizar(@PathVariable int id, HttpServletRequest request, RedirectAttributes redirect) {
        Map<String, Object> datos = leerCampos(request);
        boolean enviar = !"borrador".equals(request.getParameter("accion"));
        datos.put("enviar", enviar);
        try {
            List<String> faltan = enviar
                    ? documentosFaltantes(solicitudPropia(id), "Juridica".equals(datos.get("tipo_persona")))
                    : List.of();
            if (!faltan.isEmpty()) {
                datos.put("enviar", false);
                python.actualizarSolicitud(id, datos, SesionHelper.usuarioActual().getCodigoUsuario());
                redirect.addFlashAttribute("error", "Guardamos tus cambios, pero la solicitud aún no se envió: "
                        + "falta " + String.join(", ", faltan) + ". Agrégalo abajo y luego usa «Completar y enviar».");
                return "redirect:/restaurante/solicitud/" + id;
            }
            python.actualizarSolicitud(id, datos, SesionHelper.usuarioActual().getCodigoUsuario());
            redirect.addFlashAttribute("exito", enviar ? "Solicitud enviada. Un técnico de Urban Eats la revisará."
                    : "Solicitud actualizada.");
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
            List<String> problemas = revisarFechas(request.getParameter("fecha_expedicion"),
                    request.getParameter("fecha_vencimiento"), tipo, true);
            if (archivo != null && !archivo.isEmpty()) {
                problemas.addAll(revisarArchivo(archivo));
            }
            if (!problemas.isEmpty()) {
                redirect.addFlashAttribute("error", nombreDocumento(tipo) + ": " + String.join(" ", problemas));
                return "redirect:/restaurante/solicitud/" + id;
            }
            Map<String, String> campos = new LinkedHashMap<>();
            campos.put("tipo_documento", tipo);
            campos.put("numero_documento", request.getParameter("numero_documento"));
            campos.put("titular", request.getParameter("titular"));
            campos.put("resultado", request.getParameter("resultado"));
            campos.put("horas_capacitacion", request.getParameter("horas_capacitacion"));
            campos.put("fecha_expedicion", fechaIso(request.getParameter("fecha_expedicion")));
            campos.put("fecha_vencimiento", fechaIso(request.getParameter("fecha_vencimiento")));
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
        campos.put("fecha_expedicion", fechaIso(request.getParameter("doc_expedicion_" + i)));
        campos.put("fecha_vencimiento", fechaIso(request.getParameter("doc_vencimiento_" + i)));
        return campos;
    }

    // ---------- Revision de documentos (el servicio Python repite estas reglas) ----------

    /** Acepta dd/mm/aaaa (el formulario) o aaaa-mm-dd. Devuelve null si la fecha no existe. */
    static LocalDate leerFecha(String texto) {
        if (vacio(texto)) {
            return null;
        }
        String limpio = texto.trim();
        try {
            return limpio.contains("/") ? LocalDate.parse(limpio, FECHA_DD_MM_AAAA) : LocalDate.parse(limpio);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** El servicio Python recibe las fechas en ISO (aaaa-mm-dd). */
    static String fechaIso(String texto) {
        LocalDate fecha = leerFecha(texto);
        if (fecha != null) {
            return fecha.toString();
        }
        return vacio(texto) ? null : texto.trim();
    }

    /**
     * Fechas de un documento. Con exigirVigencia (al enviar) tambien rechaza documentos
     * vencidos y certificados de Camara de Comercio de mas de 30 dias, que el tecnico no aprobaria.
     */
    private List<String> revisarFechas(String textoExpedicion, String textoVencimiento, String tipo,
                                       boolean exigirVigencia) {
        List<String> problemas = new ArrayList<>();
        LocalDate hoy = LocalDate.now(ZONA);
        LocalDate expedicion = leerFecha(textoExpedicion);
        if (vacio(textoExpedicion)) {
            problemas.add("falta la fecha de expedición.");
        } else if (expedicion == null) {
            problemas.add("la fecha de expedición no existe (usa dd/mm/aaaa).");
        } else if (expedicion.isAfter(hoy)) {
            problemas.add("la fecha de expedición no puede ser futura.");
        } else if (exigirVigencia && CAMARA.equals(tipo) && expedicion.isBefore(hoy.minusDays(DIAS_CAMARA))) {
            problemas.add("debe estar expedido hace " + DIAS_CAMARA + " días o menos.");
        }
        if (!vacio(textoVencimiento)) {
            LocalDate vencimiento = leerFecha(textoVencimiento);
            if (vencimiento == null) {
                problemas.add("la fecha de vencimiento no existe (usa dd/mm/aaaa).");
            } else if (expedicion != null && !vencimiento.isAfter(expedicion)) {
                problemas.add("el vencimiento debe ser posterior a la expedición.");
            } else if (exigirVigencia && vencimiento.isBefore(hoy)) {
                problemas.add("el documento ya está vencido.");
            }
        }
        return problemas;
    }

    /** Un documento del formulario de creacion. Al enviar, debe ir con su PDF. */
    private List<String> revisarDocumento(MultipartHttpServletRequest request, int i, String tipo, boolean enviar) {
        List<String> problemas = revisarFechas(request.getParameter("doc_expedicion_" + i),
                request.getParameter("doc_vencimiento_" + i), tipo, enviar);
        MultipartFile archivo = request.getFile("doc_archivo_" + i);
        boolean conArchivo = archivo != null && !archivo.isEmpty();
        if (conArchivo) {
            problemas.addAll(revisarArchivo(archivo));
        } else if (enviar) {
            problemas.add("falta el PDF.");
        }
        if ((MANIPULACION.equals(tipo) || MEDICO.equals(tipo)) && vacio(request.getParameter("doc_titular_" + i))) {
            problemas.add("falta el nombre del manipulador.");
        }
        if (MANIPULACION.equals(tipo)) {
            Integer horas = enteroOpcional(request.getParameter("doc_horas_" + i));
            if (horas == null || horas <= 0) {
                problemas.add("faltan las horas del curso.");
            } else if (enviar && horas < HORAS_MINIMAS) {
                problemas.add("el curso debe ser de " + HORAS_MINIMAS + " horas o más.");
            }
        }
        return problemas;
    }

    private static List<String> revisarArchivo(MultipartFile archivo) {
        List<String> problemas = new ArrayList<>();
        String nombre = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename().toLowerCase();
        if (!nombre.endsWith(".pdf") && !"application/pdf".equals(archivo.getContentType())) {
            problemas.add("el archivo debe ser PDF.");
        }
        if (archivo.getSize() > MAX_PDF) {
            problemas.add("el PDF pesa más de 5 MB.");
        }
        return problemas;
    }

    /** True si el usuario escribio algo o adjunto un archivo en ese documento. */
    private static boolean documentoDiligenciado(MultipartHttpServletRequest request, int i) {
        for (String campo : List.of("doc_numero_", "doc_titular_", "doc_horas_", "doc_expedicion_", "doc_vencimiento_")) {
            if (!vacio(request.getParameter(campo + i))) {
                return true;
            }
        }
        MultipartFile archivo = request.getFile("doc_archivo_" + i);
        return archivo != null && !archivo.isEmpty();
    }

    /** La Camara de Comercio solo se pide a personas juridicas; los demas, a todos. */
    private static boolean esObligatorio(String tipo, boolean juridica) {
        return !CAMARA.equals(tipo) || juridica;
    }

    /** Documentos obligatorios que la solicitud aun no tiene (los rechazados no cuentan). */
    private static List<String> documentosFaltantes(Map<String, Object> solicitud, boolean juridica) {
        Set<String> cargados = new HashSet<>();
        if (solicitud.get("documentos") instanceof List<?> documentos) {
            for (Object item : documentos) {
                if (item instanceof Map<?, ?> documento && !"Rechazado".equals(documento.get("estado_revision"))) {
                    cargados.add(String.valueOf(documento.get("tipo_documento")));
                }
            }
        }
        List<String> faltan = new ArrayList<>();
        for (String tipo : TIPOS_DOCUMENTO) {
            if (esObligatorio(tipo, juridica) && !cargados.contains(tipo)) {
                faltan.add(nombreDocumento(tipo));
            }
        }
        return faltan;
    }

    private static String nombreDocumento(String tipo) {
        if (tipo == null) {
            return "Documento";
        }
        return switch (tipo) {
            case CAMARA -> "Cámara de Comercio";
            case MANIPULACION -> "Certificado de manipulación";
            case MEDICO -> "Certificado médico";
            default -> tipo;
        };
    }

    private static boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private static Integer enteroOpcional(String valor) {
        try {
            return vacio(valor) ? null : Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static Integer entero(Object valor) {
        return valor == null ? null : Integer.valueOf(String.valueOf(valor).replace(".0", ""));
    }
}
