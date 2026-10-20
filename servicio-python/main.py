"""Punto de entrada del servicio Python de Urban Eats.

Uso:  python main.py

Antes de arrancar revisa la configuracion, la base de datos y la IA local, y muestra
el estado de cada una. Si la base de datos no responde, explica que hacer y se
detiene sin mostrar un traceback (item 5: "se ejecuta sin errores al iniciar").
"""
import logging
import sys

import uvicorn
from sqlalchemy.exc import OperationalError

from app.core.config import configuracion
from app.core.database import Base, motor, probar_conexion
from app.services import ollama_cliente

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")

# En Windows la consola a veces no muestra emojis ni tildes; se fuerza UTF-8.
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")


def verificar_entorno() -> bool:
    print("=" * 60)
    print(" Urban Eats - servicio Python (FastAPI)")
    print("=" * 60)

    if not configuracion.api_key:
        print("[X] Falta API_KEY en servicio-python/.env (debe ser igual a PYTHON_API_KEY de Spring Boot).")
        return False
    print("[OK] API_KEY configurada")

    try:
        version = probar_conexion()
        print(f"[OK] Base de datos conectada (MariaDB/MySQL {version})")
    except OperationalError as error:
        print("[X] No se pudo conectar a la base de datos.")
        print("    Revise que XAMPP (MySQL) este encendido y que DB_URL del .env sea correcta.")
        print(f"    Detalle: {error.orig}")
        return False

    try:
        # Crea SOLO las tablas propias de Python que falten (nunca modifica las existentes).
        import app.models  # noqa: F401  registra los modelos
        Base.metadata.create_all(motor, checkfirst=True)
        print("[OK] Tablas del servicio verificadas (solicitudes, documentos, rutas)")
    except Exception as error:  # noqa: BLE001
        print(f"[X] No se pudieron crear las tablas del servicio: {error}")
        return False

    estado_ia = ollama_cliente.estado()
    if estado_ia["disponible"]:
        print(f"[OK] IA local lista: Ollama con el modelo {estado_ia['modelo']} (cargando en memoria...)")
        ollama_cliente.precargar()
    else:
        print(f"[!] IA local no disponible ({estado_ia['detalle']}): el chat respondera en modo basico.")

    proveedor = "OpenRouteService" if configuracion.ors_api_key else "linea recta (falta ORS_API_KEY)"
    print(f"[OK] Rutas del domicilio: {proveedor}")
    print(f"[OK] Documentacion interactiva: http://{configuracion.host}:{configuracion.puerto}/docs")
    print("=" * 60)
    return True


if __name__ == "__main__":
    if not verificar_entorno():
        sys.exit(1)
    uvicorn.run("app.api:app", host=configuracion.host, port=configuracion.puerto, log_level="info")
