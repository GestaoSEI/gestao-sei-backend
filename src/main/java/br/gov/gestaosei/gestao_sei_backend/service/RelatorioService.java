package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class RelatorioService {

    private volatile JasperReport jasperReport;

    private JasperReport getCompiledReport() throws JRException, FileNotFoundException {
        if (jasperReport == null) {
            synchronized (this) {
                if (jasperReport == null) {
                    InputStream inputStream = getClass().getResourceAsStream("/reports/processos.jrxml");
                    if (inputStream == null) {
                        throw new FileNotFoundException("Arquivo de relatório não encontrado: /reports/processos.jrxml");
                    }
                    jasperReport = JasperCompileManager.compileReport(inputStream);
                }
            }
        }
        return jasperReport;
    }

    public byte[] gerarRelatorioProcessos(List<ProcessoDTO> processos) throws JRException, FileNotFoundException {
        JasperReport report = getCompiledReport();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(processos);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Sistema SEI");
        parameters.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
