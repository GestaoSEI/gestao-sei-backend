package br.gov.creasvm.processos_sei.controller;

import br.gov.creasvm.processos_sei.dto.HistoricoProcessoDTO;
import br.gov.creasvm.processos_sei.dto.ProcessoDTO;
import br.gov.creasvm.processos_sei.dto.ProcessoFiltroDTO;
import br.gov.creasvm.processos_sei.service.ProcessoService;
import br.gov.creasvm.processos_sei.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Processos", description = "API para gerenciamento de processos SEI")
@RestController
@RequestMapping("/api/processos")
public class ProcessoController {

    private final ProcessoService processoService;
    private final RelatorioService relatorioService;

    @Autowired
    public ProcessoController(ProcessoService processoService, RelatorioService relatorioService) {
        this.processoService = processoService;
        this.relatorioService = relatorioService;
    }

    @Operation(summary = "Listar todos os processos",
            description = "Retorna uma lista de todos os processos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de processos retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ProcessoDTO>> listarTodos() {
        return ResponseEntity.ok(processoService.listarTodos());
    }

    @Operation(summary = "Buscar processo por ID",
            description = "Retorna um único processo pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Processo encontrado")
    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ProcessoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(processoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar processo por número",
            description = "Retorna um processo pelo seu número")
    @ApiResponse(responseCode = "200", description = "Processo encontrado")
    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    @GetMapping("/numero/{numeroProcesso}")
    public ResponseEntity<ProcessoDTO> buscarPorNumero(
            @Parameter(description = "Número do processo a ser buscado") @PathVariable String numeroProcesso) {
        return ResponseEntity.ok(processoService.buscarPorNumero(numeroProcesso));
    }

    @Operation(summary = "Criar novo processo",
            description = "Cria um novo processo no sistema")
    @ApiResponse(responseCode = "201", description = "Processo criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    @PostMapping
    public ResponseEntity<ProcessoDTO> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do processo a ser criado",
                    required = true)
            @Valid @RequestBody ProcessoDTO processoDTO) {
        ProcessoDTO processoCriado = processoService.salvar(processoDTO);
        return new ResponseEntity<>(processoCriado, HttpStatus.CREATED);
    }

    @Operation(summary = "Atualizar processo por ID",
            description = "Atualiza um processo existente pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Processo atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<ProcessoDTO> atualizar(
            @Parameter(description = "ID do processo a ser atualizado") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados atualizados do processo",
                    required = true)
            @Valid @RequestBody ProcessoDTO processoDTO) {
        return ResponseEntity.ok(processoService.atualizar(id, processoDTO));
    }

    @Operation(summary = "Excluir processo",
            description = "Remove um processo do sistema pelo seu ID")
    @ApiResponse(responseCode = "204", description = "Processo excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do processo a ser excluído") @PathVariable Long id) {
        processoService.deletar(id);
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

        ProcessoFiltroDTO filtro = new ProcessoFiltroDTO();
        filtro.setStatus(status);
        filtro.setUnidadeAtual(unidade);
        filtro.setPrazoExpirado(prazoExpirado);
        filtro.setDataInicio(dataInicio);
        filtro.setDataFim(dataFim);

        return ResponseEntity.ok(processoService.filtrar(filtro));
    }

    @Operation(summary = "Buscar processos por palavra-chave",
            description = "Busca processos que contenham a palavra-chave em qualquer campo (número, tipo, origem, unidade, status)")
    @GetMapping("/busca")
    public ResponseEntity<List<ProcessoDTO>> buscarPorPalavraChave(
            @Parameter(description = "Palavra-chave para busca") @RequestParam String keyword) {
        return ResponseEntity.ok(processoService.buscarPorPalavraChave(keyword));
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
                ProcessoFiltroDTO filtro = new ProcessoFiltroDTO();
                filtro.setStatus(status);
                filtro.setUnidadeAtual(unidade);
                filtro.setPrazoExpirado(prazoExpirado);
                filtro.setDataInicio(dataInicio);
                filtro.setDataFim(dataFim);
                processos = processoService.filtrar(filtro);
            }

            byte[] pdfBytes = relatorioService.gerarRelatorioProcessos(processos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "relatorio_processos.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (JRException | FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Consultar histórico do processo",
            description = "Retorna o histórico de alterações de um processo específico")
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoProcessoDTO>> consultarHistorico(
            @Parameter(description = "ID do processo") @PathVariable Long id) {
        return ResponseEntity.ok(processoService.getHistoricoPorProcessoId(id));
    }
}
