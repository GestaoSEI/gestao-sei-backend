package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    public byte[] gerarRelatorioProcessos(List<ProcessoDTO> processos) throws JRException, FileNotFoundException {
        // Carrega o arquivo .jrxml
        InputStream inputStream = getClass().getResourceAsStream("/reports/processos.jrxml");
        if (inputStream == null) {
            throw new FileNotFoundException("Arquivo de relatório não encontrado: /reports/processos.jrxml");
        }
        
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

        // Cria o datasource com a lista de processos
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(processos);

        // Parâmetros do relatório (se houver)
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Sistema SEI");

        // Preenche o relatório
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Exporta para PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
