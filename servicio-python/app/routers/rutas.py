"""Modulo de rutas del domicilio (/api/rutas)."""
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.database import obtener_sesion
from app.core.seguridad import verificar_api_key
from app.services import ruta_service

router = APIRouter(prefix="/api/rutas", tags=["Rutas del domicilio"], dependencies=[Depends(verificar_api_key)])


@router.get("/envio/{codigo_envio}", summary="Ruta cocina -> cliente (OpenRouteService o línea recta)")
def ruta_del_envio(codigo_envio: int, sesion: Session = Depends(obtener_sesion)):
    return ruta_service.ruta_del_envio(sesion, codigo_envio)
