"""Esquemas (Pydantic) de las solicitudes de vinculacion de cocinas.

Pydantic valida automaticamente tipos y formatos. Las reglas colombianas
(NIT con digito de verificacion, telefono, localidad) estan en utils/validadores.py.
Si algo falla, FastAPI responde 422 con el mensaje de cada campo.
"""
from datetime import date, datetime, time
from decimal import Decimal
from typing import Literal

from pydantic import BaseModel, ConfigDict, Field, field_validator, model_validator

from app.utils import validadores


class SolicitudBase(BaseModel):
    nombre_comercial: str = Field(min_length=3, max_length=150, examples=["Cocina de Prueba Urban Eats"])
    razon_social: str = Field(min_length=3, max_length=150, examples=["Cocina de Prueba Urban Eats S.A.S."])
    tipo_persona: Literal["Natural", "Juridica"]
    nit: str = Field(examples=["900123456"])
    digito_verificacion: str = Field(examples=["8"])
    matricula_mercantil: str | None = Field(default=None, max_length=30)
    tipo_establecimiento: Literal["Restaurante", "Cocina oculta"]
    categoria: str = Field(min_length=3, max_length=60, examples=["Casera"])
    telefono: str = Field(examples=["3205550199"])
    correo: str = Field(max_length=100, examples=["cocina.prueba@urbaneats.test"])
    direccion: str = Field(min_length=5, max_length=200)
    localidad: str = Field(examples=["Chapinero"])
    latitud: Decimal | None = Field(default=None, ge=4.4, le=4.9)    # caja aproximada de Bogota
    longitud: Decimal | None = Field(default=None, ge=-74.3, le=-73.9)
    hora_apertura: time
    hora_cierre: time

    @field_validator("telefono")
    @classmethod
    def telefono_valido(cls, valor: str) -> str:
        return validadores.validar_telefono(valor)

    @field_validator("localidad")
    @classmethod
    def localidad_valida(cls, valor: str) -> str:
        return validadores.validar_localidad(valor)

    @field_validator("correo")
    @classmethod
    def correo_valido(cls, valor: str) -> str:
        if "@" not in valor or "." not in valor.split("@")[-1]:
            raise ValueError("El correo no tiene un formato válido.")
        return valor.strip().lower()

    @model_validator(mode="after")
    def reglas_generales(self):
        # El NIT y su DV se revisan juntos (el DV depende del NIT).
        self.nit, self.digito_verificacion = validadores.validar_nit(self.nit, self.digito_verificacion)
        if self.tipo_persona == "Juridica" and not self.matricula_mercantil:
            raise ValueError("Una persona jurídica debe registrar su matrícula mercantil (Cámara de Comercio).")
        if not validadores.cubre_franja_nocturna(self.hora_apertura, self.hora_cierre):
            raise ValueError("Urban Eats vincula cocinas nocturnas: el horario debe cubrir parte de la franja "
                             "de 10:00 p. m. a 6:00 a. m.")
        return self


class SolicitudCrear(SolicitudBase):
    """Lo que manda Spring Boot al crear una solicitud."""

    codigo_usuario: int = Field(gt=0, description="Usuario que solicita la vinculación")
    autoriza_datos: bool = Field(description="Autorización de tratamiento de datos (Ley 1581 de 2012)")
    enviar: bool = Field(default=True, description="False la deja como Borrador")

    @field_validator("autoriza_datos")
    @classmethod
    def debe_autorizar(cls, valor: bool) -> bool:
        if not valor:
            raise ValueError("Debe autorizar el tratamiento de datos personales (Ley 1581 de 2012).")
        return valor


class SolicitudActualizar(SolicitudBase):
    """Corregir los datos de una solicitud (solo en Borrador o Requiere ajustes)."""

    enviar: bool = True


class CambioEstado(BaseModel):
    """Decision del tecnico sobre una solicitud."""

    estado: Literal["En revision", "Requiere ajustes", "Aprobada", "Rechazada"]
    observaciones: str | None = Field(default=None, max_length=500)
    codigo_revisor: int = Field(gt=0)

    @model_validator(mode="after")
    def observacion_obligatoria(self):
        if self.estado in ("Requiere ajustes", "Rechazada") and not (self.observaciones or "").strip():
            raise ValueError("Debe explicar el motivo cuando pide ajustes o rechaza una solicitud.")
        return self


class DocumentoRespuesta(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    codigo_documento: int
    tipo_documento: str
    numero_documento: str | None
    titular: str | None
    resultado: str | None
    horas_capacitacion: int | None
    fecha_expedicion: date
    fecha_vencimiento: date | None
    tiene_archivo: bool = False
    estado_revision: str
    observacion: str | None


class HistorialRespuesta(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    estado_anterior: str | None
    estado_nuevo: str
    codigo_usuario: int | None
    comentario: str | None
    fecha: datetime


class SolicitudRespuesta(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    codigo_solicitud: int
    codigo_usuario: int
    nombre_comercial: str
    razon_social: str
    tipo_persona: str
    nit: str
    digito_verificacion: str
    matricula_mercantil: str | None
    tipo_establecimiento: str
    categoria: str
    telefono: str
    correo: str
    direccion: str
    localidad: str
    latitud: Decimal | None
    longitud: Decimal | None
    hora_apertura: time
    hora_cierre: time
    autoriza_datos: bool
    estado: str
    observaciones: str | None
    codigo_revisor: int | None
    codigo_restaurante: int | None
    es_demo: bool
    fecha_creacion: datetime | None
    fecha_actualizacion: datetime | None


class SolicitudDetalle(SolicitudRespuesta):
    documentos: list[DocumentoRespuesta] = []
    historial: list[HistorialRespuesta] = []


class PaginaSolicitudes(BaseModel):
    total: int
    pagina: int
    tamano: int
    resultados: list[SolicitudRespuesta]


class ItemCumplimiento(BaseModel):
    requisito: str
    cumple: bool
    detalle: str


class Cumplimiento(BaseModel):
    """Checklist automatico: que documentos faltan o estan vencidos."""

    codigo_solicitud: int
    puede_aprobarse: bool
    items: list[ItemCumplimiento]

