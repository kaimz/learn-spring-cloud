熔断器 Hystrix 的使用

1. 添加相应的 jar 包 `spring-cloud-starter-hystrix`
2. 在启动类上加上开启注解 `@EnableCircuitBreaker `
3. 在需要熔断的服务方法加上 `@HystrixCommand` 注解，并且指定调用该方法出现异常后调用的方法名，参数不用写，但是两个方法的参数保持一致