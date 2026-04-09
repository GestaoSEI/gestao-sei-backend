package br.gov.gestaosei.gestao_sei_backend.controller;

import br.gov.gestaosei.gestao_sei_backend.dto.AuthenticationDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.LoginResponseDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.RegisterDTO;
import br.gov.gestaosei.gestao_sei_backend.dto.ResetPasswordDTO;
import br.gov.gestaosei.gestao_sei_backend.exception.ErrorResponse;
import br.gov.gestaosei.gestao_sei_backend.model.Role;
import br.gov.gestaosei.gestao_sei_backend.model.Usuario;
import br.gov.gestaosei.gestao_sei_backend.repository.UsuarioRepository;
import br.gov.gestaosei.gestao_sei_backend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "API para autenticação e registro de usuários")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    @Operation(
        summary = "Login de usuário",
        description = "Autentica um usuário no sistema e retorna um token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciais inválidas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de login inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Credenciais de login",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "login": "joao.silva",
                          "senha": "123456"
                        }
                        """
                    )
                )
            )
            @RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    @Operation(
        summary = "Registrar novo usuário",
        description = "Cria um novo usuário no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário criado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Login já existe ou dados inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do novo usuário",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RegisterDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "login": "joao.silva",
                          "senha": "123456",
                          "role": "USER"
                        }
                        """
                    )
                )
            )
            @RequestBody @Valid RegisterDTO data) {
        if (usuarioRepository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());
        Role userRole = Role.valueOf(data.role());
        Usuario newUser = new Usuario(data.login(), encryptedPassword, userRole);

        usuarioRepository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(
        summary = "Redefinir senha",
        description = "Redefine a senha de um usuário informado pelo login"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Senha redefinida com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Login não encontrado ou dados inválidos",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity resetPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados para redefinir senha",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ResetPasswordDTO.class),
                    examples = @ExampleObject(
                        value = """
                        {
                          "login": "joao.silva",
                          "novaSenha": "NovaSenha@123"
                        }
                        """
                    )
                )
            )
            @RequestBody @Valid ResetPasswordDTO data) {

        Usuario usuario = usuarioRepository.findByLogin(data.login());
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado com o login: " + data.login());
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.novaSenha());
        usuario.setSenha(encryptedPassword);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok().build();
    }
}
