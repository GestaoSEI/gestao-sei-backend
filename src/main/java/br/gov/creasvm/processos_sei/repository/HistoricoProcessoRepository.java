package br.gov.creasvm.processos_sei.repository;

import br.gov.creasvm.processos_sei.model.HistoricoProcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoProcessoRepository extends JpaRepository<HistoricoProcesso, Long> {
    List<HistoricoProcesso> findByProcessoIdOrderByDataAtualizacaoDesc(Long processoId);
}
