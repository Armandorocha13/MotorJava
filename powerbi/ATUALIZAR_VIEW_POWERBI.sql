-- ========================================
-- VIEW POWER BI PARA EQUIPAMENTOS SERIALIZADOS
-- ========================================
-- Objetivo: Fornecer dados para os relatórios de gestão de estoque e aging.
-- Base de dados: equipamentos_serializados (Tabela Enriquecida)
-- ========================================

CREATE OR REPLACE VIEW vw_powerbi_equipamentos AS
SELECT 
    -- 1. IDENTIFICAÇÃO DO TÉCNICO E HIERARQUIA
    e.nome_do_tecnico AS "Nome do Técnico",
    e.id_do_tecnico AS "Matrícula",
    e.supervisor AS "Supervisor",
    e.coordenador AS "Coordenador",
    e.gerente AS "Gerente",
    e.uf_do_tecnico AS "UF",
    e.centro_fisico_do_tecnico AS "Centro Físico",

    -- 2. DADOS DO EQUIPAMENTO
    e.sku AS "Código SKU",
    e.descricao_sku AS "Descrição do Material",
    e.numero_de_serie AS "Serial",
    e.quantidade AS "Quantidade",
    e.descricao_estado AS "Estado do Material",
    e.id_grupo AS "ID Grupo",
    e.descricao_grupo AS "Grupo",

    -- 3. GESTÃO DE AGING (TEMPO DE ESTOQUE)
    e.dias AS "Dias em Estoque",
    e.status_aging AS "Status Aging", -- Já calculado no Java (0-7, 7-14, >14, Reversa)
    
    -- 4. INFORMAÇÕES DE REGRAS E QUALIDADE
    e.id_regra AS "ID Regra", -- Para tabela auxiliar de regras
    e.devolucoes AS "Devoluções", -- Pode indicar Reversa
    e.retirada_danificada AS "Retirada Danificada",
    e.expurgo AS "Expurgo",
    
    -- 5. OUTROS DETALHES
    e.ultima_modificacao AS "Data Última Modificação",
    e.status_do_tecnico AS "Status Técnico",
    e.motivo_de_indisponibilidade AS "Motivo Indisponibilidade",
    e.wo AS "WO",
    e.transferencia AS "Transferência"

FROM equipamentos_serializados e;

-- ========================================
-- VERIFICAÇÃO SIMPLES
-- ========================================
SELECT '✅ View vw_powerbi_equipamentos atualizada com sucesso!' AS Status;
SELECT COUNT(*) AS Total_Registros FROM vw_powerbi_equipamentos;
