package br.gov.gestaosei.gestao_sei_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.gov.gestaosei.gestao_sei_backend.repository")
@EntityScan(basePackages = "br.gov.gestaosei.gestao_sei_backend.model")
public class GestaoSeiBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(GestaoSeiBackendApplication.class, args);
	}

}
