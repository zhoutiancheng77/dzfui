package com.dzf.zxkj.common.base;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class BaseController {

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired(required = false)
    private IOperatorLogService operatorLogService;

    public void writeLogRecord(LogRecordEnum recordEnum, String msg) {
        writeLogRecord(recordEnum, msg, ISysConstants.SYS_2);
    }

    public void writeLogRecord(LogRecordEnum recordEnum, String msg, Integer ident) {
        try {
//            String login_corp = request.getHeader("pk_corp");
//            String login_userid = request.getHeader("userId");
//            operatorLogService.saveLog(login_corp, null, IpUtil.getIpAddr(request), recordEnum.getValue(), msg, ident, login_userid);
        } catch (Exception e) {
            log.error("错误", e);
        }
    }


}
