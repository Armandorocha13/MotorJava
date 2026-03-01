package com.motorjava.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * CONFIGURAÇÃO DE BANCO DE DADOS
 * ---------------------------------------------------------
 * Classe utilitária para gerenciar a conexão com o PostgreSQL (Supabase).
 * Ela lê as credenciais (url, usuário, senha) de um arquivo externo
 * chamado 'database.properties', para não deixar senhas fixas no código.
 */
public class DatabaseConfig {

    private static final Properties properties = new Properties();

    // Bloco estático: Roda uma vez assim que a classe é carregada na memória.
    // Tenta ler o arquivo 'database.properties' da pasta src/main/resources.
    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.err.println("❌ ERRO CRÍTICO: Arquivo 'database.properties' não encontrado na pasta resources!");
            } else {
                properties.load(input); // Carrega as configurações do arquivo
                System.out.println("🔧 Configuração de Banco de Dados carregada com sucesso.");

                // Explicitly loading the driver to avoid "No suitable driver found"
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("❌ Erro ao inicializar driver ou ler configuração: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Métodos para pegar os valores individuais
    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUser() {
        return properties.getProperty("db.user");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }

    /**
     * Cria e retorna uma nova conexão com o banco de dados.
     * Quem chamar este método deve usar try-with-resources para fechar a conexão
     * depois.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }
}
