服务网关（Api Gateway）
1. 加入 zuul 的依赖 `spring-cloud-starter-netflix-zuul`
2. 在系统入口启动类上加入注解 `EnableZuulProxy` 表示开启 ZUUL 代理
3. 在系统配置文件 `application.yml` 文件中加入服务网管需要路由的规则
4. 可以使用服务网关完成过滤，在`GatewayFilter`中。可以应用权限验证等操作。