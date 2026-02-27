package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.HistoricoProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoFiltroDTO;

import java.util.List;

public interface ProcessoService {
    List<ProcessoDTO> listarTodos();
    ProcessoDTO buscarPorId(Long id);
    ProcessoDTO buscarPorNumero(String numeroProcesso);
    ProcessoDTO salvar(ProcessoDTO processoDTO);
    ProcessoDTO atualizar(Long id, ProcessoDTO processoDTO);
    List<ProcessoDTO> filtrar(ProcessoFiltroDTO filtro);
    List<ProcessoDTO> buscarPorPalavraChave(String keyword);
    List<HistoricoProcessoDTO> getHistoricoPorProcessoId(Long processoId);

    void deletar(Long id);
}
