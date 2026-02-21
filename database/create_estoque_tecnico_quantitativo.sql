-- Tabela para armazenamento do estoque técnico quantitativo
CREATE TABLE IF NOT EXISTS estoque_tecnico_quantitativo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    empresa VARCHAR(150),
    nome_fantasia_empresa VARCHAR(150),
    tecnico VARCHAR(150),
    regional VARCHAR(100),
    grupo_produto VARCHAR(150),
    part_number VARCHAR(100),
    codigo VARCHAR(100),
    nome VARCHAR(255),
    unidade VARCHAR(50),
    quantidade DECIMAL(15,2),
    tipo VARCHAR(100),
    data_importacao DATETIME DEFAULT CURRENT_TIMESTAMP
);
