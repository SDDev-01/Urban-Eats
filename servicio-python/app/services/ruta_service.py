"""Ruta del domicilio: de la cocina a la direccion de entrega.

1. Si hay ORS_API_KEY, pide la ruta por calles a OpenRouteService (gratis, 2.000 al dia).
2. Si no hay clave o ORS falla, la pide al servidor publico de OSRM (OpenStreetMap,
   sin clave): ruta por calles con indicaciones, como en Google Maps.
3. Si no hay internet (try/except), calcula una linea recta con la formula de
   Haversine y una velocidad promedio de 25 km/h.
La ruta de cada envio se guarda en ruta_envio para no pedirla en cada consulta. Una
linea recta guardada se vuelve a intentar por calles pasados unos minutos.
"""
import json
import logging
import math
from datetime import datetime, timedelta

import httpx
from sqlalchemy import text
from sqlalchemy.orm import Session

from app.core.config import configuracion
from app.core.excepciones import RecursoNoEncontrado, ReglaDeNegocio
from app.models.ruta import RutaEnvio

log = logging.getLogger("urbaneats")

URL_ORS = "https://api.openrouteservice.org/v2/directions/{perfil}/geojson"
# El servidor publico de OSRM solo tiene el perfil de carro; sirve igual para moto.
URL_OSRM = "https://router.project-osrm.org/route/v1/driving/{lng1},{lat1};{lng2},{lat2}"
VELOCIDAD_PROMEDIO_KMH = 25  # moto en ciudad, de noche
PERFILES = {"Moto": "driving-car", "Carro": "driving-car", "Bicicleta": "cycling-regular"}
REINTENTAR_LINEA_RECTA = timedelta(minutes=2)


def ruta_del_envio(sesion: Session, codigo_envio: int) -> dict:
    guardada = sesion.get(RutaEnvio, codigo_envio)
    if guardada and _sirve_la_guardada(guardada):
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
            log.warning("OpenRouteService no disponible, se intenta OSRM: %s", error)
    if ruta is None:
        try:
            ruta = _ruta_osrm(origen, destino)
        except (httpx.HTTPError, KeyError, IndexError, ValueError) as error:
            log.warning("OSRM no disponible, se usa linea recta: %s", error)
    if ruta is None:
        ruta = _linea_recta(origen, destino)

    if guardada is None:
        guardada = RutaEnvio(codigo_envio=codigo_envio)
        sesion.add(guardada)
    guardada.proveedor = ruta["proveedor"]
    guardada.distancia_metros = ruta["distanciaMetros"]
    guardada.duracion_segundos = ruta["duracionSegundos"]
    guardada.coordenadas_json = json.dumps(ruta["coordenadas"])
    guardada.pasos_json = json.dumps(ruta["pasos"], ensure_ascii=False)
    guardada.fecha_calculo = datetime.now()
    sesion.commit()
    return ruta


def _sirve_la_guardada(guardada: RutaEnvio) -> bool:
    """La ruta por calles se reutiliza siempre; la linea recta solo por unos minutos."""
    if guardada.proveedor != "linea_recta":
        return True
    return guardada.fecha_calculo is not None and datetime.now() - guardada.fecha_calculo < REINTENTAR_LINEA_RECTA


def _ruta_openrouteservice(origen, destino, perfil) -> dict:
    # ORS recibe las coordenadas como [longitud, latitud] (al reves que Leaflet).
    respuesta = httpx.post(
        URL_ORS.format(perfil=perfil),
        headers={"Authorization": configuracion.ors_api_key},
        json={"coordinates": [[origen[1], origen[0]], [destino[1], destino[0]]],
              "instructions": True, "language": "es"},
        timeout=15)
    respuesta.raise_for_status()
    ruta = respuesta.json()["features"][0]
    resumen = ruta["properties"]["summary"]
    coordenadas = [[lat, lng] for lng, lat in ruta["geometry"]["coordinates"]]
    pasos = [{"texto": paso["instruction"], "distanciaMetros": int(paso["distance"])}
             for segmento in ruta["properties"].get("segments", []) for paso in segmento.get("steps", [])]
    return {"proveedor": "openrouteservice", "distanciaMetros": int(resumen["distance"]),
            "duracionSegundos": int(resumen["duration"]), "coordenadas": coordenadas, "pasos": pasos}


def _ruta_osrm(origen, destino) -> dict:
    respuesta = httpx.get(
        URL_OSRM.format(lat1=origen[0], lng1=origen[1], lat2=destino[0], lng2=destino[1]),
        params={"overview": "full", "geometries": "geojson", "steps": "true"},
        timeout=6)
    respuesta.raise_for_status()
    cuerpo = respuesta.json()
    if cuerpo.get("code") != "Ok":
        raise ValueError(f"OSRM respondio {cuerpo.get('code')}")
    ruta = cuerpo["routes"][0]
    coordenadas = [[lat, lng] for lng, lat in ruta["geometry"]["coordinates"]]
    return {"proveedor": "osrm", "distanciaMetros": int(ruta["distance"]),
            "duracionSegundos": int(ruta["duration"]), "coordenadas": coordenadas,
            "pasos": pasos_osrm(ruta["legs"][0]["steps"])}


# ---- Indicaciones en espanol a partir de las maniobras de OSRM ----

GIROS = {
    "right": "Gira a la derecha", "left": "Gira a la izquierda",
    "slight right": "Gira levemente a la derecha", "slight left": "Gira levemente a la izquierda",
    "sharp right": "Gira cerrado a la derecha", "sharp left": "Gira cerrado a la izquierda",
    "straight": "Sigue derecho", "uturn": "Da la vuelta en U",
}
SIGUE_IGUAL = {"turn", "continue", "new name", "notification", "use lane"}
# Curvas suaves por la misma via (bifurcaciones, calzadas): no son un giro para el repartidor.
SUAVES = {None, "straight", "slight left", "slight right"}


def texto_maniobra(tipo: str, modificador: str | None, calle: str, salida: int | None = None) -> str:
    """Traduce una maniobra de OSRM (type + modifier) a una indicacion corta."""
    por = f" por {calle}" if calle else ""
    if tipo == "depart":
        return f"Sal{por}" if calle else "Sal de la cocina"
    if tipo == "arrive":
        return "Llegaste a la dirección de entrega"
    if tipo in ("roundabout", "rotary", "roundabout turn"):
        numero = f"la {salida}.ª salida" if salida else "la salida"
        return f"En la glorieta toma {numero}" + (f" hacia {calle}" if calle else "")
    if tipo in ("exit roundabout", "exit rotary"):
        return f"Sal de la glorieta{por}"
    if tipo in SIGUE_IGUAL and modificador in (None, "straight"):
        return f"Continúa{por}" if calle else "Sigue derecho"
    return GIROS.get(modificador or "straight", "Continúa") + por


def pasos_osrm(pasos: list[dict]) -> list[dict]:
    """Convierte los pasos de OSRM en indicaciones. Junta los "sigue derecho" y las curvas
    suaves por la misma via, que OSRM repite en cada cruce o bifurcacion."""
    resultado: list[dict] = []
    calle_anterior = None
    for paso in pasos:
        maniobra = paso["maneuver"]
        tipo, modificador = maniobra.get("type", ""), maniobra.get("modifier")
        calle = paso.get("name") or ""
        distancia = int(paso.get("distance", 0))
        recto = tipo in SIGUE_IGUAL and modificador in (None, "straight")
        misma_via = tipo in SIGUE_IGUAL | {"fork", "merge"} and modificador in SUAVES and calle == calle_anterior
        if resultado and ((recto and not calle) or misma_via):
            resultado[-1]["distanciaMetros"] += distancia
            continue
        resultado.append({"texto": texto_maniobra(tipo, modificador, calle, maniobra.get("exit")),
                          "distanciaMetros": distancia})
        calle_anterior = calle or calle_anterior
    return resultado


def _linea_recta(origen, destino) -> dict:
    metros = distancia_haversine(origen, destino)
    puntos = 20
    coordenadas = [[origen[0] + (destino[0] - origen[0]) * i / puntos,
                    origen[1] + (destino[1] - origen[1]) * i / puntos] for i in range(puntos + 1)]
    return {"proveedor": "linea_recta", "distanciaMetros": int(metros),
            "duracionSegundos": int(metros / (VELOCIDAD_PROMEDIO_KMH * 1000 / 3600)),
            "coordenadas": coordenadas, "pasos": []}


def distancia_haversine(a, b) -> float:
    """Distancia en metros entre dos puntos (lat, lng) sobre la Tierra."""
    radio = 6_371_000
    lat1, lng1, lat2, lng2 = map(math.radians, (a[0], a[1], b[0], b[1]))
    h = math.sin((lat2 - lat1) / 2) ** 2 + math.cos(lat1) * math.cos(lat2) * math.sin((lng2 - lng1) / 2) ** 2
    return 2 * radio * math.asin(math.sqrt(h))


def _a_dict(ruta: RutaEnvio) -> dict:
    return {"proveedor": ruta.proveedor, "distanciaMetros": ruta.distancia_metros,
            "duracionSegundos": ruta.duracion_segundos, "coordenadas": json.loads(ruta.coordenadas_json),
            "pasos": json.loads(ruta.pasos_json) if ruta.pasos_json else []}
