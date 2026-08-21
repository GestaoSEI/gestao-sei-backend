package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.model.Processo;
import br.gov.gestaosei.gestao_sei_backend.model.StatusProcesso;
import br.gov.gestaosei.gestao_sei_backend.repository.ProcessoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private ProcessoRepository processoRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Processo emAndamento;
    private Processo prazoProximo;
    private Processo vencido;
    private Processo concluido;

    @BeforeEach
    void setUp() {
        emAndamento = new Processo();
        emAndamento.setNumeroProcesso("0001");
        emAndamento.setStatus(StatusProcesso.EM_ANDAMENTO);
        emAndamento.setDataPrazoFinal(LocalDate.now().plusDays(10));

        prazoProximo = new Processo();
        prazoProximo.setNumeroProcesso("0002");
        prazoProximo.setStatus(StatusProcesso.EM_ANDAMENTO);
        prazoProximo.setDataPrazoFinal(LocalDate.now().plusDays(5));

        vencido = new Processo();
        vencido.setNumeroProcesso("0003");
        vencido.setStatus(StatusProcesso.PRAZO_PROXIMO);
        vencido.setDataPrazoFinal(LocalDate.now().minusDays(1));

        concluido = new Processo();
        concluido.setNumeroProcesso("0004");
        concluido.setStatus(StatusProcesso.CONCLUIDO);
        concluido.setDataPrazoFinal(LocalDate.now().minusDays(1));
    }

    @Test
    void verificarProcessosVencidos_DeveAtualizarStatusConformePrazo() {
        when(processoRepository.findByStatusInAndDataPrazoFinalIsNotNull(List.of(StatusProcesso.EM_ANDAMENTO, StatusProcesso.PRAZO_PROXIMO)))
                .thenReturn(List.of(emAndamento, prazoProximo, vencido));
        when(processoRepository.save(any(Processo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        agendamentoService.verificarProcessosVencidos();

        ArgumentCaptor<Processo> captor = ArgumentCaptor.forClass(Processo.class);
        verify(processoRepository, times(2)).save(captor.capture());
        verify(processoRepository, never()).save(concluido);

        assertEquals(StatusProcesso.PRAZO_PROXIMO, prazoProximo.getStatus());
        assertEquals(StatusProcesso.EXPIRADO, vencido.getStatus());
        assertEquals(StatusProcesso.EM_ANDAMENTO, emAndamento.getStatus());
        assertEquals(StatusProcesso.CONCLUIDO, concluido.getStatus());
    }

    @Test
    void atualizarStatusFluxoPrazo_DeveAtualizarStatusConformePrazo() {
        when(processoRepository.findByStatusInAndDataPrazoFinalIsNotNull(List.of(StatusProcesso.EM_ANDAMENTO, StatusProcesso.PRAZO_PROXIMO)))
                .thenReturn(List.of(emAndamento, prazoProximo, vencido));
        when(processoRepository.save(any(Processo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        agendamentoService.atualizarStatusFluxoPrazo();

        verify(processoRepository, times(2)).save(any(Processo.class));
        assertEquals(StatusProcesso.PRAZO_PROXIMO, prazoProximo.getStatus());
        assertEquals(StatusProcesso.EXPIRADO, vencido.getStatus());
    }
}
