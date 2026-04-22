# Guia de Estudos - Motor Java (AXIS CONTROL)

Este documento lista os principais conceitos, tecnologias e padrões de arquitetura utilizados nesta aplicação. Se você deseja aprender como construir ferramentas de automação profissional com interface desktop, este é o roteiro ideal.

---

## 1. Linguagem e Ambiente
*   **Java 17 (OpenJDK)**: Uso das funcionalidades modernas do Java para backend e lógica de processamento.
*   **Maven**: Gerenciamento de dependências e automação de build.
*   **Programação Orientada a Objetos (POO)**: Organização do código em classes, interfaces e separação de responsabilidades (GUI, Service, Core, Config).

## 2. Interface Gráfica (GUI)
*   **Java Swing**: Framework nativo do Java para construção de interfaces desktop.
*   **FlatLaf**: Biblioteca de "Look and Feel" que moderniza o Swing, permitindo temas como Dark Mode e Light Mode com design premium.
*   **Tipografia Customizada**: Integração de fontes externas (Google Fonts como *Quicksand* e *Barrio*) diretamente no projeto Java.
*   **Design UX/UI**: Criação de layouts responsivos, dashboards com cards, e feedback visual para o usuário.

## 3. Automação e Integração
*   **JACOB (Java COM Bridge)**: Tecnologia avançada para permitir que o Java "fale" com o Microsoft Excel via API COM do Windows.
*   **VBA (Visual Basic for Applications)**: Criação e execução de macros dentro do Excel para processamento pesado de dados.
*   **Execução Silenciosa**: Técnicas para rodar processos em segundo plano sem alertas ou janelas intrusivas para o usuário.

## 4. Persistência e Dados
*   **MySQL / JDBC**: Conexão com banco de dados relacional para armazenamento e consulta de informações.
*   **Manipulação de Arquivos (I/O)**: Leitura e escrita de arquivos de configuração, logs e arquivos externos para sincronização com planilhas.
*   **Sincronização de Dados**: Lógica para ler dados de uma fonte e escrever em posições específicas dentro de um arquivo Excel.

## 5. Web e Comunicação
*   **Serviços HTTP**: Consumo de APIs externas para integração com outros sistemas ou serviços web.
*   **Monitoramento**: Implementação de mecanismos de "heartbeat" para verificar a saúde do backend.

## 6. Scripts e Ferramentas do Windows
*   **Arquivos .BAT**: Automação da inicialização do ambiente Java e da aplicação.
*   **Arquivos .VBS**: Execução de scripts silenciosos para melhorar a experiência do usuário (evitando telas pretas de terminal).
*   **Backup Estruturado**: Lógica de segurança para duplicar arquivos críticos antes de operações de escrita.

---

## Sugestão de Ordem de Estudo:
1.  **Fundamentos de Java** (Sintaxe, Classes, Métodos).
2.  **Swing Básico** (Janelas, Botões, Layouts).
3.  **FlatLaf** (Temas e customização visual).
4.  **Manipulação de Arquivos e Excel** (JACOB e Macros).
5.  **Banco de Dados** (MySQL e JDBC).
6.  **Arquitetura** (Como separar o código em camadas).

---
*Este projeto é um exemplo prático de como o Java pode ser poderoso para criar soluções que unem o mundo corporativo (Excel/VBA) com o desenvolvimento moderno de software.*
