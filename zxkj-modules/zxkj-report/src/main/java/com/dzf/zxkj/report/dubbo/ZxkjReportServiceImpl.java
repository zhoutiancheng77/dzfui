package com.dzf.zxkj.report.dubbo;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.QueryCondictionVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.report.service.IZxkjReportService;
import com.dzf.zxkj.report.service.cwbb.*;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.service.cwzb.INummnyReport;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjReportServiceImpl implements IZxkjReportService {

    @Autowired
    private IFsYeReport fsYeReport;
    @Autowired
    private ILrbReport lrbReport;
    @Autowired
    private IZcFzBReport zcFzBReport;
    @Autowired
    private ILrbQuarterlyReport lrbQuarterlyReport;
    @Autowired
    private IXjllbReport xjllReport;
    @Autowired
    private IXjllbQuarterlyReport xjlyquarbReport;
    @Autowired
    INummnyReport gl_rep_nmdtserv;
    @Override
    public FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Integer direction) {
        return fsYeReport.getFsJyeVOs(vo, direction);
    }

    @Override
    public Map<String, FseJyeVO> getFsJyeVOs(String pk_corp, String period, Integer direction) throws DZFWarpException {
        return fsYeReport.getFsJyeVOs(pk_corp,period,direction);
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

    @Override
    public LrbVO[] getLRBVOs(QueryParamVO paramVO) {
        return lrbReport.getLRBVOs(paramVO);
    }
    @Override
    public ZcFzBVO[] getZCFZBVOs(String period , String pk_corp,String ishasjz,String[] hasyes){
        return zcFzBReport.getZCFZBVOs(period, pk_corp, ishasjz, hasyes);
    }
    @Override
    public XjllbVO[] getXJLLVOs(QueryParamVO vo){
        return xjllReport.query(vo);
    }
    @Override
    public List<XjllquarterlyVo> getXjllQuartervos(QueryParamVO paramvo,String jd){
        return xjlyquarbReport.getXjllQuartervos(paramvo, jd);
    }

    @Override
    public List<NumMnyGlVO> getNumMnyGlVO(QueryCondictionVO paramVo) {
        return gl_rep_nmdtserv.getNumMnyGlVO(paramVo);
    }
}
