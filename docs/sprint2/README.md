# Lab03S02 — Persistência e CRUDs iniciais

## Entrega

- Modelo ER: [`modelo-er.puml`](modelo-er.puml) · [`images/modelo-er.png`](images/modelo-er.png)
- Estratégia de acesso: **Spring Data JPA** (ORM Hibernate) — repositórios no pacote `repository`
- CRUDs iniciais (front Thymeleaf + back Spring MVC):
  - Aluno (`/alunos`)
  - Empresa Parceira (`/empresas`)

## Como rodar

1. Suba o PostgreSQL (Docker):

```bash
docker compose up -d
```

Credenciais padrão (ver `application.yml` e `docker-compose.yml`):

- DB: `moeda_estudantil`
- User: `moeda`
- Password: `moeda123`
- Porta: `5432`

2. Execute a aplicação:

```bash
mvn spring-boot:run
```

3. Abra http://localhost:8080

Instituições pré-cadastradas no startup: PUC Minas, UFMG, CEFET-MG, UNA.
