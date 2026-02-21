package br.gov.creasvm.processos_sei.controller;

import br.gov.creasvm.processos_sei.dto.ProcessoDTO;
import br.gov.creasvm.processos_sei.dto.ProcessoFiltroDTO;
import br.gov.creasvm.processos_sei.service.ProcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Processos", description = "API para gerenciamento de processos SEI")
@RestController
@RequestMapping("/api/processos")
public class ProcessoController {

    private final ProcessoService processoService;

    @Autowired
    public ProcessoController(ProcessoService processoService) {
        this.processoService = processoService;
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
            description = "Filtra processos por status, unidade e/ou prazo expirado")
    @GetMapping("/filtro")
    public ResponseEntity<List<ProcessoDTO>> filtrar(
            @Parameter(description = "Status do processo") @RequestParam(required = false) String status,
            @Parameter(description = "Unidade atual do processo") @RequestParam(required = false) String unidade,
            @Parameter(description = "Apenas processos com prazo expirado") @RequestParam(required = false) Boolean prazoExpirado) {

        ProcessoFiltroDTO filtro = new ProcessoFiltroDTO();
        filtro.setStatus(status);
        filtro.setUnidadeAtual(unidade);
        filtro.setPrazoExpirado(prazoExpirado);

        return ResponseEntity.ok(processoService.filtrar(filtro));
    }

    @Operation(summary = "Buscar processos por palavra-chave",
            description = "Busca processos que contenham a palavra-chave em qualquer campo (número, tipo, origem, unidade, status)")
    @GetMapping("/busca")
    public ResponseEntity<List<ProcessoDTO>> buscarPorPalavraChave(
            @Parameter(description = "Palavra-chave para busca") @RequestParam String keyword) {
        return ResponseEntity.ok(processoService.buscarPorPalavraChave(keyword));
    }
}
