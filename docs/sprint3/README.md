# Lab03S03 — versão final

## O que entrou

- Login com e-mail e senha para aluno, professor e empresa. As telas de cada perfil exigem a sessão correspondente.
- Cadastro público de aluno (nome, e-mail, senha, CPF, RG, endereço, curso e instituição) e de empresa. Cada um edita ou exclui só o próprio cadastro.
- Professores pré-cadastrados na subida da aplicação.
- Crédito de 1.000 moedas por semestre, somado ao saldo existente, uma vez por período (`AAAA-1` ou `AAAA-2`, fuso America/Sao_Paulo), recuperando todos os períodos pendentes desde o último crédito ou a data de cadastro. Executado na inicialização, diariamente e como conferência ao acessar o painel.
- Envio de moedas com aluno, valor e motivo obrigatório. Sem saldo, a operação é recusada.
- E-mail ao aluno no recebimento. No resgate, o mesmo código de cupom vai para o aluno e para a empresa. O envio ocorre após o commit, com status e até 5 tentativas. O Docker inclui Mailpit para demonstração SMTP local; para caixas externas, veja [email.md](email.md).
- Vantagens da empresa: descrição, foto e custo em moedas.
- Extrato do professor (saldo e envios) e do aluno (saldo, recebimentos e resgates).

## Apresentação

- Apresentação resumida com 8 slides: apresentação do grupo, funcionalidades, tecnologias, arquitetura MVC, envio de moedas, e-mails, modelagem e resultados. Inclui os diagramas originais de [componentes](../sprint1/images/diagrama-componentes.png) e de [classes](../sprint1/images/diagrama-classes.png) da documentação do projeto. A apresentação pode ser feita sem executar o sistema. [Slides em PDF](slides.pdf).

## Modelos

Os diagramas de classes, componentes e ER, suas imagens e o PDF da modelagem foram atualizados para refletir a implementação final: campos, assinaturas, herança opcional por subtipo, crédito acumulado e fila de e-mails. As versões anteriores estão preservadas no histórico do Git.

## Verificação de consistência

Bloqueios de escrita protegem resgates, envios, créditos semestrais e a edição de cadastro contra perda de saldo em operações simultâneas. A suíte de testes verifica esses cenários, além do envio SMTP real para um servidor de teste local.
