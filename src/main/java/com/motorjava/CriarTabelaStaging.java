package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Statement;

public class CriarTabelaStaging {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("🏗️ Criando tabela de Stage (Área de Preparação)...");

            // Drop se existir para garantir estrutura limpa
            stmt.execute("DROP TABLE IF EXISTS stage_stock_tecnico");

            // Cria tabela com campos TEXT para aceitar qualquer entrada
            // Usando nomes genéricos ou do Excel para facilitar a importação bruta
            String sql = "CREATE TABLE stage_stock_tecnico (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome_origem TEXT, " +             // Col 1
                "id_tecnico TEXT, " +              // Col 2
                "nome_tecnico TEXT, " +            // Col 3
                "centro_fisico TEXT, " +           // Col 4
                "origem_centro_mat TEXT, " +       // Col 5
                "origem_pool TEXT, " +             // Col 6
                "sku TEXT, " +                     // Col 7
                "descricao_sku TEXT, " +           // Col 8
                "descricao_estado TEXT, " +        // Col 9
                "numero_serie TEXT, " +            // Col 10
                "quantidade TEXT, " +              // Col 11
                "id_grupo TEXT, " +                // Col 12
                "descricao_grupo TEXT, " +         // Col 13
                "ultima_modificacao TEXT, " +      // Col 14
                "companhia TEXT, " +               // Col 15
                "status_tecnico TEXT, " +          // Col 16
                "tecnologia_no_validados TEXT, " + // Col 17
                "origem_fisico_centro TEXT, " +    // Col 18
                "motivo_indisponibilidade TEXT, " +// Col 19
                "wo TEXT, " +                      // Col 20
                "uf_tecnico TEXT, " +              // Col 21
                "transferencia TEXT, " +           // Col 22
                "devolucoes TEXT, " +              // Col 23
                "id_regra TEXT, " +                // Col 24
                "retirada_danificada TEXT, " +     // Col 25
                "estado_blockchain TEXT, " +       // Col 26
                "coordenador TEXT, " +             // Col 27
                "supervisor TEXT, " +              // Col 28
                "status TEXT, " +                  // Col 29
                "dias TEXT, " +                    // Col 30
                "gerente TEXT, " +                 // Col 31
                "status_aging TEXT, " +            // Col 32
                "data_importacao DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";

            stmt.execute(sql);
            System.out.println("✅ Tabela 'stage_stock_tecnico' criada com sucesso!");
            System.out.println("ℹ️ Esta tabela aceita qualquer dado sem validação de tipo.");

        } catch (Exception e) {
            System.err.println("❌ Erro ao criar tabela stage: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
