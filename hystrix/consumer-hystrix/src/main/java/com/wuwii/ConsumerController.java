package com.wuwii;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhang Kai
 * @version 1.0
 * @since <pre>2018/3/27 15:38</pre>
 */
@RestController
public class ConsumerController {
    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/consume")
    public String consume() {
        return consumerService.getRemoteStr();
    }
}
