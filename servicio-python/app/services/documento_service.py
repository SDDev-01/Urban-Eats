"""Documentos de una solicitud: crear (con PDF opcional), listar, revisar y eliminar."""
import hashlib
import logging
import uuid
from datetime import date
from pathlib import Path

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.core.config import configuracion
from app.core.excepciones import RecursoNoEncontrado, ReglaDeNegocio
from app.models import solicitud as m
from app.models.solicitud import DocumentoSolicitud
from app.services import solicitud_service

log = logging.getLogger("urbaneats")

TAMANO_MAXIMO_BYTES = 5 * 1024 * 1024  # 5 MB
RESULTADOS_CONCEPTO = ["Favorable", "Favorable con requerimientos", "Desfavorable"]
# No se aceptan documentos nuevos en solicitudes ya decididas.
ESTADOS_CERRADOS = {m.APROBADA, m.RECHAZADA}


def agregar(sesion: Session, codigo_solicitud: int, tipo: str, fecha_expedicion: date,
            numero: str | None = None, titular: str | None = None, resultado: str | None = None,
            horas_capacitacion: int | None = None, fecha_vencimiento: date | None = None,
            nombre_archivo: str | None = None, contenido: bytes | None = None) -> DocumentoSolicitud:
    """Create: valida el documento y, si trae PDF, lo guarda en la carpeta uploads."""
    solicitud = solicitud_service.obtener(sesion, codigo_solicitud)
    if solicitud.estado in ESTADOS_CERRADOS:
        raise ReglaDeNegocio(f"La solicitud está \"{solicitud.estado}\": ya no recibe documentos.")

    # ---- Validaciones del documento ----
    if tipo not in m.TIPOS_DOCUMENTO:
        raise ReglaDeNegocio(f"Tipo de documento inválido. Use uno de: {', '.join(m.TIPOS_DOCUMENTO)}.")
    if fecha_expedicion > date.today():
        raise ReglaDeNegocio("La fecha de expedición no puede ser futura.")
    if fecha_vencimiento and fecha_vencimiento <= fecha_expedicion:
        raise ReglaDeNegocio("La fecha de vencimiento debe ser posterior a la de expedición.")
    if tipo == m.CONCEPTO_SANITARIO and resultado not in RESULTADOS_CONCEPTO:
        raise ReglaDeNegocio(f"El concepto sanitario debe indicar el resultado: {', '.join(RESULTADOS_CONCEPTO)}.")
    if tipo in (m.CERT_MANIPULACION, m.CERT_MEDICO) and not (titular or "").strip():
        raise ReglaDeNegocio("Indique el nombre del manipulador titular del certificado.")
    if tipo == m.CERT_MANIPULACION and (horas_capacitacion is None or horas_capacitacion <= 0):
        raise ReglaDeNegocio("Indique las horas del curso de manipulación de alimentos.")

    documento = DocumentoSolicitud(
        codigo_solicitud=codigo_solicitud, tipo_documento=tipo, numero_documento=numero,
        titular=titular, resultado=resultado if tipo == m.CONCEPTO_SANITARIO else None,
        horas_capacitacion=horas_capacitacion, fecha_expedicion=fecha_expedicion,
        fecha_vencimiento=fecha_vencimiento)

    if contenido:
        documento.ruta_archivo, documento.hash_archivo = _guardar_pdf(codigo_solicitud, nombre_archivo, contenido)

    sesion.add(documento)
    sesion.commit()
    return documento


def listar(sesion: Session, codigo_solicitud: int) -> list[DocumentoSolicitud]:
    """Read: documentos de una solicitud."""
    solicitud_service.obtener(sesion, codigo_solicitud)  # 404 si la solicitud no existe
    return list(sesion.scalars(select(DocumentoSolicitud)
                               .where(DocumentoSolicitud.codigo_solicitud == codigo_solicitud)
                               .order_by(DocumentoSolicitud.codigo_documento)))


def obtener(sesion: Session, codigo_documento: int) -> DocumentoSolicitud:
    documento = sesion.get(DocumentoSolicitud, codigo_documento)
    if documento is None:
        raise RecursoNoEncontrado(f"No existe el documento {codigo_documento}.")
    return documento


def ruta_archivo(sesion: Session, codigo_documento: int) -> Path:
    """Ruta del PDF para descargarlo."""
    documento = obtener(sesion, codigo_documento)
    if not documento.ruta_archivo:
        raise RecursoNoEncontrado("Este documento no tiene archivo adjunto.")
    ruta = configuracion.carpeta_archivos / documento.ruta_archivo
    if not ruta.is_file():
        raise RecursoNoEncontrado("El archivo del documento ya no está en el servidor.")
    return ruta


def revisar(sesion: Session, codigo_documento: int, estado_revision: str, observacion: str | None) -> DocumentoSolicitud:
    """Update: el tecnico marca el documento como Valido o Rechazado."""
    if estado_revision not in ("Pendiente", "Valido", "Rechazado"):
        raise ReglaDeNegocio("El estado de revisión debe ser Pendiente, Valido o Rechazado.")
    if estado_revision == "Rechazado" and not (observacion or "").strip():
        raise ReglaDeNegocio("Explique por qué rechaza el documento.")
    documento = obtener(sesion, codigo_documento)
    documento.estado_revision = estado_revision
    documento.observacion = observacion
    sesion.commit()
    return documento


def eliminar(sesion: Session, codigo_documento: int) -> None:
    """Delete: borra el registro y su PDF (si el archivo falla, igual se borra el registro)."""
    documento = obtener(sesion, codigo_documento)
    if documento.solicitud.estado in ESTADOS_CERRADOS:
        raise ReglaDeNegocio("No se pueden borrar documentos de una solicitud ya decidida.")
    if documento.ruta_archivo:
        try:
            (configuracion.carpeta_archivos / documento.ruta_archivo).unlink(missing_ok=True)
        except OSError as error:
            log.warning("No se pudo borrar el archivo %s: %s", documento.ruta_archivo, error)
    sesion.delete(documento)
    sesion.commit()


def _guardar_pdf(codigo_solicitud: int, nombre: str | None, contenido: bytes) -> tuple[str, str]:
    """Valida que sea un PDF de hasta 5 MB, lo guarda con nombre unico y devuelve (ruta, sha256)."""
    if len(contenido) > TAMANO_MAXIMO_BYTES:
        raise ReglaDeNegocio("El archivo supera el máximo de 5 MB.")
    # Se revisa la "firma" del archivo, no solo la extension: un PDF real empieza con %PDF.
    if not contenido.startswith(b"%PDF"):
        raise ReglaDeNegocio(f"El archivo {nombre or ''} no es un PDF válido.")

    relativa = Path("solicitudes") / str(codigo_solicitud) / f"{uuid.uuid4().hex}.pdf"
    destino = configuracion.carpeta_archivos / relativa
    try:
        destino.parent.mkdir(parents=True, exist_ok=True)
        destino.write_bytes(contenido)
    except OSError as error:
        log.error("No se pudo guardar el PDF: %s", error)
        raise ReglaDeNegocio("No se pudo guardar el archivo en el servidor.") from error
    return relativa.as_posix(), hashlib.sha256(contenido).hexdigest()
