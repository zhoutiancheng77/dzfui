package com.dzf.zxkj.platform.service.batchprint;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetQryVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;

import java.util.List;

/**
 * 归档任务执行
 */
public interface INewBatchPrintSetTaskSer {

    /**
     * 根据公司+期间查询成功的公司设置
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    public List<BatchPrintSetQryVo> queryPrintVOs(String pk_corp , String cuserid, String period)  throws DZFWarpException;

    /**
     * 查询任务(根据用户id)
     * @param userid
     * @return
     * @throws DZFWarpException
     */
    public List<BatchPrintSetVo> queryTask(String userid,String period) throws DZFWarpException;

    /**
     * 执行任务
     * @throws DZFWarpException
     */
    public void execTask(String userid) throws DZFWarpException;


    /**
     * 删除任务数据
     * @throws DZFWarpException
     */
    public void deleteTask(String priid) throws DZFWarpException;

    /**
     * 下载任务文件
     * @throws DZFWarpException
     */
    public Object[] downLoadFile(String pk_corp,String id) throws DZFWarpException;

    /**
     * 批量下载
     * @param pk_corp
     * @param ids
     * @return
     * @throws DZFWarpException
     */
    public Object[] downBatchLoadFiles(String pk_corp,String[] ids) throws DZFWarpException;

    /**
     * 根据用户id + 公司信息生成任务信息
     * @param corpids
     * @param userid
     * @throws DZFWarpException
     */
    public void saveTask(String corpidstr,String userid,String type,String period, String vprintdate,String bsysdate) throws DZFWarpException;
}
