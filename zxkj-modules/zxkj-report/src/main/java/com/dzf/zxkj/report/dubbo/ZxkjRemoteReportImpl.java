package com.dzf.zxkj.report.dubbo;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.IRemoteReportService;
import com.dzf.zxkj.report.service.cwbb.*;
import com.dzf.zxkj.report.service.cwzb.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Slf4j
@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjRemoteReportImpl implements IRemoteReportService {

    @Autowired
    private IBatchLrbReport gl_rep_batchlrbserv;
    @Autowired
    private ICwgyInfoReport iCwgyInfoReport;
    @Autowired
    private IZcFzBReport gl_rep_zcfzserv;
    @Autowired
    private IFsYeReport gl_rep_fsyebserv;
    @Autowired
    private ILrbReport gl_rep_lrbserv;
    @Autowired
    private IXjllbReport gl_rep_xjlybserv;
    @Autowired
    private IKMMXZReport gl_rep_kmmxjserv;
    @Autowired
    private IAppReport appservice;
    @Autowired
    private IFkTjBgService gl_fktjbgserv;
    @Autowired
    private IXjRjZReport gl_rep_xjyhrjzserv;
    @Autowired
    private ILrbQuarterlyReport gl_rep_lrbquarterlyserv;
    @Autowired
    private INummnyReport gl_rep_nmdtserv;
    @Autowired
    private IFzhsYebReport gl_rep_fzyebserv;
    @Autowired
    private IFzKmmxReport gl_rep_fzkmmxjrptserv;

    @Override
    public Map<String, double[]> queryLrbData(String period, String[] cids) {
        try {
            return gl_rep_batchlrbserv.queryLrbFromCorpids(period, cids);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getKMZZVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Map<String, CwgyInfoVO[]> getCwgyInfoVOs(String year, String pk_corp, Object[] obj) {
        try {
            return iCwgyInfoReport.getCwgyInfoVOs(year, pk_corp, obj);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getCwgyInfoVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Object[] getZCFZBVOsConMsg(String period, String pk_corp, String ishasjz, String[] hasyes) {
        try {
            return gl_rep_zcfzserv.getZCFZBVOsConMsg(period, pk_corp, ishasjz,hasyes);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getZCFZBVOsConMsg异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public ZcFzBVO[] getZCFZBVOs(String period, String pk_corp, String ishasjz, String hasyes){
        try {
            return gl_rep_zcfzserv.getZCFZBVOs(period, pk_corp, ishasjz,hasyes);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getZCFZBVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Object[] getYearFsJyeVOs(String year, String pk_corp, Object[] qryobjs, String rptsource){
        try {
            return gl_rep_fsyebserv.getYearFsJyeVOs(year, pk_corp, qryobjs,rptsource);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getYearFsJyeVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Object[] getEveryPeriodFsJyeVOs(DZFDate startdate, DZFDate enddate, String pk_corp, Object[] objs, String rptsource, DZFBoolean ishasjz){
        try {
            return gl_rep_fsyebserv.getEveryPeriodFsJyeVOs(startdate, enddate, pk_corp,objs,rptsource,ishasjz);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getEveryPeriodFsJyeVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public LrbVO[] getLrbVos(QueryParamVO vo, String pk_corp, Map<String, YntCpaccountVO> mp,
                      Map<String, FseJyeVO> map, String xmmcid){
        try {
            return gl_rep_lrbserv.getLrbVos(vo, pk_corp, mp,map,xmmcid);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getLrbVos异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public XjllbVO[] query(QueryParamVO vo){
        try {
            return gl_rep_xjlybserv.query(vo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用query异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public KmMxZVO[] getKMMXZVOs(QueryParamVO vo, Object[] qryobj) {

        try {
            return gl_rep_kmmxjserv.getKMMXZVOs(vo,qryobj);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getKMMXZVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public ExpendpTionVO[] getAppSjMny(String period, String pk_corp, String pk_currency){
        try {
            return appservice.getAppSjMny(period,pk_corp,pk_currency);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getAppSjMny异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public DZFDouble[] getAppNetProfit(String year, String pk_corp){
        try {
            return appservice.getAppNetProfit(year,pk_corp);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getAppNetProfit异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Object[] queryZzsBg(String year, CorpVO cpvo){
        try {
            return gl_fktjbgserv.queryZzsBg(year,cpvo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用queryZzsBg异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public LrbVO[] getLRBVOs(QueryParamVO paramVO) {
        try {
            return gl_rep_lrbserv.getLRBVOs(paramVO);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getLRBVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public KmMxZVO[] getXJRJZVOsConMo(String pk_corp, String kmsbegin, String kmsend,
                                      DZFDate begindate, DZFDate enddate, DZFBoolean xswyewfs, DZFBoolean xsyljfs,
                                      DZFBoolean ishasjz, DZFBoolean ishassh, String pk_currency, List<String> kmcodelist, Object[] qryobjs){
        try {
            return gl_rep_xjyhrjzserv.getXJRJZVOsConMo( pk_corp,  kmsbegin,  kmsend,
                     begindate,  enddate,  xswyewfs,  xsyljfs,
                     ishasjz,  ishassh,  pk_currency, kmcodelist, qryobjs);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getXJRJZVOsConMo异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public LrbquarterlyVO[] getLRBquarterlyVOs(QueryParamVO paramVO){
        try {
            return gl_rep_lrbquarterlyserv.getLRBquarterlyVOs(paramVO);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getLRBquarterlyVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public List<NumMnyDetailVO> getNumMnyDetailVO(String startDate, String enddate,
                                                  String pk_inventory, QueryParamVO paramvo, String pk_corp, String user_id, String pk_bz, String xsfzhs, DZFDate begdate){
        try {
            return gl_rep_nmdtserv.getNumMnyDetailVO(startDate,enddate,pk_inventory,paramvo,pk_corp,user_id,pk_bz,xsfzhs,begdate);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getNumMnyDetailVO异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public List<FzYebVO> getFzYebVOs(KmReoprtQueryParamVO paramVo){
        try {
            return gl_rep_fzyebserv.getFzYebVOs(paramVo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getFzYebVOs异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Object[] getFzkmmxVos(KmReoprtQueryParamVO paramavo, DZFBoolean bshowcolumn){
        try {
            return gl_rep_fzkmmxjrptserv.getFzkmmxVos(paramavo,bshowcolumn);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getFzkmmxVos异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
    @Override
    public Map<String,List<FzKmmxVO>> getAllFzKmmxVos(KmReoprtQueryParamVO paramavo){
        try {
            return gl_rep_fzkmmxjrptserv.getAllFzKmmxVos(paramavo);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getAllFzKmmxVos异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
}
