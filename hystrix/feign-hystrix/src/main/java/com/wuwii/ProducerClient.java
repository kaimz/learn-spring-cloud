package com.wuwii;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * value： producer name
 * path：prefix path variable
 * configuration: custom configuration information to override Feign's default configuration.
 * fallback: hystrix
 * </br>
 * @author Zhang Kai
 * @version 1.0
 * @since <pre>2018/3/28 9:53</pre>
 */
@FeignClient(value = "eureka-producer-1", path = "/", configuration = FeignConfiguration.class, fallback = ProducerClientFallback.class)
public interface ProducerClient {
    @GetMapping("/")
    String dc();
}
