"""Recorre un pedido completo con los 3 roles contra la aplicacion Java (Spring Boot).

Con Spring Boot (8080) y el servicio Python encendidos:
    .venv\\Scripts\\python.exe -m scripts.demo.demo_pedido_3_roles              (con pausas, para la exposicion)
    .venv\\Scripts\\python.exe -m scripts.demo.demo_pedido_3_roles --sin-pausas  (prueba automatica)

Cada rol usa su propia sesion HTTP (como tres navegadores distintos). Mientras corre,
se puede mostrar en pantalla la pagina de seguimiento del cliente: el repartidor se
mueve sobre el mapa porque el script envia su posicion paso a paso.
Ademas verifica los casos negativos: cancelar tarde y entregar dos veces.
"""
import os
import re
import sys
import time

import httpx
from sqlalchemy import text

from app.core.database import SesionLocal
from scripts.demo.comun import pausa, titulo

JAVA = os.environ.get("JAVA_URL", "http://localhost:8080")
CLAVE = "UrbanEats2026*"  # usuarios de database/fase6/USUARIOS_DE_PRUEBA.md
PAUSAS = "--sin-pausas" not in sys.argv
errores: list[str] = []


def verificar(condicion: bool, mensaje: str) -> None:
    print(f"   [{'OK' if condicion else 'FALLA'}] {mensaje}")
    if not condicion:
        errores.append(mensaje)


def iniciar_sesion(correo: str) -> httpx.Client:
    """Abre una sesion como lo haria el navegador: lee el token CSRF del formulario y envia el login."""
    cliente = httpx.Client(base_url=JAVA, timeout=60, follow_redirects=False)
    try:
        pagina = cliente.get("/login").text
    except httpx.ConnectError:
        print(f"   [X] Spring Boot no responde en {JAVA}. Inícielo primero.")
        sys.exit(1)
    respuesta = cliente.post("/login", data={"email": correo, "password": CLAVE, "_csrf": token_formulario(pagina)})
    destino = respuesta.headers.get("location", "")
    verificar(respuesta.status_code == 302 and "error" not in destino, f"Inicio de sesión de {correo}")
    return cliente


def token_formulario(html: str) -> str:
    return re.search(r'name="_csrf"\s+value="([^"]+)"', html).group(1)


def token_meta(html: str) -> str:
    return re.search(r'name="csrf-token"\s+content="([^"]+)"', html).group(1)


def main() -> None:
    with SesionLocal() as sesion:
        cocina = sesion.execute(text("SELECT CodigoRestaurante FROM restaurante WHERE Nombre = 'Las Brasas 24h'")).scalar_one()
        platos = sesion.execute(text(
            "SELECT pl.CodigoPlato, pl.Nombre FROM plato pl JOIN menu mn ON mn.CodigoMenu = pl.CodigoMenu "
            "WHERE mn.CodigoRestaurante = :c AND pl.Disponibilidad = 'Disponible' ORDER BY pl.CodigoPlato LIMIT 2"),
            {"c": cocina}).all()

    titulo("1. CLIENTE: Laura pide en 'Las Brasas 24h' y paga (modo demostración)")
    cliente = iniciar_sesion("cliente.demo@urbaneats.test")
    csrf_cliente = token_meta(cliente.get("/pago").text)
    carrito = [{"id": platos[0][0], "cantidad": 1}, {"id": platos[1][0], "cantidad": 2}]
    print(f"   Carrito: 1× {platos[0][1]}, 2× {platos[1][1]}")
    respuesta = cliente.post("/process_payment", headers={"X-CSRF-TOKEN": csrf_cliente},
                             json={"items": carrito, "metodo": "demo"}).json()
    pedido = respuesta.get("pedido")
    verificar(respuesta.get("status") == "approved" and pedido is not None,
              f"Pago aprobado y pedido #{pedido} creado en estado Solicitado")
    if pedido is None:
        print(f"   Respuesta: {respuesta}")
        sys.exit(1)
    print(f"   Seguimiento en el navegador: {JAVA}/rastreo/{pedido}")
    pausa(PAUSAS, "Muestre la pantalla de seguimiento del cliente y presione Enter")

    titulo("2. COCINA: Andrés acepta el pedido y lo marca listo")
    cocina_http = iniciar_sesion("cocina.demo@urbaneats.test")
    seleccion = cocina_http.get("/seleccion-restaurante").text
    cocina_http.post(f"/seleccion-restaurante/{cocina}", data={"_csrf": token_formulario(seleccion)})
    csrf_cocina = token_meta(cocina_http.get("/perfilRestaurante").text)
    for accion, esperado in (("aceptar", "En preparación"), ("listo", "Listo para envío")):
        r = cocina_http.post(f"/perfilRestaurante/pedido/{pedido}/estado", headers={"X-CSRF-TOKEN": csrf_cocina},
                             json={"accion": accion})
        verificar(r.status_code == 200 and r.json().get("estado") == esperado, f"Cocina: {accion} -> {esperado}")
        time.sleep(1)

    print("\n   Caso negativo: el cliente intenta cancelar cuando la cocina ya aceptó")
    r = cliente.post(f"/pedido/{pedido}/cancelar", headers={"X-CSRF-TOKEN": csrf_cliente})
    verificar(r.status_code == 409, f"Cancelación rechazada (HTTP {r.status_code}): {r.json().get('error')}")
    pausa(PAUSAS)

    titulo("3. REPARTIDOR: Sergio toma el pedido y recorre la ruta")
    repartidor = iniciar_sesion("repartidor.demo@urbaneats.test")
    panel = repartidor.get("/perfilRepartidor").text
    coincidencia = re.search(rf'data-pedido="{pedido}".*?/repartidor/tomar/(\d+)', panel, re.DOTALL)
    verificar(coincidencia is not None, "El pedido aparece en 'Pedidos disponibles'")
    envio = coincidencia.group(1)
    repartidor.post(f"/repartidor/tomar/{envio}", data={"_csrf": token_formulario(panel)})
    panel = repartidor.get("/perfilRepartidor").text
    verificar(f"/repartidor/entregar/{envio}" in panel, "El pedido quedó 'En camino' con este repartidor")

    ruta = repartidor.get(f"/rastreo/{pedido}/ruta").json()
    puntos = ruta.get("coordenadas", [])
    print(f"   Ruta: {ruta.get('distanciaMetros', 0) / 1000:.1f} km ({ruta.get('proveedor')}), {len(puntos)} puntos")
    csrf_repartidor = token_meta(panel)
    paso = max(1, len(puntos) // 12)
    for indice, (lat, lng) in enumerate(puntos[::paso], start=1):
        r = repartidor.post("/repartidor/ubicacion", headers={"X-CSRF-TOKEN": csrf_repartidor},
                            json={"envioId": int(envio), "lat": lat, "lng": lng})
        print(f"   Posición {indice}: {lat:.5f}, {lng:.5f} -> HTTP {r.status_code}")
        time.sleep(1.5 if PAUSAS else 0.2)
    pausa(PAUSAS, "El cliente ve la moto avanzar en el mapa. Enter para entregar")

    repartidor.post(f"/repartidor/entregar/{envio}", data={"_csrf": token_formulario(panel)})
    verificar("Pedido marcado como entregado" in repartidor.get("/perfilRepartidor").text, "Pedido entregado")
    print("\n   Caso negativo: entregar dos veces el mismo pedido")
    respuesta = repartidor.post(f"/repartidor/entregar/{envio}", data={"_csrf": token_formulario(panel)})
    mensaje = repartidor.get("/perfilRepartidor").text
    verificar("no puede pasar" in mensaje or "no está asignado" in mensaje,
              "La segunda entrega se rechaza con un mensaje en pantalla")

    titulo("4. CLIENTE: ve el pedido entregado y su historial")
    datos = cliente.get(f"/rastreo/{pedido}/datos").json()
    verificar(datos["estado"] == "Entregado", f"Estado final: {datos['estadoEtiqueta']}")
    for hito in datos["historial"]:
        print(f"   {hito['hora']:>9}  {hito['estado']:<18} {hito['comentario']}")
    verificar(len(datos["historial"]) == 5, "Historial con los 5 cambios de estado")

    titulo("Resultado" if not errores else f"Resultado: {len(errores)} verificación(es) fallaron")
    print("   Todas las verificaciones pasaron." if not errores else "\n".join(f"   - {e}" for e in errores))
    sys.exit(1 if errores else 0)


if __name__ == "__main__":
    main()
