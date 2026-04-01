# 📋 Gestão SEI Backend

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=flat-square&logo=docker)](https://www.docker.com/)

> 🚀 Sistema de backend para controle de prazos e tramitação de processos do SEI, desenvolvido especialmente para servidores públicos.

## 📋 Sobre o Projeto

O **Gestão SEI Backend** é uma solução robusta para gerenciar o fluxo de processos administrativos do sistema SEI. Ele permite o acompanhamento de prazos, registro automático de histórico de tramitação e geração de relatórios gerenciais em PDF.

## ✨ Funcionalidades Implementadas

### 🔐 **Segurança e Acesso**
- Autenticação via **JWT (JSON Web Token)**.
- Controle de perfis: `ADMIN` (gestão total) e `USER` (consulta e edição).
- Endpoints para auto-cadastro (`/auth/register`) e gestão administrativa de usuários (`/api/usuarios`).

### 📊 **Gestão de Processos**
- Cadastro completo de processos (Número, Tipo, Origem, Unidade, Status, Prazo).
- **Busca Simplificada:** Localize processos por qualquer termo (número, tipo, observação) em um único campo.
- **Histórico Automático:** Toda mudança de status ou unidade gera um registro histórico com data, hora e usuário responsável.
- **Controle de Prazos Inteligente:**
    - Sinalização visual de urgência `(!)` no relatório para processos que vencem em 5 dias ou menos.
    - **Agendamento Automático:** O sistema verifica diariamente à meia-noite processos vencidos e altera o status para `EXPIRADO`.

### 📈 **Relatórios Gerenciais**
- Geração de listagem em PDF via **JasperReports**.
- Filtros dinâmicos integrados ao relatório.
- Formatação de data padrão brasileiro (PT-BR).

## 🏗️ Arquitetura

```
src/main/java/br/gov/gestaosei/gestao_sei_backend/
├── 📁 config/          # Segurança (JWT), OpenAPI e Filtros
├── 📁 controller/      # API Endpoints (Processos, Usuários, Auth)
├── 📁 dto/            # Objetos de Transferência de Dados
├── 📁 exception/      # Tratamento de erros e exceções
├── 📁 model/          # Entidades do Banco de Dados
├── 📁 repository/     # Acesso ao Banco (Spring Data JPA)
└── 📁 service/        # Regras de Negócio e Agendamentos
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

### **Processos**
- `POST /api/processos` - Cadastrar novo processo.
- `GET /api/processos/busca` - Busca simplificada (Query Param: `keyword`).
- `PUT /api/processos/atualizar` - Atualizar processo (Query Param: `numero`).
- `DELETE /api/processos/excluir` - Remover processo (Query Param: `numero`).
- `GET /api/processos/relatorio` - Gerar PDF filtrado.
- `GET /api/processos/historico/{id}` - Consultar histórico de tramitação.

### **Usuários**
- `POST /auth/register` - Auto-cadastro de usuários.
- `POST /api/usuarios` - Cadastro administrativo (realizado por ADMIN).

## 🧪 Testes

O projeto conta com uma suíte de **testes unitários** cobrindo as principais regras de negócio. Os testes são executados automaticamente durante o build da imagem Docker.

Para rodar localmente:
```bash
mvn test
```

## 🤝 Contribuindo

Este é um projeto **Open Source**. Sinta-se à vontade para abrir Issues ou enviar Pull Requests na nossa organização [GestaoSEI](https://github.com/GestaoSEI).

## 📄 Licença

Este projeto está licenciado sob a **MIT License**.

---
Made with ❤️ by Gilvaneide Medeiros
