package com.dzf.zxkj.report.service.cwbb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;

import java.util.Map;

public interface ILrbQuarterlyReport {

    /**
     * 利润表取数
     */
    LrbquarterlyVO[] getLRBquarterlyVOs(QueryParamVO paramVO) throws DZFWarpException;


    /**
     * 从科目明细账取数据
     *
     * @return
     * @throws DZFWarpException
     */
    Map<String, LrbquarterlyVO[]> getLRBquarterlyVOs(QueryParamVO vo, Object[] objs) throws DZFWarpException;


    /**
     * 每个月的利润表取数
     */
    Map<String, DZFDouble> getYearLRBquarterlyVOs(String year, String pk_corp) throws DZFWarpException;

}
