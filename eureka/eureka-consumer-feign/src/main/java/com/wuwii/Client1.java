package com.wuwii;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author KronChan
 * @version 1.0
 * @since <pre>2018/3/25 11:26</pre>
 */
@FeignClient("eureka-producer-1")
public interface Client1 {

    /**
     * 注意的是默认地址需要加上 “/” ，不然会出现解析错误
     */
    @GetMapping("/")
    String dc();
}
