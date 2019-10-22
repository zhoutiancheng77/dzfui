package com.dzf.zxkj.report.controller;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import com.dzf.zxkj.common.entity.Json;

@Slf4j
public class ReportBaseController {

    protected void printErrorLog(Grid grid,Throwable e, String errorinfo){
        if(StringUtil.isEmpty(errorinfo))
            errorinfo = "操作失败";
        if(e instanceof BusinessException){
            grid.setMsg(e.getMessage());
        }else{
            grid.setMsg(errorinfo);
            log.error(errorinfo,e);
        }
        grid.setSuccess(false);
    }


    protected void printErrorLog(Json json,Throwable e,String errorinfo){
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
