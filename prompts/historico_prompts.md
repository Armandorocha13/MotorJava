# Histórico de Prompts - Evolução AXIS

Este documento registra as instruções principais fornecidas pelo usuário para a transformação do projeto AXIS.

## 🛠️ Evolução da Sessão (Recente)

... (itens anteriores mantidos) ...

18. **Modularização**: "Organize a estrutura de pastas do projeto, otimize e garante que tudo funcione perfeitamente."
19. **Separação de Automações**: "separe as automacoes, em uma pasta chamada automacoes e com as pastas para cada tipo de automacao de relatorio dentro."
20. **Dark Mode Premium**: "Troque a cor do projeto para preto com detalhes em branco" (Refletido na v5.0).
21. **Integração IHS**: "coloque o consumo ihs finalizado dentro do nosso projeo... criar um modulo novo com icone e tudo mais."
22. **Design Minimalista**: "remove o console do axis no modulo consumo ihs... centraliza os botoes dos cards e exiba barra de carregamento individual."

## 📌 Decisões de Design e Arquitetura (v5.0)

- **Arquitetura Multi-módulo**: Separação clara entre `app-core` (interface) e `automation` (lógica pesada).
- **Maven Reactor**: Build centralizado que gerencia todas as dependências e módulos de uma só vez.
- **Configuração Desacoplada**: Uso de um arquivo de propriedades externo para que o usuário final possa ajustar caminhos sem recompilar o código.
- **Dark Mode Moderno**: Interface em preto profundo com elementos em branco e tons de cinza (#121212), seguindo padrões de design de apps premium.
- **Resiliência de Path**: Uso de variáveis de ambiente do Java (user.home) e caminhos relativos robustos nos scripts `.bat`.
- **Organização de Dados**: Centralização de arquivos Excel e saídas na pasta `data/`, evitando poluição do diretório de código.

---
*Este histórico serve como base de conhecimento para futuras expansões e manutenções do sistema.*
