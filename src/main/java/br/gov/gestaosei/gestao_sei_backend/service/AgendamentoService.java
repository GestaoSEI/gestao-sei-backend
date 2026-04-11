package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.model.Processo;
import br.gov.gestaosei.gestao_sei_backend.repository.ProcessoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AgendamentoService {

    private final ProcessoRepository processoRepository;

    public AgendamentoService(ProcessoRepository processoRepository) {
        this.processoRepository = processoRepository;
    }

    // Executa todos os dias à meia-noite (00:00)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void verificarProcessosVencidos() {
        LocalDate hoje = LocalDate.now();
        
        // Busca processos vencidos que ainda não estão com status final
        List<Processo> processosVencidos = processoRepository.findByDataPrazoFinalBefore(hoje);

        for (Processo processo : processosVencidos) {
            if (!isStatusFinal(processo.getStatus())) {
                processo.setStatus("Expirado");
                processoRepository.save(processo);
                System.out.println("Processo " + processo.getNumeroProcesso() + " atualizado para EXPIRADO automaticamente.");
            }
        }
    }

    private boolean isStatusFinal(String status) {
        if (status == null) return false;
        return "Concluído".equalsIgnoreCase(status) || 
               "Encerrado".equalsIgnoreCase(status) || 
               "Expirado".equalsIgnoreCase(status);
    }
}
