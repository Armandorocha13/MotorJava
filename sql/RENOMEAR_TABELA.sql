-- ========================================
-- RENOMEAR TABELA: estoque_vivo_historico -> stock_tecnico
-- Sistema: VIVO Aging - Equipamentos Serializados
-- ========================================

-- Verificar se a tabela antiga existe e renomeá-la
RENAME TABLE estoque_vivo_historico TO stock_tecnico;

-- Verificar a estrutura
DESCRIBE stock_tecnico;

SELECT '✅ Tabela renomeada com sucesso!' AS status;
