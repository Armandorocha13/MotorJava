# 📊 Mapeamento de Colunas - Tabela `stock_tecnico`

## 🗄️ Informações Gerais
- **Banco de Dados:** vivo_aging
- **Tabela:** stock_tecnico
- **Tipo:** Tabela de estoque técnico de equipamentos serializados
- **Última Atualização:** 2026-02-17

---

## 📋 Estrutura de Colunas

### 1️⃣ **Identificação e Origem**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 1 | `origem_arquivo` | VARCHAR | 255 | SIM | Nome do arquivo de origem dos dados (ex: 001.xlsx) |
| 2 | `Fonte_de_dados` | VARCHAR | 50 | SIM | Fonte dos dados importados |

---

### 2️⃣ **Identificação do Técnico**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 3 | `id_tecnico` | INT | - | SIM | ID único do técnico |
| 4 | `nome_do_tecnico` | VARCHAR | 50 | SIM | Nome completo do técnico responsável |

---

### 3️⃣ **Localização**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 5 | `centro_fisico_tecnico` | VARCHAR | 100 | SIM | Centro físico onde o técnico está alocado |
| 6 | `origem_centro_materiais` | VARCHAR | 100 | SIM | Centro de origem dos materiais |
| 7 | `origem_pool_centro_materiais` | VARCHAR | 100 | SIM | Pool de origem do centro de materiais |

---

### 4️⃣ **Produto/SKU**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 8 | `sku` | VARCHAR | - | SIM | Código SKU do produto |
| 9 | `descricao_sku` | VARCHAR | 100 | SIM | Descrição detalhada do SKU |

---

### 5️⃣ **Serialização e Identificação do Equipamento**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 10 | `numero_de_serie` | VARCHAR | 100 | SIM | Número de série único do equipamento |
| 11 | `id_grupo` | INT | - | SIM | ID do grupo ao qual o equipamento pertence |
| 12 | `descricao_grupo` | VARCHAR | 100 | SIM | Descrição do grupo |

---

### 6️⃣ **Datas e Temporalidade**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 13 | `ultima_modificacao` | DATE | - | SIM | Data da última modificação do registro |
| 14 | `companhia` | VARCHAR | 10 | SIM | Código da companhia |

---

### 7️⃣ **Classificação e Hierarquia**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 15 | `tecnologia_no_validados` | VARCHAR | - | SIM | Tecnologias não validadas |
| 16 | `origem_centro_fisico` | VARCHAR | 100 | SIM | Centro físico de origem |
| 17 | `botao_de_indisponibilidade` | VARCHAR | 50 | SIM | Indicador de indisponibilidade |
| 18 | `numero_de_serie_validacao` | VARCHAR | 100 | SIM | Número de série para validação |
| 19 | `id_de_tecnico` | INT | - | SIM | ID alternativo do técnico |

---

### 8️⃣ **Controle e Rastreamento**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 20 | `totalErencia` | VARCHAR | 50 | SIM | Total de referência/herança |
| 21 | `devolucoes` | VARCHAR | 50 | SIM | Registro de devoluções |
| 22 | `id_rapm` | INT | - | SIM | ID do sistema RAPM |
| 23 | `retirado_distinguido` | VARCHAR | 10 | SIM | Indicador se foi retirado/distinguido |

---

### 9️⃣ **Status e Estado**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 24 | `estao_bloqueado` | VARCHAR | 10 | SIM | Indicador de bloqueio do estoque |
| 25 | `coordenador` | VARCHAR | 100 | SIM | Nome do coordenador responsável |
| 26 | `supervisor` | VARCHAR | 100 | SIM | Nome do supervisor responsável |
| 27 | `status_tecnico` | VARCHAR | 50 | SIM | Status atual do técnico |
| 28 | `dias_estoque` | INT | - | SIM | Quantidade de dias que o item está em estoque |

---

### 🔟 **Análise e Classificação (Campos Calculados)**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 29 | `status_aging` | VARCHAR | 50 | SIM | Classificação de aging (Normal, Atenção, Crítico) |
| 30 | `excluir` | VARCHAR | 10 | SIM | Marcador para exclusão |

---

### 1️⃣1️⃣ **Auditoria e Snapshot**

| # | Coluna | Tipo | Tamanho | Nulo | Descrição |
|---|--------|------|---------|------|-----------|
| 31 | `data_snapshot` | DATE | - | SIM | Data do snapshot/foto dos dados |

---

## 🔄 Campos Calculados Automaticamente

### **dias_estoque**
```sql
dias_estoque = DATEDIFF(CURDATE(), ultima_modificacao)
```
Calcula quantos dias o equipamento está em estoque desde a última modificação.

### **status_aging**
```sql
status_aging = CASE 
    WHEN dias_estoque IS NULL THEN 'Não Classificado'
    WHEN dias_estoque BETWEEN 0 AND 7 THEN 'Normal (0-7 dias)'
    WHEN dias_estoque BETWEEN 8 AND 14 THEN 'Atenção (7-14 dias)'
    WHEN dias_estoque > 14 THEN 'Aging Crítico (>14 dias)'
    ELSE 'Não Classificado'
END
```

---

## 🔗 Relacionamentos

### **Tabela `forca_sp` (Força de Trabalho)**
- **Chave de Junção:** `nome_do_tecnico` ↔ `colaborador`
- **Campos Enriquecidos:**
  - `supervisor` - Atualizado via JOIN
  - `coordenador` - Atualizado via JOIN

---

## 📊 Índices Criados

```sql
CREATE INDEX idx_nome_tecnico ON stock_tecnico(nome_do_tecnico);
CREATE INDEX idx_data_snapshot ON stock_tecnico(data_snapshot);
CREATE INDEX idx_status_aging ON stock_tecnico(status_aging);
CREATE INDEX idx_dias_estoque ON stock_tecnico(dias_estoque);
CREATE INDEX idx_numero_serie ON stock_tecnico(numero_de_serie);
CREATE INDEX idx_supervisor ON stock_tecnico(supervisor);
```

---

## 📥 Origem dos Dados

### **Arquivo Excel/CSV**
Os dados são importados de arquivos `.xlsx` ou `.csv` localizados em:
```
C:\Users\user\Desktop\ARMANDO POWER BI\VivoAging\Equipamentos serializados\
```

### **Processo de Importação**
1. **Classe:** `ImportadorArquivo.java`
2. **Método:** `executarCarga(String filePath)`
3. **Colunas do CSV:** 25 colunas principais
4. **Colunas Extras:** 7 colunas adicionais (calculadas/NULL)
5. **Total:** 33 colunas + `data_snapshot` (CURRENT_DATE)

---

## 🎯 Uso Principal

### **View para Power BI**
```sql
CREATE OR REPLACE VIEW vw_powerbi_equipamentos AS
SELECT 
    e.numero_de_serie AS numero_serie,
    e.nome_do_tecnico AS nome_tecnico,
    f.supervisor AS supervisor,
    f.coordenador AS coordenador,
    e.sku AS sku,
    e.descricao_sku AS descricao,
    e.status_tecnico AS status_tecnico,
    e.dias_estoque AS dias_estoque,
    e.status_aging AS status_aging,
    -- ... outros campos
FROM stock_tecnico e
INNER JOIN forca_sp f 
    ON UPPER(TRIM(e.nome_do_tecnico)) = UPPER(TRIM(f.colaborador))
WHERE e.data_snapshot = (SELECT MAX(data_snapshot) FROM stock_tecnico);
```

---

## ⚠️ Observações Importantes

1. **Encoding:** Os dados são lidos com charset ISO-8859-1 para suportar caracteres especiais
2. **Transações:** A importação usa transações com commit/rollback
3. **Limpeza:** Dados do dia atual são deletados antes de nova importação
4. **Validação:** Linhas vazias são ignoradas automaticamente
5. **Batch Processing:** Inserções são feitas em lotes de 1000 registros

---

## 🔧 Manutenção

### **Atualizar Dados**
```bash
java -cp "bin;lib/*" com.motorjava.ImportadorArquivo
```

### **Verificar Schema**
```bash
java -cp "bin;lib/*" com.motorjava.VerificarSchema
```

### **Verificar Ordem das Colunas**
```bash
java -cp "bin;lib/*" com.motorjava.VerificarOrdemColunas
```

---

**Documento gerado em:** 2026-02-17  
**Versão:** 1.0  
**Projeto:** MotorJava - Sistema VIVO Aging
