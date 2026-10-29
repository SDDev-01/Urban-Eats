package com.urbaneats.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.urbaneats.dao.DireccionRepository;
import com.urbaneats.dao.PagoRepository;
import com.urbaneats.dao.TransaccionRepository;
import com.urbaneats.entity.Cliente;
import com.urbaneats.entity.Direccion;
import com.urbaneats.entity.Pago;
import com.urbaneats.entity.Pedido;
import com.urbaneats.entity.Transaccion;
import com.urbaneats.service.PedidoDesdeCarritoService.CarritoValidado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementacion de la capa de servicio para Pago.
 *
 * Regla de negocio: PAGO ANTES DEL PEDIDO. El pedido solo se crea cuando el pago
 * queda "approved". Si MercadoPago rechaza o falla, no se crea nada y el usuario
 * ve el motivo (antes se creaba el pedido igual, aunque el pago hubiera fallado).
 *
 * Modo demo (PAGOS_MODO_DEMO=true en el .env): no llama a MercadoPago y aprueba el
 * pago localmente con una transaccion "DEMO-...". Sirve para probar y exponer el
 * flujo completo sin llaves de MercadoPago; en pantalla se avisa que es simulado.
 */
@Service
public class PagoService implements IPagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private static final String APROBADO = "approved";

    // Motivos de rechazo de MercadoPago (status_detail). En pruebas se provocan con el
    // nombre del titular: OTHE, FUND, SECU, EXPI, CALL, LOCK, DUPL... (APRO = aprobado).
    private static final Map<String, String> MENSAJES_ERROR = Map.ofEntries(
            Map.entry("cc_rejected_insufficient_amount", "Saldo insuficiente en la tarjeta."),
            Map.entry("cc_rejected_bad_filled_card_number", "Número de tarjeta incorrecto."),
            Map.entry("cc_rejected_bad_filled_date", "Fecha de vencimiento incorrecta."),
            Map.entry("cc_rejected_bad_filled_security_code", "Código de seguridad incorrecto."),
            Map.entry("cc_rejected_bad_filled_other", "Datos de la tarjeta incorrectos."),
            Map.entry("cc_rejected_high_risk", "Tu pago fue rechazado por seguridad."),
            Map.entry("cc_rejected_call_for_authorize", "Debes autorizar el pago con tu banco."),
            Map.entry("cc_rejected_card_disabled", "La tarjeta está deshabilitada. Llama a tu banco o usa otra."),
            Map.entry("cc_rejected_duplicated_payment", "Ya hiciste un pago igual hace un momento. Usa otra tarjeta si necesitas pagar de nuevo."),
            Map.entry("cc_rejected_max_attempts", "Superaste los intentos permitidos. Usa otra tarjeta."),
            Map.entry("cc_rejected_blacklist", "No pudimos procesar el pago con esta tarjeta."),
            Map.entry("cc_rejected_invalid_installments", "La tarjeta no acepta ese número de cuotas."),
            Map.entry("cc_rejected_other_reason", "La tarjeta rechazó el pago. Usa otra tarjeta."),
            Map.entry("rejected_by_bank", "Pago rechazado por el banco.")
    );

    private final PagoRepository pagoRepository;
    private final TransaccionRepository transaccionRepository;
    private final DireccionRepository direccionRepository;
    private final PedidoDesdeCarritoService pedidoDesdeCarritoService;

    private final String accessToken;
    private final boolean pagosModoDemo;

    public PagoService(PagoRepository pagoRepository,
                       TransaccionRepository transaccionRepository,
                       DireccionRepository direccionRepository,
                       PedidoDesdeCarritoService pedidoDesdeCarritoService,
                       @Value("${mercadopago.access-token:}") String accessToken,
                       @Value("${mercadopago.public-key:}") String publicKey,
                       @Value("${urbaneats.pagos.modo-demo:false}") boolean pagosModoDemo) {
        this.pagoRepository = pagoRepository;
        this.transaccionRepository = transaccionRepository;
        this.direccionRepository = direccionRepository;
        this.pedidoDesdeCarritoService = pedidoDesdeCarritoService;
        this.accessToken = accessToken;
        this.pagosModoDemo = pagosModoDemo;
        avisarSiFaltanCredenciales(publicKey);
    }

    /** Aviso en la consola de Spring si los pagos reales estan activos sin credenciales validas. */
    private void avisarSiFaltanCredenciales(String publicKey) {
        if (pagosModoDemo) {
            log.info("Pagos en modo demostración (PAGOS_MODO_DEMO=true): no se usa MercadoPago.");
            return;
        }
        if (!credencialValida(accessToken) || !credencialValida(publicKey)) {
            log.warn("PAGOS_MODO_DEMO=false pero faltan MERCADOPAGO_PUBLIC_KEY o MERCADOPAGO_ACCESS_TOKEN "
                    + "(deben empezar con TEST- o APP_USR-). La página de pago mostrará un error.");
        } else if (accessToken.startsWith("TEST-")) {
            log.info("MercadoPago en modo prueba (credenciales TEST-): usa las tarjetas de prueba.");
        }
    }

    private static boolean credencialValida(String credencial) {
        return credencial != null && (credencial.startsWith("TEST-") || credencial.startsWith("APP_USR-"));
    }

    @Override
    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    @Override
    public Optional<Pago> buscarPorId(Integer id) {
        return pagoRepository.findById(id);
    }

    @Override
    public Pago guardar(Pago pago) {
        return pagoRepository.save(pago);
    }

    @Override
    public Pago actualizar(Integer id, Pago pago) {
        if (pagoRepository.existsById(id)) {
            pago.setCodigoPago(id);
            return pagoRepository.save(pago);
        }
        return null;
    }

    @Override
    public void eliminar(Integer id) {
        pagoRepository.deleteById(id);
    }

    @Override
    public ResultadoPago iniciarPago(Cliente cliente, Integer codigoUsuario, Map<String, Object> datosPago,
                                     List<Map<String, Object>> items, Integer codigoDireccion) {
        // 1) El carrito se valida en el servidor: precios de la base, una sola cocina,
        //    platos disponibles. Si algo falla, lanza ReglaNegocioException.
        CarritoValidado carrito = pedidoDesdeCarritoService.validarCarrito(items);
        Direccion direccion = resolverDireccion(codigoUsuario, codigoDireccion);

        if (!pagosModoDemo && (accessToken == null || accessToken.isBlank())) {
            return new ResultadoPago("rejected", null,
                    "La pasarela no está configurada: faltan las credenciales de prueba de MercadoPago en el .env.", null);
        }

        // 2) Se registra el intento de pago como pendiente.
        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setMonto(carrito.total());
        pago.setEstadoPago("pending");
        pago.setFechaPago(LocalDate.now());
        pago.setHoraPago(LocalTime.now());
        pago = pagoRepository.save(pago);

        // 3) Se cobra: en modo demo se aprueba localmente; si no, se llama a MercadoPago.
        String estado;
        String detalle = null;
        Long idMercadoPago = null;
        Transaccion transaccion = new Transaccion();
        transaccion.setPago(pago);

        if (pagosModoDemo) {
            estado = APROBADO;
            transaccion.setTransaccionID("DEMO-" + UUID.randomUUID().toString().substring(0, 18));
            transaccion.setMetodoPago("demo");
            transaccion.setCodigoRespuesta("accredited");
        } else {
            try {
                Payment payment = crearPagoEnMercadoPago(datosPago, carrito.total());
                estado = payment.getStatus();
                detalle = payment.getStatusDetail();
                idMercadoPago = payment.getId();
                transaccion.setTransaccionID(String.valueOf(payment.getId()));
                transaccion.setMetodoPago(payment.getPaymentMethodId());
                transaccion.setCodigoRespuesta(payment.getStatusDetail());
            } catch (MPApiException e) {
                String contenido = e.getApiResponse() != null ? e.getApiResponse().getContent() : e.getMessage();
                log.error("MercadoPago rechazo la solicitud ({}): {}", e.getStatusCode(), contenido);
                return marcarFallido(pago, mensajeDeMercadoPago(e.getStatusCode(), contenido));
            } catch (MPException | RuntimeException e) {
                log.error("Error comunicandose con MercadoPago: {}", e.getMessage(), e);
                return marcarFallido(pago, "No pudimos comunicarnos con MercadoPago. Intenta de nuevo en un momento.");
            }
        }

        pago.setEstadoPago(estado);
        pagoRepository.save(pago);
        transaccionRepository.save(transaccion);

        // 4) Solo con el pago aprobado se crea el pedido.
        if (!APROBADO.equals(estado)) {
            return new ResultadoPago(estado, idMercadoPago, mensajePara(estado, detalle), null);
        }
        try {
            Pedido pedido = pedidoDesdeCarritoService.crear(cliente, carrito, direccion, pago, codigoUsuario);
            return new ResultadoPago(APROBADO, idMercadoPago, null, pedido.getCodigoPedido());
        } catch (RuntimeException e) {
            log.error("El pago {} fue aprobado pero fallo al crear el pedido", pago.getCodigoPago(), e);
            return new ResultadoPago(APROBADO, idMercadoPago,
                    "Tu pago fue aprobado pero no pudimos registrar el pedido. Contacta a soporte con el pago #"
                            + pago.getCodigoPago() + ".", null);
        }
    }

    /** True si los pagos estan en modo demostracion (sin MercadoPago). */
    public boolean isPagosModoDemo() {
        return pagosModoDemo;
    }

    /** La direccion elegida, solo si es del usuario; si no, la primera que tenga. */
    private Direccion resolverDireccion(Integer codigoUsuario, Integer codigoDireccion) {
        List<Direccion> direcciones = direccionRepository.findByUsuario_CodigoUsuario(codigoUsuario);
        if (direcciones.isEmpty()) {
            throw new ReglaNegocioException("Agrega una dirección de entrega en tu perfil antes de pagar.");
        }
        return direcciones.stream()
                .filter(direccion -> direccion.getCodigoDireccion().equals(codigoDireccion))
                .findFirst()
                .orElse(direcciones.get(0));
    }

    private ResultadoPago marcarFallido(Pago pago, String mensaje) {
        pago.setEstadoPago("rejected");
        pagoRepository.save(pago);
        return new ResultadoPago("rejected", null, mensaje, null);
    }

    /** Traduce los errores tipicos de MercadoPago (sobre todo en pruebas) a algo que el usuario pueda corregir. */
    static String mensajeDeMercadoPago(int codigoHttp, String contenido) {
        String texto = contenido == null ? "" : contenido.toLowerCase();
        if (codigoHttp == 401 || texto.contains("unauthorized") || texto.contains("invalid access token")) {
            return "Las credenciales de MercadoPago del .env no son válidas. Revisa MERCADOPAGO_ACCESS_TOKEN "
                    + "(credenciales de prueba TEST-).";
        }
        if (texto.contains("invalid users involved") || texto.contains("2034")) {
            return "MercadoPago no acepta ese correo de pagador: usa uno distinto al de la cuenta vendedora "
                    + "(en pruebas sirve cualquier correo, por ejemplo comprador.prueba@gmail.com).";
        }
        if (texto.contains("card_token") || texto.contains("card token") || texto.contains("3003") || texto.contains("2006")) {
            return "Los datos de la tarjeta vencieron en el formulario. Vuelve a escribirlos e intenta de nuevo.";
        }
        if (texto.contains("identification")) {
            return "El documento del pagador no es válido. En pruebas usa CC 123456789.";
        }
        if (texto.contains("email")) {
            return "Revisa el correo del pagador.";
        }
        return "MercadoPago no pudo procesar el pago. Revisa los datos e intenta de nuevo.";
    }

    private String mensajePara(String estado, String detalle) {
        if ("in_process".equals(estado) || "pending".equals(estado)) {
            return "Tu pago quedó en revisión. Cuando MercadoPago lo apruebe podrás pedir; intenta con otra tarjeta si tienes prisa.";
        }
        return MENSAJES_ERROR.getOrDefault(detalle, "Tu pago fue rechazado. Intenta con otro medio de pago.");
    }

    private Payment crearPagoEnMercadoPago(Map<String, Object> datosPago, BigDecimal monto) throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken(accessToken);

        PaymentClient client = new PaymentClient();
        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(Map.of("X-Idempotency-Key", UUID.randomUUID().toString()))
                .build();

        PaymentCreateRequest.PaymentCreateRequestBuilder builder = PaymentCreateRequest.builder()
                .transactionAmount(monto) // el total calculado en el servidor, no el del navegador
                .description("Pedido Urban Eats")
                .token(asString(datosPago.get("token")))
                .paymentMethodId(asString(datosPago.get("payment_method_id")))
                .issuerId(asString(datosPago.get("issuer_id")))
                .installments(datosPago.get("installments") != null
                        ? Integer.valueOf(String.valueOf(datosPago.get("installments"))) : 1)
                .payer(construirPayer(datosPago));

        return client.create(builder.build(), requestOptions);
    }

    @SuppressWarnings("unchecked")
    private PaymentPayerRequest construirPayer(Map<String, Object> datosPago) {
        Object payerObj = datosPago.get("payer");
        if (!(payerObj instanceof Map)) {
            return PaymentPayerRequest.builder().build();
        }
        Map<String, Object> payer = (Map<String, Object>) payerObj;

        PaymentPayerRequest.PaymentPayerRequestBuilder builder = PaymentPayerRequest.builder()
                .email(asString(payer.get("email")));

        Object identificacionObj = payer.get("identification");
        if (identificacionObj instanceof Map) {
            Map<String, Object> identificacion = (Map<String, Object>) identificacionObj;
            builder.identification(IdentificationRequest.builder()
                    .type(asString(identificacion.get("type")))
                    .number(asString(identificacion.get("number")))
                    .build());
        }

        return builder.build();
    }

    private String asString(Object valor) {
        return valor != null ? String.valueOf(valor) : null;
    }
}
