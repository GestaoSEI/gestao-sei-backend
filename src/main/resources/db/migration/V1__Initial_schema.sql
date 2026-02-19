CREATE TABLE IF NOT EXISTS processos (
    id BIGSERIAL PRIMARY KEY,
    numero_processo VARCHAR(50) NOT NULL UNIQUE,
    tipo_processo VARCHAR(100) NOT NULL,
    origem VARCHAR(100) NOT NULL,
    unidade_atual VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_prazo_final DATE
);