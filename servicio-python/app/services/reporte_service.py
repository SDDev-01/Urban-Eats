"""Reportes y consultas de la operacion (item 10 de la lista de chequeo).

Se escriben en SQL explicito (text) porque son consultas de agregacion
(SUM, COUNT, GROUP BY) sobre tablas que pertenecen a Spring Boot, y asi el
equipo las puede explicar igual que en la clase de bases de datos.
Todos los valores del usuario viajan como parametros (:nombre): nunca se pegan
dentro del SQL, lo que evita la inyeccion SQL.
"""
from datetime import date, timedelta
from decimal import Decimal

from sqlalchemy import text
from sqlalchemy.orm import Session

from app.core.excepciones import ReglaDeNegocio

ESTADOS_PEDIDO = ["Solicitado", "En preparacion", "Listo para envio", "En camino", "Entregado", "Cancelado"]
FRANJA_NOCTURNA_SQL = "(HOUR(p.FechaHora) >= 22 OR HOUR(p.FechaHora) < 6)"

# Columnas por las que se puede ordenar el reporte de pedidos (lista blanca: el
# texto que llega del usuario nunca se pega directamente en el SQL).
ORDENES_PEDIDOS = {
    "fecha_desc": "p.FechaHora DESC", "fecha_asc": "p.FechaHora ASC",
    "total_desc": "total DESC", "total_asc": "total ASC",
    "cocina_asc": "cocina ASC, p.FechaHora DESC",
}


def _filtros_comunes(desde: date | None, hasta: date | None, codigo_restaurante: int | None,
                     franja: str | None) -> tuple[list[str], dict]:
    """Arma las condiciones WHERE que comparten varios reportes."""
    if desde and hasta and desde > hasta:
        raise ReglaDeNegocio("La fecha inicial no puede ser mayor que la final.")
    condiciones, parametros = [], {}
    if desde:
        condiciones.append("p.FechaHora >= :desde")
        parametros["desde"] = desde
    if hasta:
        condiciones.append("p.FechaHora < :hasta")
        parametros["hasta"] = hasta + timedelta(days=1)  # incluye todo el dia final
    if codigo_restaurante:
        condiciones.append("p.CodigoRestaurante = :cocina")
        parametros["cocina"] = codigo_restaurante
    if franja == "nocturna":
        condiciones.append(FRANJA_NOCTURNA_SQL)
    elif franja == "diurna":
        condiciones.append(f"NOT {FRANJA_NOCTURNA_SQL}")
    elif franja:
        raise ReglaDeNegocio("La franja debe ser 'nocturna' o 'diurna'.")
    return condiciones, parametros


def pedidos(sesion: Session, estados: list[str] | None = None, desde: date | None = None,
            hasta: date | None = None, codigo_restaurante: int | None = None, franja: str | None = None,
            monto_min: float | None = None, monto_max: float | None = None, texto: str | None = None,
            orden: str = "fecha_desc", pagina: int = 1, tamano: int = 50) -> dict:
    """Pedidos con filtros combinables, ordenamiento y paginacion."""
    condiciones, parametros = _filtros_comunes(desde, hasta, codigo_restaurante, franja)

    if estados:
        invalidos = [estado for estado in estados if estado not in ESTADOS_PEDIDO]
        if invalidos:
            raise ReglaDeNegocio(f"Estado(s) inválido(s): {', '.join(invalidos)}.")
        marcadores = []
        for indice, estado in enumerate(estados):
            parametros[f"estado{indice}"] = estado
            marcadores.append(f":estado{indice}")
        condiciones.append(f"p.Estado IN ({', '.join(marcadores)})")
    if texto:
        condiciones.append("(CONCAT(u.Nombres, ' ', u.Apellidos) LIKE :texto OR e.Descripcion LIKE :texto)")
        parametros["texto"] = f"%{texto.strip()}%"

    filtros_total = []
    if monto_min is not None:
        filtros_total.append("total >= :monto_min")
        parametros["monto_min"] = monto_min
    if monto_max is not None:
        filtros_total.append("total <= :monto_max")
        parametros["monto_max"] = monto_max
    if monto_min is not None and monto_max is not None and monto_min > monto_max:
        raise ReglaDeNegocio("El monto mínimo no puede ser mayor que el máximo.")

    where = ("WHERE " + " AND ".join(condiciones)) if condiciones else ""
    having = ("HAVING " + " AND ".join(filtros_total)) if filtros_total else ""
    base = f"""
        SELECT p.CodigoPedido AS codigo, p.FechaHora AS fecha_hora, p.Estado AS estado,
               r.Nombre AS cocina, CONCAT(u.Nombres, ' ', u.Apellidos) AS cliente,
               COALESCE(d.Direccion, '') AS direccion, e.Descripcion AS platos,
               IF({FRANJA_NOCTURNA_SQL}, 'Nocturna', 'Diurna') AS franja,
               COALESCE(SUM(dp.Cantidad * dp.PrecioUnitario), 0) AS total
        FROM pedido p
        JOIN envio e        ON e.CodigoEnvio = p.CodigoEnvio
        JOIN restaurante r  ON r.CodigoRestaurante = p.CodigoRestaurante
        JOIN cliente c      ON c.CodigoCliente = e.CodigoCliente
        JOIN usuario u      ON u.CodigoUsuario = c.CodigoUsuario
        LEFT JOIN direccion d ON d.CodigoDireccion = e.CodigoDireccion
        LEFT JOIN detalle_pedido dp ON dp.CodigoPedido = p.CodigoPedido
        {where}
        GROUP BY p.CodigoPedido, p.FechaHora, p.Estado, r.Nombre, u.Nombres, u.Apellidos, d.Direccion, e.Descripcion
        {having}
    """
    total_filas = sesion.execute(text(f"SELECT COUNT(*) FROM ({base}) AS t"), parametros).scalar_one()
    suma = sesion.execute(text(f"SELECT COALESCE(SUM(total), 0) FROM ({base}) AS t"), parametros).scalar_one()

    parametros["limite"] = tamano
    parametros["desplazamiento"] = (pagina - 1) * tamano
    orden_sql = ORDENES_PEDIDOS.get(orden, ORDENES_PEDIDOS["fecha_desc"])
    filas = sesion.execute(text(f"{base} ORDER BY {orden_sql} LIMIT :limite OFFSET :desplazamiento"),
                           parametros).mappings().all()
    return {
        "total_registros": total_filas,
        "suma_total": float(suma),
        "pagina": pagina,
        "tamano": tamano,
        "filas": [_limpiar(fila) for fila in filas],
    }


def ventas_por_cocina(sesion: Session, desde: date | None = None, hasta: date | None = None,
                      codigo_restaurante: int | None = None, franja: str | None = None) -> list[dict]:
    """Pedidos, ventas entregadas, ticket promedio y porcentaje nocturno por cocina."""
    condiciones, parametros = _filtros_comunes(desde, hasta, codigo_restaurante, franja)
    where = ("WHERE " + " AND ".join(condiciones)) if condiciones else ""
    filas = sesion.execute(text(f"""
        SELECT r.CodigoRestaurante AS codigo, r.Nombre AS cocina,
               COUNT(*) AS pedidos,
               SUM(p.Estado = 'Entregado') AS entregados,
               SUM(p.Estado = 'Cancelado') AS cancelados,
               COALESCE(SUM(CASE WHEN p.Estado = 'Entregado' THEN t.total END), 0) AS ventas,
               COALESCE(AVG(CASE WHEN p.Estado = 'Entregado' THEN t.total END), 0) AS ticket_promedio,
               ROUND(100 * AVG({FRANJA_NOCTURNA_SQL}), 1) AS porcentaje_nocturno
        FROM pedido p
        JOIN restaurante r ON r.CodigoRestaurante = p.CodigoRestaurante
        JOIN (SELECT CodigoPedido, SUM(Cantidad * PrecioUnitario) AS total
              FROM detalle_pedido GROUP BY CodigoPedido) t ON t.CodigoPedido = p.CodigoPedido
        {where}
        GROUP BY r.CodigoRestaurante, r.Nombre
        ORDER BY ventas DESC
    """), parametros).mappings().all()
    return [_limpiar(fila) for fila in filas]


def pedidos_por_hora(sesion: Session, desde: date | None = None, hasta: date | None = None,
                     codigo_restaurante: int | None = None) -> list[dict]:
    """Cuantos pedidos entran a cada hora del dia (0 a 23): muestra la demanda nocturna."""
    condiciones, parametros = _filtros_comunes(desde, hasta, codigo_restaurante, None)
    condiciones.append("p.Estado <> 'Cancelado'")
    where = "WHERE " + " AND ".join(condiciones)
    conteos = dict(sesion.execute(text(f"""
        SELECT HOUR(p.FechaHora) AS hora, COUNT(*) AS pedidos
        FROM pedido p {where}
        GROUP BY HOUR(p.FechaHora)
    """), parametros).all())
    # Se devuelven las 24 horas, aunque alguna tenga cero pedidos.
    return [{"hora": hora, "etiqueta": f"{hora:02d}:00", "pedidos": int(conteos.get(hora, 0)),
             "franja": "Nocturna" if hora >= 22 or hora < 6 else "Diurna"} for hora in range(24)]


def tiempos_entrega(sesion: Session, desde: date | None = None, hasta: date | None = None,
                    codigo_restaurante: int | None = None) -> list[dict]:
    """Minutos promedio desde que se pide hasta que se entrega, por cocina (usa historial_pedido)."""
    condiciones, parametros = _filtros_comunes(desde, hasta, codigo_restaurante, None)
    condiciones.append("p.Estado = 'Entregado'")
    where = "WHERE " + " AND ".join(condiciones)
    filas = sesion.execute(text(f"""
        SELECT r.Nombre AS cocina, COUNT(*) AS entregas,
               ROUND(AVG(TIMESTAMPDIFF(MINUTE, h.inicio, h.fin)), 1) AS minutos_promedio,
               MIN(TIMESTAMPDIFF(MINUTE, h.inicio, h.fin)) AS minutos_minimo,
               MAX(TIMESTAMPDIFF(MINUTE, h.inicio, h.fin)) AS minutos_maximo
        FROM pedido p
        JOIN restaurante r ON r.CodigoRestaurante = p.CodigoRestaurante
        JOIN (SELECT CodigoPedido,
                     MIN(CASE WHEN EstadoNuevo = 'Solicitado' THEN Fecha END) AS inicio,
                     MAX(CASE WHEN EstadoNuevo = 'Entregado' THEN Fecha END) AS fin
              FROM historial_pedido GROUP BY CodigoPedido) h ON h.CodigoPedido = p.CodigoPedido
        {where} AND h.inicio IS NOT NULL AND h.fin IS NOT NULL
        GROUP BY r.Nombre
        ORDER BY minutos_promedio ASC
    """), parametros).mappings().all()
    return [_limpiar(fila) for fila in filas]


def platos_top(sesion: Session, desde: date | None = None, hasta: date | None = None,
               codigo_restaurante: int | None = None, etiqueta: str | None = None, limite: int = 10) -> list[dict]:
    """Platos mas vendidos (en pedidos no cancelados); se puede filtrar por sello, ej. 'Balanceado'."""
    condiciones, parametros = _filtros_comunes(desde, hasta, codigo_restaurante, None)
    condiciones.append("p.Estado <> 'Cancelado'")
    if etiqueta:
        condiciones.append("FIND_IN_SET(:etiqueta, REPLACE(pl.Etiquetas, ', ', ',')) > 0")
        parametros["etiqueta"] = etiqueta
    parametros["limite"] = limite
    filas = sesion.execute(text(f"""
        SELECT pl.Nombre AS plato, r.Nombre AS cocina, SUM(dp.Cantidad) AS unidades,
               SUM(dp.Cantidad * dp.PrecioUnitario) AS ventas, pl.Calorias AS calorias,
               COALESCE(pl.Etiquetas, '') AS sellos
        FROM detalle_pedido dp
        JOIN pedido p ON p.CodigoPedido = dp.CodigoPedido
        JOIN plato pl ON pl.CodigoPlato = dp.CodigoPlato
        JOIN restaurante r ON r.CodigoRestaurante = p.CodigoRestaurante
        WHERE {" AND ".join(condiciones)}
        GROUP BY pl.CodigoPlato, pl.Nombre, r.Nombre, pl.Calorias, pl.Etiquetas
        ORDER BY unidades DESC
        LIMIT :limite
    """), parametros).mappings().all()
    return [_limpiar(fila) for fila in filas]


def documentos_por_vencer(sesion: Session, dias: int = 30) -> list[dict]:
    """Documentos de cocinas aprobadas o en tramite que vencen pronto (o ya vencieron)."""
    if dias < 0 or dias > 365:
        raise ReglaDeNegocio("Los días deben estar entre 0 y 365.")
    filas = sesion.execute(text("""
        SELECT s.NombreComercial AS cocina, s.Estado AS estado_solicitud, d.TipoDocumento AS documento,
               d.Titular AS titular, d.FechaVencimiento AS vence,
               DATEDIFF(d.FechaVencimiento, CURDATE()) AS dias_restantes
        FROM documento_solicitud d
        JOIN solicitud_restaurante s ON s.CodigoSolicitud = d.CodigoSolicitud
        WHERE d.FechaVencimiento IS NOT NULL
          AND d.FechaVencimiento <= DATE_ADD(CURDATE(), INTERVAL :dias DAY)
          AND s.Estado <> 'Rechazada'
        ORDER BY d.FechaVencimiento ASC
    """), {"dias": dias}).mappings().all()
    return [_limpiar(fila) for fila in filas]


def _limpiar(fila) -> dict:
    """Convierte Decimal/fechas de la base en tipos que JSON, Excel y PDF entienden."""
    limpio = {}
    for clave, valor in dict(fila).items():
        if isinstance(valor, Decimal):
            # SUM y AVG devuelven Decimal: entero si no tiene decimales, si no float.
            valor = int(valor) if valor == valor.to_integral_value() else float(valor)
        limpio[clave] = valor
    return limpio
