' ==========================================
' SCRIPT DE ATUALIZAÇÃO AUTOMÁTICA DE DADOS
' ==========================================
Option Explicit

Dim appExcel, workbook, connection
Dim caminhoArquivo
Dim fso

' CONFIGURAÇÃO DO CAMINHO
caminhoArquivo = "C:\Users\user\Desktop\MotorJava\data\EQUIPAMENTO_SERIALIZADOS_VOLANTE_SP.xlsx"

' Verifica se o arquivo existe antes de tentar abrir
Set fso = CreateObject("Scripting.FileSystemObject")
If Not fso.FileExists(caminhoArquivo) Then
    MsgBox "ERRO CRÍTICO: Arquivo não encontrado em:" & vbCrLf & caminhoArquivo, 16, "Erro VBScript"
    WScript.Quit 1
End If

' Inicia tratamento de erros
On Error Resume Next

Set appExcel = CreateObject("Excel.Application")
If Err.Number <> 0 Then
    MsgBox "Erro ao iniciar o Excel: " & Err.Description, 16, "Erro"
    WScript.Quit 1
End If

' Configurações para execução silenciosa
appExcel.Visible = False
appExcel.DisplayAlerts = False
appExcel.ScreenUpdating = False

Set workbook = appExcel.Workbooks.Open(caminhoArquivo)

If Err.Number <> 0 Then
    MsgBox "Erro ao abrir a planilha. Verifique se ela já não está aberta." & vbCrLf & "Erro: " & Err.Description, 16, "Erro"
    encerrarExcel
    WScript.Quit 1
End If

' Garante que as conexões esperem o refresh terminar (desabilita Background Refresh)
For Each connection In workbook.Connections
    If connection.Type = 1 Then ' 1 = OLEDBConnection
        connection.OLEDBConnection.BackgroundQuery = False
    End If
Next

' Atualiza Tudo
workbook.RefreshAll

' Espera o Power Query/Cálculos terminarem
appExcel.CalculateUntilAsyncQueriesDone

If Err.Number <> 0 Then
    MsgBox "Erro durante a atualização dos dados: " & Err.Description, 16, "Erro"
    workbook.Close False ' Fecha sem salvar se deu erro
else
    workbook.Save
    workbook.Close True ' Fecha salvando
End If

encerrarExcel

' ==========================================
' FUNÇÃO PARA LIMPAR MEMÓRIA
' ==========================================
Sub encerrarExcel()
    On Error Resume Next
    appExcel.Quit
    Set workbook = Nothing
    Set appExcel = Nothing
    Set fso = Nothing
End Sub