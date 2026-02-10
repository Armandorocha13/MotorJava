package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import java.sql.*;

public class ListaColunas {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("DESCRIBE estoque_vivo_historico")) {
            
            System.out.println("--- COLUNAS DA TABELA ---");
            while (rs.next()) {
                System.out.println(rs.getString("Field") + " (" + rs.getString("Type") + ")");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
