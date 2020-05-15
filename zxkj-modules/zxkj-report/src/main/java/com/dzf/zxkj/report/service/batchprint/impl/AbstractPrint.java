package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

public  abstract  class AbstractPrint {

    public abstract  byte[] print(BatchPrintSetVo setVo, CorpVO corpVO, UserVO uservo);
}
