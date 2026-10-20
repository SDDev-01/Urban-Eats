"""Crea la aplicacion FastAPI y registra los modulos (routers) y el manejo de errores."""
from fastapi import FastAPI

from app.core.excepciones import registrar_manejadores
from app.routers import chat, documentos, reportes, rutas, salud, solicitudes

DESCRIPCION = """
Servicio Python de **Urban Eats** (comida real para quienes mueven la noche).

* **Solicitudes de cocinas**: CRUD de la vinculación con verificación de documentos sanitarios.
* **Reportes**: pedidos, ventas, horas, tiempos y platos, con filtros y exportación CSV/Excel/PDF.
* **Asistente virtual**: IA local con Ollama, conectada a los pedidos reales.
* **Rutas**: recorrido del domicilio con OpenRouteService.

Para probar: botón **Authorize** y escribir la clave `X-API-KEY` del `.env`.
"""

app = FastAPI(title="Urban Eats – Servicio Python", version="1.0.0", description=DESCRIPCION)

registrar_manejadores(app)
app.include_router(salud.router)
app.include_router(solicitudes.router)
app.include_router(documentos.router)
app.include_router(reportes.router)
app.include_router(chat.router)
app.include_router(rutas.router)
