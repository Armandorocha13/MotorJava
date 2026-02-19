package com.motorjava.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    // Caminhos Base
    public static final String BASE_PBI = "C:\\Users\\user\\Documents\\PBI\\BASE DADOS";
    
    // Outlook
    public static final String PATH_OUTLOOK = BASE_PBI + "\\OUTLOOK";
    
    // IHS / WMS
    public static final String PATH_DOWNLOADS = System.getProperty("user.home") + "\\Downloads";
    public static final String PATH_IHS_WMS_QUERY = BASE_PBI + "\\IHS\\WMS\\QUERY";
    
    // Vivo Aging (Mantendo compatibilidade com o que já existe se necessário)
    public static final String PATH_VIVO_DATA = "C:\\Users\\user\\Desktop\\MotorJava\\data";
    public static final String SCRIPT_VIVO_VBS = "C:\\Users\\user\\Desktop\\MotorJava\\scripts\\atualizar_dados.vbs";

    // Power Automate Webhook URL (Placeholder para ser preenchido)
    public static final String POWER_AUTOMATE_WEBHOOK_URL = "https://prod-XX.brazilsouth.logic.azure.com:443/workflows/...";

    // Aniel API
    public static final String ANIEL_API_BASE_URL = "https://api.aniel.com.br/v1"; // Exemplo
}
