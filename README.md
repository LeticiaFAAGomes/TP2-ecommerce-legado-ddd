# 🛒 E-commerce Legado — DDD

### Teste de Performance 2 — Aggregates, Consistência Transacional e Eventos de Domínio

#### Refatoração de aplicação monolítica com foco em DDD, Bounded Contexts, Aggregate Root, Value Objects, Ports & Adapters e Domain Events

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/) [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot) [![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa) [![H2](https://img.shields.io/badge/H2-Database-09476B?style=for-the-badge&logo=h2&logoColor=white)](https://www.h2database.com/) [![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

---

## 📌 Sobre o Projeto

O **E-commerce Legado** é uma aplicação monolítica desenvolvida propositalmente com alto acoplamento entre suas responsabilidades, utilizada como base para atividades práticas de **Domain-Driven Design (DDD)**.

No **TP1**, o foco foi a refatoração arquitetural de Pagamentos da aplicação, reduzindo o acoplamento entre os diferentes contextos e reorganizando o código de acordo com conceitos de DDD, com destaque para a criação do **contexto de Pagamento**.

No **TP2**, o foco avança para os conceitos de **consistência transacional dos Aggregates** e **Eventos de Domínio**, evoluindo o Aggregate `Pagamento` para publicar internamente os eventos relevantes que ocorrem durante seu ciclo de vida (como a aprovação de um pagamento), preparando o terreno para integrações assíncronas entre contextos.

O projeto continua sendo uma aplicação **monolítica**, porém organizada internamente em contextos e responsabilidades bem definidas.

---

# 🎯 Objetivos

## TP1

- Refatorar uma aplicação monolítica propositalmente acoplada.
- Aplicar conceitos de Domain-Driven Design.
- Identificar e separar responsabilidades do domínio.
- Criar um Bounded Context de Pagamento.
- Reduzir o acoplamento entre Pedido e Pagamento.
- Implementar Aggregate Root.
- Implementar Value Objects.
- Substituir strings relacionadas ao domínio por enums.
- Encapsular regras de negócio no domínio.
- Separar domínio de infraestrutura.
- Utilizar interfaces como portas de acesso e integração.
- Separar o modelo de domínio do modelo de persistência JPA.
- Criar abstração para o processador de pagamentos.
- Manter o comportamento original da aplicação após a refatoração.

## TP2

- Entender a razão de existir dos Aggregates e sua fronteira de consistência transacional.
- Reforçar o uso de referência entre Aggregates apenas por identificador (`PedidoId`, `UsuarioId`).
- Criar a abstração de **Domain Event** (`DomainEvent`).
- Implementar um evento de domínio concreto (`PagamentoAprovadoEvent`).
- Criar um `AggregateRoot` genérico, responsável por registrar os eventos ocorridos no Aggregate.
- Fazer o Aggregate `Pagamento` publicar um evento ao ser aprovado.
- Documentar os conceitos de filas, tópicos, Event Store e Event Sourcing.

---

# 🏛️ Arquitetura

O projeto continua utilizando uma arquitetura **monolítica**, porém passa a possuir uma organização interna baseada em responsabilidades e contextos de domínio.

A refatoração ocorre no contexto de **Pagamento**.

```text
┌────────────────────────────────────────────────────────────┐
│                       E-COMMERCE                           │
│                       Monólito                             │
│                                                            │
│  ┌───────────────┐       ┌──────────────────────────────┐  │
│  │    Pedido     │       │          Pagamento           │  │
│  │               │──────►│                              │  │
│  │ Application   │       │ Application                  │  │
│  │ Domain        │       │ Domain                       │  │
│  │ Infrastructure│       │ Infrastructure               │  │
│  └───────────────┘       └──────────────┬───────────────┘  │
│                                         │                  │
│                                         ▼                  │
│                              ┌─────────────────────┐       │
│                              │ Processador de      │       │
│                              │ Pagamento           │       │
│                              └─────────────────────┘       │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

A comunicação entre Pedido e Pagamento ocorre por meio de uma **abstração**, evitando que o contexto de Pagamento conheça diretamente os repositórios de outros contextos.

---

# 🧩 Bounded Context — Pagamento

O contexto de Pagamento concentra as responsabilidades relacionadas ao processamento e ao estado dos pagamentos.

Entre suas responsabilidades estão:

- Criar um pagamento.
- Validar informações necessárias.
- Processar o pagamento.
- Aprovar um pagamento (e publicar o evento correspondente).
- Recusar um pagamento.
- Registrar o motivo da recusa.
- Registrar o código de autorização.
- Controlar o status do pagamento.
- Armazenar informações relacionadas ao pedido e usuário por meio de identificadores.
- Persistir os pagamentos.

O contexto de Pagamento **não acessa diretamente**:

```text
UsuarioRepository
ProdutoRepository
EstoqueRepository
PedidoRepository
```

Em vez disso, trabalha somente com os identificadores necessários (encapsulados em Value Objects como `PedidoId` e `UsuarioId`) para representar a relação com outros contextos.

---

# 🧱 Estrutura do Contexto de Pagamento

A organização do contexto de Pagamento segue a divisão entre **Domain**, **Application** e **Infrastructure**. A partir do TP2, o domínio passa a conter também os pacotes `event` (eventos de domínio) e `shared` (abstrações reutilizáveis por qualquer Aggregate, como o `AggregateRoot`).

```text
payment/
│
├── domain/
│   ├── model/
│   │   └── Pagamento.java
│   │
│   ├── valueObject/
│   │   ├── Dinheiro.java
│   │   ├── NumeroCartao.java
│   │   ├── PagamentoId.java
│   │   ├── PedidoId.java
│   │   └── UsuarioId.java
│   │
│   ├── enums/
│   │   ├── StatusPagamento.java
│   │   └── FormaPagamento.java
│   │
│   ├── event/
│   │   ├── DomainEvent.java
│   │   └── PagamentoAprovadoEvent.java
│   │
│   ├── shared/
│   │   └── AggregateRoot.java
│   │
│   └── repository/
│       └── PagamentoRepository.java
│
├── application/
│   └── PagamentoService.java
│
└── infrastruture/
    ├── persistence/
    │   ├── PagamentoJpaEntity.java
    │   ├── JpaPagamentoRepository.java
    │   └── PagamentoRepositoryImpl.java
    │
    └── payment/
        ├── ProcessadorPagamento.java
        └── ResultadoProcessamento.java
```

---

# 🧠 Domain

A camada de domínio concentra os conceitos e regras relacionados ao negócio de pagamentos.

Ela não deve depender diretamente de controllers, repositories JPA ou detalhes específicos de infraestrutura.

---

## 💳 Aggregate Root — Pagamento

A entidade `Pagamento` representa o **Aggregate Root** do contexto de Pagamento e, a partir do TP2, estende `AggregateRoot`, herdando a capacidade de registrar eventos de domínio.

Ela controla o ciclo de vida do pagamento e concentra operações relacionadas ao seu estado, garantindo que todas as alterações realizadas dentro de uma mesma operação sejam consistentes entre si — essa é a **fronteira de consistência transacional** do Aggregate.

Principais informações:

```text
Pagamento (extends AggregateRoot)
├── PagamentoId
├── PedidoId
├── UsuarioId
├── Dinheiro
├── FormaPagamento
├── NumeroCartao
├── StatusPagamento
├── motivo
├── codigoAutorizacao
└── processadoEm
```

O pagamento pode ser criado como:

```text
PENDENTE
```

e posteriormente assumir um dos estados:

```text
APROVADO
RECUSADO
```

As alterações de estado são realizadas por comportamentos do próprio domínio, como:

```java
aprovar(String codigoAutorizacao)
recusar(String motivo)
```

Ao ser aprovado, o Aggregate registra internamente um evento de domínio (`PagamentoAprovadoEvent`), sem realizar a publicação diretamente — essa responsabilidade fica a cargo de uma camada externa ao domínio.

---

# 💰 Value Objects

O contexto de Pagamento utiliza **Value Objects** para representar conceitos do domínio que possuem significado próprio.

## Dinheiro

Representa valores monetários utilizados pelo pagamento. Responsável por encapsular o `BigDecimal` utilizado pelo domínio.

## NumeroCartao

Representa o número do cartão utilizado no pagamento, centralizando validação, representação e mascaramento do número.

## PagamentoId

Representa o identificador do pagamento, evitando que o domínio dependa diretamente de tipos primitivos.

## PedidoId e UsuarioId

Representam, respectivamente, a referência ao Aggregate `Pedido` e ao `Usuario`. Como um Aggregate só deve acessar outro pelo identificador, esses Value Objects evitam o uso de `Long` cru dentro do domínio de Pagamento, reduzindo o acoplamento entre os contextos.

---

# 🔢 Enums

As informações que anteriormente eram representadas por `String` foram substituídas por tipos específicos do domínio.

## StatusPagamento

```text
PENDENTE
APROVADO
RECUSADO
```

## FormaPagamento

```text
CARTAO
```

A utilização de enums reduz erros relacionados a valores textuais inconsistentes.

---

# 📣 Domain Events (TP2)

O contexto de Pagamento passa a publicar **eventos de domínio**: fatos relevantes que aconteceram durante o ciclo de vida do Aggregate e que podem interessar a outras partes do sistema.

## DomainEvent

Interface que define a estrutura mínima de qualquer evento de domínio:

```text
DomainEvent
└── occurredOn(): LocalDateTime
```

## PagamentoAprovadoEvent

Implementação concreta, publicada quando um pagamento é aprovado:

```text
PagamentoAprovadoEvent
├── pagamentoId
├── pedidoId
└── occurredOn
```

## AggregateRoot

Classe base reutilizável por qualquer Aggregate do sistema, responsável por acumular os eventos ocorridos durante uma operação:

```text
AggregateRoot
├── registrarEvento(DomainEvent)
├── eventosOcorridos(): List<DomainEvent>
└── limparEventos()
```

O fluxo de publicação segue a ideia de que o domínio apenas **registra** o evento; a infraestrutura (ou a camada de aplicação) é responsável por **ler** a lista de eventos após a persistência e efetivamente publicá-los — por exemplo, em um tópico de mensageria — mantendo o domínio livre de dependências de infraestrutura.

```text
Pagamento.aprovar()
        │
        ▼
registrarEvento(PagamentoAprovadoEvent)
        │
        ▼
PagamentoRepository.salvar(...)
        │
        ▼
eventosOcorridos() ──► Publicação (tópico / fila) ──► limparEventos()
```

---

# 📦 Application

A camada de Application coordena os casos de uso do contexto de Pagamento.

## PagamentoService

Coordena o processamento do pagamento utilizando:

```text
Pagamento
     │
     ▼
ProcessadorPagamento
     │
     ▼
ResultadoProcessamento
     │
     ▼
Pagamento.aprovar()
ou
Pagamento.recusar()
     │
     ▼
PagamentoRepository
```

O serviço não deve acessar diretamente `UsuarioRepository`, `ProdutoRepository`, `EstoqueRepository` ou `PedidoRepository`, mantendo o contexto de Pagamento isolado dos detalhes de persistência dos demais contextos.

---

# 🔌 Infrastructure

A camada de Infrastructure contém as implementações necessárias para integrar o domínio com tecnologias externas: persistência JPA, implementação do repository, processador de pagamento e conversão entre entidade JPA e entidade de domínio.

---

# 🗄️ Persistência

A persistência utiliza uma separação entre o modelo de domínio e o modelo JPA.

```text
              DOMAIN
                 │
                 ▼
          Pagamento.java
                 │
        PagamentoRepository
                 │
                 ▼
          IMPLEMENTAÇÃO
                 │
                 ▼
       PagamentoJpaEntity
                 │
                 ▼
     JpaPagamentoRepository
                 │
                 ▼
                H2
```

Essa separação evita que a entidade de domínio dependa diretamente da infraestrutura de persistência.

---

# 📚 Repository Pattern

```text
Domain
   │
   ▼
PagamentoRepository
   ▲
   │
PagamentoRepositoryImpl
   │
   ▼
JpaPagamentoRepository
```

O domínio conhece apenas o contrato, enquanto a infraestrutura conhece a tecnologia utilizada.

---

# 💳 Processador de Pagamento

```text
PagamentoService
       │
       ▼
ProcessadorPagamento
       ▲
       │
ProcessadorPagamentoImpl
```

O processador retorna um `ResultadoProcessamento`, contendo aprovação, status, motivo e código de autorização.

---

# 🔗 Integração com Pedido

O contexto de Pagamento não mantém relacionamentos JPA diretamente com `Pedido` e `Usuario`. Em vez disso, utiliza `PedidoId` e `UsuarioId`:

```text
Pedido
   │
   │ PedidoId
   ▼
Contexto de Pagamento
   │
   ▼
Pagamento
```

---

# 💰 Regras de Pagamento

### Valor inválido

Valores menores ou iguais a zero são recusados.

### Limite

Valores superiores a R$ 10.000,00 são recusados.

### Cartão bloqueado

Cartões terminados em `0000` são recusados.

### Cartão aprovado

Cartões terminados em `1111` representam um cenário de aprovação (e disparam o `PagamentoAprovadoEvent`).

---

# 🔄 Fluxo de Criação do Pedido

```text
Cliente
   │
   │ POST /pedidos
   ▼
PedidoController
   │
   ▼
PedidoService
   │
   ├──► Usuário
   ├──► Produto
   ├──► Estoque
   └──► Pedido
            │
            ▼
      Contexto Pagamento
            │
            ▼
      PagamentoService
            │
            ▼
   ProcessadorPagamento
            │
       ┌────┴────────────────────┐
       ▼                         ▼
   APROVADO                  RECUSADO
       │                         │
       ▼                         ▼
  PagamentoAprovadoEvent   PAGAMENTO_RECUSADO
       │
       ▼
     PAGO
```

---

# 🧪 Testes

## Listar usuários

```http
GET http://localhost:8080/usuarios
```

## Listar produtos

```http
GET http://localhost:8080/produtos
```

## Consultar estoque

```http
GET http://localhost:8080/estoques
```

## Criar pedido aprovado

```http
POST http://localhost:8080/pedidos
Content-Type: application/json
```

```json
{
  "usuarioId": 1,
  "itens": [
    { "produtoId": 1, "quantidade": 2 },
    { "produtoId": 2, "quantidade": 1 }
  ],
  "formaPagamento": "CARTAO",
  "numeroCartao": "4111111111111111"
}
```

Resultado esperado:

```text
Pagamento: APROVADO
Pedido: PAGO
```

## Teste de cartão bloqueado

```http
POST http://localhost:8080/pedidos
Content-Type: application/json
```

```json
{
  "usuarioId": 1,
  "itens": [{ "produtoId": 1, "quantidade": 1 }],
  "formaPagamento": "CARTAO",
  "numeroCartao": "4111111111110000"
}
```

Resultado esperado:

```text
Pagamento: RECUSADO
Motivo: CARTAO_BLOQUEADO
```

## Teste de limite excedido

```http
POST http://localhost:8080/pedidos
Content-Type: application/json
```

```json
{
  "usuarioId": 1,
  "itens": [{ "produtoId": 4, "quantidade": 2 }],
  "formaPagamento": "CARTAO",
  "numeroCartao": "5555555555554444"
}
```

Resultado esperado:

```text
Pagamento: RECUSADO
Motivo: LIMITE_EXCEDIDO
```

---

# 🗃️ Banco de Dados

O projeto utiliza o **H2 Database** em memória.

```text
JDBC URL: jdbc:h2:mem:ecommerce
User Name: sa
Password:
```

Console H2 disponível em:

```text
http://localhost:8080/h2-console
```

---

# 🌐 Endpoints

## 👤 Usuários

| Método | Endpoint    | Descrição         |
| ------ | ----------- | ----------------- |
| GET    | `/usuarios` | Lista os usuários |

## 📦 Produtos

| Método | Endpoint    | Descrição         |
| ------ | ----------- | ----------------- |
| GET    | `/produtos` | Lista os produtos |

## 📊 Estoque

| Método | Endpoint    | Descrição          |
| ------ | ----------- | ------------------ |
| GET    | `/estoques` | Consulta o estoque |

## 🛒 Pedidos

| Método | Endpoint        | Descrição        |
| ------ | --------------- | ---------------- |
| GET    | `/pedidos`      | Lista os pedidos |
| GET    | `/pedidos/{id}` | Busca um pedido  |
| POST   | `/pedidos`      | Cria um pedido   |

---

# 🛠️ Tecnologias

- **Java 25**
- **Spring Boot 4.1.0**
- **Spring Web**
- **Spring Data JPA**
- **Bean Validation**
- **H2 Database**
- **Maven**
- **Domain-Driven Design**
- **Aggregate Root**
- **Value Objects**
- **Repository Pattern**
- **Ports & Adapters**
- **Bounded Contexts**
- **Domain Events**

---

# 📁 Estrutura Geral do Projeto

```text
ecommerce-legado-ddd
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/edu/infnet/ecommerce/
│   │   │       │
│   │   │       ├── config/
│   │   │       │   └── DadosIniciaisConfig.java
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── EstoqueController.java
│   │   │       │   ├── PedidoController.java
│   │   │       │   ├── ProdutoController.java
│   │   │       │   └── UsuarioController.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── Estoque.java
│   │   │       │   ├── ItemPedido.java
│   │   │       │   ├── Pedido.java
│   │   │       │   ├── Produto.java
│   │   │       │   └── Usuario.java
│   │   │       │
│   │   │       ├── exception/
│   │   │       │   ├── ApiError.java
│   │   │       │   ├── EstoqueInsuficienteException.java
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   ├── PagamentoRecusadoException.java
│   │   │       │   └── RecursoNaoEncontradoException.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── EstoqueRepository.java
│   │   │       │   ├── PedidoRepository.java
│   │   │       │   ├── ProdutoRepository.java
│   │   │       │   └── UsuarioRepository.java
│   │   │       │
│   │   │       ├── request/
│   │   │       │   ├── AjusteEstoqueRequest.java
│   │   │       │   ├── CriarPedidoRequest.java
│   │   │       │   └── ItemPedidoRequest.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── EstoqueService.java
│   │   │       │   ├── PedidoService.java
│   │   │       │   ├── ProdutoService.java
│   │   │       │   └── UsuarioService.java
│   │   │       │
│   │   │       ├── payment/
│   │   │       │   │
│   │   │       │   ├── domain/
│   │   │       │   │   ├── model/
│   │   │       │   │   │   └── Pagamento.java
│   │   │       │   │   │
│   │   │       │   │   ├── valueObject/
│   │   │       │   │   │   ├── Dinheiro.java
│   │   │       │   │   │   ├── NumeroCartao.java
│   │   │       │   │   │   ├── PagamentoId.java
│   │   │       │   │   │   ├── PedidoId.java
│   │   │       │   │   │   └── UsuarioId.java
│   │   │       │   │   │
│   │   │       │   │   ├── enums/
│   │   │       │   │   │   ├── FormaPagamento.java
│   │   │       │   │   │   └── StatusPagamento.java
│   │   │       │   │   │
│   │   │       │   │   ├── event/
│   │   │       │   │   │   ├── DomainEvent.java
│   │   │       │   │   │   └── PagamentoAprovadoEvent.java
│   │   │       │   │   │
│   │   │       │   │   ├── shared/
│   │   │       │   │   │   └── AggregateRoot.java
│   │   │       │   │   │
│   │   │       │   │   └── repository/
│   │   │       │   │       └── PagamentoRepository.java
│   │   │       │   │
│   │   │       │   ├── application/
│   │   │       │   │   └── PagamentoService.java
│   │   │       │   │
│   │   │       │   └── infrastruture/
│   │   │       │       ├── persistence/
│   │   │       │       │   ├── PagamentoJpaEntity.java
│   │   │       │       │   ├── JpaPagamentoRepository.java
│   │   │       │       │   └── PagamentoRepositoryImpl.java
│   │   │       │       │
│   │   │       │       └── payment/
│   │   │       │           ├── ProcessadorPagamento.java
│   │   │       │           └── ResultadoProcessamento.java
│   │   │       │
│   │   │       └── EcommerceLegadoApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.yml
│   │
│   └── test/
│       └── java/
│
├── requests.http
├── .gitignore
├── pom.xml
├── instrucoes.md
├── TP2.md
└── README.md
```

---

# 🚀 Como Executar

## Pré-requisitos

- JDK 25
- Maven 3.6.3 ou superior
- Git

```bash
java -version
mvn -version
```

## 1. Clonar o projeto

```bash
git clone <URL-DO-REPOSITORIO>
cd TP1-ecommerce-legado-ddd
```

## 2. Executar a aplicação

```bash
mvn spring-boot:run
```

Disponível em:

```text
http://localhost:8080
```

## 3. Executar os testes

```bash
mvn test
mvn clean test
```

---

# 🔎 Verificação da Refatoração

| Problema original                          | Solução aplicada                |
| ------------------------------------------ | ------------------------------- |
| Entidade de Pagamento acoplada a `Pedido`  | Uso de `PedidoId`               |
| Entidade de Pagamento acoplada a `Usuario` | Uso de `UsuarioId`              |
| Strings para status                        | `StatusPagamento`               |
| Strings para forma de pagamento            | `FormaPagamento`                |
| `BigDecimal` diretamente no domínio        | `Dinheiro`                      |
| Número do cartão diretamente no domínio    | `NumeroCartao`                  |
| ID primitivo do pagamento                  | `PagamentoId`                   |
| Regras espalhadas                          | Regras encapsuladas no domínio  |
| Repository JPA diretamente no domínio      | Interface de repository         |
| Domínio dependente de JPA                  | Entidade JPA separada           |
| Processador concreto                       | Abstração para processador      |
| Pagamento acessando repositories externos  | Removido                        |
| Falta de Aggregate Root                    | `Pagamento` como Aggregate Root |
| Ausência de rastreabilidade de mudanças    | `DomainEvent` + `AggregateRoot` |
| Publicação de eventos acoplada ao domínio  | Domínio apenas registra eventos |

---

# 📚 Conceitos de DDD Aplicados

- **Domain-Driven Design**
- **Bounded Context**
- **Aggregate**
- **Aggregate Root**
- **Value Objects**
- **Entities**
- **Domain Services**
- **Application Services**
- **Repository Pattern**
- **Ports & Adapters**
- **Separation of Concerns**
- **Encapsulation**
- **Low Coupling**
- **High Cohesion**
- **Domain Events**
- **Consistência Transacional**

---

# ✅ Resultado

Depois da refatoração (TP1 + TP2):

```text
Pedido
   │
   │ integração por abstração
   ▼
Pagamento (Aggregate Root)
   │
   ├── Value Objects
   ├── Enums
   ├── Regras de domínio
   ├── Domain Events (registrados via AggregateRoot)
   │
   ▼
PagamentoRepository
   │
   ▼
PagamentoRepositoryImpl
   │
   ▼
JPA
```

O resultado é um monólito com **maior coesão, menor acoplamento e melhor separação das responsabilidades de domínio**, agora também capaz de expor os eventos relevantes ocorridos em seus Aggregates.

---

# 👩‍💻 Autora

**Letícia Gomes**

Projeto desenvolvido individualmente para a disciplina de **Domain-Driven Design (DDD) e Arquitetura de Softwares Escaláveis** do bloco de **Desenvolvimento de Softwares Escaláveis**.

---

# 📄 Licença

Este projeto está licenciado sob a licença **MIT**.
