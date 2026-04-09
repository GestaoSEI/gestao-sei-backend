package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.AlterarSenhaDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.UsuarioDTO;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import br.gov.gestaosei.gestao_sei_backend.repository.HistoricoProcessoRepository;
import br.gov.gestaosei.gestao_sei_backend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final HistoricoProcessoRepository historicoProcessoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, HistoricoProcessoRepository historicoProcessoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.historicoProcessoRepository = historicoProcessoRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorLogin(String login) {
        Usuario usuario = usuarioRepository.findByLogin(login);
        if (usuario != null) {
            return toDTO(usuario);
        } else {
            throw new EntityNotFoundException("Usuário não encontrado com o login: " + login);
        }
    }

    @Transactional
    public UsuarioDTO criar(UsuarioDTO dto) {
        // Verificar se login já existe
        if (usuarioRepository.findByLogin(dto.getLogin()) != null) {
            throw new IllegalArgumentException("Login já existe: " + dto.getLogin());
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(dto.getLogin());
        usuario.setRole(dto.getRole());
        usuario.setSenha(passwordEncoder.encode("senha123")); // Senha padrão

        usuario = usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));

        // Verificar se login já existe (para outro usuário)
        Usuario usuarioComMesmoLogin = usuarioRepository.findByLogin(dto.getLogin());
        if (usuarioComMesmoLogin != null && !usuarioComMesmoLogin.getId().equals(id)) {
            throw new IllegalArgumentException("Login já existe: " + dto.getLogin());
        }

        usuarioExistente.setLogin(dto.getLogin());
        usuarioExistente.setRole(dto.getRole());

        usuarioRepository.save(usuarioExistente);
        return toDTO(usuarioExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));

        if (historicoProcessoRepository.existsByUsuarioId(id)) {
            throw new IllegalStateException(
                "Não é possível excluir o usuário '" + usuario.getLogin() +
                "' pois ele possui registros no histórico de tramitação de processos."
            );
        }

        usuarioRepository.delete(usuario);
    }

    @Transactional
    public void alterarSenha(Long id, AlterarSenhaDTO dto, String loginAutenticado, boolean isAdmin) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));

        if (!isAdmin) {
            if (!usuario.getLogin().equals(loginAutenticado)) {
                throw new SecurityException("Você não tem permissão para alterar a senha de outro usuário.");
            }
            if (dto.senhaAtual() == null || dto.senhaAtual().isBlank()) {
                throw new IllegalArgumentException("Senha atual é obrigatória.");
            }
            if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getPassword())) {
                throw new IllegalArgumentException("Senha atual incorreta.");
            }
        }

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getLogin(),
            usuario.getRole()
        );
    }
}
