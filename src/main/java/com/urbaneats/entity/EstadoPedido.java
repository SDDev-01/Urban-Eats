package com.urbaneats.entity;

/**
 * Los seis estados por los que pasa un pedido.
 *
 * Solicitado -> En preparacion -> Listo para envio -> En camino -> Entregado
 * (y Cancelado, si el cliente lo cancela o la cocina lo rechaza).
 *
 * En la base se guarda valorBd, sin tildes, para no tener problemas de
 * codificacion entre MariaDB, Java y Python. En pantalla se muestra la etiqueta.
 */
public enum EstadoPedido {

    SOLICITADO("Solicitado", "Solicitado"),
    EN_PREPARACION("En preparacion", "En preparación"),
    LISTO_PARA_ENVIO("Listo para envio", "Listo para envío"),
    EN_CAMINO("En camino", "En camino"),
    ENTREGADO("Entregado", "Entregado"),
    CANCELADO("Cancelado", "Cancelado");

    private final String valorBd;
    private final String etiqueta;

    EstadoPedido(String valorBd, String etiqueta) {
        this.valorBd = valorBd;
        this.etiqueta = etiqueta;
    }

    public String getValorBd() {
        return valorBd;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Un pedido entregado o cancelado ya no cambia de estado. */
    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }

    /** Convierte el texto guardado en la base al estado correspondiente. */
    public static EstadoPedido desdeBd(String valor) {
        for (EstadoPedido estado : values()) {
            if (estado.valorBd.equals(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de pedido desconocido en la base: " + valor);
    }
}
