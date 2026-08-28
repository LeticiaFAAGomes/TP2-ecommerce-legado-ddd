# 🛒 E-commerce Legado — DDD

### TP1 — Refatoração com Domain-Driven Design

#### Refatoração de aplicação monolítica com foco em DDD, Bounded Contexts, Aggregate Root, Value Objects e Ports & Adapters

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/) [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot) [![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa) [![H2](https://img.shields.io/badge/H2-Database-09476B?style=for-the-badge&logo=h2&logoColor=white)](https://www.h2database.com/) [![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

---

## 📌 Sobre o Projeto

O **E-commerce Legado** é uma aplicação monolítica desenvolvida propositalmente com alto acoplamento entre suas responsabilidades, utilizada como base para uma atividade prática de **Domain-Driven Design (DDD)**.

O objetivo da atividade é realizar a **refatoração arquitetural da aplicação**, reduzindo o acoplamento entre os diferentes contextos do sistema e reorganizando o código de acordo com conceitos de DDD.

A refatoração possui como principal foco a criação do **contexto de Pagamento**, separando suas regras de negócio, persistência e integrações das demais partes da aplicação.

O projeto continua sendo uma aplicação **monolítica**, porém organizada internamente em contextos e responsabilidades bem definidas.

---

# 🎯 Objetivos

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
- Aprovar um pagamento.
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

Em vez disso, trabalha somente com os identificadores necessários para representar a relação com outros contextos.

---

# 🧱 Estrutura do Contexto de Pagamento

A organização do contexto de Pagamento segue a divisão entre **Domain**, **Application** e **Infrastructure**.

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
│   │   └── PagamentoId.java
│   │
│   ├── enums/
│   │   ├── StatusPagamento.java
│   │   └── FormaPagamento.java
│   │
│   └── repository/
│       └── PagamentoRepository.java
│
├── application/
│   └── PagamentoService.java
│
└── infrastructure/
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

A entidade `Pagamento` representa o **Aggregate Root** do contexto de Pagamento.

Ela controla o ciclo de vida do pagamento e concentra operações relacionadas ao seu estado.

Principais informações:

```text
Pagamento
├── PagamentoId
├── pedidoId
├── usuarioId
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
aprovar()
recusar()
```

---

# 💰 Value Objects

O contexto de Pagamento utiliza **Value Objects** para representar conceitos do domínio que possuem significado próprio.

## Dinheiro

Representa valores monetários utilizados pelo pagamento.

Responsável por encapsular o `BigDecimal` utilizado pelo domínio.

```text
Dinheiro
└── valor
```

---

## NumeroCartao

Representa o número do cartão utilizado no pagamento.

O Value Object permite centralizar comportamentos relacionados ao cartão, como:

- validação;
- representação;
- mascaramento do número.

O número completo do cartão não deve ser utilizado desnecessariamente fora do contexto.

---

## PagamentoId

Representa o identificador do pagamento.

A utilização de um Value Object para o identificador evita que o domínio dependa diretamente de tipos primitivos para representar conceitos importantes.

---

# 🔢 Enums

As informações que anteriormente eram representadas por `String` foram substituídas por tipos específicos do domínio.

## StatusPagamento

Representa o estado atual do pagamento.

```text
PENDENTE
APROVADO
RECUSADO
```

## FormaPagamento

Representa a forma utilizada para realizar o pagamento.

```text
CARTAO
```

A utilização de enums reduz erros relacionados a valores textuais inconsistentes.

---

# 📦 Application

A camada de Application coordena os casos de uso do contexto de Pagamento.

## PagamentoService

O `PagamentoService` atua como serviço de aplicação.

Sua responsabilidade é coordenar o processamento do pagamento utilizando:

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

O serviço não deve acessar diretamente:

```text
UsuarioRepository
ProdutoRepository
EstoqueRepository
PedidoRepository
```

Dessa forma, o contexto de Pagamento permanece isolado dos detalhes de persistência dos demais contextos.

---

# 🔌 Infrastructure

A camada de Infrastructure contém as implementações necessárias para integrar o domínio com tecnologias externas.

Entre elas estão:

- Persistência JPA.
- Implementação do repository.
- Processador de pagamento.
- Conversão entre entidade JPA e entidade de domínio.

---

# 🗄️ Persistência

A persistência utiliza uma separação entre o modelo de domínio e o modelo JPA.

```text
              DOMAIN
                 │
                 ▼
          Pagamento.java
                 │
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

O domínio define uma interface:

```text
PagamentoRepository
```

Essa interface representa uma porta para persistência.

A implementação fica na infraestrutura:

```text
PagamentoRepositoryImpl
```

que utiliza:

```text
JpaPagamentoRepository
```

Dessa forma:

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

O processamento do cartão é representado por uma abstração.

```text
PagamentoService
       │
       ▼
ProcessadorPagamento
       ▲
       │
ProcessadorPagamentoImpl
```

O objetivo é evitar que o serviço de aplicação fique diretamente acoplado a uma implementação concreta.

O processador retorna um:

```text
ResultadoProcessamento
```

contendo informações como:

- aprovação;
- status;
- motivo;
- código de autorização.

---

# 🔗 Integração com Pedido

O contexto de Pagamento não mantém relacionamentos JPA diretamente com as entidades:

```text
Pedido
Usuario
```

Em vez disso, utiliza identificadores:

```text
pedidoId
usuarioId
```

Isso evita que o Aggregate de Pagamento mantenha referências diretas a entidades pertencentes a outros contextos.

A comunicação conceitual passa a ser:

```text
Pedido
   │
   │ pedidoId
   ▼
Contexto de Pagamento
   │
   ▼
Pagamento
```

Dessa forma, o contexto de Pagamento não precisa conhecer a implementação interna do contexto de Pedido.

---

# 💰 Regras de Pagamento

As regras originais da aplicação foram preservadas durante a refatoração.

### Valor inválido

Valores menores ou iguais a zero são recusados.

```text
valor <= R$ 0,00
        ↓
     RECUSADO
```

### Limite

Valores superiores a R$ 10.000,00 são recusados.

```text
valor > R$ 10.000,00
        ↓
     RECUSADO
```

### Cartão bloqueado

Cartões terminados em:

```text
0000
```

são recusados.

### Cartão aprovado

Cartões válidos podem ser aprovados pelo processador de pagamento.

O cartão utilizado nos testes com final:

```text
1111
```

representa um cenário de aprovação.

---

# 🔄 Fluxo de Criação do Pedido

O fluxo principal da aplicação continua sendo a criação de um pedido.

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
   │
   ├──► Produto
   │
   ├──► Estoque
   │
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
       ┌────┴────┐
       ▼         ▼
   APROVADO   RECUSADO
       │         │
       ▼         ▼
     PAGO    PAGAMENTO_RECUSADO
```

---

# 🧪 Testes

Foram realizados testes para verificar tanto o funcionamento da aplicação quanto a manutenção das regras de negócio após a refatoração.

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

Exemplo:

```json
{
  "usuarioId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2
    },
    {
      "produtoId": 2,
      "quantidade": 1
    }
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

---

## Teste de cartão bloqueado

```http
POST http://localhost:8080/pedidos
Content-Type: application/json
```

```json
{
  "usuarioId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 1
    }
  ],
  "formaPagamento": "CARTAO",
  "numeroCartao": "4111111111110000"
}
```

Resultado esperado:

```text
Pagamento: RECUSADO
Motivo: CARTAO_BLOQUEADO
```

---

## Teste de limite excedido

```http
POST http://localhost:8080/pedidos
Content-Type: application/json
```

```json
{
  "usuarioId": 1,
  "itens": [
    {
      "produtoId": 4,
      "quantidade": 2
    }
  ],
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

Configuração:

```text
JDBC URL: jdbc:h2:mem:ecommerce
User Name: sa
Password:
```

## Console H2

Durante a execução da aplicação, o console pode ser acessado em:

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
│   │   │       │   │   │   └── PagamentoId.java
│   │   │       │   │   │
│   │   │       │   │   ├── enums/
│   │   │       │   │   │   ├── FormaPagamento.java
│   │   │       │   │   │   └── StatusPagamento.java
│   │   │       │   │   │
│   │   │       │   │   └── repository/
│   │   │       │   │       └── PagamentoRepository.java
│   │   │       │   │
│   │   │       │   ├── application/
│   │   │       │   │   └── PagamentoService.java
│   │   │       │   │
│   │   │       │   └── infrastructure/
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
└── README.md
```

---

# 🚀 Como Executar

## Pré-requisitos

É necessário possuir:

- JDK 25
- Maven 3.6.3 ou superior
- Git

Verifique o Java:

```bash
java -version
```

Verifique o Maven:

```bash
mvn -version
```

---

## 1. Clonar o projeto

```bash
git clone <URL-DO-REPOSITORIO>
```

Entre na pasta:

```bash
cd TP1-ecommerce-legado-ddd
```

---

## 2. Executar a aplicação

Utilize:

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

---

## 3. Executar os testes

Para executar os testes automatizados:

```bash
mvn test
```

Para realizar uma compilação limpa:

```bash
mvn clean test
```

---

# 🔎 Verificação da Refatoração

A refatoração foi realizada buscando atender aos principais problemas identificados na aplicação legada.

| Problema original                          | Solução aplicada                |
| ------------------------------------------ | ------------------------------- |
| Entidade de Pagamento acoplada a `Pedido`  | Uso de `pedidoId`               |
| Entidade de Pagamento acoplada a `Usuario` | Uso de `usuarioId`              |
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

---

# 📚 Conceitos de DDD Aplicados

O projeto aplica os seguintes conceitos:

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

---

# ⚠️ Observação sobre o Projeto Legado

A aplicação original foi disponibilizada propositalmente com problemas arquiteturais para serem identificados e corrigidos durante a atividade.

Entre os problemas estavam:

- organização horizontal por camada técnica;
- entidades JPA utilizadas diretamente;
- relacionamentos entre entidades de contextos diferentes;
- `PedidoService` com múltiplas responsabilidades;
- acesso direto a vários repositories;
- pagamento acoplado a pedido e usuário;
- dependência direta de processador concreto;
- regras de negócio espalhadas;
- ausência de Aggregate Root;
- ausência de Value Objects;
- ausência de portas de integração.

A proposta da atividade não é simplesmente mover classes entre pacotes, mas **reduzir as dependências entre os contextos e melhorar a modelagem do domínio**.

---

# ✅ Resultado

Após a refatoração, o sistema mantém seu comportamento funcional original, porém o contexto de Pagamento passa a possuir uma estrutura própria e responsabilidades melhor definidas.

A principal melhoria pode ser representada por:

```text
ANTES

PedidoService
   │
   ├── UsuarioRepository
   ├── ProdutoRepository
   ├── EstoqueRepository
   ├── PedidoRepository
   ├── PagamentoRepository
   └── PagamentoService
              │
              ├── PedidoRepository
              ├── UsuarioRepository
              └── ProcessadorPagamento
```

Depois da refatoração:

```text
DEPOIS

Pedido
   │
   │ integração por abstração
   ▼
Pagamento
   │
   ├── Aggregate Root
   ├── Value Objects
   ├── Enums
   ├── Regras de domínio
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

O resultado é um monólito com **maior coesão, menor acoplamento e melhor separação das responsabilidades de domínio**.

---

# 👩‍💻 Autora

**Letícia Gomes**

Projeto desenvolvido individualmente para a disciplina de **Domain-Driven Design (DDD) e Arquitetura de Softwares Escaláveis** do bloco de **Desenvolvimento de Softwares Escaláveis**.

---

# 📄 Licença

Este projeto está licenciado sob a licença **MIT**.
