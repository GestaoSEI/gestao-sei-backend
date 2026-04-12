package br.gov.gestaosei.gestao_sei_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado do processamento de importação CSV")
public class ImportacaoResultadoDTO {

    @Schema(description = "Quantidade de processos importados com sucesso")
    private int importados;

    @Schema(description = "Quantidade de processos marcados como duplicata (número já existia no sistema)")
    private int duplicatas;

    @Schema(description = "Quantidade de linhas com erro de processamento")
    private int erros;

    @Schema(description = "Mensagens descritivas dos erros ocorridos por linha")
    private List<String> mensagensErro;

    public ImportacaoResultadoDTO(int importados, int duplicatas, int erros, List<String> mensagensErro) {
        this.importados = importados;
        this.duplicatas = duplicatas;
        this.erros = erros;
        this.mensagensErro = mensagensErro;
    }

    public int getImportados() {
        return importados;
    }

    public int getDuplicatas() {
        return duplicatas;
    }

    public int getErros() {
        return erros;
    }

    public List<String> getMensagensErro() {
        return mensagensErro;
    }
}
