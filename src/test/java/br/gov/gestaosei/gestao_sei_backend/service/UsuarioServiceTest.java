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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

        usuarioDTOTeste = new UsuarioDTO(1L, "testuser", Role.USER);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void testCriarUsuario() {
        // Arrange
        when(usuarioRepository.findByLogin("testuser")).thenReturn(null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTeste);

        // Act
        UsuarioDTO resultado = usuarioService.criar(usuarioDTOTeste);

        // Assert
        assertNotNull(resultado);
        assertEquals("testuser", resultado.login());
        assertEquals(Role.USER, resultado.role());
        verify(usuarioRepository).findByLogin("testuser");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com login existente")
    void testCriarUsuarioComLoginExistente() {
        // Arrange
        when(usuarioRepository.findByLogin("testuser")).thenReturn(usuarioTeste);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.criar(usuarioDTOTeste)
        );
        assertEquals("Login já existe: testuser", exception.getMessage());
        verify(usuarioRepository).findByLogin("testuser");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void testBuscarUsuarioPorId() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));

        // Act
        UsuarioDTO resultado = usuarioService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("testuser", resultado.login());
        assertEquals(Role.USER, resultado.role());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário por ID inexistente")
    void testBuscarUsuarioPorIdInexistente() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> usuarioService.buscarPorId(1L)
        );
        assertEquals("Usuário não encontrado com o ID: 1", exception.getMessage());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve listar todos usuários")
    void testListarTodosUsuarios() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setLogin("testuser2");
        usuario2.setRole(Role.ADMIN);

        List<Usuario> usuarios = Arrays.asList(usuarioTeste, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<UsuarioDTO> resultado = usuarioService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("testuser", resultado.get(0).login());
        assertEquals("testuser2", resultado.get(1).login());
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testAtualizarUsuario() {
        // Arrange
        UsuarioDTO dtoAtualizado = new UsuarioDTO(1L, "user_atualizado", Role.ADMIN);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(usuarioRepository.findByLogin("user_atualizado")).thenReturn(null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTeste);

        // Act
        UsuarioDTO resultado = usuarioService.atualizar(1L, dtoAtualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("user_atualizado", resultado.login());
        assertEquals(Role.ADMIN, resultado.role());
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário com login duplicado")
    void testAtualizarUsuarioComLoginDuplicado() {
        // Arrange
        UsuarioDTO dtoAtualizado = new UsuarioDTO(1L, "user_duplicado", Role.ADMIN);
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        outroUsuario.setLogin("user_duplicado");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(usuarioRepository.findByLogin("user_duplicado")).thenReturn(outroUsuario);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.atualizar(1L, dtoAtualizado)
        );
        assertEquals("Login já existe: user_duplicado", exception.getMessage());
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void testAtualizarUsuarioInexistente() {
        // Arrange
        UsuarioDTO dtoAtualizado = new UsuarioDTO(1L, "user_atualizado", Role.ADMIN);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> usuarioService.atualizar(1L, dtoAtualizado)
        );
        assertEquals("Usuário não encontrado com o ID: 1", exception.getMessage());
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void testDeletarUsuario() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> usuarioService.deletar(1L));

        // Assert
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void testDeletarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> usuarioService.deletar(1L)
        );
        assertEquals("Usuário não encontrado com o ID: 1", exception.getMessage());
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository, never()).deleteById(1L);
    }
}
