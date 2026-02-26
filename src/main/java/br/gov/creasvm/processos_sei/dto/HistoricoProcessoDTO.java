package br.gov.creasvm.processos_sei.dto;

import br.gov.creasvm.processos_sei.model.HistoricoProcesso;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoricoProcessoDTO {
    private Long id;
    private LocalDateTime dataAtualizacao;
    private String usuarioLogin;
    private String statusAnterior;
    private String statusNovo;
    private String unidadeAnterior;
    private String unidadeNova;
    private String observacaoDaMudanca;

    public HistoricoProcessoDTO(HistoricoProcesso historico) {
        this.id = historico.getId();
        this.dataAtualizacao = historico.getDataAtualizacao();
        this.usuarioLogin = historico.getUsuario().getLogin();
        this.statusAnterior = historico.getStatusAnterior();
        this.statusNovo = historico.getStatusNovo();
        this.unidadeAnterior = historico.getUnidadeAnterior();
        this.unidadeNova = historico.getUnidadeNova();
        this.observacaoDaMudanca = historico.getObservacaoDaMudanca();
    }
}
