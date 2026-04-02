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
2. **Triagem**: Assim que novos arquivos chegam, eles são detectados e validados.
3. **Padronização**: O arquivo é processado e movido para as pastas de destino configuradas.
4. **Carga**: O `ImportadorArquivo` lê o Excel e insere os dados no Banco de Dados (MySQL/PostgreSQL).
5. **Dashboard**: A interface moderna mostra o status dos processos e logs em tempo real.

## 📋 Pré-requisitos

- Java JDK 25 (Configurado no script de inicialização)
- Supabase (PostgreSQL) configurado
- Estrutura de tabelas criada (ver database/SUPABASE_SETUP.sql)

## 🚀 Como Iniciar

Apenas execute o arquivo:
> **start_project.bat**

Este script irá iniciar o backend Java e o dashboard React simultaneamente.

Backend rodando na porta 8080.
Dashboard rodando na porta 5173.
