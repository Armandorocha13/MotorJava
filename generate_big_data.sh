#!/bin/bash
FILE="input_files/aniel_heavy.csv"
echo "Relatorio Massivo Aniel;;" > $FILE
echo "Gerado em: $(date +%d/%m/%Y);;" >> $FILE
echo "DATA_REF;GESTORA;NOME_CLIENTE;VALOR" >> $FILE
EMPRESAS=("ffa" "outra" "xyz" "aniel_corp")
for i in {1..10000}
do
    ANO=$(( ( RANDOM % 30 ) + 2010 )) # Range mais realista para o teste
    MES=$(( ( RANDOM % 12 ) + 1 ))
    DIA=$(( ( RANDOM % 28 ) + 1 ))
    GESTORA=${EMPRESAS[$(( RANDOM % 4 ))]}
    printf "%02d/%02d/%d;%s;Cliente_Simulado_%d;%.2f\n" $DIA $MES $ANO $GESTORA $i $(echo "scale=2; $RANDOM/100" | bc) >> $FILE
done
echo "Arquivo $FILE gerado com sucesso!"
