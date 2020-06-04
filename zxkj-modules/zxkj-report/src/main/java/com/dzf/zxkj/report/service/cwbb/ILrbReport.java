package com.dzf.zxkj.report.service.cwbb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbVO;

import java.util.List;
import java.util.Map;

public interface ILrbReport {

    /**
     * 利润表取数
     */
    LrbVO[] getLRBVOs(QueryParamVO paramVO) throws DZFWarpException;


    /**
     * 利润表取数(包含具体某几个项目)
     */
    LrbVO[] getLRBVOsConXm(QueryParamVO paramVO, List<String> xmid) throws DZFWarpException;

    /**
     * 从发生表获取数据
     */
    LrbVO[] getLrbVosFromFs(QueryParamVO paramVO, Map<String, YntCpaccountVO> mp, String pk_corp, FseJyeVO[] fvos) throws DZFWarpException;

    /**
     * 利润表取数(按照期间取数 如 2017-12-01 到2018-12-31)
     */
    LrbVO[] getLRBVOsByPeriod(QueryParamVO paramVO) throws DZFWarpException;


    /**
     * 利润表取数(多个月份)
     */
    Map<String, DZFDouble> getYearLRBVOs(String year, String pk_corp, Object[] objs) throws DZFWarpException;


    /**
     * 获取每个月的数据
     */
    Map<String, List<LrbVO>> getYearLrbMap(String year, String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz) throws DZFWarpException;

    /**
     * 获取区间段每个区间的数据
     */
    List<LrbVO[]> getBetweenLrbMap(DZFDate begdate, DZFDate enddate,
                                   String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz) throws DZFWarpException;


    /**
     * 财务报税文件VO
     *
     * @param qj
     * @param corpIds
     * @param qjlx    0 月 1 季度
     * @return
     * @throws DZFWarpException
     */
    LrbVO[] getLrbDataForCwBs(String qj, String corpIds, String qjlx,String corpType, String areaType) throws DZFWarpException;

    /**
     * 通过发生额余额表生成利润表数据
     */
    LrbVO[] getLrbVos(QueryParamVO vo, String pk_corp, Map<String, YntCpaccountVO> mp,
                      Map<String, FseJyeVO> map, String xmmcid) throws DZFWarpException;

}
