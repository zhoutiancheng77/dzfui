package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.report.query.cwbb.LrbQuarterlyQueryVO;
import com.dzf.zxkj.report.vo.cwbb.LrbquarterlyVO;

import java.util.Map;

public interface ILrbQuarterlyService {
    /**
     *  利润表取数
     * @param queryVO
     * @return
     * @throws Exception
     */
    LrbquarterlyVO[] getLRBquarterlyVOs(LrbQuarterlyQueryVO queryVO) throws Exception;

    /**
     * 从科目明细账取数据
     * @param vo
     * @param objs
     * @return
     * @throws Exception
     */
    Map<String, LrbquarterlyVO[]> getLRBquarterlyVOs(LrbQuarterlyQueryVO vo, Object[] objs) throws Exception;

    /**
     * 每个月的利润表取数
     * @param year
     * @param pk_corp
     * @return
     * @throws Exception
     */
    Map<String, DZFDouble> getYearLRBquarterlyVOs(String year, String pk_corp) throws Exception;
}
