<div align="center">

# <u> 🛠️ Spring Orders - Design Patterns & AI </u>

</div>

Este é um projeto básico desenvolvido para o curso da **DIO (Digital Innovation One)** no Bootcamp de Java AI envolvendo diferentes serviços aprendidos.

> **Contexto do Projeto:** A ideia inicial do projeto teve suas diretrizes concebidas com auxílio de Inteligência Artificial e evoluiu para uma aplicação em Spring Boot. O sistema demonstra na prática a aplicação de **Design Patterns** (como Singleton e Facade), controle de acesso (**Spring Security com RBAC e JWT**), consumo de serviços externos com **Spring Cloud OpenFeign**, versionamento e auditoria de banco de dados com **Flyway Migrations** e **Spring Data JPA**, além de recursos inteligentes com **Inteligência Artificial (LLM & STT)** utilizando o ecossistema **Spring AI**.

<br>

## 🚀 O que o projeto faz?

O projeto simula um sistema completo de gerenciamento de **Pedidos (Orders)** para estabelecimentos comerciais, contemplando:
* **Gerenciamento de Clientes (Clients):** Cadastro, consulta, atualização e exclusão de clientes.
* **Gerenciamento de Produtos (Products):** Controle de catálogo e buscas especializadas por nome e ID.
* **Gerenciamento de Pedidos (Orders):** Composição de pedidos com múltiplos itens, cálculo automatizado de valores totais, abatimento e reserva de estoque e transição de estados de pedido e pagamento.
* **Processamento de Pagamentos (Payments):** Suporte a tipos de pagamento (`ESPECIE`, `DEBITO`, `CREDITO` e `PIX`).
* **Autenticação e Autorização com RBAC (Role-Based Access Control):** Segurança com **Spring Security** e **JWT (JSON Web Token)** stateless, com controle de acessos por papéis (`MANAGER`, `STOCKER`, `CASHIER`, `CUSTOMER`).
* **Comunicação via emails com OpenFeign:** Envio automatizado de e-mails transacionais de confirmação e status de pedidos integrado com a API externa do **MailerSend**.
* **Inteligência Artificial (LLM):** Integração com **Google Gemini** (`gemini-3.6-flash`) via **Spring AI** para interpretação de pedidos em linguagem natural e execução de ferramentas (`@Tool`) automáticas para ações de busca e identificação de produtos no banco de dados.
* **Speech-To-Text (STT):** Integração com **Groq API** (`whisper-large-v3-turbo`) via interface OpenAI do **Spring AI** para transcrição de gravações de áudio (STT) e cadastro automático de pedidos.
* **Auditoria e Logs de Banco de Dados com Flyway:** Versionamento estruturado das tabelas do banco de dados MySQL e trilha de auditoria automatizada através de Triggers SQL para monitorar inserções, atualizações e exclusões de usuários.

<br>

## 💻 Tecnologias Utilizadas

* **Java 21 (LTS)**
* **Spring Boot (v4.1.0)**
  * **Spring Web MVC** (Desenvolvimento da API REST)
  * **Spring Data JPA** (Persistência e mapeamento objeto-relacional)
  * **Spring Security** (Segurança, filtros e autorização RBAC)
  * **Spring Cloud OpenFeign** (Cliente HTTP declarativo para consumo de APIs externas)
  * **Spring AI (BOM v2.0.0)**:
    * `spring-ai-starter-model-google-genai`: Integração com LLM Google Gemini (`gemini-3.6-flash`)
    * `spring-ai-starter-model-openai`: Integração com Groq Audio Transcription (`whisper-large-v3-turbo`)
  * **Spring Boot Starter Flyway** & **Flyway MySQL** (Migrações e auditoria de banco de dados)
  * **Spring Boot Docker Compose** (Suporte ao ciclo de vida do banco em container)
* **JWT (JSON Web Token - java-jwt v4.6.0 / Auth0)** para autenticação stateless
* **BCrypt Password Encoder** para hash seguro de senhas
* **MySQL 9.6** (Banco de dados relacional executado em container Docker)
* **Docker** (Plataforma de contâiner do banco de dados)
* **MailerSend API** (Envio de e-mails com templates personalizados)
* **Springdoc OpenAPI (v2.8.9)** com Swagger UI e suporte a autorização Bearer JWT
* **Dotenv Java (v3.2.0)** para carregamento de variáveis de ambiente a partir de arquivo `.env`
* **Lombok (v9.5.0)** para redução de código boilerplate (códigos frequentemente usados e reutilizáveis)
* **Gradle** (Automação de dependências e build do projeto)

<br>

## 🧩 Implementação de Design Patterns

Neste projeto, os seguintes padrões de projeto **(Design Patterns)** e padrões arquiteturais foram implementados:

1. **Singleton:** 
   No ecossistema Spring Boot, o container de Inversão de Controle (IoC) utiliza o escopo Singleton por padrão. As classes anotadas com `@Service`, `@RestController`, `@Repository` e `@Component`, bem como instâncias criadas com `@Bean`, são instanciadas uma única vez durante o ciclo de vida da aplicação. A injeção automática de dependências com `@Autowired` garante que as referências compartilhadas acessem a mesma instância gerenciada para processamento das funcionalidades.
2. **Facade:** 
   O padrão Facade foi aplicado na arquitetura através dos **Services** (como `OrderService`, `UserAuthService` e `ChatClientService`). Eles atuam como fachadas unificadas que orquestram a complexidade de diferentes repositórios, validação de regras de estoque, chamadas a APIs externas (OpenFeign), serviços de IA e transações de banco de dados, fornecendo interfaces limpas, centradas e simplificadas aos Controllers.

<br>

## 🎙️ Funcionamento da IA e Speech-To-Text (STT) no Projeto

O projeto conta com um módulo de Inteligência Artificial completo gerenciado pelo **Spring AI**, composto por dois pilares integrados:

```
[Áudio do Usuário]
         │
         ▼
[SpeechToTextService] ──► API Groq (Whisper Large V3 Turbo)
         │
         ▼ (Transcrição do Áudio em Texto)
[ChatClientService] ──► Google Gemini (Gemini 3.6 Flash)
         │
         ├──► Invoca @Tool ("get-product-by-name" em ProductService)
         │        └──► Consulta MySQL e resolve ID/dados do produto
         │
         ▼ (Estruturação JSON validada: ModelOrderRequestResponseAi)
[OrderService]
         ├──► Persiste Pedido (Orders), Itens (Order_Items) e Pagamento (Payments) no MySQL
         └──► Envia e-mail de confirmação via MailerSend (OpenFeign)
```

### 1. Speech-To-Text (STT) com Groq e Whisper
* **Tecnologia:** `spring-ai-starter-model-openai` apontado para o endpoint de transcrição de áudio da **Groq** (`https://api.groq.com/openai/v1`).
* **Modelo Utilizado:** `whisper-large-v3-turbo`.
* **Funcionamento:** O serviço `SpeechToTextService` recebe um arquivo de áudio (`MultipartFile`), valida o formato e monta um `AudioTranscriptionPrompt`. O modelo processa o arquivo e retorna a transcrição textual com a estrutura (JSON) dos produtos do catálogo (`/ai/audio-to-orderJson`) ou o texto puro (`/ai/audio-to-text`).

### 2. Large Language Model (LLM) com Google GenAI e Gemini
* **Tecnologia:** `spring-ai-starter-model-google-genai` com o modelo **Google Gemini** (`gemini-3.6-flash`) gerenciado pela interface `ChatClient`.
* **Prompt:** Um prompt orienta o modelo a atuar como um conversor estruturas de pedidos com base nas regras de negócio. O prompt estipula regras estritas de formatação, mapeamento de métodos de pagamento (`ESPECIE`, `DEBITO`, `CREDITO`, `PIX`) e tratamento de erros e informações ausentes.
* **Chamada de Ferramentas (Tool Calling):** O `ChatClientConfig` registra o `ProductService` como fornecedor de ferramentas. Quando o usuário informa um produto pelo nome (ex: *"dois cadernos e uma caneta"*), o LLM aciona automaticamente o método anotado com `@Tool` (`get-product-by-name`) para obter os IDs e os produtos reais completos do banco de dados.
* **Saída Tipada Estruturada:** O retorno da IA é automaticamente desserializado no record `ModelOrderRequestResponseAi`. Caso ocorra alguma pendência (ex: falta de método de pagamento ou quantidade de produto não informada), a IA preenche o campo explicativo `message` e o sistema trata o fluxo com a exceção `AiInvalidDataEntered`.

<br>

## 📜 Logs e Auditoria com Flyway Migrations

O banco de dados MySQL possui versionamento automatizado gerenciado pelo **Flyway**, garantindo que as alterações de schema e regras estruturais sejam aplicadas de forma reprodutível e controlada na pasta `src/main/resources/db/migration/`. Assim, a criação de tabelas (entidades) e FKs (chaves estrangeiras) foi definida e versionada pelos arquivos com extensão **.sql** gerenciados pela ferramenta.

### Trilha de Auditoria de Usuários
Para garantir rastreabilidade, segurança e auditoria (compliance) priorizou-se:
1. **Tabela de Auditoria:** Criação da tabela `users_audit`, responsável por armazenar cada evento, o tipo de operação realizada, estados anteriores e posteriores e data e horário da ação realizada.
2. **Triggers Automáticas no MySQL:**
   * **Insert:** Trigger `tgr_user_audit_insert` registra o evento `'I'` sempre que um novo usuário for inserido.
   * **Update:** Trigger `tgr_user_audit_update` registra a operação `'U'`, salvando as credenciais e roles antigas (`OLD`) e as novas (`NEW`).
   * **Delete:** Trigger `tgr_user_audit_delete` registra a exclusão com a operação `'D'`.

3. **Tabela no banco de dados `users_audit`**
```json
{
  "id": 1,
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "operation": "I",
  "oldUsername": null,
  "oldPassword": null,
  "oldRole": null,
  "username": "gerente_pedro",
  "password": "$2a$10$encrypted...",
  "role": "MANAGER",
  "datetime": "2026-10-14 21:42:00"
}
```
> **Nota:** O campo `operation` registra `'I'` (Insert), `'U'` (Update) ou `'D'` (Delete) conforme a operação realizada.

<br>

## 🌐 Endpoints e Funcionalidades Oferecidas

### 1. Autenticação e Usuários (`/auth`)
* `POST /auth/login-token`: Autentica o usuário validando credenciais criptografadas via BCrypt e retorna o token JWT assinado.
* `POST /auth/register`: Cadastra um novo usuário no sistema associando seu respectivo papel (`role`).
* `PUT /auth`: Atualiza o nome de usuário, senha e perfil de acesso mediante confirmação das credenciais anteriores.
* `DELETE /auth`: Exclui a conta de usuário informando `username` e `password`.

### 2. Produtos (`/products`)
* `GET /products/listAll`: Lista todos os produtos cadastrados.
* `GET /products/id/{id}`: Retorna os detalhes de um produto através do seu ID.
* `GET /products/search?name={name}`: Busca produtos que contêm no nome o texto informado.
* `POST /products`: Cadastra novo produto no estoque *(Permissão: `STOCKER`, `MANAGER`)*.
* `PATCH /products/{id}`: Atualiza dados de um produto existente *(Permissão: `STOCKER`, `MANAGER`)*.
* `DELETE /products/{id}`: Remove um produto pelo ID *(Permissão: `STOCKER`, `MANAGER`)*.

### 3. Pedidos (`/orders`)
* `GET /orders/listAll`: Lista todos os pedidos registrados.
* `GET /orders/{id}`: Busca um pedido pelo seu id contendo seus itens, valores gerais e dados de pagamento.
* `POST /orders`: Cria um novo pedido, valida e atualiza a quantidade dos itens em estoque, calcula os totais, vincula pagamento e cliente e envia o e-mail de confirmação ao cliente *(Permissão: `CASHIER`, `MANAGER`)*.
* `PATCH /orders/{id}`: Atualiza dados do pedido e itens associados, recalculando estoque e valores *(Permissão: `CASHIER`, `MANAGER`)*.
* `POST /orders/{id}/paid`: Simula a execução de um pagamento, atualizando o status do pedido para concluído (`CONCLUDED`) e do pagamento para aprovado (`APPROVED`) *(Permissão: `CASHIER`, `MANAGER`)*.
* `DELETE /orders/{id}`: Exclui um pedido do sistema *(Permissão: `CASHIER`, `MANAGER`)*.

### 4. Clientes (`/clients`)
* `GET /clients/listAll`: Lista todos os clientes cadastrados *(Permissão: `CASHIER`, `MANAGER`)*.
* `GET /clients/{id}`: Consulta dados de um cliente pelo ID *(Permissão: `CASHIER`, `MANAGER`)*.
* `POST /clients`: Cadastra um novo cliente *(Permissão: `CASHIER`, `CUSTOMER`, `MANAGER`)*.
* `PATCH /clients/{id}`: Atualiza informações de um cliente cadastrado *(Permissão: `CUSTOMER`, `MANAGER`)*.
* `DELETE /clients/{id}`: Remove um cliente registrado *(Permissão: `CUSTOMER`, `MANAGER`)*.

### 5. Inteligência Artificial (`/ai`)
* `GET /ai/text-to-orderJson?text={prompt}`: Processa um texto em linguagem natural através do LLM (Gemini), consulta o catálogo no banco de dados via Tool Calling e gera o objeto JSON formatado de `OrderRequest` *(Permissão: `CASHIER`, `MANAGER`)*.
* `POST /ai/audio-to-orderJson`: Recebe um arquivo de áudio (`multipart/form-data`), transcreve via STT (Whisper/Groq) e gera o o objeto JSON formatado de `OrderRequest` *(Permissão: `CASHIER`, `MANAGER`)*.
* `POST /ai/audio-to-text`: Recebe um arquivo de áudio (`multipart/form-data`) e retorna a transcrição literal em um JSON *(Permissão: `CASHIER`, `MANAGER`)*.
* `POST /ai/save-order-from-prompt?text={prompt}`: Converte o prompt de texto em um pedido (Order), valida estoque, salva no banco e envia o e-mail de notificação *(Permissão: `CASHIER`, `MANAGER`)*.
* `POST /ai/save-order-from-audio`: Recebe gravação de áudio, transcreve com STT, interpreta com LLM e estrutura objeto JSON do pedido (Order), grava o pedido no banco e envia o e-mail ao cliente *(Permissão: `CASHIER`, `MANAGER`)*.

<br>

## 🏗 Arquitetura do Projeto

O projeto é estruturado em arquitetura em camadas:

* **`Controller`:** Exposição dos endpoints RESTful, validações de requisição HTTP e mapeamento de DTOs.
* **`Client`:** Interfaces declarativas do **Spring Cloud OpenFeign** (`MailerSendClient`) para integração com provedores externos de e-mail via API REST.
* **`Service`:** Regras de negócio essenciais, orquestração de transações, integração com IA e chamadas externas.
  * **`Service/Enums`:** Definição dos enums (constantes) do domínio (`RolesEnum`, `PaymentMethodsEnum`, `StatusOrderEnum`, `StatusPaymentEnum`).
  * **`Service/Interfaces`:** Interfaces base com padronização de detrminados serviços (`CrudService`).
* **`Configs`:** Configurações e registros de beans da aplicação:
  * **`Configs/AI`:** Configuração do `ChatClient` do Spring AI com contexto de sistema (system prompt) e injeção de tools para o modelo Google Gemini.
  * **`Configs/security`:** Configurações segurança relacionadas com autenticação, filtro de validação JWT (`SecurityFilter`), emissor de tokens (`TokenService`), políticas de RBAC (`WebSecurityConfig`) e integração Swagger com Bearer Auth (`OpenAPIConfig`).
* **`Repository`:** Camada de persistência e interfaces de consulta com Spring Data JPA.
* **`Entity`:** Entidades mapeadas para o MySQL com anotações JPA/Hibernate (`Client`, `Order`, `OrderItem`, `Payment`, `Product`, `UserAuth`).
* **`Model`:** Modelos de segurança e sessão (`UserAuthModel` implementando `UserDetails`).
* **`DTO`:** Objetos de transferência de dados distribuídos em:
  * **`DTO/Request`:** Objetos de entrada validados com anotações do Jakarta Validation (`@Valid`, `@NotNull`, `@NotBlank`, `@Size`, `@Positive`).
  * **`DTO/Response`:** Objetos imutáveis (`records`) para saídas customizadas da API e integração de IA.
* **`Exception`:** Exceções customizadas para erros de API, processamento, entrada ou saída de dados e tratamento centralizado de exceções (`GlobalExceptionHandler`) com respostas em JSON padronizadas (`{"message":"..."}`) e códigos HTTP semânticos (400, 401, 403, 404, 409).
* **`resources/db/migration`:** Migrações versionadas do Flyway com scripts SQL para tabelas, constraints/FKs e triggers de auditoria para o banco de dados.
* **`resources/templates`:** Templates HTML utilizados para e-mails transacionais (`email_template.html`).

<br>

## 🧱 Estrutura das entidades - API

### Produto (`product`)
```json
{
  "id": 1,
  "name": "Notebook Gamer",
  "price": 4500.00,
  "stock": 15
}
```

### Pedido (`order`)
```json
{
  "orderId": 1,
  "client": {
    "id": 1,
    "name": "Pedro Henrique",
    "email": "pedrohenrique@gmail.com"
  },
  "payment": {
    "id": 1,
    "orderId": 1,
    "status": "PENDING",
    "type": "PIX",
    "paymentDate": "2026-10-14T21:40:00.000Z"
  },
  "totalValue": 9000.00,
  "status": "PENDING",
  "creationDate": "2026-10-14T21:40:00.000Z",
  "orderItems": [
    {
      "id": 1,
      "product": {
        "id": 1,
        "name": "Notebook Gamer",
        "price": 4500.00,
        "stock": 13
      },
      "orderId": 1,
      "amount": 2,
      "totalPrice": 9000.00
    }
  ]
}
```
> **Nota:** Valores válidos para `status`: `PENDING`, `CONCLUDED`.

### Item do Pedido (`order_item`)
```json
{
  "id": 1,
  "product": {
    "id": 1,
    "name": "Notebook Gamer",
    "price": 4500.00,
    "stock": 13
  },
  "orderId": 1,
  "amount": 2,
  "totalPrice": 9000.00
}
```

### Pagamento (`payment`)
```json
{
  "id": 1,
  "orderId": 1,
  "status": "PENDING",
  "type": "PIX",
  "paymentDate": "2026-10-14T21:40:00.000Z"
}
```
> **Nota:** Valores válidos para `type`: `ESPECIE`, `DEBITO`, `CREDITO`, `PIX`. Valores válidos para `status`: `PENDING`, `APPROVED`.

### Cliente (`client`)
```json
{
  "id": 1,
  "name": "Pedro Henrique",
  "email": "pedrohenrique@gmail.com"
}
```

### Usuário do Sistema (`user_auth`)
```json
{
  "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "username": "gerente_pedro",
  "password": "encrypted_bcrypt_password",
  "role": "MANAGER"
}
```
> **Nota:** Roles disponíveis: `MANAGER`, `STOCKER`, `CASHIER`, `CUSTOMER`.

### Resposta Estruturada da IA (`ModelOrderRequestResponseAi`)
Estrutura utilizada internamente pelo LLM (Gemini) ao converter voz ou texto em dados estruturados para uso no endpoint `POST /orders`:
```json
{
  "clientId": 1,
  "orderItems": [
    {
      "productId": 1,
      "amount": 2
    }
  ],
  "payment": {
    "type": "PIX"
  },
  "message": "order definido"
}
```

### Resposta de Transcrição de Áudio (`STT Transcription`)
```json
{
  "transcription": "Cliente 1 deseja comprar 2 unidades de Notebook Gamer e pagar com PIX"
}
```

<br>

## 🔐 Autenticação e Controle de Acesso (RBAC)

O sistema utiliza o **Spring Security** com autenticação stateless baseada em **JWT**. Todas as requisições autenticadas devem enviar o token no cabeçalho HTTP:
* `Authorization: Bearer <token>`

### Perfis de Acesso (Roles) e Permissões
| Perfil (Role) | Descrição | Permissões no Sistema                                                                                                                                                                  |
| :--- | :--- |:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`MANAGER`** | Gerente geral | Acesso irrestrito a todos os recursos da aplicação (produtos, pedidos, clientes, IA e relatórios).                                                                                     |
| **`STOCKER`** | Estoquista | Criação, atualização e exclusão de produtos no catálogo (`POST`, `PUT`, `DELETE` em `/products/**`).                                                                                   |
| **`CASHIER`** | Operador de Caixa / Vendas | Criação e atualização de pedidos (`/orders/**`), quitação de pagamentos (`/orders/{id}/paid`), acesso às operações de IA (`/ai/**`) e consulta e cadastro de clientes (`/clients/**`). |
| **`CUSTOMER`** | Cliente final | Autoatendimento: gerenciar o próprio cadastro de cliente (`POST`, `PUT`, `DELETE` em `/clients/**`).                                                                                   |

* **Rotas Públicas (Sem necessidade de Token):** 
  * Swagger UI e documentação OpenAPI: `/swagger-ui/**`, `/v3/**`
  * Endpoints de autenticação: `POST /auth/login-token`, `POST /auth/register`, `PUT /auth`, `DELETE /auth`
* **Rotas Autenticadas Comuns:** Operações de consulta `GET` em `/products/**` e `/orders/**` estão liberadas para qualquer usuário autenticado.

---

### 1. Registrar um Novo Usuário (POST `/auth/register`)
**Requisição:**
```json
{
  "username": "gerente",
  "password": "senha_segura_123",
  "role": "MANAGER"
}
```
**Resposta (201 Created):**
```json
{
  "username": "gerente",
  "role": "MANAGER"
}
```

### 2. Efetuar Login (POST `/auth/login-token`)
**Requisição:**
```json
{
  "username": "gerente",
  "password": "senha_segura_123"
}
```
**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. Atualizar Credenciais / Role (PUT `/auth`)
**Requisição:**
```json
{
  "oldUsername": "gerente",
  "oldPassword": "senha_segura_123",
  "newUsername": "gerente_atualizado",
  "newPassword": "nova_senha_456",
  "newRole": "MANAGER"
}
```
**Resposta (200 OK)**

### 4. Deletar Usuário (DELETE `/auth`)
**Requisição:**
```json
{
  "username": "gerente_atualizado",
  "password": "nova_senha_456"
}
```
**Resposta (404 NOT FOUND)**

<br>

## 📖 Exemplos de Uso da API

Todos os endpoints podem ser testados de forma interativa via **Swagger UI**:
`http://localhost:8080/swagger-ui/index.html`

> Para testar endpoints protegidos no Swagger, clique no botão **Authorize 🔓**, insira o token obtido no login e confirme.

---

### 🤖 Exemplos de Uso com Inteligência Artificial (AI & STT)

#### A. Transcrever Áudio em Texto (POST `/ai/audio-to-text`)
* **Endpoint:** `POST /ai/audio-to-text`
* **Tipo de Requisição:** `multipart/form-data`
* **Campo do formulário:** `audio` *(arquivo `.ogg`, `.mp3` ou `.wav` - por exemplo, o arquivo presente em `src/test/resources/audio/Compra_Realizada.ogg`)*
* **Resposta (200 OK):**
```json
{
  "transcription": "Gostaria de registrar para o cliente número um o pedido de duas unidades de Notebook Gamer com pagamento via PIX."
}
```

#### B. Converter Áudio em JSON de Pedido (POST `/ai/audio-to-orderJson`)
Transcreve a voz e converte diretamente para o objeto JSON do pedido:
* **Requisição:** `POST /ai/audio-to-orderJson` com o arquivo de áudio no campo `audio`.
* **Resposta (200 OK):** Objeto `OrderRequest` estruturado pronto para submissão.

#### C. Converter Texto em JSON de Pedido (GET `/ai/text-to-orderJson`)
O modelo interpreta o texto, descobre o ID do produto automaticamente no banco de dados e monta o objetp JSON:
* **Requisição:** `GET /ai/text-to-orderJson?text=Cliente%201%20deseja%202%20Notebook%20Gamer%20e%20pagar%20no%20PIX`
* **Resposta (200 OK):**
```json
{
  "clientId": 1,
  "orderItems": [
    {
      "productId": 1,
      "amount": 2
    }
  ],
  "payment": {
    "type": "PIX"
  }
}
```

#### D. Criar e Persistir Pedido Diretamente por Prompt de Voz ou Texto
* **Por texto:** `POST /ai/save-order-from-prompt?text=Cliente 1 comprou 2 Notebook Gamer pago via PIX`
* **Por áudio:** `POST /ai/save-order-from-audio` (com arquivo de voz anexado)
* **Resultado:** O sistema transcreve o áudio, interpreta o texto com o Gemini, consulta o catálogo com `@Tool`, deduz o estoque, salva o pedido no MySQL e dispara o e-mail de confirmação ao cliente via MailerSend, retornando o `OrderResponse` completo.

---

### 📦 Exemplos de Cadastros e Pedidos Tradicionais

#### Cadastro de Produto (POST `/products`):
```json
{
  "name": "Notebook Gamer",
  "price": 4500.00,
  "stock": 15
}
```

#### Cadastro de Cliente (POST `/clients`):
```json
{
  "name": "Pedro Henrique",
  "email": "pedrohenrique@gmail.com"
}
```

#### Cadastro Manual de Pedido (POST `/orders`):
```json
{
  "clientId": 1,
  "orderItems": [
    {
      "productId": 1,
      "amount": 2
    }
  ],
  "payment": {
    "type": "PIX"
  }
}
```

#### Concluir e Pagar Pedido (POST `/orders/{id}/paid`):
Executa um pagamento, atualizando o status do pedido para `CONCLUDED` e do pagamento para `APPROVED` a partir do **id** informado.

<br>

## ⚙️ Passo a passo para clonar e utilizar

### 1. Clonar o Repositório
```bash
git clone <url-do-repositorio>
cd Order_Spring_DesignPatterns
```

### 2. Pré-requisitos
* **Java 21** instalado e configurado nas variáveis de ambiente.
* **Docker** e **Docker Compose** instalados e em execução.
* Chaves de API para os serviços integrados:
  * Google AI Studio API Key (para o modelo Gemini LLM)
  * Groq Cloud API Key (para a transcrição STT com Whisper)
  * MailerSend API Token e Template ID (para o envio de e-mails transacionais)

### 3. Configurar as Variáveis de Ambiente (`.env`)
Copie o arquivo de exemplo `.env.example` para `.env`:
```bash
cp .env.example .env
```
Preencha os valores das chaves no arquivo `.env`:
```env
# Chave de segurança para assinatura e validação do token JWT
JWT_SECURITY_KEY=sua_chave_secreta_jwt_aqui

# Credenciais do banco de dados MySQL
MYSQL_USER=app
MYSQL_ROOT_PASSWORD=sua_senha_mysql

# Configurações do MailerSend (Spring Cloud OpenFeign)
MAILERSEND_API_TOKEN=mlsn.seu_token_mailersend
TEMPLATE_TOKEN=mlsn.seu_token_template
TEMPLATE_ID_HTML=id_do_template_configurado
ADDRESS_EMAIL=seu_email_verificado_mailersend

# Google AI Studio (Spring AI - Gemini LLM)
TOKEN_API_GOOGLE_GENAI=sua_chave_google_gemini

# Groq Cloud (Spring AI - Whisper STT)
TOKEN_API_GROQ_STT=sua_chave_groq_whisper
```

### 4. Inicializar o Banco de Dados com Docker Compose
O banco de dados MySQL 9.6 está configurado no arquivo `compose.yml` mapeando a porta local `3311`:
```bash
docker compose up -d
```
> O **Flyway** aplicará automaticamente todas as migrações e triggers de auditoria na primeira inicialização da aplicação.

### 5. Executar a Aplicação
* **No Windows:**
  ```bash
  .\gradlew.bat bootRun
  ```
* **No Linux / macOS:**
  ```bash
  ./gradlew bootRun
  ```

### 6. Acessar a Documentação Interativa
Assim que o console indicar que a aplicação subiu com sucesso, acesse:
* **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **OpenAPI Docs:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

> **NOTA:** O uso de IA durante o desenvolvimento do código deste projeto foi majoritariamente destinado à correção de erros, automatização de conteúdos do README, sugestões de melhorias de códigos e auxílio em integrações e funcionalidades de dependências externas (como o ecossistema Spring Boot).
