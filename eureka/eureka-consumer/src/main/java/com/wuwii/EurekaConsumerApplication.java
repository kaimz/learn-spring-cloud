package com.wuwii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@Configuration
@RestController
public class EurekaConsumerApplication {

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(EurekaConsumerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 消费者消费 服务提供者 （eureka-client-1） 的某个服务（url），使用 rest 方式
     */
    @GetMapping("/consume")
    public String consume() {
        ServiceInstance serviceInstance = loadBalancerClient.choose("eureka-client-1");
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort();
        log.info(">>>>>>>> Consume the url is: {}", url);
        return restTemplate().getForObject(url, String.class);
    }
}
