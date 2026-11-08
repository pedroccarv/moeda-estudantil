# Tutorial de 20 minutos — Moeda Estudantil

Salão de tecnologias da Sprint 3. Stack do protótipo: Java 21, Spring Boot 3, Spring MVC, Thymeleaf, Spring Data JPA, Hibernate, PostgreSQL e sessão HTTP. E-mail via JavaMail quando houver SMTP.

Abra os slides em `docs/sprint3/slides.html` e deixe o sistema em http://localhost:8080. Suba antes com `docker compose up -d` e `mvn spring-boot:run`.

## 0–2 min — O problema

Professores recebem moedas para reconhecer alunos. O aluno troca essas moedas por vantagens de empresas parceiras. Todo mundo entra com login. Instituições e professores já existem; aluno e empresa se cadastram.

## 2–6 min — Spring MVC

Mostre um pedido de ponta a ponta, o envio de moedas.

1. O navegador envia `POST /professor/enviar` com aluno, valor e motivo.
2. `AutenticacaoInterceptor` barra quem não tem sessão.
3. `ProfessorController` só continua se o papel da sessão for professor.
4. `ProfessorService` chama `Professor.enviarMoedas`, grava a transação e pede o e-mail.
5. A resposta é um redirect para o extrato, renderizado por Thymeleaf.

Camadas, de cima para baixo: template, controller, service, repository, banco. É o mesmo desenho do diagrama de componentes da Sprint 1.

## 6–10 min — Thymeleaf

Abra `templates/professor/enviar.html` e `templates/fragments.html`.

- O formulário é HTML comum com `th:action`, `th:each` e `th:text`.
- A barra do topo muda conforme `sessao.papel`.
- Não há uma API JSON: o back-end devolve a página pronta.

Troque de aluno para professor na demonstração e mostre que o menu muda.

## 10–15 min — JPA e PostgreSQL

Abra `domain/Usuario.java` e o modelo ER.

- Herança `JOINED`: tabela `usuario` mais `aluno`, `professor` e `empresa_parceira`.
- O mesmo vale para `transacao`, `transacao_envio` e `transacao_resgate`.
- Cada interface em `repository/` estende `JpaRepository`. O Hibernate cria e atualiza as tabelas (`ddl-auto: update`).
- `docker-compose.yml` sobe o Postgres. A aplicação fala com ele por `application.yml`.

No quadro, o crédito do semestre: se `ultimoPeriodoCreditado` ainda não é o período atual (`2026-2`, por exemplo), soma 1.000 ao saldo. O que sobrou não é zerado.

## 15–18 min — Regras e sessão

Login em `AuthService`: busca o e-mail em `Usuario`, chama `autenticar` e guarda id, nome, e-mail e papel na sessão HTTP.

Regras visíveis no domínio, sem framework:

- `enviarMoedas` exige motivo e saldo.
- `debitar` recusa resgate acima do saldo.
- `cadastrarVantagem` exige descrição, foto e custo positivo.
- O resgate gera um código de 8 caracteres e grava `TransacaoResgate`.

Mostre ao vivo: entrar como Ana (`ana.souza@pucminas.br` / `prof123`), enviar moedas com motivo, entrar como o aluno e abrir o extrato.

## 18–20 min — E-mail, foto e como rodar

`EmailService` grava `NotificacaoEmail` e, se existir `JavaMailSender`, tenta o SMTP. Sem host configurado, o texto aparece no extrato — é o que a banca vê sem um servidor de e-mail. Com as variáveis `SPRING_MAIL_*`, o mesmo texto sai de verdade. Falha de SMTP não desfaz a moeda.

A foto da vantagem vai para `data/uploads/` e é servida em `/uploads/**`.

Feche com os três comandos:

```bash
docker compose up -d
mvn spring-boot:run
mvn test
```

Os testes (`RegrasMoedaTest` e `FluxoSistemaTest`) cobrem saldo, motivo, crédito único no semestre, cupom igual nos dois e-mails e resgate sem saldo. Eles usam H2; o sistema que sobe no laboratório usa PostgreSQL.
