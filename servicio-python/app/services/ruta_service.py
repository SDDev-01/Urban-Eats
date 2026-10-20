"""Ruta del domicilio: de la cocina a la direccion de entrega.

1. Si hay ORS_API_KEY, pide la ruta por calles a OpenRouteService (gratis, 2.000 al dia).
2. Si no hay clave, se agoto la cuota o no hay internet (try/except), calcula una
   linea recta con la formula de Haversine y una velocidad promedio de 25 km/h.
La ruta de cada envio se guarda en ruta_envio para no gastar la cuota en cada consulta.
"""
import json
import logging
import math

import httpx
from sqlalchemy import text
from sqlalchemy.orm import Session

from app.core.config import configuracion
from app.core.excepciones import RecursoNoEncontrado, ReglaDeNegocio
from app.models.ruta import RutaEnvio

log = logging.getLogger("urbaneats")

URL_ORS = "https://api.openrouteservice.org/v2/directions/{perfil}/geojson"
VELOCIDAD_PROMEDIO_KMH = 25  # moto en ciudad, de noche
PERFILES = {"Moto": "driving-car", "Carro": "driving-car", "Bicicleta": "cycling-regular"}


def ruta_del_envio(sesion: Session, codigo_envio: int) -> dict:
    guardada = sesion.get(RutaEnvio, codigo_envio)
    if guardada and (guardada.proveedor == "openrouteservice" or not configuracion.ors_api_key):
        return _a_dict(guardada)

    datos = sesion.execute(text("""
        SELECT r.Latitud AS lat_cocina, r.Longitud AS lng_cocina,
               d.Latitud AS lat_destino, d.Longitud AS lng_destino, v.TipoVehiculo AS vehiculo
        FROM envio e
        JOIN restaurante r ON r.CodigoRestaurante = e.CodigoRestaurante
        LEFT JOIN direccion d ON d.CodigoDireccion = e.CodigoDireccion
        LEFT JOIN vehiculo v ON v.CodigoRepartidor = e.CodigoRepartidor
        WHERE e.CodigoEnvio = :envio
    """), {"envio": codigo_envio}).mappings().first()
    if datos is None:
        raise RecursoNoEncontrado(f"No existe el envío {codigo_envio}.")
    if None in (datos["lat_cocina"], datos["lng_cocina"], datos["lat_destino"], datos["lng_destino"]):
        raise ReglaDeNegocio("La cocina o la dirección de entrega no tienen ubicación en el mapa.")

    origen = (float(datos["lat_cocina"]), float(datos["lng_cocina"]))
    destino = (float(datos["lat_destino"]), float(datos["lng_destino"]))
    perfil = PERFILES.get(datos["vehiculo"] or "Moto", "driving-car")

    ruta = None
    if configuracion.ors_api_key:
        try:
            ruta = _ruta_openrouteservice(origen, destino, perfil)
        except (httpx.HTTPError, KeyError, IndexError, ValueError) as error:
            log.warning("OpenRouteService no disponible, se usa linea recta: %s", error)
    if ruta is None:
        ruta = _linea_recta(origen, destino)

    if guardada is None:
        guardada = RutaEnvio(codigo_envio=codigo_envio)
        sesion.add(guardada)
    guardada.proveedor = ruta["proveedor"]
    guardada.distancia_metros = ruta["distanciaMetros"]
    guardada.duracion_segundos = ruta["duracionSegundos"]
    guardada.coordenadas_json = json.dumps(ruta["coordenadas"])
    sesion.commit()
    return ruta


def _ruta_openrouteservice(origen, destino, perfil) -> dict:
    # ORS recibe las coordenadas como [longitud, latitud] (al reves que Leaflet).
    respuesta = httpx.post(
        URL_ORS.format(perfil=perfil),
        headers={"Authorization": configuracion.ors_api_key},
        json={"coordinates": [[origen[1], origen[0]], [destino[1], destino[0]]]},
        timeout=15)
    respuesta.raise_for_status()
    ruta = respuesta.json()["features"][0]
    resumen = ruta["properties"]["summary"]
    coordenadas = [[lat, lng] for lng, lat in ruta["geometry"]["coordinates"]]
    return {"proveedor": "openrouteservice", "distanciaMetros": int(resumen["distance"]),
            "duracionSegundos": int(resumen["duration"]), "coordenadas": coordenadas}


def _linea_recta(origen, destino) -> dict:
    metros = distancia_haversine(origen, destino)
    puntos = 20
    coordenadas = [[origen[0] + (destino[0] - origen[0]) * i / puntos,
                    origen[1] + (destino[1] - origen[1]) * i / puntos] for i in range(puntos + 1)]
    return {"proveedor": "linea_recta", "distanciaMetros": int(metros),
            "duracionSegundos": int(metros / (VELOCIDAD_PROMEDIO_KMH * 1000 / 3600)),
            "coordenadas": coordenadas}


def distancia_haversine(a, b) -> float:
    """Distancia en metros entre dos puntos (lat, lng) sobre la Tierra."""
    radio = 6_371_000
    lat1, lng1, lat2, lng2 = map(math.radians, (a[0], a[1], b[0], b[1]))
    h = math.sin((lat2 - lat1) / 2) ** 2 + math.cos(lat1) * math.cos(lat2) * math.sin((lng2 - lng1) / 2) ** 2
    return 2 * radio * math.asin(math.sqrt(h))


def _a_dict(ruta: RutaEnvio) -> dict:
    return {"proveedor": ruta.proveedor, "distanciaMetros": ruta.distancia_metros,
            "duracionSegundos": ruta.duracion_segundos, "coordenadas": json.loads(ruta.coordenadas_json)}
