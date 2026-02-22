-- ============================================================
-- SUPABASE SETUP (POSTGRESQL) - MOTOR JAVA
-- ============================================================

-- 1. Tabelas do Aniel
DROP TABLE IF EXISTS aniel_movimentacoes CASCADE;
CREATE TABLE aniel_movimentacoes (
    id SERIAL PRIMARY KEY,
    contrato VARCHAR(255),
    projeto VARCHAR(255),
    equipe VARCHAR(255),
    numero_obra VARCHAR(255),
    rdo VARCHAR(255),
    contrato_assinante VARCHAR(255),
    numero_os VARCHAR(255),
    data_agendamento VARCHAR(100),
    data_aplicacao VARCHAR(100),
    cod_material VARCHAR(255),
    cod_cpl VARCHAR(255),
    cod_cpl_aux VARCHAR(255),
    quantidade_aplic INTEGER,
    quantidade_removida INTEGER,
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS aniel_saldo_estoque CASCADE;
CREATE TABLE aniel_saldo_estoque (
    id SERIAL PRIMARY KEY,
    tipo_saldo VARCHAR(255),
    grupo VARCHAR(255),
    codmat VARCHAR(255),
    descricao VARCHAR(255),
    descricao_auxiliar VARCHAR(255),
    unid VARCHAR(255),
    codcpl VARCHAR(255),
    cod_mat_auxiliar VARCHAR(255),
    cod_cpl_auxiliar VARCHAR(255),
    saldo_em_estoque VARCHAR(255),
    prog_rm VARCHAR(255),
    prog_tm VARCHAR(255),
    saldo_disponivel VARCHAR(255),
    valor VARCHAR(255),
    cod_grupo_material VARCHAR(255),
    grupo_de_material VARCHAR(255),
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS aniel_saldo_volante CASCADE;
CREATE TABLE aniel_saldo_volante (
    id SERIAL PRIMARY KEY,
    contrato VARCHAR(255),
    projeto VARCHAR(255),
    cod_material VARCHAR(255),
    desc_material VARCHAR(255),
    descricao_auxiliar VARCHAR(255),
    cod_cpl_aux VARCHAR(255),
    unidade VARCHAR(255),
    cod_compl VARCHAR(255),
    grupo_de_material VARCHAR(255),
    recebido VARCHAR(255),
    devolucao VARCHAR(255),
    aplicado VARCHAR(255),
    removido VARCHAR(255),
    saldo VARCHAR(255),
    valor_unit VARCHAR(255),
    total_r VARCHAR(255),
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS aniel_conferencia_material CASCADE;
CREATE TABLE aniel_conferencia_material (
    id SERIAL PRIMARY KEY,
    contrato VARCHAR(255),
    projeto VARCHAR(255),
    codmat VARCHAR(255),
    descricao_material VARCHAR(255),
    codcompl VARCHAR(255),
    cod_fornec VARCHAR(255),
    fornecedor VARCHAR(255),
    equipe VARCHAR(255),
    nome VARCHAR(255),
    n_doc VARCHAR(255),
    observacao TEXT,
    entrada VARCHAR(255),
    saida VARCHAR(255),
    tipo_movimento VARCHAR(255),
    data_movimento VARCHAR(255),
    usuario_baixa VARCHAR(255),
    data_baixa VARCHAR(255),
    hora_baixa VARCHAR(255),
    usuario_criacao VARCHAR(255),
    data_criacao VARCHAR(255),
    hora_criacao VARCHAR(255),
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabelas do Vivo Aging
DROP TABLE IF EXISTS stage_stock_tecnico CASCADE;
CREATE TABLE stage_stock_tecnico (
    id SERIAL PRIMARY KEY,
    col01 TEXT, col02 TEXT, col03 TEXT, col04 TEXT, col05 TEXT,
    col06 TEXT, col07 TEXT, col08 TEXT, col09 TEXT, col10 TEXT,
    col11 TEXT, col12 TEXT, col13 TEXT, col14 TEXT, col15 TEXT,
    col16 TEXT, col17 TEXT, col18 TEXT, col19 TEXT, col20 TEXT,
    col21 TEXT, col22 TEXT, col23 TEXT, col24 TEXT, col25 TEXT,
    col26 TEXT, col27 TEXT, col28 TEXT, col29 TEXT, col30 TEXT,
    col31 TEXT, col32 TEXT,
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS estock_tecnico CASCADE;
CREATE TABLE estock_tecnico (
    id SERIAL PRIMARY KEY,
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
    estado_blockchain VARCHAR(255),
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS equipamentos_serializados CASCADE;
CREATE TABLE equipamentos_serializados (
    id SERIAL PRIMARY KEY,
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
    estado_blockchain VARCHAR(255),
    coordenador VARCHAR(255),
    supervisor VARCHAR(255),
    status VARCHAR(255),
    dias VARCHAR(255),
    gerente VARCHAR(255),
    status_aging VARCHAR(255),
    expurgo VARCHAR(255),
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabelas do One Page (Modems)
DROP TABLE IF EXISTS saldo_modem_rj CASCADE;
CREATE TABLE saldo_modem_rj (
    id SERIAL PRIMARY KEY,
    status VARCHAR(255),
    material VARCHAR(255),
    unidade INTEGER,
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS saldo_modem_sp CASCADE;
CREATE TABLE saldo_modem_sp (
    id SERIAL PRIMARY KEY,
    status VARCHAR(255),
    material VARCHAR(255),
    unidade INTEGER,
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Tabelas WMS
DROP TABLE IF EXISTS estoque_detalhado CASCADE;
CREATE TABLE estoque_detalhado (
    id SERIAL PRIMARY KEY,
    part_number VARCHAR(100),
    codigo VARCHAR(100),
    nome VARCHAR(255),
    unidade VARCHAR(50),
    quantidade VARCHAR(255),
    peso_total VARCHAR(255),
    status VARCHAR(100),
    estado VARCHAR(100),
    regional VARCHAR(100),
    cidade VARCHAR(100),
    almoxarifado VARCHAR(150),
    deposito VARCHAR(150),
    empresa_gestora VARCHAR(150),
    fabricante VARCHAR(150),
    familia VARCHAR(150),
    grupo_produto VARCHAR(150),
    giro_de_estoque VARCHAR(100),
    nivel_de_criticidade VARCHAR(100),
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS estoque_tecnico_quantitativo CASCADE;
CREATE TABLE estoque_tecnico_quantitativo (
    id SERIAL PRIMARY KEY,
    empresa VARCHAR(150),
    nome_fantasia_empresa VARCHAR(150),
    tecnico VARCHAR(150),
    regional VARCHAR(100),
    grupo_produto VARCHAR(150),
    part_number VARCHAR(100),
    codigo VARCHAR(100),
    nome VARCHAR(255),
    unidade VARCHAR(50),
    quantidade VARCHAR(255),
    tipo VARCHAR(100),
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS pedido_devolucao_material CASCADE;
CREATE TABLE pedido_devolucao_material (
    id SERIAL PRIMARY KEY,
    documento VARCHAR(100),
    data VARCHAR(50), 
    status VARCHAR(100),
    responsavel VARCHAR(150),
    tecnico VARCHAR(150),
    part_number VARCHAR(100),
    codigo VARCHAR(100),
    nome VARCHAR(255),
    fabricante VARCHAR(150),
    quantidade VARCHAR(255),
    bem_patrimonial VARCHAR(100),
    serial VARCHAR(100),
    estado VARCHAR(100),
    cidade VARCHAR(100),
    almoxarifado VARCHAR(150),
    deposito VARCHAR(150),
    empresa_gestora VARCHAR(150),
    observacoes TEXT,
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Views
CREATE OR REPLACE VIEW vw_estoque_tecnico_resumo AS
SELECT 
    nome_do_tecnico AS tecnico,
    id_do_tecnico AS id_tecnico,
    supervisor,
    coordenador,
    gerente,
    uf_do_tecnico AS uf,
    centro_fisico_do_tecnico AS centro_fisico,
    numero_de_serie AS serial,
    sku,
    descricao_sku AS descricao,
    descricao_estado AS estado,
    dias AS dias_estoque,
    status_aging,
    status AS status_tecnico,
    quantidade,
    ultima_modificacao,
    nome_da_origem AS origem
FROM equipamentos_serializados;
