package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.UsuarioDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class RelatorioService {

    private volatile JasperReport jasperReportProcessos;
    private volatile JasperReport jasperReportUsuarios;

    private JasperReport getCompiledReport(String resourcePath) throws JRException, FileNotFoundException {
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new FileNotFoundException("Arquivo de relatório não encontrado: " + resourcePath);
        }
        return JasperCompileManager.compileReport(inputStream);
    }

    private JasperReport getCompiledReportProcessos() throws JRException, FileNotFoundException {
        if (jasperReportProcessos == null) {
            synchronized (this) {
                if (jasperReportProcessos == null) {
                    jasperReportProcessos = getCompiledReport("/reports/processos.jrxml");
                }
            }
        }
        return jasperReportProcessos;
    }

    private JasperReport getCompiledReportUsuarios() throws JRException, FileNotFoundException {
        if (jasperReportUsuarios == null) {
            synchronized (this) {
                if (jasperReportUsuarios == null) {
                    jasperReportUsuarios = getCompiledReport("/reports/usuarios.jrxml");
                }
            }
        }
        return jasperReportUsuarios;
    }

    public byte[] gerarRelatorioProcessos(List<ProcessoDTO> processos) throws JRException, FileNotFoundException {
        JasperReport report = getCompiledReportProcessos();
        List<ProcessoDTO> processosOrdenados = processos.stream()
            .sorted(Comparator.comparing(ProcessoDTO::getDataPrazoFinal,
                Comparator.nullsLast(Comparator.reverseOrder())))
            .toList();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(processosOrdenados);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Sistema SEI");
        parameters.put(JRParameter.REPORT_LOCALE, Locale.of("pt", "BR"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public byte[] gerarRelatorioUsuarios(List<UsuarioDTO> usuarios) throws JRException, FileNotFoundException {
        JasperReport report = getCompiledReportUsuarios();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(usuarios);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Sistema SEI");
        parameters.put(JRParameter.REPORT_LOCALE, Locale.of("pt", "BR"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
