package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.CwgyInfoVO;

import java.util.Map;

public interface ICwgyInfoReport {

    /**
     * 财务概要信息表取数
     */
    CwgyInfoVO[] getCwgyInfoVOs(QueryParamVO paramVO) throws DZFWarpException;


    /**
     * 取一年的财务概要信息表
     */
    Map<String, CwgyInfoVO[]> getCwgyInfoVOs(String year, String pk_corp, Object[] obj) throws DZFWarpException;

}
