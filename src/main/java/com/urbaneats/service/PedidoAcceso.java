package com.urbaneats.service;

import org.springframework.stereotype.Component;

import com.urbaneats.entity.Envio;
import com.urbaneats.entity.Pedido;
import com.urbaneats.security.SesionHelper;

/**
 * Decide quien puede ver un pedido y en calidad de que.
 * Evita que cualquier usuario consulte o cancele pedidos ajenos cambiando el numero
 * en la URL (antes /pedido/{id}/estado y /cancelar no revisaban el dueno).
 */
@Component
public class PedidoAcceso {

    public static final String CLIENTE = "cliente";
    public static final String COCINA = "cocina";
    public static final String REPARTIDOR = "repartidor";
    public static final String TECNICO = "tecnico";

    /**
     * Devuelve como ve el usuario este pedido: "cliente" (lo pidio), "cocina" (es gerente
     * de la cocina), "repartidor" (lo lleva), "tecnico" (soporte) o null si no tiene acceso.
     */
    public String vistaDe(Pedido pedido, Integer codigoUsuario) {
        if (pedido == null || codigoUsuario == null) {
            return null;
        }
        Envio envio = pedido.getEnvio();

        if (envio != null && envio.getCliente() != null
                && codigoUsuario.equals(envio.getCliente().getUsuario().getCodigoUsuario())) {
            return CLIENTE;
        }
        if (envio != null && envio.getRepartidor() != null
                && codigoUsuario.equals(envio.getRepartidor().getUsuario().getCodigoUsuario())) {
            return REPARTIDOR;
        }
        if (pedido.getRestaurante() != null && pedido.getRestaurante().getGerente() != null
                && codigoUsuario.equals(pedido.getRestaurante().getGerente().getUsuario().getCodigoUsuario())) {
            return COCINA;
        }
        if (SesionHelper.tieneRol("TECNICO")) {
            return TECNICO;
        }
        return null;
    }
}
