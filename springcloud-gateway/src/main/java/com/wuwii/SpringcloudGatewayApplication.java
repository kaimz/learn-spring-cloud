package com.wuwii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringcloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudGatewayApplication.class, args);
    }

    @Bean
    public RouteDefinitionLocator discoveryClientRouteDefinitionLocator(DiscoveryClient discoveryClient) {
        return new DiscoveryClientRouteDefinitionLocator(discoveryClient);
    }
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


}
