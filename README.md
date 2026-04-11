# 📋 Gestão SEI Backend

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=flat-square&logo=docker)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/Testes-22%20passing-brightgreen?style=flat-square)](src/test)

> 🚀 Sistema de backend para controle de prazos e tramitação de processos do SEI, desenvolvido especialmente para servidores públicos.

## 📋 Sobre o Projeto

O **Gestão SEI Backend** é uma solução robusta para gerenciar o fluxo de processos administrativos do sistema SEI. Ele permite o acompanhamento de prazos, registro automático de histórico de tramitação e geração de relatórios gerenciais em PDF.

> 💡 **Este repositório é um template.** A organização [GestaoSEI](https://github.com/GestaoSEI) foi criada para que outros servidores públicos possam utilizar este sistema como ponto de partida, adaptando-o à realidade de sua própria unidade. Sinta-se à vontade para fazer um fork e personalizar.

## ✨ Funcionalidades Implementadas

### 🔐 **Segurança e Acesso**

- Autenticação via **JWT (JSON Web Token)**.
- Controle de perfis: `ADMIN` (gestão total) e `USER` (consulta e edição).
- Auto-cadastro (`/auth/register`), reset de senha (`/auth/reset-password`) e gestão administrativa de usuários.
- **Exclusão de usuário** (ADMIN): bloqueada automaticamente se o usuário possuir registros no histórico de processos.
- **Troca de senha:** ADMIN pode redefinir a senha de qualquer usuário; USER pode alterar apenas a própria senha (exige senha atual).

### 📊 **Gestão de Processos**

- Cadastro completo de processos (Número, Tipo, Origem, Unidade, Status, Prazo).
- **Busca Simplificada:** Localize processos por qualquer termo (número, tipo, observação) em um único campo.
- **Filtros Avançados:** por status, unidade e prazo expirado.
- **Histórico Automático:** Toda mudança de status ou unidade gera um registro histórico com data, hora e usuário responsável.
- **Controle de Prazos Inteligente:**
  - Sinalização de urgência para processos que vencem em 5 dias ou menos.
  - **Agendamento Automático:** O sistema verifica diariamente à meia-noite processos vencidos e altera o status para `EXPIRADO`.

### 📈 **Relatórios Gerenciais**

- Geração de listagem em PDF via **JasperReports 7.0.6**.
- Filtros dinâmicos integrados ao relatório.
- Formatação de data padrão brasileiro (PT-BR).
- Relatório de usuários cadastrados.

## 🏗️ Arquitetura

```text
src/main/java/br/gov/gestaosei/gestao_sei_backend/
├── 📁 config/          # Segurança (JWT), OpenAPI e Filtros
├── 📁 controller/      # API Endpoints (Processos, Usuários, Auth)
├── 📁 dto/             # Objetos de Transferência de Dados
├── 📁 exception/       # Tratamento de erros e exceções
├── 📁 model/           # Entidades do Banco de Dados
├── 📁 repository/      # Acesso ao Banco (Spring Data JPA)
└── 📁 service/         # Regras de Negócio e Agendamentos
```

## 🚀 Como Executar (Docker)

O projeto está totalmente conteinerizado, facilitando o setup inicial.

**Pré-requisitos:** Docker e Docker Compose instalados.

1. **Subir a aplicação:**

   ```bash
   docker-compose up --build -d
   ```

2. **Acessar a documentação (Swagger):**
   Abra no navegador: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

3. **Credenciais Iniciais (DataInitializer):**
   - **Usuário:** `admin`
   - **Senha:** `admin123`

## 🔧 Endpoints Principais

### **Autenticação** (`/auth`)

| Método | Rota                   | Descrição                        |
| ------ | ---------------------- | -------------------------------- |
| `POST` | `/auth/login`          | Autentica e retorna token JWT    |
| `POST` | `/auth/register`       | Auto-cadastro de usuário         |
| `POST` | `/auth/reset-password` | Redefine senha pelo login        |

### **Processos** (`/api/processos`)

| Método | Rota | Descrição |
| ------ | ---- | --------- |
| `GET` | `/api/processos` | Lista todos os processos |
| `POST` | `/api/processos` | Cadastra novo processo |
| `PUT` | `/api/processos/atualizar?numero=` | Atualiza processo |
| `DELETE` | `/api/processos/excluir?numero=` | Remove processo |
| `GET` | `/api/processos/busca?keyword=` | Busca por termo |
| `GET` | `/api/processos/filtro` | Filtra por status / unidade / prazo |
| `GET` | `/api/processos/historico/{id}` | Histórico de tramitação |
| `GET` | `/api/processos/relatorio` | Gera PDF com filtros |

### **Usuários** (`/api/usuarios`)

| Método | Rota | Descrição |
| ------ | ---- | --------- |
| `GET` | `/api/usuarios` | Lista todos (ADMIN) |
| `GET` | `/api/usuarios/login/{login}` | Busca por login |
| `POST` | `/api/usuarios` | Cria usuário com senha padrão `senha123` (ADMIN) |
| `PUT` | `/api/usuarios/{id}` | Atualiza login/perfil (ADMIN) |
| `DELETE` | `/api/usuarios/{id}` | Exclui usuário (ADMIN) — bloqueado se houver histórico |
| `PUT` | `/api/usuarios/{id}/senha` | Altera senha (ADMIN sem senha atual; USER exige senha atual) |
| `GET` | `/api/usuarios/relatorio` | Gera PDF com lista de usuários (ADMIN) |

## 🧪 Testes

O projeto conta com **22 testes unitários** cobrindo as principais regras de negócio, incluindo validação de exclusão de usuário com histórico e troca de senha por perfil. Os testes são executados automaticamente durante o build da imagem Docker.

Para rodar localmente:

```bash
./mvnw test
```

## 🤝 Contribuindo

Este é um projeto **Open Source**. Leia o [Guia de Contribuição](CONTRIBUTING.md) para saber como configurar o ambiente, seguir os padrões do projeto e enviar um Pull Request.

## 📄 Licença

Este projeto está licenciado sob a **MIT License**.

---

Made with ❤️ by Gilvaneide Medeiros
