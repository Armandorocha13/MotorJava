-- Tabela para armazenamento do Saldo de Estoque do Aniel (Versão Resiliente)
DROP TABLE IF EXISTS aniel_saldo_estoque;
CREATE TABLE aniel_saldo_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
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
    data_importacao DATETIME DEFAULT CURRENT_TIMESTAMP
);
