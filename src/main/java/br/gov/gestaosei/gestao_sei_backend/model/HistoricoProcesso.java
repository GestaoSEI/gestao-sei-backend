package br.gov.gestaosei.gestao_sei_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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
}
