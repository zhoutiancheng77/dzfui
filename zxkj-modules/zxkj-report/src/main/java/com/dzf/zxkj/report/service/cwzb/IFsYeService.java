package com.dzf.zxkj.report.service.cwzb;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.query.cwzb.FsYeQueryVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;

import java.util.List;
import java.util.Map;

public interface IFsYeService {
    FseJyeVO[] getFsJyeVOs(FsYeQueryVO vo, Integer direction) throws Exception;
    Map<String, FseJyeVO> getFsJyeVOs(String pk_corp, String period, Integer direction) throws Exception;
    Object[] getFsJyeVOs1(FsYeQueryVO vo) throws Exception;
    Object[] getFsJyeVOs1(FsYeQueryVO vo,Object[] qryobjs) throws Exception;
    FseJyeVO[] getFsJyeVOs(FsYeQueryVO vo, Object[] qryobjs) throws Exception;
    Object[] getYearFsJyeVOs(String year, String pk_corp,Object[] qryobjs,String rptsource) throws Exception;
    Object[] getYearFsJyeVOs(String year, String pk_corp, String xmmcid, Object[] objs, String rptsource, DZFBoolean ishasjz) throws Exception;
    Object[] getEveryPeriodFsJyeVOs(DZFDate startdate, DZFDate enddate, String pk_corp, Object[] objs, String rptsource, DZFBoolean ishasjz) throws Exception;
    Object[] getYearFsJyeVOsLrbquarter(String year, String pk_corp, DZFDate corpdate, DZFBoolean ishasjz,String rptsource)
            throws Exception;
    FseJyeVO[] getBetweenPeriodFs(DZFDate begdate,DZFDate enddate,Map<String, List<FseJyeVO>> periodfsmap) throws Exception;
    Map<String,Map<String,Double>>  getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(String pk_corp, YntCpaccountVO[] yntCpaccountVOS, String beginPeriod, String endPeriod) throws Exception;
}
