package com.urbaneats.controller;

/** Forma que restaurantes.js espera para cada tarjeta de window.RESTAURANTES_BD. */
public record RestauranteResumen(Integer id, String nombre, String direccion, String horario, String imagen) {
}
