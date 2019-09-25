package com.dzf.zxkj.platform.services.report;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

import java.util.List;
import java.util.Map;

/**
 * 发生余额表
 *
 * @author zhangj
 */
public interface IFsYeReport {

    /**
     * @param vos
     * @param direction 0是本身，1是资产负债表
     *                  跨年查询时  QueryParamVO 中  xswyewfs 属性一定要设置成false
     * @return
     * @throws BusinessException
     */
    FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Integer direction) throws DZFWarpException;

    Map<String, FseJyeVO> getFsJyeVOs(String pk_corp, String period, Integer direction) throws DZFWarpException;

    /**
     * 跨年查询时  QueryParamVO 中  xswyewfs 属性一定要设置成false
     *
     * @param vo
     * @return
     * @throws DZFWarpException
     */
    Object[] getFsJyeVOs1(QueryParamVO vo) throws DZFWarpException;

    /**
     * 根据科目明细账查询发生余额数据
     * 跨年查询时  QueryParamVO 中  xswyewfs 属性一定要设置成false
     *
     * @param vo
     * @param qryobjs 科目明细数据 来源(com.dzf.service.gl.gl_kmreport.IKMMXZReport.getKMMXZVOs1(QueryParamVO vo, boolean b))
     * @return
     * @throws DZFWarpException
     */
    Object[] getFsJyeVOs1(QueryParamVO vo, Object[] qryobjs) throws DZFWarpException;

    /**
     * 根据科目明细账查询发生余额数据
     * 跨年查询时  QueryParamVO 中  xswyewfs 属性一定要设置成false
     *
     * @param vos
     * @param direction 0是本身，1是资产负债表
     * @return
     * @throws BusinessException
     */
    FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Object[] qryobjs) throws DZFWarpException;

    /**
     * 取一年的发生额及余额表
     *
     * @param year
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    Object[] getYearFsJyeVOs(String year, String pk_corp, Object[] qryobjs, String rptsource) throws DZFWarpException;

    /**
     * 取一年的发生额及余额表(包含某个项目id)
     *
     * @param year
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    Object[] getYearFsJyeVOs(String year, String pk_corp, String xmmcid, Object[] objs, String rptsource, DZFBoolean ishasjz) throws DZFWarpException;


    /**
     * 取一段区间的发生额及余额表(每次一个区间)
     *
     * @param year
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    Object[] getEveryPeriodFsJyeVOs(DZFDate startdate, DZFDate enddate, String pk_corp, Object[] objs, String rptsource, DZFBoolean ishasjz) throws DZFWarpException;


    /**
     * 取一个季度的发生额余额表 (这个接口方法慎用，如果查当年的用getYearFsJyeVOs方法)
     *
     * @param year
     * @param pk_corp
     * @param corpdate
     * @param ishasjz
     * @return
     * @throws DZFWarpException
     */
    Object[] getYearFsJyeVOsLrbquarter(String year, String pk_corp, DZFDate corpdate, DZFBoolean ishasjz, String rptsource)
            throws DZFWarpException;


    /**
     * 获取某个区间段的发生额余额数据
     *
     * @param begdate
     * @param enddate
     * @param fslist
     * @param periodfsmap 通过每个月的发生数据拼接,为了重新计算期初等
     * @return
     * @throws DZFWarpException
     */
    FseJyeVO[] getBetweenPeriodFs(DZFDate begdate, DZFDate enddate, Map<String, List<FseJyeVO>> periodfsmap) throws DZFWarpException;

    Map<String, Map<String, Double>> getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(String pk_corp, YntCpaccountVO[] yntCpaccountVOS, String beginPeriod, String endPeriod) throws DZFWarpException;
}
