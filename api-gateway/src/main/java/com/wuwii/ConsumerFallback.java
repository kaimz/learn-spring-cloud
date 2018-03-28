package com.wuwii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 针对某个微服务的异常状态进行控制，
 * 这个方法是对 feign-hystrix 的微服务的异常状态进行控制，服务有问题需要降级返回这个类中的错误信息
 * @author Zhang Kai
 * @version 1.0
 * @since <pre>2018/3/28 18:58</pre>
 */
@Component
public class ConsumerFallback implements FallbackProvider {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(ConsumerFallback.class);
    /**
     * 返回需要进行服务降级的服务名，
     * 注意的是注册到eureka中的服务名，服务网关中的 serviceId
     */
    @Override
    public String getRoute() {
        return "feign-hystrix";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        log.error(cause.getMessage(), cause);
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.BAD_REQUEST.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return "Bad Request";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("The request was failed.".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return headers;
            }
        };
    }
}
