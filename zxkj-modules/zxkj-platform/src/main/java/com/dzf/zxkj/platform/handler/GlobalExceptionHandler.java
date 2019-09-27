package com.dzf.zxkj.platform.handler;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.entity.ReturnData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ReturnData businessException(BusinessException e){
        return ReturnData.error().message(e.getMessage());
    }

    @ExceptionHandler(DAOException.class)
    @ResponseBody
    public ReturnData businessException(DAOException e){
        return ReturnData.error().message(e.getMessage());
    }

    @ExceptionHandler(DZFWarpException.class)
    @ResponseBody
    public ReturnData businessException(DZFWarpException e){
        return ReturnData.error().message(e.getMessage());
    }

    @ExceptionHandler(WiseRunException.class)
    @ResponseBody
    public ReturnData businessException(WiseRunException e){
        return ReturnData.error().message(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ReturnData businessException(RuntimeException e){
        return ReturnData.error().message(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ReturnData businessException(Exception e){
        return ReturnData.error().message("系统异常,请联系管理员！");
    }

    @ExceptionHandler(Error.class)
    @ResponseBody
    public ReturnData businessException(Error e){
        return ReturnData.error().message("系统异常,请联系管理员！");
    }
}
