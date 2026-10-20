"""Logica de negocio de las solicitudes de vinculacion de cocinas (CRUD + reglas).

Flujo:
  Borrador -> Enviada -> En revision -> Aprobada  (se crea la cocina en `restaurante`)
                                     -> Requiere ajustes -> (la cocina corrige) -> Enviada
                                     -> Rechazada
Regla de realismo: no se aprueba ninguna cocina sin sus documentos vigentes
(RUT, Camara de Comercio reciente, concepto sanitario favorable, manipuladores
con curso de 10 horas o mas y certificado medico) - Resolucion 2674 de 2013.
"""
from datetime import date, timedelta

from sqlalchemy import func, or_, select, text
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session, selectinload

from app.core.excepciones import RecursoNoEncontrado, ReglaDeNegocio, TransicionNoPermitida
from app.models import solicitud as m
from app.models.solicitud import DocumentoSolicitud, HistorialSolicitud, SolicitudRestaurante
from app.schemas.solicitud import (CambioEstado, Cumplimiento, ItemCumplimiento, SolicitudActualizar,
                                   SolicitudCrear)

CODIGO_CIUDAD_BOGOTA = 1
DIAS_VIGENCIA_CAMARA = 30        # politica de la plataforma: certificado reciente
DIAS_VIGENCIA_ANUAL = 365        # curso de manipulacion y certificado medico
HORAS_MINIMAS_MANIPULACION = 10

# Desde cada estado, a cuales puede pasar el tecnico.
TRANSICIONES_TECNICO = {
    m.ENVIADA: {m.EN_REVISION, m.REQUIERE_AJUSTES, m.APROBADA, m.RECHAZADA},
    m.EN_REVISION: {m.REQUIERE_AJUSTES, m.APROBADA, m.RECHAZADA},
}
# Estados en los que el solicitante todavia puede editar sus datos.
EDITABLES = {m.BORRADOR, m.REQUIERE_AJUSTES}
# Estados en los que se puede borrar la solicitud.
ELIMINABLES = {m.BORRADOR, m.RECHAZADA}

ORDENES = {
    "fecha_desc": SolicitudRestaurante.fecha_creacion.desc(),
    "fecha_asc": SolicitudRestaurante.fecha_creacion.asc(),
    "nombre_asc": SolicitudRestaurante.nombre_comercial.asc(),
    "nombre_desc": SolicitudRestaurante.nombre_comercial.desc(),
    "estado_asc": SolicitudRestaurante.estado.asc(),
}


# ============================== CRUD ==============================

def crear(sesion: Session, datos: SolicitudCrear, es_demo: bool = False) -> SolicitudRestaurante:
    """Create: registra una solicitud nueva (Enviada o Borrador)."""
    if _existe_nit(sesion, datos.nit):
        raise ReglaDeNegocio(f"Ya existe una solicitud con el NIT {datos.nit}.")

    campos = datos.model_dump(exclude={"enviar"})
    solicitud = SolicitudRestaurante(**campos, estado=m.ENVIADA if datos.enviar else m.BORRADOR, es_demo=es_demo)
    sesion.add(solicitud)
    try:
        sesion.flush()  # aqui la base asigna el codigo; si el NIT esta repetido falla
    except IntegrityError:
        sesion.rollback()
        raise ReglaDeNegocio(f"Ya existe una solicitud con el NIT {datos.nit}.")
    _registrar_historial(sesion, solicitud, None, solicitud.estado, datos.codigo_usuario, "Solicitud creada")
    sesion.commit()
    return solicitud


def listar(sesion: Session, estado: str | None = None, localidad: str | None = None,
           tipo: str | None = None, texto: str | None = None, codigo_usuario: int | None = None,
           orden: str = "fecha_desc", pagina: int = 1, tamano: int = 20) -> tuple[int, list[SolicitudRestaurante]]:
    """Read (lista): filtros combinables, ordenamiento y paginacion."""
    consulta = select(SolicitudRestaurante)
    if estado:
        consulta = consulta.where(SolicitudRestaurante.estado == estado)
    if localidad:
        consulta = consulta.where(SolicitudRestaurante.localidad == localidad)
    if tipo:
        consulta = consulta.where(SolicitudRestaurante.tipo_establecimiento == tipo)
    if codigo_usuario:
        consulta = consulta.where(SolicitudRestaurante.codigo_usuario == codigo_usuario)
    if texto:
        patron = f"%{texto.strip()}%"
        consulta = consulta.where(or_(SolicitudRestaurante.nombre_comercial.ilike(patron),
                                      SolicitudRestaurante.razon_social.ilike(patron),
                                      SolicitudRestaurante.nit.like(patron)))

    total = sesion.scalar(select(func.count()).select_from(consulta.subquery()))
    consulta = consulta.order_by(ORDENES.get(orden, ORDENES["fecha_desc"]))
    consulta = consulta.offset((pagina - 1) * tamano).limit(tamano)
    return total, list(sesion.scalars(consulta))


def obtener(sesion: Session, codigo: int) -> SolicitudRestaurante:
    """Read (una): con sus documentos e historial."""
    solicitud = sesion.scalar(
        select(SolicitudRestaurante)
        .options(selectinload(SolicitudRestaurante.documentos), selectinload(SolicitudRestaurante.historial))
        .where(SolicitudRestaurante.codigo_solicitud == codigo))
    if solicitud is None:
        raise RecursoNoEncontrado(f"No existe la solicitud {codigo}.")
    return solicitud


def actualizar(sesion: Session, codigo: int, datos: SolicitudActualizar, codigo_usuario: int) -> SolicitudRestaurante:
    """Update: el solicitante corrige sus datos (solo en Borrador o Requiere ajustes)."""
    solicitud = obtener(sesion, codigo)
    if solicitud.codigo_usuario != codigo_usuario:
        raise ReglaDeNegocio("Solo quien hizo la solicitud puede modificarla.")
    if solicitud.estado not in EDITABLES:
        raise TransicionNoPermitida(f"La solicitud está \"{solicitud.estado}\" y ya no se puede modificar.")
    if datos.nit != solicitud.nit and _existe_nit(sesion, datos.nit):
        raise ReglaDeNegocio(f"Ya existe una solicitud con el NIT {datos.nit}.")

    for campo, valor in datos.model_dump(exclude={"enviar"}).items():
        setattr(solicitud, campo, valor)

    anterior = solicitud.estado
    if datos.enviar:
        solicitud.estado = m.ENVIADA
    _registrar_historial(sesion, solicitud, anterior, solicitud.estado, codigo_usuario,
                         "Datos corregidos y reenviados" if datos.enviar else "Datos actualizados")
    sesion.commit()
    return solicitud


def eliminar(sesion: Session, codigo: int, codigo_usuario: int | None = None) -> None:
    """Delete: solo borradores o rechazadas (una aprobada ya creo una cocina)."""
    solicitud = obtener(sesion, codigo)
    if codigo_usuario is not None and solicitud.codigo_usuario != codigo_usuario:
        raise ReglaDeNegocio("Solo quien hizo la solicitud puede eliminarla.")
    if solicitud.estado not in ELIMINABLES:
        raise TransicionNoPermitida(f"Una solicitud \"{solicitud.estado}\" no se puede eliminar; "
                                    "solo borradores o rechazadas.")
    sesion.delete(solicitud)
    sesion.commit()


# ============================== DECISION DEL TECNICO ==============================

def cambiar_estado(sesion: Session, codigo: int, cambio: CambioEstado) -> SolicitudRestaurante:
    """El tecnico revisa, pide ajustes, aprueba o rechaza."""
    solicitud = obtener(sesion, codigo)
    permitidos = TRANSICIONES_TECNICO.get(solicitud.estado, set())
    if cambio.estado not in permitidos:
        raise TransicionNoPermitida(f"No se puede pasar de \"{solicitud.estado}\" a \"{cambio.estado}\".")

    if cambio.estado == m.APROBADA:
        revision = evaluar_cumplimiento(solicitud)
        if not revision.puede_aprobarse:
            faltantes = "; ".join(item.detalle for item in revision.items if not item.cumple)
            raise ReglaDeNegocio(f"No se puede aprobar: {faltantes}")
        solicitud.codigo_restaurante = _crear_cocina(sesion, solicitud)

    anterior = solicitud.estado
    solicitud.estado = cambio.estado
    solicitud.observaciones = cambio.observaciones
    solicitud.codigo_revisor = cambio.codigo_revisor
    _registrar_historial(sesion, solicitud, anterior, cambio.estado, cambio.codigo_revisor,
                         cambio.observaciones or f"Cambio a {cambio.estado}")
    sesion.commit()  # todo o nada: si algo fallo antes, no queda ni la cocina ni el cambio
    return solicitud


def evaluar_cumplimiento(solicitud: SolicitudRestaurante, hoy: date | None = None) -> Cumplimiento:
    """Checklist automatico de documentos. No modifica nada."""
    hoy = hoy or date.today()
    documentos = [doc for doc in solicitud.documentos if doc.estado_revision != "Rechazado"]

    def de_tipo(tipo):
        return [doc for doc in documentos if doc.tipo_documento == tipo]

    def vigente(doc, dias_maximos):
        if doc.fecha_vencimiento:
            return doc.fecha_vencimiento >= hoy
        return doc.fecha_expedicion >= hoy - timedelta(days=dias_maximos)

    items = []

    rut = de_tipo(m.RUT)
    items.append(ItemCumplimiento(requisito="RUT", cumple=bool(rut),
                                  detalle="RUT cargado" if rut else "Falta el RUT"))

    camaras = de_tipo(m.CAMARA_COMERCIO)
    if solicitud.tipo_persona == "Juridica" or camaras:
        reciente = any(doc.fecha_expedicion >= hoy - timedelta(days=DIAS_VIGENCIA_CAMARA) for doc in camaras)
        items.append(ItemCumplimiento(
            requisito="Cámara de Comercio", cumple=reciente,
            detalle=("Certificado de Cámara de Comercio reciente" if reciente else
                     f"Falta el certificado de Cámara de Comercio expedido hace {DIAS_VIGENCIA_CAMARA} días o menos")))

    conceptos = [doc for doc in de_tipo(m.CONCEPTO_SANITARIO) if vigente(doc, DIAS_VIGENCIA_ANUAL)]
    favorable = any(doc.resultado == "Favorable" for doc in conceptos)
    if favorable:
        detalle = "Concepto sanitario favorable y vigente"
    elif conceptos:
        detalle = "El concepto sanitario no es Favorable (tiene requerimientos o es desfavorable)"
    else:
        detalle = "Falta el concepto sanitario vigente de la Secretaría Distrital de Salud"
    items.append(ItemCumplimiento(requisito="Concepto sanitario", cumple=favorable, detalle=detalle))

    cursos = [doc for doc in de_tipo(m.CERT_MANIPULACION)
              if vigente(doc, DIAS_VIGENCIA_ANUAL) and (doc.horas_capacitacion or 0) >= HORAS_MINIMAS_MANIPULACION]
    items.append(ItemCumplimiento(
        requisito="Manipulación de alimentos", cumple=bool(cursos),
        detalle=(f"{len(cursos)} manipulador(es) con curso vigente" if cursos else
                 f"Falta al menos un certificado de manipulación vigente de {HORAS_MINIMAS_MANIPULACION} horas o más")))

    medicos = [doc for doc in de_tipo(m.CERT_MEDICO) if vigente(doc, DIAS_VIGENCIA_ANUAL)]
    items.append(ItemCumplimiento(
        requisito="Certificado médico", cumple=bool(medicos),
        detalle="Certificado médico vigente" if medicos else "Falta el certificado médico vigente del manipulador"))

    return Cumplimiento(codigo_solicitud=solicitud.codigo_solicitud,
                        puede_aprobarse=all(item.cumple for item in items), items=items)


# ============================== AUXILIARES ==============================

def _existe_nit(sesion: Session, nit: str) -> bool:
    return sesion.scalar(select(func.count()).where(SolicitudRestaurante.nit == nit)) > 0


def _registrar_historial(sesion, solicitud, anterior, nuevo, codigo_usuario, comentario):
    sesion.add(HistorialSolicitud(solicitud=solicitud, estado_anterior=anterior, estado_nuevo=nuevo,
                                  codigo_usuario=codigo_usuario, comentario=comentario))


def _formato_hora(hora) -> str:
    """09:00 -> '9:00 a. m.' (formato colombiano para mostrar el horario)."""
    sufijo = "a. m." if hora.hour < 12 else "p. m."
    hora12 = hora.hour % 12 or 12
    return f"{hora12}:{hora.minute:02d} {sufijo}"


def _crear_cocina(sesion: Session, solicitud: SolicitudRestaurante) -> int:
    """Al aprobar: crea (o reutiliza) el gerente, la cocina y el rol Gerente.

    Se usa SQL explicito porque estas tablas pertenecen a Spring Boot.
    Todo ocurre en la misma transaccion que el cambio de estado.
    """
    codigo_usuario = solicitud.codigo_usuario

    codigo_gerente = sesion.execute(
        text("SELECT CodigoGerente FROM gerente WHERE CodigoUsuario = :u ORDER BY CodigoGerente LIMIT 1"),
        {"u": codigo_usuario}).scalar()
    if codigo_gerente is None:
        codigo_gerente = sesion.execute(
            text("INSERT INTO gerente (CodigoUsuario) VALUES (:u)"), {"u": codigo_usuario}).lastrowid

    horario = f"{_formato_hora(solicitud.hora_apertura)} – {_formato_hora(solicitud.hora_cierre)}"
    codigo_restaurante = sesion.execute(text("""
        INSERT INTO restaurante (CodigoCiudad, CodigoGerente, Nombre, Direccion, Horario, Latitud, Longitud,
                                 HoraApertura, HoraCierre, Categoria, AceptaPedidos, Activo)
        VALUES (:ciudad, :gerente, :nombre, :direccion, :horario, :lat, :lng, :abre, :cierra, :categoria, 1, 1)
    """), {
        "ciudad": CODIGO_CIUDAD_BOGOTA, "gerente": codigo_gerente, "nombre": solicitud.nombre_comercial,
        "direccion": f"{solicitud.direccion}, {solicitud.localidad}", "horario": horario,
        "lat": solicitud.latitud, "lng": solicitud.longitud, "abre": solicitud.hora_apertura,
        "cierra": solicitud.hora_cierre, "categoria": solicitud.categoria,
    }).lastrowid

    # Un menu inicial para que la cocina pueda empezar a cargar platos.
    sesion.execute(text("INSERT INTO menu (Categoria, CodigoRestaurante) VALUES ('Platos principales', :r)"),
                   {"r": codigo_restaurante})

    sesion.execute(text("""
        INSERT INTO rol_usuario (CodigoUsuario, CodigoRol)
        SELECT :u, r.CodigoRol FROM rol r
        WHERE r.NombreRol = 'Gerente'
          AND NOT EXISTS (SELECT 1 FROM rol_usuario ru WHERE ru.CodigoUsuario = :u AND ru.CodigoRol = r.CodigoRol)
    """), {"u": codigo_usuario})
    return codigo_restaurante
