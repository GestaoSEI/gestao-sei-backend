package br.gov.gestaosei.gestao_sei_backend.dto;

import br.gov.gestaosei.gestao_sei_backend.model.HistoricoProcesso;
import br.gov.gestaosei.gestao_sei_backend.model.Processo;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("HistoricoProcessoDTO Tests")
class HistoricoProcessoDTOTest {

    private HistoricoProcesso historicoTeste;
    private Processo processoTeste;
    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        usuarioTeste = new Usuario();
        usuarioTeste.setId(1L);
        usuarioTeste.setLogin("testuser");

        processoTeste = new Processo();
        processoTeste.setId(1L);
        processoTeste.setOrigem("Setor A");
        processoTeste.setUnidadeAtual("Setor B");

        historicoTeste = new HistoricoProcesso();
        historicoTeste.setId(1L);
        historicoTeste.setProcesso(processoTeste);
        historicoTeste.setUsuario(usuarioTeste);
        historicoTeste.setDataAtualizacao(LocalDateTime.now());
        historicoTeste.setStatusAnterior("Em Andamento");
        historicoTeste.setStatusNovo("Concluído");
        historicoTeste.setUnidadeAnterior("Setor A");
        historicoTeste.setUnidadeNova("Setor B");
        historicoTeste.setObservacaoDaMudanca("Processo concluído com sucesso");
    }

    @Test
    @DisplayName("Deve criar DTO com histórico completo")
    void testConstrutorComHistoricoCompleto() {
        // Act
        HistoricoProcessoDTO dto = new HistoricoProcessoDTO(historicoTeste);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("testuser", dto.getUsuarioLogin());
        assertEquals("Em Andamento", dto.getStatusAnterior());
        assertEquals("Concluído", dto.getStatusNovo());
        assertEquals("Setor A", dto.getOrigem());
        assertEquals("Setor B", dto.getUnidadeAtual());
        assertEquals("Processo concluído com sucesso", dto.getObservacaoDaMudanca());
        assertNotNull(dto.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve criar DTO com unidades nulas (fallback para processo)")
    void testConstrutorComUnidadesNulas() {
        // Arrange
        historicoTeste.setUnidadeAnterior(null);
        historicoTeste.setUnidadeNova(null);

        // Act
        HistoricoProcessoDTO dto = new HistoricoProcessoDTO(historicoTeste);

        // Assert
        assertNotNull(dto);
        assertEquals("Setor A", dto.getOrigem()); // fallback para processo.origem
        assertEquals("Setor B", dto.getUnidadeAtual()); // fallback para processo.unidadeAtual
    }

    @Test
    @DisplayName("Deve criar DTO com unidades não nulas")
    void testConstrutorComUnidadesNaoNulas() {
        // Arrange
        historicoTeste.setUnidadeAnterior("Setor X");
        historicoTeste.setUnidadeNova("Setor Y");

        // Act
        HistoricoProcessoDTO dto = new HistoricoProcessoDTO(historicoTeste);

        // Assert
        assertNotNull(dto);
        assertEquals("Setor X", dto.getOrigem()); // usa histórico.unidadeAnterior
        assertEquals("Setor Y", dto.getUnidadeAtual()); // usa histórico.unidadeNova
    }

    @Test
    @DisplayName("Deve criar DTO com usuário nulo")
    void testConstrutorComUsuarioNulo() {
        // Arrange
        historicoTeste.setUsuario(null);

        // Act
        HistoricoProcessoDTO dto = new HistoricoProcessoDTO(historicoTeste);
        // O construtor não deve lançar exceção, mas o usuário pode ser null
        assertNotNull(dto);
        assertNull(dto.getUsuarioLogin());
    }

    @Test
    @DisplayName("Deve criar DTO com processo nulo")
    void testConstrutorComProcessoNulo() {
        // Arrange
        historicoTeste.setProcesso(null);

        // Act
        HistoricoProcessoDTO dto = new HistoricoProcessoDTO(historicoTeste);
        
        // Assert
        assertNotNull(dto);
        // Quando processo é nulo, mas histórico tem unidades, usa valores do histórico
        assertEquals("Setor A", dto.getOrigem());
        assertEquals("Setor B", dto.getUnidadeAtual());
    }

    @Test
    @DisplayName("Deve criar DTO com status nulos")
    void testConstrutorComStatusNulos() {
        // Arrange
        historicoTeste.setStatusAnterior(null);
        historicoTeste.setStatusNovo(null);

        // Act
        HistoricoProcessoDTO dto = new HistoricoProcessoDTO(historicoTeste);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getStatusAnterior());
        assertNull(dto.getStatusNovo());
    }
}
