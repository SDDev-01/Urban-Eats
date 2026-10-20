"""Modulo de vinculacion de cocinas: CRUD de solicitudes (/api/solicitudes)."""
from typing import Literal

from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.orm import Session

from app.core.database import obtener_sesion
from app.core.seguridad import verificar_api_key
from app.schemas.solicitud import (CambioEstado, Cumplimiento, PaginaSolicitudes, SolicitudActualizar,
                                   SolicitudCrear, SolicitudDetalle, SolicitudRespuesta)
from app.services import solicitud_service

router = APIRouter(prefix="/api/solicitudes", tags=["Solicitudes de cocinas"],
                   dependencies=[Depends(verificar_api_key)])


def _detalle(solicitud) -> SolicitudDetalle:
    detalle = SolicitudDetalle.model_validate(solicitud)
    for documento, origen in zip(detalle.documentos, solicitud.documentos):
        documento.tiene_archivo = bool(origen.ruta_archivo)
    return detalle


@router.post("", response_model=SolicitudDetalle, status_code=status.HTTP_201_CREATED,
             summary="Crear una solicitud de vinculación")
def crear(datos: SolicitudCrear, sesion: Session = Depends(obtener_sesion)):
    return _detalle(solicitud_service.crear(sesion, datos))


@router.get("", response_model=PaginaSolicitudes, summary="Listar solicitudes con filtros y orden")
def listar(estado: str | None = None,
           localidad: str | None = None,
           tipo: str | None = Query(default=None, description="Restaurante o Cocina oculta"),
           q: str | None = Query(default=None, description="Busca en nombre, razón social o NIT"),
           codigo_usuario: int | None = None,
           orden: Literal["fecha_desc", "fecha_asc", "nombre_asc", "nombre_desc", "estado_asc"] = "fecha_desc",
           pagina: int = Query(default=1, ge=1),
           tamano: int = Query(default=20, ge=1, le=100),
           sesion: Session = Depends(obtener_sesion)):
    total, resultados = solicitud_service.listar(sesion, estado, localidad, tipo, q, codigo_usuario,
                                                 orden, pagina, tamano)
    return PaginaSolicitudes(total=total, pagina=pagina, tamano=tamano,
                             resultados=[SolicitudRespuesta.model_validate(s) for s in resultados])


@router.get("/{codigo}", response_model=SolicitudDetalle, summary="Ver una solicitud con documentos e historial")
def obtener(codigo: int, sesion: Session = Depends(obtener_sesion)):
    return _detalle(solicitud_service.obtener(sesion, codigo))


@router.put("/{codigo}", response_model=SolicitudDetalle, summary="Corregir los datos (Borrador o Requiere ajustes)")
def actualizar(codigo: int, datos: SolicitudActualizar, codigo_usuario: int = Query(gt=0),
               sesion: Session = Depends(obtener_sesion)):
    return _detalle(solicitud_service.actualizar(sesion, codigo, datos, codigo_usuario))


@router.delete("/{codigo}", status_code=status.HTTP_204_NO_CONTENT,
               summary="Eliminar (solo borradores o rechazadas)")
def eliminar(codigo: int, codigo_usuario: int | None = None, sesion: Session = Depends(obtener_sesion)):
    solicitud_service.eliminar(sesion, codigo, codigo_usuario)


@router.patch("/{codigo}/estado", response_model=SolicitudDetalle,
              summary="Decisión del técnico: revisar, pedir ajustes, aprobar o rechazar")
def cambiar_estado(codigo: int, cambio: CambioEstado, sesion: Session = Depends(obtener_sesion)):
    return _detalle(solicitud_service.cambiar_estado(sesion, codigo, cambio))


@router.get("/{codigo}/cumplimiento", response_model=Cumplimiento,
            summary="Checklist automático de documentos para aprobar")
def cumplimiento(codigo: int, sesion: Session = Depends(obtener_sesion)):
    return solicitud_service.evaluar_cumplimiento(solicitud_service.obtener(sesion, codigo))
