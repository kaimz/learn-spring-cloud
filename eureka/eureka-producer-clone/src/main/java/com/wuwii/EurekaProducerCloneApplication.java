package com.wuwii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
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
