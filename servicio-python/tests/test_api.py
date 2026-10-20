"""Pruebas de la API con TestClient: seguridad y validaciones (se rechazan antes de tocar la base)."""
from fastapi.testclient import TestClient

from app.api import app
from app.core.config import configuracion

cliente = TestClient(app)
CLAVE = {"X-API-KEY": configuracion.api_key}

SOLICITUD = {
    "codigo_usuario": 20, "nombre_comercial": "Cocina Test", "razon_social": "Cocina Test S.A.S.",
    "tipo_persona": "Juridica", "nit": "900123456", "digito_verificacion": "8", "matricula_mercantil": "123",
    "tipo_establecimiento": "Cocina oculta", "categoria": "Casera", "telefono": "3205550199",
    "correo": "test@urbaneats.test", "direccion": "Cl. 53 # 27-20", "localidad": "Teusaquillo",
    "hora_apertura": "21:00", "hora_cierre": "05:00", "autoriza_datos": True,
}


def test_sin_clave_responde_401():
    respuesta = cliente.get("/api/solicitudes")
    assert respuesta.status_code == 401
    assert "X-API-KEY" in respuesta.json()["error"]


def test_dv_incorrecto_responde_422_en_espanol():
    respuesta = cliente.post("/api/solicitudes", json={**SOLICITUD, "digito_verificacion": "3"}, headers=CLAVE)
    assert respuesta.status_code == 422
    assert "dígito de verificación" in str(respuesta.json()["campos"])


def test_horario_solo_diurno_se_rechaza():
    respuesta = cliente.post("/api/solicitudes", json={**SOLICITUD, "hora_apertura": "08:00", "hora_cierre": "17:00"},
                             headers=CLAVE)
    assert respuesta.status_code == 422
    assert "nocturnas" in str(respuesta.json()["campos"])


def test_sin_autorizacion_de_datos_se_rechaza():
    respuesta = cliente.post("/api/solicitudes", json={**SOLICITUD, "autoriza_datos": False}, headers=CLAVE)
    assert respuesta.status_code == 422
    assert "1581" in str(respuesta.json()["campos"])


def test_juridica_sin_matricula_se_rechaza():
    respuesta = cliente.post("/api/solicitudes", json={**SOLICITUD, "matricula_mercantil": None}, headers=CLAVE)
    assert respuesta.status_code == 422


def test_rechazar_exige_motivo():
    respuesta = cliente.patch("/api/solicitudes/1/estado", json={"estado": "Rechazada", "codigo_revisor": 19},
                              headers=CLAVE)
    assert respuesta.status_code == 422
    assert "motivo" in str(respuesta.json()["campos"])


def test_formato_de_reporte_invalido():
    respuesta = cliente.get("/api/reportes/pedidos?formato=docx", headers=CLAVE)
    assert respuesta.status_code == 422
