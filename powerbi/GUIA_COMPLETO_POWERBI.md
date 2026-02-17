# 📊 GUIA COMPLETO: POWER BI PARA INICIANTES
## Sistema de Equipamentos Serializados - VIVO SP

---

## 🎯 O QUE VOCÊ VAI CRIAR

Um dashboard profissional no Power BI mostrando:
- **Equipamentos por técnico e supervisor**
- **Aging de equipamentos** (quantos dias está parado)
- **Gráficos e indicadores** (KPIs)
- **Filtros interativocd s**

**Tempo total:** 30 minutos  
**Nível:** Iniciante (nunca mexeu com Power BI)

---

## 📋 PRÉ-REQUISITOS

Antes de começar, você precisa ter:
- ✅ MySQL rodando (XAMPP)
- ✅ Banco de dados `vivo_aging` com dados
- ✅ Windows 10/11

---

## 🚀 PASSO 1: PREPARAR O BANCO DE DADOS (5 minutos)

### 1.1 - Executar o script SQL

Abra o **PowerShell** e execute:

```powershell
# Ir para a pasta
cd "C:\Users\user\Desktop\MotorJava\powerbi"

# Executar o script
Get-Content "SETUP_COMPLETO_POWERBI.sql" | c:\xampp\mysql\bin\mysql.exe -u root vivo_aging
```

**O que esse script faz:**
- Calcula quantos dias cada equipamento está com o técnico
- Classifica em: Normal (0-7 dias), Atenção (7-14 dias), Crítico (>14 dias)
- Cria índices para o Power BI funcionar rápido
- Cria uma view otimizada

**Resultado esperado:**
```
✅ Etapa 1/4: Dias em estoque calculados
✅ Etapa 2/4: Status aging calculado
✅ Etapa 3/4: Índices criados
✅ Etapa 4/4: View criada
✅ BANCO PRONTO PARA POWER BI!
```

---

## 💻 PASSO 2: INSTALAR O POWER BI DESKTOP (10 minutos)

### 2.1 - Baixar o Power BI

1. Acesse: **https://powerbi.microsoft.com/pt-br/desktop/**
2. Clique em **"Baixar Grátis"** ou **"Download"**
3. Aguarde o download (cerca de 500 MB)

### 2.2 - Instalar

1. Execute o arquivo baixado (`PBIDesktopSetup_x64.exe`)
2. Clique em **"Next"** > **"Next"** > **"Install"**
3. Aguarde a instalação (3-5 minutos)
4. Clique em **"Finish"**

### 2.3 - Instalar MySQL Connector/ODBC

**IMPORTANTE:** O Power BI precisa deste driver ODBC para conectar ao MySQL.

1. Acesse: **https://dev.mysql.com/downloads/connector/odbc/**
2. Escolha: **"Windows (x86, 64-bit), MSI Installer"**
3. Clique em **"Download"**
4. Na próxima página, clique em **"No thanks, just start my download"**
5. Execute o arquivo baixado (`mysql-connector-odbc-x.x.xx-winx64.msi`)
6. Clique em **"Next"** > **"Typical"** > **"Install"**
7. Clique em **"Finish"**

### 2.4 - Configurar DSN ODBC (Fonte de Dados)

**O que é DSN?** É um "atalho" que o Power BI usa para conectar ao MySQL.

1. Pressione **Win + R** e digite: `odbcad32`
2. Clique em **"OK"** (abrirá o Administrador de Fonte de Dados ODBC)
3. Vá na aba **"DSN do Sistema"**
4. Clique em **"Adicionar..."**
5. Selecione **"MySQL ODBC 8.x Unicode Driver"** (ou versão mais recente)
6. Clique em **"Concluir"**

**Configurar a conexão:**

```
Data Source Name: vivo_aging_odbc
Description: Conexão VIVO Aging Equipamentos
TCP/IP Server: localhost
Port: 3306
User: root
Password: (deixe em branco se não tiver senha)
Database: vivo_aging
```

7. Clique em **"Test"** para testar a conexão
8. Se aparecer **"Connection successful"**, clique em **"OK"**
9. Clique em **"OK"** novamente para salvar o DSN

---

## 🔌 PASSO 3: CONECTAR O POWER BI AO MYSQL (5 minutos)

### 3.1 - Abrir o Power BI

1. Abra o **Power BI Desktop** (ícone amarelo no menu Iniciar)
2. Feche a tela de boas-vindas (se aparecer)

### 3.2 - Escolher Método de Conexão

Existem **2 formas** de conectar. Escolha uma:

---

#### **OPÇÃO A: Conexão via ODBC (RECOMENDADO)** ✅

Mais estável e compatível com todas as versões do Power BI.

1. Clique em **"Obter Dados"** > **"Mais..."**
2. Digite **"ODBC"** na busca
3. Selecione **"ODBC"**
4. Clique em **"Conectar"**

**Configurar ODBC:**

5. Na janela "De ODBC":
   - Selecione **"DSN (Nome da Fonte de Dados)"**
   - Escolha **"vivo_aging_odbc"** (que criamos no Passo 2.4)
   - Clique em **"OK"**

6. Se pedir credenciais:
   - Selecione **"Padrão ou Personalizado"**
   - Nome de usuário: `root`
   - Senha: (deixe em branco)
   - Clique em **"Conectar"**

7. Na janela "Navegador":
   - Expanda **"vivo_aging"** > **"Tables"**
   - Marque ☑️ **"vw_powerbi_equipamentos"**
   - Clique em **"Carregar"**

---

#### **OPÇÃO B: Conexão Direta MySQL** (Alternativa)

1. Clique em **"Obter Dados"** > **"Mais..."**
2. Digite **"MySQL"** na busca
3. Selecione **"Banco de Dados MySQL"**
4. Clique em **"Conectar"**

**Configurar Conexão:**

5. Na janela "Banco de Dados MySQL":
   ```
   Servidor: localhost
   Banco de dados: vivo_aging
   ```
6. Clique em **"Opções Avançadas"** (seta para baixo)
7. No campo **"Instrução SQL"**, cole:
   ```sql
   SELECT * FROM vw_powerbi_equipamentos
   ```
8. Clique em **"OK"**

9. Na janela de credenciais:
   - Selecione **"Banco de dados"**
   - Nome de usuário: `root`
   - Senha: (deixe em branco)
   - Clique em **"Conectar"**

10. Aguarde carregar e clique em **"Carregar"**

---

### 3.3 - Verificar Dados Carregados

Após carregar:

1. No painel **"Dados"** (direita), você verá a tabela **"vw_powerbi_equipamentos"**
2. Clique na seta para expandir e ver as colunas:
   - `numero_serie`
   - `nome_tecnico`
   - `supervisor`
   - `coordenador`
   - `dias_estoque`
   - `status_aging`
   - E outras...

**Pronto!** Os dados estão no Power BI.

---

## 📊 PASSO 4: CRIAR SEU PRIMEIRO RELATÓRIO (10 minutos)

### 4.1 - Criar Tabela de Equipamentos

1. No painel direito, clique no ícone de **"Tabela"** (parece uma grade)
2. Arraste para ocupar a maior parte da tela
3. No painel **"Dados"** (direita), marque:
   - ☑️ supervisor
   - ☑️ nome_tecnico
   - ☑️ numero_serie
   - ☑️ dias_estoque
   - ☑️ status_aging

### 4.2 - Adicionar Formatação Condicional

1. Selecione a tabela (clique nela)
2. No painel **"Visualizações"** (direita), clique no ícone de **pincel** (Formatar)
3. Expanda **"Valores"**
4. Encontre **"dias_estoque"**
5. Ative **"Formatação condicional"** > **"Cores de fundo"**
6. Configure:
   - **Estilo:** Regras
   - **Regra 1:** Se valor **> 14** → Cor: **Vermelho claro** (#FFCDD2)
   - **Regra 2:** Se valor **> 7** → Cor: **Amarelo claro** (#FFF9C4)
   - **Senão:** Cor: **Verde claro** (#C8E6C9)

### 4.3 - Criar Cartões KPI

**Cartão 1: Total de Equipamentos**

1. Clique em área vazia do canvas
2. Selecione visual **"Cartão"** (ícone com "123")
3. Posicione no topo esquerdo
4. No painel **"Dados"**, arraste **"numero_serie"** para o cartão
5. O Power BI vai contar automaticamente

**Cartão 2: Equipamentos Críticos**

1. Clique em área vazia
2. Selecione visual **"Cartão"**
3. Posicione ao lado do primeiro
4. Clique em **"Modelagem"** > **"Nova Medida"**
5. Cole este código:

```dax
Equipamentos Críticos = 
CALCULATE(
    COUNT(vw_powerbi_equipamentos[numero_serie]),
    vw_powerbi_equipamentos[dias_estoque] > 14
)
```

6. Pressione **Enter**
7. Arraste a medida **"Equipamentos Críticos"** para o cartão

**Cartão 3: Média de Dias**

1. Novo cartão
2. Nova medida:

```dax
Média Dias = AVERAGE(vw_powerbi_equipamentos[dias_estoque])
```

3. Arraste para o cartão
4. Formate para mostrar 1 casa decimal

**Cartão 4: % Crítico**

1. Novo cartão
2. Nova medida:

```dax
% Crítico = 
DIVIDE(
    [Equipamentos Críticos],
    COUNT(vw_powerbi_equipamentos[numero_serie]),
    0
) * 100
```

3. Arraste para o cartão
4. Formate como percentual

### 4.4 - Criar Gráfico de Barras

1. Clique em área vazia
2. Selecione **"Gráfico de Barras Empilhadas Horizontais"**
3. Configure:
   - **Eixo Y:** supervisor
   - **Valores:** Criar 3 medidas:

```dax
0-7 Dias = 
CALCULATE(
    COUNT(vw_powerbi_equipamentos[numero_serie]),
    vw_powerbi_equipamentos[dias_estoque] >= 0,
    vw_powerbi_equipamentos[dias_estoque] <= 7
)

7-14 Dias = 
CALCULATE(
    COUNT(vw_powerbi_equipamentos[numero_serie]),
    vw_powerbi_equipamentos[dias_estoque] > 7,
    vw_powerbi_equipamentos[dias_estoque] <= 14
)

Acima 14 Dias = 
CALCULATE(
    COUNT(vw_powerbi_equipamentos[numero_serie]),
    vw_powerbi_equipamentos[dias_estoque] > 14
)
```

4. Arraste as 3 medidas para **"Valores"**

### 4.5 - Personalizar Cores

1. Selecione o gráfico de barras
2. Vá em **"Formatar"** > **"Cores de dados"**
3. Configure:
   - **0-7 Dias:** Verde (#28A745)
   - **7-14 Dias:** Amarelo (#FFC107)
   - **Acima 14 Dias:** Vermelho (#DC3545)

### 4.6 - Adicionar Filtros (Slicers)

1. Clique em área vazia
2. Selecione **"Segmentação de Dados"**
3. Arraste **"supervisor"** para o slicer
4. Posicione no topo da página
5. Repita para criar slicer de **"status_aging"**

---

## 💾 PASSO 5: SALVAR E FINALIZAR (2 minutos)

### 5.1 - Salvar o Arquivo

1. Clique em **"Arquivo"** > **"Salvar Como"**
2. Escolha a pasta: `C:\Users\user\Desktop\MotorJava\powerbi\`
3. Nome do arquivo: `VIVO_Aging_Equipamentos.pbix`
4. Clique em **"Salvar"**

### 5.2 - Atualizar Dados

Para atualizar os dados do MySQL:
- Pressione **F5**
- Ou clique em **"Atualizar"** na faixa de opções

---

## 🎨 DICAS DE DESIGN

### Tema de Cores VIVO

1. Vá em **"Exibição"** > **"Temas"** > **"Personalizar tema atual"**
2. Em **"Cores de dados"**, adicione:
   ```
   #660099, #FF6B00, #00A0DC, #28A745, #DC3545, #FFC107
   ```

### Título do Relatório

1. **"Inserir"** > **"Caixa de Texto"**
2. Digite: **"EQUIPAMENTOS SERIALIZADOS - VIVO SP"**
3. Formate:
   - Fonte: Segoe UI Semibold
   - Tamanho: 20pt
   - Cor: Roxo VIVO (#660099)
   - Alinhamento: Centro

---

## 🆘 PROBLEMAS COMUNS

### ❌ "Não foi possível conectar ao servidor MySQL"

**Solução:**
1. Verifique se o XAMPP está rodando
2. Abra o Painel de Controle do XAMPP
3. Certifique-se de que o MySQL está com status "Running"

### ❌ "A view 'vw_powerbi_equipamentos' não existe"

**Solução:**
Execute novamente o script SQL do PASSO 1:
```powershell
Get-Content "SETUP_COMPLETO_POWERBI.sql" | c:\xampp\mysql\bin\mysql.exe -u root vivo_aging
```

### ❌ "Credenciais inválidas"

**Solução:**
1. **Arquivo** > **Opções e Configurações** > **Configurações de Fonte de Dados**
2. Localize a conexão MySQL
3. **Editar Permissões** > **Limpar Permissões**
4. Reconecte com as credenciais corretas

### ❌ Dados não aparecem ou estão vazios

**Solução:**
Verifique se há dados no banco:
```powershell
c:\xampp\mysql\bin\mysql.exe -u root vivo_aging -e "SELECT COUNT(*) FROM vw_powerbi_equipamentos;"
```

---

## 📚 PRÓXIMOS PASSOS

Depois de criar seu primeiro relatório:

1. **Adicione mais visuais:**
   - Gráfico de pizza (distribuição por faixa)
   - Gráfico de linha (evolução ao longo do tempo)
   - Matriz (tabela dinâmica)

2. **Explore recursos avançados:**
   - Drill-through (navegação entre páginas)
   - Bookmarks (salvar visualizações)
   - Tooltips personalizados

3. **Publique no Power BI Service:**
   - Clique em **"Publicar"**
   - Faça login com conta Microsoft
   - Compartilhe com sua equipe

---

## 📞 AJUDA ADICIONAL

### Atalhos Úteis
- **Ctrl + S:** Salvar
- **F5:** Atualizar dados
- **Ctrl + T:** Obter dados
- **Ctrl + Z:** Desfazer

### Links Úteis
- **Documentação Power BI:** https://docs.microsoft.com/pt-br/power-bi/
- **Vídeos tutoriais:** https://www.youtube.com/powerbi
- **Comunidade:** https://community.powerbi.com/

---

## ✅ CHECKLIST FINAL

- [ ] Executei o script SQL (`SETUP_COMPLETO_POWERBI.sql`)
- [ ] Instalei o Power BI Desktop
- [ ] Instalei o MySQL Connector/ODBC
- [ ] Conectei o Power BI ao MySQL
- [ ] Importei a view `vw_powerbi_equipamentos`
- [ ] Criei a tabela de equipamentos
- [ ] Criei 4 cartões KPI
- [ ] Criei o gráfico de barras
- [ ] Adicionei formatação condicional
- [ ] Adicionei filtros (slicers)
- [ ] Salvei o arquivo .pbix

---

**Parabéns! 🎉**

Você criou seu primeiro dashboard no Power BI!

**Criado em:** Fevereiro 2026  
**Sistema:** VIVO Aging - Equipamentos Serializados  
**Versão:** 1.0 - Simplificada
