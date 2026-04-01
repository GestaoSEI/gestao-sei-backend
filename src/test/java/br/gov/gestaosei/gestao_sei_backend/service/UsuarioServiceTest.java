package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.UsuarioDTO;
import br.gov.gestaosei.gestao_sei_backend.model.Role;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import br.gov.gestaosei.gestao_sei_backend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioTeste;
    private UsuarioDTO usuarioDTOTeste;

    @BeforeEach
    void setUp() {
        usuarioTeste = new Usuario();
        usuarioTeste.setId(1L);
        usuarioTeste.setLogin("testuser");
        usuarioTeste.setRole(Role.USER);
        usuarioTeste.setSenha("senha123");

        usuarioDTOTeste = new UsuarioDTO();
        usuarioDTOTeste.setId(1L);
        usuarioDTOTeste.setLogin("testuser");
        usuarioDTOTeste.setRole(Role.USER);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void testCriarUsuario() {
        when(usuarioRepository.findByLogin("testuser")).thenReturn(null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTeste);

        UsuarioDTO resultado = usuarioService.criar(usuarioDTOTeste);

        assertNotNull(resultado);
        assertEquals("testuser", resultado.getLogin());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com login existente")
    void testCriarUsuarioComLoginExistente() {
        when(usuarioRepository.findByLogin("testuser")).thenReturn(usuarioTeste);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.criar(usuarioDTOTeste));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por login com sucesso")
    void testBuscarUsuarioPorLogin() {
        when(usuarioRepository.findByLogin("testuser")).thenReturn(usuarioTeste);

        UsuarioDTO resultado = usuarioService.buscarPorLogin("testuser");

        assertNotNull(resultado);
        assertEquals("testuser", resultado.getLogin());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar login inexistente")
    void testBuscarUsuarioPorLoginInexistente() {
        when(usuarioRepository.findByLogin("unknown")).thenReturn(null);

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
        when(usuarioRepository.findByLogin("testuser")).thenReturn(usuarioTeste);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTeste);

        UsuarioDTO resultado = usuarioService.atualizar(1L, usuarioDTOTeste);

        assertNotNull(resultado);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar usuário")
    void testDeletarUsuario() {
        assertThrows(UnsupportedOperationException.class, () -> usuarioService.deletar(1L));
    }
}
