package com.dzf.zxkj.platform.service.batchprint;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;

import java.util.List;

/**
 * 归档任务执行
 */
public interface INewBatchPrintSetTaskSer {


    /**
     * 查询任务
     * @param userid
     * @return
     * @throws DZFWarpException
     */
    public List<BatchPrintSetVo> queryTask(String userid) throws DZFWarpException;

    /**
     * 执行任务
     * @throws DZFWarpException
     */
    public void execTask() throws DZFWarpException;


    /**
     * 删除任务数据
     * @throws DZFWarpException
     */
    public void deleteTask() throws DZFWarpException;

    /**
     * 下载任务文件
     * @throws DZFWarpException
     */
    public void downLoadFile() throws DZFWarpException;
}
