package br.gov.creasvm.processos_sei.repository;

import br.gov.creasvm.processos_sei.model.Processo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT p FROM Processo p WHERE " +
           "LOWER(p.numeroProcesso) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.tipoProcesso) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.origem) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.unidadeAtual) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.status) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.observacao) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Processo> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Processo p WHERE " +
           "(:unidadeAtual IS NULL OR p.unidadeAtual = :unidadeAtual) AND " +
           "(:dataInicio IS NULL OR p.dataPrazoFinal >= :dataInicio) AND " +
           "(:dataFim IS NULL OR p.dataPrazoFinal <= :dataFim)")
    List<Processo> findByUnidadeAndPrazoBetween(@Param("unidadeAtual") String unidadeAtual,
                                                @Param("dataInicio") LocalDate dataInicio,
                                                @Param("dataFim") LocalDate dataFim);
}
