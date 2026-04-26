# Guia de Estudos - Motor Java (AXIS CONTROL)

Este documento lista os principais conceitos, tecnologias e padrões de arquitetura utilizados nesta aplicação. Se você deseja aprender como construir ferramentas de automação profissional com interface desktop, este é o roteiro ideal.

---

## 1. Arquitetura Modular e Maven
*   **Maven Multi-Module**: Organização do projeto em sub-módulos específicos (`motor-nucleo`, `automacoes/giro-maquinario`, `automacoes/consumo-ihs`). Isso permite isolar a lógica de cada automação e manter o núcleo do sistema independente.
*   **POM Pai (Parent POM)**: Uso de um arquivo central para gerenciar versões de dependências (POI, FlatLaf, etc.) e a estrutura de build para todos os módulos.
*   **Gerenciamento de Dependências**: Utilização do Maven para resolver bibliotecas externas e bibliotecas internas (o núcleo depende dos módulos de automação).

## 2. Interface Gráfica (GUI) Avançada
*   **Java Swing & FlatLaf**: Uso do framework nativo com temas modernos (Dark Mode).
*   **CardLayout**: Técnica para trocar de telas dentro da mesma janela (Navegação entre Dashboard e Automações).
*   **Tipografia Dinâmica**: Carregamento de fontes `.ttf` como recursos do projeto.
*   **Thread Safety no Swing**: Uso de `SwingUtilities.invokeLater` para atualizar a interface a partir de processos em segundo plano (evitando travamentos).

## 3. Automação de Processos (RPA em Java)
*   **JACOB (Java COM Bridge)**: Integração profunda com o Microsoft Office para controlar o Excel programaticamente.
*   **Execução de Macros VBA**: Como disparar funções escritas em VBA diretamente do Java.
*   **Processos Subjacentes**: Uso de `ProcessBuilder` para disparar scripts `.bat` ou outros binários independentes.

## 4. Configuração e Portabilidade
*   **Externalized Configuration**: Uso de arquivos `.properties` fora do código para gerenciar caminhos de arquivos e parâmetros do sistema.
*   **JDK/Maven Portátil**: Como configurar um projeto para rodar em qualquer máquina sem necessidade de instalação prévia do Java no sistema.
*   **System Properties**: Uso de variáveis como `${user.home}` para criar caminhos dinâmicos que funcionam em diferentes usuários.

## 5. Manipulação de Dados e Arquivos
*   **Apache POI**: Leitura e escrita direta em arquivos `.xlsx` sem necessidade de abrir o Excel (processamento em memória).
*   **NIO.2 (java.nio.file)**: Manipulação moderna de arquivos, cópias, movimentação e backups.
*   **Logging sob demanda**: Implementação de pop-ups (`JDialog`) para exibição de logs detalhados, mantendo a interface principal limpa.
*   **Feedback em tempo real**: Uso de `SwingWorker` e `JProgressBar` para reportar o progresso de tarefas longas sem travar a UI.

---

## Sugestão de Ordem de Estudo:
1.  **Fundamentos de Java** (Sintaxe e POO).
2.  **Maven** (Estrutura de pastas e o arquivo pom.xml).
3.  **Swing & Layout Managers** (Como montar telas que não quebram ao redimensionar).
4.  **Integração COM/Jacob** (Entender como o Windows expõe o Excel para o Java).
5.  **Configurações e Propriedades** (Como tornar seu software flexível e fácil de configurar).

---
*Este projeto é um laboratório prático de Engenharia de Software aplicada a automação corporativa.*
