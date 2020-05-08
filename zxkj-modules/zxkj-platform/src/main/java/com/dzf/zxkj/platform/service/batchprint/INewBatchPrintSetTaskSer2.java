package com.dzf.zxkj.platform.service.batchprint;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;

import java.util.List;

/**
 * 归档任务
 */
public interface INewBatchPrintSetTaskSer2 {

    public List<BatchPrintSetVo> queryTask(String userid) throws DZFWarpException;

}
