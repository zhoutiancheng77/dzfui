package com.dzf.zxkj.platform.auth.util;

import com.dzf.zxkj.common.constant.ISysConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Component
public class SystemUtil {

    public static SystemUtil systemUtil;

    @PostConstruct
    public void init(){
        systemUtil = this;
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
    public static String getLoginCorpId(){
        HttpServletRequest request = getRequest();
        String id = request.getHeader(ISysConstant.LOGIN_PK_CORP);
        if (id == null) {
            id = request.getParameter(ISysConstant.LOGIN_PK_CORP);
        }
        return id;
    }

    public static String getLoginUserId(){
        HttpServletRequest request = getRequest();
        String id = request.getHeader(ISysConstant.LOGIN_USER_ID);
        if (id == null) {
            id = request.getParameter(ISysConstant.LOGIN_USER_ID);
        }
        return id;
    }

    public static String getLoginDate(){
        HttpServletRequest request = getRequest();
        String date = request.getHeader(ISysConstant.LOGIN_DATE);
        if (date == null) {
            date = request.getParameter(ISysConstant.LOGIN_DATE);
        }
        return date;
    }

    public static String getClientId(){
        HttpServletRequest request = getRequest();
        return request.getHeader(ISysConstant.CLIENT_ID);
    }

}
