package com.dzf.zxkj.gateway.filter;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.common.constant.ISysConstant;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.HttpStatusEnum;
import com.dzf.zxkj.gateway.config.GatewayConfig;
import com.dzf.zxkj.platform.auth.model.jwt.IJWTInfo;
import com.dzf.zxkj.platform.auth.model.sys.CorpModel;
import com.dzf.zxkj.platform.auth.model.sys.UserModel;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import com.dzf.zxkj.platform.auth.service.ISysService;
import com.dzf.zxkj.platform.auth.utils.JWTUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
@Slf4j
public class PermissionFilter extends ZuulFilter {

    @Reference(version = "1.0.0")
    private ISysService sysService;

    @Reference(version = "1.0.0")
    private IAuthService authService;

    @Autowired
    private GatewayConfig gatewayConfig;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    public boolean isStaticFilter() {
        return false;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        if (StringUtils.equalsAnyIgnoreCase(request.getRequestURI(), "/api/auth/captcha", "/api/auth/login", "/api/auth/loginByToken")) {
            return false;
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String token = getLoginInfo(requestContext, ISysConstant.TOKEN);
        String useridFromHeader = getLoginInfo(requestContext, ISysConstant.LOGIN_USER_ID);
        String currentCorp = getLoginInfo(requestContext, ISysConstant.LOGIN_PK_CORP);

        if (StringUtils.isBlank(token)) {
            sendError(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_TOKEN_ERROR_CODE, requestContext);
            return null;
        }
        //token校验
        IJWTInfo ijwtInfo = null;
        try {
            ijwtInfo = JWTUtil.getInfoFromToken(token, gatewayConfig.getUserPubKey());
        } catch (Exception e) {
            log.info("token验证失败！");
            sendError(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_TOKEN_ERROR_CODE, requestContext);
            return null;
        }

        if(request.getContentType() != null && request.getContentType().equalsIgnoreCase("application/x-www-form-urlencoded;charset=UTF-8")){
            return null;
        }

        if(request.getRequestURL().indexOf("/api/auth/to") != -1){
            return null;
        }

        //校验token内userid与消息头的userid是否一致
        String useridFormToken = ijwtInfo.getBody();

        if (StringUtils.isAnyBlank(useridFormToken, useridFromHeader) || !useridFormToken.equals(useridFromHeader)) {
            sendError(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_TOKEN_ERROR_CODE, requestContext);
            return null;
        }

        String clientId = getLoginInfo(requestContext, ISysConstant.CLIENT_ID);

        //判断是否唯一登录
        if (!StringUtils.isBlank(clientId) && authService.validateMultipleLogin(useridFormToken, clientId)) {
            sendError(HttpStatus.UNAUTHORIZED, HttpStatusEnum.MULTIPLE_LOGIN_ERROR, requestContext);
            return null;
        }

        //token过期时间校验
        if (authService.validateTokenEx(useridFormToken, clientId)) {
            sendError(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_TOKEN_EXPIRED_CODE, requestContext);
            return null;
        }

        if (StringUtils.equalsAnyIgnoreCase(request.getRequestURI(), "/api/zxkj/sm_user/gsQuery", "/api/zxkj/sm_user/gsSelect")) {
            return null;
        }

        //用户与公司关联校验
        List<String> corps = authService.getPkCorpByUserId(useridFormToken);
        if (corps == null || corps.contains(currentCorp)) {
            log.info("用户没有操作公司权限！");
            sendError(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_USER_FORBIDDEN_CODE, requestContext);
            return null;
        }

        //查询公司和用户vo
        final CorpModel corpModel = sysService.queryCorpByPk(currentCorp);
        final UserModel userModel = sysService.queryByUserId(useridFormToken);
        if (corpModel == null || userModel == null) {
            sendError(HttpStatus.UNAUTHORIZED, HttpStatusEnum.EX_USER_INVALID_CODE, requestContext);
            return null;
        }
        return null;
    }

    private String getLoginInfo(RequestContext requestContext, String name) {
        HttpServletRequest request = requestContext.getRequest();
        String info = request.getHeader(name);
        if (StringUtils.isBlank(info)) {
            info = request.getParameter(name);
            if(!StringUtils.isBlank(info)){
                requestContext.addZuulRequestHeader(name, info);
            }
        }
        return info;
    }

    private void sendError(HttpStatus status, HttpStatusEnum httpStatus, RequestContext requestContext) {
        //过滤该请求，不往下级服务转发，到此结束不进行路由
        requestContext.setSendZuulResponse(false);
        HttpServletResponse response = requestContext.getResponse();
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            response.sendError(status.value());
        } catch (IOException e) {
            log.error("权限校验回写异常", e);
        }
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(JSONObject.toJSONString(new ReturnData<>(httpStatus.value(), httpStatus.msg())));
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            pw.close();
        }
    }
}
