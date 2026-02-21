package br.gov.creasvm.processos_sei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.gov.creasvm.processos_sei.repository")
@EntityScan(basePackages = "br.gov.creasvm.processos_sei.model")
public class ProcessosSeiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProcessosSeiApplication.class, args);
	}

}
