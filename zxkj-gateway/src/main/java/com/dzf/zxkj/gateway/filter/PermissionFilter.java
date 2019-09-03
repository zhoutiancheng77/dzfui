package com.dzf.zxkj.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Auther: dandelion
 * @Date: 2019-09-03
 * @Description:
 */
@Configuration
@Slf4j
public class PermissionFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("check token and user permission....");
        ServerHttpRequest request = exchange.getRequest();
        log.info(request.getPath().value());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
