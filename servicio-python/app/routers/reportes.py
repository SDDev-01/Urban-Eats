"""Modulo de reportes (/api/reportes): filtros, ordenamiento y exportacion.

Todos aceptan formato=json (por defecto), csv, xlsx o pdf.
"""
from datetime import date
from typing import Literal

from fastapi import APIRouter, Depends, Query
from fastapi.responses import Response
from sqlalchemy.orm import Session

from app.core.database import obtener_sesion
from app.core.seguridad import verificar_api_key
from app.services import exportador, reporte_service

router = APIRouter(prefix="/api/reportes", tags=["Reportes"], dependencies=[Depends(verificar_api_key)])

Formato = Literal["json", "csv", "xlsx", "pdf"]
Franja = Literal["nocturna", "diurna"]


def _responder(titulo, columnas, filas, formato, extra=None):
    """JSON para la pantalla, o el archivo para descargar."""
    if formato == "json":
        respuesta = {"titulo": titulo, "columnas": [{"clave": c, "titulo": t} for c, t in columnas], "filas": filas}
        respuesta.update(extra or {})
        return respuesta
    contenido, tipo, nombre = exportador.exportar(titulo, columnas, filas, formato)
    return Response(content=contenido, media_type=tipo,
                    headers={"Content-Disposition": f'attachment; filename="{nombre}"'})


@router.get("/pedidos", summary="Pedidos con filtros combinables, orden y paginación")
def pedidos(estado: list[str] | None = Query(default=None, description="Se puede repetir: estado=Entregado&estado=Cancelado"),
            desde: date | None = None, hasta: date | None = None,
            codigo_restaurante: int | None = None, franja: Franja | None = None,
            monto_min: float | None = Query(default=None, ge=0), monto_max: float | None = Query(default=None, ge=0),
            q: str | None = Query(default=None, description="Busca por cliente o platos"),
            orden: Literal["fecha_desc", "fecha_asc", "total_desc", "total_asc", "cocina_asc"] = "fecha_desc",
            pagina: int = Query(default=1, ge=1), tamano: int = Query(default=50, ge=1, le=1000),
            formato: Formato = "json", sesion: Session = Depends(obtener_sesion)):
    if formato != "json":
        pagina, tamano = 1, 10000  # el archivo descargado lleva todos los resultados
    resultado = reporte_service.pedidos(sesion, estado, desde, hasta, codigo_restaurante, franja,
                                        monto_min, monto_max, q, orden, pagina, tamano)
    columnas = [("codigo", "Pedido"), ("fecha_hora", "Fecha y hora"), ("cocina", "Cocina"), ("cliente", "Cliente"),
                ("platos", "Platos"), ("estado", "Estado"), ("franja", "Franja"), ("total", "Total COP")]
    extra = {k: resultado[k] for k in ("total_registros", "suma_total", "pagina", "tamano")}
    return _responder("Reporte de pedidos", columnas, resultado["filas"], formato, extra)


@router.get("/ventas-por-cocina", summary="Ventas, ticket promedio y % nocturno por cocina")
def ventas_por_cocina(desde: date | None = None, hasta: date | None = None, codigo_restaurante: int | None = None,
                      franja: Franja | None = None, formato: Formato = "json",
                      sesion: Session = Depends(obtener_sesion)):
    filas = reporte_service.ventas_por_cocina(sesion, desde, hasta, codigo_restaurante, franja)
    columnas = [("cocina", "Cocina"), ("pedidos", "Pedidos"), ("entregados", "Entregados"),
                ("cancelados", "Cancelados"), ("ventas", "Ventas COP"), ("ticket_promedio", "Ticket promedio"),
                ("porcentaje_nocturno", "% nocturno")]
    return _responder("Ventas por cocina", columnas, filas, formato)


@router.get("/pedidos-por-hora", summary="Distribución de pedidos por hora del día")
def pedidos_por_hora(desde: date | None = None, hasta: date | None = None, codigo_restaurante: int | None = None,
                     formato: Formato = "json", sesion: Session = Depends(obtener_sesion)):
    filas = reporte_service.pedidos_por_hora(sesion, desde, hasta, codigo_restaurante)
    columnas = [("etiqueta", "Hora"), ("pedidos", "Pedidos"), ("franja", "Franja")]
    nocturnos = sum(f["pedidos"] for f in filas if f["franja"] == "Nocturna")
    total = sum(f["pedidos"] for f in filas)
    extra = {"total": total, "nocturnos": nocturnos,
             "porcentaje_nocturno": round(100 * nocturnos / total, 1) if total else 0}
    return _responder("Pedidos por hora", columnas, filas, formato, extra)


@router.get("/tiempos-entrega", summary="Minutos promedio de entrega por cocina")
def tiempos_entrega(desde: date | None = None, hasta: date | None = None, codigo_restaurante: int | None = None,
                    formato: Formato = "json", sesion: Session = Depends(obtener_sesion)):
    filas = reporte_service.tiempos_entrega(sesion, desde, hasta, codigo_restaurante)
    columnas = [("cocina", "Cocina"), ("entregas", "Entregas"), ("minutos_promedio", "Min. promedio"),
                ("minutos_minimo", "Mínimo"), ("minutos_maximo", "Máximo")]
    return _responder("Tiempos de entrega", columnas, filas, formato)


@router.get("/platos-top", summary="Platos más vendidos (filtro por sello, ej. Balanceado)")
def platos_top(desde: date | None = None, hasta: date | None = None, codigo_restaurante: int | None = None,
               etiqueta: str | None = None, limite: int = Query(default=10, ge=1, le=100),
               formato: Formato = "json", sesion: Session = Depends(obtener_sesion)):
    filas = reporte_service.platos_top(sesion, desde, hasta, codigo_restaurante, etiqueta, limite)
    columnas = [("plato", "Plato"), ("cocina", "Cocina"), ("unidades", "Unidades"), ("ventas", "Ventas COP"),
                ("calorias", "Kcal"), ("sellos", "Sellos")]
    return _responder("Platos más vendidos", columnas, filas, formato)


@router.get("/documentos-por-vencer", summary="Documentos de cocinas que vencen pronto")
def documentos_por_vencer(dias: int = Query(default=30, ge=0, le=365), formato: Formato = "json",
                          sesion: Session = Depends(obtener_sesion)):
    filas = reporte_service.documentos_por_vencer(sesion, dias)
    columnas = [("cocina", "Cocina"), ("estado_solicitud", "Solicitud"), ("documento", "Documento"),
                ("titular", "Titular"), ("vence", "Vence"), ("dias_restantes", "Días")]
    return _responder("Documentos por vencer", columnas, filas, formato)
