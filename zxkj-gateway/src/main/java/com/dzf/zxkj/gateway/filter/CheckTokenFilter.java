package com.dzf.zxkj.gateway.filter;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.common.enums.HttpStatusEnum;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
@SuppressWarnings("all")
public class CheckTokenFilter implements GlobalFilter, Ordered {

    @Reference(version = "1.0.0")
    private IAuthService authService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getQueryParams().getFirst("authToken");
        //重定向到登录界面
        if(!StringUtil.isEmpty(token)){
            boolean b = authService.validateTokenByInter(token);
            ServerHttpResponse response = exchange.getResponse();
            if(b){
                String url = "http://127.0.0.1:8521/login?token=" + token;

                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION, url);
                return response.setComplete();
            }else{
               return reponse(HttpStatusEnum.EX_TOKEN_ERROR_CODE, response);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> reponse(HttpStatusEnum httpStatus, ServerHttpResponse response) {
        JSONObject message = new JSONObject();
        message.put("status", httpStatus.value());
        message.put("message", httpStatus.msg());
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
