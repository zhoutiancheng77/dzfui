package com.dzf.zxkj.platform.util.zncs;

import com.dzf.zxkj.common.constant.ISysConstant;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Component
public class SystemUtil {

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    public static SystemUtil systemUtil;

    @PostConstruct
    public void init(){
        systemUtil = this;
    }

    public static CorpVO queryCorp(String pk_corp){
        return systemUtil.zxkjPlatformService.queryCorpByPk(pk_corp);
    }

    public static UserVO queryUser(String userid){
        return systemUtil.zxkjPlatformService.queryUserById(userid);
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String getLoginCorpId(){
        return getRequest().getHeader(ISysConstant.LOGIN_PK_CORP);
    }

    public static String getLoginUserId(){
        return getRequest().getHeader(ISysConstant.LOGIN_USER_ID);
    }

    public static CorpVO getLoginCorpVo(){
        return systemUtil.zxkjPlatformService.queryCorpByPk(getLoginCorpId());
    }

    public static UserVO getLoginUserVo(){
        return systemUtil.zxkjPlatformService.queryUserById(getLoginUserId());
    }

    public static String getLoginDate(){
        return getRequest().getHeader(ISysConstant.LOGIN_DATE);
    }

}
