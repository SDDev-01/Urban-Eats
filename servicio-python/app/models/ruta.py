"""Cache de rutas calculadas con OpenRouteService.

Cada envio guarda su ruta una sola vez: asi no se gasta la cuota gratuita
(2.000 rutas al dia) cada vez que el mapa se actualiza.
"""
from datetime import datetime

from sqlalchemy import DateTime, Integer, String, Text, func
from sqlalchemy.orm import Mapped, mapped_column

from app.core.database import Base


class RutaEnvio(Base):
    __tablename__ = "ruta_envio"

    codigo_envio: Mapped[int] = mapped_column("CodigoEnvio", Integer, primary_key=True, autoincrement=False)
    proveedor: Mapped[str] = mapped_column("Proveedor", String(30))  # openrouteservice / linea_recta
    distancia_metros: Mapped[int] = mapped_column("DistanciaMetros", Integer)
    duracion_segundos: Mapped[int] = mapped_column("DuracionSegundos", Integer)
    coordenadas_json: Mapped[str] = mapped_column("CoordenadasJson", Text)  # [[lat, lng], ...]
    fecha_calculo: Mapped[datetime] = mapped_column("FechaCalculo", DateTime, server_default=func.now())
