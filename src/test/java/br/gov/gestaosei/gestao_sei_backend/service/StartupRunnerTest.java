package br.gov.gestaosei.gestao_sei_backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StartupRunnerTest {

    @Mock
    private AgendamentoService agendamentoService;

    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private StartupRunner startupRunner;

    @Test
    void run_DeveChamarVerificarProcessosVencidos() {
        doNothing().when(agendamentoService).verificarProcessosVencidos();

        startupRunner.run(applicationArguments);

        verify(agendamentoService).verificarProcessosVencidos();
    }

    @Test
    void run_SeAgendamentoLancarExcecao_NaoPropaga() {
        doThrow(new RuntimeException("erro teste")).when(agendamentoService).verificarProcessosVencidos();

        // deve não propagar exceção
        startupRunner.run(applicationArguments);

        verify(agendamentoService).verificarProcessosVencidos();
    }
}

