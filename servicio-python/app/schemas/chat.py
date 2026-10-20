"""Esquemas del asistente virtual (chatbot)."""
from typing import Literal

from pydantic import BaseModel, Field


class Turno(BaseModel):
    rol: Literal["usuario", "asistente"]
    texto: str = Field(max_length=2000)


class ChatEntrada(BaseModel):
    """Lo que manda Spring Boot: quien pregunta (ya autenticado por Java) y el mensaje."""

    mensaje: str = Field(min_length=1, max_length=500)
    codigo_usuario: int | None = Field(default=None, description="null si no inicio sesión")
    rol: Literal["anonimo", "cliente", "cocina", "repartidor", "tecnico"] = "anonimo"
    nombre: str | None = Field(default=None, max_length=100)
    codigo_restaurante: int | None = Field(default=None, description="Cocina activa si el rol es cocina")
    historial: list[Turno] = Field(default=[], max_length=6)


class ChatSalida(BaseModel):
    respuesta: str
    modo: Literal["ia", "basico"]
    intencion: str
    segundos: float
