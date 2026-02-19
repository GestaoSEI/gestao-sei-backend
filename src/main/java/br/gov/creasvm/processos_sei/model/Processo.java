package br.gov.creasvm.processos_sei.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "processos")
public class Processo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroProcesso;

    @Column(nullable = false)
    private String tipoProcesso;

    @Column(nullable = false)
    private String origem;

    @Column(nullable = false)
    private String unidadeAtual;

    @Column(nullable = false)
    private String status;

    @Column(name = "data_prazo_final")
    private LocalDate dataPrazoFinal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(String tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getUnidadeAtual() {
        return unidadeAtual;
    }

    public void setUnidadeAtual(String unidadeAtual) {
        this.unidadeAtual = unidadeAtual;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDataPrazoFinal() {
        return dataPrazoFinal;
    }

    public void setDataPrazoFinal(LocalDate dataPrazoFinal) {
        this.dataPrazoFinal = dataPrazoFinal;
    }
}