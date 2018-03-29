package com.wuwii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class ZipkinApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZipkinApplication.class, args);
	}
	/**
	 * logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ZipkinApplication.class);

	@GetMapping("/")
	public String home() {
		log.info("Handling home");
		return "Hello World";
	}
}
