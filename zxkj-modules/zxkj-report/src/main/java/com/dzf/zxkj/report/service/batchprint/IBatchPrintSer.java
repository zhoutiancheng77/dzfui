package com.dzf.zxkj.report.service.batchprint;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.sys.UserVO;

public interface IBatchPrintSer {

    public void batchexectask(BatchPrintSetVo[] setVos, UserVO userVO) throws DZFWarpException;
}
