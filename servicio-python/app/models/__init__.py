"""Modelos (tablas) del servicio. Importarlos aqui registra todas las tablas en Base.metadata."""
from app.models import externas  # noqa: F401  (debe ir primero: define usuario y restaurante)
from app.models.ruta import RutaEnvio  # noqa: F401
from app.models.solicitud import DocumentoSolicitud, HistorialSolicitud, SolicitudRestaurante  # noqa: F401
