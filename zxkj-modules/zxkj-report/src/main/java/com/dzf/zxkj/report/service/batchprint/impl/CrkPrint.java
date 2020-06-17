package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;

/**
 * 总账核算模式，出入库打印
 */
public class CrkPrint extends AbstractPrint{

    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    public CrkPrint(IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }

    @Override
    public byte[] print(BatchPrintSetVo setVo, CorpVO corpVO, UserVO uservo) {
        return zxkjPlatformService.execCrkPrintTask(setVo, uservo, corpVO);
    }
}
