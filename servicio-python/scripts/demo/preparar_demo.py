"""Deja la base lista para la sustentacion (se puede repetir antes de cada ensayo).

    .venv\\Scripts\\python.exe -m scripts.demo.preparar_demo

1. Borra SOLO lo marcado como demo: solicitudes con NIT de prueba (y la cocina que
   hayan creado al aprobarse) y los pedidos historicos "DEMO · ...".
2. Crea dos solicitudes de cocina para que el tecnico las revise en vivo:
   - "Cocina de Prueba Urban Eats": todo en regla -> se puede aprobar.
   - "Cocina Sin Papeles": concepto sanitario vencido -> el sistema no deja aprobarla.
3. Genera ~200 pedidos de los ultimos 30 dias (mas de noche que de dia) para los reportes.
"""
import random
from datetime import date, datetime, time, timedelta

from sqlalchemy import text

from app.core.database import Base, SesionLocal, motor
from app.schemas.solicitud import SolicitudCrear
from app.services import documento_service, solicitud_service
from scripts.demo import documentos_prueba
from scripts.demo.comun import (CORREO_SOLICITANTE, NIT_COCINA_PRUEBA, NIT_COCINA_SIN_PAPELES, NITS_DEMO,
                                PREFIJO_PEDIDO_DEMO, exigir_modo_demo, titulo)

PEDIDOS_HISTORICOS = 200
DIAS_HISTORIA = 30


def limpiar(sesion, nits: list[str] = NITS_DEMO, borrar_historicos: bool = True) -> None:
    """Borra las solicitudes con esos NIT (y la cocina que crearon) y, si se pide, los pedidos historicos."""
    titulo("Limpiando datos de demostración anteriores")
    marcadores = ", ".join(f"'{nit}'" for nit in nits)  # constantes del codigo, no datos del usuario

    # Cocinas creadas al aprobar solicitudes de prueba
    cocinas = [fila[0] for fila in sesion.execute(text(
        f"SELECT CodigoRestaurante FROM solicitud_restaurante WHERE Nit IN ({marcadores}) "
        "AND CodigoRestaurante IS NOT NULL")).all()]
    for cocina in cocinas:
        borrar_pedidos(sesion, "e.CodigoRestaurante = :c", {"c": cocina})
        sesion.execute(text("DELETE FROM alergeno WHERE CodigoPlato IN (SELECT pl.CodigoPlato FROM plato pl "
                            "JOIN menu mn ON mn.CodigoMenu = pl.CodigoMenu WHERE mn.CodigoRestaurante = :c)"), {"c": cocina})
        sesion.execute(text("DELETE pl FROM plato pl JOIN menu mn ON mn.CodigoMenu = pl.CodigoMenu "
                            "WHERE mn.CodigoRestaurante = :c"), {"c": cocina})
        sesion.execute(text("DELETE FROM menu WHERE CodigoRestaurante = :c"), {"c": cocina})
    sesion.execute(text(f"UPDATE solicitud_restaurante SET CodigoRestaurante = NULL WHERE Nit IN ({marcadores})"))
    for cocina in cocinas:
        sesion.execute(text("DELETE FROM restaurante WHERE CodigoRestaurante = :c"), {"c": cocina})

    # El solicitante de prueba vuelve a ser solo cliente (si ya no tiene cocinas)
    usuario = codigo_solicitante(sesion)
    sesion.execute(text("DELETE g FROM gerente g WHERE g.CodigoUsuario = :u AND NOT EXISTS "
                        "(SELECT 1 FROM restaurante r WHERE r.CodigoGerente = g.CodigoGerente)"), {"u": usuario})
    sesion.execute(text("DELETE ru FROM rol_usuario ru JOIN rol r ON r.CodigoRol = ru.CodigoRol "
                        "WHERE ru.CodigoUsuario = :u AND r.NombreRol = 'Gerente' "
                        "AND NOT EXISTS (SELECT 1 FROM gerente g WHERE g.CodigoUsuario = :u)"), {"u": usuario})

    # Solicitudes de prueba (sus documentos e historial se borran en cascada)
    borradas = sesion.execute(text(f"DELETE FROM solicitud_restaurante WHERE Nit IN ({marcadores})")).rowcount

    # Pedidos historicos generados
    pedidos = 0
    if borrar_historicos:
        pedidos = borrar_pedidos(sesion, "e.Descripcion LIKE :p", {"p": PREFIJO_PEDIDO_DEMO + "%"})
        # Tambien los pedidos que hicieron los usuarios de prueba en ensayos anteriores.
        pedidos += borrar_pedidos(sesion, "e.CodigoCliente IN (SELECT c.CodigoCliente FROM cliente c JOIN usuario u "
                                          "ON u.CodigoUsuario = c.CodigoUsuario WHERE u.Correo LIKE '%@urbaneats.test')", {})
    sesion.commit()
    print(f"   {borradas} solicitud(es), {len(cocinas)} cocina(s) y {pedidos} pedido(s) de prueba eliminados.")


def borrar_pedidos(sesion, condicion: str, parametros: dict) -> int:
    """Borra envios (y todo lo que depende de ellos) que cumplan la condicion."""
    envios = [fila[0] for fila in sesion.execute(text(f"SELECT e.CodigoEnvio FROM envio e WHERE {condicion}"),
                                                  parametros).all()]
    for envio in envios:
        p = {"e": envio}
        sesion.execute(text("DELETE t FROM transaccion t JOIN pago pg ON pg.CodigoPago = t.CodigoPago "
                            "WHERE pg.CodigoEnvio = :e"), p)
        sesion.execute(text("DELETE FROM pago WHERE CodigoEnvio = :e"), p)
        sesion.execute(text("DELETE FROM opinion WHERE CodigoPedido IN (SELECT CodigoPedido FROM pedido WHERE CodigoEnvio = :e)"), p)
        sesion.execute(text("DELETE FROM pedido WHERE CodigoEnvio = :e"), p)  # detalle e historial en cascada
        sesion.execute(text("DELETE FROM ruta_envio WHERE CodigoEnvio = :e"), p)
        sesion.execute(text("DELETE FROM envio WHERE CodigoEnvio = :e"), p)
    return len(envios)


def codigo_solicitante(sesion) -> int:
    codigo = sesion.execute(text("SELECT CodigoUsuario FROM usuario WHERE Correo = :c"),
                            {"c": CORREO_SOLICITANTE}).scalar()
    if codigo is None:
        raise SystemExit("No existe el usuario de prueba solicitante.demo: ejecute database/fase6/03_datos_bogota_nocturno.sql")
    return codigo


def crear_solicitudes(sesion) -> None:
    titulo("2. Creando las cocinas de prueba para revisar en vivo")
    usuario = codigo_solicitante(sesion)
    hoy = date.today()

    buena = solicitud_service.crear(sesion, SolicitudCrear(
        codigo_usuario=usuario, autoriza_datos=True, nombre_comercial="Cocina de Prueba Urban Eats",
        razon_social="Cocina de Prueba Urban Eats S.A.S.", tipo_persona="Juridica", nit=NIT_COCINA_PRUEBA,
        digito_verificacion="8", matricula_mercantil="03999888", tipo_establecimiento="Cocina oculta",
        categoria="Casera", telefono="3205550199", correo="cocina.prueba@urbaneats.test",
        direccion="Cl. 53 # 27-20", localidad="Teusaquillo", latitud=4.6431, longitud=-74.0761,
        hora_apertura=time(21, 0), hora_cierre=time(5, 0)), es_demo=True)
    pdfs = documentos_prueba.juego_completo("Cocina de Prueba", NIT_COCINA_PRUEBA, "8")
    agregar(sesion, buena.codigo_solicitud, "RUT", hoy - timedelta(days=60), pdfs, numero="900123456-8")
    agregar(sesion, buena.codigo_solicitud, "Camara de Comercio", hoy - timedelta(days=5), pdfs, numero="03999888")
    agregar(sesion, buena.codigo_solicitud, "Concepto sanitario", hoy - timedelta(days=20), pdfs,
            numero="CS-2026-0457", resultado="Favorable", vence=hoy + timedelta(days=345))
    agregar(sesion, buena.codigo_solicitud, "Certificado manipulacion", hoy - timedelta(days=40), pdfs,
            titular="Ana Ruiz Pineda", horas=10, vence=hoy + timedelta(days=325))
    agregar(sesion, buena.codigo_solicitud, "Certificado medico", hoy - timedelta(days=40), pdfs,
            titular="Ana Ruiz Pineda", vence=hoy + timedelta(days=325))
    print(f"   Solicitud #{buena.codigo_solicitud}: Cocina de Prueba Urban Eats (Enviada, documentos en regla)")

    mala = solicitud_service.crear(sesion, SolicitudCrear(
        codigo_usuario=usuario, autoriza_datos=True, nombre_comercial="Cocina Sin Papeles",
        razon_social="Cocina Sin Papeles S.A.S.", tipo_persona="Juridica", nit=NIT_COCINA_SIN_PAPELES,
        digito_verificacion="9", matricula_mercantil="03111222", tipo_establecimiento="Cocina oculta",
        categoria="Comida rápida", telefono="3205550188", correo="sin.papeles@urbaneats.test",
        direccion="Cra. 30 # 10-15", localidad="Puente Aranda", latitud=4.6120, longitud=-74.0990,
        hora_apertura=time(22, 0), hora_cierre=time(4, 0)), es_demo=True)
    agregar(sesion, mala.codigo_solicitud, "RUT", hoy - timedelta(days=90), pdfs, numero="901555222-9")
    agregar(sesion, mala.codigo_solicitud, "Concepto sanitario", hoy - timedelta(days=400), pdfs,
            numero="CS-2025-0099", resultado="Favorable", vence=hoy - timedelta(days=35))
    print(f"   Solicitud #{mala.codigo_solicitud}: Cocina Sin Papeles (concepto vencido, sin Cámara ni manipuladores)")


def agregar(sesion, codigo, tipo, expedicion, pdfs, numero=None, titular=None, resultado=None, horas=None, vence=None):
    documento_service.agregar(sesion, codigo, tipo, expedicion, numero, titular, resultado, horas, vence,
                              f"{tipo}.pdf", pdfs[tipo])


def generar_pedidos(sesion) -> None:
    titulo(f"3. Generando {PEDIDOS_HISTORICOS} pedidos de los últimos {DIAS_HISTORIA} días")
    azar = random.Random(2026)  # semilla fija: siempre genera los mismos datos
    cocinas = sesion.execute(text("SELECT CodigoRestaurante FROM restaurante WHERE Activo = 1 AND Nombre <> 'Cocina de Prueba Urban Eats'")).scalars().all()
    platos_por_cocina = {c: sesion.execute(text(
        "SELECT pl.CodigoPlato, pl.Nombre, pl.Precio FROM plato pl JOIN menu mn ON mn.CodigoMenu = pl.CodigoMenu "
        "WHERE mn.CodigoRestaurante = :c AND pl.Precio IS NOT NULL"), {"c": c}).all() for c in cocinas}
    clientes = sesion.execute(text(
        "SELECT c.CodigoCliente, c.CodigoUsuario, (SELECT MIN(d.CodigoDireccion) FROM direccion d "
        "WHERE d.CodigoUsuario = c.CodigoUsuario) FROM cliente c JOIN rol_usuario ru ON ru.CodigoUsuario = c.CodigoUsuario "
        "JOIN rol r ON r.CodigoRol = ru.CodigoRol WHERE r.NombreRol = 'Cliente'")).all()
    repartidores = sesion.execute(text("SELECT CodigoRepartidor, CodigoUsuario FROM repartidor")).all()

    # Peso de cada hora del dia: la franja de 10 p. m. a 6 a. m. pesa mucho mas.
    pesos_hora = [9, 8, 6, 5, 4, 3, 2, 1, 1, 1, 1, 2, 3, 2, 1, 1, 1, 2, 3, 4, 5, 7, 9, 10]
    creados = 0
    for _ in range(PEDIDOS_HISTORICOS):
        cocina = azar.choice(cocinas)
        if not platos_por_cocina[cocina]:
            continue
        cliente, usuario_cliente, direccion = azar.choice(clientes)
        repartidor, usuario_repartidor = azar.choice(repartidores)
        hora = azar.choices(range(24), weights=pesos_hora)[0]
        inicio = datetime.combine(date.today() - timedelta(days=azar.randint(1, DIAS_HISTORIA)),
                                  time(hora, azar.randint(0, 59)))
        cancelado = azar.random() < 0.08
        lineas = [(azar.choice(platos_por_cocina[cocina]), azar.randint(1, 2)) for _ in range(azar.randint(1, 3))]
        total = sum(float(plato[2]) * cantidad for plato, cantidad in lineas)
        descripcion = PREFIJO_PEDIDO_DEMO + ", ".join(f"{c}× {p[1]}" for p, c in lineas)

        envio = sesion.execute(text(
            "INSERT INTO envio (CodigoCliente, CodigoRepartidor, CodigoRestaurante, CodigoDireccion, Descripcion, FechaEnvio, HoraEntrega) "
            "VALUES (:cl, :rep, :co, :dir, :desc, :f, :h)"),
            {"cl": cliente, "rep": None if cancelado else repartidor, "co": cocina, "dir": direccion,
             "desc": descripcion[:300], "f": inicio.date(), "h": (inicio + timedelta(minutes=40)).time()}).lastrowid
        pedido = sesion.execute(text("SELECT CodigoPedido FROM pedido WHERE CodigoEnvio = :e"), {"e": envio}).scalar_one()
        estado = "Cancelado" if cancelado else "Entregado"
        sesion.execute(text("UPDATE pedido SET FechaPedido = :f, FechaHora = :fh, Estado = :es WHERE CodigoPedido = :p"),
                       {"f": inicio.date(), "fh": inicio, "es": estado, "p": pedido})
        for plato, cantidad in lineas:
            sesion.execute(text("INSERT INTO detalle_pedido (CodigoPedido, CodigoPlato, Cantidad, PrecioUnitario) "
                                "VALUES (:p, :pl, :c, :pr)"), {"p": pedido, "pl": plato[0], "c": cantidad, "pr": plato[2]})
        pago = sesion.execute(text("INSERT INTO pago (CodigoCliente, CodigoEnvio, Monto, FechaPago, HoraPago, EstadoPago) "
                                   "VALUES (:cl, :e, :m, :f, :h, 'approved')"),
                              {"cl": cliente, "e": envio, "m": total, "f": inicio.date(), "h": inicio.time()}).lastrowid
        sesion.execute(text("INSERT INTO transaccion (TransaccionID, CodigoPago, MetodoPago, CodigoRespuesta) "
                            "VALUES (:t, :p, 'demo', 'accredited')"), {"t": f"DEMO-H{pago}", "p": pago})

        # Historial con tiempos realistas: preparar 10-25 min, esperar 3-10, viajar 12-30.
        hitos = [(None, "Solicitado", usuario_cliente, 0)]
        if cancelado:
            hitos.append(("Solicitado", "Cancelado", usuario_cliente, azar.randint(1, 5)))
        else:
            t1 = azar.randint(1, 4)
            t2 = t1 + azar.randint(10, 25)
            t3 = t2 + azar.randint(3, 10)
            t4 = t3 + azar.randint(12, 30)
            hitos += [("Solicitado", "En preparacion", None, t1), ("En preparacion", "Listo para envio", None, t2),
                      ("Listo para envio", "En camino", usuario_repartidor, t3), ("En camino", "Entregado", usuario_repartidor, t4)]
        for anterior, nuevo, quien, minutos in hitos:
            sesion.execute(text("INSERT INTO historial_pedido (CodigoPedido, EstadoAnterior, EstadoNuevo, CodigoUsuario, Comentario, Fecha) "
                                "VALUES (:p, :a, :n, :u, 'Pedido de demostración', :f)"),
                           {"p": pedido, "a": anterior, "n": nuevo, "u": quien, "f": inicio + timedelta(minutes=minutos)})
        creados += 1
    sesion.commit()
    print(f"   {creados} pedidos creados (el {round(100 * sum(pesos_hora[h] for h in (22, 23, 0, 1, 2, 3, 4, 5)) / sum(pesos_hora))} % "
          "esperado en franja nocturna).")


def main() -> None:
    exigir_modo_demo()
    import app.models  # noqa: F401
    Base.metadata.create_all(motor, checkfirst=True)
    with SesionLocal() as sesion:
        limpiar(sesion)
        crear_solicitudes(sesion)
        generar_pedidos(sesion)
    titulo("Listo. Usuarios de prueba en database/fase6/USUARIOS_DE_PRUEBA.md")
    print("   Técnico: tecnico.demo@urbaneats.test -> /tecnico/solicitudes para aprobar la Cocina de Prueba.")


if __name__ == "__main__":
    main()
