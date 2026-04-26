package com.motorjava.config;

public class Config {
    // Caminhos Base
    public static final String BASE_PBI = "C:\\Users\\user\\Documents\\PBI\\BASE DADOS";

    // Power Automate Webhook URL
    public static final String POWER_AUTOMATE_WEBHOOK_URL = "https://prod-XX.brazilsouth.logic.azure.com:443/workflows/...";

    public static final String PATH_DOWNLOADS = System.getProperty("user.home") + "\\Downloads";

    // Endpoints
    public static final String ENDPOINT_MOVIMENTACOES = "/api/AplicacoesRemocoesApi/GetAll";

    // Email / Outlook IMAP (Configurações) - Ignorar por enquanto
    public static final String MAIL_HOST = "outlook.office365.com";
    public static final String MAIL_PORT = "993";
    public static final String MAIL_USER = "seu_email@outlook.com"; // Deve ser configurado
    public static final String MAIL_PASS = "sua_senha_ou_app_password"; // Recomenda-se usar variável de ambiente ou
                                                                        // prop separada
}
