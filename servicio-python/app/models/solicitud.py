"""Tablas de la vinculacion de cocinas (propias del servicio Python).

solicitud_restaurante: la solicitud de una cocina para entrar a Urban Eats.
documento_solicitud:   los documentos legales y sanitarios que la respaldan.
historial_solicitud:   cada cambio de estado (quien, cuando y por que).

Spring Boot no toca estas tablas; solo cuando una solicitud se aprueba el servicio
crea la fila en `restaurante` (ver services/solicitud_service.py).
"""
from datetime import date, datetime, time
from decimal import Decimal

from sqlalchemy import (Boolean, Date, DateTime, ForeignKey, Integer, Numeric, String, Time,
                        func)
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base

# Estados de la solicitud (texto sin tildes, igual que en Java)
BORRADOR = "Borrador"
ENVIADA = "Enviada"
EN_REVISION = "En revision"
REQUIERE_AJUSTES = "Requiere ajustes"
APROBADA = "Aprobada"
RECHAZADA = "Rechazada"
ESTADOS_SOLICITUD = [BORRADOR, ENVIADA, EN_REVISION, REQUIERE_AJUSTES, APROBADA, RECHAZADA]

# Tipos de documento exigidos (Resolucion 2674 de 2013 y registro mercantil)
RUT = "RUT"
CAMARA_COMERCIO = "Camara de Comercio"
CONCEPTO_SANITARIO = "Concepto sanitario"
CERT_MANIPULACION = "Certificado manipulacion"
CERT_MEDICO = "Certificado medico"
TIPOS_DOCUMENTO = [RUT, CAMARA_COMERCIO, CONCEPTO_SANITARIO, CERT_MANIPULACION, CERT_MEDICO]


class SolicitudRestaurante(Base):
    __tablename__ = "solicitud_restaurante"

    codigo_solicitud: Mapped[int] = mapped_column("CodigoSolicitud", Integer, primary_key=True, autoincrement=True)
    codigo_usuario: Mapped[int] = mapped_column("CodigoUsuario", ForeignKey("usuario.CodigoUsuario"))
    nombre_comercial: Mapped[str] = mapped_column("NombreComercial", String(150))
    razon_social: Mapped[str] = mapped_column("RazonSocial", String(150))
    tipo_persona: Mapped[str] = mapped_column("TipoPersona", String(10))  # Natural / Juridica
    nit: Mapped[str] = mapped_column("Nit", String(15), unique=True)
    digito_verificacion: Mapped[str] = mapped_column("DigitoVerificacion", String(1))
    matricula_mercantil: Mapped[str | None] = mapped_column("MatriculaMercantil", String(30))
    tipo_establecimiento: Mapped[str] = mapped_column("TipoEstablecimiento", String(20))  # Restaurante / Cocina oculta
    categoria: Mapped[str] = mapped_column("Categoria", String(60))
    telefono: Mapped[str] = mapped_column("Telefono", String(20))
    correo: Mapped[str] = mapped_column("Correo", String(100))
    direccion: Mapped[str] = mapped_column("Direccion", String(200))
    localidad: Mapped[str] = mapped_column("Localidad", String(40))
    latitud: Mapped[Decimal | None] = mapped_column("Latitud", Numeric(10, 7))
    longitud: Mapped[Decimal | None] = mapped_column("Longitud", Numeric(10, 7))
    hora_apertura: Mapped[time] = mapped_column("HoraApertura", Time)
    hora_cierre: Mapped[time] = mapped_column("HoraCierre", Time)
    autoriza_datos: Mapped[bool] = mapped_column("AutorizaDatos", Boolean)  # Ley 1581 de 2012
    estado: Mapped[str] = mapped_column("Estado", String(20), default=ENVIADA)
    observaciones: Mapped[str | None] = mapped_column("Observaciones", String(500))
    codigo_revisor: Mapped[int | None] = mapped_column("CodigoRevisor", ForeignKey("usuario.CodigoUsuario"))
    codigo_restaurante: Mapped[int | None] = mapped_column("CodigoRestaurante", ForeignKey("restaurante.CodigoRestaurante"))
    es_demo: Mapped[bool] = mapped_column("EsDemo", Boolean, default=False)
    fecha_creacion: Mapped[datetime] = mapped_column("FechaCreacion", DateTime, server_default=func.now())
    fecha_actualizacion: Mapped[datetime | None] = mapped_column("FechaActualizacion", DateTime, onupdate=func.now())

    documentos: Mapped[list["DocumentoSolicitud"]] = relationship(
        back_populates="solicitud", cascade="all, delete-orphan", order_by="DocumentoSolicitud.codigo_documento")
    historial: Mapped[list["HistorialSolicitud"]] = relationship(
        back_populates="solicitud", cascade="all, delete-orphan", order_by="HistorialSolicitud.codigo_historial")


class DocumentoSolicitud(Base):
    __tablename__ = "documento_solicitud"

    codigo_documento: Mapped[int] = mapped_column("CodigoDocumento", Integer, primary_key=True, autoincrement=True)
    codigo_solicitud: Mapped[int] = mapped_column(
        "CodigoSolicitud", ForeignKey("solicitud_restaurante.CodigoSolicitud", ondelete="CASCADE"))
    tipo_documento: Mapped[str] = mapped_column("TipoDocumento", String(40))
    numero_documento: Mapped[str | None] = mapped_column("NumeroDocumento", String(50))
    titular: Mapped[str | None] = mapped_column("Titular", String(150))  # p. ej. el manipulador
    resultado: Mapped[str | None] = mapped_column("Resultado", String(40))  # solo concepto sanitario
    horas_capacitacion: Mapped[int | None] = mapped_column("HorasCapacitacion", Integer)  # manipulacion
    fecha_expedicion: Mapped[date] = mapped_column("FechaExpedicion", Date)
    fecha_vencimiento: Mapped[date | None] = mapped_column("FechaVencimiento", Date)
    ruta_archivo: Mapped[str | None] = mapped_column("RutaArchivo", String(255))
    hash_archivo: Mapped[str | None] = mapped_column("HashArchivo", String(64))
    estado_revision: Mapped[str] = mapped_column("EstadoRevision", String(20), default="Pendiente")
    observacion: Mapped[str | None] = mapped_column("Observacion", String(300))

    solicitud: Mapped[SolicitudRestaurante] = relationship(back_populates="documentos")


class HistorialSolicitud(Base):
    __tablename__ = "historial_solicitud"

    codigo_historial: Mapped[int] = mapped_column("CodigoHistorial", Integer, primary_key=True, autoincrement=True)
    codigo_solicitud: Mapped[int] = mapped_column(
        "CodigoSolicitud", ForeignKey("solicitud_restaurante.CodigoSolicitud", ondelete="CASCADE"))
    estado_anterior: Mapped[str | None] = mapped_column("EstadoAnterior", String(20))
    estado_nuevo: Mapped[str] = mapped_column("EstadoNuevo", String(20))
    codigo_usuario: Mapped[int | None] = mapped_column("CodigoUsuario", Integer)
    comentario: Mapped[str | None] = mapped_column("Comentario", String(500))
    fecha: Mapped[datetime] = mapped_column("Fecha", DateTime, server_default=func.now())

    solicitud: Mapped[SolicitudRestaurante] = relationship(back_populates="historial")
