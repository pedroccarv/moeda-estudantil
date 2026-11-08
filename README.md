# Sistema de Moeda Estudantil

Projeto da disciplina **Projeto de Software** (PUC Minas) — Laboratório 3.

Stack: **Java 21 + Spring Boot + Spring Data JPA + Thymeleaf + PostgreSQL** (arquitetura MVC).

## Como executar

1. Suba o PostgreSQL:

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

Alunos e empresas se cadastram pela tela inicial. No primeiro acesso do semestre, o professor recebe 1.000 moedas somadas ao saldo que já tinha.

### E-mail

Cada aviso (recebimento de moedas e cupom de resgate) fica gravado e aparece no extrato de quem o recebeu. Para também disparar SMTP, defina `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME` e `SPRING_MAIL_PASSWORD` antes de subir a aplicação. Se o envio SMTP falhar, a operação de moedas não é desfeita: o texto continua registrado no sistema.

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
- Roteiro do tutorial (20 min): [docs/sprint3/tutorial.md](docs/sprint3/tutorial.md)

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
docs/sprint3/    # Slides e tutorial
```

Fotos das vantagens ficam em `data/uploads/` (não versionado).

## Testes

```bash
mvn test
```

Os testes usam H2 em memória. A aplicação em execução usa PostgreSQL.

## Autor

Pedro Henrique Carvalho Pereira
