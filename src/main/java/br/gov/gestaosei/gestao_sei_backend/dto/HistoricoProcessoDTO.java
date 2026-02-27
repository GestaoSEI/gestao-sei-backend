package br.gov.gestaosei.gestao_sei_backend.dto;

import br.gov.gestaosei.gestao_sei_backend.model.HistoricoProcesso;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoricoProcessoDTO {
    private Long id;
    private LocalDateTime dataAtualizacao;
    private String usuarioLogin;
    private String statusAnterior;
    private String statusNovo;
    private String origem;
    private String unidadeAtual;
    private String observacaoDaMudanca;

    public HistoricoProcessoDTO(HistoricoProcesso historico) {
        this.id = historico.getId();
        this.dataAtualizacao = historico.getDataAtualizacao();
        this.usuarioLogin = historico.getUsuario().getLogin();
        this.statusAnterior = historico.getStatusAnterior();
        this.statusNovo = historico.getStatusNovo();
        this.origem = historico.getUnidadeAnterior() != null ? historico.getUnidadeAnterior() : historico.getProcesso().getOrigem();
        this.unidadeAtual = historico.getUnidadeNova() != null ? historico.getUnidadeNova() : historico.getProcesso().getUnidadeAtual();
        this.observacaoDaMudanca = historico.getObservacaoDaMudanca();
    }
}
