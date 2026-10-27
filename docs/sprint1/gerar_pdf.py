"""Gera o PDF de entrega da Sprint 1 (Lab03S01) — apenas os diagramas."""

from pathlib import Path

from fpdf import FPDF
from fpdf.enums import XPos, YPos

ROOT = Path(__file__).resolve().parent
IMAGES = ROOT / "images"
OUT = ROOT / "Lab03S01_Moeda_Estudantil.pdf"
FONT = Path(r"C:\Windows\Fonts\arial.ttf")
FONT_BOLD = Path(r"C:\Windows\Fonts\arialbd.ttf")


def content_width(pdf: FPDF) -> float:
    return pdf.w - pdf.l_margin - pdf.r_margin


def add_title(pdf: FPDF, text: str):
    pdf.set_x(pdf.l_margin)
    pdf.set_font("ArialDoc", "B", 14)
    pdf.set_text_color(20, 20, 20)
    pdf.multi_cell(content_width(pdf), 8, text, new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.ln(2)


def add_diagram(pdf: FPDF, title: str, image: Path, orientation: str = "L"):
    pdf.add_page(orientation=orientation)
    add_title(pdf, title)
    usable_w = content_width(pdf)
    usable_h = pdf.h - pdf.get_y() - pdf.b_margin - 4
    pdf.image(str(image), x=pdf.l_margin, w=usable_w, h=usable_h, keep_aspect_ratio=True)


def build() -> Path:
    pdf = FPDF(orientation="L", unit="mm", format="A4")
    pdf.set_auto_page_break(auto=False)
    pdf.set_margins(12, 12, 12)
    pdf.add_font("ArialDoc", fname=str(FONT))
    pdf.add_font("ArialDoc", style="B", fname=str(FONT_BOLD))

    add_diagram(pdf, "1. Diagrama de Casos de Uso", IMAGES / "diagrama-casos-de-uso.png", "L")
    add_diagram(pdf, "2. Histórias do Usuário", IMAGES / "historias-usuario.png", "L")
    add_diagram(pdf, "3. Diagrama de Classes", IMAGES / "diagrama-classes.png", "L")
    add_diagram(pdf, "4. Diagrama de Componentes", IMAGES / "diagrama-componentes.png", "P")

    pdf.output(str(OUT))
    return OUT


if __name__ == "__main__":
    path = build()
    print(f"PDF gerado: {path}")
