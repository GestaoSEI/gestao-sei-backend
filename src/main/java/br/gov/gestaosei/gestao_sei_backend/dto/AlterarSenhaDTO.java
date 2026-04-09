package br.gov.gestaosei.gestao_sei_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaDTO(

        String senhaAtual,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 4, message = "Nova senha deve ter no mínimo 4 caracteres")
        String novaSenha
) {}
