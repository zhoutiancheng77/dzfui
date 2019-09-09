package com.dzf.zxkj.gateway.filter;

import com.dzf.zxkj.platform.api.IPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
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

    @Reference(version = "1.0.1", protocol = "dubbo", timeout = 9000)
    private IPermissionService permissionService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //验证用户
        //校验权限

        permissionService.queryAllPermissionVo();

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
