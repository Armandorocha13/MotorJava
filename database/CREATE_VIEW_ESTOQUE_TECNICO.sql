-- ========================================
-- VIEW: Estoque Técnico - Resumo para Análise
-- Sistema: VIVO Aging - Equipamentos Serializados
-- ========================================
-- Esta view consolida informações principais sobre equipamentos
-- dos técnicos para análise de aging e gestão de estoque
-- ========================================

CREATE OR REPLACE VIEW vw_estoque_tecnico_resumo AS
SELECT 
    -- Identificação do Técnico
    nome_do_tecnico AS tecnico,
    id_tecnico,
    
    -- Hierarquia
    supervisor,
    coodernador AS coordenador,
    gerente,
    
    -- Localização
    uf_do_tecnico AS uf,
    centro_fisico_tecnico AS centro_fisico,
    
    -- Equipamento
    numero_de_serie AS serial,
    sku,
    descricao_sku AS descricao,
    descricao_estado AS estado,
    
    -- Análise de Aging
    dias AS dias_estoque,
    status_aging,
    
    -- Status
    status AS status_tecnico,
    
    -- Informações Adicionais
    quantidade,
    ultima_modificacao,
    Fonte_de_dados AS origem
    
FROM estock_tecnico

-- Ordenação padrão: por dias em estoque (decrescente) e técnico
ORDER BY dias DESC, nome_do_tecnico;

-- ========================================
-- VERIFICAÇÃO DA VIEW
-- ========================================

SELECT '✅ View criada com sucesso!' AS status;

-- Resumo geral
SELECT 
    COUNT(*) AS total_equipamentos,
    COUNT(DISTINCT tecnico) AS total_tecnicos,
    COUNT(DISTINCT supervisor) AS total_supervisores,
    COUNT(DISTINCT coordenador) AS total_coordenadores
FROM vw_estoque_tecnico_resumo;

-- Resumo por Status Aging
SELECT 
    status_aging,
    COUNT(*) AS quantidade,
    COUNT(DISTINCT tecnico) AS tecnicos_afetados
FROM vw_estoque_tecnico_resumo
GROUP BY status_aging
ORDER BY quantidade DESC;

-- Top 10 técnicos com mais equipamentos em aging
SELECT 
    tecnico,
    supervisor,
    COUNT(*) AS total_equipamentos,
    AVG(dias_estoque) AS media_dias,
    MAX(dias_estoque) AS max_dias
FROM vw_estoque_tecnico_resumo
WHERE dias_estoque > 7
GROUP BY tecnico, supervisor
ORDER BY total_equipamentos DESC
LIMIT 10;

SELECT '========================================' AS '';
SELECT '✅ VIEW PRONTA PARA USO!' AS 'STATUS';
SELECT 'Use: SELECT * FROM vw_estoque_tecnico_resumo' AS 'CONSULTA';
SELECT '========================================' AS '';
