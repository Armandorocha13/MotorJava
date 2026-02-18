-- ========================================
-- ATUALIZAR VIEW POWER BI - SEM ACENTUAÇÃO
-- ========================================
-- Execute este script para recriar a view com nomes padronizados
-- ========================================

CREATE OR REPLACE VIEW vw_powerbi_equipamentos AS
SELECT 
    e.numero_serie AS numero_serie,
    e.nome_tecnico AS nome_tecnico,
    f.supervisor AS supervisor,
    f.coordenador AS coordenador,
    f.coordenador AS gerente,
    e.sku AS sku,
    e.descricao_sku AS descricao,
    e.status_tecnico AS status_tecnico,
    e.dias_estoque AS dias_estoque,
    e.status_aging AS status_aging,
    f.funcao AS funcao,
    f.sexo AS sexo,
    f.status AS status_colaborador,
    f.contato AS contato,
    f.sap AS sap,
    CASE 
        WHEN e.dias_estoque BETWEEN 0 AND 7 THEN '0 a 7 dias'
        WHEN e.dias_estoque BETWEEN 8 AND 14 THEN '7 a 14 dias'
        WHEN e.dias_estoque > 14 THEN 'Acima de 14 dias'
        ELSE 'Nao Classificado'
    END AS faixa_aging,
    CASE 
        WHEN e.dias_estoque > 21 THEN 'Alto Risco'
        WHEN e.dias_estoque > 14 THEN 'Medio Risco'
        WHEN e.dias_estoque > 7 THEN 'Baixo Risco'
        ELSE 'Normal'
    END AS categoria_risco,
    e.data_ultima_modificacao AS data_ultima_modificacao,
    e.data_snapshot AS data_snapshot,
    e.uf AS uf,
    e.centro_fisico AS centro_fisico
FROM estoque_vivo_historico e
INNER JOIN forca_sp f ON UPPER(TRIM(e.nome_tecnico)) COLLATE utf8mb4_unicode_ci = UPPER(TRIM(f.colaborador)) COLLATE utf8mb4_unicode_ci
WHERE e.data_snapshot = (SELECT MAX(data_snapshot) FROM estoque_vivo_historico);

-- VERIFICAÇÃO
SELECT '✅ View atualizada com sucesso!' AS status;

SELECT 
    COUNT(*) AS total_equipamentos,
    COUNT(DISTINCT nome_tecnico) AS total_tecnicos,
    COUNT(DISTINCT supervisor) AS total_supervisores
FROM vw_powerbi_equipamentos;
