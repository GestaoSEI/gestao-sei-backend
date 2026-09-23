package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.model.Processo;
import br.gov.gestaosei.gestao_sei_backend.model.StatusProcesso;
import br.gov.gestaosei.gestao_sei_backend.repository.ProcessoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AgendamentoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgendamentoService.class);

    private final ProcessoRepository processoRepository;

    public AgendamentoService(ProcessoRepository processoRepository) {
        this.processoRepository = processoRepository;
    }

    // Executa todos os dias à meia-noite (00:00)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void verificarProcessosVencidos() {
        atualizarStatusFluxoPrazo();
    }

    @Transactional
    public void atualizarStatusFluxoPrazo() {
        LocalDate hoje = LocalDate.now();

        // Recalcula o status apenas dos processos que participam do fluxo de prazo
        List<Processo> processos = processoRepository.findByStatusInAndDataPrazoFinalIsNotNull(
                List.copyOf(StatusProcesso.STATUS_FLUXO_PRAZO)
        );

        for (Processo processo : processos) {
            String statusAtualizado = calcularStatusAutomatico(processo, hoje);
            if (statusAtualizado != null && !statusAtualizado.equalsIgnoreCase(processo.getStatus())) {
                processo.setStatus(statusAtualizado);
                processoRepository.save(processo);
                LOGGER.info("Processo {} atualizado para {} automaticamente", processo.getNumeroProcesso(), statusAtualizado.toUpperCase());
            }
        }
    }

    private String calcularStatusAutomatico(Processo processo, LocalDate hoje) {
        if (processo == null || processo.getDataPrazoFinal() == null) {
            return null;
        }

        String statusAtual = normalizarStatus(processo.getStatus());
        LOGGER.debug("Processo: {} | Status atual: {} | Data prazo: {} | Hoje: {}", processo.getNumeroProcesso(), statusAtual, processo.getDataPrazoFinal(), hoje);
        
        if (isStatusFinal(statusAtual)) {
            LOGGER.debug("Status é final, não recalcula");
            return null;
        }

        if (!isStatusFluxoPrazo(statusAtual)) {
            LOGGER.debug("Status não está no fluxo de prazo");
            return null;
        }

        if (processo.getDataPrazoFinal().isBefore(hoje)) {
            LOGGER.debug("Prazo expirado; retornando EXPIRADO");
            return StatusProcesso.EXPIRADO;
        }

        long diasParaVencer = ChronoUnit.DAYS.between(hoje, processo.getDataPrazoFinal());
        if (diasParaVencer <= 5) {
            LOGGER.debug("Faltam {} dias; retornando PRAZO_PROXIMO", diasParaVencer);
            return StatusProcesso.PRAZO_PROXIMO;
        }

        LOGGER.debug("Faltam {} dias; retornando EM_ANDAMENTO", diasParaVencer);
        return StatusProcesso.EM_ANDAMENTO;
    }

    private boolean isStatusFinal(String status) {
        return status != null && StatusProcesso.STATUS_FINAIS.stream().anyMatch(status::equalsIgnoreCase);
    }

    private boolean isStatusFluxoPrazo(String status) {
        return status != null && StatusProcesso.STATUS_FLUXO_PRAZO.stream().anyMatch(status::equalsIgnoreCase);
    }

    private String normalizarStatus(String status) {
        if (status == null) {
            return null;
        }
        String valor = status.trim();
        String valorLower = valor.toLowerCase();
        if (valorLower.startsWith("prazo")) {
            return StatusProcesso.PRAZO_PROXIMO;
        }
        if (valorLower.startsWith("expirado")) {
            return StatusProcesso.EXPIRADO;
        }
        if (valorLower.startsWith("conclus") || valorLower.startsWith("conclu")) {
            return StatusProcesso.CONCLUIDO;
        }
        if (valorLower.startsWith("encerrado")) {
            return StatusProcesso.ENCERRADO;
        }
        if (valorLower.startsWith("encaminh") || valorLower.startsWith("aguard") || valorLower.startsWith("em")) {
            return StatusProcesso.EM_ANDAMENTO;
        }
        return valor;
    }

    public String recalcularStatusSeNecessario(Processo processo) {
        LocalDate hoje = LocalDate.now();
        return calcularStatusAutomatico(processo, hoje);
    }

    public String recalcularStatusQuandoDataMuda(Processo processo) {
        return recalcularStatusSeNecessario(processo);
    }
}
