package com.dzf.zxkj.report.dubbo;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.report.service.IZxkjReportService;
import com.dzf.zxkj.report.service.cwbb.ILrbQuarterlyReport;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service(version = "1.0.0", timeout = Integer.MAX_VALUE, group = "zxkj-report")
public class ZxkjReportServiceImpl implements IZxkjReportService {

    @Autowired
    private IFsYeReport fsYeReport;
    @Autowired
    private ILrbReport lrbReport;
    @Autowired
    private IZcFzBReport zcFzBReport;
    @Autowired
    private ILrbQuarterlyReport lrbQuarterlyReport;

    @Override
    public FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Integer direction) {
        return fsYeReport.getFsJyeVOs(vo, direction);
    }

    @Override
    public Map<String, Map<String, Double>> getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(String pk_corp, YntCpaccountVO[] yntCpaccountVOS, String beginPeriod, String endPeriod) {
        return fsYeReport.getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(pk_corp, yntCpaccountVOS, beginPeriod, endPeriod);
    }

    @Override
    public LrbVO[] getLRBVOsConXm(QueryParamVO paramVO, List<String> xmid) {
        return lrbReport.getLRBVOsConXm(paramVO, xmid);
    }

    @Override
    public ZcFzBVO[] getZCFZBVOsConXmids(String period, String pk_corp, String ishasjz, String[] hasyes, List<String> xmids) {
        return zcFzBReport.getZCFZBVOsConXmids(period, pk_corp, ishasjz, hasyes, xmids);
    }

    @Override
    public ZcFzBVO[] getZcfzVOs(String pk_corp, String[] hasyes, Map<String, YntCpaccountVO> mapc, FseJyeVO[] fvos) {
        return zcFzBReport.getZcfzVOs(pk_corp, hasyes, mapc, fvos);
    }

    @Override
    public LrbquarterlyVO[] getLRBquarterlyVOs(QueryParamVO paramVO) {
        return lrbQuarterlyReport.getLRBquarterlyVOs(paramVO);
    }

    @Override
    public Map<String, List<LrbVO>> getYearLrbMap(String year, String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz) {
        return lrbReport.getYearLrbMap(year, pk_corp, xmmcid, objs, ishasjz);
    }

    @Override
    public Object[] getFsJyeVOs1(QueryParamVO vo) {
        return fsYeReport.getFsJyeVOs1(vo);
    }

    @Override
    public LrbVO[] getLRBVOsByPeriod(QueryParamVO paramVO) {
        return lrbReport.getLRBVOsByPeriod(paramVO);
    }
}
