package br.gov.gestaosei.gestao_sei_backend.repository;

import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByLogin(String login);
    Usuario findByEmail(String email);
    Usuario findByLoginOrEmail(String login, String email);
}
