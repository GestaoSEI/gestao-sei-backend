package br.gov.gestaosei.gestao_sei_backend.controller;

import br.gov.gestaosei.gestao_sei_backend.dto.ErrorResponse;
import br.gov.gestaosei.gestao_sei_backend.dto.UsuarioDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários do sistema")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

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
            @Parameter(description = "Login do usuário", example = "joao.silva", required = true)
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
            description = "Dados inválidos ou login já existe",
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
                          "login": "joao.silva",
                          "role": "USER"
                        }
                        """
                    )
                )
            )
            @RequestBody @Valid UsuarioDTO dto) {
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
            description = "Dados inválidos ou login já existe",
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
                          "login": "joao.silva.atualizado",
                          "role": "ADMIN"
                        }
                        """
                    )
                )
            )
            @RequestBody @Valid UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }
}
