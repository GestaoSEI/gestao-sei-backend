package br.gov.gestaosei.gestao_sei_backend.dto;

import br.gov.gestaosei.gestao_sei_backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para transferência de dados de usuários")
public class UsuarioDTO {
    
    @Schema(description = "ID do usuário", example = "1")
    private Long id;
    
    @Schema(description = "Login do usuário", example = "joao.silva", required = true)
    @NotBlank(message = "Login é obrigatório")
    private String login;
    
    @Schema(description = "Perfil do usuário", example = "USER", required = true)
    @NotNull(message = "Perfil é obrigatório")
    private Role role;
}
