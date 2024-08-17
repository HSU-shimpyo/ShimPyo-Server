package com.hsu.shimpyoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ShimpyooApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShimpyooApplication.class, args);
	}

}
