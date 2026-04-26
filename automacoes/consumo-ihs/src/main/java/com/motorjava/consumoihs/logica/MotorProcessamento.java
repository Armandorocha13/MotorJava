package com.motorjava.consumoihs.logica;

import com.motorjava.consumoihs.modelos.InformacaoOS;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Motor principal de processamento: Normalização e Consolidação de dados.
 */
public class MotorProcessamento {
    private static final SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");

    // Método principal que coordena o fluxo
    public static void executarFluxoCompleto(String caminhoArquivo, BiConsumer<String, Integer> rastreador) throws Exception {
        normalizarExcel(caminhoArquivo, rastreador);
        consolidarDados(caminhoArquivo, rastreador);
    }

    /**
     * Limpa e padroniza a planilha CONSUMO IHS.
     */
    public static void normalizarExcel(String caminho, BiConsumer<String, Integer> rastreador) throws Exception {
        rastreador.accept("🧹 Iniciando limpeza da planilha...", 10);
        File arquivo = new File(caminho);
        if (!arquivo.exists()) throw new FileNotFoundException("Arquivo não encontrado em: " + caminho);

        try (Workbook wb = new XSSFWorkbook(new FileInputStream(arquivo))) {
            // 1. Limpar Base de Dados
            Sheet abaBase = wb.getSheet("BASE DE DADOS");
            if (abaBase != null) {
                rastreador.accept("  > Organizando aba BASE DE DADOS...", 30);
                int linhaCabecalho = encontrarLinhaCabecalho(abaBase);
                if (linhaCabecalho > 0) {
                    for (int i = 0; i < linhaCabecalho; i++) {
                        Row r = abaBase.getRow(i);
                        if (r != null) abaBase.removeRow(r);
                    }
                    abaBase.shiftRows(linhaCabecalho, abaBase.getLastRowNum(), -linhaCabecalho);
                }

                // Limpar linhas vazias e padronizar textos
                for (int i = abaBase.getLastRowNum(); i >= 1; i--) {
                    Row r = abaBase.getRow(i);
                    if (r == null) continue;
                    String os = extrairValorCelula(r.getCell(4)).trim();
                    if (os.isEmpty()) {
                        abaBase.removeRow(r);
                    } else {
                        Cell celulaServico = r.getCell(3);
                        if (celulaServico != null) celulaServico.setCellValue(celulaServico.toString().toUpperCase().trim());
                    }
                }
            }

            rastreador.accept("  > Padronizando abas de apoio...", 70);
            limparAbaSimples(wb.getSheet("FORÇA DE TRABALHO"), 3);
            limparAbaSimples(wb.getSheet("DE PARA TIPO DE TRABALHO"), 0);
            limparAbaPesquisa(wb.getSheet("PESQUISA ANIEL (NOVOS)"));
            limparAbaPesquisa(wb.getSheet("PESQUISA ANIEL (RETIRADOS)"));

            rastreador.accept("  > Gravando alterações no disco...", 90);
            try (FileOutputStream fos = new FileOutputStream(caminho)) {
                wb.write(fos);
            }
        }
        rastreador.accept("✨ Planilha normalizada com sucesso!", 100);
    }

    /**
     * Realiza o cruzamento de dados e gera os relatórios finais.
     */
    public static void consolidarDados(String caminho, BiConsumer<String, Integer> rastreador) throws Exception {
        rastreador.accept("📖 Lendo arquivos para consolidação...", 10);
        Workbook wb = new XSSFWorkbook(new FileInputStream(caminho));

        rastreador.accept("🔍 Cruzando informações base...", 30);
        Map<String, InformacaoOS> mapaBase = carregarMapaBase(wb.getSheet("BASE DE DADOS"));
        
        rastreador.accept("🔍 Carregando tabelas de apoio...", 50);
        Map<String, String> mapaEquipes = carregarMapaSimples(wb.getSheet("FORÇA DE TRABALHO"), 3, 4);
        Map<String, String> mapaDePara = carregarMapaSimples(wb.getSheet("DE PARA TIPO DE TRABALHO"), 0, 4);

        List<String[]> resultados = new ArrayList<>();
        
        rastreador.accept("⚙️ Processando Novos...", 75);
        processarAbaRelatorio(wb.getSheet("PESQUISA ANIEL (NOVOS)"), true, mapaBase, mapaEquipes, mapaDePara, resultados);
        
        rastreador.accept("⚙️ Processando Retirados...", 90);
        processarAbaRelatorio(wb.getSheet("PESQUISA ANIEL (RETIRADOS)"), false, mapaBase, mapaEquipes, mapaDePara, resultados);

        wb.close();
        
        rastreador.accept("💾 Gerando arquivos de saída...", 95);
        File pastaRaiz = new File(caminho).getParentFile();
        gerarHtml(resultados, new File(pastaRaiz, "html_output"));
        gerarExcel(resultados, new File(pastaRaiz, "excel_output"));
        
        rastreador.accept("🏁 Consolidação concluída: " + resultados.size() + " registros.", 100);
    }

    // Auxiliares de Limpeza
    private static int encontrarLinhaCabecalho(Sheet aba) {
        for (int i = 0; i < Math.min(50, aba.getLastRowNum() + 1); i++) {
            Row r = aba.getRow(i);
            if (r == null) continue;
            for (int c = 0; c < 5; c++) {
                Cell celula = r.getCell(c);
                if (celula != null && celula.toString().toUpperCase().contains("NOME") && celula.toString().toUpperCase().contains("PSR")) return i;
            }
        }
        return -1;
    }

    private static void limparAbaSimples(Sheet aba, int colunaChave) {
        if (aba == null) return;
        for (int i = aba.getLastRowNum(); i >= 1; i--) {
            Row r = aba.getRow(i);
            if (r == null || extrairValorCelula(r.getCell(colunaChave)).isEmpty()) {
                if (r != null) aba.removeRow(r);
            }
        }
    }

    private static void limparAbaPesquisa(Sheet aba) {
        if (aba == null) return;
        for (int i = aba.getLastRowNum(); i >= 1; i--) {
            Row r = aba.getRow(i);
            if (r == null) continue;
            String os = extrairValorCelula(r.getCell(26));
            String status = extrairValorCelula(r.getCell(0)).toUpperCase();
            if (os.isEmpty() || status.contains("ENCONTRADO") || (!status.equals("SIM") && !status.equals("NÃO") && !status.equals("NAO"))) {
                aba.removeRow(r);
            }
        }
    }

    // Auxiliares de Dados
    private static Map<String, InformacaoOS> carregarMapaBase(Sheet aba) {
        Map<String, InformacaoOS> mapa = new HashMap<>();
        if (aba == null) return mapa;
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row r = aba.getRow(i);
            if (r == null) continue;
            String os = extrairValorCelula(r.getCell(4));
            if (!os.isEmpty()) {
                mapa.put(os, new InformacaoOS(extrairValorCelula(r.getCell(3)), extrairDataCelula(r.getCell(15))));
            }
        }
        return mapa;
    }

    private static Map<String, String> carregarMapaSimples(Sheet aba, int colChave, int colValor) {
        Map<String, String> mapa = new HashMap<>();
        if (aba == null) return mapa;
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row r = aba.getRow(i);
            if (r == null) continue;
            String chave = extrairValorCelula(r.getCell(colChave)).toUpperCase().trim();
            String valor = extrairValorCelula(r.getCell(colValor)).trim();
            if (!chave.isEmpty()) mapa.put(chave, valor);
        }
        return mapa;
    }

    private static void processarAbaRelatorio(Sheet aba, boolean ehNovo, Map<String, InformacaoOS> mapaBase, Map<String, String> equipes, Map<String, String> dePara, List<String[]> listaFinal) {
        if (aba == null) return;
        for (int i = 1; i <= aba.getLastRowNum(); i++) {
            Row linha = aba.getRow(i);
            if (linha == null) continue;
            String os = extrairValorCelula(linha.getCell(26));
            String status = extrairValorCelula(linha.getCell(0)).toLowerCase();
            if (os.isEmpty() || status.contains("encontrado")) continue;

            String posicao = extrairValorCelula(linha.getCell(1)).toLowerCase();
            Date dataAniel = extrairDataCelula(linha.getCell(19));
            
            boolean deveIncluir = false;
            if (status.contains("não") || status.contains("nao")) deveIncluir = true;
            else if (status.contains("sim") && posicao.contains("aplicado")) {
                InformacaoOS info = mapaBase.get(os);
                if (info != null && info.dataModificacao != null && dataAniel != null && dataAniel.before(info.dataModificacao)) deveIncluir = true;
            }

            if (deveIncluir) {
                InformacaoOS info = mapaBase.get(os);
                String nomeSvc = (info != null) ? info.nomeServico.toUpperCase().trim() : "";
                String idSvc = dePara.getOrDefault(nomeSvc, "");
                String matricula = extrairValorCelula(linha.getCell(23));
                String uf = equipes.getOrDefault(matricula, "SP");
                String codCt = (uf.contains("RJ")) ? "33" : "32";

                listaFinal.add(new String[]{
                    codCt, extrairValorCelula(linha.getCell(18)), os, os, "", "1",
                    (dataAniel != null ? FORMATO_DATA.format(dataAniel) : ""), idSvc, "1", matricula,
                    extrairValorCelula(linha.getCell(12)), extrairValorCelula(linha.getCell(4)), ehNovo ? "1" : "", !ehNovo ? "1" : ""
                });
            }
        }
    }

    // Geradores de Saída
    private static void gerarHtml(List<String[]> dados, File pastaDestino) throws IOException {
        if (!pastaDestino.exists()) pastaDestino.mkdirs();
        StringBuilder html = new StringBuilder("<!DOCTYPE html><html lang='pt-br'><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:sans-serif;padding:20px;} table{border-collapse:collapse;width:100%;font-size:11px;} th,td{border:1px solid #ccc;padding:4px;text-align:left;} th{background:#333;color:white;}</style></head><body>");
        html.append("<h1>CONSOLIDAÇÃO FINAL - FFA INFRAESTRUTURA</h1><table><thead><tr><th>CODCT</th><th>PROJETO</th><th>NUM_OS</th><th>CONTRATO</th><th>NUM_CLIENTE</th><th>TERMINAL</th><th>DATA_EXECUCAO</th><th>TIPO_SERVICO</th><th>SUB_TIPO</th><th>EQUIPE</th><th>CODMAT</th><th>SERIAL</th><th>QTDE_APLIC</th><th>QTDE_REMOV</th></tr></thead><tbody>");
        for (String[] r : dados) {
            html.append("<tr>");
            for (String c : r) html.append("<td>").append(c).append("</td>");
            html.append("</tr>");
        }
        html.append("</tbody></table></body></html>");
        File arquivoSaida = new File(pastaDestino, "CONSOLIDACAO_PADRAO_ANIEL.html");
        try (Writer w = new OutputStreamWriter(new FileOutputStream(arquivoSaida), StandardCharsets.UTF_8)) {
            w.write(html.toString());
        }
    }

    private static void gerarExcel(List<String[]> dados, File pastaDestino) throws IOException {
        if (!pastaDestino.exists()) pastaDestino.mkdirs();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("Importacao Aniel");
            String[] cabecalhos = {"CODCT", "PROJETO", "NUM_OS", "CONTRATO", "NUM_CLIENTE", "TERMINAL", "DATA_EXECUCAO", "TIPO_SERVICO", "SUB_TIPO", "EQUIPE", "CODMAT", "SERIAL", "QTDE_APLIC", "QTDE_REMOV"};
            Row linhaC = s.createRow(0);
            for (int i = 0; i < cabecalhos.length; i++) linhaC.createCell(i).setCellValue(cabecalhos[i]);
            for (int i = 0; i < dados.size(); i++) {
                Row r = s.createRow(i + 1);
                String[] info = dados.get(i);
                for (int j = 0; j < info.length; j++) r.createCell(j).setCellValue(info[j]);
            }
            File arquivoSaida = new File(pastaDestino, "CONSOLIDACAO_FINAL.xlsx");
            try (FileOutputStream fos = new FileOutputStream(arquivoSaida)) {
                wb.write(fos);
            }
        }
    }

    // Utilitários de Célula
    private static String extrairValorCelula(Cell c) {
        if (c == null) return "";
        if (c.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(c)) return FORMATO_DATA.format(c.getDateCellValue());
            String v = String.format("%.0f", c.getNumericCellValue());
            return v.endsWith(".0") ? v.substring(0, v.length() - 2) : v;
        }
        String res = c.toString().trim();
        return res.endsWith(".0") ? res.substring(0, res.length() - 2) : res;
    }

    private static Date extrairDataCelula(Cell c) {
        if (c != null && c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) return c.getDateCellValue();
        return null;
    }
}
