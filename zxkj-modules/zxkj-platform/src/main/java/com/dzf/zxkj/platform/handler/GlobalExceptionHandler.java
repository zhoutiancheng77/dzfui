package com.dzf.zxkj.platform.handler;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.HttpStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ReturnData businessException(BusinessException e) {
        Json json = new Json();
        json.setMsg(e.getMessage());
        return ReturnData.ok().data(json);
    }

    @ExceptionHandler(DAOException.class)
    @ResponseBody
    public ReturnData businessException(DAOException e) {
        log.error("DAO异常", e);
        return ReturnData.error().message("系统异常");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ReturnData businessException(MissingServletRequestParameterException e) {
        log.error("请求缺失参数", e);
        return ReturnData.error(HttpStatusEnum.MISS_REQUEST_PARAMETER_CODE.value()).message("请求参数为空");
    }
    /*@ExceptionHandler(DZFWarpException.class)
    @ResponseBody
    public ReturnData businessException(DZFWarpException e){
        log.error("DZFWarpException", e);
        return ReturnData.error().message("系统异常");
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
*/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ReturnData businessException(Exception e) {
        log.error("请求异常", e);
        return ReturnData.error().message("系统异常,请联系管理员！");
    }

    @ExceptionHandler(Error.class)
    @ResponseBody
    public ReturnData businessException(Error e) {
        log.error("系统错误", e);
        return ReturnData.error().message("系统异常,请联系管理员！");
    }
}
