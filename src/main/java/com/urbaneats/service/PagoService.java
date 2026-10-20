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

    private static final Map<String, String> MENSAJES_ERROR = Map.of(
            "cc_rejected_insufficient_amount", "Saldo insuficiente en la tarjeta.",
            "cc_rejected_bad_filled_card_number", "Número de tarjeta incorrecto.",
            "cc_rejected_bad_filled_date", "Fecha de vencimiento incorrecta.",
            "cc_rejected_bad_filled_security_code", "Código de seguridad incorrecto.",
            "cc_rejected_bad_filled_other", "Datos de la tarjeta incorrectos.",
            "cc_rejected_high_risk", "Tu pago fue rechazado por seguridad.",
            "cc_rejected_call_for_authorize", "Debes autorizar el pago con tu banco.",
            "rejected_by_bank", "Pago rechazado por el banco."
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
                       @Value("${urbaneats.pagos.modo-demo:false}") boolean pagosModoDemo) {
        this.pagoRepository = pagoRepository;
        this.transaccionRepository = transaccionRepository;
        this.direccionRepository = direccionRepository;
        this.pedidoDesdeCarritoService = pedidoDesdeCarritoService;
        this.accessToken = accessToken;
        this.pagosModoDemo = pagosModoDemo;
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
                log.error("MercadoPago rechazo la solicitud ({}): {}", e.getStatusCode(),
                        e.getApiResponse() != null ? e.getApiResponse().getContent() : e.getMessage());
                return marcarFallido(pago, "MercadoPago no pudo procesar el pago. Revisa los datos e intenta de nuevo.");
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
