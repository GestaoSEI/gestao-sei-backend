package br.gov.creasvm.processos_sei.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Processos SEI - CREASVM")
                        .description("API para gerenciamento de processos do Sistema Eletrônico de Informações")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Suporte CREASVM")
                                .email("suporte@creasvm.gov.br"))
                        .license(new License()
                                .name("Licença Pública")
                                .url("https://www.gov.br/")));
    }
}