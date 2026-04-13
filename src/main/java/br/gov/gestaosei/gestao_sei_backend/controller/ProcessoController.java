package br.gov.gestaosei.gestao_sei_backend.controller;

import br.gov.gestaosei.gestao_sei_backend.dto.HistoricoProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ImportacaoResultadoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoFiltroDTO;
import br.gov.gestaosei.gestao_sei_backend.service.ProcessoService;
import br.gov.gestaosei.gestao_sei_backend.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/processos")
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "false")
public class ProcessoController {

    private final ProcessoService processoService;
    private final RelatorioService relatorioService;

    @Autowired
    public ProcessoController(ProcessoService processoService, RelatorioService relatorioService) {
        this.processoService = processoService;
        this.relatorioService = relatorioService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os processos",
            description = "Retorna uma lista de todos os processos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de processos retornada com sucesso")
    public ResponseEntity<List<ProcessoDTO>> listarTodos() {
        return ResponseEntity.ok(processoService.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Criar novo processo",
            description = "Cria um novo processo no sistema")
    @ApiResponse(responseCode = "201", description = "Processo criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    public ResponseEntity<ProcessoDTO> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do processo a ser criado",
                    required = true)
            @Valid @RequestBody ProcessoDTO processoDTO) {
        ProcessoDTO processoCriado = processoService.salvar(processoDTO);
        return new ResponseEntity<>(processoCriado, HttpStatus.CREATED);
    }

    @PutMapping("/atualizar")
    @Operation(summary = "Atualizar processo por número",
            description = "Atualiza um processo existente buscando pelo seu número (passado como parâmetro)")
    @ApiResponse(responseCode = "200", description = "Processo atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    public ResponseEntity<ProcessoDTO> atualizarPorNumero(
            @Parameter(description = "Número do processo a ser atualizado") @RequestParam String numero,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados atualizados do processo",
                    required = true)
            @Valid @RequestBody ProcessoDTO processoDTO) {
        return ResponseEntity.ok(processoService.atualizarPorNumero(numero, processoDTO));
    }

    @DeleteMapping("/excluir")
    @Operation(summary = "Excluir processo por número",
            description = "Remove um processo do sistema pelo seu número (passado como parâmetro)")
    @ApiResponse(responseCode = "204", description = "Processo excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    public ResponseEntity<Void> deletarPorNumero(
            @Parameter(description = "Número do processo a ser excluído") @RequestParam String numero) {
        processoService.deletarPorNumero(numero);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrar processos",
            description = "Filtra processos por status, unidade, prazo expirado e/ou intervalo de datas")
    @GetMapping("/filtro")
    public ResponseEntity<List<ProcessoDTO>> filtrar(
            @Parameter(description = "Status do processo") @RequestParam(required = false) String status,
            @Parameter(description = "Unidade atual do processo") @RequestParam(required = false) String unidade,
            @Parameter(description = "Apenas processos com prazo expirado") @RequestParam(required = false) Boolean prazoExpirado,
            @Parameter(description = "Data inicial do prazo final") @RequestParam(required = false) LocalDate dataInicio,
            @Parameter(description = "Data final do prazo final") @RequestParam(required = false) LocalDate dataFim) {

        ProcessoFiltroDTO filtro = new ProcessoFiltroDTO(status, unidade, prazoExpirado, dataInicio, dataFim);
        return ResponseEntity.ok(processoService.filtrar(filtro));
    }

    @GetMapping("/busca")
    @Operation(summary = "Buscar processos por palavra-chave",
            description = "Busca processos que contenham a palavra-chave no número, tipo, origem, unidade, status ou observação")
    @ApiResponse(responseCode = "200", description = "Processos encontrados com sucesso")
    public ResponseEntity<List<ProcessoDTO>> buscarPorPalavraChave(
            @Parameter(description = "Palavra-chave para busca") @RequestParam String keyword) {
        return ResponseEntity.ok(processoService.buscarPorPalavraChave(keyword));
    }

    @GetMapping("/historico/{processoId}")
    @Operation(summary = "Obter histórico de um processo",
            description = "Retorna o histórico de alterações de um processo específico")
    @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso")
    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    public ResponseEntity<List<HistoricoProcessoDTO>> getHistoricoPorProcessoId(
            @Parameter(description = "ID do processo") @PathVariable Long processoId) {
        return ResponseEntity.ok(processoService.getHistoricoPorProcessoId(processoId));
    }

    @Operation(summary = "Importar processos via CSV",
            description = "Importa processos de um arquivo CSV no formato do modelo. Linhas com número já existente são marcadas como duplicata para revisão. Coluna obrigatória: NumeroProcesso, TipoProcesso, Origem, UnidadeAtual, Status, DataPrazoFinal (AAAA-MM-DD). Observacao é opcional.")
    @ApiResponse(responseCode = "200", description = "Importação processada — verifique o resultado para detalhes")
    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportacaoResultadoDTO> importarCsv(
            @Parameter(description = "Arquivo CSV com os processos a importar") @RequestParam("file") MultipartFile file) {
        try {
            ImportacaoResultadoDTO resultado = processoService.importarCsv(file);
            return ResponseEntity.ok(resultado);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Gerar relatório PDF",
            description = "Gera um relatório PDF com base nos filtros ou palavra-chave fornecidos")
    @GetMapping("/relatorio")
    public ResponseEntity<byte[]> gerarRelatorio(
            @Parameter(description = "Status do processo") @RequestParam(required = false) String status,
            @Parameter(description = "Unidade atual do processo") @RequestParam(required = false) String unidade,
            @Parameter(description = "Apenas processos com prazo expirado") @RequestParam(required = false) Boolean prazoExpirado,
            @Parameter(description = "Data inicial do prazo final") @RequestParam(required = false) LocalDate dataInicio,
            @Parameter(description = "Data final do prazo final") @RequestParam(required = false) LocalDate dataFim,
            @Parameter(description = "Palavra-chave para busca") @RequestParam(required = false) String keyword) {

        try {
            List<ProcessoDTO> processos;

            if (keyword != null && !keyword.isEmpty()) {
                processos = processoService.buscarPorPalavraChave(keyword);
            } else {
                ProcessoFiltroDTO filtro = new ProcessoFiltroDTO(status, unidade, prazoExpirado, dataInicio, dataFim);
                processos = processoService.filtrar(filtro);
            }

            processos.sort(
                    Comparator.comparing(ProcessoDTO::getDataPrazoFinal,
                                    Comparator.nullsLast(Comparator.reverseOrder()))
            );

            byte[] pdfBytes = relatorioService.gerarRelatorioProcessos(processos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Alterado para "attachment" para forçar o download
            headers.setContentDispositionFormData("attachment", "relatorio_processos.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (JRException | FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
