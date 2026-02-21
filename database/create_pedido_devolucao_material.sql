-- Tabela para armazenamento de pedidos de devolução de material
CREATE TABLE IF NOT EXISTS pedido_devolucao_material (
    id INT AUTO_INCREMENT PRIMARY KEY,
    documento VARCHAR(100),
    data_documento VARCHAR(50), -- Mantendo como VARCHAR para flexibilidade na importação
    status VARCHAR(100),
    responsavel VARCHAR(150),
    tecnico VARCHAR(150),
    part_number VARCHAR(100),
    codigo VARCHAR(100),
    nome VARCHAR(255),
    fabricante VARCHAR(150),
    quantidade DECIMAL(15,2),
    bem_patrimonial VARCHAR(100),
    serial_number VARCHAR(100),
    estado VARCHAR(100),
    cidade VARCHAR(100),
    almoxarifado VARCHAR(150),
    deposito VARCHAR(150),
    empresa_gestora VARCHAR(150),
    observacoes TEXT,
    data_importacao DATETIME DEFAULT CURRENT_TIMESTAMP
);
