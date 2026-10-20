"""Validaciones de datos colombianos que usan los esquemas Pydantic.

Cada funcion lanza ValueError con un mensaje en espanol cuando el dato no sirve;
Pydantic captura ese ValueError y lo convierte en un error 422 para el cliente.
"""
import re
from datetime import time

# Pesos oficiales de la DIAN para calcular el digito de verificacion del NIT.
# Se aplican de derecha a izquierda sobre los digitos del NIT.
PESOS_DIAN = [3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71]

LOCALIDADES_BOGOTA = [
    "Usaquén", "Chapinero", "Santa Fe", "San Cristóbal", "Usme", "Tunjuelito", "Bosa",
    "Kennedy", "Fontibón", "Engativá", "Suba", "Barrios Unidos", "Teusaquillo",
    "Los Mártires", "Antonio Nariño", "Puente Aranda", "La Candelaria",
    "Rafael Uribe Uribe", "Ciudad Bolívar", "Sumapaz",
]


def calcular_digito_verificacion(nit: str) -> int:
    """Calcula el digito de verificacion (DV) de un NIT con el algoritmo modulo 11 de la DIAN.

    Ejemplo: para el NIT 900123456 el DV es 8.
    """
    digitos = [int(caracter) for caracter in nit]
    suma = sum(digito * peso for digito, peso in zip(reversed(digitos), PESOS_DIAN))
    residuo = suma % 11
    return residuo if residuo in (0, 1) else 11 - residuo


def limpiar_nit(nit: str) -> str:
    """Quita puntos, espacios y el guion del DV: '900.123.456-8' -> '9001234568'."""
    return re.sub(r"[.\s-]", "", nit or "")


def validar_nit(nit: str, digito_verificacion: str) -> tuple[str, str]:
    """Revisa formato y DV. Devuelve (nit, dv) limpios o lanza ValueError."""
    nit = limpiar_nit(nit)
    if not nit.isdigit() or not 6 <= len(nit) <= 15:
        raise ValueError("El NIT debe tener entre 6 y 15 dígitos, sin el dígito de verificación.")
    if not str(digito_verificacion).isdigit() or len(str(digito_verificacion)) != 1:
        raise ValueError("El dígito de verificación debe ser un solo número del 0 al 9.")
    esperado = calcular_digito_verificacion(nit)
    if int(digito_verificacion) != esperado:
        raise ValueError(f"El dígito de verificación no corresponde al NIT {nit} "
                         f"(revise el RUT: debería ser {esperado}).")
    return nit, str(digito_verificacion)


def validar_telefono(telefono: str) -> str:
    """Celular colombiano de 10 digitos que empieza por 3, o fijo de Bogota 601 + 7 digitos."""
    numero = re.sub(r"[\s()+-]", "", telefono or "")
    if numero.startswith("57") and len(numero) == 12:
        numero = numero[2:]
    if re.fullmatch(r"3\d{9}", numero) or re.fullmatch(r"601\d{7}", numero):
        return numero
    raise ValueError("El teléfono debe ser un celular de 10 dígitos (3XX...) o un fijo de Bogotá (601...).")


def validar_localidad(localidad: str) -> str:
    """La localidad debe ser una de las 20 de Bogota (sin importar mayusculas)."""
    for nombre in LOCALIDADES_BOGOTA:
        if nombre.lower() == (localidad or "").strip().lower():
            return nombre
    raise ValueError("La localidad debe ser una de las 20 localidades de Bogotá.")


def cubre_franja_nocturna(apertura: time, cierre: time) -> bool:
    """True si la cocina atiende en algun momento entre las 10 p. m. y las 6 a. m.

    Cuando el cierre es menor que la apertura, la cocina cierra al dia siguiente.
    """
    if apertura == cierre:
        return True  # abierta 24 horas
    if apertura < cierre:
        # Mismo dia: es nocturna si abre antes de las 6 a. m. o cierra despues de las 10 p. m.
        return apertura < time(6, 0) or cierre > time(22, 0)
    return True  # cruza la medianoche
