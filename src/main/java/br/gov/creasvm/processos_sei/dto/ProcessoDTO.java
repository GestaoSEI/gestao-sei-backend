package br.gov.creasvm.processos_sei.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProcessoDTO {
    @Schema(description = "ID único do processo", example = "1")
    private Long id;

    @Schema(description = "Número único do processo", example = "12345/2023", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O número do processo é obrigatório!")
    private String numeroProcesso;

    @Schema(description = "Tipo do processo", example = "Administrativo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O tipo do processo é obrigatório!")
    private String tipoProcesso;

    @Schema(description = "Origem do processo", example = "Protocolo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "A origem do processo é obrigatória!")
    private String origem;

    @Schema(description = "Unidade atual do processo", example = "Setor de Protocolo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "A unidade atual é obrigatória!")
    private String unidadeAtual;

    @Schema(description = "Status atual do processo", example = "Em andamento", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O status do processo é obrigatório!")
    private String status;

    @Schema(description = "Data do prazo final", example = "2023-12-31", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "A data de prazo final é obrigatória!")
    private LocalDate dataPrazoFinal;

    //Getters e Setters
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
