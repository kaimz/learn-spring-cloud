package com.wuwii;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhang Kai
 * @version 1.0
 * @since <pre>2018/3/28 10:11</pre>
 */
@RestController
public class ConsumerController {
    @Autowired
    private ProducerClient producerClient;

    /**
     * Test the feign consume.
     */
    @GetMapping("/consume")
    public String consume() {
        return producerClient.dc();
    }
}
