package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.HistoricoProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoFiltroDTO;
import br.gov.gestaosei.gestao_sei_backend.model.HistoricoProcesso;
import br.gov.gestaosei.gestao_sei_backend.model.Processo;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import br.gov.gestaosei.gestao_sei_backend.repository.HistoricoProcessoRepository;
import br.gov.gestaosei.gestao_sei_backend.repository.ProcessoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

        LocalDate hoje = LocalDate.now();

        // Parte de todos e aplica cada filtro de forma acumulativa
        List<Processo> processos = processoRepository.findAll();

        if (filtro.getStatus() != null && !filtro.getStatus().isBlank()) {
            processos = processos.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus().equalsIgnoreCase(filtro.getStatus()))
                    .collect(Collectors.toList());
        }

        if (filtro.getUnidadeAtual() != null && !filtro.getUnidadeAtual().isBlank()) {
            String unidadeLower = filtro.getUnidadeAtual().toLowerCase();
            processos = processos.stream()
                    .filter(p -> p.getUnidadeAtual() != null && p.getUnidadeAtual().toLowerCase().contains(unidadeLower))
                    .collect(Collectors.toList());
        }

        if (Boolean.TRUE.equals(filtro.getPrazoExpirado())) {
            processos = processos.stream()
                    .filter(p -> p.getDataPrazoFinal() != null && p.getDataPrazoFinal().isBefore(hoje))
                    .collect(Collectors.toList());
        }

        if (filtro.getDataInicio() != null) {
            processos = processos.stream()
                    .filter(p -> p.getDataPrazoFinal() != null && !p.getDataPrazoFinal().isBefore(filtro.getDataInicio()))
                    .collect(Collectors.toList());
        }

        if (filtro.getDataFim() != null) {
            processos = processos.stream()
                    .filter(p -> p.getDataPrazoFinal() != null && !p.getDataPrazoFinal().isAfter(filtro.getDataFim()))
                    .collect(Collectors.toList());
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

        return atualizarProcessoExistente(processoExistente, processoDTO);
    }

    @Override
    @Transactional
    public ProcessoDTO atualizarPorNumero(String numeroProcesso, ProcessoDTO processoDTO) {
        Processo processoExistente = processoRepository.findByNumeroProcesso(numeroProcesso)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o número: " + numeroProcesso));

        return atualizarProcessoExistente(processoExistente, processoDTO);
    }

    private ProcessoDTO atualizarProcessoExistente(Processo processoExistente, ProcessoDTO processoDTO) {
        // Guarda os valores antigos para comparação
        String statusAnterior = processoExistente.getStatus();
        String unidadeAnterior = processoExistente.getUnidadeAtual();

        // Atualiza o processo com os novos dados
        // Preserva o ID original
        Long idOriginal = processoExistente.getId();
        BeanUtils.copyProperties(processoDTO, processoExistente, "id");
        processoExistente.setId(idOriginal);
        
        Processo processoAtualizado = processoRepository.save(processoExistente);

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

    @Override
    @Transactional
    public void deletarPorNumero(String numeroProcesso) {
        Processo processo = processoRepository.findByNumeroProcesso(numeroProcesso)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o número: " + numeroProcesso));
        processoRepository.delete(processo);
    }

    private ProcessoDTO toDTO(Processo processo) {
        ProcessoDTO dto = new ProcessoDTO();
        BeanUtils.copyProperties(processo, dto);
        
        // Calcula o alerta de prazo apenas para processos "Em andamento"
        if (processo.getDataPrazoFinal() != null && isStatusAtivo(processo.getStatus())) {
            long diasParaVencer = ChronoUnit.DAYS.between(LocalDate.now(), processo.getDataPrazoFinal());
            // Alerta se vencer em 5 dias ou menos (incluindo vencidos)
            dto.setAlertaUrgencia(diasParaVencer <= 5);
        } else {
            dto.setAlertaUrgencia(false);
        }
        
        return dto;
    }

    private boolean isStatusAtivo(String status) {
        if (status == null) return false;
        return "Em andamento".equalsIgnoreCase(status);
    }

    private Processo toEntity(ProcessoDTO dto) {
        Processo processo = new Processo();
        BeanUtils.copyProperties(dto, processo);
        return processo;
    }
}
