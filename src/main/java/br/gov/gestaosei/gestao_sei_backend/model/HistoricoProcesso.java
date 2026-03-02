package br.gov.gestaosei.gestao_sei_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_processos")
public class HistoricoProcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    private String statusAnterior;
    private String statusNovo;

    private String unidadeAnterior;
    private String unidadeNova;

    @Column(columnDefinition = "TEXT")
    private String observacaoDaMudanca;

    public HistoricoProcesso() {
        // Construtor padrão
    }

    public HistoricoProcesso(Processo processo, Usuario usuario, String statusAnterior, String statusNovo, String unidadeAnterior, String unidadeNova, String observacaoDaMudanca) {
        this.processo = processo;
        this.usuario = usuario;
        this.dataAtualizacao = LocalDateTime.now();
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.unidadeAnterior = unidadeAnterior;
        this.unidadeNova = unidadeNova;
        this.observacaoDaMudanca = observacaoDaMudanca;
    }

    // Getters e Setters manuais
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(String statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public String getStatusNovo() {
        return statusNovo;
    }

    public void setStatusNovo(String statusNovo) {
        this.statusNovo = statusNovo;
    }

    public String getUnidadeAnterior() {
        return unidadeAnterior;
    }

    public void setUnidadeAnterior(String unidadeAnterior) {
        this.unidadeAnterior = unidadeAnterior;
    }

    public String getUnidadeNova() {
        return unidadeNova;
    }

    public void setUnidadeNova(String unidadeNova) {
        this.unidadeNova = unidadeNova;
    }

    public String getObservacaoDaMudanca() {
        return observacaoDaMudanca;
    }

    public void setObservacaoDaMudanca(String observacaoDaMudanca) {
        this.observacaoDaMudanca = observacaoDaMudanca;
    }
}
