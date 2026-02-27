package br.gov.gestaosei.gestao_sei_backend.dto;

import br.gov.gestaosei.gestao_sei_backend.model.Role;

public record RegisterDTO(String login, String senha, Role role) {
}
