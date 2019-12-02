package com.dzf.zxkj.report.dubbo;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.AgeReportQueryVO;
import com.dzf.zxkj.common.query.QueryCondictionVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.report.service.IZxkjReportService;
import com.dzf.zxkj.report.service.cwbb.*;
import com.dzf.zxkj.report.service.cwzb.IAgeBalanceReportService;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.service.cwzb.INummnyReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Slf4j
@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjReportServiceImpl implements IZxkjReportService {

    @Autowired
    private IKMMXZReport gl_rep_kmmxjserv;
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
    private INummnyReport gl_rep_nmdtserv;
    @Autowired
    private IRptSetService rptsetser;
    @Autowired
    private IAgeBalanceReportService gl_rep_zlyeb;

    @Override
    public FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Integer direction) {
        try {
            return fsYeReport.getFsJyeVOs(vo, direction);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getFsJyeVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Map<String, LrbquarterlyVO[]> getLRBquarterlyVOs(QueryParamVO vo, Object[] objs) throws DZFWarpException {
        try {
            return lrbQuarterlyReport.getLRBquarterlyVOs(vo, objs);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getLRBquarterlyVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Object[] qryobjs) throws DZFWarpException {
        try {
            return fsYeReport.getFsJyeVOs(vo, qryobjs);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getFsJyeVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    public AgeReportResultVO query(AgeReportQueryVO param) throws DZFWarpException {
        try {
            return gl_rep_zlyeb.query(param);
        } catch (DZFWarpException e) {
            log.error(String.format("调用query异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Object[] getEveryPeriodFsJyeVOs(DZFDate startdate, DZFDate enddate, String pk_corp, Object[] objs, String rptsource, DZFBoolean ishasjz) throws DZFWarpException {
        try {
            return fsYeReport.getEveryPeriodFsJyeVOs(startdate, enddate, pk_corp, objs, rptsource, ishasjz);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getEveryPeriodFsJyeVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    public Object[] getKMMXZVOs1(QueryParamVO vo, boolean b) throws DZFWarpException {
        try {
            return gl_rep_kmmxjserv.getKMMXZVOs1(vo, b);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getKMMXZVOs1异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public List<String> queryLrbKmsFromDaima(String pk_corp, List<String> xmid) throws DZFWarpException {
        try {
            return rptsetser.queryLrbKmsFromDaima(pk_corp, xmid);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryLrbKmsFromDaima异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Map<String, FseJyeVO> getFsJyeVOs(String pk_corp, String period, Integer direction) throws DZFWarpException {
        try {
            return fsYeReport.getFsJyeVOs(pk_corp, period, direction);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getFsJyeVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Map<String, Map<String, Double>> getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(String pk_corp, YntCpaccountVO[] yntCpaccountVOS, String beginPeriod, String endPeriod) {
        try {
            return fsYeReport.getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod(pk_corp, yntCpaccountVOS, beginPeriod, endPeriod);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getVoucherFseQryVOListByPkCorpAndKmBetweenPeriod异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public LrbVO[] getLRBVOsConXm(QueryParamVO paramVO, List<String> xmid) {
        try {
            return lrbReport.getLRBVOsConXm(paramVO, xmid);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getLRBVOsConXm异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public ZcFzBVO[] getZCFZBVOsConXmids(String period, String pk_corp, String ishasjz, String[] hasyes, List<String> xmids) {
        try {
            return zcFzBReport.getZCFZBVOsConXmids(period, pk_corp, ishasjz, hasyes, xmids);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getZCFZBVOsConXmids异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public ZcFzBVO[] getZcfzVOs(String pk_corp, String[] hasyes, Map<String, YntCpaccountVO> mapc, FseJyeVO[] fvos) {
        try {
            return zcFzBReport.getZcfzVOs(pk_corp, hasyes, mapc, fvos);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getZcfzVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public LrbquarterlyVO[] getLRBquarterlyVOs(QueryParamVO paramVO) {
        try {
            return lrbQuarterlyReport.getLRBquarterlyVOs(paramVO);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getLRBquarterlyVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Map<String, List<LrbVO>> getYearLrbMap(String year, String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz) {
        try {
            return lrbReport.getYearLrbMap(year, pk_corp, xmmcid, objs, ishasjz);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getYearLrbMap异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public Object[] getFsJyeVOs1(QueryParamVO vo) {
        try {
            return fsYeReport.getFsJyeVOs1(vo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getFsJyeVOs1异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public LrbVO[] getLRBVOsByPeriod(QueryParamVO paramVO) {
        try {
            return lrbReport.getLRBVOsByPeriod(paramVO);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getLRBVOsByPeriod异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public LrbVO[] getLRBVOs(QueryParamVO paramVO) {
        try {
            return lrbReport.getLRBVOs(paramVO);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getLRBVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public ZcFzBVO[] getZCFZBVOs(String period, String pk_corp, String ishasjz, String[] hasyes) {
        try {
            return zcFzBReport.getZCFZBVOs(period, pk_corp, ishasjz, hasyes);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getZCFZBVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public XjllbVO[] getXJLLVOs(QueryParamVO vo) {
        try {
            return xjllReport.query(vo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getXJLLVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public List<XjllquarterlyVo> getXjllQuartervos(QueryParamVO paramvo, String jd) {
        try {
            return xjlyquarbReport.getXjllQuartervos(paramvo, jd);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getXjllQuartervos异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public List<NumMnyGlVO> getNumMnyGlVO(QueryCondictionVO paramVo) {
        try {
            return gl_rep_nmdtserv.getNumMnyGlVO(paramVo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNumMnyGlVO异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
}
