package com.wuwii;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhang Kai
 * @version 1.0
 * @since <pre>2018/3/28 9:57</pre>
 */
@Configuration
public class FeignConfiguration {

    /**
     * Controls the level of logging.
     */
    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }

}
