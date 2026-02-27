package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.model.Processo;
import br.gov.gestaosei.gestao_sei_backend.repository.ProcessoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

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

    @InjectMocks
    private ProcessoServiceImpl processoService;

    private Processo processo;
    private ProcessoDTO processoDTO;

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
    }

    @Test
    void listarTodos_DeveRetornarListaDeProcessos() {
        when(processoRepository.findAll()).thenReturn(Arrays.asList(processo));

        List<ProcessoDTO> resultado = processoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(processo.getNumeroProcesso(), resultado.get(0).getNumeroProcesso());
        verify(processoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_ComIdExistente_DeveRetornarProcesso() {
        when(processoRepository.findById(1L)).thenReturn(Optional.of(processo));

        ProcessoDTO resultado = processoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(processo.getId(), resultado.getId());
        verify(processoRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_ComIdInexistente_DeveLancarExcecao() {
        when(processoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> processoService.buscarPorId(99L));
        verify(processoRepository, times(1)).findById(99L);
    }

    @Test
    void salvar_DeveRetornarProcessoSalvo() {
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoDTO resultado = processoService.salvar(processoDTO);

        assertNotNull(resultado);
        assertEquals(processoDTO.getNumeroProcesso(), resultado.getNumeroProcesso());
        verify(processoRepository, times(1)).save(any(Processo.class));
    }

    @Test
    void atualizar_ComIdExistente_DeveAtualizarERetornarProcesso() {
        when(processoRepository.findById(1L)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);

        ProcessoDTO resultado = processoService.atualizar(1L, processoDTO);

        assertNotNull(resultado);
        assertEquals(processoDTO.getNumeroProcesso(), resultado.getNumeroProcesso());
        verify(processoRepository, times(1)).findById(1L);
        verify(processoRepository, times(1)).save(any(Processo.class));
    }

    @Test
    void atualizar_ComIdInexistente_DeveLancarExcecao() {
        when(processoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> processoService.atualizar(99L, processoDTO));
        verify(processoRepository, times(1)).findById(99L);
        verify(processoRepository, never()).save(any(Processo.class));
    }

    @Test
    void deletar_ComIdExistente_DeveDeletarProcesso() {
        when(processoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(processoRepository).deleteById(1L);

        processoService.deletar(1L);

        verify(processoRepository, times(1)).existsById(1L);
        verify(processoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletar_ComIdInexistente_DeveLancarExcecao() {
        when(processoRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> processoService.deletar(99L));
        verify(processoRepository, times(1)).existsById(99L);
        verify(processoRepository, never()).deleteById(99L);
    }
}
