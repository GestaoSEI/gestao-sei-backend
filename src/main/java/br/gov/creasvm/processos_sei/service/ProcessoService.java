package br.gov.creasvm.processos_sei.service;

import br.gov.creasvm.processos_sei.dto.ProcessoDTO;
import br.gov.creasvm.processos_sei.dto.ProcessoFiltroDTO;

import java.util.List;

public interface ProcessoService {
    List<ProcessoDTO> listarTodos();
    ProcessoDTO buscarPorId(Long id);
    ProcessoDTO buscarPorNumero(String numeroProcesso);
    ProcessoDTO salvar(ProcessoDTO processoDTO);
    ProcessoDTO atualizar(Long id, ProcessoDTO processoDTO);
    List<ProcessoDTO> filtrar(ProcessoFiltroDTO filtro);
    List<ProcessoDTO> buscarPorPalavraChave(String keyword);

    void deletar(Long id);
}
