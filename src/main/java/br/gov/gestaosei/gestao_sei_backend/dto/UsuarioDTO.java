package br.gov.gestaosei.gestao_sei_backend.dto;

import br.gov.gestaosei.gestao_sei_backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para transferência de dados de usuários")
public record UsuarioDTO(
    
    @Schema(description = "ID do usuário", example = "1")
    Long id,
    
    @Schema(description = "Login do usuário", example = "joao.silva", required = true)
    @NotBlank(message = "Login é obrigatório")
    String login,
    
    @Schema(description = "Perfil do usuário", example = "USER", required = true)
    @NotNull(message = "Perfil é obrigatório")
    Role role
) {
}
