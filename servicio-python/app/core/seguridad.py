"""Proteccion de la API: solo Spring Boot (que conoce la clave) puede llamarla."""
import secrets

from fastapi import Security
from fastapi.security import APIKeyHeader

from app.core.config import configuracion
from app.core.excepciones import AccesoDenegado

# Esto hace que Swagger (/docs) muestre el boton "Authorize" para escribir la clave.
esquema_api_key = APIKeyHeader(name="X-API-KEY", auto_error=False)


def verificar_api_key(x_api_key: str | None = Security(esquema_api_key)) -> None:
    """Dependencia de FastAPI: rechaza la peticion si el header X-API-KEY no coincide."""
    if not configuracion.api_key:
        raise AccesoDenegado("El servicio no tiene API_KEY configurada en su .env.")
    # compare_digest compara en tiempo constante (evita adivinar la clave midiendo tiempos)
    if x_api_key is None or not secrets.compare_digest(x_api_key, configuracion.api_key):
        raise AccesoDenegado("Clave X-API-KEY ausente o incorrecta.")
