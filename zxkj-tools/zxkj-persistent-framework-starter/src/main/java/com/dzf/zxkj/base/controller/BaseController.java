package com.dzf.zxkj.base.controller;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.base.IOperatorLogService;
import com.dzf.zxkj.common.constant.ISysConstant;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tool.IpUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
public class BaseController {

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired(required = false)
    private IOperatorLogService operatorLogService;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    public void printErrorLog(Grid grid, Throwable e, String errorinfo) {
        if (StringUtil.isEmpty(errorinfo))
            errorinfo = "操作失败";
        if (e instanceof BusinessException) {
            grid.setMsg(e.getMessage());
        } else {
            grid.setMsg(errorinfo);
            log.error(errorinfo, e);
        }
        grid.setSuccess(false);
    }


    public void printErrorLog(Json json, Throwable e, String errorinfo) {
        if (StringUtil.isEmpty(errorinfo))
            errorinfo = "操作失败";
        if (e instanceof BusinessException) {
            json.setMsg(e.getMessage());
        } else {
            json.setMsg(errorinfo);
            log.error(errorinfo, e);
        }
        json.setSuccess(false);
    }

    public void writeLogRecord(LogRecordEnum recordEnum, String msg) {
        writeLogRecord(recordEnum, msg, ISysConstants.SYS_2);
    }

    public void writeLogRecord(LogRecordEnum recordEnum, String msg, Integer ident) {
        String notRecord = request.getParameter("unjl");
        if ("Y".equals(notRecord)) {
            // 不记录日志
            return;
        }
        try {
            String login_corp = request.getHeader(ISysConstant.LOGIN_PK_CORP);
            String login_userid = request.getHeader(ISysConstant.LOGIN_USER_ID);
            operatorLogService.saveLog(login_corp, null, IpUtil.getIpAddr(request), recordEnum.getValue(), msg, ident, login_userid);
        } catch (Exception e) {
            log.error("错误", e);
        }
    }

    public void writeLogRecord(String login_corp, LogRecordEnum recordEnum, String msg, Integer ident) {
        String notRecord = request.getParameter("unjl");
        if ("Y".equals(notRecord)) {
            // 不记录日志
            return;
        }
        try {
            String login_userid = request.getHeader(ISysConstant.LOGIN_USER_ID);
            operatorLogService.saveLog(login_corp, null, IpUtil.getIpAddr(request), recordEnum.getValue(), msg, ident, login_userid);
        } catch (Exception e) {
            log.error("错误", e);
        }
    }

    /**
     * @param vos 数组数据（需包含pk_corp字段）
     */
    public void checkSecurityData(SuperVO[] vos) {
        checkSecurityData(vos, null, null, false);
    }

    /**
     * @param vos     数组数据（数据中包含pk_corp）二者传其一就可
     * @param corps   公司数据      二者传其一就可
     * @param cuserid 用户 （不传默认登录用户）
     */
    public void checkSecurityData(SuperVO[] vos, String[] corps, String cuserid) {
        checkSecurityData(vos, corps, cuserid, false);
    }

    /**
     * @param vos         数组数据（数据中包含pk_corp）二者传其一就可
     * @param corps       公司数据      二者传其一就可
     * @param cuserid     用户 （不传默认登录用户）
     * @param isCheckData 是否校验数据有效性(根据主键校验是否存在) 只有传vo数组时可用
     */
    public void checkSecurityData(SuperVO[] vos, String[] corps, String cuserid, boolean isCheckData) {
        if (StringUtils.isEmpty(cuserid)) {
            cuserid = getLoginUserId();
        }
        zxkjPlatformService.checkSecurityData(vos, corps, cuserid, isCheckData);
    }

    /**
     * 校验登录用户是否有公司权限
     *
     * @param corpId
     */
    public void checkOwnCorp(String... corpId) {
        checkSecurityData(null, corpId, null, false);
    }

    public String getLoginUserId() {
        return request.getHeader(ISysConstant.LOGIN_USER_ID);
    }

    // 将查询后的结果分页
    public SuperVO[] getPageVOs(SuperVO[] pageVos, int page, int rows) {

        int beginIndex = rows * (page - 1);
        if (beginIndex >= pageVos.length) {// 防止beginIndex数组越界
            beginIndex = rows *(pageVos.length/ rows);
        }

        int endIndex = rows * page;
        if (endIndex >= pageVos.length) {// 防止endIndex数组越界
            endIndex = pageVos.length;
        }
        pageVos = Arrays.copyOfRange(pageVos, beginIndex, endIndex);
        return pageVos;
    }

}
