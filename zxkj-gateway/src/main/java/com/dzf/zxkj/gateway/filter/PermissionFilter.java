package com.dzf.zxkj.gateway.filter;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.common.constant.ISysConstant;
import com.dzf.zxkj.common.enums.HttpStatusEnum;
import com.dzf.zxkj.gateway.config.GatewayConfig;
import com.dzf.zxkj.platform.auth.model.jwt.IJWTInfo;
import com.dzf.zxkj.platform.auth.model.sys.CorpModel;
import com.dzf.zxkj.platform.auth.model.sys.UserModel;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import com.dzf.zxkj.platform.auth.service.ISysService;
import com.dzf.zxkj.platform.auth.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Auther: dandelion
 * @Date: 2019-09-03
 * @Description:
 */
@Configuration
@Slf4j
@SuppressWarnings("all")
public class PermissionFilter implements GlobalFilter, Ordered {

    private final DataBufferFactory dataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();

    @Reference(version = "1.0.0")
    private ISysService sysService;

    @Reference(version = "1.0.0")
    private IAuthService authService;

    @Autowired
    private GatewayConfig gatewayConfig;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //登陆请求不验证权限
        log.info("登录url:" + path);
        if (StringUtils.equalsAnyIgnoreCase(path, "/api/auth/captcha", "/api/auth/login", "/api/auth/loginByToken")) {
            return chain.filter(exchange);
        }

        return getAuthInfoFromRequest(exchange).flatMap(authInfo -> {
            ServerHttpResponse response = exchange.getResponse();
            //token非空判断
            String token = authInfo.get(ISysConstant.TOKEN);
            if (StringUtils.isBlank(token)) {
                return reponse(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_TOKEN_ERROR_CODE, response);
            }
            //token校验
            IJWTInfo ijwtInfo = null;
            try {
                ijwtInfo = JWTUtil.getInfoFromToken(token, gatewayConfig.getUserPubKey());
            } catch (Exception e) {
                log.info("token验证失败！");
                return reponse(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_TOKEN_ERROR_CODE, response);
            }
            //校验token内userid与消息头的userid是否一致
            String useridFormToken = ijwtInfo.getBody();
            String useridFromHeader = authInfo.get(ISysConstant.LOGIN_USER_ID);
            if (StringUtils.isAnyBlank(useridFormToken, useridFromHeader) || !useridFormToken.equals(useridFromHeader)) {
                return reponse(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_TOKEN_ERROR_CODE, response);
            }
            String clientId = authInfo.get(ISysConstant.CLIENT_ID);

            //判断是否唯一登录
            if (authService.validateMultipleLogin(useridFormToken, clientId)) {
                return reponse(HttpStatus.UNAUTHORIZED, HttpStatusEnum.MULTIPLE_LOGIN_ERROR, response);
            }

            //token过期时间校验
            if (authService.validateTokenEx(useridFormToken, clientId)) {
                return reponse(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_TOKEN_EXPIRED_CODE, response);
            }

            if (StringUtils.equalsAnyIgnoreCase(path, "/api/zxkj/sm_user/gsQuery", "/api/zxkj/sm_user/gsSelect")) {
                return chain.filter(exchange);
            }

            String currentCorp = authInfo.get(ISysConstant.LOGIN_PK_CORP);
            //用户与公司关联校验
            List<String> corps = authService.getPkCorpByUserId(useridFormToken);
            if (corps == null || corps.contains(currentCorp)) {
                log.info("用户没有操作公司权限！");
                return reponse(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_USER_FORBIDDEN_CODE, response);
            }

            //查询公司和用户vo
            final CorpModel corpModel = sysService.queryCorpByPk(currentCorp);
            final UserModel userModel = sysService.queryByUserId(useridFormToken);
            if (corpModel == null || userModel == null) {
                return reponse(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_USER_INVALID_CODE, response);
            }

            //权限校验
            Set<String> allPermissions = authService.getAllPermission();
            Set<String> myPermisssions = authService.getPermisssionByUseridAndPkCorp(useridFormToken, currentCorp);

            if (allPermissions.contains(path) && !myPermisssions.contains(path)) {
                return reponse(HttpStatus.FORBIDDEN, HttpStatusEnum.EX_USER_FORBIDDEN_CODE, response);
            }

            return chain.filter(exchange);
        });
    }

    private Mono<Void> reponse(HttpStatus status, HttpStatusEnum httpStatus, ServerHttpResponse response) {
        JSONObject message = new JSONObject();
        message.put("status", httpStatus.value());
        message.put("message", httpStatus.msg());
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(status);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    private class InputStreamHolder {
        InputStream inputStream;
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Map<String, String>> getAuthInfoFromRequest(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        Mono<Map<String, String>> authInfoMono = null;
        Map<String, String> authInfo = getAuthInfo(headers);
        if (authInfo.isEmpty()) {
            HttpMethod requestMethod = request.getMethod();
            if (HttpMethod.GET == requestMethod) {
                authInfo = getAuthInfo(request.getQueryParams());
            } else if (HttpMethod.POST == requestMethod
                    && MediaType.APPLICATION_FORM_URLENCODED.equals(headers.getContentType())) {
                final ResolvableType resolvableType;
                resolvableType = ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);
                authInfoMono = MESSAGE_READERS.stream().filter(reader ->
                        reader.canRead(resolvableType, exchange.getRequest().getHeaders().getContentType()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("no suitable HttpMessageReader."))
                        .readMono(resolvableType, exchange.getRequest(), Collections.emptyMap())
                        .flatMap(resolvedBody -> {
                            Map<String, String> info;
                            if (resolvedBody instanceof MultiValueMap) {
                                info = getAuthInfo((MultiValueMap) resolvedBody);
                            } else {
                                info = new HashMap<>();
                            }
                            return Mono.just(info);
                        });
            }
        }
        if (authInfoMono == null) {
            if (authInfo == null) {
                authInfo = new HashMap<>();
            }
            authInfoMono = Mono.just(authInfo);
        }
        return authInfoMono;
    }

    private Map<String, String> getAuthInfo(HttpHeaders headers) {
        Map<String, String> map = new HashMap<>();
        String token = headers.getFirst(ISysConstant.TOKEN);
        if (!StringUtils.isBlank(token)) {
            map.put(ISysConstant.TOKEN, token);
            map.put(ISysConstant.LOGIN_USER_ID, headers.getFirst(ISysConstant.LOGIN_USER_ID));
            map.put(ISysConstant.LOGIN_PK_CORP, headers.getFirst(ISysConstant.LOGIN_PK_CORP));
            map.put(ISysConstant.CLIENT_ID, headers.getFirst(ISysConstant.CLIENT_ID));
        }
        return map;
    }

    private Map<String, String> getAuthInfo(MultiValueMap<String, String> param) {
        Map<String, String> map = new HashMap<>();
        String token = param.getFirst(ISysConstant.TOKEN);
        if (!StringUtils.isBlank(token)) {
            map.put(ISysConstant.TOKEN, token);
            map.put(ISysConstant.LOGIN_USER_ID, param.getFirst(ISysConstant.LOGIN_USER_ID));
            map.put(ISysConstant.LOGIN_PK_CORP, param.getFirst(ISysConstant.LOGIN_PK_CORP));
            map.put(ISysConstant.CLIENT_ID, param.getFirst(ISysConstant.CLIENT_ID));
        }
        return map;
    }
}
