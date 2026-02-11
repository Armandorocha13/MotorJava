package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import java.sql.*;

public class VerificarSchema {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, "vivo_aging", "estoque_vivo_historico", null);
            
            System.out.println("=== ESTRUTURA DA TABELA estoque_vivo_historico ===\n");
            int colIndex = 1;
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                String colType = rs.getString("TYPE_NAME");
                int colSize = rs.getInt("COLUMN_SIZE");
                String nullable = rs.getString("IS_NULLABLE");
                
                System.out.printf("%2d. %-35s | %-15s | Size: %-5d | Null: %s\n", 
                    colIndex++, colName, colType, colSize, nullable);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
