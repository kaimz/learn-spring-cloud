package com.wuwii;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author KronChan
 * @version 1.0
 * @since <pre>2018/3/25 11:28</pre>
 */
@RestController
public class Controller {
    @Autowired
    private Client1 client1;

    /**
     * 消费 client1 中的某个 rest api
     */
    @GetMapping("/consume")
    public String consume() {
        return client1.dc();
    }
}
