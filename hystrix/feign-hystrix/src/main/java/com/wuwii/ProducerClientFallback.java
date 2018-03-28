package com.wuwii;

import org.springframework.stereotype.Component;

/**
 * @author Zhang Kai
 * @version 1.0
 * @since <pre>2018/3/28 10:00</pre>
 */
@Component
public class ProducerClientFallback implements ProducerClient {
    @Override
    public String dc() {
        return "Got the message form remote producer failed.";
    }
}
