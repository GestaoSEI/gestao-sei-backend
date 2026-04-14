package br.gov.gestaosei.gestao_sei_backend.controller;

import br.gov.gestaosei.gestao_sei_backend.dto.AlterarSenhaDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ErrorResponse;
import br.gov.gestaosei.gestao_sei_backend.dto.UsuarioDTO;
import br.gov.gestaosei.gestao_sei_backend.model.Role;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import br.gov.gestaosei.gestao_sei_backend.service.RelatorioService;
import br.gov.gestaosei.gestao_sei_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários do sistema")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping
    @Operation(
        summary = "Listar todos usuários",
        description = "Retorna uma lista com todos os usuários cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuários retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDTO.class)
            )
        )
    })
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        if (!isAdminAutenticado()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/login/{login}")
    @Operation(
        summary = "Buscar usuário por login",
        description = "Retorna os dados de um usuário específico pelo seu login (forma amigável)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<UsuarioDTO> buscarPorLogin(
            @Parameter(description = "Login atual ou e-mail do usuário", example = "joao.silva@orgao.gov.br", required = true)
            @PathVariable String login) {
        return ResponseEntity.ok(usuarioService.buscarPorLogin(login));
    }

    @PostMapping
    @Operation(
        summary = "Criar novo usuário",
        description = "Cria um novo usuário no sistema com senha padrão 'senha123'"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário criado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou e-mail já existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<UsuarioDTO> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do novo usuário",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "nomeCompleto": "João da Silva",
                          "email": "joao.silva@orgao.gov.br",
                          "dataNascimento": "1985-07-21",
                          "role": "USER"
                        }
                        """
                    )
                )
            )
            @RequestBody @Valid UsuarioDTO dto) {
        if (!isAdminAutenticado()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(usuarioService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar usuário",
        description = "Atualiza os dados de um usuário existente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário atualizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou e-mail já existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<UsuarioDTO> atualizar(
            @Parameter(description = "ID do usuário", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados atualizados do usuário",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "nomeCompleto": "João da Silva Atualizado",
                          "email": "joao.silva.atualizado@orgao.gov.br",
                          "dataNascimento": "1985-07-21",
                          "role": "ADMIN"
                        }
                        """
                    )
                )
            )
            @RequestBody @Valid UsuarioDTO dto) {
        if (!isAdminAutenticado()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir usuário",
        description = "Remove um usuário. Bloqueado se o usuário possuir registros no histórico de processos. Requer perfil ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
        @ApiResponse(
            responseCode = "403",
            description = "Acesso negado — requer perfil ADMIN",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Usuário possui histórico de processos e não pode ser excluído",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID do usuário", example = "2", required = true)
            @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof Usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario currentUser = (Usuario) auth.getPrincipal();
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/senha")
    @Operation(
        summary = "Alterar senha",
        description = "Altera a senha de um usuário. ADMIN pode alterar qualquer senha sem senha atual. USER pode alterar apenas a própria senha, informando a senha atual."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
        @ApiResponse(
            responseCode = "400",
            description = "Senha atual incorreta ou dados inválidos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autenticado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Sem permissão para alterar senha de outro usuário",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<Void> alterarSenha(
            @Parameter(description = "ID do usuário", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados para alteração de senha",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                        {
                          "senhaAtual": "senhaAnterior",
                          "novaSenha": "novaSenha123"
                        }
                        """)
                )
            )
            @RequestBody @Valid AlterarSenhaDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof Usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario currentUser = (Usuario) auth.getPrincipal();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        usuarioService.alterarSenha(id, dto, currentUser.getLogin(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorio")
    @Operation(summary = "Gerar relatório PDF de usuários", description = "Gera um relatório PDF com a lista de todos os usuários cadastrados")
    public ResponseEntity<byte[]> gerarRelatorio() {
        if (!isAdminAutenticado()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            List<UsuarioDTO> usuarios = usuarioService.listarTodos();
            byte[] pdfBytes = relatorioService.gerarRelatorioUsuarios(usuarios);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio_usuarios.pdf");
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isAdminAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Usuario currentUser)) {
            return false;
        }
        return currentUser.getRole() == Role.ADMIN;
    }
}
