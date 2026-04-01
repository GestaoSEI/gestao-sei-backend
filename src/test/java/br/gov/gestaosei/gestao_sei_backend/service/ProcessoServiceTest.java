package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.model.Processo;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import br.gov.gestaosei.gestao_sei_backend.repository.HistoricoProcessoRepository;
import br.gov.gestaosei.gestao_sei_backend.repository.ProcessoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessoServiceTest {

    @Mock
    private ProcessoRepository processoRepository;

    @Mock
    private HistoricoProcessoRepository historicoProcessoRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProcessoServiceImpl processoService;

    private Processo processo;
    private ProcessoDTO processoDTO;
    private Usuario usuarioLogado;

    @BeforeEach
    void setUp() {
        processo = new Processo();
        processo.setId(1L);
        processo.setNumeroProcesso("12345/2023");
        processo.setTipoProcesso("Administrativo");
        processo.setOrigem("Protocolo");
        processo.setUnidadeAtual("Setor A");
        processo.setStatus("Em andamento");
        processo.setDataPrazoFinal(LocalDate.now().plusDays(10));
        processo.setObservacao("Teste");

        processoDTO = new ProcessoDTO();
        BeanUtils.copyProperties(processo, processoDTO);

        usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);
        usuarioLogado.setLogin("admin");
    }

    private void mockUsuarioLogado() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioLogado);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void listarTodos_DeveRetornarListaDeProcessos() {
        when(processoRepository.findAll()).thenReturn(Arrays.asList(processo));

        List<ProcessoDTO> resultado = processoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(processo.getNumeroProcesso(), resultado.get(0).getNumeroProcesso());
    }

    @Test
    void buscarPorPalavraChave_DeveChamarRepositorio() {
        when(processoRepository.searchByKeyword("teste")).thenReturn(Arrays.asList(processo));

        List<ProcessoDTO> resultado = processoService.buscarPorPalavraChave("teste");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(processoRepository).searchByKeyword("teste");
    }

    @Test
    void salvar_DeveRetornarProcessoSalvo() {
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoDTO resultado = processoService.salvar(processoDTO);

        assertNotNull(resultado);
        assertEquals(processoDTO.getNumeroProcesso(), resultado.getNumeroProcesso());
    }

    @Test
    void atualizarPorNumero_ComMudancaDeStatus_DeveGravarHistorico() {
        mockUsuarioLogado();
        Processo processoNovo = new Processo();
        BeanUtils.copyProperties(processo, processoNovo);
        processoNovo.setStatus("Concluído");

        processoDTO.setStatus("Concluído");

        when(processoRepository.findByNumeroProcesso("12345/2023")).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processoNovo);

        ProcessoDTO resultado = processoService.atualizarPorNumero("12345/2023", processoDTO);

        assertNotNull(resultado);
        assertEquals("Concluído", resultado.getStatus());
        verify(historicoProcessoRepository, times(1)).save(any());
    }

    @Test
    void deletarPorNumero_ComNumeroExistente_DeveDeletar() {
        when(processoRepository.findByNumeroProcesso("12345/2023")).thenReturn(Optional.of(processo));
        doNothing().when(processoRepository).delete(processo);

        processoService.deletarPorNumero("12345/2023");

        verify(processoRepository).delete(processo);
    }

    @Test
    void toDTO_QuandoVencendoEm4Dias_DeveAtivarAlertaUrgencia() {
        processo.setDataPrazoFinal(LocalDate.now().plusDays(4));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoDTO resultado = processoService.salvar(processoDTO);

        assertTrue(resultado.getAlertaUrgencia());
    }

    @Test
    void toDTO_QuandoVencendoEm10Dias_DeveDesativarAlertaUrgencia() {
        processo.setDataPrazoFinal(LocalDate.now().plusDays(10));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoDTO resultado = processoService.salvar(processoDTO);

        assertFalse(resultado.getAlertaUrgencia());
    }
}
