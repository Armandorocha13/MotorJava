-- ========================================
-- SETUP COMPLETO PARA POWER BI - VERSÃO FINAL
-- Sistema: VIVO Aging - Equipamentos Serializados
-- ========================================
-- Execute este arquivo UMA VEZ antes de conectar o Power BI
-- ========================================

-- ETAPA 1: CALCULAR DIAS EM ESTOQUE
UPDATE estoque_vivo_historico
SET dias_estoque = DATEDIFF(CURDATE(), data_ultima_modificacao)
WHERE data_ultima_modificacao IS NOT NULL;

SELECT '✅ Etapa 1/5: Dias em estoque calculados' AS status;

-- ETAPA 2: ATUALIZAR SUPERVISOR E COORDENADOR
UPDATE estoque_vivo_historico e
INNER JOIN forca_sp f ON UPPER(TRIM(e.nome_tecnico)) COLLATE utf8mb4_unicode_ci = UPPER(TRIM(f.colaborador)) COLLATE utf8mb4_unicode_ci
SET 
    e.supervisor = f.supervisor,
    e.coordenador = f.coordenador;

SELECT '✅ Etapa 2/5: Supervisores e coordenadores atualizados' AS status;

-- ETAPA 3: CALCULAR STATUS AGING
UPDATE estoque_vivo_historico
SET status_aging = CASE 
    WHEN dias_estoque IS NULL THEN 'Não Classificado'
    WHEN dias_estoque BETWEEN 0 AND 7 THEN 'Normal (0-7 dias)'
    WHEN dias_estoque BETWEEN 8 AND 14 THEN 'Atenção (7-14 dias)'
    WHEN dias_estoque > 14 THEN 'Aging Crítico (>14 dias)'
    ELSE 'Não Classificado'
END;

SELECT '✅ Etapa 3/5: Status aging calculado' AS status;

-- ETAPA 4: CRIAR ÍNDICES
CREATE INDEX IF NOT EXISTS idx_nome_tecnico ON estoque_vivo_historico(nome_tecnico);
CREATE INDEX IF NOT EXISTS idx_data_snapshot ON estoque_vivo_historico(data_snapshot);
CREATE INDEX IF NOT EXISTS idx_status_aging ON estoque_vivo_historico(status_aging);
CREATE INDEX IF NOT EXISTS idx_dias_estoque ON estoque_vivo_historico(dias_estoque);
CREATE INDEX IF NOT EXISTS idx_numero_serie ON estoque_vivo_historico(numero_serie);
CREATE INDEX IF NOT EXISTS idx_supervisor ON estoque_vivo_historico(supervisor);
CREATE INDEX IF NOT EXISTS idx_colaborador ON forca_sp(colaborador);

SELECT '✅ Etapa 4/5: Índices criados' AS status;

-- ETAPA 5: CRIAR VIEW PARA POWER BI (APENAS TÉCNICOS CADASTRADOS)
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
        WHEN e.dias_estoque BETWEEN 0 AND 7 THEN '0-7 Dias'
        WHEN e.dias_estoque BETWEEN 8 AND 14 THEN '7-14 Dias'
        WHEN e.dias_estoque > 14 THEN 'Acima 14 Dias'
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

SELECT '✅ Etapa 5/5: View criada' AS status;

-- VERIFICAÇÃO FINAL
SELECT '========================================' AS '';
SELECT 'RESUMO DOS DADOS' AS '';
SELECT '========================================' AS '';

SELECT 
    COUNT(*) AS 'Total Equipamentos',
    COUNT(DISTINCT nome_tecnico) AS 'Tecnicos',
    COUNT(DISTINCT supervisor) AS 'Supervisores',
    COUNT(DISTINCT coordenador) AS 'Coordenadores'
FROM vw_powerbi_equipamentos;

SELECT '========================================' AS '';

SELECT 
    supervisor,
    COUNT(*) AS 'Total Equipamentos',
    COUNT(CASE WHEN dias_estoque > 14 THEN 1 END) AS 'Criticos'
FROM vw_powerbi_equipamentos
GROUP BY supervisor
ORDER BY COUNT(*) DESC;

SELECT '========================================' AS '';
SELECT '✅ BANCO PRONTO PARA POWER BI!' AS 'STATUS';
SELECT 'Use a query: SELECT * FROM vw_powerbi_equipamentos' AS 'PRÓXIMO PASSO';
SELECT '========================================' AS '';
