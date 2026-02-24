package br.gov.creasvm.processos_sei.config;

import br.gov.creasvm.processos_sei.model.Role;
import br.gov.creasvm.processos_sei.model.Usuario;
import br.gov.creasvm.processos_sei.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByLogin("admin") == null) {
            String encryptedPassword = new BCryptPasswordEncoder().encode("admin123");
            Usuario admin = new Usuario("admin", encryptedPassword, Role.ADMIN);
            usuarioRepository.save(admin);
            System.out.println("Usuário ADMIN padrão criado: admin / admin123");
        }
    }
}
