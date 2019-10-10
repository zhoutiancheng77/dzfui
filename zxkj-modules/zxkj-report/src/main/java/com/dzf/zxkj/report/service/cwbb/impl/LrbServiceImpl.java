package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.report.query.cwbb.LrbQueryVO;
import com.dzf.zxkj.report.service.cwbb.ILrbService;
import com.dzf.zxkj.report.vo.cwbb.LrbVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LrbServiceImpl implements ILrbService {
    @Override
    public LrbVO[] getLRBVOs(LrbQueryVO queryVO) throws Exception {
        return new LrbVO[0];
    }

    @Override
    public LrbVO[] getLRBVOsConXm(LrbQueryVO queryVO, List<String> xmid) throws Exception {
        return new LrbVO[0];
    }

    @Override
    public LrbVO[] getLrbVosFromFs(LrbQueryVO queryVO, Map<String, YntCpaccountVO> mp, String pk_corp, FseJyeVO[] fvos) throws Exception {
        return new LrbVO[0];
    }

    @Override
    public LrbVO[] getLRBVOsByPeriod(LrbQueryVO paramVO) throws Exception {
        return new LrbVO[0];
    }

    @Override
    public Map<String, DZFDouble> getYearLRBVOs(String year, String pk_corp, Object[] objs) throws Exception {
        return null;
    }

    @Override
    public Map<String, List<LrbVO>> getYearLrbMap(String year, String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz) throws Exception {
        return null;
    }

    @Override
    public List<LrbVO[]> getBetweenLrbMap(DZFDate begdate, DZFDate enddate, String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz) throws Exception {
        return null;
    }

    @Override
    public LrbVO[] getLrbDataForCwBs(String qj, String corpIds, String qjlx) throws Exception {
        return new LrbVO[0];
    }

    @Override
    public LrbVO[] getLrbVos(LrbQueryVO queryVO, String pk_corp, Map<String, YntCpaccountVO> mp, Map<String, FseJyeVO> map, String xmmcid) throws Exception {
        return new LrbVO[0];
    }
}
