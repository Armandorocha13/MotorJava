package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Statement;

public class CriarViewFinal {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("🏗️ Criando VIEW Final Unificada (Dados + Força SP)...");

            // Remove se existir
            stmt.execute("DROP VIEW IF EXISTS vw_estock_tecnico_completa");

            String sql = "CREATE VIEW vw_estock_tecnico_completa AS " +
                "SELECT " +
                "e.Fonte_de_dados, " +
                "e.id_tecnico, " + // Mantém o ID original (com letras)
                "e.nome_do_tecnico, " +
                "e.centro_fisico_tecnico, " +
                "e.origem_centro_materiais, " +
                "e.origem_pool_centro_materiais, " +
                "e.sku, " +
                "e.descricao_sku, " +
                "e.descricao_estado, " +
                "e.numero_de_serie, " +
                "e.quantidade, " +
                "e.id_grupo, " +
                "e.descricao_grupo, " +
                "e.ultima_modificacao, " +
                "e.companhia, " +
                "e.tecnologia_no_validados, " +
                "e.origem_centro_fisico, " +
                "e.motivo_de_indisponibilidade, " +
                "e.wo, " +
                "e.uf_do_tecnico, " +
                "e.trasferencia, " +
                "e.devolucoes, " +
                "e.id_regra, " +
                "e.retirada_danificada, " +
                "e.estado_blockchain, " +
                
                // Mapeamento Inteligente: Se existir na tabela técnica usa, senão pega da Força SP
                "COALESCE(NULLIF(e.coodernador, ''), f.coordenador) AS coodernador, " +
                "COALESCE(NULLIF(e.supervisor, ''), f.supervisor) AS supervisor, " +
                "COALESCE(NULLIF(e.status, ''), f.status) AS status, " +
                
                "e.dias, " +
                "COALESCE(NULLIF(e.gerente, ''), 'N/A') AS gerente, " + // Gerente não tem na Força SP, definindo N/A
                "e.status_aging, " +
                "e.expurgO " +
                
                "FROM estock_tecnico e " +
                "LEFT JOIN forca_sp f ON " +
                // Lógica de JOIN: Remove não-numéricos de ambos os lados e compara
                // Isso garante que 'A0100765' bata com '0100765' ou '100765'
                "REGEXP_REPLACE(e.id_tecnico, '[^0-9]', '') = REGEXP_REPLACE(f.sap, '[^0-9]', '')";

            stmt.execute(sql);
            System.out.println("✅ VIEW 'vw_estock_tecnico_completa' criada com sucesso!");
            System.out.println("ℹ️ Consulte esta view para ver: ID, Nome, e Supervisores/Coordenadores preenchidos.");

        } catch (Exception e) {
            System.err.println("❌ Erro ao criar View: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
