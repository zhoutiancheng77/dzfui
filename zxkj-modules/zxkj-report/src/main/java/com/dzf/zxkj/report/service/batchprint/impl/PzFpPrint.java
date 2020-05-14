package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PzFpPrint extends  AbstractPrint {


    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    public PzFpPrint(IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }

    public byte[] print (BatchPrintSetVo setvo,CorpVO corpVO, UserVO userVO) {
        return zxkjPlatformService.execPzCoverTask(setvo, userVO, corpVO);
    }
}
