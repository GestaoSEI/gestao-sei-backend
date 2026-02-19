package br.gov.creasvm.processos_sei.dto;

public class ProcessoFiltroDTO {
    private String status;
    private String unidadeAtual;
    private Boolean prazoExpirado;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnidadeAtual() {
        return unidadeAtual;
    }

    public void setUnidadeAtual(String unidadeAtual) {
        this.unidadeAtual = unidadeAtual;
    }

    public Boolean getPrazoExpirado() {
        return prazoExpirado;
    }

    public void setPrazoExpirado(Boolean prazoExpirado) {
        this.prazoExpirado = prazoExpirado;
    }
}