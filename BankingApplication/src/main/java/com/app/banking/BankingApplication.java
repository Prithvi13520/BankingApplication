package com.app.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "Banking Application",
		description = "REST APIs for Banking Application",
		version = "v1.0",
		contact = @Contact(
			name = "Prithvi",
			email = "Prithvi13520@gmail.com",
			url = "https://github.com/Prithvi13520/BankingApplication"
		),
		license = @License(
			name = "Banking Application",
			url = "https://github.com/Prithvi13520/BankingApplication"
		)
	),
	externalDocs = @ExternalDocumentation(
		description = "Spring Boot Application for Banking Services",
		url = "https://github.com/Prithvi13520/BankingApplication"
	)
)
public class BankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}

}
