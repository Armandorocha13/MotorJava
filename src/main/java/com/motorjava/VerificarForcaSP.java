package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import java.sql.*;

public class VerificarForcaSP {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("🔍 VERIFICANDO ESTRUTURA DA TABELA FORCA_SP");
            ResultSet rs = stmt.executeQuery("DESCRIBE forca_sp");
            while (rs.next()) {
                System.out.println("Coluna: " + rs.getString("Field") + " | Tipo: " + rs.getString("Type"));
            }
            
            // Tenta ver um registro de exemplo para confirmar nomes
            System.out.println("\n🔍 EXEMPLO DE DADOS (Limit 1):");
            rs = stmt.executeQuery("SELECT * FROM forca_sp LIMIT 1");
            if (rs.next()) {
                 ResultSetMetaData md = rs.getMetaData();
                 for (int i = 1; i <= md.getColumnCount(); i++) {
                     System.out.println(md.getColumnName(i) + ": " + rs.getString(i));
                 }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
