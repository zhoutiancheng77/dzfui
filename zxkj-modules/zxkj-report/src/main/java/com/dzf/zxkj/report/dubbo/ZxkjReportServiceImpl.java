package com.dzf.zxkj.report.dubbo;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.report.service.IZxkjReportService;
import org.apache.dubbo.config.annotation.Service;

import java.util.List;
import java.util.Map;

@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjReportServiceImpl implements IZxkjReportService {
    @Override
    public FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Integer direction) {
        return new FseJyeVO[0];
    }

    @Override
    public Map<String, Map<String, Double>> getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(String pk_corp, YntCpaccountVO[] yntCpaccountVOS, String beginPeriod, String endPeriod) {
        return null;
    }

    @Override
    public LrbVO[] getLRBVOsConXm(QueryParamVO paramVO, List<String> xmid) {
        return new LrbVO[0];
    }

    @Override
    public ZcFzBVO[] getZCFZBVOsConXmids(String period, String pk_corp, String ishasjz, String[] hasyes, List<String> xmids) {
        return new ZcFzBVO[0];
    }

    @Override
    public ZcFzBVO[] getZcfzVOs(String pk_corp, String[] hasyes, Map<String, YntCpaccountVO> mapc, FseJyeVO[] fvos) {
        return new ZcFzBVO[0];
    }

    @Override
    public LrbquarterlyVO[] getLRBquarterlyVOs(QueryParamVO paramVO) {
        return new LrbquarterlyVO[0];
    }

    @Override
    public Map<String, List<LrbVO>> getYearLrbMap(String year, String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz) {
        return null;
    }

    @Override
    public Object[] getFsJyeVOs1(QueryParamVO vo) {
        return new Object[0];
    }

    @Override
    public LrbVO[] getLRBVOsByPeriod(QueryParamVO paramVO) {
        return new LrbVO[0];
    }
}
