package com.dzf.zxkj.platform.service.batchprint;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintFileSetVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetQryVo;

import java.util.List;

/**
 * 归档设置
 */
public interface IBatchPrintFileSet {

    /**
     * 保存用户关联的所有公司设置
     * @param setvo
     * @throws DZFWarpException
     */
    public void saveFileSet(BatchPrintFileSetVo[] setvo,String cuserid) throws DZFWarpException;


    /**
     * 查询当前用户关联的设置
     * @param cuserid
     * @return
     * @throws DZFWarpException
     */
    public BatchPrintFileSetVo[] queryFileSet (String cuserid) throws DZFWarpException;


}
