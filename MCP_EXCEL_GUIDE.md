# Guia de Uso: Excel MCP Server

Este projeto utiliza o [excel-mcp-server](https://github.com/haris-musa/excel-mcp-server) para manipular arquivos Excel (.xlsx) diretamente através do Gemini CLI.

## Configuração Atual

- **Python Venv:** `python/venv`
- **Diretório de Planilhas:** `C:\Users\mando\OneDrive\Área de Trabalho\planilhas`
- **Comando do Servidor:** `python -m excel_mcp stdio`

## Como Usar via Gemini CLI

Você pode solicitar tarefas em linguagem natural e o Gemini usará as ferramentas do MCP automaticamente. Exemplos:

### 1. Criar uma nova planilha
> "Crie uma planilha chamada 'vendas.xlsx' com as colunas Produto, Quantidade e Preço."

### 2. Ler dados de um arquivo
> "Leia os dados da planilha 'test_mcp.xlsx' e me mostre o que tem lá."

### 3. Adicionar fórmulas e formatação
> "Na planilha 'vendas.xlsx', adicione uma coluna 'Total' que seja Quantidade * Preço e coloque o cabeçalho em negrito."

### 4. Gerar Gráficos e Pivot Tables
> "Crie um gráfico de barras baseado nos dados da planilha 'relatorio.xlsx'."

## Comandos Úteis (Terminal)

Para listar os servidores MCP configurados:
```bash
gemini mcp list
```

Para verificar se o servidor está respondendo (em modo debug):
```bash
gemini mcp --debug
```

## Ferramentas Disponíveis no MCP
- `read_spreadsheet`: Lê o conteúdo de um arquivo.
- `create_spreadsheet`: Cria um novo arquivo .xlsx.
- `edit_spreadsheet`: Altera células, estilos e fórmulas.
- `add_chart`: Insere gráficos (barras, linhas, pizza).
- `create_pivot_table`: Gera tabelas dinâmicas.
