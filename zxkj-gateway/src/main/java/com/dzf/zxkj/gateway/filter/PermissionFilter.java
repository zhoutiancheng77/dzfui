package com.dzf.zxkj.gateway.filter;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.gateway.config.GatewayConfig;
import com.dzf.zxkj.platform.auth.model.jwt.IJWTInfo;
import com.dzf.zxkj.platform.auth.model.sys.CorpModel;
import com.dzf.zxkj.platform.auth.model.sys.UserModel;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import com.dzf.zxkj.platform.auth.service.ISysService;
import com.dzf.zxkj.platform.auth.utils.JWTUtil;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Set;

/**
 * @Auther: dandelion
 * @Date: 2019-09-03
 * @Description:
 */
@Configuration
@Slf4j
@SuppressWarnings("all")
public class PermissionFilter implements GlobalFilter, Ordered {

    @Reference(version = "1.0.0")
    private ISysService sysService;

    @Reference(version = "1.0.0")
    private IAuthService authService;

    @Autowired
    private GatewayConfig gatewayConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //登陆请求不验证权限
        System.out.println(path);
        System.out.println(gatewayConfig.getLoginUrl());
        System.out.println(gatewayConfig.getLoginUrl().equals(path));
        if (gatewayConfig.getLoginUrl().equals(path) || (gatewayConfig.getIgnoreUrl() != null && gatewayConfig.getIgnoreUrl().contains(path))) {
            return chain.filter(exchange);
        }

        HttpHeaders headers = request.getHeaders();
        ServerHttpResponse response = exchange.getResponse();
        //token非空判断
        String token = headers.getFirst("X-ACCESS-TOKEN");
        if (StringUtils.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //token校验
        IJWTInfo ijwtInfo = null;
        try {
            ijwtInfo = JWTUtil.getInfoFromToken(token, gatewayConfig.getUserPubKey());
        } catch (Exception e) {
            log.info("token验证失败！");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        String pk_corp = headers.getFirst("pk_corp");
        //用户与公司关联校验
        List<String> corps = authService.getPkCorpByUserId(ijwtInfo.getBody());
        if (corps == null || corps.contains(pk_corp)) {
            log.info("用户没有操作公司权限！");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //参数中存在pk_corp直接使用参数中的
        String queryCorp = request.getQueryParams().getFirst("pk_corp");
        if (StringUtils.isNotBlank(queryCorp)) {
            pk_corp = queryCorp;
        }
        //查询公司和用户vo
        final CorpModel corpModel = sysService.queryCorpByPk(pk_corp);
        final UserModel userModel = sysService.queryByUserId(ijwtInfo.getBody());
        if (corpModel == null || userModel == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //权限校验
        Set<String> allPermissions = authService.getAllPermission();
        Set<String> myPermisssions = authService.getPermisssionByUserid(ijwtInfo.getBody());

        //内置到请求body中
        ServerHttpRequestDecorator serverHttpRequestDecorator = new ServerHttpRequestDecorator(request) {
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
                    //把用户id和客户端id添加到json对象中
                    jsonObject.put("corpVo", corpModel);
                    jsonObject.put("userVo", userModel);
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
        return -1;
    }
}
