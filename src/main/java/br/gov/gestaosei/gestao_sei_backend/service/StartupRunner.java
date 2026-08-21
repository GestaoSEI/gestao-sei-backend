package br.gov.gestaosei.gestao_sei_backend.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

    private final AgendamentoService agendamentoService;

    public StartupRunner(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            agendamentoService.verificarProcessosVencidos();
        } catch (RuntimeException e) {
            System.err.println("Falha ao atualizar status vencidos na inicializacao: " + e.getMessage());
        }
    }
}
