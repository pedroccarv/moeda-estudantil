# Sistema de Moeda Estudantil

Projeto da disciplina **Projeto de Software** (PUC Minas) — Laboratório 3.

Stack: **Java 21 + Spring Boot + Spring Data JPA + Thymeleaf + PostgreSQL** (arquitetura MVC).

## Como executar

1. Suba o PostgreSQL e o servidor SMTP local de demonstração (Mailpit):

```bash
docker compose up -d
```

2. Rode a aplicação:

```bash
mvn spring-boot:run
```

3. Acesse http://localhost:8080

Credenciais do banco (padrão):

| Item | Valor |
|------|-------|
| URL | `jdbc:postgresql://localhost:5432/moeda_estudantil` |
| Usuário | `moeda` |
| Senha | `moeda123` |

### Professores pré-cadastrados

A instituição envia a lista. Neste protótipo ela entra na subida da aplicação:

| Nome | E-mail | Senha | Instituição |
|------|--------|-------|-------------|
| Ana Souza | ana.souza@pucminas.br | prof123 | PUC Minas |
| Carlos Lima | carlos.lima@ufmg.br | prof123 | UFMG |

Alunos e empresas se cadastram pela tela inicial. Na inicialização e diariamente à meia-noite (America/Sao_Paulo), todos os professores recebem 1.000 moedas por semestre pendente. O acesso ao painel também reconcilia o saldo. O cálculo recupera períodos sem login e preserva o saldo anterior, sem duplicar créditos concorrentes.

### E-mail

As notificações são gravadas junto com a operação de moedas. O envio SMTP começa **após o commit**; uma operação desfeita não envia cupom. A tela distingue mensagem pendente, falha e envio aceito pelo servidor SMTP.

Com `docker compose up -d`, o SMTP local já fica disponível em `localhost:1025`. Abra [a caixa de demonstração](http://localhost:8025) para conferir as mensagens recebidas pelo Mailpit. Ele captura os e-mails localmente; **não entrega nas caixas pessoais dos destinatários**.

Para entrega externa, configure um servidor e um remetente autorizados conforme [Configuração de e-mail](docs/sprint3/email.md). Há até 5 tentativas, com varredura a cada minuto. Falha de SMTP preserva saldo, transação e o mesmo cupom para nova tentativa.

### Consistência e atualização de bases existentes

- Resgates, envios, créditos semestrais e edição do cadastro do aluno usam bloqueios de escrita no banco. Operações simultâneas não podem gastar ou sobrescrever o mesmo saldo.
- Novos professores armazenam `dataCadastro`; o primeiro crédito inclui todos os semestres desde essa data. Cadastros antigos usam `ultimoPeriodoCreditado`; se ambos os campos estiverem ausentes, somente o período atual é creditado, pois o sistema não possui evidência da data de ingresso. Para recuperar períodos anteriores desses cadastros, informe a data real de cadastro antes da primeira execução da versão atualizada.
- `ddl-auto: update` acrescenta os novos campos. Notificações preexistentes recebem status `LEGADO`, sem reenvio automático: seu histórico não distingue envio SMTP de mero registro. `dataEnvio` mantém a data de registro; `enviadoEm` é a data do aceite pelo servidor SMTP.
- Como o SMTP não participa da transação do banco, uma interrupção depois do aceite pelo servidor e antes de gravar o status pode causar reenvio. O código do cupom é preservado.

## Entregas

### Sprint 1 (Lab03S01) — Modelagem

Ver [docs/sprint1/](docs/sprint1/) · PDF: [Lab03S01_Moeda_Estudantil.pdf](docs/sprint1/Lab03S01_Moeda_Estudantil.pdf)

### Sprint 2 (Lab03S02) — Persistência e CRUDs iniciais

- Modelo ER: [docs/sprint2/modelo-er.puml](docs/sprint2/modelo-er.puml) · [PNG](docs/sprint2/images/modelo-er.png)
- ORM: Spring Data JPA / Hibernate

### Sprint 3 (Lab03S03) — Versão final

- Login por perfil e cadastro do próprio aluno ou da própria empresa
- Crédito semestral, envio de moedas, extratos, vantagens e resgate com cupom
- Slides: [docs/sprint3/slides.pdf](docs/sprint3/slides.pdf)

## Rotas

| Quem | Onde |
|------|------|
| Visitante | `/`, `/login`, `/alunos/novo`, `/empresas/nova` |
| Aluno | `/aluno`, `/aluno/extrato`, `/aluno/vantagens` |
| Professor | `/professor`, `/professor/enviar`, `/professor/extrato` |
| Empresa | `/empresa`, `/empresa/vantagens/nova` |

## Estrutura

```
src/main/java/br/pucminas/moeda/
  domain/        # Entidades JPA
  repository/    # Spring Data JPA
  service/       # Regras de negócio
  web/           # Controllers MVC
  config/        # Seed e recursos estáticos de upload
src/main/resources/templates/
docs/sprint1/    # UML da Sprint 1
docs/sprint2/    # ER
docs/sprint3/    # Slides e documentação
```

Fotos das vantagens ficam em `data/uploads/` (não versionado).

## Testes

```bash
mvn test
```

Os testes usam H2 em memória e um servidor SMTP GreenMail isolado, sem enviar e-mails externos. Cobrem concorrência de saldos, semestres perdidos e crédito único, edição concorrente, entrega SMTP, rollback e novas tentativas. A aplicação em execução usa PostgreSQL.

Para repetir a suíte em um **banco PostgreSQL descartável**, configure `TEST_DATABASE_URL`, `TEST_DATABASE_DRIVER=org.postgresql.Driver`, `TEST_DATABASE_USERNAME` e `TEST_DATABASE_PASSWORD`. Os testes usam `create-drop`: nunca aponte essas variáveis para uma base com dados que deseja manter.

## Autor

Pedro Henrique Carvalho Pereira
