"""Utilidades compartidas por los scripts de demostracion.

Todos los scripts se ejecutan desde la carpeta servicio-python:
    .venv\\Scripts\\python.exe -m scripts.demo.preparar_demo
"""
import sys

from app.core.config import configuracion

# Datos de prueba (ficticios). Los NIT tienen el digito de verificacion correcto.
NIT_COCINA_PRUEBA = "900123456"        # DV 8: "Cocina de Prueba Urban Eats" (todo en regla)
NIT_COCINA_SIN_PAPELES = "901555222"   # cocina con concepto sanitario vencido (debe rechazarse)
NIT_PASO_A_PASO = "901234567"          # la que crea en vivo demo_cocina_paso_a_paso.py
NIT_BORRADOR = "901234568"             # borrador que el paso a paso crea y elimina
NITS_DEMO = [NIT_COCINA_PRUEBA, NIT_COCINA_SIN_PAPELES, NIT_PASO_A_PASO, NIT_BORRADOR]

CORREO_SOLICITANTE = "solicitante.demo@urbaneats.test"
CORREO_TECNICO = "tecnico.demo@urbaneats.test"
PREFIJO_PEDIDO_DEMO = "DEMO · "  # marca de los pedidos historicos generados

if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")


def exigir_modo_demo() -> None:
    """Los scripts que borran o generan datos solo corren con MODO_DEMO=true en el .env."""
    if not configuracion.modo_demo:
        print("Este script solo funciona con MODO_DEMO=true en servicio-python/.env")
        sys.exit(1)


def titulo(texto: str) -> None:
    print("\n" + "=" * 70)
    print(f" {texto}")
    print("=" * 70)


def pausa(activa: bool, mensaje: str = "Enter para continuar...") -> None:
    if activa:
        input(f"\n>>> {mensaje}")
