"""Pruebas de las validaciones colombianas (no necesitan base de datos)."""
from datetime import time

import pytest

from app.utils import validadores


@pytest.mark.parametrize("nit, dv", [("800197268", 4), ("900123456", 8), ("860002964", 4)])
def test_digito_verificacion_dian(nit, dv):
    assert validadores.calcular_digito_verificacion(nit) == dv


def test_nit_con_puntos_y_guion_se_limpia():
    assert validadores.validar_nit("900.123.456", "8") == ("900123456", "8")


def test_nit_con_dv_incorrecto_explica_cual_es_el_correcto():
    with pytest.raises(ValueError, match="debería ser 8"):
        validadores.validar_nit("900123456", "5")


@pytest.mark.parametrize("nit", ["123", "ABC123456", ""])
def test_nit_con_formato_invalido(nit):
    with pytest.raises(ValueError):
        validadores.validar_nit(nit, "1")


@pytest.mark.parametrize("entrada, esperado", [
    ("320 555 0199", "3205550199"), ("+57 3205550199", "3205550199"), ("6017654321", "6017654321")])
def test_telefonos_validos(entrada, esperado):
    assert validadores.validar_telefono(entrada) == esperado


@pytest.mark.parametrize("telefono", ["12345", "2205550199", "320555019"])
def test_telefonos_invalidos(telefono):
    with pytest.raises(ValueError):
        validadores.validar_telefono(telefono)


def test_localidad_sin_importar_mayusculas():
    assert validadores.validar_localidad("teusaquillo") == "Teusaquillo"
    with pytest.raises(ValueError):
        validadores.validar_localidad("Medellín")


@pytest.mark.parametrize("abre, cierra, esperado", [
    (time(21), time(5), True),     # cruza la medianoche
    (time(18), time(23), True),    # cierra despues de las 10 p. m.
    (time(8), time(17), False),    # solo de dia: no es una cocina nocturna
    (time(0), time(0), True),      # 24 horas
])
def test_franja_nocturna(abre, cierra, esperado):
    assert validadores.cubre_franja_nocturna(abre, cierra) is esperado
