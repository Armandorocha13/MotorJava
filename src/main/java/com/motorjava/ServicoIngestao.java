package com.motorjava;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import static java.nio.file.StandardWatchEventKinds.*;


/**
 * SERVIÇO DE INGESTÃO (O CÉREBRO)
 * ---------------------------------------------------------
 * Esta classe é responsável por ficar "escutando" a pasta de Downloads.
 * Assim que um arquivo novo chega, ela:
 * 1. Verifica se é um Excel.
 * 2. Verifica se é um arquivo de interesse (Stock ou Vivo).
 * 3. Renomeia e move para a pasta definitiva.
 * 4. Aciona a carga no banco de dados.
 */
public class ServicoIngestao {

    // --- CONFIGURAÇÕES DE PASTAS ---
    private static final String HOME = System.getProperty("user.home");
    
    // Pasta que será monitorada (Onde os arquivos chegam)
    private static final String PASTA_MONITORADA = HOME + "/Downloads"; // Pasta Monitorada
    
    // Pasta definitiva dos arquivos (Onde os arquivos ficam organizados)
    private static final String PASTA_STOCK_TECNICO = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\VivoAging\\StockTecnicos";
    
    // Legado: Outras pastas de sistemas (mantido para compatibilidade futura)
    private static final String BASE_SISTEMA_DIVERSOS = HOME + "/SIMULADO_PROJETO/Sistema_Final/";
    private static final Map<String, String> OUTRAS_ROTAS = new HashMap<>();

    static {
        OUTRAS_ROTAS.put("wms", "WMS/QUERY/wms_atual.xlsx");
        OUTRAS_ROTAS.put("aniel", "Aniel/QUERY/aniel_atual.xlsx");
        OUTRAS_ROTAS.put("modem", "Modem/QUERY/relatorio_modem_atual.xlsx");
    }

    public static void main(String[] args) {
        try {
            iniciarMonitoramento();
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Erro fatal: " + e.getMessage());
        }
    }

    /**
     * MÉTODO PRINCIPAL DE MONITORAMENTO
     * Fica num loop infinito esperando arquivos aparecerem na pasta de monitoramento.
     */
    public static void iniciarMonitoramento() throws IOException, InterruptedException {
        Path path = Paths.get(PASTA_MONITORADA);

        if (!Files.exists(path)) {
            System.err.println("❌ Pasta de monitoramento não existe: " + PASTA_MONITORADA);
            return;
        }

        // Cria o servico de observação do Sistema Operacional
        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, ENTRY_CREATE); // Registra interesse apenas em criação de arquivos

        System.out.println("🚀 [SERVIÇO INICIADO] Monitorando pasta: " + PASTA_MONITORADA);

        // Loop Infinito
        while (true) {
            WatchKey key = watchService.take(); // Fica parado aqui até algo acontecer
            for (WatchEvent<?> event : key.pollEvents()) { // Quando acontece, processa
                Path fileName = (Path) event.context();

                // Filtra apenas arquivos que nos interessam
                if (isExcel(fileName.toString())) {
                    System.out.println("\n🆕 Detetado: " + fileName);

                    // Aguarda o download terminar (arquivo parar de crescer)
                    esperarArquivoEstabilizar(path.resolve(fileName));

                    // Inicia o fluxo de tratamento
                    processarTriagem(path.resolve(fileName));
                }
            }
            if (!key.reset())
                break;
        }
    }

    /**
     * Decide o que fazer com o arquivo encontrado.
     * Se for 'vivo' ou 'stock', manda para o tratamento especial.
     * Se for outros, manda para as rotas genéricas.
     */
    private static void processarTriagem(Path pathArquivo) {
        String nomeArquivo = pathArquivo.getFileName().toString().toLowerCase();

        // 1. Tratamento Prioritário: Stock Tecnico (Vivo)
        if (nomeArquivo.contains("vivo") || nomeArquivo.contains("stock")) {
            tratarStockTecnico(pathArquivo);
            return;
        }

        // 2. Tratamento Genérico (Outros sistemas) - Mantido para compatibilidade
        String destinoRelativo = null;
        for (Map.Entry<String, String> entrada : OUTRAS_ROTAS.entrySet()) {
            if (nomeArquivo.contains(entrada.getKey())) {
                destinoRelativo = entrada.getValue();
                break;
            }
        }

        if (destinoRelativo != null) {
            moverParaDestinoGenerico(pathArquivo.toFile(), new File(BASE_SISTEMA_DIVERSOS + destinoRelativo));
        } else {
            System.out.println("⚠️ IGNORADO: O arquivo não corresponde a nenhuma regra de negócio: " + nomeArquivo);
        }
    }

    /**
     * FLUXO CRÍTICO: Tratamento do Stock Técnico
     * 1. Cria a pasta de destino se não existir.
     * 2. Calcula o próximo número de arquivo (ex: 005.xlsx se já tiver até o 004).
     * 3. Move e renomeia o arquivo.
     * 4. Chama o Importador para gravar no banco.
     */
    private static void tratarStockTecnico(Path origem) {
        try {
            System.out.println("\n⏳ --- PASSO 1: PADRONIZAÇÃO DO ARQUIVO ---");
            File diretorioDestino = new File(PASTA_STOCK_TECNICO);
            
            // Garante que a pasta existe
            if (!diretorioDestino.exists()) {
                diretorioDestino.mkdirs();
            }

            // --- LÓGICA DE RENOMEAÇÃO SEQUENCIAL ---
            // Lista todos os arquivos .xlsx da pasta
            File[] arquivos = diretorioDestino.listFiles((dir, name) -> name.endsWith(".xlsx"));
            int max = 0;
            if (arquivos != null) {
                for (File f : arquivos) {
                    String nome = f.getName();
                    // Procura por nomes que sejam apenas números (ex: 001.xlsx)
                    if (nome.matches("\\d{3}\\.xlsx")) {
                        try {
                            int n = Integer.parseInt(nome.replace(".xlsx", ""));
                            if (n > max)
                                max = n; // Descobre o maior número atual
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
            int proximo = max + 1; // O novo arquivo será o próximo número
            String novoNome = String.format("%03d.xlsx", proximo);
            File destino = new File(diretorioDestino, novoNome);

            // Move o arquivo da pasta de downloads para a pasta final com o novo nome
            if (destino.exists()) {
                FileUtils.forceDelete(destino); // Segurança anti-conflito
            }
            FileUtils.moveFile(origem.toFile(), destino);
            
            System.out.println("✅ Arquivo renomeado para: " + novoNome);
            System.out.println("✅ Arquivo salvo em: " + PASTA_STOCK_TECNICO);

            // --- CHAMADA DE CARGA NO BANCO ---
            System.out.println("\n⏳ --- PASSO 2: IMPORTAÇÃO PARA BANCO DE DADOS ---");
            System.out.println("🔄 Lendo planilha e inserindo registros...");
            try {
                // Chama a classe que sabe ler Excel e gravar SQL
                ImportadorArquivo.executarCarga(destino.getAbsolutePath());
                System.out.println("✅ Ciclo de processamento concluído com sucesso!");
            } catch (Exception e) {
                System.err.println("❌ Erro ao executar carga automática: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("❌ Erro de I/O ao processar arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Auxiliar para mover arquivos de outros sistemas (Legado)
     */
    private static void moverParaDestinoGenerico(File origem, File destino) {
        try {
            FileUtils.forceMkdirParent(destino);
            if (destino.exists()) {
                FileUtils.forceDelete(destino);
                System.out.println("♻️ Substituindo versão antiga em: " + destino.getName());
            }
            FileUtils.moveFile(origem, destino);
            System.out.println("✅ Arquivo movido para: " + destino.getAbsolutePath());
            System.out.println("ℹ️ Apenas arquivamento, sem importação configurada.");

        } catch (IOException e) {
            System.err.println("❌ Erro ao mover arquivo genérico: " + e.getMessage());
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    /**
     * Verifica se o arquivo é um Excel ou CSV válido
     */
    private static boolean isExcel(String nome) {
        return nome.endsWith(".xlsx") || nome.endsWith(".xls") || nome.endsWith(".csv");
    }

    /**
     * Aguarda o arquivo terminar de ser gravado (Downloads geralmente criam o arquivo com 0kb e enchem aos poucos)
     */
    private static void esperarArquivoEstabilizar(Path path) throws InterruptedException {
        long tamanhoAnterior = -1;
        int tentativas = 0;
        // Espera até que o tamanho pare de mudar por 1 segundo
        while (tentativas < 30) { // Timeout de 30s
            long tamanhoAtual = path.toFile().length();
            if (tamanhoAnterior == tamanhoAtual && tamanhoAtual > 0)
                break;
            tamanhoAnterior = tamanhoAtual;
            Thread.sleep(1000);
            tentativas++;
        }
    }
}