package com.urbaneats.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.urbaneats.dao.PagoRepository;
import com.urbaneats.dao.TransaccionRepository;
import com.urbaneats.entity.Cliente;
import com.urbaneats.entity.Pago;
import com.urbaneats.entity.Transaccion;
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
 * Aqui vive la integracion con MercadoPago y el armado del envio/pedido tras un pago exitoso,
 * tal como lo hacia PagoController::iniciarPago en la rama php-Laravel.
 */
@Service
public class PagoService implements IPagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    /** Estados de MercadoPago que se consideran suficientes para armar el pedido. */
    private static final List<String> ESTADOS_EXITOSOS = List.of("approved", "in_process", "authorized");

    private static final Map<String, String> MENSAJES_ERROR = Map.of(
            "cc_rejected_insufficient_amount", "Saldo insuficiente en la tarjeta.",
            "cc_rejected_bad_filled_card_number", "Número de tarjeta incorrecto.",
            "cc_rejected_bad_filled_date", "Fecha de vencimiento incorrecta.",
            "cc_rejected_bad_filled_other", "Datos de la tarjeta incorrectos.",
            "cc_rejected_high_risk", "Tu pago fue rechazado por seguridad.",
            "rejected_by_bank", "Pago rechazado por el banco."
    );

    private final PagoRepository pagoRepository;
    private final TransaccionRepository transaccionRepository;
    private final PedidoDesdeCarritoService pedidoDesdeCarritoService;

    private final String accessToken;

    public PagoService(PagoRepository pagoRepository,
                        TransaccionRepository transaccionRepository,
                        PedidoDesdeCarritoService pedidoDesdeCarritoService,
                        @Value("${mercadopago.access-token}") String accessToken) {
        this.pagoRepository = pagoRepository;
        this.transaccionRepository = transaccionRepository;
        this.pedidoDesdeCarritoService = pedidoDesdeCarritoService;
        this.accessToken = accessToken;
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
    public ResultadoPago iniciarPago(Cliente cliente, Map<String, Object> datosPago, List<Map<String, Object>> items) {
        BigDecimal monto = toBigDecimal(datosPago.get("transaction_amount"));

        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setMonto(monto);
        pago.setEstadoPago("pending");
        pago.setFechaPago(LocalDate.now());
        pago.setHoraPago(LocalTime.now());
        pago = pagoRepository.save(pago);

        boolean mpFallback = false;
        String paymentStatus = "in_process";
        Long paymentId = null;
        String paymentStatusDetail = null;

        try {
            Payment payment = crearPagoEnMercadoPago(datosPago, monto);
            paymentStatus = payment.getStatus();
            paymentId = payment.getId();
            paymentStatusDetail = payment.getStatusDetail();

            pago.setEstadoPago(paymentStatus);
            pagoRepository.save(pago);

            Transaccion transaccion = new Transaccion();
            transaccion.setTransaccionID(String.valueOf(payment.getId()));
            transaccion.setPago(pago);
            transaccion.setMetodoPago(payment.getPaymentMethodId());
            transaccion.setCodigoRespuesta(payment.getStatusDetail());
            transaccionRepository.save(transaccion);
        } catch (Exception e) {
            log.error("Error al procesar el pago en MercadoPago: {}", e.getMessage(), e);
            mpFallback = true;
            pago.setEstadoPago("in_process");
            pagoRepository.save(pago);
        }

        String estadoFinal = mpFallback ? "in_process" : paymentStatus;
        Integer codigoPedido = null;

        if ((mpFallback || ESTADOS_EXITOSOS.contains(paymentStatus)) && items != null && !items.isEmpty()) {
            Integer codigoRestaurante = pedidoDesdeCarritoService.resolverCodigoRestaurante(items);
            if (codigoRestaurante != null) {
                try {
                    codigoPedido = pedidoDesdeCarritoService.crear(cliente, codigoRestaurante, items, pago);
                } catch (Exception e) {
                    log.error("Error creando pedido tras pago: {}", e.getMessage(), e);
                    return new ResultadoPago(estadoFinal, paymentId,
                            "Tu pago fue procesado pero hubo un error registrando el pedido. Contacta soporte.", null);
                }
            }
        }

        String mensajeError = MENSAJES_ERROR.get(paymentStatusDetail);
        return new ResultadoPago(estadoFinal, paymentId, mensajeError, codigoPedido);
    }

    private Payment crearPagoEnMercadoPago(Map<String, Object> datosPago, BigDecimal monto) throws MPException, com.mercadopago.exceptions.MPApiException {
        MercadoPagoConfig.setAccessToken(accessToken);

        PaymentClient client = new PaymentClient();
        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(Map.of("X-Idempotency-Key", UUID.randomUUID().toString()))
                .build();

        PaymentCreateRequest.PaymentCreateRequestBuilder builder = PaymentCreateRequest.builder()
                .transactionAmount(monto)
                .description("pedido Urban Eats")
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

    private BigDecimal toBigDecimal(Object valor) {
        return valor != null ? new BigDecimal(String.valueOf(valor)) : BigDecimal.ZERO;
    }

    private String asString(Object valor) {
        return valor != null ? String.valueOf(valor) : null;
    }
}
