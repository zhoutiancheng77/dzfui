package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.common.entity.Json;
import org.slf4j.Logger;

public class JsonErrorUtil {

    public  static void jsonErrorLog(Json json, Logger log, Throwable e, String errorinfo){
        if(StringUtil.isEmpty(errorinfo))
            errorinfo = "操作失败";
        if(e instanceof BusinessException){
            json.setMsg(e.getMessage());
        }else{
            json.setMsg(errorinfo);
            log.error(errorinfo,e);
        }
        json.setSuccess(false);
    }
}
