"""Demostracion por consola del modulo Python (items 5 a 10 de la lista de chequeo).

Con el servicio encendido (python main.py), en otra consola:
    .venv\\Scripts\\python.exe -m scripts.demo.demo_cocina_paso_a_paso
    (agregue --sin-pausas para que no espere Enter entre pasos)

Recorre el CRUD completo por la API, mostrando las validaciones y los errores
controlados con try/except, y termina con un reporte exportado a Excel.
"""
import sys
from datetime import date, timedelta
from pathlib import Path

import httpx

from app.core.config import configuracion
from scripts.demo import documentos_prueba
from scripts.demo.comun import (CORREO_SOLICITANTE, CORREO_TECNICO, NIT_BORRADOR, NIT_PASO_A_PASO, pausa,
                                titulo)

API = f"http://{configuracion.host}:{configuracion.puerto}/api"
PAUSAS = "--sin-pausas" not in sys.argv


def llamar(metodo: str, ruta: str, **kwargs) -> httpx.Response:
    """Hace la peticion y muestra el resultado; los errores de red se capturan."""
    try:
        respuesta = httpx.request(metodo, API + ruta, headers={"X-API-KEY": configuracion.api_key}, timeout=30, **kwargs)
    except httpx.ConnectError:
        print("   [X] El servicio no responde. Inícielo primero con: python main.py")
        sys.exit(1)
    print(f"   {metodo} {ruta} -> HTTP {respuesta.status_code}")
    return respuesta


def mostrar_error(respuesta: httpx.Response) -> None:
    datos = respuesta.json()
    print(f"   Mensaje: {datos.get('error')}")
    for campo, mensaje in (datos.get("campos") or {}).items():
        print(f"     - {campo}: {mensaje}")


def main() -> None:
    hoy = date.today()
    codigo_usuario = buscar_usuario(CORREO_SOLICITANTE)
    tecnico = buscar_usuario(CORREO_TECNICO)
    base = {
        "codigo_usuario": codigo_usuario, "autoriza_datos": True, "nombre_comercial": "Cocina Paso a Paso",
        "razon_social": "Cocina Paso a Paso S.A.S.", "tipo_persona": "Juridica", "nit": NIT_PASO_A_PASO,
        "digito_verificacion": "7", "matricula_mercantil": "03777666", "tipo_establecimiento": "Cocina oculta",
        "categoria": "Saludable / balanceada", "telefono": "3205550177", "correo": "paso.a.paso@urbaneats.test",
        "direccion": "Cra. 15 # 85-20", "localidad": "Chapinero", "latitud": 4.6680, "longitud": -74.0560,
        "hora_apertura": "20:00", "hora_cierre": "04:00",
    }
    limpiar_anterior()

    titulo("PASO 1 - Validación: NIT con dígito de verificación equivocado")
    print("   Se envía el NIT 901234567 con DV 3 (el correcto, según la DIAN, es 7).")
    mostrar_error(llamar("POST", "/solicitudes", json={**base, "digito_verificacion": "3"}))
    print("   El error lo detectó Pydantic y la excepción se convirtió en un 422 en español.")
    pausa(PAUSAS)

    titulo("PASO 2 - CREATE: solicitud válida (queda como Borrador)")
    respuesta = llamar("POST", "/solicitudes", json={**base, "enviar": False})
    codigo = respuesta.json()["codigo_solicitud"]
    print(f"   Solicitud #{codigo} creada en estado {respuesta.json()['estado']}.")
    pausa(PAUSAS)

    titulo("PASO 3 - READ: listado con filtros y ordenamiento")
    datos = llamar("GET", "/solicitudes", params={"localidad": "Chapinero", "orden": "nombre_asc"}).json()
    for s in datos["resultados"]:
        print(f"   #{s['codigo_solicitud']:<3} {s['nombre_comercial']:<32} {s['estado']}")
    pausa(PAUSAS)

    titulo("PASO 4 - UPDATE: corregir el teléfono y enviarla a revisión")
    respuesta = llamar("PUT", f"/solicitudes/{codigo}", params={"codigo_usuario": codigo_usuario},
                       json={**base, "telefono": "3205550166", "enviar": True})
    print(f"   Teléfono: {respuesta.json()['telefono']} · Estado: {respuesta.json()['estado']}")
    pausa(PAUSAS)

    titulo("PASO 5 - Regla de negocio: aprobar sin documentos")
    mostrar_error(llamar("PATCH", f"/solicitudes/{codigo}/estado", json={"estado": "Aprobada", "codigo_revisor": tecnico}))
    pausa(PAUSAS)

    titulo("PASO 6 - Subir documentos (incluye un archivo falso que se rechaza)")
    mostrar_error(llamar("POST", f"/solicitudes/{codigo}/documentos",
                         data={"tipo_documento": "RUT", "fecha_expedicion": str(hoy)},
                         files={"archivo": ("rut.pdf", b"esto no es un pdf", "application/pdf")}))
    pdfs = documentos_prueba.juego_completo("Cocina Paso a Paso", NIT_PASO_A_PASO, "7")
    documentos = [
        ("RUT", {"fecha_expedicion": str(hoy - timedelta(days=30))}),
        ("Camara de Comercio", {"fecha_expedicion": str(hoy - timedelta(days=3))}),
        ("Concepto sanitario", {"fecha_expedicion": str(hoy - timedelta(days=15)), "resultado": "Favorable",
                                "fecha_vencimiento": str(hoy + timedelta(days=350))}),
        ("Certificado manipulacion", {"fecha_expedicion": str(hoy - timedelta(days=10)), "titular": "Luis Gómez",
                                      "horas_capacitacion": "12", "fecha_vencimiento": str(hoy + timedelta(days=355))}),
        ("Certificado medico", {"fecha_expedicion": str(hoy - timedelta(days=10)), "titular": "Luis Gómez"}),
    ]
    for tipo, campos in documentos:
        llamar("POST", f"/solicitudes/{codigo}/documentos", data={"tipo_documento": tipo, **campos},
               files={"archivo": (f"{tipo}.pdf", pdfs[tipo], "application/pdf")})
    pausa(PAUSAS)

    titulo("PASO 7 - Checklist automático de cumplimiento")
    cumplimiento = llamar("GET", f"/solicitudes/{codigo}/cumplimiento").json()
    for item in cumplimiento["items"]:
        print(f"   [{'OK' if item['cumple'] else 'X '}] {item['requisito']}: {item['detalle']}")
    print(f"   ¿Puede aprobarse? {'Sí' if cumplimiento['puede_aprobarse'] else 'No'}")
    pausa(PAUSAS)

    titulo("PASO 8 - Aprobar: se crea la cocina en la base de Spring Boot")
    aprobada = llamar("PATCH", f"/solicitudes/{codigo}/estado",
                      json={"estado": "Aprobada", "codigo_revisor": tecnico, "observaciones": "Documentos en regla"}).json()
    print(f"   Estado: {aprobada['estado']} · Cocina creada con código {aprobada['codigo_restaurante']}")
    pausa(PAUSAS)

    titulo("PASO 9 - DELETE: una solicitud aprobada no se puede borrar, un borrador sí")
    mostrar_error(llamar("DELETE", f"/solicitudes/{codigo}"))
    borrador = llamar("POST", "/solicitudes", json={**base, "nit": NIT_BORRADOR, "digito_verificacion":
                                                   str(digito(NIT_BORRADOR)), "enviar": False}).json()
    llamar("DELETE", f"/solicitudes/{borrador['codigo_solicitud']}")
    print("   Borrador eliminado (HTTP 204 = sin contenido).")
    pausa(PAUSAS)

    titulo("PASO 10 - Reporte: pedidos de la franja nocturna, del más caro al más barato")
    datos = llamar("GET", "/reportes/pedidos", params={"franja": "nocturna", "orden": "total_desc", "tamano": 5}).json()
    print(f"   {datos['total_registros']} pedidos nocturnos · total ${datos['suma_total']:,.0f}".replace(",", "."))
    for fila in datos["filas"]:
        print(f"   #{fila['codigo']:<4} {fila['fecha_hora'][:16]}  {fila['cocina']:<26} ${fila['total']:>9,.0f}".replace(",", "."))
    excel = llamar("GET", "/reportes/pedidos", params={"franja": "nocturna", "formato": "xlsx"})
    destino = Path("demo") / "reporte-pedidos-nocturnos.xlsx"
    destino.write_bytes(excel.content)
    print(f"   Excel guardado en servicio-python/{destino.as_posix()}")
    titulo("Fin de la demostración del módulo Python")


def buscar_usuario(correo: str) -> int:
    from sqlalchemy import text
    from app.core.database import SesionLocal
    with SesionLocal() as sesion:
        return sesion.execute(text("SELECT CodigoUsuario FROM usuario WHERE Correo = :c"), {"c": correo}).scalar_one()


def limpiar_anterior() -> None:
    """Si la demo se corre dos veces el NIT ya existiria: se borra solo esta cocina de prueba."""
    from app.core.database import SesionLocal
    from scripts.demo.preparar_demo import limpiar
    with SesionLocal() as sesion:
        limpiar(sesion, nits=[NIT_PASO_A_PASO, NIT_BORRADOR], borrar_historicos=False)


def digito(nit: str) -> int:
    from app.utils.validadores import calcular_digito_verificacion
    return calcular_digito_verificacion(nit)


if __name__ == "__main__":
    main()
