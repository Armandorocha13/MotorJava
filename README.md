# AXIS CONTROL - MOTOR JAVA v4.2

O **AXIS CONTROL** é uma aplicação desktop nativa desenvolvida em Java para automação de relatórios e processamento de dados via macros Excel (VBA). Esta versão (v4.x) marca a migração completa de uma arquitetura Web/API para uma aplicação local autossuficiente, garantindo maior estabilidade, segurança e performance.

## 🚀 Principais Funcionalidades

- **Dashboard Nativo**: Interface gráfica moderna construída com Java Swing e FlatLaf.
- **Página de Entrada (Splash)**: Tela de boas-vindas com branding exclusivo (Fonte Barrio).
- **Automação VBA Silenciosa**: Execução de macros Excel via biblioteca Jacob com supressão total de alertas.
- **Backup Inteligente**: Geração automática de cópias de segurança com timestamp antes de cada processamento.
- **Limpeza de Conflitos**: Rotina automática para deletar nomes de intervalos corrompidos ou em conflito no Excel (`_FilterDatabase`).
- **Design Premium**: Interface minimalista com tema "Light Mode" (Branco e Preto) e cantos arredondados.

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 17 (OpenJDK).
- **Interface (GUI)**: Swing + FlatLaf (Look and Feel).
- **Automação**: Jacob (Java COM Bridge) para integração com MS Excel.
- **Gerenciamento**: Apache Maven 3.9.9.
- **Tipografia**: Barrio (Google Fonts) para identidade visual.

## 📂 Estrutura de Pastas

- `backend/`: Código fonte Java da aplicação.
- `tools/`: Binários locais do JDK e Maven.
- `prompts/`: Histórico de comandos e instruções utilizadas durante o desenvolvimento.
- `CHANGELOG.md`: Registro detalhado de todas as modificações realizadas.

## 🔌 Como Iniciar

Basta executar o arquivo:
`INICIAR_MOTOR.bat` na raiz do projeto.

---
*AXIS - O centro da automação.*
