package com.dzf.zxkj.platform.service.batchprint;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintFileSetVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetQryVo;

import java.util.List;

/**
 * 归档设置
 */
public interface IBatchPrintFileSet {

    /**
     * 根据公司+期间查询成功的公司设置
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    public List<BatchPrintSetQryVo> queryPrintVOs(String pk_corp ,String cuserid, String period)  throws DZFWarpException;


    /**
     * 当前用户关联的所有公司设置
     * @param setvo
     * @throws DZFWarpException
     */
    public void saveFileSet(BatchPrintFileSetVo setvo,String pk_corp,String cuserid,String period) throws DZFWarpException;


}
