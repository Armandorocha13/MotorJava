# 🔗 CADEIAS DE CONEXÃO ODBC - POWER BI

## 📋 CADEIA DE CONEXÃO PRINCIPAL

Use esta cadeia de conexão no Power BI quando ele pedir:

```
Driver={MySQL ODBC 9.6 Unicode Driver};Server=localhost;Port=3306;Database=vivo_aging;User=root;Password=;Option=3;
```

---

## 🎯 COMO USAR NO POWER BI

### Método 1: Via Cadeia de Conexão (Avançado)

1. Abra o **Power BI Desktop**
2. Clique em **"Obter Dados"** > **"Mais..."**
3. Digite **"ODBC"** na busca
4. Selecione **"ODBC"** e clique em **"Conectar"**
5. Selecione a opção: **"Avançado"**
6. No campo **"Cadeia de conexão"**, cole:
   ```
   Driver={MySQL ODBC 9.6 Unicode Driver};Server=localhost;Port=3306;Database=vivo_aging;User=root;Password=;Option=3;
   ```
7. Clique em **"OK"**
8. Se pedir credenciais:
   - Selecione **"Padrão ou Personalizado"**
   - Nome de usuário: `root`
   - Senha: (deixe em branco)
   - Clique em **"Conectar"**
9. Na janela "Navegador":
   - Expanda **"vivo_aging"**
   - Marque ☑️ **"vw_powerbi_equipamentos"**
   - Clique em **"Carregar"**

---

### Método 2: Via DSN (Mais Fácil) ✅ RECOMENDADO

Se você já configurou o DSN `vivo_aging_odbc`:

1. Abra o **Power BI Desktop**
2. Clique em **"Obter Dados"** > **"Mais..."**
3. Digite **"ODBC"** e selecione **"ODBC"**
4. Clique em **"Conectar"**
5. Selecione: **"DSN (Nome da Fonte de Dados)"**
6. No dropdown, escolha: **`vivo_aging_odbc`**
7. Clique em **"OK"**
8. Na janela "Navegador":
   - Expanda **"vivo_aging"**
   - Marque ☑️ **"vw_powerbi_equipamentos"**
   - Clique em **"Carregar"**

---

## 🔧 VARIAÇÕES DA CADEIA DE CONEXÃO

### Com Senha (se você configurou senha no MySQL):

```
Driver={MySQL ODBC 9.6 Unicode Driver};Server=localhost;Port=3306;Database=vivo_aging;User=root;Password=SUA_SENHA_AQUI;Option=3;
```

### Com IP Específico (se não for localhost):

```
Driver={MySQL ODBC 9.6 Unicode Driver};Server=192.168.1.100;Port=3306;Database=vivo_aging;User=root;Password=;Option=3;
```

### Com Porta Diferente (se mudou a porta padrão):

```
Driver={MySQL ODBC 9.6 Unicode Driver};Server=localhost;Port=3307;Database=vivo_aging;User=root;Password=;Option=3;
```

---

## 📝 EXPLICAÇÃO DOS PARÂMETROS

| Parâmetro | Valor | Descrição |
|-----------|-------|-----------|
| `Driver` | `{MySQL ODBC 9.6 Unicode Driver}` | Driver ODBC instalado no seu sistema |
| `Server` | `localhost` | Endereço do servidor MySQL |
| `Port` | `3306` | Porta do MySQL (padrão) |
| `Database` | `vivo_aging` | Nome do banco de dados |
| `User` | `root` | Usuário do MySQL |
| `Password` | (vazio) | Senha do MySQL (em branco se não tiver) |
| `Option` | `3` | Opções de conexão (3 = padrão recomendado) |

---

## 🆘 PROBLEMAS COMUNS

### ❌ "Driver não encontrado"

**Erro:**
```
[Microsoft][ODBC Driver Manager] Data source name not found and no default driver specified
```

**Solução:**
Verifique o nome exato do driver instalado:
```powershell
Get-OdbcDriver | Where-Object {$_.Name -like "*MySQL*"}
```

Use o nome exato que aparecer (ex: `MySQL ODBC 9.6 Unicode Driver`)

---

### ❌ "Access denied for user 'root'@'localhost'"

**Solução 1:** Senha incorreta
- Se você configurou senha no MySQL, adicione na cadeia:
  ```
  Password=SUA_SENHA;
  ```

**Solução 2:** Testar no terminal
```powershell
c:\xampp\mysql\bin\mysql.exe -u root -p vivo_aging
```

---

### ❌ "Can't connect to MySQL server on 'localhost'"

**Solução:**
1. Abra o **XAMPP Control Panel**
2. Verifique se o MySQL está **"Running"** (verde)
3. Se não estiver, clique em **"Start"**

---

### ❌ "Unknown database 'vivo_aging'"

**Solução:**
Verifique se o banco existe:
```powershell
c:\xampp\mysql\bin\mysql.exe -u root -e "SHOW DATABASES;"
```

Se não aparecer `vivo_aging`, você precisa importar os dados primeiro.

---

## ✅ TESTAR CONEXÃO ODBC

Antes de usar no Power BI, teste a conexão:

### Via PowerShell:
```powershell
$connectionString = "Driver={MySQL ODBC 9.6 Unicode Driver};Server=localhost;Port=3306;Database=vivo_aging;User=root;Password=;Option=3;"
$connection = New-Object System.Data.Odbc.OdbcConnection($connectionString)
try {
    $connection.Open()
    Write-Host "✅ Conexão bem-sucedida!" -ForegroundColor Green
    $connection.Close()
} catch {
    Write-Host "❌ Erro: $($_.Exception.Message)" -ForegroundColor Red
}
```

### Via Administrador ODBC:
1. Pressione **Win + R** e digite: `odbcad32`
2. Vá na aba **"DSN do Sistema"**
3. Selecione **`vivo_aging_odbc`**
4. Clique em **"Configurar"**
5. Clique em **"Test"**
6. Deve aparecer: **"Connection successful"** ✅

---

## 📚 REFERÊNCIAS

- **Driver instalado:** MySQL ODBC 9.6 Unicode Driver (64-bit)
- **Servidor:** localhost:3306
- **Banco:** vivo_aging
- **View:** vw_powerbi_equipamentos

---

**Criado em:** 13/02/2026  
**Sistema:** VIVO Aging - Equipamentos Serializados
