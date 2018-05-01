package com.wuwii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by KronChan on 2018/5/1 11:54.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ResourceFirstApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResourceFirstApplication.class, args);
    }
}
