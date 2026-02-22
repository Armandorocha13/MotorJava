-- Tabela para armazenamento da Conferencia de Material do Aniel
DROP TABLE IF EXISTS aniel_conferencia_material;
CREATE TABLE aniel_conferencia_material (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contrato VARCHAR(255),
    projeto VARCHAR(255),
    codmat VARCHAR(255), -- Corrigido para bater com normalização
    descricao_material VARCHAR(255),
    codcompl VARCHAR(255), -- Corrigido para bater com normalização
    cod_fornec VARCHAR(255),
    fornecedor VARCHAR(255),
    equipe VARCHAR(255),
    nome VARCHAR(255),
    n_doc VARCHAR(255), -- Corrigido para bater com normalização
    observacao VARCHAR(255),
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
    data_importacao DATETIME DEFAULT CURRENT_TIMESTAMP
);
