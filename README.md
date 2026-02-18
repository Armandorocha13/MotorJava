# 🚀 Módulo de Ingestão e Monitoramento (Motor Java)

Este projeto é responsável por gerenciar a entrada de arquivos (Excel/CSV) e automaticamente processá-los e inseri-los no banco de dados.

O sistema funciona em **background**, monitorando a pasta de **Downloads** do usuário.

## 📂 Estrutura do Projeto

```
/
├── SIMULADO_PROJETO/        # (Legado) Estrutura antiga de pastas
├── src/main/java/           # Código Fonte Java
│   └── com/motorjava/
│       ├── GuiApp.java              # 🎨 Interface Gráfica (Dashboard)
│       ├── ServicoIngestao.java     # 🧠 Serviço de Monitoramento (Watcher)
│       ├── ImportadorArquivo.java   # 💾 Lógica de Banco de Dados (ETL)
│       └── config/
│           └── DatabaseConfig.java  # ⚙️ Configuração de Conexão MySQL
├── resources/               # Arquivos de recurso
│   └── database.properties  # Senhas e URLs do Banco
└── INICIAR_DASHBOARD.bat    # ▶️ Clique duplo aqui para rodar
```

## 🛠️ Como Funciona

1. **Monitoramento**: O `ServicoIngestao` observa a pasta `C:\Users\%USERNAME%\Downloads`.
2. **Triagem**: Assim que um arquivo como `stock_vivo_atual.xlsx` ou `001.xlsx` chega, ele é detectado.
3. **Padronização**: O arquivo é renomeado sequencialmente (ex: `005.xlsx`) e movido para:
   - `Desktop\ARMANDO POWER BI\VivoAging\StockTecnicos`
4. **Carga**: O `ImportadorArquivo` lê o Excel e insere os dados na tabela `estoque_vivo_historico` do MySQL.
5. **Dashboard**: A interface (`GuiApp`) mostra o progresso visualmente.

## 📋 Pré-requisitos

- Java JDK 25 (Configurado no script de inicialização)
- MySQL Server rodando
- Tabela `estoque_vivo_historico` criada

## 🚀 Como Iniciar

Apenas execute o arquivo:
> **iniciar_projeto.bat**

Este script irá verificar se o Maven está instalado e, caso contrário, oferecerá a opção de instalar e configurar automaticamente.

E então a aplicação será iniciada.
