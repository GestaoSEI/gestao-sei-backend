package br.gov.creasvm.processos_sei.service;

import br.gov.creasvm.processos_sei.dto.HistoricoProcessoDTO;
import br.gov.creasvm.processos_sei.dto.ProcessoDTO;
import br.gov.creasvm.processos_sei.dto.ProcessoFiltroDTO;
import br.gov.creasvm.processos_sei.model.HistoricoProcesso;
import br.gov.creasvm.processos_sei.model.Processo;
import br.gov.creasvm.processos_sei.model.Usuario;
import br.gov.creasvm.processos_sei.repository.HistoricoProcessoRepository;
import br.gov.creasvm.processos_sei.repository.ProcessoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProcessoServiceImpl implements ProcessoService {

    private final ProcessoRepository processoRepository;
    private final HistoricoProcessoRepository historicoProcessoRepository;

    public ProcessoServiceImpl(ProcessoRepository processoRepository, HistoricoProcessoRepository historicoProcessoRepository) {
        this.processoRepository = processoRepository;
        this.historicoProcessoRepository = historicoProcessoRepository;
    }

    @Override
    public List<ProcessoDTO> filtrar(ProcessoFiltroDTO filtro) {
        if (filtro == null) {
            return listarTodos();
        }

        List<Processo> processos;
        LocalDate hoje = LocalDate.now();

        if (filtro.getDataInicio() != null || filtro.getDataFim() != null) {
            processos = processoRepository.findByUnidadeAndPrazoBetween(
                    filtro.getUnidadeAtual(),
                    filtro.getDataInicio(),
                    filtro.getDataFim()
            );
        } else if (filtro.getStatus() != null && filtro.getUnidadeAtual() != null) {
            processos = processoRepository.findByStatusAndUnidadeAtual(
                    filtro.getStatus(),
                    filtro.getUnidadeAtual()
            );
        } else if (filtro.getStatus() != null) {
            processos = processoRepository.findByStatus(filtro.getStatus());
        } else if (filtro.getUnidadeAtual() != null) {
            processos = processoRepository.findByUnidadeAtual(filtro.getUnidadeAtual());
        } else if (Boolean.TRUE.equals(filtro.getPrazoExpirado())) {
            processos = processoRepository.findByDataPrazoFinalBefore(hoje);
        } else {
            processos = processoRepository.findAll();
        }

        return processos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessoDTO> buscarPorPalavraChave(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return listarTodos();
        }
        return processoRepository.searchByKeyword(keyword).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricoProcessoDTO> getHistoricoPorProcessoId(Long processoId) {
        return historicoProcessoRepository.findByProcessoIdOrderByDataAtualizacaoDesc(processoId)
                .stream()
                .map(HistoricoProcessoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessoDTO> listarTodos() {
        return processoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessoDTO buscarPorId(Long id) {
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o ID: " + id));
        return toDTO(processo);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessoDTO buscarPorNumero(String numeroProcesso) {
        Processo processo = processoRepository.findByNumeroProcesso(numeroProcesso)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o número: " + numeroProcesso));
        return toDTO(processo);
    }

    @Override
    @Transactional
    public ProcessoDTO salvar(ProcessoDTO processoDTO) {
        Processo processo = toEntity(processoDTO);
        processo = processoRepository.save(processo);
        return toDTO(processo);
    }

    @Override
    @Transactional
    public ProcessoDTO atualizar(Long id, ProcessoDTO processoDTO) {
        Processo processoExistente = processoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o ID: " + id));

        // Guarda os valores antigos para comparação
        String statusAnterior = processoExistente.getStatus();
        String unidadeAnterior = processoExistente.getUnidadeAtual();

        // Atualiza o processo com os novos dados
        Processo processoAtualizado = toEntity(processoDTO);
        processoAtualizado.setId(processoExistente.getId());
        processoRepository.save(processoAtualizado);

        // Obtém o usuário logado
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Compara e registra o histórico
        boolean statusMudou = !Objects.equals(statusAnterior, processoAtualizado.getStatus());
        boolean unidadeMudou = !Objects.equals(unidadeAnterior, processoAtualizado.getUnidadeAtual());

        if (statusMudou || unidadeMudou) {
            HistoricoProcesso historico = new HistoricoProcesso(
                    processoAtualizado,
                    usuarioLogado,
                    statusMudou ? statusAnterior : null,
                    statusMudou ? processoAtualizado.getStatus() : null,
                    unidadeMudou ? unidadeAnterior : null,
                    unidadeMudou ? processoAtualizado.getUnidadeAtual() : null,
                    processoDTO.getObservacao() // Usamos a observação do DTO como a "observação da mudança"
            );
            historicoProcessoRepository.save(historico);
        }

        return toDTO(processoAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!processoRepository.existsById(id)) {
            throw new EntityNotFoundException("Processo não encontrado com o ID: " + id);
        }
        processoRepository.deleteById(id);
    }

    private ProcessoDTO toDTO(Processo processo) {
        ProcessoDTO dto = new ProcessoDTO();
        BeanUtils.copyProperties(processo, dto);
        return dto;
    }

    private Processo toEntity(ProcessoDTO dto) {
        Processo processo = new Processo();
        BeanUtils.copyProperties(dto, processo);
        return processo;
    }
}
