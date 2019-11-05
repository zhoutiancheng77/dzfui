package com.dzf.zxkj.operate.log.aop;

import com.dzf.zxkj.common.base.IOperatorLogService;
import com.dzf.zxkj.common.constant.ISysConstant;
import com.dzf.zxkj.common.tool.IpUtil;
import com.dzf.zxkj.operate.log.annotation.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Configuration
public class LogRecordAspect {
    @Autowired
    private IOperatorLogService operatorLogService;

    @Pointcut("@annotation(com.dzf.zxkj.operate.log.annotation.LogRecord)")
    public void LogRecord() {
    }

    @After("LogRecord()")
    public void doAfterReturning(JoinPoint joinPoint) {
        // 记录日志
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogRecord logRecord = method.getAnnotation(LogRecord.class);
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String pk_corp = request.getHeader(ISysConstant.LOGIN_PK_CORP);
            String userid = request.getHeader(ISysConstant.LOGIN_USER_ID);
            String ip = IpUtil.getIpAddr(request);//由于网关转发和前端Nginx分发可能不准确
            operatorLogService.saveLog(pk_corp, null, ip, logRecord.type().getValue(), logRecord.msg(), logRecord.ident(), userid);
        } catch (Exception e) {
            log.error("操作日志记录异常", e);
        }
    }
}
