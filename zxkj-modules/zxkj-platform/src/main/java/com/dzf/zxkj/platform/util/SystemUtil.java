package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.common.constant.ISysConstant;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Component
public class SystemUtil {

    @Autowired
    private ICorpService  corpService;
    @Autowired
    private IUserService userService;

    public static SystemUtil systemUtil;

    @PostConstruct
    public void init(){
        systemUtil = this;
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
        return systemUtil.corpService.queryByPk(getLoginCorpId());
    }

    public static UserVO getLoginUserVo(){
        return systemUtil.userService.queryUserById(getLoginUserId());
    }

}
