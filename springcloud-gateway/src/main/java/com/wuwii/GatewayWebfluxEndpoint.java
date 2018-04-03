package com.wuwii;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping("/gateway")
public class GatewayWebfluxEndpoint implements ApplicationEventPublisherAware {
    /**
     * 路由定义定位器
     */
    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;
    /**
     * 全局过滤器
     */
    @Autowired
    private List<GlobalFilter> globalFilters;
    /**
     * 网关过滤器工厂
     */
    @Autowired
    private List<GatewayFilterFactory> gatewayFilters;
    /**
     * 存储器 RouteDefinitionLocator 对象
     */
    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;
    /**
     * 路由定位器
     */
    @Autowired
    private RouteLocator routeLocator;

    /**
     * 应用事件发布器
     */
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

    }

    /**
     *
     * @return 路由过滤器工厂列表
     */
    @GetMapping("/globalfilters")
    public Mono<HashMap<String, Object>> globalfilters() {
        return getNamesToOrders(this.globalFilters);
    }

    /**
     *
     * @return 路由过滤器工厂列表
     */
    @GetMapping("/routefilters")
    public Mono<HashMap<String, Object>> routefilers() {
        return getNamesToOrders(this.gatewayFilters);
    }

    /**
     * 路由列表
     */
    @GetMapping("/routes")
    public Mono<Map<String, List>> routes() {
        Mono<List<RouteDefinition>> routeDefs = this.routeDefinitionLocator.getRouteDefinitions().collectList();
        Mono<List<Route>> routes = this.routeLocator.getRoutes().collectList();
        return Mono.zip(routeDefs, routes).map(tuple -> {
            Map<String, List> allRoutes = new HashMap<>();
            allRoutes.put("routeDefinitions", tuple.getT1());
            allRoutes.put("routes", tuple.getT2());
            return allRoutes;
        });
    }

    /**
     *  单个路由信息
     */
    @GetMapping("/routes/{id}")
    public Mono<ResponseEntity<RouteDefinition>> route(@PathVariable String id) {
        //TODO: missing RouteLocator
        return this.routeDefinitionLocator.getRouteDefinitions()
                .filter(route -> route.getId().equals(id))
                .singleOrEmpty()
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * 单个路由的过滤器
     */
    @GetMapping("/routes/{id}/combinedfilters")
    public Mono<HashMap<String, Object>> combinedfilters(@PathVariable String id) {
        //TODO: missing global filters
        return this.routeLocator.getRoutes()
                .filter(route -> route.getId().equals(id))
                .reduce(new HashMap<>(), this::putItem);
    }

    /**
     * 新增路由
     * @param id
     * @param route
     * @return
     */
    @PostMapping("/routes/{id}")
    @SuppressWarnings("unchecked")
    public Mono<ResponseEntity<Void>> save(@PathVariable String id, @RequestBody Mono<RouteDefinition> route) {
        return this.routeDefinitionWriter.save(route.map(r ->  { // 设置 ID
            r.setId(id);
            //log.debug("Saving route: " + route);
            return r;
        })).then(Mono.defer(() -> // status ：201 ，创建成功。参见 HTTP 规范 ：https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/201
                Mono.just(ResponseEntity.created(URI.create("/routes/"+id)).build())
        ));
    }

    /**
     * 删除路由
     * @param id
     * @return
     */
    @DeleteMapping("/routes/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable String id) {
        return this.routeDefinitionWriter.delete(Mono.just(id))
                .then(Mono.defer(() -> Mono.just(ResponseEntity.ok().build()))) // 删除成功
                .onErrorResume(t -> t instanceof NotFoundException, t -> Mono.just(ResponseEntity.notFound().build())); // 删除失败
    }

    /**
     * 刷新缓存
     */
    @PostMapping("/refresh")
    public Mono<Void> refresh() {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return Mono.empty();
    }


    private <T> Mono<HashMap<String, Object>> getNamesToOrders(List<T> list) {
        return Flux.fromIterable(list).reduce(new HashMap<>(), this::putItem);
    }
    private HashMap<String, Object> putItem(HashMap<String, Object> map, Object o) {
        Integer order = null;
        if (o instanceof Ordered) {
            order = ((Ordered)o).getOrder();
        }
        //filters.put(o.getClass().getName(), order);
        map.put(o.toString(), order);
        return map;
    }
}
