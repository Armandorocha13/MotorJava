from flask import Flask, request, jsonify
from flask_cors import CORS
import pandas as pd
import openpyxl
import os
import datetime
import re

app = Flask(__name__)
CORS(app)

EXCEL_FILES_PATH = r"C:\Users\mando\OneDrive\Área de Trabalho\planilhas"

if not os.path.exists(EXCEL_FILES_PATH):
    os.makedirs(EXCEL_FILES_PATH)

def get_df(filename):
    if not filename.endswith('.xlsx'):
        filename += '.xlsx'
    path = os.path.join(EXCEL_FILES_PATH, filename)
    if not os.path.exists(path):
        raise FileNotFoundError(f"Arquivo {filename} não encontrado.")
    return pd.read_excel(path), path

@app.route('/api/excel/execute', methods=['POST'])
def execute_command():
    data = request.json
    command = data.get('command', '').lower().strip()
    
    try:
        # --- INTELIGÊNCIA DE CRIAÇÃO ---
        # Detecta: cria/criar/novo arquivo/planilha [nome] com as colunas [col1, col2...]
        if any(x in command for x in ['cria', 'criar', 'novo', 'gerar']):
            # Extrair nome do arquivo (procura por algo terminado em .xlsx ou a primeira palavra após o verbo)
            match_file = re.search(r'([a-zA-Z0-9_-]+\.xlsx)', command)
            filename = match_file.group(1) if match_file else "nova_planilha.xlsx"
            
            # Extrair colunas (procura por "colunas", "campos" ou "cabecalhos")
            cols = ['Data', 'Descricao', 'Valor'] # Default
            if 'colunas' in command or 'campos' in command:
                cols_part = re.split(r'colunas|campos', command)[-1].replace(' e ', ',').strip()
                cols = [c.strip().capitalize() for c in cols_part.split(',') if c.strip()]

            filepath = os.path.join(EXCEL_FILES_PATH, filename)
            df = pd.DataFrame(columns=cols)
            df.to_excel(filepath, index=False)
            return jsonify({"success": True, "msg": f"Sucesso! Arquivo '{filename}' criado com as colunas: {', '.join(cols)}"})

        # --- COMANDOS DE ANÁLISE ---
        elif 'listar' in command:
            files = [f for f in os.listdir(EXCEL_FILES_PATH) if f.endswith('.xlsx')]
            return jsonify({"success": True, "msg": f"Arquivos na pasta: {', '.join(files) if files else 'Vazio'}"})

        elif 'resumo' in command:
            filename = command.split(' ')[-1]
            df, _ = get_df(filename)
            return jsonify({"success": True, "msg": f"Resumo de {filename}:\n{df.describe().to_string()}"})

        elif 'colunas' in command:
            filename = command.split(' ')[-1]
            df, _ = get_df(filename)
            return jsonify({"success": True, "msg": f"Colunas: {', '.join(df.columns)}"})

        elif 'limpar' in command:
            filename = command.split(' ')[-1]
            df, path = get_df(filename)
            df.dropna().to_excel(path, index=False)
            return jsonify({"success": True, "msg": f"Arquivo {filename} limpo (linhas nulas removidas)."})

        elif 'ajuda' in command or 'help' in command:
            return jsonify({"success": True, "msg": "Comandos: Criar [nome] colunas [A, B], Listar, Resumo [arquivo], Colunas [arquivo], Limpar [arquivo]"})

        else:
            return jsonify({"success": False, "msg": "Comando não compreendido. Tente: 'criar arquivo vendas.xlsx com as colunas produto, valor'"})

    except Exception as e:
        return jsonify({"success": False, "msg": f"Erro: {str(e)}"})

if __name__ == '__main__':
    print(f"Servidor Excel Plus Inteligente Ativo na porta 5001")
    app.run(port=5001)
