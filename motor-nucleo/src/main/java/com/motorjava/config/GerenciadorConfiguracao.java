package com.motorjava.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public class GerenciadorConfiguracao {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "../configuracoes/configuracoes.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            // Se não encontrar o arquivo no diretório relativo (rodando via IDE), tenta no root
            try (InputStream input = new FileInputStream("configuracoes/configuracoes.properties")) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Aviso: Não foi possível carregar configuracoes.properties. Usando valores padrão.");
            }
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value != null && value.contains("${user.home}")) {
            value = value.replace("${user.home}", System.getProperty("user.home"));
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null) ? value : defaultValue;
    }
}
