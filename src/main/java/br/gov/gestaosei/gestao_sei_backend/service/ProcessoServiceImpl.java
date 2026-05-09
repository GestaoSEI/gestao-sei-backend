package br.gov.gestaosei.gestao_sei_backend.service;

import br.gov.gestaosei.gestao_sei_backend.dto.HistoricoProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ImportacaoResultadoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ProcessoFiltroDTO;
import br.gov.gestaosei.gestao_sei_backend.model.HistoricoProcesso;
import br.gov.gestaosei.gestao_sei_backend.model.Processo;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import br.gov.gestaosei.gestao_sei_backend.repository.HistoricoProcessoRepository;
import br.gov.gestaosei.gestao_sei_backend.repository.ProcessoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProcessoServiceImpl implements ProcessoService {
    private static final String NUMERO_PROCESSO_REGEX = "^\\d{4}\\.\\d{4}/\\d{7}-\\d$";

    private final ProcessoRepository processoRepository;
    private final HistoricoProcessoRepository historicoProcessoRepository;

    public ProcessoServiceImpl(ProcessoRepository processoRepository, HistoricoProcessoRepository historicoProcessoRepository) {
        this.processoRepository = processoRepository;
        this.historicoProcessoRepository = historicoProcessoRepository;
    }

    @Override
    public List<ProcessoDTO> filtrar(ProcessoFiltroDTO filtro) {
        if (filtro == null) {
            return listarTodos();
        }

        LocalDate hoje = LocalDate.now();

        // Parte de todos e aplica cada filtro de forma acumulativa
        List<Processo> processos = processoRepository.findAll();

        if (filtro.getStatus() != null && !filtro.getStatus().isBlank()) {
            String statusNormalizado = normalizarStatus(filtro.getStatus());
            processos = processos.stream()
                .filter(p -> p.getStatus() != null && normalizarStatus(p.getStatus()).equalsIgnoreCase(statusNormalizado))
                    .collect(Collectors.toList());
        }

        if (filtro.getUnidadeAtual() != null && !filtro.getUnidadeAtual().isBlank()) {
            String unidadeLower = filtro.getUnidadeAtual().toLowerCase();
            processos = processos.stream()
                    .filter(p -> p.getUnidadeAtual() != null && p.getUnidadeAtual().toLowerCase().contains(unidadeLower))
                    .collect(Collectors.toList());
        }

        if (Boolean.TRUE.equals(filtro.getPrazoExpirado())) {
            processos = processos.stream()
                    .filter(p -> p.getDataPrazoFinal() != null && p.getDataPrazoFinal().isBefore(hoje))
                    .collect(Collectors.toList());
        }

        if (filtro.getDataInicio() != null) {
            processos = processos.stream()
                    .filter(p -> p.getDataPrazoFinal() != null && !p.getDataPrazoFinal().isBefore(filtro.getDataInicio()))
                    .collect(Collectors.toList());
        }

        if (filtro.getDataFim() != null) {
            processos = processos.stream()
                    .filter(p -> p.getDataPrazoFinal() != null && !p.getDataPrazoFinal().isAfter(filtro.getDataFim()))
                    .collect(Collectors.toList());
        }

        return processos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessoDTO> buscarPorPalavraChave(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return listarTodos();
        }
        String keywordNormalizada = keyword.trim().toLowerCase();
        return processoRepository.findAll().stream()
                .filter(p -> contemPalavraChave(p, keywordNormalizada))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricoProcessoDTO> getHistoricoPorProcessoId(Long processoId) {
        return historicoProcessoRepository.findByProcessoIdOrderByDataAtualizacaoDesc(processoId)
                .stream()
                .map(this::toHistoricoDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessoDTO> listarTodos() {
        return processoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessoDTO buscarPorId(Long id) {
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o ID: " + id));
        return toDTO(processo);
    }

    @Override
    @Transactional
    public ProcessoDTO salvar(ProcessoDTO processoDTO) {
        String numeroNormalizado = normalizarNumeroProcesso(processoDTO.getNumeroProcesso());
        if (buscarProcessoPorNumeroFlex(numeroNormalizado).isPresent()) {
            throw new IllegalArgumentException("Esse processo já foi cadastrado.");
        }

        Processo processo = toEntity(processoDTO);
        processo.setNumeroProcesso(numeroNormalizado);
        processo = processoRepository.save(processo);
        return toDTO(processo);
    }

    @Override
    @Transactional
    public ProcessoDTO atualizar(Long id, ProcessoDTO processoDTO) {
        Processo processoExistente = processoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o ID: " + id));

        return atualizarProcessoExistente(processoExistente, processoDTO);
    }

    @Override
    @Transactional
    public ProcessoDTO atualizarPorNumero(String numeroProcesso, ProcessoDTO processoDTO) {
        Processo processoExistente = buscarProcessoPorNumeroFlex(numeroProcesso)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o número: " + numeroProcesso));

        return atualizarProcessoExistente(processoExistente, processoDTO);
    }

    private ProcessoDTO atualizarProcessoExistente(Processo processoExistente, ProcessoDTO processoDTO) {
        // Guarda os valores antigos para comparação
        String statusAnterior = normalizarStatus(processoExistente.getStatus());
        String unidadeAnterior = processoExistente.getUnidadeAtual();

        processoDTO.setStatus(normalizarStatus(processoDTO.getStatus()));

        // Atualiza o processo com os novos dados
        // Preserva o ID original e o flag de duplicata
        Long idOriginal = processoExistente.getId();
        boolean duplicataOriginal = processoExistente.isDuplicata();
        BeanUtils.copyProperties(processoDTO, processoExistente, "id", "duplicata");
        processoExistente.setDuplicata(duplicataOriginal);
        processoExistente.setId(idOriginal);
        
        Processo processoAtualizado = processoRepository.save(processoExistente);

        // Obtém o usuário logado
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Compara e registra o histórico
        boolean statusMudou = !Objects.equals(statusAnterior, normalizarStatus(processoAtualizado.getStatus()));
        boolean unidadeMudou = !Objects.equals(unidadeAnterior, processoAtualizado.getUnidadeAtual());

        if (statusMudou || unidadeMudou) {
            HistoricoProcesso historico = new HistoricoProcesso(
                    processoAtualizado,
                    usuarioLogado,
                    statusMudou ? statusAnterior : null,
                    statusMudou ? normalizarStatus(processoAtualizado.getStatus()) : null,
                    unidadeMudou ? unidadeAnterior : null,
                    unidadeMudou ? processoAtualizado.getUnidadeAtual() : null,
                    processoDTO.getObservacao() // Usamos a observação do DTO como a "observação da mudança"
            );
            historicoProcessoRepository.save(historico);
        }

        return toDTO(processoAtualizado);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o ID: " + id));
        if (!processo.isDuplicata()) {
            throw new IllegalArgumentException("O processo só pode ser excluído se estiver marcado como duplicata.");
        }
        processoRepository.delete(processo);
    }

    @Override
    @Transactional
    public void deletarPorNumero(String numeroProcesso) {
        Processo processo = buscarProcessoPorNumeroFlex(numeroProcesso)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com o número: " + numeroProcesso));
        if (!processo.isDuplicata()) {
            throw new IllegalArgumentException("O processo só pode ser excluído se estiver marcado como duplicata.");
        }
        processoRepository.delete(processo);
    }

    @Override
    @Transactional
    public ImportacaoResultadoDTO importarCsv(MultipartFile file) throws IOException {
        if (processoRepository.count() > 0) {
            throw new IllegalStateException("Importação bloqueada: já existem processos cadastrados. Limpe o banco de dados para realizar nova importação inicial.");
        }

        int importados = 0;
        int duplicatas = 0;
        int erros = 0;
        List<String> mensagensErro = new ArrayList<>();
        DateTimeFormatter formatterBr = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String conteudoCsv = decodificarConteudoCsv(file.getBytes());

        try (BufferedReader reader = new BufferedReader(new StringReader(conteudoCsv))) {

            String linha;
            boolean primeiraLinha = true;
            char delimitador = ',';
            Map<String, Integer> indiceCabecalho = new HashMap<>();
            int numeroLinha = 0;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                if (primeiraLinha) {
                    delimitador = detectarDelimitador(linha);
                    String[] cabecalho = linha.split(String.valueOf(delimitador), -1);
                    for (int i = 0; i < cabecalho.length; i++) {
                        indiceCabecalho.put(normalizarCabecalho(cabecalho[i]), i);
                    }
                    primeiraLinha = false;
                    continue; // Pula o cabeçalho
                }
                if (linha.isBlank()) continue;

                String[] campos = linha.split(String.valueOf(delimitador), -1);
                if (campos.length < 6) {
                    erros++;
                    mensagensErro.add("Linha " + numeroLinha + ": formato inválido (mínimo 6 campos esperados; delimitador esperado: '" + delimitador + "').");
                    continue;
                }

                if (linhaSemConteudo(campos)) {
                    continue;
                }

                int idxNumero = obterIndice(indiceCabecalho, "numeroprocesso", 0);
                int idxTipo = obterIndice(indiceCabecalho, "tipoprocesso", 1);
                int idxOrigem = obterIndice(indiceCabecalho, "origem", 2);
                int idxUnidade = obterIndice(indiceCabecalho, "unidadeatual", 3);
                int idxStatus = obterIndice(indiceCabecalho, "status", 4);
                int idxDataPrazo = obterIndice(indiceCabecalho, "dataprazofinal", 5);
                int idxObservacao = obterIndice(indiceCabecalho, "observacao", 6);

                String numero = normalizarNumeroProcesso(valorCampo(campos, idxNumero));
                String tipoProcesso = valorCampo(campos, idxTipo);
                String origem = valorCampo(campos, idxOrigem);
                String unidadeAtual = valorCampo(campos, idxUnidade);
                String status = normalizarStatus(valorCampo(campos, idxStatus));
                String dataPrazo = valorCampo(campos, idxDataPrazo);
                String observacao = valorCampo(campos, idxObservacao);
                try {
                    if (numero.isBlank()) {
                        throw new IllegalArgumentException("número do processo vazio");
                    }
                    validarNumeroProcessoImportacao(numero);
                    Optional<Processo> existenteOpt = buscarProcessoPorNumeroFlex(numero);
                    if (existenteOpt.isPresent()) {
                        // Número já existe no banco: conta como duplicata, mas atualiza dados válidos para corrigir texto/campos antigos.
                        Processo existente = existenteOpt.get();
                        boolean alterou = atualizarProcessoComDadosCsv(
                                existente,
                                tipoProcesso,
                                origem,
                                unidadeAtual,
                                status,
                                dataPrazo,
                                observacao,
                                formatterBr
                        );
                        if (alterou) {
                            processoRepository.save(existente);
                        }
                        duplicatas++;
                    } else {
                        Processo p = new Processo();
                        p.setNumeroProcesso(numero);
                        p.setTipoProcesso(tipoProcesso);
                        p.setOrigem(origem);
                        p.setUnidadeAtual(unidadeAtual);
                        p.setStatus(status);
                        if (!dataPrazo.isBlank()) {
                            p.setDataPrazoFinal(parseData(dataPrazo, formatterBr));
                        }
                        if (!observacao.isBlank()) {
                            p.setObservacao(observacao);
                        }
                        processoRepository.save(p);
                        importados++;
                    }
                } catch (Exception e) {
                    erros++;
                    mensagensErro.add("Linha " + numeroLinha + " (" + numero + "): " + e.getMessage());
                }
            }
        }
        return new ImportacaoResultadoDTO(importados, duplicatas, erros, mensagensErro);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarProcessosCsv() throws IOException {
        List<Processo> processos = processoRepository.findAll();
        StringBuilder csvContent = new StringBuilder();

        // Cabeçalho CSV
        csvContent.append("numeroProcesso,tipoProcesso,origem,unidadeAtual,status,dataPrazoFinal,observacao\n");

        // Dados dos processos
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // Formato YYYY-MM-DD
        for (Processo processo : processos) {
            csvContent.append(escapeCsv(processo.getNumeroProcesso())).append(",");
            csvContent.append(escapeCsv(processo.getTipoProcesso())).append(",");
            csvContent.append(escapeCsv(processo.getOrigem())).append(",");
            csvContent.append(escapeCsv(processo.getUnidadeAtual())).append(",");
            csvContent.append(escapeCsv(processo.getStatus())).append(",");
            csvContent.append(processo.getDataPrazoFinal() != null ? processo.getDataPrazoFinal().format(formatter) : "").append(",");
            csvContent.append(escapeCsv(processo.getObservacao())).append("\n");
        }

        return csvContent.toString().getBytes(StandardCharsets.UTF_8);
    }

    // Método auxiliar para escapar valores CSV (lidar com vírgulas, aspas, etc.)
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\""); // Escapa aspas duplas
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\""; // Envolve em aspas se contiver caracteres especiais
        }
        return escaped;
    }

    private char detectarDelimitador(String cabecalho) {
        if (cabecalho == null) return ',';
        String semBom = cabecalho.replace("\uFEFF", "");
        int qtdPontoVirgula = semBom.length() - semBom.replace(";", "").length();
        int qtdVirgula = semBom.length() - semBom.replace(",", "").length();
        return qtdPontoVirgula > qtdVirgula ? ';' : ',';
    }

    private String normalizarCabecalho(String valor) {
        String semBom = valor == null ? "" : valor.replace("\uFEFF", "").trim().toLowerCase();
        return Normalizer.normalize(semBom, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9]", "");
    }

    private int obterIndice(Map<String, Integer> cabecalho, String nomeCampo, int fallback) {
        return cabecalho.getOrDefault(nomeCampo, fallback);
    }

    private String valorCampo(String[] campos, int indice) {
        if (indice < 0 || indice >= campos.length) {
            return "";
        }
        if (campos[indice] == null) {
            return "";
        }
        return campos[indice].replace('\u00A0', ' ').trim();
    }

    private String normalizarNumeroProcesso(String numeroBruto) {
        if (numeroBruto == null) return "";
        String originalLimpo = numeroBruto
                .replace('\u00A0', ' ')
                .replace("", "")
                .trim();

        String limpo = originalLimpo;
        limpo = limpo.replaceAll("\\s+", "");
        limpo = limpo.replaceAll("[^0-9./-]", "");

        // Ex.: 60242021/0008379-6 -> 6024.2021/0008379-6
        limpo = limpo.replaceAll("^(\\d{4})(\\d{4})/(\\d{7})-(\\d)$", "$1.$2/$3-$4");
        // Ex.: 6018.2022-0030428-7 -> 6018.2022/0030428-7
        limpo = limpo.replaceAll("^(\\d{4}\\.\\d{4})-(\\d{7})-(\\d)$", "$1/$2-$3");

        if (limpo.matches("^[0-9]{4}\\.[0-9]{4}/[0-9]{7}-[0-9]$")) {
            return limpo;
        }

        if (originalLimpo.matches("^[0-9]{4}\\.[0-9]{4}/[0-9]{7}-[0-9]$")) {
            return originalLimpo;
        }

        return originalLimpo;
    }

    private void validarNumeroProcessoImportacao(String numeroProcesso) {
        if (!numeroProcesso.matches(NUMERO_PROCESSO_REGEX)) {
            throw new IllegalArgumentException(
                    "número do processo inválido; use o padrão xxxx.xxxx/xxxxxxx-x");
        }
    }

    private Optional<Processo> buscarProcessoPorNumeroFlex(String numeroProcesso) {
        Optional<Processo> exato = processoRepository.findByNumeroProcesso(numeroProcesso);
        if (exato.isPresent()) {
            return exato;
        }

        String normalizado = normalizarNumeroProcesso(numeroProcesso);
        if (!normalizado.isBlank()) {
            Optional<Processo> diretoNormalizado = processoRepository.findByNumeroProcesso(normalizado);
            if (diretoNormalizado.isPresent()) {
                return diretoNormalizado;
            }

            String chaveAlvo = chaveNumero(normalizado);
            return processoRepository.findAll().stream()
                    .filter(p -> !chaveAlvo.isBlank())
                    .filter(p -> chaveAlvo.equals(chaveNumero(p.getNumeroProcesso())))
                    .findFirst();
        }

        return Optional.empty();
    }

    private String chaveNumero(String numero) {
        if (numero == null) {
            return "";
        }
        return numero.replaceAll("\\D", "");
    }

    private LocalDate parseData(String data, DateTimeFormatter formatterBr) {
        if (data.matches("^\\d{5}$")) {
            // Excel serial date (base 1899-12-30).
            return LocalDate.of(1899, 12, 30).plusDays(Long.parseLong(data));
        }
        try {
            return LocalDate.parse(data);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(data, formatterBr);
        }
    }

    private boolean linhaSemConteudo(String[] campos) {
        for (String campo : campos) {
            if (campo != null && !campo.replace('\u00A0', ' ').trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean atualizarProcessoComDadosCsv(
            Processo processo,
            String tipoProcesso,
            String origem,
            String unidadeAtual,
            String status,
            String dataPrazo,
            String observacao,
            DateTimeFormatter formatterBr
    ) {
        boolean alterou = false;

        alterou |= atualizarTextoNaoVazio(processo::getTipoProcesso, processo::setTipoProcesso, tipoProcesso);
        alterou |= atualizarTextoNaoVazio(processo::getOrigem, processo::setOrigem, origem);
        alterou |= atualizarTextoNaoVazio(processo::getUnidadeAtual, processo::setUnidadeAtual, unidadeAtual);
        alterou |= atualizarTextoNaoVazio(
            () -> normalizarStatus(processo.getStatus()),
            processo::setStatus,
            normalizarStatus(status)
        );
        alterou |= atualizarTextoNaoVazio(processo::getObservacao, processo::setObservacao, observacao);

        if (!dataPrazo.isBlank()) {
            LocalDate novaData = parseData(dataPrazo, formatterBr);
            if (!Objects.equals(novaData, processo.getDataPrazoFinal())) {
                processo.setDataPrazoFinal(novaData);
                alterou = true;
            }
        }

        return alterou;
    }

    private boolean atualizarTextoNaoVazio(java.util.function.Supplier<String> getter,
                                            java.util.function.Consumer<String> setter,
                                            String novoValor) {
        if (novoValor == null || novoValor.isBlank()) {
            return false;
        }
        String atual = getter.get();
        if (!Objects.equals(atual, novoValor)) {
            setter.accept(novoValor);
            return true;
        }
        return false;
    }

    private String decodificarConteudoCsv(byte[] bytes) {
        CharsetDecoder utf8Decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return utf8Decoder.decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            return new String(bytes, Charset.forName("Windows-1252"));
        }
    }

    private boolean contemPalavraChave(Processo processo, String keywordNormalizada) {
        return contemTexto(processo.getNumeroProcesso(), keywordNormalizada)
                || contemTexto(processo.getTipoProcesso(), keywordNormalizada)
                || contemTexto(processo.getOrigem(), keywordNormalizada)
                || contemTexto(processo.getUnidadeAtual(), keywordNormalizada)
                || contemTexto(normalizarStatus(processo.getStatus()), keywordNormalizada)
                || contemTexto(processo.getObservacao(), keywordNormalizada);
    }

    private boolean contemTexto(String valor, String keywordNormalizada) {
        return valor != null && valor.toLowerCase().contains(keywordNormalizada);
    }

    private ProcessoDTO toDTO(Processo processo) {
        ProcessoDTO dto = new ProcessoDTO();
        BeanUtils.copyProperties(processo, dto);
        dto.setNumeroProcesso(normalizarNumeroProcesso(dto.getNumeroProcesso()));
        dto.setStatus(normalizarStatus(dto.getStatus()));
        
        // Calcula o alerta de prazo apenas para processos "Em andamento"
        if (processo.getDataPrazoFinal() != null && isStatusAtivo(processo.getStatus())) {
            long diasParaVencer = ChronoUnit.DAYS.between(LocalDate.now(), processo.getDataPrazoFinal());
            // Alerta se vencer em 5 dias ou menos (incluindo vencidos)
            dto.setAlertaUrgencia(diasParaVencer <= 5);
        } else {
            dto.setAlertaUrgencia(false);
        }
        
        return dto;
    }

    private boolean isStatusAtivo(String status) {
        status = normalizarStatus(status);
        if (status == null) return false;
        return "Em andamento".equalsIgnoreCase(status);
    }

    private Processo toEntity(ProcessoDTO dto) {
        Processo processo = new Processo();
        BeanUtils.copyProperties(dto, processo);
        processo.setStatus(normalizarStatus(processo.getStatus()));
        return processo;
    }

    private HistoricoProcessoDTO toHistoricoDTO(HistoricoProcesso historico) {
        HistoricoProcessoDTO dto = new HistoricoProcessoDTO(historico);
        dto.setStatusAnterior(normalizarStatus(dto.getStatusAnterior()));
        dto.setStatusNovo(normalizarStatus(dto.getStatusNovo()));
        return dto;
    }

    private String normalizarStatus(String status) {
        if (status == null) {
            return null;
        }
        String valor = status.trim();
        String valorLower = valor.toLowerCase();
        if (valor.equalsIgnoreCase("Respondido - Encerrado")) {
            return "Encerrado";
        }
        if (valorLower.startsWith("respondido")) {
            return "Respondido";
        }
        if (valorLower.startsWith("conclus") || valorLower.startsWith("conclu")) {
            return "Concluído";
        }
        if (valorLower.startsWith("encerrado")) {
            return "Encerrado";
        }
        if (valorLower.startsWith("encaminh")) {
            return "Em andamento";
        }
        if (valorLower.startsWith("aguard")) {
            return "Em andamento";
        }
        if (valorLower.startsWith("expirado")) {
            return "Expirado";
        }
        return valor;
    }
}
