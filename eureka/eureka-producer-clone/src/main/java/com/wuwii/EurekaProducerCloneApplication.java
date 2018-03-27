package com.wuwii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class EurekaProducerCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaProducerCloneApplication.class, args);
	}

	@GetMapping
	public String dc() {
		return "I am the second producer";
	}
}
