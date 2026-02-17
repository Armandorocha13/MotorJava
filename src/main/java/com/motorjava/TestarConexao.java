package com.motorjava;

import com.motorjava.config.DatabaseConfig;
import java.sql.Connection;

public class TestarConexao {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("✅ Conexão com o banco de dados OK!");
        } catch (Exception e) {
            System.err.println("❌ Erro de conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
