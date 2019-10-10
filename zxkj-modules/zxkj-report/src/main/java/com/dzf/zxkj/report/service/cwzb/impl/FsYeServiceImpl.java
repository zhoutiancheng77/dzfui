package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.query.cwzb.FsYeQueryVO;
import com.dzf.zxkj.report.service.cwzb.IFsYeService;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FsYeServiceImpl implements IFsYeService {
    @Override
    public FseJyeVO[] getFsJyeVOs(FsYeQueryVO vo, Integer direction) throws Exception {
        return new FseJyeVO[0];
    }

    @Override
    public Map<String, FseJyeVO> getFsJyeVOs(String pk_corp, String period, Integer direction) throws Exception {
        return null;
    }

    @Override
    public Object[] getFsJyeVOs1(FsYeQueryVO vo) throws Exception {
        return new Object[0];
    }

    @Override
    public Object[] getFsJyeVOs1(FsYeQueryVO vo, Object[] qryobjs) throws Exception {
        return new Object[0];
    }

    @Override
    public FseJyeVO[] getFsJyeVOs(FsYeQueryVO vo, Object[] qryobjs) throws Exception {
        return new FseJyeVO[0];
    }

    @Override
    public Object[] getYearFsJyeVOs(String year, String pk_corp, Object[] qryobjs, String rptsource) throws Exception {
        return new Object[0];
    }

    @Override
    public Object[] getYearFsJyeVOs(String year, String pk_corp, String xmmcid, Object[] objs, String rptsource, DZFBoolean ishasjz) throws Exception {
        return new Object[0];
    }

    @Override
    public Object[] getEveryPeriodFsJyeVOs(DZFDate startdate, DZFDate enddate, String pk_corp, Object[] objs, String rptsource, DZFBoolean ishasjz) throws Exception {
        return new Object[0];
    }

    @Override
    public Object[] getYearFsJyeVOsLrbquarter(String year, String pk_corp, DZFDate corpdate, DZFBoolean ishasjz, String rptsource) throws Exception {
        return new Object[0];
    }

    @Override
    public FseJyeVO[] getBetweenPeriodFs(DZFDate begdate, DZFDate enddate, Map<String, List<FseJyeVO>> periodfsmap) throws Exception {
        return new FseJyeVO[0];
    }

    @Override
    public Map<String, Map<String, Double>> getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(String pk_corp, YntCpaccountVO[] yntCpaccountVOS, String beginPeriod, String endPeriod) throws Exception {
        return null;
    }
}
