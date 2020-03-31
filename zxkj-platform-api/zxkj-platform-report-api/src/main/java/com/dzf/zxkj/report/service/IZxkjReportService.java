package com.dzf.zxkj.report.service;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.AgeReportQueryVO;
import com.dzf.zxkj.common.query.QueryCondictionVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.report.*;

import java.util.List;
import java.util.Map;

public interface IZxkjReportService {
    FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Integer direction);
    FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Object[] qryobjs);
    Object[] getEveryPeriodFsJyeVOs(DZFDate startdate, DZFDate enddate, String pk_corp, Object[] objs, String rptsource, DZFBoolean ishasjz);
    Object[] getKMMXZVOs1(QueryParamVO vo,boolean  b);
    KmZzVO[] getKMZZVOs(QueryParamVO vo, Object[] kmmx_objs);
    KmMxZVO[] getKMMXZConFzVOs(QueryParamVO vo, Object[] qryobjs);
    Map<String, FseJyeVO> getFsJyeVOs(String pk_corp, String period, Integer direction);
    Map<String, Map<String,Double>>  getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(String pk_corp, YntCpaccountVO[] yntCpaccountVOS, String beginPeriod, String endPeriod);
    LrbVO[] getLRBVOsConXm(QueryParamVO paramVO, List<String> xmid);
    List<String> queryLrbKmsFromDaima(String pk_trade_accountschema,List<String> xmid);
    // 资产负债表
    ZcFzBVO[] getZCFZBVOsConXmids(String period, String pk_corp, String ishasjz, String[] hasyes, List<String> xmids);
    ZcFzBVO[] getZCFZBVOs(String period , String pk_corp,String ishasjz,String[] hasyes);
    ZcFzBVO[] getZcfzVOs(String pk_corp, String[] hasyes, Map<String, YntCpaccountVO> mapc, FseJyeVO[] fvos);
    List<ZcFzBVO[]> getZcfzVOs(DZFDate begdate, DZFDate enddate, String pk_corp, String ishasjz, String[] hasyes, Object[] qryobjs);

    LrbquarterlyVO[] getLRBquarterlyVOs(QueryParamVO paramVO);
    Map<String, LrbquarterlyVO[]> getLRBquarterlyVOs(QueryParamVO vo,Object[] objs);
    Map<String, List<LrbVO>> getYearLrbMap(String year, String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz);
    Object[] getFsJyeVOs1(QueryParamVO vo);
    LrbVO[] getLRBVOsByPeriod(QueryParamVO paramVO);
    LrbVO[] getLRBVOs(QueryParamVO paramVO);
    List<LrbVO[]> getBetweenLrbMap(DZFDate begdate, DZFDate enddate,
                                   String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz);
    XjllbVO[] getXJLLVOs(QueryParamVO vo);
    List<XjllquarterlyVo> getXjllQuartervos(QueryParamVO paramvo,String jd);
    /**
     * 数量金额总账
     */
   List<NumMnyGlVO> getNumMnyGlVO(QueryCondictionVO paramVo) ;

    AgeReportResultVO query (AgeReportQueryVO param);

    /**
     * 现金银行日记账
     * @param pk_corp
     * @param kmsbegin
     * @param kmsend
     * @param begindate
     * @param enddate
     * @param xswyewfs
     * @param xsyljfs
     * @param ishasjz
     * @param ishassh
     * @param pk_currency
     * @param kmcodelist
     * @param qryobjs
     * @return
     */
    KmMxZVO[] getXJRJZVOsConMo(String pk_corp, String kmsbegin, String kmsend,
                               DZFDate begindate, DZFDate enddate, DZFBoolean xswyewfs, DZFBoolean xsyljfs,
                               DZFBoolean ishasjz, DZFBoolean ishassh, String pk_currency, List<String> kmcodelist, Object[] qryobjs);
}
