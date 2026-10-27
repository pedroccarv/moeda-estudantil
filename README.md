# Sistema de Moeda Estudantil

Projeto da disciplina **Projeto de Software** (PUC Minas) — Laboratório 3.

Stack: **Java 21 + Spring Boot + Spring Data JPA + Thymeleaf + PostgreSQL** (arquitetura MVC).

## Como executar (Sprint 2)

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

## Entregas

### Sprint 1 (Lab03S01) — Modelagem

Ver [docs/sprint1/](docs/sprint1/) · PDF: [Lab03S01_Moeda_Estudantil.pdf](docs/sprint1/Lab03S01_Moeda_Estudantil.pdf)

### Sprint 2 (Lab03S02) — Persistência e CRUDs

- Modelo ER: [docs/sprint2/modelo-er.puml](docs/sprint2/modelo-er.puml) · [PNG](docs/sprint2/images/modelo-er.png)
- ORM: Spring Data JPA / Hibernate
- CRUDs iniciais:
  - Alunos: `/alunos`
  - Empresas: `/empresas`

Detalhes: [docs/sprint2/README.md](docs/sprint2/README.md)

## Estrutura

```
src/main/java/br/pucminas/moeda/
  domain/        # Entidades JPA
  repository/    # Spring Data JPA (DAO)
  service/       # Regras de negócio
  web/           # Controllers MVC
  config/        # Seed de instituições
src/main/resources/templates/  # Front Thymeleaf
docs/sprint1/    # UML Sprint 1
docs/sprint2/    # ER + notas Sprint 2
```

## Autor

Pedro Henrique Carvalho Pereira
