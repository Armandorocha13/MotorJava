-- ========================================
-- VIEW: Gestão de Aging com Justificativas
-- Sistema: VIVO Aging - Equipamentos Serializados
-- ========================================
-- Esta view inclui campos para gestão de justificativas e prazos
-- ========================================

CREATE OR REPLACE VIEW vw_gestao_aging AS
SELECT 
    -- Identificação
    id_tecnico,
    nome_do_tecnico AS tecnico,
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
    
    -- Aging
    dias AS dias_estoque,
    status_aging,
    ultima_modificacao AS data_ultima_modificacao,
    
    -- Campos para Gestão (inicialmente NULL, podem ser atualizados via tabela auxiliar)
    NULL AS justificativa,
    NULL AS data_prazo,
    NULL AS responsavel_justificativa,
    NULL AS data_justificativa,
    NULL AS status_tratamento,
    
    -- Status do Técnico
    status AS status_tecnico,
    
    -- Informações Adicionais
    quantidade,
    Fonte_de_dados AS origem
    
FROM estock_tecnico

-- Ordenação: Prioriza equipamentos com mais dias em estoque
ORDER BY dias DESC, supervisor, nome_do_tecnico;

-- ========================================
-- VERIFICAÇÃO DA VIEW
-- ========================================

SELECT '✅ View vw_gestao_aging criada com sucesso!' AS status;

-- Resumo por Supervisor
SELECT 
    supervisor,
    COUNT(*) AS total_equipamentos,
    COUNT(DISTINCT tecnico) AS total_tecnicos,
    SUM(CASE WHEN dias_estoque > 14 THEN 1 ELSE 0 END) AS criticos,
    SUM(CASE WHEN dias_estoque BETWEEN 7 AND 14 THEN 1 ELSE 0 END) AS atencao,
    SUM(CASE WHEN dias_estoque < 7 THEN 1 ELSE 0 END) AS normal,
    AVG(dias_estoque) AS media_dias
FROM vw_gestao_aging
GROUP BY supervisor
ORDER BY criticos DESC, total_equipamentos DESC;

SELECT '========================================' AS '';
SELECT '✅ VIEW PRONTA PARA USO!' AS 'STATUS';
SELECT 'Use: SELECT * FROM vw_gestao_aging' AS 'CONSULTA';
SELECT 'Campos adicionais: justificativa, data_prazo, responsavel_justificativa, data_justificativa, status_tratamento' AS 'OBSERVAÇÃO';
SELECT '========================================' AS '';
