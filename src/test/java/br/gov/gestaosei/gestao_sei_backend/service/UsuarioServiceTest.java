package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.AlterarSenhaDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.UsuarioDTO;
import br.gov.gestaosei.gestao_sei_backend.model.Role;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import br.gov.gestaosei.gestao_sei_backend.repository.HistoricoProcessoRepository;
import br.gov.gestaosei.gestao_sei_backend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService Tests")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HistoricoProcessoRepository historicoProcessoRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioTeste;
    private UsuarioDTO usuarioDTOTeste;

    @BeforeEach
    void setUp() {
        usuarioTeste = new Usuario();
        usuarioTeste.setId(1L);
        usuarioTeste.setLogin("testuser@orgao.gov.br");
        usuarioTeste.setEmail("testuser@orgao.gov.br");
        usuarioTeste.setNomeCompleto("Usuário Teste");
        usuarioTeste.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuarioTeste.setRole(Role.USER);
        usuarioTeste.setSenha("senha123");

        usuarioDTOTeste = new UsuarioDTO();
        usuarioDTOTeste.setId(1L);
        usuarioDTOTeste.setLogin("testuser@orgao.gov.br");
        usuarioDTOTeste.setNomeCompleto("Usuário Teste");
        usuarioDTOTeste.setEmail("testuser@orgao.gov.br");
        usuarioDTOTeste.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuarioDTOTeste.setRole(Role.USER);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void testCriarUsuario() {
        when(usuarioRepository.findByLogin("testuser@orgao.gov.br")).thenReturn(null);
        when(usuarioRepository.findByEmail("testuser@orgao.gov.br")).thenReturn(null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTeste);

        UsuarioDTO resultado = usuarioService.criar(usuarioDTOTeste);

        assertNotNull(resultado);
        assertEquals("testuser@orgao.gov.br", resultado.getLogin());
        assertEquals("testuser@orgao.gov.br", resultado.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com e-mail existente")
    void testCriarUsuarioComLoginExistente() {
        when(usuarioRepository.findByLogin("testuser@orgao.gov.br")).thenReturn(usuarioTeste);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.criar(usuarioDTOTeste));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por login com sucesso")
    void testBuscarUsuarioPorLogin() {
        when(usuarioRepository.findByLoginOrEmail("testuser@orgao.gov.br", "testuser@orgao.gov.br")).thenReturn(usuarioTeste);

        UsuarioDTO resultado = usuarioService.buscarPorLogin("testuser@orgao.gov.br");

        assertNotNull(resultado);
        assertEquals("testuser@orgao.gov.br", resultado.getLogin());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar login inexistente")
    void testBuscarUsuarioPorLoginInexistente() {
        when(usuarioRepository.findByLoginOrEmail("unknown", "unknown")).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> usuarioService.buscarPorLogin("unknown"));
    }

    @Test
    @DisplayName("Deve listar todos usuários")
    void testListarTodosUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuarioTeste));

        List<UsuarioDTO> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testAtualizarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(usuarioRepository.findByLogin("testuser@orgao.gov.br")).thenReturn(usuarioTeste);
        when(usuarioRepository.findByEmail("testuser@orgao.gov.br")).thenReturn(usuarioTeste);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTeste);

        UsuarioDTO resultado = usuarioService.atualizar(1L, usuarioDTOTeste);

        assertNotNull(resultado);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuário sem histórico com sucesso")
    void testDeletarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(historicoProcessoRepository.existsByUsuarioId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> usuarioService.deletar(1L));
        verify(usuarioRepository).delete(usuarioTeste);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário com histórico")
    void testDeletarUsuarioComHistorico() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(historicoProcessoRepository.existsByUsuarioId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> usuarioService.deletar(1L));
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve alterar senha do próprio usuário com senha atual correta")
    void testAlterarSenhaProprioUsuario() {
        String senhaHash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("senha123");
        usuarioTeste.setSenha(senhaHash);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTeste);

        AlterarSenhaDTO dto = new AlterarSenhaDTO("senha123", "novaSenha456");
        assertDoesNotThrow(() -> usuarioService.alterarSenha(1L, dto, "testuser@orgao.gov.br", false));
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao alterar senha do próprio usuário com senha atual errada")
    void testAlterarSenhaSenhaAtualErrada() {
        String senhaHash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("senha123");
        usuarioTeste.setSenha(senhaHash);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        AlterarSenhaDTO dto = new AlterarSenhaDTO("senhaErrada", "novaSenha456");
        assertThrows(IllegalArgumentException.class, () -> usuarioService.alterarSenha(1L, dto, "testuser@orgao.gov.br", false));
    }

    @Test
    @DisplayName("ADMIN deve alterar senha sem informar senha atual")
    void testAlterarSenhaAdmin() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTeste);

        AlterarSenhaDTO dto = new AlterarSenhaDTO(null, "novaSenha456");
        assertDoesNotThrow(() -> usuarioService.alterarSenha(1L, dto, "admin", true));
        verify(usuarioRepository).save(any(Usuario.class));
    }
}
