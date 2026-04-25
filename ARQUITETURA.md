# 🏗️ Arquitetura e Design de Software - Gestão SEI

Este documento detalha as decisões arquiteturais, o modelo de dados e o fluxo de funcionamento do sistema Gestão SEI.

## 1. Visão Geral da Arquitetura

O sistema segue o padrão de arquitetura em camadas do **Spring Boot**, garantindo a separação de responsabilidades e facilitando a manutenção e testes.

- **Controller**: Exposição dos endpoints REST e tratamento de requisições.
- **Service**: Concentração das regras de negócio (validações, cálculos de prazo, agendamentos).
- **Repository**: Interface de comunicação com o banco de dados PostgreSQL via Spring Data JPA.
- **Model/Entity**: Representação das tabelas do banco de dados e seus relacionamentos.
- **DTO (Data Transfer Object)**: Segurança na trafegação de dados, evitando a exposição direta das entidades.

## 2. Diagramas UML

### A. Diagrama de Classe (Modelo de Dados)
O diagrama abaixo representa a estrutura das entidades e como elas se relacionam para manter a integridade do histórico.

```mermaid
classDiagram
    class Usuario {
        +Long id
        +String login
        +String senha
        +String nomeCompleto
        +String email
        +LocalDate dataNascimento
        +Role role
    }

    class Processo {
        +Long id
        +String numero
        +String tipo
        +String origem
        +String unidadeAtual
        +StatusProcesso status
        +LocalDate prazo
        +String observacao
    }

    class HistoricoProcesso {
        +Long id
        +LocalDateTime dataHora
        +String descricao
        +String unidadeAnterior
        +String unidadeNova
    }

    Usuario "1" -- "*" HistoricoProcesso : registra
    Processo "1" -- "*" HistoricoProcesso : possui
```

### B. Ciclo de Vida do Processo (Estados)
O sistema gerencia automaticamente os estados dos processos com base na interação do usuário e no passar do tempo.

```mermaid
stateDiagram-v2
    [*] --> ABERTO: Cadastro Inicial
    
    ABERTO --> EM_TRAMITACAO: Alterar Unidade/Status
    EM_TRAMITACAO --> EM_TRAMITACAO: Novas Movimentações
    
    ABERTO --> EXPIRADO: Prazo Vencido (Agendamento)
    EM_TRAMITACAO --> EXPIRADO: Prazo Vencido (Agendamento)
    
    EXPIRADO --> EM_TRAMITACAO: Atualização após Vencimento
    
    EM_TRAMITACAO --> CONCLUIDO: Finalizar Processo
    CONCLUIDO --> ABERTO: Reabrir Processo
    
    CONCLUIDO --> [*]
```

### C. Fluxo de Agendamento Automático
O sistema possui um serviço de agendamento (`@Scheduled`) que executa diariamente à meia-noite para garantir a atualização dos prazos.

```mermaid
sequenceDiagram
    participant S as AgendamentoService
    participant R as ProcessoRepository
    participant H as HistoricoRepository
    participant DB as PostgreSQL

    Note over S: Executado diariamente (00:00)
    S->>R: buscarProcessosVencidos(dataAtual)
    R->>DB: SELECT onde prazo < hoje E status != 'CONCLUIDO'
    DB-->>R: Lista de processos
    R-->>S: List<Processo>
    
    loop Para cada processo
        S->>S: Altera status para 'EXPIRADO'
        S->>H: Registra histórico automático
        S->>R: save(processo)
        R->>DB: UPDATE
    end
```

## 3. Regras de Negócio (RN)

| ID | Regra de Negócio | Descrição |
|:---:|:--- |:--- |
| **RN01** | **Integridade de Usuário** | Não é permitida a exclusão de usuários que possuam registros vinculados no histórico de processos. |
| **RN02** | **Auditoria Obrigatória** | Toda alteração de 'Unidade Atual' ou 'Status' deve gerar automaticamente um registro no Histórico com o usuário logado. |
| **RN03** | **Alerta de Urgência** | Processos com prazo <= 5 dias são sinalizados como urgentes no sistema. |
| **RN04** | **Segurança Administrativa** | Apenas perfis `ADMIN` podem gerenciar usuários e redefinir senhas de terceiros. |
| **RN05** | **Troca de Senha Própria** | Usuários `USER` devem informar a senha atual para definir uma nova. |

## 4. Tecnologias Utilizadas

- **Java 21**: Uso de Records e novas APIs de data/hora.
- **Spring Boot 3.4**: Segurança com JWT e persistência com JPA.
- **JasperReports**: Motor de geração de relatórios complexos em PDF.
- **Docker**: Conteinerização para padronização de ambientes de desenvolvimento e produção.
