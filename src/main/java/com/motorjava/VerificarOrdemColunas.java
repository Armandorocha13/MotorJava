package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import java.sql.*;

public class VerificarOrdemColunas {
    public static void main(String[] args) {
        String sql = "SELECT * FROM estoque_vivo_historico LIMIT 0";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData meta = rs.getMetaData();
            int totalColunas = meta.getColumnCount();
            
            System.out.println("=== ORDEM DAS COLUNAS NA TABELA ===\n");
            System.out.println("Total de colunas: " + totalColunas + "\n");
            
            for (int i = 1; i <= totalColunas; i++) {
                String nome = meta.getColumnName(i);
                String tipo = meta.getColumnTypeName(i);
                int tamanho = meta.getColumnDisplaySize(i);
                
                System.out.printf("%2d. %-35s | %-15s | Size: %d\n", i, nome, tipo, tamanho);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
