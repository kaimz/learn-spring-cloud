前言

伴随着 Spring Boot 2 的发布，Spring Cloud 2 也新增了不少新的功能，其中由于第一版本中的 API 网关 zuul 1 是阻塞 API ，在长连接上存在着性能问题。Spring Cloud Gateway 毫无疑问成了亮点。

可以学习源码源码地址

这篇文章主要是从芋道源码：Spring-Cloud-Gateway 学习，解决了很多问题和疑惑，非常感谢。

同时参考官方的文档Spring Cloud Gateway

所以看源码还是非常有用的。

<!--more-->

使用路由

在 pom.xml 中添加 spring-cloud-starter-gateway 依赖：

            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-gateway</artifactId>
            </dependency>

路由断言

        /**
         * 路由断言工厂
         *
         * 步骤：
         * 1. 设置路由规则
         * 2. 设置返回
         */
        @Bean
        public RouterFunction<ServerResponse> helloRouterFunction() {
            // public static <T extends ServerResponse> RouterFunction<T> route(RequestPredicate predicate,
            //			HandlerFunction<T> handlerFunction)
            RouterFunction<ServerResponse> route = RouterFunctions.route(
                    path("/hello"),
                    request -> ServerResponse.ok().body(BodyInserters.fromObject("Hello World.")))
                    .andRoute(RequestPredicates.GET("/kronchan"),
                            r -> ServerResponse
                                    .ok()
                                    .body(BodyInserters.fromObject("Hello KronChan.")));
            return route;
        }

启动应用可以发现控制台下面有一段信息：

    apped /hello -> com.wuwii.SpringcloudGatewayApplication$$Lambda$244/240000757@57562473
    (GET && /kronchan) -> com.wuwii.SpringcloudGatewayApplication$$Lambda$246/1565096593@7a360554
    

然后我们使用 GET 请求/kronchan，查看返回内容

使用 RouteLocator 路由定位器

        @Bean
        public RouteLocatorBuilder routeLocatorBuilder(ConfigurableApplicationContext context) {
            return new RouteLocatorBuilder(context);
        }
    
        /**
         * 路由过滤器工厂。
         * 网关经常需要对路由请求进行过滤，进行一些操作，如鉴权之后构造头部之类的，
         * 过滤的种类很多，如增加请求头、增加请求参数、增加响应头和断路器等等功能。
         */
        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
            String url = "http://wuwii.com";
            return builder.routes()
                    // public Builder route(Function<PredicateSpec, Builder> fn)
                    .route(r -> r.order(1) // # int 当请求匹配到多个路由时，使用 order 值小的路由，默认为 0
                                    .path("/wuwii") // 设置 /wuwii 的路由规则，准备转发到 http://wuwii.com
                                    .filters(f ->
                                            // 对响应进行过滤处理，增加响应的头部 X-CustomHeader zk，这个在成功加载完页面后可以进行查看结果
                                            f.addResponseHeader("X-CustomHeader", "zk")
                                                    // 路径的过滤器 stripPrefix(int parts) 去除第一个前缀，
                                                    // 这个是按照 “/” 划分的，例如这个例子中：
                                                    // 我路由规则 /wuwii 是要把它转发到 Url "http://wuwii.com"，
                                                    // 如果我不加下面这段代码，它最终请求的地址是 `http://wuwii.com/wuwii`。
                                                    .stripPrefix(1))
                                    // 设置路由规则转发的 uri，
                                    // 转发到注册中心的服务 lb://application-name，
                                    // lb 含义是 LoadBalancerClientFilter 选择一个服务 负载均衡
                                    // 设置转发到websocket服务 ws://localhost:9000
                                    .uri(url)
                            // 光上面可不行，我们还需要获取这个页面的样式和脚本，
                            // 因为我样式和脚本使用的相对路径，我们转发的 Url 其实还是本地的，
                            // 比如 "http://localhost:8012/js/index.js"，我们本地没有这个文件，当然会 Not Found，
                            // 根据这个结果可以改造一下就可以了。
                            // 下面是获取上个 Url 页面的 js 和 css 则不需要进行去除 path 的操作
                    ).route("js-and-css", r -> r.path("/js/main.css").or().path("/js/index.js")
                            .uri(url))
                    .build();
                    // 经过上面的处理，我们就转发路由规则到 http://wuwii.com 的整个页面，可以试试了。
        }

重新启动应用，我们访问 /wuwii，本地的 url 转发到了 http://wuwii.com 

这部分具体可以参考Spring-Cloud-Gateway 源码解析 —— 路由（2.3）之 Java 自定义 RouteLocator

服务注册组件

1. 这个时候我们需要使用服务注册，我使用的是 Eureka，在 pom.xml 加入客户端的依赖：
               <dependency>
                   <groupId>org.springframework.cloud</groupId>
                   <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
               </dependency>
       
   然后新开启服务提供者 feign-hystrix ，新加入一个 /consume 服务，启动它。
2. 然后在系统配置文件加入的注册服务中心信息：
       eureka:
         client:
           service-url:
             defaultZone: http://k.wuwii.com:1001/eureka
   

1. 通过调用 DiscoveryClient 获取注册在注册中心的服务列表，将服务注册路由。
           @Bean
           public RouteDefinitionLocator discoveryClientRouteDefinitionLocator(DiscoveryClient discoveryClient) {
               return new DiscoveryClientRouteDefinitionLocator(discoveryClient);
           }
   

使用服务注册组件自动将我们注册在 Eureka 中的服务按照程序中默认的设置方式设置路由规则，源码：

    	@Override
    	public Flux<RouteDefinition> getRouteDefinitions() {
    		return Flux.fromIterable(discoveryClient.getServices())
    				.map(serviceId -> {
    					RouteDefinition routeDefinition = new RouteDefinition();
    					routeDefinition.setId(this.routeIdPrefix + serviceId);
    					routeDefinition.setUri(URI.create("lb://" + serviceId));
    
    					// add a predicate that matches the url at /serviceId
    					/*PredicateDefinition barePredicate = new PredicateDefinition();
    					barePredicate.setName(normalizePredicateName(PathRoutePredicate.class));
    					barePredicate.addArg(PATTERN_KEY, "/" + serviceId);
    					routeDefinition.getPredicates().add(barePredicate);*/
    
    					// add a predicate that matches the url at /serviceId/**
    					PredicateDefinition subPredicate = new PredicateDefinition();
    					subPredicate.setName(normalizeRoutePredicateName(PathRoutePredicateFactory.class));
    					subPredicate.addArg(PATTERN_KEY, "/" + serviceId + "/**");
    					routeDefinition.getPredicates().add(subPredicate);
    
    					//TODO: support for other default predicates
    
    					// add a filter that removes /serviceId by default
    					FilterDefinition filter = new FilterDefinition();
    					filter.setName(normalizeFilterFactoryName(RewritePathGatewayFilterFactory.class));
    					String regex = "/" + serviceId + "/(?<remaining>.*)";
    					String replacement = "/${remaining}";
    					filter.addArg(REGEXP_KEY, regex);
    					filter.addArg(REPLACEMENT_KEY, replacement);
    					routeDefinition.getFilters().add(filter);
    
    					//TODO: support for default filters
    
    					return routeDefinition;
    				});
    	}

重启应用，访问 /feign-hystrix/consume。

具体可以参考Spring-Cloud-Gateway 源码解析 —— 路由（1.4）之 DiscoveryClientRouteDefinitionLocator 注册中心

使用配置文件自定义

    spring:
      application:
        name: springcloud-gateway
      cloud:
        gateway:
          enable: true # 参考类 GatewayAutoConfiguration， 网关开启与关闭
          # 设置全局的过滤器。
          #default-filters:
          #- AddResponseHeader=X-Response-Default-Foo, Default-Bar
    
          # 设置 route
          routes:
          - id: feign-hystrix1
            uri: http://wuwii.com
            order: -1 # int 当请求匹配到多个路由时，使用 order 值小的路由，默认为 0，因此我们使用服务发现组件进行注册的路由的  Order都为0。
            predicates:
            - Path=/feign-hystrix/**
            filters:
            - StripPrefix=1

上面我故意顶掉之前系统服务中心的 feign-hystrix 看谁生效。

（order）越小优先级越高，所以它顶掉了上一步使用服务注册组件中的相同的路由规则。

过滤工厂

上面我们多次使用到 FIlter ，它到底能取到哪些值，怎么使用？

它主要是由 GatewayFilterFactory 这个接口实现。看下它的实现，就可以了解到使用哪些过滤器。



具体使用可以参考 Spring-Cloud-Gateway 源码解析 —— 过滤器 (4.2) 之 GatewayFilterFactory 过滤器工厂

使用的话可以参考官方的文档GatewayFilter Factories

熔断机制

参考官方文档5.4 Hystrix GatewayFilter Factory

The Hystrix GatewayFilter Factory requires a single name parameter, which is the name of the HystrixCommand.

application.yml. 

    spring:
      cloud:
        gateway:
          routes:
          - id: hystrix_route
            uri: http://example.org
            filters:
            - Hystrix=myCommandName

This wraps the remaining filters in a HystrixCommand with command name myCommandName.

The Hystrix filter can also accept an optional fallbackUri parameter. Currently, only forward: schemed URIs are supported. If the fallback is called, the request will be forwarded to the controller matched by the URI.

application.yml. 

    spring:
      cloud:
        gateway:
          routes:
          - id: hystrix_route
            uri: lb://backing-service:8088
            predicates:
            - Path=/consumingserviceendpoint
            filters:
            - name: Hystrix
              args:
                name: fallbackcmd
                fallbackUri: forward:/incaseoffailureusethis
            - RewritePath=/consumingserviceendpoint, /backingserviceendpoint

This will forward to the /incaseoffailureusethis URI when the Hystrix fallback is called. Note that this example also demonstrates (optional) Spring Cloud Netflix Ribbon load-balancing via the lb prefix on the destination URI.



我来写个简单的，出现服务降级，进行住转发到 /fallback

    # 设置 route
    routes:
    - id: feign-hystrix1
      uri: lb://feign-hystrix
      order: -1
      predicates:
      - Path=/consumer/**
      filters:
      - StripPrefix=1
      - name: Hystrix
        args:
         name: fallbackcmd
         fallbackUri: forward:/fallback # 自定义 fallbackUrl
      #- Hystrix=myCommandName # 熔断器，这个直接返回空的

在 API GATEWAY 工程中新建一个 fallback 的 服务：

        @GetMapping("/fallback")
        public String fallback() {
            return "Cannot got the server.";
        }

然后还有一个问题，这个必须设置，不然不能成功路由：

    # 目前 Hystrix Command 执行超时时，返回客户端 504 状态码，
    # 如果使用 JSON 格式作为数据返回，则需要修改下该 HystrixGatewayFilter 的代码实现。
    hystrix:
      command:
        default:
          execution:
            isolation:
              thread:
                timeoutInMilliseconds: 10000



然后停止 feign-hystrix1  这个服务提供者，查看是否完成服务降级。

限流机制

未完成

参考Spring-Cloud-Gateway 源码解析 —— 过滤器 (4.10) 之 RequestRateLimiterGatewayFilterFactory 请求限流

网关管理

参考Spring-Cloud-Gateway 源码解析 —— 网关管理 HTTP API


