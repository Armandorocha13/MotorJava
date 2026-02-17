package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Statement;

public class CorrigirBanco {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("🔧 Padronizando Collation do Banco de Dados...");

            // Padroniza Stage para utf8mb4_general_ci
            stmt.execute("ALTER TABLE stage_stock_tecnico CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            System.out.println("✅ Tabela 'stage_stock_tecnico' convertida para utf8mb4_general_ci.");

            // Padroniza Tabela Final
            stmt.execute("ALTER TABLE estock_tecnico CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            System.out.println("✅ Tabela 'estock_tecnico' convertida para utf8mb4_general_ci.");

            // Padroniza Tabela Força SP (se possível)
            try {
                stmt.execute("ALTER TABLE forca_sp CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
                System.out.println("✅ Tabela 'forca_sp' convertida para utf8mb4_general_ci.");
            } catch (Exception e) {
                System.out.println("⚠️ Não foi possível alterar 'forca_sp' (pode estar trancada ou sem permissão), mas a stage já foi ajustada.");
            }

            System.out.println("\n🚀 Banco corrigido! O erro de Collation deve desaparecer.");

        } catch (Exception e) {
            System.err.println("❌ Erro ao corrigir banco: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
