package com.motorjava;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import static java.nio.file.StandardWatchEventKinds.*;

public class ServicoIngestao {

    private static final String HOME = System.getProperty("user.home");
    // Sua pasta dedicada onde você salvará os arquivos tratados
    private static final String PASTA_TRATADOS = HOME + "/SIMULADO_PROJETO/Saida_Tratada";
    private static final String BASE_SISTEMA = HOME + "/SIMULADO_PROJETO/Sistema_Final/";

    // Configurações de Banco de Dados (Exemplo MySQL)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/meu_banco_dados";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "senha";

    // Mapa para associar Palavra-Chave -> Subpasta de Destino
    private static final Map<String, String> ROTAS = new HashMap<>();

    static {
        ROTAS.put("vivo", "VIVO/QUERY/stock_vivo_atual.xlsx");
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
        Path path = Paths.get(PASTA_TRATADOS);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
            System.out.println("📂 Pasta de monitoramento criada: " + PASTA_TRATADOS);
        }

        WatchService watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, ENTRY_CREATE);

        System.out.println("🚀 [MOTOR JAVA] FFA INFRAESTRUTURA - Aguardando arquivos tratados...");

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
        String destinoRelativo = null;

        // Busca a palavra-chave no mapa
        for (Map.Entry<String, String> entrada : ROTAS.entrySet()) {
            if (nomeArquivo.contains(entrada.getKey())) {
                destinoRelativo = entrada.getValue();
                break;
            }
        }

        if (destinoRelativo != null) {
            moverParaDestino(pathArquivo.toFile(), new File(BASE_SISTEMA + destinoRelativo));
        } else {
            System.out.println("⚠️ IGNORADO: Nenhuma regra encontrada para o arquivo: " + nomeArquivo);
        }
    }

    private static void moverParaDestino(File origem, File destino) {
        try {
            // Garante que a pasta pai exista (ex: VIVO/QUERY)
            FileUtils.forceMkdirParent(destino);

            // Se o arquivo antigo existir (ex: o de ontem), deleta para substituir pelo
            // novo
            if (destino.exists()) {
                FileUtils.forceDelete(destino);
                System.out.println("♻️ Removendo versão antiga em: " + destino.getName());
            }

            FileUtils.moveFile(origem, destino);
            System.out.println("✅ SUCESSO: Movido para -> " + destino.getAbsolutePath());

            // 3. Processamento do conteúdo Excel e Envio para DB
            processarExcelESalvarNoBanco(destino);

            System.out.println("-------------------------------------------------");

        } catch (IOException e) {
            System.err.println("❌ Erro ao processar arquivo: " + e.getMessage());
        }
    }

    private static void processarExcelESalvarNoBanco(File arquivoExcel) {
        System.out.println("📊 Lendo conteúdo de: " + arquivoExcel.getName());

        try (FileInputStream fis = new FileInputStream(arquivoExcel);
                Workbook workbook = new XSSFWorkbook(fis);
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            Sheet sheet = workbook.getSheetAt(0); // Lê a primeira aba
            Iterator<Row> rowIterator = sheet.iterator();

            // SQL Exemplo: Ajuste conforme sua tabela
            String sql = "INSERT INTO dados_ingestao (coluna1, coluna2, origem) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Pular cabeçalho se houver
            if (rowIterator.hasNext())
                rowIterator.next();

            int count = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Exemplo de leitura de células (ajuste conforme seu Excel)
                String celula1 = getCellValueAsString(row.getCell(0));
                String celula2 = getCellValueAsString(row.getCell(1));

                pstmt.setString(1, celula1);
                pstmt.setString(2, celula2);
                pstmt.setString(3, arquivoExcel.getName());

                pstmt.addBatch();
                count++;
            }

            pstmt.executeBatch();
            System.out.println("🗄️ " + count + " linhas inseridas no banco de dados.");

        } catch (IOException e) {
            System.err.println("❌ Erro ao ler Excel: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erro de Banco de Dados: " + e.getMessage());
            System.err.println("💡 Dica: Verifique se o MySQL está rodando e se a tabela 'dados_ingestao' existe.");
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