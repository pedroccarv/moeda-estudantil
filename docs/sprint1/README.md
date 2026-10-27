# Lab03S01 — Modelagem do sistema

Entrega da Sprint 1 conforme o enunciado do Laboratório 3.

## Artefatos

1. **Diagrama de Casos de Uso** — [`diagrama-casos-de-uso.puml`](diagrama-casos-de-uso.puml)
2. **Histórias do Usuário** — [`historias-usuario.md`](historias-usuario.md)
3. **Diagrama de Classes** — [`diagrama-classes.puml`](diagrama-classes.puml)
4. **Diagrama de Componentes** — [`diagrama-componentes.puml`](diagrama-componentes.puml)

## Diagramas (PNG)

### Casos de Uso

![Diagrama de Casos de Uso](images/diagrama-casos-de-uso.png)

### Classes

![Diagrama de Classes](images/diagrama-classes.png)

### Componentes

![Diagrama de Componentes](images/diagrama-componentes.png)

## Como regenerar as imagens

Com Java e o JAR do PlantUML em `.tools/plantuml.jar`:

```powershell
java -jar .\.tools\plantuml.jar -tpng -charset UTF-8 -o "$PWD\docs\sprint1\images" docs\sprint1\*.puml
```

Depois renomeie os PNGs gerados para os nomes em `images/` (sem espaços), se necessário.

## Atores principais

| Ator | Responsabilidades |
|------|-------------------|
| Aluno | Cadastro, extrato, resgate de vantagens |
| Professor | Envio de moedas, extrato, crédito semestral |
| Empresa Parceira | Cadastro, cadastro de vantagens |
| Sistema de E-mail | Notificações de recebimento e cupom |

## Próxima sprint (Lab03S02)

Modelo ER, estratégia de acesso ao BD e CRUDs iniciais de aluno e empresa.
