"""Excepciones propias del servicio y como se convierten en respuestas JSON.

Los servicios lanzan estas excepciones (raise) y los manejadores de abajo las
capturan para responder con un mensaje en espanol y el codigo HTTP correcto,
sin que el usuario vea un traceback.
"""
import logging

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from sqlalchemy.exc import IntegrityError, OperationalError

log = logging.getLogger("urbaneats")


class ErrorUrbanEats(Exception):
    """Base de las excepciones del servicio."""

    codigo_http = 400

    def __init__(self, mensaje: str):
        super().__init__(mensaje)
        self.mensaje = mensaje


class RecursoNoEncontrado(ErrorUrbanEats):
    codigo_http = 404


class ReglaDeNegocio(ErrorUrbanEats):
    """Los datos son validos pero la accion rompe una regla (ej: aprobar sin concepto sanitario)."""

    codigo_http = 409


class TransicionNoPermitida(ReglaDeNegocio):
    """Cambio de estado que no esta permitido (ej: de Rechazada a Aprobada)."""


class AccesoDenegado(ErrorUrbanEats):
    codigo_http = 401


class ServicioExternoNoDisponible(ErrorUrbanEats):
    """Ollama u OpenRouteService no responden."""

    codigo_http = 503


def registrar_manejadores(app: FastAPI) -> None:
    """Conecta cada tipo de error con su respuesta JSON."""

    @app.exception_handler(ErrorUrbanEats)
    async def error_propio(_: Request, error: ErrorUrbanEats):
        return JSONResponse(status_code=error.codigo_http, content={"error": error.mensaje})

    @app.exception_handler(RequestValidationError)
    async def error_validacion(_: Request, error: RequestValidationError):
        # Pydantic devuelve una lista de errores; se dejan en un formato simple:
        # {"error": "...", "campos": {"nit": "mensaje", ...}}
        campos = {}
        for detalle in error.errors():
            nombre = ".".join(str(parte) for parte in detalle["loc"] if parte not in ("body", "query"))
            mensaje = detalle["msg"].removeprefix("Value error, ")
            campos[nombre or "solicitud"] = mensaje
        return JSONResponse(status_code=422, content={"error": "Hay datos inválidos.", "campos": campos})

    @app.exception_handler(IntegrityError)
    async def error_integridad(_: Request, error: IntegrityError):
        log.warning("Violacion de integridad: %s", error.orig)
        return JSONResponse(status_code=409, content={"error": "El registro ya existe o depende de otro dato."})

    @app.exception_handler(OperationalError)
    async def error_base_datos(_: Request, error: OperationalError):
        log.error("Base de datos no disponible: %s", error.orig)
        return JSONResponse(status_code=503, content={"error": "La base de datos no está disponible."})
