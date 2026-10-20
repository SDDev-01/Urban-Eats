package com.urbaneats.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urbaneats.dao.EnvioRepository;
import com.urbaneats.dao.HistorialPedidoRepository;
import com.urbaneats.dao.PedidoRepository;
import com.urbaneats.dao.UsuarioRepository;
import com.urbaneats.entity.Envio;
import com.urbaneats.entity.EstadoPedido;
import com.urbaneats.entity.HistorialPedido;
import com.urbaneats.entity.Pedido;
import com.urbaneats.entity.Repartidor;
import com.urbaneats.entity.Restaurante;

/**
 * Unico lugar donde cambia el estado de un pedido. Asi las reglas no quedan
 * repartidas por los controladores.
 *
 *   Cliente:    Solicitado -> Cancelado (solo si la cocina aun no lo acepta)
 *   Cocina:     Solicitado -> En preparacion (aceptar) o -> Cancelado (rechazar)
 *               En preparacion -> Listo para envio
 *   Repartidor: Listo para envio -> En camino (tomar)
 *               En camino -> Listo para envio (liberar: devuelve el pedido)
 *               En camino -> Entregado
 *
 * Cada cambio deja una fila en historial_pedido: quien, de que estado a cual y cuando.
 */
@Service
public class PedidoEstadoService {

    private final PedidoRepository pedidoRepository;
    private final EnvioRepository envioRepository;
    private final HistorialPedidoRepository historialRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoEstadoService(PedidoRepository pedidoRepository,
                               EnvioRepository envioRepository,
                               HistorialPedidoRepository historialRepository,
                               UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.envioRepository = envioRepository;
        this.historialRepository = historialRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ===================== COCINA =====================

    @Transactional
    public Pedido aceptar(Integer codigoPedido, Restaurante cocina, Integer codigoUsuario) {
        Pedido pedido = pedidoDeLaCocina(codigoPedido, cocina);
        cambiar(pedido, EstadoPedido.SOLICITADO, EstadoPedido.EN_PREPARACION, codigoUsuario,
                "La cocina aceptó el pedido");
        return pedido;
    }

    @Transactional
    public Pedido rechazar(Integer codigoPedido, Restaurante cocina, Integer codigoUsuario, String motivo) {
        Pedido pedido = pedidoDeLaCocina(codigoPedido, cocina);
        String comentario = "Rechazado por la cocina: "
                + (motivo == null || motivo.isBlank() ? "sin motivo" : recortar(motivo.trim(), 250));
        cambiar(pedido, EstadoPedido.SOLICITADO, EstadoPedido.CANCELADO, codigoUsuario, comentario);
        return pedido;
    }

    @Transactional
    public Pedido marcarListo(Integer codigoPedido, Restaurante cocina, Integer codigoUsuario) {
        Pedido pedido = pedidoDeLaCocina(codigoPedido, cocina);
        cambiar(pedido, EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO_PARA_ENVIO, codigoUsuario,
                "Listo para que un repartidor lo recoja");
        return pedido;
    }

    // ===================== REPARTIDOR =====================

    @Transactional
    public Pedido tomar(Integer codigoEnvio, Repartidor repartidor, Integer codigoUsuario) {
        Envio envio = buscarEnvio(codigoEnvio);
        Pedido pedido = envio.getPedido();
        if (pedido == null || pedido.getEstado() != EstadoPedido.LISTO_PARA_ENVIO || envio.getRepartidor() != null) {
            throw new ReglaNegocioException("Ese pedido ya no está disponible.");
        }

        // Sentencia atomica: si otro repartidor lo tomo un instante antes, cambia 0 filas.
        int filas = envioRepository.asignarRepartidorSiLibre(codigoEnvio, repartidor.getCodigoRepartidor());
        if (filas == 0) {
            throw new ReglaNegocioException("Otro repartidor tomó este pedido primero.");
        }

        // El UPDATE limpio la memoria de JPA, por eso se vuelve a leer el pedido.
        Pedido actualizado = pedidoRepository.findById(pedido.getCodigoPedido()).orElseThrow();
        cambiar(actualizado, EstadoPedido.LISTO_PARA_ENVIO, EstadoPedido.EN_CAMINO, codigoUsuario,
                "Repartidor asignado: va hacia la cocina y luego al cliente");
        return actualizado;
    }

    @Transactional
    public Pedido liberar(Integer codigoEnvio, Repartidor repartidor, Integer codigoUsuario) {
        Envio envio = envioDelRepartidor(codigoEnvio, repartidor);
        Pedido pedido = envio.getPedido();

        envio.setRepartidor(null);
        envio.setLatitudRepartidor(null);
        envio.setLongitudRepartidor(null);
        envio.setFechaUbicacion(null);
        envioRepository.save(envio);

        cambiar(pedido, EstadoPedido.EN_CAMINO, EstadoPedido.LISTO_PARA_ENVIO, codigoUsuario,
                "El repartidor liberó el pedido; vuelve a estar disponible");
        return pedido;
    }

    @Transactional
    public Pedido entregar(Integer codigoEnvio, Repartidor repartidor, Integer codigoUsuario) {
        Envio envio = envioDelRepartidor(codigoEnvio, repartidor);
        Pedido pedido = envio.getPedido();
        cambiar(pedido, EstadoPedido.EN_CAMINO, EstadoPedido.ENTREGADO, codigoUsuario, "Pedido entregado al cliente");
        return pedido;
    }

    /** Guarda la ultima posicion del repartidor (GPS del celular o simulacion de la demo). */
    @Transactional
    public void actualizarUbicacion(Integer codigoEnvio, Repartidor repartidor, BigDecimal latitud, BigDecimal longitud) {
        if (latitud == null || longitud == null
                || latitud.abs().compareTo(BigDecimal.valueOf(90)) > 0
                || longitud.abs().compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new ReglaNegocioException("Coordenadas inválidas.");
        }
        Envio envio = envioDelRepartidor(codigoEnvio, repartidor);
        if (envio.getPedido().getEstado() != EstadoPedido.EN_CAMINO) {
            throw new ReglaNegocioException("Solo se comparte la ubicación de un pedido en camino.");
        }
        envio.setLatitudRepartidor(latitud);
        envio.setLongitudRepartidor(longitud);
        envio.setFechaUbicacion(LocalDateTime.now());
        envioRepository.save(envio);
    }

    // ===================== CLIENTE =====================

    @Transactional
    public Pedido cancelarPorCliente(Integer codigoPedido, Integer codigoUsuario) {
        Pedido pedido = pedidoRepository.findById(codigoPedido)
                .orElseThrow(() -> new ReglaNegocioException("El pedido no existe."));
        Integer duenio = pedido.getEnvio().getCliente().getUsuario().getCodigoUsuario();
        if (!duenio.equals(codigoUsuario)) {
            throw new ReglaNegocioException("Solo quien hizo el pedido puede cancelarlo.");
        }
        if (pedido.getEstado() != EstadoPedido.SOLICITADO) {
            throw new ReglaNegocioException("La cocina ya aceptó tu pedido; ya no se puede cancelar.");
        }
        cambiar(pedido, EstadoPedido.SOLICITADO, EstadoPedido.CANCELADO, codigoUsuario, "Cancelado por el cliente");
        return pedido;
    }

    // ===================== HISTORIAL =====================

    /** Deja una fila en historial_pedido. Tambien la usa PedidoDesdeCarritoService al crear el pedido. */
    public void registrarHistorial(Pedido pedido, EstadoPedido anterior, EstadoPedido nuevo,
                                   Integer codigoUsuario, String comentario) {
        HistorialPedido fila = new HistorialPedido();
        fila.setPedido(pedido);
        fila.setEstadoAnterior(anterior != null ? anterior.getValorBd() : null);
        fila.setEstadoNuevo(nuevo.getValorBd());
        fila.setUsuario(codigoUsuario != null ? usuarioRepository.getReferenceById(codigoUsuario) : null);
        fila.setComentario(comentario);
        fila.setFecha(LocalDateTime.now());
        historialRepository.save(fila);
    }

    // ===================== AUXILIARES =====================

    /** Valida que el pedido este en el estado esperado, lo cambia y deja el rastro en el historial. */
    private void cambiar(Pedido pedido, EstadoPedido esperado, EstadoPedido nuevo,
                         Integer codigoUsuario, String comentario) {
        EstadoPedido actual = pedido.getEstado();
        if (actual != esperado) {
            throw new ReglaNegocioException("El pedido #" + pedido.getCodigoPedido() + " está \""
                    + actual.getEtiqueta() + "\" y no puede pasar a \"" + nuevo.getEtiqueta() + "\".");
        }
        pedido.setEstado(nuevo);
        pedidoRepository.save(pedido);
        registrarHistorial(pedido, actual, nuevo, codigoUsuario, comentario);
    }

    private Pedido pedidoDeLaCocina(Integer codigoPedido, Restaurante cocina) {
        Pedido pedido = pedidoRepository.findById(codigoPedido)
                .orElseThrow(() -> new ReglaNegocioException("El pedido no existe."));
        if (cocina == null || !pedido.getRestaurante().getCodigoRestaurante().equals(cocina.getCodigoRestaurante())) {
            throw new ReglaNegocioException("Ese pedido no es de tu cocina.");
        }
        return pedido;
    }

    private Envio buscarEnvio(Integer codigoEnvio) {
        return envioRepository.findById(codigoEnvio)
                .orElseThrow(() -> new ReglaNegocioException("El envío no existe."));
    }

    private Envio envioDelRepartidor(Integer codigoEnvio, Repartidor repartidor) {
        Envio envio = buscarEnvio(codigoEnvio);
        boolean esSuyo = envio.getRepartidor() != null
                && envio.getRepartidor().getCodigoRepartidor().equals(repartidor.getCodigoRepartidor());
        if (!esSuyo || envio.getPedido() == null) {
            throw new ReglaNegocioException("Ese envío no está asignado a ti.");
        }
        return envio;
    }

    private String recortar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo);
    }
}
