package com.urbaneats.controller;

import com.urbaneats.entity.Envio;

import java.math.BigDecimal;

/** Envuelve un Envio con el total ya calculado, para que la plantilla no tenga que sumarlo. */
public record EnvioConTotal(Envio envio, BigDecimal total) {
}
