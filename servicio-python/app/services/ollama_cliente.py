"""Cliente HTTP de Ollama: el modelo de lenguaje corre en el mismo computador, gratis.

Documentacion de la API: POST {OLLAMA_URL}/api/chat con la lista de mensajes.
Si Ollama no esta encendido o tarda demasiado, se lanza ServicioExternoNoDisponible
y el chat responde en "modo basico" (ver chat_service.py).
"""
import logging
import re

import httpx

from app.core.config import configuracion
from app.core.excepciones import ServicioExternoNoDisponible

log = logging.getLogger("urbaneats")

TIEMPO_MAXIMO_SEGUNDOS = 120  # en CPU un modelo de 4B puede tardar; despues de esto se usa el modo basico


def _opciones(extra: dict | None = None) -> dict:
    """Opciones del modelo. num_ctx pequeno = lee el contexto mas rapido en CPU."""
    opciones = {"num_ctx": 2048}
    if configuracion.ollama_capas_gpu is not None:
        opciones["num_gpu"] = configuracion.ollama_capas_gpu
    opciones.update(extra or {})
    return opciones


def conversar(mensajes: list[dict], temperatura: float = 0.3, max_tokens: int = 220) -> str:
    """Envia la conversacion a Ollama y devuelve el texto de la respuesta."""
    cuerpo = {
        "model": configuracion.ollama_model,
        "messages": mensajes,
        "stream": False,
        "think": False,          # por si el modelo "razona": que responda directo (usar la variante -instruct)
        "keep_alive": "30m",     # deja el modelo cargado en memoria entre preguntas
        "options": _opciones({"temperature": temperatura, "num_predict": max_tokens}),
    }
    try:
        respuesta = httpx.post(f"{configuracion.ollama_url}/api/chat", json=cuerpo, timeout=TIEMPO_MAXIMO_SEGUNDOS)
        respuesta.raise_for_status()
        texto = respuesta.json()["message"]["content"]
    except httpx.TimeoutException as error:
        raise ServicioExternoNoDisponible("La IA local tardó demasiado en responder.") from error
    except httpx.HTTPStatusError as error:
        log.error("Ollama respondio %s: %s", error.response.status_code, error.response.text[:300])
        raise ServicioExternoNoDisponible(f"Ollama respondió con error {error.response.status_code} "
                                          f"(¿está descargado el modelo {configuracion.ollama_model}?).") from error
    except (httpx.HTTPError, KeyError, ValueError) as error:
        raise ServicioExternoNoDisponible("La IA local (Ollama) no está disponible.") from error

    # Algunos modelos igual envian su razonamiento entre etiquetas <think>; se quita.
    return re.sub(r"<think>.*?</think>", "", texto, flags=re.DOTALL).strip()


def estado() -> dict:
    """Para /api/salud y el arranque: dice si Ollama responde y si el modelo esta descargado."""
    try:
        respuesta = httpx.get(f"{configuracion.ollama_url}/api/tags", timeout=3)
        respuesta.raise_for_status()
        modelos = [modelo["name"] for modelo in respuesta.json().get("models", [])]
    except (httpx.HTTPError, ValueError):
        return {"disponible": False, "modelo": configuracion.ollama_model, "detalle": "Ollama no responde"}
    descargado = any(nombre == configuracion.ollama_model or nombre.startswith(configuracion.ollama_model + ":")
                     for nombre in modelos)
    return {"disponible": descargado, "modelo": configuracion.ollama_model,
            "detalle": "listo" if descargado else f"falta: ollama pull {configuracion.ollama_model}"}


def precargar() -> None:
    """Carga el modelo en memoria al arrancar, para que la primera pregunta de la demo no espere."""
    try:
        httpx.post(f"{configuracion.ollama_url}/api/generate",
                   json={"model": configuracion.ollama_model, "prompt": "", "keep_alive": "30m",
                         "options": _opciones()},  # mismas opciones: si cambian, Ollama recarga el modelo
                   timeout=TIEMPO_MAXIMO_SEGUNDOS)
    except httpx.HTTPError as error:
        log.warning("No se pudo precargar el modelo: %s", error)
