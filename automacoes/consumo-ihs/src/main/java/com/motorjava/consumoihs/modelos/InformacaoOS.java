package com.motorjava.consumoihs.modelos;

import java.util.Date;

/**
 * Representa as informações básicas de uma OS extraídas da Base de Dados IHS.
 */
public class InformacaoOS {
    public String nomeServico;
    public Date dataModificacao;

    public InformacaoOS(String nomeServico, Date dataModificacao) {
        this.nomeServico = nomeServico;
        this.dataModificacao = dataModificacao;
    }
}
