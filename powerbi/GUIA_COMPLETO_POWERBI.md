# 📊 GUIA COMPLETO: POWER BI PARA INICIANTES
## Sistema de Equipamentos Serializados - VIVO SP

---

## 🎯 O QUE VOCÊ VAI CRIAR

Um dashboard profissional no Power BI mostrando:
- **Equipamentos por técnico e supervisor**
- **Aging de equipamentos** (quantos dias está parado)
- **Gráficos e indicadores** (KPIs)
- **Filtros interativos**

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

### 2.3 - Instalar MySQL Connector

**IMPORTANTE:** O Power BI precisa deste conector para falar com o MySQL.

1. Acesse: **https://dev.mysql.com/downloads/connector/odbc/**
2. Escolha: **"Windows (x86, 64-bit), MSI Installer"**
3. Clique em **"Download"**
4. Na próxima página, clique em **"No thanks, just start my download"**
5. Execute o arquivo baixado
6. Clique em **"Next"** > **"Next"** > **"Install"**
7. Clique em **"Finish"**

---

## 🔌 PASSO 3: CONECTAR O POWER BI AO MYSQL (5 minutos)

### 3.1 - Abrir o Power BI

1. Abra o **Power BI Desktop** (ícone amarelo no menu Iniciar)
2. Feche a tela de boas-vindas (se aparecer)

### 3.2 - Obter Dados

1. Na tela inicial, clique em **"Obter Dados"**
   - Ou use o menu: **Página Inicial** > **Obter Dados**
   - Ou pressione **Ctrl + T**

2. Na janela que abrir:
   - Digite **"MySQL"** na caixa de pesquisa
   - Selecione **"Banco de Dados MySQL"**
   - Clique em **"Conectar"**

### 3.3 - Configurar Conexão

Na janela "Banco de Dados MySQL":

```
Servidor: localhost
Banco de dados: vivo_aging
```

- Clique em **"OK"**

### 3.4 - Autenticar

Na janela de credenciais:

1. No lado esquerdo, selecione **"Banco de dados"**
2. Preencha:
   ```
   Nome de usuário: root
   Senha: (deixe em branco se não tiver senha)
   ```
3. Clique em **"Conectar"**

### 3.5 - Importar a View

1. Clique em **"Opções Avançadas"** (seta para baixo)
2. No campo **"Instrução SQL"**, cole:

```sql
SELECT * FROM vw_powerbi_equipamentos
```

3. Clique em **"OK"**
4. Aguarde carregar (pode levar 10-30 segundos)
5. Clique em **"Carregar"**

**Pronto!** Os dados estão no Power BI.

---

## 📊 PASSO 4: CRIAR SEU PRIMEIRO RELATÓRIO (10 minutos)

### 4.1 - Criar Tabela de Equipamentos

1. No painel direito, clique no ícone de **"Tabela"** (parece uma grade)
2. Arraste para ocupar a maior parte da tela
3. No painel **"Dados"** (direita), marque:
   - ☑️ Supervisor
   - ☑️ Nome do Técnico
   - ☑️ Número de Série
   - ☑️ Dias em Estoque
   - ☑️ Status Aging

### 4.2 - Adicionar Formatação Condicional

1. Selecione a tabela (clique nela)
2. No painel **"Visualizações"** (direita), clique no ícone de **pincel** (Formatar)
3. Expanda **"Valores"**
4. Encontre **"Dias em Estoque"**
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
4. No painel **"Dados"**, arraste **"Número de Série"** para o cartão
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
    COUNT('Query1'[Número de Série]),
    'Query1'[Dias em Estoque] > 14
)
```

6. Pressione **Enter**
7. Arraste a medida **"Equipamentos Críticos"** para o cartão

**Cartão 3: Média de Dias**

1. Novo cartão
2. Nova medida:

```dax
Média Dias = AVERAGE('Query1'[Dias em Estoque])
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
    COUNT('Query1'[Número de Série]),
    0
) * 100
```

3. Arraste para o cartão
4. Formate como percentual

### 4.4 - Criar Gráfico de Barras

1. Clique em área vazia
2. Selecione **"Gráfico de Barras Empilhadas Horizontais"**
3. Configure:
   - **Eixo Y:** Supervisor
   - **Valores:** Criar 3 medidas:

```dax
0-7 Dias = 
CALCULATE(
    COUNT('Query1'[Número de Série]),
    'Query1'[Dias em Estoque] >= 0,
    'Query1'[Dias em Estoque] <= 7
)

7-14 Dias = 
CALCULATE(
    COUNT('Query1'[Número de Série]),
    'Query1'[Dias em Estoque] > 7,
    'Query1'[Dias em Estoque] <= 14
)

Acima 14 Dias = 
CALCULATE(
    COUNT('Query1'[Número de Série]),
    'Query1'[Dias em Estoque] > 14
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
3. Arraste **"Supervisor"** para o slicer
4. Posicione no topo da página
5. Repita para criar slicer de **"Status Aging"**

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
