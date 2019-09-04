package com.dzf.zxkj.jbsz.handler.exception;

import com.dzf.zxkj.common.constant.HttpStatus;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: dandelion
 * @Date: 2019-09-03
 * @Description:
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ReturnData exceptionHandler(BusinessException e) {
        return ReturnData.error().message(e.getMessage());
    }

    @ExceptionHandler(DZFWarpException.class)
    @ResponseBody
    public ReturnData exceptionHandler(DZFWarpException e) {
        return ReturnData.error().message(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ReturnData exceptionHandler(RuntimeException e) {
        return ReturnData.error().message(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ReturnData exceptionHandler(Exception e) {
        return ReturnData.error(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(e.getMessage());
    }
}
