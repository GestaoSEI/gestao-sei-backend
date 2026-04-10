package br.gov.gestaosei.gestao_sei_backend.config;

import br.gov.gestaosei.gestao_sei_backend.model.Processo;
import br.gov.gestaosei.gestao_sei_backend.model.Role;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import br.gov.gestaosei.gestao_sei_backend.repository.ProcessoRepository;
import br.gov.gestaosei.gestao_sei_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProcessoRepository processoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByLogin("admin") == null) {
            String encryptedPassword = new BCryptPasswordEncoder().encode("admin123");
            Usuario admin = new Usuario("admin", encryptedPassword, Role.ADMIN);
            usuarioRepository.save(admin);
            System.out.println("Usuário ADMIN padrão criado: admin / admin123");
        }

        if (processoRepository.count() == 0) {
            LocalDate hoje = LocalDate.now();

            Processo p1 = new Processo();
            p1.setNumeroProcesso("6024.2024/0001001-3");
            p1.setTipoProcesso("Ofício");
            p1.setOrigem("Protocolo Geral");
            p1.setUnidadeAtual("Setor Jurídico");
            p1.setStatus("Em andamento");
            p1.setDataPrazoFinal(hoje.plusDays(3));
            p1.setObservacao("Aguardando parecer do setor jurídico — prazo urgente");

            Processo p2 = new Processo();
            p2.setNumeroProcesso("6024.2024/0002002-7");
            p2.setTipoProcesso("Processo Administrativo");
            p2.setOrigem("Secretaria Municipal");
            p2.setUnidadeAtual("Gabinete");
            p2.setStatus("Respondido");
            p2.setDataPrazoFinal(hoje.plusMonths(2));
            p2.setObservacao("Documentação enviada ao departamento responsável");

            Processo p3 = new Processo();
            p3.setNumeroProcesso("6024.2023/0003003-1");
            p3.setTipoProcesso("Requerimento");
            p3.setOrigem("Ouvidoria");
            p3.setUnidadeAtual("CREAS NPJ");
            p3.setStatus("Concluído");
            p3.setDataPrazoFinal(hoje.minusMonths(1));
            p3.setObservacao("Processo encerrado com sucesso");

            Processo p4 = new Processo();
            p4.setNumeroProcesso("6024.2024/0004004-5");
            p4.setTipoProcesso("Licitação");
            p4.setOrigem("Compras e Contratos");
            p4.setUnidadeAtual("Departamento Financeiro");
            p4.setStatus("Em andamento");
            p4.setDataPrazoFinal(hoje.plusMonths(6));
            p4.setObservacao("Análise de proposta técnica em andamento");

            Processo p5 = new Processo();
            p5.setNumeroProcesso("6024.2023/0005005-9");
            p5.setTipoProcesso("Auditoria");
            p5.setOrigem("Controle Interno");
            p5.setUnidadeAtual("Arquivo");
            p5.setStatus("Expirado");
            p5.setDataPrazoFinal(hoje.minusMonths(3));
            p5.setObservacao("Prazo encerrado sem retorno");

            processoRepository.saveAll(List.of(p1, p2, p3, p4, p5));
            System.out.println("5 processos de exemplo criados.");
        }
    }
}
