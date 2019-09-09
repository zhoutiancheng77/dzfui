package com.dzf.zxkj.log.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */
@Aspect
@Component
@Slf4j
public class LogAop {

    @Pointcut("execution(* com.dzf..*Controller.*(..))&&@annotation(com.dzf.zxkj.log.annotation.LogRecord)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } finally {
            try {
                //记录日志
            } catch (Exception e) {
                log.error("LogRecord 操作失败：" + e.getMessage());
            }
        }
    }

}
