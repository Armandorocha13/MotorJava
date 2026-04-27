package com.motorjava.consumoihs.modelos;

import java.util.Date;

/**
 * Representa as informações básicas de uma OS extraídas da Base de Dados IHS.
 * Segue o princípio de Encapsulamento.
 */
public class InformacaoOS {
    private String nomeServico;
    private Date dataModificacao;

    public InformacaoOS(String nomeServico, Date dataModificacao) {
        this.nomeServico = nomeServico;
        this.dataModificacao = dataModificacao;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public Date getDataModificacao() {
        return dataModificacao;
    }

    public void setDataModificacao(Date dataModificacao) {
        this.dataModificacao = dataModificacao;
    }
}
