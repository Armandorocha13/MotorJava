# AXIS CONTROL - MOTOR JAVA v5.0

O **AXIS CONTROL** é um ecossistema de automação desktop desenvolvido em Java, projetado para centralizar e otimizar o processamento de relatórios, integração com Excel (VBA) e gestão de dados. A versão 5.0 introduz uma arquitetura modularizada, separando a interface principal das automações específicas.

## 🚀 Módulos e Funcionalidades

### 🖥️ Dashboard Central (motor-nucleo)
- Interface premium construída com **Java Swing** e **FlatLaf**.
- Design em **Dark Mode** total (Preto/Cinza escuro).
- Tipografia moderna (**Quicksand** e **Barrio**).
- Gerenciamento centralizado de logs e execução de processos.

### 🤖 Automações (automacoes/)

#### 1. Giro de Maquinários (giro-maquinario)
- **Sincronização de Configurações**: Escrita direta em planilhas via Jacob.
- **Processamento de Base**: Execução automatizada de macros VBA (`maquinarios`).
- **Giro de Estoque**: Integração com Power BI para visualização de resultados.
- **Backup Automático**: Geração de backups datados antes de qualquer modificação.


## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 17 (OpenJDK portátil incluído).
- **Gerenciamento**: Maven (Estrutura Multi-módulo).
- **GUI**: Java Swing + FlatLaf (Dark Mode).
- **Integração Excel**: Jacob (Java COM Bridge) e Apache POI.
- **Configuração**: Centralizada em arquivos `.properties` (configuracoes/).

## 📂 Estrutura do Projeto

- `motor-nucleo/`: Núcleo da aplicação e interface gráfica.
- `automacoes/`: Módulos independentes de automação de relatórios.
- `configuracoes/`: Arquivos de configuração e caminhos de sistema.
- `dados/`: Repositório central de dados (entrada/saída).
- `banco-de-dados/`: Scripts SQL e configuração de banco.
- `ferramentas/`: JDK 17 e Maven 3.9.9 portáteis.
- `documentos/` & `estudo/`: Documentação técnica e materiais de estudo.

## 🔌 Como Iniciar

1. Certifique-se de que o Excel está instalado (para módulos que usam Jacob).
2. Execute o arquivo: `INICIAR_SISTEMA.bat` na raiz.
3. O sistema irá compilar e iniciar o Dashboard automaticamente.

---
*AXIS - O centro da automação modular.*
