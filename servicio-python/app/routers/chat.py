"""Modulo del asistente virtual (/api/chat)."""
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.database import obtener_sesion
from app.core.seguridad import verificar_api_key
from app.schemas.chat import ChatEntrada, ChatSalida
from app.services import chat_service

router = APIRouter(prefix="/api/chat", tags=["Asistente virtual (IA local)"],
                   dependencies=[Depends(verificar_api_key)])


@router.post("", response_model=ChatSalida, summary="Responder un mensaje con datos reales de la base")
def responder(entrada: ChatEntrada, sesion: Session = Depends(obtener_sesion)):
    return chat_service.responder(sesion, entrada)
