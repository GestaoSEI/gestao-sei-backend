package br.gov.gestaosei.gestao_sei_backend.repository;

import br.gov.gestaosei.gestao_sei_backend.model.HistoricoProcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoProcessoRepository extends JpaRepository<HistoricoProcesso, Long> {
    List<HistoricoProcesso> findByProcessoIdOrderByDataAtualizacaoDesc(Long processoId);
}
