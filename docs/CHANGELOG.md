# Changelog - AXIS CONTROL

Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

## [4.2.0] - 2026-04-19 (Sessão Atual)

### Adicionado
- **Interface Nativa**: Implementação do `ModernDashboard.java` em Swing.
- **Branding**: Integração da fonte **Barrio** para o logo AXIS.
- **Landing Page**: Tela de entrada estilizada com botão de acesso.
- **Sidebar Slim**: Barra lateral de navegação com ícones de traçado minimalista.
- **Tema Light**: Interface branca com detalhes em preto para visual contemporâneo.
- **Auto-Maximização**: A aplicação agora inicia maximizada.
- **Pasta de Prompts**: Criada pasta para histórico de instruções.

### Modificado
- **Lógica de Excel**: Refinada para incluir limpeza agressiva de `Names` (Conflito de Nomes).
- **Segurança**: Gitignore atualizado para proteger `database.properties` e caminhos locais.
- **Texto**: Legenda da home alterada para "O centro da automação".
- **StatusBar**: Indicador de sistema simplificado para "ONLINE".

### Removido
- Antigo frontend em React (Vite/Node.js).
- Servidor HTTP (LocalServer) e APIs Rest.
- Dependência de navegador para operação.

## [4.1.0] - 2026-04-15 a 18
- Migração inicial para Java local.
- Configuração do pacote Jacob para automação VBA.
- Criação do script `INICIAR_MOTOR.bat`.
