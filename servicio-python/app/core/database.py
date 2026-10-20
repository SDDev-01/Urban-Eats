"""Conexion a la base de datos MySQL/MariaDB que comparte con Spring Boot."""
from sqlalchemy import create_engine, text
from sqlalchemy.orm import DeclarativeBase, sessionmaker

from app.core.config import configuracion

# pool_pre_ping revisa que la conexion siga viva antes de usarla (MariaDB cierra
# las conexiones inactivas despues de un tiempo).
motor = create_engine(configuracion.db_url, pool_pre_ping=True)
SesionLocal = sessionmaker(bind=motor, autoflush=False, expire_on_commit=False)


class Base(DeclarativeBase):
    """Clase base de los modelos (tablas) propios del servicio Python."""


def obtener_sesion():
    """Dependencia de FastAPI: abre una sesion por peticion y siempre la cierra."""
    sesion = SesionLocal()
    try:
        yield sesion
    finally:
        sesion.close()


def probar_conexion() -> str:
    """Devuelve la version del servidor de base de datos; lanza excepcion si no conecta."""
    with motor.connect() as conexion:
        return conexion.execute(text("SELECT VERSION()")).scalar_one()
