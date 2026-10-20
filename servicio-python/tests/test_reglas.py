"""Pruebas de reglas de negocio sin base de datos: cumplimiento, chat y exportacion."""
from datetime import date, time, timedelta
from types import SimpleNamespace

from app.models import solicitud as m
from app.services import chat_service, exportador
from app.services.ruta_service import distancia_haversine
from app.services.solicitud_service import evaluar_cumplimiento

HOY = date(2026, 10, 19)


def documento(tipo, dias_atras=10, vence_en=None, resultado=None, horas=None, revision="Pendiente"):
    return SimpleNamespace(tipo_documento=tipo, fecha_expedicion=HOY - timedelta(days=dias_atras),
                           fecha_vencimiento=HOY + timedelta(days=vence_en) if vence_en is not None else None,
                           resultado=resultado, horas_capacitacion=horas, estado_revision=revision)


def solicitud_con(documentos, tipo_persona="Juridica"):
    return SimpleNamespace(codigo_solicitud=1, tipo_persona=tipo_persona, documentos=documentos)


def completos():
    return [documento(m.RUT), documento(m.CAMARA_COMERCIO),
            documento(m.CONCEPTO_SANITARIO, vence_en=200, resultado="Favorable"),
            documento(m.CERT_MANIPULACION, horas=10), documento(m.CERT_MEDICO)]


def test_con_todos_los_documentos_se_puede_aprobar():
    assert evaluar_cumplimiento(solicitud_con(completos()), HOY).puede_aprobarse


def test_concepto_sanitario_vencido_no_se_aprueba():
    docs = completos()
    docs[2] = documento(m.CONCEPTO_SANITARIO, dias_atras=400, vence_en=-5, resultado="Favorable")
    resultado = evaluar_cumplimiento(solicitud_con(docs), HOY)
    assert not resultado.puede_aprobarse


def test_concepto_con_requerimientos_no_es_suficiente():
    docs = completos()
    docs[2] = documento(m.CONCEPTO_SANITARIO, vence_en=200, resultado="Favorable con requerimientos")
    assert not evaluar_cumplimiento(solicitud_con(docs), HOY).puede_aprobarse


def test_camara_de_comercio_vieja_no_cuenta():
    docs = completos()
    docs[1] = documento(m.CAMARA_COMERCIO, dias_atras=45)
    assert not evaluar_cumplimiento(solicitud_con(docs), HOY).puede_aprobarse


def test_curso_de_manipulacion_de_pocas_horas_no_cuenta():
    docs = completos()
    docs[3] = documento(m.CERT_MANIPULACION, horas=4)
    assert not evaluar_cumplimiento(solicitud_con(docs), HOY).puede_aprobarse


def test_documento_rechazado_por_el_tecnico_no_cuenta():
    docs = completos()
    docs[0] = documento(m.RUT, revision="Rechazado")
    assert not evaluar_cumplimiento(solicitud_con(docs), HOY).puede_aprobarse


def test_persona_natural_no_necesita_camara_de_comercio():
    docs = [d for d in completos() if d.tipo_documento != m.CAMARA_COMERCIO]
    assert evaluar_cumplimiento(solicitud_con(docs, "Natural"), HOY).puede_aprobarse


def test_intenciones_del_chat():
    assert chat_service.detectar_intencion("¿Dónde está mi pedido?") == "pedido"
    assert chat_service.detectar_intencion("Recomiéndame algo balanceado") == "menu"
    assert chat_service.detectar_intencion("¿El caldo tiene gluten?") == "alergenos"
    assert chat_service.detectar_intencion("hola") == "general"


def test_precio_maximo_del_mensaje():
    assert chat_service.precio_maximo("algo por menos de $20.000") == 20000
    assert chat_service.precio_maximo("hasta 15 mil") == 15000
    assert chat_service.precio_maximo("algo rico") is None


def test_cocina_abierta_de_madrugada():
    assert chat_service.abierta(time(21), time(6), time(2, 30))
    assert not chat_service.abierta(time(21), time(6), time(12))


def test_exportar_csv_con_tildes():
    contenido, tipo, nombre = exportador.exportar("Prueba", [("a", "Cocina")], [{"a": "Arepas La Costeña"}], "csv")
    assert tipo.startswith("text/csv") and nombre.endswith(".csv")
    assert "Costeña" in contenido.decode("utf-8")


def test_exportar_pdf_y_excel():
    filas = [{"a": "Caldo – casero", "b": 12000.0}]
    columnas = [("a", "Plato"), ("b", "Precio")]
    assert exportador.exportar("Prueba", columnas, filas, "pdf")[0].startswith(b"%PDF")
    assert exportador.exportar("Prueba", columnas, filas, "xlsx")[0][:2] == b"PK"  # un .xlsx es un zip


def test_distancia_haversine_bogota():
    # Plaza de Bolivar -> Parque de la 93: unos 9 km en linea recta
    assert 8000 < distancia_haversine((4.5981, -74.0760), (4.6767, -74.0483)) < 10000


def test_alergeno_mencionado():
    assert chat_service.alergeno_mencionado("¿Qué platos no tienen gluten?") == "Gluten"
    assert chat_service.alergeno_mencionado("soy alérgico al maní") == "Maní"
    assert chat_service.alergeno_mencionado("algo rico") is None
