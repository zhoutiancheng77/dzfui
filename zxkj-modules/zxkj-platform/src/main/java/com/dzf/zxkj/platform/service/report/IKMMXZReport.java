package com.dzf.zxkj.platform.service.report;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IKMMXZReport {

    /**
     * 科目明细账(不包含辅助项目)
     *
     * @param vo     查询条件vo
     * @param qryobj 根据查询条件查询后的结果集
     * @return
     * @throws BusinessException
     */
    KmMxZVO[] getKMMXZVOs(QueryParamVO vo, Object[] qryobj) throws DZFWarpException;


    /**
     * 科目明细账（包含辅助项目）
     *
     * @param vo
     * @return
     * @throws DZFWarpException
     */
    KmMxZVO[] getKMMXZConFzVOs(QueryParamVO vo, Object[] qryobjs) throws DZFWarpException;


    /**
     * 科目明细账
     *
     * @param period
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    Object[] getKMMXZVOs1(QueryParamVO vo, boolean b) throws DZFWarpException;


    /**
     * km的查询条件
     *
     * @param vo
     * @return
     */
    String getKmTempTable(QueryParamVO vo);


    /**
     * 获取某个期间的发生(不包含辅助项目)
     *
     * @param pk_corp
     * @param ishasjz
     * @param ishassh
     * @param start
     * @param end
     * @param kmwhere
     * @return
     * @throws DZFWarpException
     */
    List<KmMxZVO> getKmFSByPeriod(String pk_corp, DZFBoolean ishasjz, DZFBoolean ishassh, DZFDate start, DZFDate end, String kmwhere) throws DZFWarpException;

    /**
     * 获取科目的值
     *
     * @param pk_corp
     * @param kmwhere
     * @return
     * @throws BusinessException
     */
    Map<String, YntCpaccountVO> getKM(String pk_corp, String kmwhere) throws DZFWarpException;

    /**
     * 辅助明细添加(发生额，总账，明细账 使用)
     *
     * @param qcmapvos
     * @param fsmapvos
     * @param periods
     * @param kmmap
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    List<KmMxZVO> getResultVos(Map<String, KmMxZVO> qcmapvos, Map<String, List<KmMxZVO>> fsmapvos,
                               HashMap<String, DZFDouble[]> corpbegqcmap, List<String> periods, Map<String,
            YntCpaccountVO> kmmap, String pk_corp, DZFBoolean ishowfs, String kmslast, DZFBoolean btotalyear) throws DZFWarpException;


    /**
     * 暂，不适用
     *
     * @param kms_first
     * @param pk_corp
     * @return
     */
    YntCpaccountVO[] getkm_first(String kms_first, String pk_corp);

}
