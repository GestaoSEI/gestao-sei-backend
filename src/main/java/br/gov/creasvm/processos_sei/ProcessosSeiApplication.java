package br.gov.creasvm.processos_sei;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "API de Processos SEI",
				version = "1.0",
				description = "API para gerenciamento de processos do Sistema Eletrônico de Informações"))

@EnableJpaRepositories(basePackages = "br.gov.creasvm.processos_sei.repository")
@EntityScan(basePackages = "br.gov.creasvm.processos_sei.model")
public class ProcessosSeiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProcessosSeiApplication.class, args);
	}

}
