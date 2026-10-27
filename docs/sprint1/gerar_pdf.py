"""Gera o PDF de entrega da Sprint 1 (Lab03S01)."""

from pathlib import Path

from fpdf import FPDF
from fpdf.enums import XPos, YPos

ROOT = Path(__file__).resolve().parent
IMAGES = ROOT / "images"
OUT = ROOT / "Lab03S01_Moeda_Estudantil.pdf"
FONT = Path(r"C:\Windows\Fonts\arial.ttf")
FONT_BOLD = Path(r"C:\Windows\Fonts\arialbd.ttf")


class PdfEntrega(FPDF):
    def header(self):
        if self.page_no() == 1:
            return
        self.set_x(self.l_margin)
        self.set_font("ArialDoc", "B", 9)
        self.set_text_color(80, 80, 80)
        self.cell(
            0,
            6,
            "Lab03S01 - Sistema de Moeda Estudantil - PUC Minas",
            new_x=XPos.LMARGIN,
            new_y=YPos.NEXT,
        )
        self.set_draw_color(180, 180, 180)
        self.line(self.l_margin, self.get_y(), self.w - self.r_margin, self.get_y())
        self.ln(4)

    def footer(self):
        self.set_y(-12)
        self.set_x(self.l_margin)
        self.set_font("ArialDoc", size=8)
        self.set_text_color(120, 120, 120)
        self.cell(0, 8, f"Página {self.page_no()}/{{nb}}", align="C")


def content_width(pdf: FPDF) -> float:
    return pdf.w - pdf.l_margin - pdf.r_margin


def add_heading(pdf: FPDF, text: str, size: int = 16):
    pdf.set_x(pdf.l_margin)
    pdf.set_font("ArialDoc", "B", size)
    pdf.set_text_color(20, 20, 20)
    pdf.multi_cell(content_width(pdf), 8, text, new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.ln(2)


def add_paragraph(pdf: FPDF, text: str, size: int = 11):
    pdf.set_x(pdf.l_margin)
    pdf.set_font("ArialDoc", size=size)
    pdf.set_text_color(40, 40, 40)
    pdf.multi_cell(content_width(pdf), 6, text, new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.ln(1)


def add_image_fit(pdf: FPDF, path: Path, max_h: float | None = None):
    pdf.set_x(pdf.l_margin)
    usable_w = content_width(pdf)
    usable_h = (pdf.h - pdf.get_y() - pdf.b_margin - 8) if max_h is None else max_h
    if usable_h < 40:
        pdf.add_page(orientation=pdf.cur_orientation)
        usable_h = pdf.h - pdf.get_y() - pdf.b_margin - 8 if max_h is None else max_h
    pdf.image(str(path), x=pdf.l_margin, w=usable_w, h=usable_h, keep_aspect_ratio=True)
    pdf.set_x(pdf.l_margin)
    pdf.ln(6)


def build() -> Path:
    pdf = PdfEntrega(orientation="P", unit="mm", format="A4")
    pdf.alias_nb_pages()
    pdf.set_auto_page_break(auto=True, margin=18)
    pdf.add_font("ArialDoc", fname=str(FONT))
    pdf.add_font("ArialDoc", style="B", fname=str(FONT_BOLD))

    # ---- Capa ----
    pdf.add_page()
    pdf.ln(35)
    pdf.set_font("ArialDoc", "B", 20)
    pdf.cell(
        0,
        10,
        "Laboratório 3 - Sprint 1 (Lab03S01)",
        align="C",
        new_x=XPos.LMARGIN,
        new_y=YPos.NEXT,
    )
    pdf.ln(3)
    pdf.set_font("ArialDoc", "B", 16)
    pdf.cell(
        0,
        9,
        "Sistema de Moeda Estudantil",
        align="C",
        new_x=XPos.LMARGIN,
        new_y=YPos.NEXT,
    )
    pdf.ln(8)
    pdf.set_font("ArialDoc", size=12)
    for line in [
        "Pontifícia Universidade Católica de Minas Gerais",
        "Curso de Engenharia de Software",
        "Disciplina: Projeto de Software",
        "Profa. Milena Menezes Adão",
        "Segundo Semestre / 2026",
    ]:
        pdf.cell(0, 7, line, align="C", new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.ln(12)
    pdf.set_font("ArialDoc", "B", 12)
    pdf.cell(0, 7, "Conteúdo desta entrega", align="C", new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.set_font("ArialDoc", size=11)
    for line in [
        "1. Diagrama de Casos de Uso",
        "2. Histórias do Usuário",
        "3. Diagrama de Classes",
        "4. Diagrama de Componentes",
    ]:
        pdf.cell(0, 7, line, align="C", new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.ln(18)
    pdf.set_font("ArialDoc", size=11)
    pdf.cell(
        0,
        7,
        "Autor: Pedro Henrique Carvalho Pereira",
        align="C",
        new_x=XPos.LMARGIN,
        new_y=YPos.NEXT,
    )

    # ---- 1. Casos de Uso ----
    pdf.add_page(orientation="L")
    add_heading(pdf, "1. Diagrama de Casos de Uso")
    add_paragraph(
        pdf,
        "Atores: Aluno, Professor, Empresa Parceira e Sistema de E-mail. "
        "Os casos de uso cobrem cadastro, autenticação, envio de moedas, "
        "consulta de extrato, cadastro/resgate de vantagens e notificações.",
    )
    add_image_fit(pdf, IMAGES / "diagrama-casos-de-uso.png")

    # ---- 2. Histórias (visao) ----
    pdf.add_page(orientation="L")
    add_heading(pdf, "2. Histórias do Usuário - Visão geral")
    add_paragraph(
        pdf,
        "Formato: Como [ator], quero [ação], para [benefício]. "
        "Organizadas em 4 épicos (HU01-HU10).",
    )
    add_image_fit(pdf, IMAGES / "historias-usuario.png")

    # ---- 2.1 Detalhamento ----
    pdf.add_page(orientation="P")
    add_heading(pdf, "2.1 Detalhamento das Histórias do Usuário")

    historias = [
        (
            "Épico: Autenticação e cadastro",
            [
                (
                    "HU01 - Cadastro de aluno",
                    "Como aluno, quero me cadastrar informando nome, e-mail, CPF, RG, "
                    "endereço, instituição de ensino e curso, para participar do sistema.",
                    [
                        "Instituições pré-cadastradas; aluno apenas seleciona.",
                        "CPF e e-mail únicos; login/senha após cadastro; saldo inicial zero.",
                    ],
                ),
                (
                    "HU02 - Cadastro de empresa parceira",
                    "Como empresa parceira, quero me cadastrar no sistema, "
                    "para oferecer vantagens em troca das moedas dos alunos.",
                    ["Cadastro inclui login e senha; após isso pode cadastrar vantagens."],
                ),
                (
                    "HU03 - Login no sistema",
                    "Como aluno, professor ou empresa, quero autenticar-me com login e senha, "
                    "para acessar as funcionalidades do meu perfil.",
                    [
                        "Credenciais inválidas impedem o acesso.",
                        "Apos login, apenas ações do tipo de usuário ficam disponíveis.",
                    ],
                ),
            ],
        ),
        (
            "Épico: Distribuição de moedas",
            [
                (
                    "HU04 - Crédito semestral",
                    "Como professor, quero receber 1.000 moedas a cada semestre, "
                    "para reconhecê-las aos alunos.",
                    [
                        "Crédito de 1.000 moedas por semestre, saldo acumulável.",
                        "Professores pré-cadastrados (nome, CPF, departamento, instituição).",
                    ],
                ),
                (
                    "HU05 - Enviar moedas a um aluno",
                    "Como professor, quero enviar moedas a um aluno com mensagem obrigatória "
                    "de motivo, para reconhecer mérito.",
                    [
                        "Exige saldo suficiente e motivo obrigatório.",
                        "Debita professor, credita aluno e registra no extrato.",
                    ],
                ),
                (
                    "HU06 - Notificação de recebimento",
                    "Como aluno, quero ser notificado por e-mail ao receber moedas, "
                    "para saber que fui reconhecido.",
                    ["E-mail após envio bem-sucedido com montante, professor e motivo."],
                ),
            ],
        ),
        (
            "Épico: Consulta de saldo e extrato",
            [
                (
                    "HU07 - Extrato do professor",
                    "Como professor, quero consultar saldo e envios realizados, "
                    "para controlar a distribuição no semestre.",
                    ["Exibe saldo e lista de envios (data, aluno, valor, motivo)."],
                ),
                (
                    "HU08 - Extrato do aluno",
                    "Como aluno, quero consultar saldo, recebimentos e resgates, "
                    "para saber quanto posso gastar.",
                    ["Exibe saldo e histórico de recebimentos/resgates."],
                ),
            ],
        ),
        (
            "Épico: Vantagens e resgate",
            [
                (
                    "HU09 - Cadastrar vantagem",
                    "Como empresa, quero cadastrar vantagens com descrição, foto e custo "
                    "em moedas, para atrair alunos.",
                    ["Campos obrigatórios: descrição, foto e custo; disponível para resgate."],
                ),
                (
                    "HU10 - Resgatar vantagem",
                    "Como aluno, quero resgatar uma vantagem com minhas moedas, "
                    "para obter descontos/produtos.",
                    [
                        "Exige saldo suficiente; debita valor; gera código de cupom.",
                        "E-mail com cupom para aluno e empresa (conferência).",
                    ],
                ),
            ],
        ),
    ]

    for epico, items in historias:
        if pdf.get_y() > pdf.h - 60:
            pdf.add_page(orientation="P")
        pdf.set_x(pdf.l_margin)
        pdf.set_font("ArialDoc", "B", 13)
        pdf.set_text_color(25, 25, 112)
        pdf.multi_cell(content_width(pdf), 7, epico, new_x=XPos.LMARGIN, new_y=YPos.NEXT)
        pdf.ln(1)
        for titulo, narrativa, criterios in items:
            if pdf.get_y() > pdf.h - 55:
                pdf.add_page(orientation="P")
            pdf.set_x(pdf.l_margin)
            pdf.set_font("ArialDoc", "B", 11)
            pdf.set_text_color(20, 20, 20)
            pdf.multi_cell(content_width(pdf), 6, titulo, new_x=XPos.LMARGIN, new_y=YPos.NEXT)
            add_paragraph(pdf, narrativa, size=10)
            pdf.set_x(pdf.l_margin)
            pdf.set_font("ArialDoc", "B", 10)
            pdf.cell(0, 5, "Critérios de aceite:", new_x=XPos.LMARGIN, new_y=YPos.NEXT)
            pdf.set_font("ArialDoc", size=10)
            for c in criterios:
                pdf.set_x(pdf.l_margin)
                pdf.multi_cell(
                    content_width(pdf),
                    5,
                    f"- {c}",
                    new_x=XPos.LMARGIN,
                    new_y=YPos.NEXT,
                )
            pdf.ln(3)

    pdf.add_page(orientation="P")
    add_heading(pdf, "2.2 Rastreabilidade HU x Casos de Uso")
    rows = [
        ("HU01", "UC02"),
        ("HU02", "UC03"),
        ("HU03", "UC01"),
        ("HU04", "UC10"),
        ("HU05", "UC05"),
        ("HU06", "UC08"),
        ("HU07", "UC06"),
        ("HU08", "UC06"),
        ("HU09", "UC04"),
        ("HU10", "UC07, UC09"),
    ]
    col_w = content_width(pdf) / 2
    pdf.set_font("ArialDoc", "B", 11)
    pdf.set_x(pdf.l_margin)
    pdf.cell(col_w, 8, "História", border=1, align="C")
    pdf.cell(col_w, 8, "Casos de Uso", border=1, align="C", new_x=XPos.LMARGIN, new_y=YPos.NEXT)
    pdf.set_font("ArialDoc", size=11)
    for hu, uc in rows:
        pdf.set_x(pdf.l_margin)
        pdf.cell(col_w, 7, hu, border=1, align="C")
        pdf.cell(col_w, 7, uc, border=1, align="C", new_x=XPos.LMARGIN, new_y=YPos.NEXT)

    # ---- 3. Classes ----
    pdf.add_page(orientation="L")
    add_heading(pdf, "3. Diagrama de Classes")
    add_paragraph(
        pdf,
        "Modelo estrutural com Usuario (Aluno, Professor, EmpresaParceira), "
        "InstituiçãoEnsino, Vantagem, Transação (Envio/Resgate) e NotificaçãoEmail.",
    )
    add_image_fit(pdf, IMAGES / "diagrama-classes.png")

    # ---- 4. Componentes ----
    pdf.add_page(orientation="P")
    add_heading(pdf, "4. Diagrama de Componentes")
    add_paragraph(
        pdf,
        "Arquitetura MVC em camadas: View -> Controller -> Services (Model) -> "
        "Repositories -> Banco de Dados, com SMTP para notificações.",
    )
    add_image_fit(pdf, IMAGES / "diagrama-componentes.png", max_h=170)

    pdf.output(str(OUT))
    return OUT


if __name__ == "__main__":
    path = build()
    print(f"PDF gerado: {path}")
