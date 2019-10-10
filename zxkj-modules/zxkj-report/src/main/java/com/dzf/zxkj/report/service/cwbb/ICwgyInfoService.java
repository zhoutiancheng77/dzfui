package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.report.query.cwbb.CwgyInfoQueryVO;
import com.dzf.zxkj.report.vo.cwbb.CwgyInfoVO;

import java.util.Map;

public interface ICwgyInfoService {
    /**
     *  财务概要信息表取数
     * @param queryVO
     * @return
     * @throws Exception
     */
    CwgyInfoVO[] getCwgyInfoVOs(CwgyInfoQueryVO queryVO) throws Exception;

    /**
     *  取一年的财务概要信息表
     * @param year
     * @param pk_corp
     * @param obj
     * @return
     * @throws Exception
     */
    Map<String, CwgyInfoVO[]> getCwgyInfoVOs(String year, String pk_corp, Object[] obj) throws Exception;
}
