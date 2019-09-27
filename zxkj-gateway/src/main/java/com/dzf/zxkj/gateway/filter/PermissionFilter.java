package com.dzf.zxkj.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.common.cache.CorpBean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.auth.model.CorpModel;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @Auther: dandelion
 * @Date: 2019-09-03
 * @Description:
 */
@Configuration
@Slf4j
@SuppressWarnings("all")
public class PermissionFilter implements GlobalFilter, Ordered {

    @Reference
    private IAuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst("x-token");
        String pk_corp = headers.getFirst("pk_corp");
        ServerHttpResponse response = exchange.getResponse();
        CorpModel corpModel = null;
        try {
            corpModel = authService.queryCorpByPk(pk_corp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(corpModel == null){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        ServerHttpRequestDecorator serverHttpRequestDecorator = new ServerHttpRequestDecorator(request){
            @Override
            public Flux<DataBuffer> getBody() {
                Flux<DataBuffer> body = super.getBody();
                return body.map(dataBuffer -> {
                    byte[] content = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(content);
                    //释放掉内存
                    DataBufferUtils.release(dataBuffer);
                    //这个就是request body的json格式数据
                    String bodyJson = new String(content, Charset.forName("UTF-8"));
                    //转化成json对象
                    JSONObject jsonObject = JSON.parseObject(bodyJson);

                    CorpBean corpBean = new CorpBean();
                    corpBean.setPk_corp("123");
                    corpBean.setBegindate(new DZFDate());
                    //把用户id和客户端id添加到json对象中
                    jsonObject.put("corpVO", corpBean);
                    String result = jsonObject.toJSONString();
                    //转成字节
                    byte[] bytes = result.getBytes();

                    NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
                    DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
                    buffer.write(bytes);
                    return buffer;
                });
            }
            //复写getHeaders方法，删除content-length
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                //由于修改了请求体的body，导致content-length长度不确定，因此使用分块编码
                httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                return httpHeaders;
            }
        };

        return chain.filter(exchange.mutate().request(serverHttpRequestDecorator).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
