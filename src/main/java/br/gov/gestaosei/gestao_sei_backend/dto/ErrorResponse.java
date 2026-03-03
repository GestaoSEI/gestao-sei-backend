package br.gov.gestaosei.gestao_sei_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO para respostas de erro da API")
public record ErrorResponse(
    
    @Schema(description = "Timestamp do erro", example = "2026-02-27T00:43:21")
    LocalDateTime timestamp,
    
    @Schema(description = "Status HTTP", example = "400")
    Integer status,
    
    @Schema(description = "Tipo do erro", example = "Bad Request")
    String error,
    
    @Schema(description = "Mensagem detalhada do erro", example = "Login é obrigatório")
    String message,
    
    @Schema(description = "Caminho da requisição", example = "/api/usuarios")
    String path
) {
}
