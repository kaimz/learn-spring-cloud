package com.wuwii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class EurekaClientApplication {

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(EurekaClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientApplication.class, args);
	}

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 服务提供者，测试提供某个服务出去，让消费者进行消费，查看测试的日志输出，注册中心已经注册了的服务名称，
     */
	@GetMapping
    public String dc() {
        String service = discoveryClient.getServices().toString();
        log.info(">>>>>>>> service : {}", service);
        return "I am the first producer";
    }
}
