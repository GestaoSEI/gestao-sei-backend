# 🤝 Guia de Contribuição — Gestão SEI Backend

Obrigada pelo interesse em contribuir! Este repositório faz parte da organização [GestaoSEI](https://github.com/GestaoSEI), criada como **template para servidores públicos** que desejam adotar um sistema de controle de processos SEI em sua unidade. Contribuições que tornem o projeto mais genérico e reutilizável são especialmente bem-vindas.

Este guia explica como configurar o ambiente, seguir os padrões do projeto e enviar contribuições.

## 📋 Índice

- [Pré-requisitos](#pré-requisitos)
- [Configuração do Ambiente Local](#configuração-do-ambiente-local)
- [Rodando os Testes](#rodando-os-testes)
- [Padrões de Código](#padrões-de-código)
- [Padrões de Commit](#padrões-de-commit)
- [Como Enviar um Pull Request](#como-enviar-um-pull-request)
- [Reportando Bugs](#reportando-bugs)
- [Sugerindo Melhorias](#sugerindo-melhorias)

---

## Pré-requisitos

| Ferramenta | Versão mínima |
| ---------- | ------------- |
| Java (JDK) | 21 |
| Maven | 3.9+ |
| Docker | 24+ |
| Docker Compose | 2.x |
| Git | qualquer |

> **Dica:** Se preferir usar apenas Docker (sem instalar Java/Maven localmente), pule direto para a seção [Rodando com Docker](#rodando-com-docker).

---

## Configuração do Ambiente Local

### 1. Faça um fork e clone o repositório

```bash
git clone https://github.com/<seu-usuario>/gestao-sei-backend.git
cd gestao-sei-backend
```

### 2. Suba o banco de dados com Docker

O projeto usa PostgreSQL 16. Para subir apenas o banco:

```bash
docker-compose up db -d
```

O banco ficará disponível em `localhost:5433` com as configurações:

| Parâmetro | Valor |
| --------- | ----- |
| Database | `gestao_sei_db` |
| Usuário | `postgres` |
| Senha | `postgres` |

### 3. Rode a aplicação localmente

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8081`.  
A documentação Swagger em `http://localhost:8081/swagger-ui.html`.

### Rodando com Docker

Para subir tudo (banco + aplicação) via Docker:

```bash
docker-compose up --build -d
```

---

## Rodando os Testes

```bash
./mvnw test
```

O projeto possui **22 testes unitários**. Todos devem passar antes de abrir um PR.

Para verificar a cobertura:

```bash
./mvnw verify
```

---

## Padrões de Código

- **Java 21** com recursos modernos (records, text blocks, sealed classes quando aplicável).
- **Spring Boot 3.x** — siga os padrões de injeção via construtor (evite `@Autowired` em campos quando possível).
- **Lombok** está disponível — use `@Data`, `@Builder`, etc. conforme o contexto.
- **Validação** via anotações Jakarta (`@NotBlank`, `@NotNull`, `@Valid`).
- **Nomes em português** para entidades e campos de domínio (padrão já estabelecido no projeto).
- Mantenha os métodos curtos e com responsabilidade única.
- Novos endpoints devem ter anotações `@Operation` e `@ApiResponses` do Swagger.
- Novas regras de negócio devem ter testes unitários correspondentes.

---

## Padrões de Commit

Usamos o padrão [Conventional Commits](https://www.conventionalcommits.org/pt-br/):

```text
<tipo>: <descrição curta em português>
```

| Tipo | Quando usar |
| ---- | ----------- |
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `refactor` | Refatoração sem mudança de comportamento |
| `test` | Adição ou correção de testes |
| `docs` | Documentação |
| `chore` | Tarefas de manutenção (deps, config) |
| `style` | Formatação, sem impacto em lógica |

**Exemplos:**

```text
feat: adicionar filtro por unidade no endpoint de processos
fix: corrigir validação de prazo nulo ao criar processo
test: adicionar testes para exclusão de usuário com histórico
docs: atualizar README com endpoint de relatório de usuários
```

---

## Como Enviar um Pull Request

1. **Crie uma branch** a partir de `main` com nome descritivo:

   ```bash
   git checkout -b feat/nome-da-funcionalidade
   # ou
   git checkout -b fix/descricao-do-bug
   ```

2. **Implemente** as alterações seguindo os padrões acima.

3. **Rode os testes** e certifique-se de que todos passam:

   ```bash
   ./mvnw test
   ```

4. **Faça commit** seguindo o padrão Conventional Commits.

5. **Abra o PR** para a branch `main` do repositório original com:
   - Título claro descrevendo a mudança
   - Descrição do que foi alterado e por quê
   - Referência à Issue relacionada (se houver): `Closes #123`

---

## Reportando Bugs

Abra uma [Issue](https://github.com/GestaoSEI/gestao-sei-backend/issues) com:

- **Título**: descrição curta do problema
- **Comportamento esperado** vs. **comportamento atual**
- **Passos para reproduzir**
- **Ambiente**: SO, versão do Java, versão do Docker

---

## Sugerindo Melhorias

Abra uma [Issue](https://github.com/GestaoSEI/gestao-sei-backend/issues) com o label `enhancement` descrevendo:

- O problema que a melhoria resolve
- A solução proposta
- Alternativas consideradas

---

Made with ❤️ by Gilvaneide Medeiros
