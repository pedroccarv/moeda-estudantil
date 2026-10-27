# Histórias do Usuário — Sistema de Moeda Estudantil

Formato: **Como** [ator], **quero** [ação], **para** [benefício].

Critérios de aceite no estilo Given/When/Then (resumidos).

---

## Épico: Autenticação e cadastro

### HU01 — Cadastro de aluno

**Como** aluno,  
**quero** me cadastrar informando nome, e-mail, CPF, RG, endereço, instituição de ensino e curso,  
**para** participar do sistema de mérito estudantil.

**Critérios de aceite**

- Instituições participantes já estão pré-cadastradas; o aluno apenas seleciona uma.
- CPF e e-mail devem ser únicos.
- Após o cadastro, o aluno possui login e senha para autenticação.
- O saldo inicial de moedas do aluno é zero.

---

### HU02 — Cadastro de empresa parceira

**Como** empresa parceira,  
**quero** me cadastrar no sistema,  
**para** oferecer vantagens em troca das moedas dos alunos.

**Critérios de aceite**

- O cadastro inclui dados de acesso (login e senha).
- Após o cadastro, a empresa pode cadastrar vantagens.

---

### HU03 — Login no sistema

**Como** aluno, professor ou empresa parceira,  
**quero** autenticar-me com login e senha,  
**para** acessar as funcionalidades do meu perfil.

**Critérios de aceite**

- Credenciais inválidas impedem o acesso.
- Após login bem-sucedido, o usuário vê apenas as ações do seu tipo (aluno / professor / empresa).

---

## Épico: Distribuição de moedas

### HU04 — Crédito semestral de moedas ao professor

**Como** professor,  
**quero** receber 1.000 moedas a cada semestre,  
**para** reconhecê-las aos alunos.

**Critérios de aceite**

- A cada semestre são creditadas 1.000 moedas.
- O saldo é acumulável: moedas não usadas no semestre anterior permanecem e somam-se às novas.
- Professores já estão pré-cadastrados (nome, CPF, departamento e instituição).

---

### HU05 — Enviar moedas a um aluno

**Como** professor,  
**quero** enviar um montante de moedas a um aluno com uma mensagem obrigatória de motivo,  
**para** reconhecer bom comportamento, participação em aula, etc.

**Critérios de aceite**

- O professor deve ter saldo suficiente; caso contrário, a operação é rejeitada.
- A mensagem de reconhecimento é obrigatória.
- O saldo do professor é debitado e o do aluno é creditado.
- Uma transação de envio fica registrada no extrato de ambos.

---

### HU06 — Notificação de recebimento de moedas

**Como** aluno,  
**quero** ser notificado por e-mail ao receber moedas,  
**para** saber imediatamente que fui reconhecido.

**Critérios de aceite**

- O e-mail é enviado após o envio bem-sucedido de moedas.
- O conteúdo indica o montante, o professor e o motivo.

---

## Épico: Consulta de saldo e extrato

### HU07 — Consultar extrato (professor)

**Como** professor,  
**quero** consultar o total de moedas que ainda possuo e as transações de envio que realizei,  
**para** controlar a distribuição no semestre.

**Critérios de aceite**

- Exibe saldo atual.
- Lista envios com data, aluno, valor e motivo.

---

### HU08 — Consultar extrato (aluno)

**Como** aluno,  
**quero** consultar o total de moedas e as transações de recebimento ou troca,  
**para** saber quanto posso gastar e o histórico da minha conta.

**Critérios de aceite**

- Exibe saldo atual.
- Lista recebimentos e resgates com data, valor e descrição.

---

## Épico: Vantagens e resgate

### HU09 — Cadastrar vantagem

**Como** empresa parceira,  
**quero** cadastrar vantagens com descrição, foto e custo em moedas,  
**para** atrair alunos ao meu estabelecimento/produto.

**Critérios de aceite**

- Campos obrigatórios: descrição, foto (imagem do produto) e custo em moedas.
- A vantagem fica disponível para resgate pelos alunos.

---

### HU10 — Resgatar vantagem

**Como** aluno,  
**quero** selecionar uma vantagem cadastrada e resgatá-la com minhas moedas,  
**para** obter descontos ou produtos de empresas parceiras.

**Critérios de aceite**

- O aluno deve ter saldo suficiente; caso contrário, o resgate é rejeitado.
- O valor da vantagem é descontado do saldo do aluno.
- O sistema gera um código de cupom.
- Um e-mail com o cupom é enviado ao aluno.
- Um e-mail com o mesmo código é enviado à empresa parceira para conferência presencial.

---

## Rastreabilidade (HU ↔ Casos de Uso)

| História | Casos de Uso |
|----------|--------------|
| HU01 | UC02 |
| HU02 | UC03 |
| HU03 | UC01 |
| HU04 | UC10 |
| HU05 | UC05 |
| HU06 | UC08 |
| HU07 | UC06 |
| HU08 | UC06 |
| HU09 | UC04 |
| HU10 | UC07, UC09 |
