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

public class ServicoIngestao {

    private static final String HOME = System.getProperty("user.home");
    // Pasta que será monitorada (Downloads do Usuário)
    private static final String PASTA_MONITORADA = HOME + "/Downloads";
    
    // Pasta definitiva para os arquivos do Stock Técnico
    private static final String PASTA_STOCK_TECNICO = "C:\\Users\\user\\Desktop\\ARMANDO POWER BI\\VivoAging\\Equipamentos serializados";
    
    // Legado: Outras pastas (mantido para compatibilidade)
    private static final String BASE_SISTEMA_DIVERSOS = HOME + "/SIMULADO_PROJETO/Sistema_Final/";

    // Mapa para associar Palavra-Chave -> Subpasta de Destino
    private static final Map<String, String> ROTAS = new HashMap<>();

    static {
        // Palavras-chave para identificar arquivos
        ROTAS.put("vivo", "STOCK"); // Identificador lógico
        ROTAS.put("stock", "STOCK");
        ROTAS.put("wms", "WMS/QUERY/wms_atual.xlsx");
        ROTAS.put("aniel", "Aniel/QUERY/aniel_atual.xlsx");
        ROTAS.put("modem", "Modem/QUERY/relatorio_modem_atual.xlsx"); // Exemplo extra
    }

    public static void main(String[] args) {
        try {
            iniciarMonitoramento();
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Erro fatal: " + e.getMessage());
        }
    }

    public static void iniciarMonitoramento() throws IOException, InterruptedException {
        Path path = Paths.get(PASTA_MONITORADA);

        if (!Files.exists(path)) {
            System.err.println("❌ Pasta de monitoramento não encontrada: " + PASTA_MONITORADA);
            return;
        }

        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, ENTRY_CREATE);

        System.out.println("🚀 [MONITORAMENTO] Observando pasta: " + PASTA_MONITORADA);

        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                Path fileName = (Path) event.context();

                if (isExcel(fileName.toString())) {
                    System.out.println("\n🆕 Detetado: " + fileName);

                    // Aguarda o arquivo ser liberado pelo SO (importante para arquivos grandes)
                    esperarArquivoEstabilizar(path.resolve(fileName));

                    processarTriagem(path.resolve(fileName));
                }
            }
            if (!key.reset())
                break;
        }
    }

    private static void processarTriagem(Path pathArquivo) {
        String nomeArquivo = pathArquivo.getFileName().toString().toLowerCase();

        // 1. Prioridade: Stock Técnico (Vivo/Stock)
        if (nomeArquivo.contains("vivo") || nomeArquivo.contains("stock")) {
            tratarStockTecnico(pathArquivo);
            return;
        }

        // 2. Outros arquivos (Lógica Genérica)
        String destinoRelativo = null;
        for (Map.Entry<String, String> entrada : ROTAS.entrySet()) {
            if (nomeArquivo.contains(entrada.getKey()) && !entrada.getValue().equals("STOCK")) {
                destinoRelativo = entrada.getValue();
                break;
            }
        }

        if (destinoRelativo != null) {
            moverParaDestinoGenerico(pathArquivo.toFile(), new File(BASE_SISTEMA_DIVERSOS + destinoRelativo));
        } else {
            System.out.println("⚠️ Arquivo ignorado (sem regra definida): " + nomeArquivo);
        }
    }

    private static void tratarStockTecnico(Path origem) {
        try {
            File diretorioDestino = new File(PASTA_STOCK_TECNICO);
            if (!diretorioDestino.exists()) diretorioDestino.mkdirs();

            // 1. LIMPA ARQUIVOS ANTIGOS (mantém apenas o mais recente)
            File[] arquivosAntigos = diretorioDestino.listFiles((d, name) -> name.matches("\\d{3}\\..*"));
            if (arquivosAntigos != null && arquivosAntigos.length > 0) {
                System.out.println("🗑️ Removendo " + arquivosAntigos.length + " arquivo(s) antigo(s)...");
                for (File antigo : arquivosAntigos) {
                    if (antigo.delete()) {
                        System.out.println("   ✓ Removido: " + antigo.getName());
                    } else {
                        System.err.println("   ✗ Falha ao remover: " + antigo.getName());
                    }
                }
            }

            // 2. Pega a extensão original do arquivo
            String nomeOriginal = origem.getFileName().toString();
            String extensao = "";
            int i = nomeOriginal.lastIndexOf('.');
            if (i > 0) {
                extensao = nomeOriginal.substring(i); // ex: .csv ou .xlsx
            }

            // 3. Sempre usa 001 (já que limpamos os antigos)
            String novoNome = String.format("001%s", extensao);
            File destino = new File(diretorioDestino, novoNome);

            // 4. Move o arquivo
            FileUtils.moveFile(origem.toFile(), destino);
            System.out.println("✅ [TRIAGEM] Arquivo Stock movido e renomeado para: " + destino.getAbsolutePath());
            System.out.println("ℹ️ [AÇÃO MÍNIMA] Arquivo pronto para importação manual.");

        } catch (IOException e) {
            System.err.println("❌ Erro ao mover Stock Técnico: " + e.getMessage());
        }
    }

    private static void moverParaDestinoGenerico(File origem, File destino) {
        try {
            FileUtils.forceMkdirParent(destino);
            if (destino.exists()) FileUtils.forceDelete(destino);
            FileUtils.moveFile(origem, destino);
            System.out.println("✅ Arquivo genérico movido para: " + destino.getAbsolutePath());
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

    private static boolean isExcel(String nome) {
        return nome.endsWith(".xlsx") || nome.endsWith(".xls") || nome.endsWith(".csv");
    }

    private static void esperarArquivoEstabilizar(Path path) throws InterruptedException {
        // Tenta verificar se o tamanho do arquivo parou de crescer
        long tamanhoAnterior = -1;
        while (true) {
            long tamanhoAtual = path.toFile().length();
            if (tamanhoAnterior == tamanhoAtual && tamanhoAtual > 0)
                break;
            tamanhoAnterior = tamanhoAtual;
            Thread.sleep(1000);
        }
    }
}