package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import java.sql.*;

public class DiagnosticoStage {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("🔍 DIAGNÓSTICO DA TABELA STAGE\n");
            
            // Verifica as colunas da stage em busca de dados
            // Vamos pegar uma linha onde col02 (ID) não seja nulo para ver o resto
            ResultSet rs = stmt.executeQuery("SELECT * FROM stage_stock_tecnico WHERE col02 IS NOT NULL LIMIT 3");
            
            while (rs.next()) {
                System.out.println("--- REGISTRO STAGE DETALHADO ---");
                for (int i = 1; i <= 32; i++) {
                    String colName = String.format("col%02d", i);
                    String valor = rs.getString(colName);
                    System.out.println(colName + ": " + (valor == null ? "NULL" : valor));
                }
                System.out.println("-------------------------------------------------");
            }
            
            // Verifica se alguma coluna "alta" tem dados
            rs = stmt.executeQuery("SELECT " +
                "COUNT(col26) as c26, COUNT(col27) as c27, " +
                "COUNT(col28) as c28, COUNT(col29) as c29, " +
                "COUNT(col30) as c30, COUNT(col31) as c31, COUNT(col32) as c32 " +
                "FROM stage_stock_tecnico");
            
            if (rs.next()) {
                System.out.println("\n📊 CONTAGEM DE DADOS NA STAGE:");
                System.out.println("Col 26: " + rs.getInt("c26"));
                System.out.println("Col 27: " + rs.getInt("c27"));
                System.out.println("Col 28: " + rs.getInt("c28"));
                System.out.println("Col 29: " + rs.getInt("c29"));
                System.out.println("Col 30: " + rs.getInt("c30"));
                System.out.println("Col 31: " + rs.getInt("c31"));
                System.out.println("Col 32: " + rs.getInt("c32"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
