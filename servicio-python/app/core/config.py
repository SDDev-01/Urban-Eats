"""Configuracion del servicio: se lee del archivo .env (o de variables de entorno)."""
from pathlib import Path

from pydantic_settings import BaseSettings, SettingsConfigDict

CARPETA_SERVICIO = Path(__file__).resolve().parents[2]


class Configuracion(BaseSettings):
    """Cada atributo corresponde a una variable del .env (sin importar mayusculas)."""

    model_config = SettingsConfigDict(env_file=CARPETA_SERVICIO / ".env", extra="ignore")

    db_url: str = "mysql+pymysql://root:@localhost:3306/urbaneats?charset=utf8mb4"
    api_key: str = ""
    ollama_url: str = "http://localhost:11434"
    ollama_model: str = "qwen3:4b-instruct"
    # Capas del modelo que van a la tarjeta de video. Vacio = Ollama decide.
    # 0 = solo CPU: en un portatil con GPU pequena (ej. 4 GB) repartir el modelo
    # entre GPU y CPU puede ser MAS lento que usar solo la CPU.
    ollama_capas_gpu: int | None = None
    ors_api_key: str = ""
    modo_demo: bool = False
    host: str = "127.0.0.1"
    puerto: int = 8000

    # Carpeta donde se guardan los PDF de las solicitudes de cocina
    carpeta_archivos: Path = CARPETA_SERVICIO / "uploads"


configuracion = Configuracion()
