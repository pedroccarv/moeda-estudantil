# Lab03S03 — versão final

## O que entrou

- Login com e-mail e senha para aluno, professor e empresa. As telas de cada perfil exigem a sessão correspondente.
- Cadastro público de aluno (nome, e-mail, senha, CPF, RG, endereço, curso e instituição) e de empresa. Cada um edita ou exclui só o próprio cadastro.
- Professores pré-cadastrados na subida da aplicação.
- Crédito de 1.000 moedas por semestre, somado ao saldo existente, uma vez por período (`AAAA-1` ou `AAAA-2`, fuso America/Sao_Paulo).
- Envio de moedas com aluno, valor e motivo obrigatório. Sem saldo, a operação é recusada.
- E-mail ao aluno no recebimento. No resgate, o mesmo código de cupom vai para o aluno e para a empresa. Sem SMTP, o texto fica no extrato; com `SPRING_MAIL_HOST`, o JavaMail também envia.
- Vantagens da empresa: descrição, foto e custo em moedas.
- Extrato do professor (saldo e envios) e do aluno (saldo, recebimentos e resgates).

## Apresentação

- Slides (abrir no navegador): [slides.html](slides.html)
- Roteiro de 20 minutos: [tutorial.md](tutorial.md)

## Modelos

O diagrama de classes e o modelo ER foram atualizados com `ultimoPeriodoCreditado` e `notificacao_email`. A versão entregue na Sprint 1 continua no histórico do Git e no PDF `docs/sprint1/Lab03S01_Moeda_Estudantil.pdf`.
