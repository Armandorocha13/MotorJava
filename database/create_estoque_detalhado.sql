-- Tabela para armazenamento detalhado de saldo e informações de materiais
CREATE TABLE IF NOT EXISTS estoque_detalhado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    part_number VARCHAR(100),
    codigo VARCHAR(100),
    nome VARCHAR(255),
    unidade VARCHAR(50),
    quantidade DECIMAL(15,2),
    peso_total DECIMAL(15,2),
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
    data_importacao DATETIME DEFAULT CURRENT_TIMESTAMP
);
