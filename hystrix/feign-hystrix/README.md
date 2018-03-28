Feign Hystrix

1.加入依赖：
```xml
<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>
```
2.Feign 已经拥有负载均衡 Ribbon 和 Hystrix 熔断器的依赖，但是默认熔断器没有开启，需要显示的在系统的配置文件中开启：
```yaml
feign:
  hystrix:
    enabled: true
```
3.开启`@EnableFeignClients`

4.编写服务提供者 `ProducerClient`

5.实现 `Fallback` （可选） 和 `Configuration` （可选）
