package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.base.IOperatorLogService;
import com.dzf.zxkj.common.constant.ISysConstant;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.tool.IpUtil;
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
    private ICorpService corpService;
    @Autowired
    private IUserService userService;

    public static SystemUtil systemUtil;

    @PostConstruct
    public void init() {
        systemUtil = this;
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String getLoginCorpId() {
        return getLoginCorpId(getRequest());
    }

    private static String getLoginCorpId(HttpServletRequest request) {
        String id = request.getHeader(ISysConstant.LOGIN_PK_CORP);
        if (id == null) {
            id = request.getParameter(ISysConstant.LOGIN_PK_CORP);
        }
        return id;
    }

    public static String getLoginUserId() {
        HttpServletRequest request = getRequest();
        return getLoginUserId(request);
    }

    private static String getLoginUserId(HttpServletRequest request) {
        String id = request.getHeader(ISysConstant.LOGIN_USER_ID);
        if (id == null) {
            id = request.getParameter(ISysConstant.LOGIN_USER_ID);
        }
        return id;
    }

    public static CorpVO getLoginCorpVo() {
        return systemUtil.corpService.queryByPk(getLoginCorpId());
    }

    public static UserVO getLoginUserVo() {
        return systemUtil.userService.queryUserById(getLoginUserId());
    }

    public static String getLoginDate() {
        HttpServletRequest request = getRequest();
        String date = request.getHeader(ISysConstant.LOGIN_DATE);
        if (date == null) {
            date = request.getParameter(ISysConstant.LOGIN_DATE);
        }
        return date;
    }

    public static void writeLogRecord(LogRecordEnum recordEnum, String msg, HttpServletRequest request) {
        try {
            if (request == null) {
                request = getRequest();
            }
            String loginCorpId = getLoginCorpId(request);
            String loginUserId = getLoginUserId(request);
            IOperatorLogService operatorLogService = SpringUtils.getBean(IOperatorLogService.class);
            operatorLogService.saveLog(loginCorpId, null, IpUtil.getIpAddr(request),
                    recordEnum.getValue(), msg, ISysConstants.SYS_2, loginUserId);
        } catch (Exception e) {
        }
    }

}
