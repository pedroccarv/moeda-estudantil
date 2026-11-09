"""Gera o PDF dos modelos atuais. Dependências: reportlab e Pillow."""
from pathlib import Path
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import A4, A3, landscape
from reportlab.lib.units import mm
from reportlab.lib.utils import ImageReader

ROOT = Path(__file__).resolve().parent
IMAGES = ROOT / "images"
OUT = ROOT / "Lab03S01_Moeda_Estudantil.pdf"


def build() -> Path:
    pdf = canvas.Canvas(str(OUT))
    pdf.setTitle("Moeda Estudantil - Modelagem atualizada")
    diagrams = [
        ("1. Diagrama de Casos de Uso", "diagrama-casos-de-uso.png", A4),
        ("2. Histórias do Usuário", "historias-usuario.png", A4),
        ("3. Diagrama de Classes", "diagrama-classes.png", A3),
        ("4. Diagrama de Componentes", "diagrama-componentes.png", A3),
    ]
    for title, filename, size in diagrams:
        width, height = landscape(size)
        pdf.setPageSize((width, height))
        margin = 12 * mm
        pdf.setFont("Helvetica-Bold", 14)
        pdf.drawString(margin, height - margin - 14, title)
        image = ImageReader(str(IMAGES / filename))
        image_w, image_h = image.getSize()
        available_h = height - 2 * margin - 30
        ratio = min((width - 2 * margin) / image_w, available_h / image_h)
        draw_w, draw_h = image_w * ratio, image_h * ratio
        pdf.drawImage(image, (width - draw_w) / 2, margin + (available_h - draw_h) / 2,
                      width=draw_w, height=draw_h)
        pdf.showPage()
    pdf.save()
    return OUT


if __name__ == "__main__":
    print(f"PDF gerado: {build()}")
