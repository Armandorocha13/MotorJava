# Histórico de Prompts - Sessão 19/04/2026

Este documento registra as instruções principais fornecidas pelo usuário para a transformação do projeto AXIS.

## 🛠️ Evolução da Sessão

1. **Migração Nativa**: "E se pegarmos esse projeto e fizermos dele uma aplicação local? Local que eu digo e sem ser no navegador. Igual fizemos com a automação rnc."
2. **Correção de Automação**: "Deu aquela mensagem de escolher o nome, quero que ele abra o arquivo excel, rode a macro, salve e feche o arquivo."
3. **Refinamento de Design**: "Melhore o design da aplicação, tente deixar o mais moderno possível. O mais parecido com o axis do react. Tire o aspecto de quadrado use um border radius para suavizar."
4. **Arquitetura de Navegação**: "Quando a aplicação abrir, já tem que abrir maximizado. Cria um sidebar para selecionar o relatório... Cria uma página de início com o botão para acessar igual ao react tinha."
5. **Simplificação Visual**: "No sidebar apenas o ícone dos módulos que temos... e melhore eles também, deixando para representar os ícones apenas com traçamentos."
6. **Branding e Fontes**: "Utiliza essa fonte [Barrio] para escrever o AXIS na tela de início... coloque a fonte na raiz do projeto mova para o lugar certo."
7. **Inversão de Cores**: "Troque a cor do projeto para branco com detalhes em preto, e fontes também."
8. **Novo Módulo**: "GIRO DE MAQUINÁRIOS... troque o ícone do módulo de maquinário para uma engrenagem."
9. **Sincronização de Fluxo**: "Copiar seriais é a primeira etapa, configurações e a segunda e atualizar última... O conteúdo desse arquivo [configMaquinarios] vai copiar e colar na aba CONFIGURAÇÃO."
10. **Consolidação de Tipografia**: "coloquei Quicksand... utiliza ela nas fontes do projeto. Mas mantenha a fonte estilosa da pagina inicial utilizada na palavras AXIS."

## 📌 Decisões de Design e Arquitetura
- Utilização de **FlatLaf** (Tema Claro).
- Layout Dinâmico: Uso de `GridLayout(1, 3)` para garantir visibilidade total dos cards em diferentes resoluções.
- Tipografia Híbrida: **Barrio** para branding e **Quicksand** para legibilidade e interface.
- Estabilidade de Macro: Seleção explícita de células e abas no Excel via Jacob para evitar falhas de foco do VBA.
- Resiliência: Inclusão manual de JDK e Maven nas configurações de ambiente internas da aplicação.
