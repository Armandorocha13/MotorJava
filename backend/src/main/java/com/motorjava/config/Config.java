package com.motorjava.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    // Caminhos Base
    public static final String BASE_PBI = "C:\\Users\\user\\Documents\\PBI\\BASE DADOS";

    // Outlook
    public static final String PATH_OUTLOOK = BASE_PBI + "\\OUTLOOK";

    // Vivo Aging (Mantendo compatibilidade com o que já existe se necessário)
    public static final String PATH_VIVO_DATA = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\VivoAging\\Equipamentos serializados";
    public static final String SCRIPT_VIVO_VBS = "C:\\Users\\user\\Desktop\\MotorJava\\scripts\\atualizar_dados.vbs";
    public static final String PATH_FORCA_SP = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\VivoAging\\forcaSP\\For\u00e7a VIVO SP.xlsx";

    // Power Automate Webhook URL
    public static final String POWER_AUTOMATE_WEBHOOK_URL = "https://prod-XX.brazilsouth.logic.azure.com:443/workflows/...";

    // --- NOVOS CAMINHOS ONE PAGE REPORT ---
    public static final String PATH_ONEPAGE_OUTLOOK = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\OnePageReport\\Outlook";
    public static final String PATH_ONEPAGE_ANIEL = "C:\\Users\\user\\Documents\\PBI\\BASE DADOS\\ANIEL\\QUERY";
    public static final String PATH_ONEPAGE_WMS = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\OnePageReport\\wms";
    public static final String PATH_DOWNLOADS = System.getProperty("user.home") + "\\Downloads";

    // Aniel (Sinapse) API - FFA Specific (Mantendo para quando o suporte liberar)
    public static final String ANIEL_API_BASE_URL = "https://anielsinapse.sinapseinformatica.com.br/FFA/Servicos/API_Aniel";
    public static final String ANIEL_USER = "FFA";
    public static final String ANIEL_PASS = "S1n4ps#@";
    public static final String ANIEL_TOKEN = "UzFuNHBzI0A=";

    // Endpoints
    public static final String ENDPOINT_MOVIMENTACOES = "/api/AplicacoesRemocoesApi/GetAll";

    // Email / Outlook IMAP (Configurações) - Ignorar por enquanto
    public static final String MAIL_HOST = "outlook.office365.com";
    public static final String MAIL_PORT = "993";
    public static final String MAIL_USER = "seu_email@outlook.com"; // Deve ser configurado
    public static final String MAIL_PASS = "sua_senha_ou_app_password"; // Recomenda-se usar variável de ambiente ou
                                                                        // prop separada
}
