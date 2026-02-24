package br.gov.creasvm.processos_sei.dto;

import br.gov.creasvm.processos_sei.model.Role;

public record RegisterDTO(String login, String senha, Role role) {
}
