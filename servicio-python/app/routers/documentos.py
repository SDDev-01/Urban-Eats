"""Documentos legales y sanitarios de una solicitud (/api/solicitudes/{id}/documentos)."""
from datetime import date

from fastapi import APIRouter, Depends, File, Form, UploadFile, status
from fastapi.responses import FileResponse
from pydantic import BaseModel, Field
from sqlalchemy.orm import Session

from app.core.database import obtener_sesion
from app.core.seguridad import verificar_api_key
from app.schemas.solicitud import DocumentoRespuesta
from app.services import documento_service

router = APIRouter(prefix="/api", tags=["Documentos de las solicitudes"],
                   dependencies=[Depends(verificar_api_key)])


class RevisionDocumento(BaseModel):
    estado_revision: str = Field(examples=["Valido"])
    observacion: str | None = None


def _respuesta(documento) -> DocumentoRespuesta:
    respuesta = DocumentoRespuesta.model_validate(documento)
    respuesta.tiene_archivo = bool(documento.ruta_archivo)
    return respuesta


@router.post("/solicitudes/{codigo}/documentos", response_model=DocumentoRespuesta,
             status_code=status.HTTP_201_CREATED, summary="Agregar un documento (PDF opcional, máx. 5 MB)")
async def agregar(codigo: int,
                  tipo_documento: str = Form(examples=["Concepto sanitario"]),
                  fecha_expedicion: date = Form(),
                  fecha_vencimiento: date | None = Form(default=None),
                  numero_documento: str | None = Form(default=None),
                  titular: str | None = Form(default=None),
                  resultado: str | None = Form(default=None),
                  horas_capacitacion: int | None = Form(default=None),
                  archivo: UploadFile | None = File(default=None),
                  sesion: Session = Depends(obtener_sesion)):
    contenido = await archivo.read() if archivo is not None and archivo.filename else None
    documento = documento_service.agregar(
        sesion, codigo, tipo_documento, fecha_expedicion, numero_documento, titular, resultado,
        horas_capacitacion, fecha_vencimiento, archivo.filename if archivo else None, contenido)
    return _respuesta(documento)


@router.get("/solicitudes/{codigo}/documentos", response_model=list[DocumentoRespuesta],
            summary="Listar los documentos de una solicitud")
def listar(codigo: int, sesion: Session = Depends(obtener_sesion)):
    return [_respuesta(doc) for doc in documento_service.listar(sesion, codigo)]


@router.get("/documentos/{codigo_documento}/archivo", summary="Descargar el PDF de un documento")
def descargar(codigo_documento: int, sesion: Session = Depends(obtener_sesion)):
    ruta = documento_service.ruta_archivo(sesion, codigo_documento)
    return FileResponse(ruta, media_type="application/pdf", filename=f"documento-{codigo_documento}.pdf")


@router.patch("/documentos/{codigo_documento}", response_model=DocumentoRespuesta,
              summary="El técnico marca el documento como Valido o Rechazado")
def revisar(codigo_documento: int, revision: RevisionDocumento, sesion: Session = Depends(obtener_sesion)):
    return _respuesta(documento_service.revisar(sesion, codigo_documento, revision.estado_revision,
                                                revision.observacion))


@router.delete("/documentos/{codigo_documento}", status_code=status.HTTP_204_NO_CONTENT,
               summary="Eliminar un documento")
def eliminar(codigo_documento: int, sesion: Session = Depends(obtener_sesion)):
    documento_service.eliminar(sesion, codigo_documento)
