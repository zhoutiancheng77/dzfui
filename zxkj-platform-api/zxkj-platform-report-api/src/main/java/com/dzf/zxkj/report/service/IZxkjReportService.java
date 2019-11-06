package com.dzf.zxkj.report.service;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;

import java.util.List;
import java.util.Map;

public interface IZxkjReportService {
    FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Integer direction);
    Map<String, FseJyeVO> getFsJyeVOs(String pk_corp, String period, Integer direction);
    Map<String, Map<String,Double>>  getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(String pk_corp, YntCpaccountVO[] yntCpaccountVOS, String beginPeriod, String endPeriod);
    LrbVO[] getLRBVOsConXm(QueryParamVO paramVO, List<String> xmid);
    ZcFzBVO[] getZCFZBVOsConXmids(String period, String pk_corp, String ishasjz, String[] hasyes, List<String> xmids);
    ZcFzBVO[] getZcfzVOs(String pk_corp, String[] hasyes, Map<String, YntCpaccountVO> mapc, FseJyeVO[] fvos);
    LrbquarterlyVO[] getLRBquarterlyVOs(QueryParamVO paramVO);
    Map<String, List<LrbVO>> getYearLrbMap(String year, String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz);
    Object[] getFsJyeVOs1(QueryParamVO vo);
    LrbVO[] getLRBVOsByPeriod(QueryParamVO paramVO);
}
