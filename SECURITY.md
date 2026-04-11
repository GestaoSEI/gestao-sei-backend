# 🔒 Política de Segurança

## Versões Suportadas

| Versão | Suporte de Segurança |
| ------ | -------------------- |
| `main` | ✅ Ativa |
| demais branches | ❌ Sem suporte |

Este projeto está em desenvolvimento ativo. Apenas a branch principal (`main`) recebe correções de segurança.

## Reportando uma Vulnerabilidade

**Não abra uma Issue pública para reportar vulnerabilidades de segurança.**

Se você encontrou uma vulnerabilidade, por favor reporte de forma privada utilizando o recurso de **[Private Vulnerability Reporting](https://github.com/GestaoSEI/gestao-sei-backend/security/advisories/new)** do próprio GitHub.

### O que incluir no relatório

- Descrição clara da vulnerabilidade
- Passos para reproduzir o problema
- Impacto potencial (o que um atacante poderia fazer)
- Versão/commit onde o problema foi identificado
- Sugestão de correção (opcional, mas bem-vinda)

## O que esperar

- **Confirmação de recebimento:** em até 5 dias úteis
- **Avaliação e resposta:** em até 15 dias úteis
- **Correção e divulgação:** após a correção ser publicada, o relatório será tornado público com seus créditos (se desejar)

## Escopo

Este repositório cobre o **backend** do sistema Gestão SEI (API REST em Java/Spring Boot). Vulnerabilidades relacionadas ao frontend devem ser reportadas em [gestao-sei-frontend](https://github.com/GestaoSEI/gestao-sei-frontend/security/advisories/new).

### Em escopo

- Injeção de SQL / JPQL
- Falhas de autenticação ou autorização JWT
- Exposição de dados sensíveis na API
- Falhas no controle de acesso entre perfis (ADMIN / USER)
- Dependências com CVEs conhecidos

### Fora de escopo

- Credenciais padrão expostas intencionalmente para fins de demonstração (`admin` / `admin123`)
- Vulnerabilidades em ambientes de desenvolvimento local
- Ataques que exijam acesso físico à máquina

## Agradecimento

Contribuições responsáveis de segurança são muito valorizadas. Obrigada por ajudar a tornar este projeto mais seguro para todos os servidores públicos que o utilizam.

---

Made with ❤️ by Gilvaneide Medeiros
