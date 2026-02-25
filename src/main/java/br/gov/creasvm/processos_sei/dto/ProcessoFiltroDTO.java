package br.gov.creasvm.processos_sei.dto;

import java.time.LocalDate;

public class ProcessoFiltroDTO {
    private String status;
    private String unidadeAtual;
    private Boolean prazoExpirado;
    private LocalDate dataInicio;
    private LocalDate dataFim;

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

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
}
