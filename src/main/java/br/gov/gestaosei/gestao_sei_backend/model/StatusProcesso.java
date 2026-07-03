package br.gov.gestaosei.gestao_sei_backend.model;

import java.util.List;
import java.util.Set;

public final class StatusProcesso {

    public static final String EM_ANDAMENTO = "Em andamento";
    public static final String PRAZO_PROXIMO = "Prazo próximo";
    public static final String EXPIRADO = "Expirado";
    public static final String CONCLUIDO = "Concluído";
    public static final String ENCERRADO = "Encerrado";
    public static final String RESPONDIDO = "Respondido";

    public static final List<String> STATUS_FLUXO_PRAZO = List.of(EM_ANDAMENTO, PRAZO_PROXIMO);
    public static final Set<String> STATUS_FINAIS = Set.of(CONCLUIDO, ENCERRADO, EXPIRADO);

    private StatusProcesso() {
    }
}
