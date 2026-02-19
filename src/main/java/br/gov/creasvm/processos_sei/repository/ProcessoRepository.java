package br.gov.creasvm.processos_sei.repository;

import br.gov.creasvm.processos_sei.model.Processo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, Long> {
    Optional<Processo> findByNumeroProcesso(String numeroProcesso);

    List<Processo> findByStatus(String status);
    List<Processo> findByUnidadeAtual(String unidadeAtual);
    List<Processo> findByDataPrazoFinalBefore(LocalDate dataAtual);
    List<Processo> findByStatusAndUnidadeAtual(String status, String unidadeAtual);
}

