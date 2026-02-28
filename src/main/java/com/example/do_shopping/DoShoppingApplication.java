package com.example.do_shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class DoShoppingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoShoppingApplication.class, args);
	}

}
