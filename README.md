# E-commerce legado — atividade de DDD

Aplicação monolítica propositalmente acoplada para uma atividade de refatoração.

## Tecnologias

- Java 25
- Spring Boot 4.1.0
- Maven
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 em memória

## Requisitos

- JDK 25
- Maven 3.6.3 ou superior

## Executar

```bash
mvn spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

## Console H2

```text
http://localhost:8080/h2-console
```

Dados da conexão:

```text
JDBC URL: jdbc:h2:mem:ecommerce
User Name: sa
Password:
```

## Fluxo principal

Use o arquivo `requests.http` ou execute:

```bash
curl -X POST http://localhost:8080/pedidos   -H "Content-Type: application/json"   -d '{
    "usuarioId": 1,
    "itens": [
      {
        "produtoId": 1,
        "quantidade": 2
      }
    ],
    "formaPagamento": "CARTAO",
    "numeroCartao": "4111111111111111"
  }'
```

## Regras simuladas de pagamento

- Valor menor ou igual a zero: recusado.
- Valor acima de R$ 10.000,00: recusado por limite.
- Cartão terminado em `0000`: cartão bloqueado.
- Cartão terminado em `1111`: aprovado.
- Outros cartões: aprovados quando o valor for válido.

## Aviso pedagógico

A arquitetura foi intencionalmente construída com problemas:

- organização horizontal por camada técnica;
- entidades JPA usadas diretamente nos controllers;
- relacionamentos entre entidades de contextos diferentes;
- `PedidoService` com múltiplas responsabilidades;
- acesso direto a vários repositórios;
- pagamento acoplado a pedido e usuário;
- dependência de um processador concreto;
- regras distribuídas em services;
- uma única transação envolvendo pedido, estoque e pagamento;
- ausência de Aggregate Root, Value Objects, portas e adaptadores.

Esses problemas fazem parte da atividade e não devem ser corrigidos antes da entrega aos alunos.
