package br.gov.gestaosei.gestao_sei_backend.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StartupRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupRunner.class);

    private final AgendamentoService agendamentoService;

    public StartupRunner(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            agendamentoService.verificarProcessosVencidos();
        } catch (RuntimeException e) {
            LOGGER.error("Falha ao atualizar status vencidos na inicializacao", e);
        }
    }
}
