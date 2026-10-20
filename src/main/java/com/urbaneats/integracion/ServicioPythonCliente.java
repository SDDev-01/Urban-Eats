package com.urbaneats.integracion;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriBuilder;

/**
 * Cliente HTTP del servicio Python (FastAPI). Es el UNICO lugar de Java que le habla.
 *
 * - La URL y la clave salen del .env (PYTHON_SERVICE_URL y PYTHON_API_KEY).
 * - La clave viaja en el header X-API-KEY: el navegador nunca la ve, porque el
 *   navegador solo habla con Spring Boot y Spring Boot habla con Python.
 * - Si Python responde con error, se lanza ServicioPythonException con su mensaje.
 *   Si no responde, el mensaje dice que el servicio no esta disponible.
 */
@Component
public class ServicioPythonCliente {

    private static final Logger log = LoggerFactory.getLogger(ServicioPythonCliente.class);
    private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA = new ParameterizedTypeReference<>() {};

    private final RestClient rest;

    public ServicioPythonCliente(@Value("${urbaneats.python.url}") String url,
                                 @Value("${urbaneats.python.api-key}") String apiKey) {
        // Cliente HTTP de Java 11+ (soporta PATCH, a diferencia de HttpURLConnection).
        java.net.http.HttpClient http = java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_1_1) // uvicorn habla HTTP/1.1
                .connectTimeout(Duration.ofSeconds(3)).build();
        JdkClientHttpRequestFactory tiempos = new JdkClientHttpRequestFactory(http);
        tiempos.setReadTimeout(Duration.ofSeconds(130)); // la IA local en CPU puede tardar
        this.rest = RestClient.builder()
                .baseUrl(url)
                .defaultHeader("X-API-KEY", apiKey)
                .requestFactory(tiempos)
                .build();
    }

    // ===================== SOLICITUDES DE COCINAS =====================

    public Map<String, Object> crearSolicitud(Map<String, Object> datos) {
        return ejecutar(() -> rest.post().uri("/api/solicitudes").contentType(MediaType.APPLICATION_JSON)
                .body(datos).retrieve().body(MAPA));
    }

    public Map<String, Object> listarSolicitudes(MultiValueMap<String, String> filtros) {
        return ejecutar(() -> rest.get().uri(b -> conParametros(b.path("/api/solicitudes"), filtros))
                .retrieve().body(MAPA));
    }

    public Map<String, Object> obtenerSolicitud(int codigo) {
        return ejecutar(() -> rest.get().uri("/api/solicitudes/{c}", codigo).retrieve().body(MAPA));
    }

    public Map<String, Object> actualizarSolicitud(int codigo, Map<String, Object> datos, int codigoUsuario) {
        return ejecutar(() -> rest.put().uri("/api/solicitudes/{c}?codigo_usuario={u}", codigo, codigoUsuario)
                .contentType(MediaType.APPLICATION_JSON).body(datos).retrieve().body(MAPA));
    }

    public void eliminarSolicitud(int codigo, Integer codigoUsuario) {
        ejecutar(() -> rest.delete().uri(b -> {
            b.path("/api/solicitudes/{c}");
            if (codigoUsuario != null) {
                b.queryParam("codigo_usuario", codigoUsuario);
            }
            return b.build(codigo);
        }).retrieve().toBodilessEntity());
    }

    public Map<String, Object> cambiarEstado(int codigo, Map<String, Object> cambio) {
        return ejecutar(() -> rest.patch().uri("/api/solicitudes/{c}/estado", codigo)
                .contentType(MediaType.APPLICATION_JSON).body(cambio).retrieve().body(MAPA));
    }

    public Map<String, Object> cumplimiento(int codigo) {
        return ejecutar(() -> rest.get().uri("/api/solicitudes/{c}/cumplimiento", codigo).retrieve().body(MAPA));
    }

    /** Envia un documento con su PDF (multipart), igual que un formulario con archivo. */
    public Map<String, Object> agregarDocumento(int codigoSolicitud, Map<String, String> campos, MultipartFile archivo) {
        MultiValueMap<String, Object> partes = new LinkedMultiValueMap<>();
        campos.forEach((clave, valor) -> {
            if (valor != null && !valor.isBlank()) {
                partes.add(clave, valor);
            }
        });
        if (archivo != null && !archivo.isEmpty()) {
            try {
                byte[] bytes = archivo.getBytes();
                String nombre = archivo.getOriginalFilename();
                partes.add("archivo", new ByteArrayResource(bytes) {
                    @Override
                    public String getFilename() {
                        return nombre;
                    }
                });
            } catch (java.io.IOException e) {
                throw new ServicioPythonException("No se pudo leer el archivo adjunto.", 400, null);
            }
        }
        return ejecutar(() -> rest.post().uri("/api/solicitudes/{c}/documentos", codigoSolicitud)
                .contentType(MediaType.MULTIPART_FORM_DATA).body(partes).retrieve().body(MAPA));
    }

    public ResponseEntity<byte[]> descargarDocumento(int codigoDocumento) {
        return ejecutar(() -> rest.get().uri("/api/documentos/{d}/archivo", codigoDocumento)
                .retrieve().toEntity(byte[].class));
    }

    public Map<String, Object> revisarDocumento(int codigoDocumento, Map<String, Object> revision) {
        return ejecutar(() -> rest.patch().uri("/api/documentos/{d}", codigoDocumento)
                .contentType(MediaType.APPLICATION_JSON).body(revision).retrieve().body(MAPA));
    }

    // ===================== REPORTES =====================

    /** Reporte en JSON para pintarlo en pantalla. nombre: pedidos, ventas-por-cocina, pedidos-por-hora... */
    public Map<String, Object> reporte(String nombre, MultiValueMap<String, String> filtros) {
        return ejecutar(() -> rest.get().uri(b -> conParametros(b.path("/api/reportes/" + nombre), filtros))
                .retrieve().body(MAPA));
    }

    /** Reporte como archivo (csv, xlsx o pdf) para descargar. */
    public ResponseEntity<byte[]> reporteArchivo(String nombre, MultiValueMap<String, String> filtros) {
        return ejecutar(() -> rest.get().uri(b -> conParametros(b.path("/api/reportes/" + nombre), filtros))
                .retrieve().toEntity(byte[].class));
    }

    // ===================== CHAT Y RUTAS =====================

    public Map<String, Object> chat(Map<String, Object> entrada) {
        return ejecutar(() -> rest.post().uri("/api/chat").contentType(MediaType.APPLICATION_JSON)
                .body(entrada).retrieve().body(MAPA));
    }

    public Map<String, Object> ruta(int codigoEnvio) {
        return ejecutar(() -> rest.get().uri("/api/rutas/envio/{e}", codigoEnvio).retrieve().body(MAPA));
    }

    public Map<String, Object> salud() {
        return ejecutar(() -> rest.get().uri("/api/salud").retrieve().body(MAPA));
    }

    // ===================== AUXILIARES =====================

    private java.net.URI conParametros(UriBuilder builder, MultiValueMap<String, String> filtros) {
        if (filtros != null) {
            filtros.forEach((clave, valores) -> valores.stream()
                    .filter(valor -> valor != null && !valor.isBlank())
                    .forEach(valor -> builder.queryParam(clave, valor)));
        }
        return builder.build();
    }

    /** Ejecuta la llamada y traduce cualquier error a ServicioPythonException con un mensaje claro. */
    @SuppressWarnings("unchecked")
    private <T> T ejecutar(java.util.function.Supplier<T> llamada) {
        try {
            return llamada.get();
        } catch (RestClientResponseException e) {
            Map<String, Object> cuerpo = null;
            try {
                cuerpo = e.getResponseBodyAs(Map.class);
            } catch (RuntimeException ignorada) {
                // el cuerpo no era JSON
            }
            String mensaje = cuerpo != null && cuerpo.get("error") != null
                    ? String.valueOf(cuerpo.get("error"))
                    : "El servicio Python respondió con error " + e.getStatusCode().value() + ".";
            Map<String, Object> campos = cuerpo != null && cuerpo.get("campos") instanceof Map<?, ?> mapa
                    ? (Map<String, Object>) mapa : null;
            throw new ServicioPythonException(mensaje, e.getStatusCode().value(), campos);
        } catch (ResourceAccessException e) {
            log.warn("El servicio Python no responde: {}", e.getMessage());
            throw new ServicioPythonException(
                    "El servicio Python no está disponible. Inícielo con: python main.py", 503, null);
        }
    }
}
