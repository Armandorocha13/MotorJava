-- Tabela Intermediária (Dados Brutos do Arquivo)
-- 25 Colunas EXATAS do arquivo.

DROP TABLE IF EXISTS estock_tecnico;

CREATE TABLE estock_tecnico (
    nome_da_origem VARCHAR(255),
    id_do_tecnico VARCHAR(255),
    nome_do_tecnico VARCHAR(255),
    centro_fisico_do_tecnico VARCHAR(255),
    origem_centro_materiais VARCHAR(255),
    origem_pool_centro_materiais VARCHAR(255),
    sku VARCHAR(255),
    descricao_sku TEXT,
    descricao_estado VARCHAR(255),
    numero_de_serie VARCHAR(255),
    quantidade VARCHAR(255),
    id_grupo VARCHAR(255),
    descricao_grupo VARCHAR(255),
    ultima_modificacao VARCHAR(255),
    companhia VARCHAR(255),
    status_do_tecnico VARCHAR(255),
    tecnologia_no_validados TEXT,
    origem_fisico_centro VARCHAR(255),
    motivo_de_indisponibilidade TEXT,
    wo VARCHAR(255),
    uf_do_tecnico VARCHAR(50),
    transferencia VARCHAR(255),
    devolucoes VARCHAR(255),
    id_regra VARCHAR(255),
    retirada_danificada VARCHAR(255),
    estado_blockchain VARCHAR(255)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
