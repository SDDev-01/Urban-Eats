"""Estado del servicio (/api/salud): lo usa Spring Boot y sirve para la demo."""
from fastapi import APIRouter

from app.core.config import configuracion
from app.core.database import probar_conexion
from app.services import ollama_cliente

router = APIRouter(prefix="/api/salud", tags=["Estado del servicio"])


@router.get("", summary="¿Está funcionando? (base de datos, IA local y rutas)")
def salud():
    try:
        base_datos = {"disponible": True, "version": probar_conexion()}
    except Exception as error:  # noqa: BLE001 - cualquier fallo de conexion se reporta, no se propaga
        base_datos = {"disponible": False, "detalle": str(error)[:200]}
    return {
        "servicio": "Urban Eats - servicio Python",
        "base_datos": base_datos,
        "ia_local": ollama_cliente.estado(),
        "rutas": {"proveedor": "openrouteservice" if configuracion.ors_api_key else "linea_recta"},
        "modo_demo": configuracion.modo_demo,
    }
