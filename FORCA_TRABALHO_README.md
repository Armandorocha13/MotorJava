# 📊 Tabela Força de Trabalho - Documentação

## 🎯 Objetivo
Armazenar informações dos técnicos e colaboradores da Vivo para integração com Power BI.

## 📋 Estrutura da Tabela: `forca_trabalho`

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| `id` | INT (PK) | ID auto-incremento |
| `sap` | VARCHAR(20) UNIQUE | Código SAP do colaborador |
| `colaborador` | VARCHAR(255) | Nome completo |
| `sexo` | CHAR(1) | M/F |
| `funcao` | VARCHAR(100) | Função do colaborador |
| `coordenador` | VARCHAR(255) | Nome do coordenador |
| `supervisor` | VARCHAR(255) | Nome do supervisor |
| `contato` | VARCHAR(20) | Telefone |
| `observacao` | TEXT | Observações gerais |
| `status` | VARCHAR(20) | ATIVO/INATIVO |
| `data_atualizacao` | TIMESTAMP | Última atualização |

## 🚀 Como Usar

### 1️⃣ Criar a Tabela no Banco
```bash
.\criar_tabela_forca.bat
```
Digite a senha do MySQL quando solicitado.

### 2️⃣ Importar Dados do Excel
```bash
.\importar_forca_trabalho.bat
```

## 📁 Arquivo de Origem
- **Nome**: `Força VIVO SP.xlsx`
- **Localização**: Raiz do projeto
- **Colunas**: Sap, Colaborador, Sexo, Função, Coordenador, Supervisor, Ctt, Obs., STATUS

## 🔄 Funcionalidades

### UPSERT Automático
O importador usa `INSERT ... ON DUPLICATE KEY UPDATE`, ou seja:
- Se o SAP **não existe**: insere novo registro
- Se o SAP **já existe**: atualiza os dados

### Validações
- ✅ SAP obrigatório (não pode ser vazio)
- ✅ Status padrão: "ATIVO" se não informado
- ✅ Sexo convertido para maiúscula
- ✅ Trim em todos os campos de texto

## 📊 Integração com Power BI

### Consulta Básica
```sql
SELECT * FROM forca_trabalho WHERE status = 'ATIVO';
```

### Join com Estoque
```sql
SELECT 
    e.nome_tecnico,
    f.coordenador,
    f.supervisor,
    f.funcao,
    f.contato,
    COUNT(*) as total_equipamentos
FROM estoque_vivo_historico e
LEFT JOIN forca_trabalho f ON e.id_tecnico = f.sap
WHERE e.data_snapshot = CURRENT_DATE
GROUP BY e.nome_tecnico, f.coordenador, f.supervisor, f.funcao, f.contato;
```

### Análise por Coordenador
```sql
SELECT 
    coordenador,
    COUNT(*) as total_tecnicos,
    SUM(CASE WHEN status = 'ATIVO' THEN 1 ELSE 0 END) as ativos
FROM forca_trabalho
GROUP BY coordenador
ORDER BY total_tecnicos DESC;
```

## 🔍 Índices Criados
- `idx_sap`: Busca rápida por código SAP
- `idx_status`: Filtro por status
- `idx_coordenador`: Agrupamento por coordenador
- `idx_supervisor`: Agrupamento por supervisor

## 📝 Observações
- A coluna `data_atualizacao` é atualizada automaticamente a cada UPDATE
- O campo `sap` é UNIQUE - não permite duplicatas
- Charset: UTF-8 (suporta acentuação)

## 🎨 Próximos Passos
1. ✅ Criar tabela no banco
2. ✅ Importar dados iniciais
3. 🔄 Configurar atualização automática (opcional)
4. 📊 Criar dashboards no Power BI
