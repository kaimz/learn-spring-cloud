package com.wuwii;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Zhang Kai
 * @version 1.0
 * @since <pre>2018/3/27 15:37</pre>
 */
@Service
public class ConsumerService {
    @Autowired
    private RestTemplate restTemplate;


    /**
     * 使用@HystrixCommand注解指定当该方法发生异常时调用的方法
     */
    @HystrixCommand(fallbackMethod = "getRemoteStrFail")
    public String getRemoteStr() {
        return restTemplate.getForObject("http://eureka-producer-1/", String.class);
    }

    /**
     * hystrix fallback方法
     */
    public String getRemoteStrFail() {
        return "Got the remote string failed.";
    }
}
