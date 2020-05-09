package com.dzf.zxkj.report.dubbo;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.AgeReportQueryVO;
import com.dzf.zxkj.common.query.QueryCondictionVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.report.service.IZxkjReportService;
import com.dzf.zxkj.report.service.batchprint.IBatchPrintSer;
import com.dzf.zxkj.report.service.cwbb.*;
import com.dzf.zxkj.report.service.cwzb.*;
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
    private IXjRjZReport gl_rep_xjyhrjzserv;
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
    @Autowired
    private IKMZZReport gl_rep_kmzjserv;

    @Autowired
    private IBatchPrintSer gl_rep_batchprinterv;

    @Override
    public String batchPrint(BatchPrintSetVo[] setvos, UserVO userVO) {
        // 根据不同设置，先循环走打印操作
        try {
            gl_rep_batchprinterv.print(setvos,userVO);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getFsJyeVOs异常,异常信息:%s", e.getMessage()), e);
            return "执行出错";
        }
        return "执行完毕";
    }

    @Override
    public FseJyeVO[] getFsJyeVOs(QueryParamVO vo, Integer direction) {
        try {
            return fsYeReport.getFsJyeVOs(vo, direction);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getFsJyeVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    public KmMxZVO[] getXJRJZVOsConMo(String pk_corp, String kmsbegin, String kmsend,
                               DZFDate begindate, DZFDate enddate, DZFBoolean xswyewfs, DZFBoolean xsyljfs,
                               DZFBoolean ishasjz, DZFBoolean ishassh, String pk_currency, List<String> kmcodelist, Object[] qryobjs){
        try {
            return gl_rep_xjyhrjzserv.getXJRJZVOsConMo(pk_corp, kmsbegin,kmsend,begindate,enddate,xswyewfs,xsyljfs,
                    ishasjz,ishassh,pk_currency,kmcodelist,qryobjs);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getKMZZVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

   public List<LrbVO[]> getBetweenLrbMap(DZFDate begdate, DZFDate enddate,
                                   String pk_corp, String xmmcid, Object[] objs, DZFBoolean ishasjz) {
       try {
           return lrbReport.getBetweenLrbMap(begdate, enddate,pk_corp,xmmcid,objs,ishasjz);
       } catch (DZFWarpException e) {
           log.error(String.format("调用getKMZZVOs异常,异常信息:%s", e.getMessage()), e);
           return null;
       }
   }

    public KmMxZVO[] getKMMXZConFzVOs(QueryParamVO vo, Object[] qryobjs){
        try {
            return gl_rep_kmmxjserv.getKMMXZConFzVOs(vo, qryobjs);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getKMZZVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    public List<ZcFzBVO[]> getZcfzVOs(DZFDate begdate, DZFDate enddate, String pk_corp, String ishasjz, String[] hasyes, Object[] qryobjs){
        try {
            return zcFzBReport.getZcfzVOs(begdate,enddate,pk_corp,ishasjz,hasyes,qryobjs );
        } catch (DZFWarpException e) {
            log.error(String.format("调用getKMZZVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }

    public KmZzVO[] getKMZZVOs(QueryParamVO vo, Object[] kmmx_objs) {
        try {
            return gl_rep_kmzjserv.getKMZZVOs(vo, kmmx_objs);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getKMZZVOs异常,异常信息:%s", e.getMessage()), e);
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
