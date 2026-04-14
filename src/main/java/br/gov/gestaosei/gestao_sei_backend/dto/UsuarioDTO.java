package br.gov.gestaosei.gestao_sei_backend.dto;

import br.gov.gestaosei.gestao_sei_backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para transferência de dados de usuários")
public class UsuarioDTO {
    
    @Schema(description = "ID do usuário", example = "1")
    private Long id;
    
    @Schema(description = "Login atual do usuário", example = "joao.silva@orgao.gov.br")
    private String login;

    @Schema(description = "Nome completo do usuário", example = "João da Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nome completo é obrigatório")
    @Pattern(regexp = "^[\\p{L}][\\p{L}\\p{M} .'-]*$", message = "Nome completo contém caracteres inválidos")
    private String nomeCompleto;

    @Schema(description = "E-mail do usuário, utilizado como login", example = "joao.silva@orgao.gov.br", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @Schema(description = "Data de nascimento do usuário", example = "1985-07-21", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser anterior à data atual")
    private LocalDate dataNascimento;
    
    @Schema(description = "Perfil do usuário", example = "USER", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Perfil é obrigatório")
    private Role role;
}
