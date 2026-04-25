# 📋 Gestão SEI Backend

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=flat-square&logo=docker)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/Testes-22%20passing-brightgreen?style=flat-square)](src/test)
[![License](https://img.shields.io/badge/Licen%C3%A7a-MIT-yellow?style=flat-square)](LICENSE)

> 🚀 Sistema de backend para controle de prazos e tramitação de processos do SEI, desenvolvido especialmente para servidores públicos.

## 📋 Sobre o Projeto

O **Gestão SEI Backend** é uma solução robusta para gerenciar o fluxo de processos administrativos do sistema SEI. Ele permite o acompanhamento de prazos, registro automático de histórico de tramitação e geração de relatórios gerenciais em PDF.

> 💡 **Este repositório é um template.** A organização [GestaoSEI](https://github.com/GestaoSEI) foi criada para que outros servidores públicos possam utilizar este sistema como ponto de partida, adaptando-o à realidade de sua própria unidade. Sinta-se à vontade para fazer um fork e personalizar.

## 🛠️ Arquitetura e Design

O projeto utiliza uma arquitetura baseada em camadas e segue padrões modernos de desenvolvimento. Para detalhes sobre o modelo de dados, ciclo de vida de processos e diagramas de sequência, acesse:

👉 **[Documentação de Arquitetura (Diagramas UML)](ARQUITETURA.md)**

### Modelo de Dados Resumido
```mermaid
classDiagram
    Usuario "1" -- "*" HistoricoProcesso : registra
    Processo "1" -- "*" HistoricoProcesso : possui
    class Usuario { +String login, +Role role }
    class Processo { +String numero, +StatusProcesso status, +LocalDate prazo }
```

## ✨ Funcionalidades e Regras de Negócio

| ID | Regra | Descrição |
| :---: | :--- | :--- |
| **RN01** | **Integridade** | Bloqueia exclusão de usuários com histórico de tramitação. |
| **RN02** | **Auditoria** | Registro automático no histórico para cada mudança de status/unidade. |
| **RN03** | **Prazos** | Sinalização de urgência (<= 5 dias) e expiração automática (Cron Job). |
| **RN04** | **Perfis** | Controle de acesso granular entre `ADMIN` e `USER`. |

### 🔐 **Segurança e Acesso**
- Autenticação via **JWT**.
- Login, reset de senha e troca de senha com validação de perfil.

### 📊 **Gestão de Processos**
- Cadastro, busca simplificada por termo único e filtros avançados.
- **Agendamento Automático:** Verificação diária à meia-noite para processos vencidos.

### 📈 **Relatórios Gerenciais**
- Exportação em PDF via **JasperReports** com filtros dinâmicos e formatação PT-BR.

## 🏗️ Estrutura de Pastas
```text
src/main/java/br/gov/gestaosei/gestao_sei_backend/
├── 📁 config/          # Segurança (JWT), OpenAPI e Filtros
├── 📁 controller/      # API Endpoints
├── 📁 model/           # Entidades JPA
├── 📁 service/         # Regras de Negócio e Agendamentos (Cron)
└── 📁 repository/      # Acesso ao Banco de Dados
```

## 🚀 Como Executar (Docker)

**Pré-requisitos:** Docker e Docker Compose.

1. **Subir a aplicação:**
   ```bash
   docker-compose up --build -d
   ```
2. **Swagger UI:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
3. **Credenciais Iniciais:** `admin` / `admin123`

## 🧪 Testes
O projeto conta com **22 testes unitários**.
```bash
./mvnw test
```

## 💾 Backup Local
Scripts PowerShell em `scripts/` para backups rápidos antes de importações:
- `.\scripts\snapshot-dados.ps1` (Backup completo: SQL + CSVs)

---
Made with ❤️ by Gilvaneide Medeiros
