"""Genera los PDF de prueba de las cocinas de demostracion.

Son documentos FICTICIOS con la marca de agua "DOCUMENTO DE PRUEBA - SIN VALIDEZ LEGAL".
No copian logos ni formatos oficiales de la DIAN, la Camara de Comercio ni la
Secretaria de Salud: solo sirven para mostrar la carga y revision de archivos.
"""
from datetime import date
from pathlib import Path

from fpdf import FPDF

CARPETA = Path(__file__).resolve().parents[2] / "demo" / "documentos"


def generar(nombre_archivo: str, titulo: str, lineas: list[str]) -> bytes:
    pdf = FPDF()
    pdf.add_page()
    # Marca de agua diagonal
    pdf.set_font("Helvetica", "B", 30)
    pdf.set_text_color(235, 200, 200)
    with pdf.rotation(35, x=105, y=150):
        pdf.text(20, 160, "DOCUMENTO DE PRUEBA")
        pdf.text(30, 178, "SIN VALIDEZ LEGAL")
    # Contenido
    pdf.set_text_color(17, 20, 23)
    pdf.set_font("Helvetica", "B", 16)
    pdf.cell(0, 12, titulo, new_x="LMARGIN", new_y="NEXT")
    pdf.set_font("Helvetica", "", 11)
    pdf.cell(0, 8, "Urban Eats - Proyecto formativo SENA (datos ficticios)", new_x="LMARGIN", new_y="NEXT")
    pdf.ln(4)
    for linea in lineas:
        pdf.multi_cell(0, 7, linea, new_x="LMARGIN", new_y="NEXT")
    pdf.ln(6)
    pdf.set_font("Helvetica", "I", 9)
    pdf.multi_cell(0, 5, "Este archivo fue generado automaticamente para la demostracion del sistema. "
                         "No corresponde a ningun tramite real ante ninguna entidad.")
    contenido = bytes(pdf.output())
    CARPETA.mkdir(parents=True, exist_ok=True)
    (CARPETA / nombre_archivo).write_bytes(contenido)
    return contenido


def juego_completo(nombre: str, nit: str, dv: str) -> dict[str, bytes]:
    """Un PDF por cada documento exigido."""
    hoy = date.today().strftime("%d/%m/%Y")
    base = nombre.lower().replace(" ", "-")
    return {
        "RUT": generar(f"{base}-rut.pdf", "Registro tributario (prueba)",
                       [f"Razon social: {nombre} S.A.S.", f"NIT: {nit}-{dv}", "Actividad: 5611 Expendio a la mesa"]),
        "Camara de Comercio": generar(f"{base}-camara.pdf", "Certificado de existencia (prueba)",
                                      [f"Razon social: {nombre} S.A.S.", f"Fecha de expedicion: {hoy}"]),
        "Concepto sanitario": generar(f"{base}-concepto.pdf", "Concepto sanitario (prueba)",
                                      [f"Establecimiento: {nombre}", "Resultado: FAVORABLE", f"Fecha de visita: {hoy}"]),
        "Certificado manipulacion": generar(f"{base}-manipulacion.pdf", "Curso de manipulacion de alimentos (prueba)",
                                            ["Manipulador: Ana Ruiz Pineda", "Intensidad: 10 horas (BPM)"]),
        "Certificado medico": generar(f"{base}-medico.pdf", "Certificado medico de aptitud (prueba)",
                                      ["Manipulador: Ana Ruiz Pineda", "Concepto: apto para manipular alimentos"]),
    }
