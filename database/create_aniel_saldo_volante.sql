-- Tabela para armazenamento do Saldo Volante do Aniel
DROP TABLE IF EXISTS aniel_saldo_volante;
CREATE TABLE aniel_saldo_volante (
    id INT AUTO_INCREMENT PRIMARY KEY,
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
    total_r VARCHAR(255), -- Corrigido para bater com normalização
    data_importacao DATETIME DEFAULT CURRENT_TIMESTAMP
);
