package com.urbaneats.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Le explica a JPA como guardar un EstadoPedido en la columna Estado (texto)
 * y como leerlo de vuelta. Asi el resto del codigo trabaja con el enum y no
 * con textos sueltos que se pueden escribir mal.
 */
@Converter
public class EstadoPedidoConverter implements AttributeConverter<EstadoPedido, String> {

    @Override
    public String convertToDatabaseColumn(EstadoPedido estado) {
        return estado == null ? null : estado.getValorBd();
    }

    @Override
    public EstadoPedido convertToEntityAttribute(String valor) {
        return valor == null ? null : EstadoPedido.desdeBd(valor);
    }
}
