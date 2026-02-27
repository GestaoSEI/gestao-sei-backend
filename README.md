# 📋 Gestão SEI Backend - Template

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

> 🚀 **Template profissional** para desenvolvimento de sistemas de gestão de processos SEI com Spring Boot REST API

## 📋 Sobre este Template

Este é um template completo e production-ready para criar APIs REST de gestão de processos administrativos baseados no sistema SEI (Sistema Eletrônico de Informações). Inclui autenticação, autorização, relatórios PDF e boas práticas de desenvolvimento.

## ✨ Features Principais

### 🔐 **Segurança**
- Spring Security com JWT
- Autenticação stateless
- Controle de acesso por roles (ADMIN/USER)
- Password encryption com BCrypt

### 📊 **Gestão de Processos**
- CRUD completo de processos
- Histórico de alterações
- Filtros avançados (status, unidade, prazo)
- Busca por palavra-chave
- Controle de prazos

### 📈 **Relatórios**
- Geração de PDF com JasperReports
- Relatórios dinâmicos baseados em filtros
- Exportação de dados

### 🛠️ **Desenvolvimento**
- OpenAPI/Swagger documentation
- Validação de dados (Jakarta Validation)
- Tratamento centralizado de exceções
- Testes unitários com JUnit 5 + Mockito

## 🏗️ Arquitetura

```
src/main/java/br/gov/gestaosei/gestao_sei_backend/
├── 📁 config/          # Configurações (Security, OpenAPI)
├── 📁 controller/      # Endpoints REST
├── 📁 dto/            # Data Transfer Objects
├── 📁 exception/      # Tratamento de exceções
├── 📁 model/          # Entidades JPA
├── 📁 repository/     # Interfaces Spring Data
└── 📁 service/        # Lógica de negócio
```

## 🚀 Tecnologias

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **Java** | 21 | Linguagem principal |
| **Spring Boot** | 3.2.3 | Framework principal |
| **Spring Security** | 6.x | Autenticação e autorização |
| **Spring Data JPA** | 3.x | Persistência de dados |
| **PostgreSQL** | 15+ | Banco de dados |
| **JWT** | 4.4.0 | Tokens de autenticação |
| **JasperReports** | 6.20.0 | Geração de PDF |
| **Lombok** | - | Boilerplate reduction |
| **OpenAPI** | 2.3.0 | Documentação API |

## 📋 Pré-requisitos

- **Java 21** ou superior
- **Maven 3.8+** ou **Gradle 7+**
- **PostgreSQL 15+**
- **IDE**: IntelliJ IDEA, Eclipse ou VS Code

## ⚙️ Configuração Rápida

### 1. **Clone este template**
```bash
git clone https://github.com/GestaoSEI/gestao-sei-backend.git
cd gestao-sei-backend
```

### 2. **Configure o banco de dados**
```sql
CREATE DATABASE gestao_sei;
CREATE USER gestao_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE gestao_sei TO gestao_user;
```

### 3. **Configure as variáveis de ambiente**
```bash
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestao_sei
spring.datasource.username=gestao_user
spring.datasource.password=sua_senha

# JWT Secret (mude para produção!)
jwt.secret=seu-secret-key-aqui
jwt.expiration=86400000
```

### 4. **Execute a aplicação**
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8081**

## 📚 Documentação da API

Acesse a documentação interativa:
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

## 🔧 Endpoints Principais

### **Autenticação**
```http
POST /api/auth/login
POST /api/auth/register
```

### **Processos**
```http
GET    /api/processos              # Listar todos
GET    /api/processos/{id}         # Buscar por ID
POST   /api/processos              # Criar novo
PUT    /api/processos/{id}         # Atualizar
DELETE /api/processos/{id}         # Excluir
GET    /api/processos/busca        # Busca por palavra-chave
GET    /api/processos/filtro       # Filtros avançados
GET    /api/processos/relatorio    # Gerar PDF
GET    /api/processos/{id}/historico # Histórico
```

## 🧪 Testes

### **Execute todos os testes**
```bash
mvn test
```

### **Execute com cobertura**
```bash
mvn clean test jacoco:report
```

Relatório de cobertura gerado em: `target/site/jacoco/index.html`

## 📦 Build e Deploy

### **Development**
```bash
mvn clean compile
mvn spring-boot:run
```

### **Production**
```bash
mvn clean package
java -jar target/gestao-sei-backend-0.0.1-SNAPSHOT.jar
```

### **Docker**
```bash
docker build -t gestao-sei-backend .
docker run -p 8081:8081 gestao-sei-backend
```

## 🔐 Segurança

### **JWT Configuration**
- **Expiration**: 24 horas (configurável)
- **Algorithm**: HS256
- **Secret**: Configure em `application.properties`

### **Roles**
- **ADMIN**: Acesso total ao sistema
- **USER**: Acesso limitado aos próprios processos

## 📝 Como Usar este Template

### **1. Personalize o Projeto**
```bash
# Atualize o pom.xml
<groupId>br.gov.sua.organizacao</groupId>
<artifactId>seu-projeto-backend</artifactId>

# Renomeie os packages
br.gov.sua.organizacao.seu_projeto_backend
```

### **2. Configure o Banco**
- Atualize `application.properties`
- Execute as migrations (se usar Flyway/Liquibase)
- Popule dados iniciais

### **3. Adapte as Regras de Negócio**
- Modifique as entidades em `model/`
- Implemente regras específicas em `service/`
- Adicione validações customizadas

## 🤝 Contribuindo

1. **Fork** este repositório
2. Crie uma **branch** (`git checkout -b feature/amazing-feature`)
3. **Commit** suas mudanças (`git commit -m 'Add amazing feature'`)
4. **Push** para a branch (`git push origin feature/amazing-feature`)
5. Abra um **Pull Request**

## 📄 Licença

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 🙋‍♀️ Suporte

- 📧 **Email**: gvmedeiros@prefeitura.gov.sp.br
- 💼 **LinkedIn**: [linkedin.com/in/gilvaneide-bertaccini](https://linkedin.com/in/gilvaneide-bertaccini/)
- 🐛 **Issues**: [GitHub Issues](https://github.com/GestaoSEI/gestao-sei-backend/issues)
- 📖 **Wiki**: [Documentação Completa](https://github.com/GestaoSEI/gestao-sei-backend/wiki)

## 🏆 Créditos

- **Desenvolvido por**: Gilvaneide Medeiros (https://github.com/GilvaneideMedeiros)
- **Tecnologias**: [Spring](https://spring.io/), [PostgreSQL](https://www.postgresql.org/)

---

⭐ **Se este template foi útil, deixe uma estrela!**

Made with ❤️ by Gilvaneide Medeiros (https://github.com/GilvaneideMedeiros)
