# Configuração e demonstração de e-mail

## SMTP local para a apresentação

1. Execute `docker compose up -d` com o Docker iniciado.
2. Execute `mvn spring-boot:run`.
3. Envie moedas e resgate uma vantagem pelo sistema.
4. Abra http://localhost:8025. Confira a mensagem de recebimento e os dois e-mails de cupom, com o mesmo código e os destinatários corretos.

O [Mailpit](https://mailpit.axllent.org/docs/install/docker/) recebe as mensagens por SMTP em `localhost:1025` e permite inspecioná-las no navegador. Não encaminha mensagens para caixas pessoais. As portas de demonstração estão expostas somente no computador local.

## Entrega em caixas externas

Use um provedor SMTP e um remetente autorizado. Defina estas variáveis no mesmo terminal que iniciará a aplicação (exemplo para porta 587 com STARTTLS):

```powershell
$env:SPRING_MAIL_HOST = 'smtp.seu-provedor.com'
$env:SPRING_MAIL_PORT = '587'
$env:SPRING_MAIL_USERNAME = 'seu-usuario'
$env:APP_MAIL_FROM = 'remetente@seu-dominio.com'
$env:SMTP_AUTH = 'true'
$env:SMTP_STARTTLS = 'true'
$env:SMTP_SSL = 'false'
$credencialSmtp = Get-Credential -UserName $env:SPRING_MAIL_USERNAME -Message 'Senha SMTP ou senha de aplicativo'
$env:SPRING_MAIL_PASSWORD = $credencialSmtp.GetNetworkCredential().Password
mvn spring-boot:run
```

Use os dados e o mecanismo de autenticação fornecidos pelo provedor. Para SMTPS na porta 465, use `SMTP_SSL=true` e `SMTP_STARTTLS=false`. Não grave senhas em arquivos versionados. Um arquivo `.env` não é carregado automaticamente pelo Spring Boot neste projeto.

O teste final externo consiste em enviar moedas a um aluno com endereço válido e resgatar uma vantagem de uma empresa com endereço válido; conferir recebimento, conteúdo e igualdade dos códigos nas duas caixas. O status `ENVIADO` significa aceite pelo servidor SMTP, não comprovação de leitura ou de entrega na caixa de entrada.

## Fila e recuperação de falhas

- O registro da mensagem participa da mesma transação das moedas; um rollback cancela ambos.
- A primeira tentativa SMTP acontece após o commit. A indisponibilidade do servidor não desfaz moedas ou resgates confirmados.
- O sistema consulta até 50 mensagens pendentes por minuto, com até 5 tentativas por mensagem. O texto e o cupom original são preservados.
- O extrato mostra o status. `FALHA` indica envio não concluído, inclusive quando as tentativas se esgotaram.
- Depois de corrigir a configuração, mensagens abaixo do limite são repetidas automaticamente. Para permitir novas tentativas das mensagens esgotadas, reinicie a aplicação com `APP_MAIL_MAX_TENTATIVAS` maior que o número já registrado (por exemplo, `10`).
- As mensagens anteriores à atualização recebem `LEGADO`: o sistema antigo não registrava o resultado SMTP. Elas não são reenviadas automaticamente.
- Uma queda do processo entre o aceite SMTP e a gravação de `ENVIADO` pode provocar envio repetido. A confirmação externa não é atômica com o banco; o mesmo cupom continua sendo usado.

## Teste automatizado

`mvn test` inicia um servidor [GreenMail](https://greenmail-mail-test.github.io/greenmail/) local e isolado. A suíte verifica entrega SMTP, destinatários, conteúdo com acentos, código igual nos cupons, rollback sem envio, recuperação de falhas e limite de tentativas. Não usa credenciais nem envia mensagens externas.
