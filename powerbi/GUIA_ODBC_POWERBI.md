# 🔌 GUIA RÁPIDO: CONFIGURAR ODBC MYSQL PARA POWER BI

## ⏱️ Tempo: 5 minutos

---

## 📋 PASSO 1: INSTALAR MYSQL ODBC DRIVER

### 1.1 - Download

1. Acesse: **https://dev.mysql.com/downloads/connector/odbc/**
2. Escolha: **"Windows (x86, 64-bit), MSI Installer"**
3. Clique em **"Download"**
4. Na próxima página: **"No thanks, just start my download"**

### 1.2 - Instalar

1. Execute o arquivo baixado: `mysql-connector-odbc-x.x.xx-winx64.msi`
2. Clique em **"Next"**
3. Selecione **"Typical"**
4. Clique em **"Install"**
5. Aguarde a instalação
6. Clique em **"Finish"**

---

## 🎯 PASSO 2: CONFIGURAR DSN (DATA SOURCE NAME)

### 2.1 - Abrir Administrador ODBC

1. Pressione **Win + R**
2. Digite: `odbcad32`
3. Pressione **Enter**

**Importante:** Isso abrirá o "Administrador de Fonte de Dados ODBC"

### 2.2 - Criar Novo DSN do Sistema

1. Clique na aba **"DSN do Sistema"**
2. Clique em **"Adicionar..."**
3. Na lista, selecione: **"MySQL ODBC 8.x Unicode Driver"**
   - Se tiver várias versões, escolha a mais recente (8.0 ou superior)
4. Clique em **"Concluir"**

### 2.3 - Configurar Conexão

Preencha os campos conforme abaixo:

```
┌─────────────────────────────────────────────┐
│ MySQL Connector/ODBC Data Source Configuration │
├─────────────────────────────────────────────┤
│                                             │
│ Data Source Name: vivo_aging_odbc          │
│ Description: Conexão VIVO Aging Equipamentos│
│                                             │
│ TCP/IP Server: localhost                   │
│ Port: 3306                                 │
│                                             │
│ User: root                                 │
│ Password: (deixe em branco)                │
│                                             │
│ Database: vivo_aging                       │
│                                             │
└─────────────────────────────────────────────┘
```

**Detalhes dos campos:**

- **Data Source Name:** `vivo_aging_odbc` (nome que você usará no Power BI)
- **Description:** `Conexão VIVO Aging Equipamentos` (descrição opcional)
- **TCP/IP Server:** `localhost` (servidor local)
- **Port:** `3306` (porta padrão do MySQL)
- **User:** `root` (usuário do MySQL)
- **Password:** deixe em branco (se não tiver senha configurada)
- **Database:** `vivo_aging` (nome do banco de dados)

### 2.4 - Testar Conexão

1. Clique no botão **"Test"**
2. Aguarde alguns segundos
3. Deve aparecer: **"Connection successful"** ✅

**Se der erro:**
- Verifique se o XAMPP está rodando
- Verifique se o MySQL está ativo no XAMPP
- Confirme o nome do banco: `vivo_aging`

### 2.5 - Salvar

1. Clique em **"OK"** (na janela de configuração)
2. Clique em **"OK"** novamente (no Administrador ODBC)

---

## 🔌 PASSO 3: CONECTAR POWER BI VIA ODBC

### 3.1 - Abrir Power BI Desktop

1. Abra o **Power BI Desktop**
2. Feche a tela de boas-vindas

### 3.2 - Obter Dados via ODBC

1. Clique em **"Obter Dados"** > **"Mais..."**
2. Digite **"ODBC"** na caixa de pesquisa
3. Selecione **"ODBC"**
4. Clique em **"Conectar"**

### 3.3 - Selecionar DSN

1. Na janela "De ODBC":
   - Selecione a opção: **"DSN (Nome da Fonte de Dados)"**
   - No dropdown, escolha: **"vivo_aging_odbc"**
2. Clique em **"OK"**

### 3.4 - Autenticar (se necessário)

Se pedir credenciais:

1. Selecione: **"Padrão ou Personalizado"**
2. Nome de usuário: `root`
3. Senha: (deixe em branco)
4. Clique em **"Conectar"**

### 3.5 - Selecionar Tabela/View

1. Na janela **"Navegador"**:
   - Expanda **"vivo_aging"**
   - Expanda **"Tables"** (ou **"Views"**)
   - Marque ☑️ **"vw_powerbi_equipamentos"**
2. Clique em **"Carregar"**

**Pronto!** 🎉 Dados carregados via ODBC.

---

## ✅ VANTAGENS DO ODBC

| Aspecto | ODBC | Conexão Direta MySQL |
|---------|------|---------------------|
| **Estabilidade** | ✅ Mais estável | ⚠️ Pode dar problemas |
| **Compatibilidade** | ✅ Funciona em todas versões | ⚠️ Depende da versão |
| **Configuração** | ✅ Reutilizável | ❌ Precisa reconfigurar |
| **Performance** | ✅ Otimizado | ✅ Otimizado |
| **Segurança** | ✅ Credenciais salvas no Windows | ⚠️ Precisa digitar sempre |

---

## 🆘 PROBLEMAS COMUNS

### ❌ "Driver não encontrado"

**Solução:**
- Reinstale o MySQL ODBC Driver
- Certifique-se de baixar a versão **64-bit**
- Reinicie o Power BI após instalar

### ❌ "DSN não aparece na lista"

**Solução:**
- Certifique-se de criar um **"DSN do Sistema"** (não "DSN do Usuário")
- Execute o `odbcad32` como Administrador
- Reinicie o Power BI

### ❌ "Connection failed"

**Solução:**
1. Abra o XAMPP Control Panel
2. Verifique se MySQL está **"Running"** (verde)
3. Clique em **"Admin"** do MySQL para abrir phpMyAdmin
4. Confirme que o banco `vivo_aging` existe

### ❌ "Access denied for user 'root'"

**Solução:**
- Se você configurou senha no MySQL, coloque a senha no campo **"Password"**
- Teste a conexão no terminal:
  ```powershell
  c:\xampp\mysql\bin\mysql.exe -u root -p vivo_aging
  ```

---

## 🔄 ATUALIZAR DADOS NO POWER BI

Após conectar via ODBC, para atualizar os dados:

1. No Power BI, pressione **F5**
2. Ou clique em **"Atualizar"** na faixa de opções
3. Os dados serão recarregados automaticamente do MySQL

---

## 📝 NOTAS IMPORTANTES

1. **DSN do Sistema vs DSN do Usuário:**
   - Use sempre **"DSN do Sistema"** para que funcione para todos os usuários do Windows

2. **Porta 3306:**
   - É a porta padrão do MySQL
   - Se você mudou a porta no XAMPP, use a porta correta

3. **Segurança:**
   - Em produção, sempre use senha para o usuário `root`
   - Considere criar um usuário específico para o Power BI

4. **Performance:**
   - A view `vw_powerbi_equipamentos` já está otimizada
   - Os índices criados no script SQL melhoram a velocidade

---

## ✅ CHECKLIST

- [ ] MySQL ODBC Driver instalado (64-bit)
- [ ] DSN do Sistema criado com nome `vivo_aging_odbc`
- [ ] Teste de conexão bem-sucedido
- [ ] Power BI conectado via ODBC
- [ ] View `vw_powerbi_equipamentos` carregada
- [ ] Dados visíveis no painel "Dados" do Power BI

---

**Criado em:** Fevereiro 2026  
**Sistema:** VIVO Aging - Equipamentos Serializados  
**Versão:** 1.0
