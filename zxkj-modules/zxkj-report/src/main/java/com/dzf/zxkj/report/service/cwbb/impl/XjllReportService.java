package com.dzf.zxkj.report.service.cwbb.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.BdtradecashflowVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.XjllMxvo;
import com.dzf.zxkj.platform.model.report.XjllQcyeVO;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.utils.*;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

//import com.dzf.model.gl.gl_bdset.BDCorpAccountVO;

/**
 * 现金流量报表
 */
@Component("xjllReportService")
@SuppressWarnings("all")
public class XjllReportService {

    @Reference(version = "1.0.0")
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private SingleObjectBO singleObjectBO = null;
    @Autowired
    private IZcFzBReport gl_rep_zcfzserv;

    @Autowired
    private IFsYeReport gl_rep_fsyebserv;

    @Autowired
    private IKMMXZReport gl_rep_kmmxjserv;


    /**
     * 现金流量报表取数
     *
     * @param period
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    public XjllbVO[] getXjllbVOs(String period, String pk_corp) throws DZFWarpException {
        //当前传入公司对应的会计科目方案
        String pk_trade_accountschema = null;
        pk_trade_accountschema = zxkjPlatformService.getCurrentCorpAccountSchema(pk_corp);
        Map<Float, XjllbVO> map = getXJLLXMBVOs(pk_trade_accountschema, period, pk_corp);

        //获取发生额的数据
        Map<String, FseJyeVO> fsmap = getSinglePeriodFs(period, pk_corp);

        Integer kmfa = zxkjPlatformService.getAccountSchema(pk_corp);

        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(pk_corp);
        CorpVO cpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
        XjllbVO[] xjllbvos = null;
        if (DzfUtil.SEVENSCHEMA.intValue() == kmfa) { //07方案
            xjllbvos = new Qy07XjlReportService(zxkjPlatformService).getXJLLB2007VOs1(map, period, pk_corp, cpvo.getBegindate(), cpvo.getCorptype(), pk_trade_accountschema, fsmap);
        } else if (DzfUtil.THIRTEENSCHEMA.intValue() == kmfa) { //13方案
            xjllbvos = new XQy13XjlReportService(zxkjPlatformService).getXJLLB2013VOs1(map, period, pk_corp, cpvo.getBegindate(), cpvo.getCorptype(), pk_trade_accountschema, fsmap);
        } else if (DzfUtil.POPULARSCHEMA.intValue() == kmfa) {//民间
            xjllbvos = new PopularXjlReportService(gl_rep_fsyebserv).getXjlBvos(map, period, cpvo.getPk_corp(), cpvo.getCorptype(), pk_trade_accountschema, cpavos);
        } else if (DzfUtil.COMPANYACCOUNTSYSTEM.intValue() == kmfa) {//企业会计制度
            xjllbvos = new CompanyXjlReportService(gl_rep_fsyebserv).getXjlBvos(map, period, pk_corp, pk_trade_accountschema);
        } else {
            throw new BusinessException("该制度暂不支持现金流量表,敬请期待!");
        }
        if (xjllbvos != null && xjllbvos.length > 0) {
            for (XjllbVO vo : xjllbvos) {
                vo.setKmfa(kmfa + "");
            }
        }
        return xjllbvos;
    }

    /**
     * 获取单个期间的发生
     *
     * @param period
     * @param pk_corp
     */
    private Map<String, FseJyeVO> getSinglePeriodFs(String period, String pk_corp) {
        Map<String, FseJyeVO> fsmap = new HashMap<String, FseJyeVO>();
        QueryParamVO vo = new QueryParamVO();
        vo.setPk_corp(pk_corp);
        vo.setQjq(period.substring(0, 4) + "-01");
        vo.setQjz(period);
        vo.setIshasjz(DZFBoolean.FALSE);
        vo.setXswyewfs(DZFBoolean.FALSE);
        vo.setBtotalyear(DZFBoolean.TRUE);// 本年累计
        vo.setKmsx("0");//资产科目
        FseJyeVO[] fvos = gl_rep_fsyebserv.getFsJyeVOs(vo, 1);
        transformMap(fsmap, fvos);
        return fsmap;
    }

    private void transformMap(Map<String, FseJyeVO> fsmap, FseJyeVO[] fvos) {
        if (fvos != null && fvos.length > 0) {
            for (FseJyeVO fsvo : fvos) {
                fsmap.put(fsvo.getKmbm(), fsvo);
            }
        }
    }

    /**
     * 截止到当前期间的每个期间的现金流量
     *
     * @return
     * @throws DZFWarpException
     */
    public Map<String, XjllbVO[]> getXjllbVOsPeriods(String period_beg, String period_end, String pk_corp) throws DZFWarpException {
//		period = period.substring(0,4)+"-12";
        Map<String, XjllbVO[]> everymap = new LinkedHashMap<String, XjllbVO[]>();
        String pk_trade_accountschema = null;
        pk_trade_accountschema = zxkjPlatformService.getCurrentCorpAccountSchema(pk_corp);
        List<String> periodlists = ReportUtil.getPeriodsByPeriod(period_beg, period_end);
        //获取科目明细表的数据
        CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
        int year = Integer.parseInt(period_beg.substring(0, 4));
        if (cpvo.getBegindate().getYear() == year && cpvo.getBegindate().getMonth() != 1) {//取建账的月份(为了不显示多余的月份)
            period_beg = DateUtils.getPeriod(cpvo.getBegindate());
        }
        Map<String, Map<Float, XjllbVO>> map = getXJLLXMBVOsYear(pk_trade_accountschema, period_beg, period_end, pk_corp);
        Object[] kmmxobjs = getBaseData(period_end, cpvo);
        QueryParamVO paramvo = new QueryParamVO();
        Map<String, FseJyeVO> tfsmap = new HashMap<String, FseJyeVO>();
        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(pk_corp);
        for (String everyperiod : periodlists) {
//			everyperiod = period.substring(0, 4) +"-"+ String.format("%02d", i);// 每个期间的值
            paramvo.setQjq(everyperiod.substring(0, 4) + "-01");
            paramvo.setQjz(everyperiod);
            FseJyeVO[] fvos = gl_rep_fsyebserv.getFsJyeVOs(paramvo, kmmxobjs);//当前期间的
            transformMap(tfsmap, fvos);
            Map<Float, XjllbVO> map1 = map.get(everyperiod);
            if (map1 == null) {//现金流量不存在，返回
                continue;
            }
            XjllbVO[] vos = null;
            if (DzfUtil.SEVENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { // 07方案
                vos = new Qy07XjlReportService(zxkjPlatformService).getXJLLB2007VOs1(map1, everyperiod, pk_corp, cpvo.getBegindate(),cpvo.getCorptype(), pk_trade_accountschema, tfsmap);
            } else if (DzfUtil.THIRTEENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { // 13方案
                vos = new XQy13XjlReportService(zxkjPlatformService).getXJLLB2013VOs1(map1, everyperiod, pk_corp, cpvo.getBegindate(),cpvo.getCorptype(),pk_trade_accountschema, tfsmap);
            } else if (DzfUtil.POPULARSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {// 民间
                vos = new PopularXjlReportService(gl_rep_fsyebserv).getXjlBvos(map1, everyperiod, cpvo.getPk_corp(),cpvo.getCorptype(), pk_trade_accountschema, cpavos);
            } else if (DzfUtil.COMPANYACCOUNTSYSTEM.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {//企业会计制度
                vos = new CompanyXjlReportService(gl_rep_fsyebserv).getXjlBvos(map1, everyperiod, pk_corp, pk_trade_accountschema);
            } else {
                vos = new XjllbVO[0];
            }
            everymap.put(everyperiod, vos);
        }
        return everymap;
    }

    /**
     * 查询科目明细账数据
     *
     * @param qj
     * @param paramvo
     * @param vo
     * @param cpvo
     * @return
     */
    private Object[] getBaseData(String period, CorpVO cpvo) {
        Object[] obj;
        DZFDate begdate = cpvo.getBegindate();
        QueryParamVO paramvo = new QueryParamVO();
        paramvo.setPk_corp(cpvo.getPk_corp());
        paramvo.setBegindate1(new DZFDate(begdate.getYear() + "-01-01"));
        paramvo.setEnddate(DateUtils.getPeriodEndDate(period));
        paramvo.setIshasjz(DZFBoolean.FALSE);
        paramvo.setXswyewfs(DZFBoolean.FALSE);
        paramvo.setBtotalyear(DZFBoolean.TRUE);//本年累计
        paramvo.setKmsx("0");//资产科目
        paramvo.setCjq(1);
        paramvo.setCjz(6);
        obj = gl_rep_kmmxjserv.getKMMXZVOs1(paramvo, false);//获取基础数据(科目明细账)
        return obj;
    }

    private Map<String, Map<Float, XjllbVO>> getXJLLXMBVOsYear(String pk_trade_accountschema, String period_beg, String period_end, String pk_corp) throws DZFWarpException {
        period_beg = period_beg.substring(0, 4) + "-01";//默认从1月开始查询
        //获取现金流量项目
        StringBuffer where = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        //查询现金流量项目
        BdtradecashflowVO[] flow1VOs = queryXjllXm(pk_trade_accountschema, where, sp);

        //获取现金流量期初
        List<XjllQcyeVO> list_qc = queryQcXjll(pk_corp, where, sp);

        //获取现金流量发生
        List<Object[]> list_fs = queryFsXjll(period_beg, period_end, pk_corp, where, sp);

        Map<String, Map<Float, XjllbVO>> yearmap = new LinkedHashMap<String, Map<Float, XjllbVO>>();

        DZFDate begindate = DateUtils.getPeriodStartDate(period_beg);

        DZFDate enddate = DateUtils.getPeriodEndDate(period_end);

        List<String> periodlists = ReportUtil.getPeriods(begindate, enddate);

        Map<String, DZFDouble> totalmap = new HashMap<String, DZFDouble>();

        //赋值期初的值
        int len = list_qc == null ? 0 : list_qc.size();
        XjllQcyeVO xqcvo = null;
        for (int i = 0; i < len; i++) {
            xqcvo = (XjllQcyeVO) list_qc.get(i);
            totalmap.put(xqcvo.getPk_project() + "_" + xqcvo.getYear(), VoUtils.getDZFDouble(xqcvo.getNmny()));
        }

        for (int m = 0; m < periodlists.size(); m++) {
            len = flow1VOs == null ? 0 : flow1VOs.length;
            String year = periodlists.get(m).substring(0, 4);
            Map<String, XjllbVO> map = new HashMap<String, XjllbVO>();
            Map<Float, XjllbVO> map1 = new HashMap<Float, XjllbVO>();
            BdtradecashflowVO cfvo = null;
            XjllbVO vo = null;
            for (int i = 0; i < len; i++) {
                cfvo = flow1VOs[i];
                vo = new XjllbVO();
                vo.setXm("　　" + cfvo.getItemname());
                vo.setHc(cfvo.getItemcode());
                vo.setPk_project(cfvo.getPk_trade_cashflow());//项目id
                vo.setRowno(Float.parseFloat(cfvo.getItemcode()));
                vo.setSqje(totalmap.get(vo.getPk_project() + "_" + year));//赋值
                map.put(cfvo.getPrimaryKey(), vo);
                map1.put(vo.getRowno(), vo);
            }


            len = list_fs == null ? 0 : list_fs.size();
            Object[] objs = null;
            for (int i = 0; i < len; i++) {
                objs = (Object[]) list_fs.get(i);
                if (periodlists.get(m).equals((String) objs[0])) {
                    vo = map.get(objs[2]);
                    if (objs[1] == null) {
                        vo.setBqje(DZFDouble.ZERO_DBL);
                    } else {
                        vo.setBqje(new DZFDouble(objs[1].toString()));
                    }

                    vo.setSqje(SafeCompute.add(totalmap.get(vo.getPk_project() + "_" + year), new DZFDouble(objs[1] == null ? "0" : objs[1].toString())));//取合计值

                    totalmap.put(vo.getPk_project() + "_" + year, vo.getSqje());//每次计算合计值

                    map1.put(vo.getRowno(), vo);
                }
            }
            yearmap.put(periodlists.get(m), map1);
        }

        return yearmap;
    }

    private List<Object[]> queryFsXjll(String period_beg, String period_end, String pk_corp, StringBuffer where, SQLParameter sp) {
        where.setLength(0);//清空值
        where.append(" select substr(doperatedate,0,7),sum(nvl(nmny,0))  f2, pk_xjllxm   ");
        where.append(" from YNT_XJLL ");
        where.append(" where   doperatedate>=?  and doperatedate<=? ");
        where.append("       and pk_corp=? and nvl(dr,0)=0 ");
        where.append(" group by substr(doperatedate,0,7) , pk_xjllxm ");
        sp.clearParams();
        sp.addParam(DateUtils.getPeriodStartDate(period_beg));
        sp.addParam(DateUtils.getPeriodEndDate(period_end));
        sp.addParam(pk_corp);
        List<Object[]> list_fs = (List) singleObjectBO.executeQuery(where.toString(), sp, new ArrayListProcessor());
        return list_fs;
    }

    private List<XjllQcyeVO> queryQcXjll(String pk_corp, StringBuffer where, SQLParameter sp) {
        where.setLength(0);
        where.append(" select sum(nvl(Nmny,0)) nmny, pk_project,substr(bd_corp.begindate,0,4) as year ");
        where.append(" from YNT_XJLLQCYE inner join bd_corp on YNT_XJLLQCYE.pk_corp =bd_corp.pk_corp ");
        where.append(" where YNT_XJLLQCYE.pk_corp=? and nvl(YNT_XJLLQCYE.dr,0)=0  ");
//		where.append(" and substr(bd_corp.begindate,0,4)=? ");
        where.append("  group by pk_project,substr(bd_corp.begindate,0,4) ");
        sp.clearParams();
        sp.addParam(pk_corp);
//		sp.addParam(period.substring(0, 4));
        List<XjllQcyeVO> list_qc = (List<XjllQcyeVO>) singleObjectBO.executeQuery(where.toString(), sp, new BeanListProcessor(XjllQcyeVO.class));
        return list_qc;
    }

    private BdtradecashflowVO[] queryXjllXm(String pk_trade_accountschema, StringBuffer where, SQLParameter sp) {
        where.append("  nvl(dr,0)=0 and pk_trade_accountschema=? ");
        sp.addParam(pk_trade_accountschema);
        BdtradecashflowVO[] flow1VOs = (BdtradecashflowVO[]) singleObjectBO.queryByCondition(BdtradecashflowVO.class, where.toString(), sp);
        return flow1VOs;
    }

    private Map<Float, XjllbVO> getXJLLXMBVOs(String pk_trade_accountschema, String period, String pk_corp) throws DZFWarpException {
        StringBuffer where = new StringBuffer();
        where.append("  nvl(dr,0)=0 and pk_trade_accountschema=? ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_trade_accountschema);
        BdtradecashflowVO[] flow1VOs = (BdtradecashflowVO[]) singleObjectBO.queryByCondition(BdtradecashflowVO.class, where.toString(), sp);
        int len = flow1VOs == null ? 0 : flow1VOs.length;
        Map<String, XjllbVO> map = new HashMap<String, XjllbVO>();
        Map<Float, XjllbVO> map1 = new HashMap<Float, XjllbVO>();
        BdtradecashflowVO cfvo = null;
        XjllbVO vo = null;
        for (int i = 0; i < len; i++) {
            cfvo = flow1VOs[i];
            vo = new XjllbVO();
            vo.setXm("　　" + cfvo.getItemname());
            vo.setHc(cfvo.getItemcode());
            vo.setRowno(Float.parseFloat(cfvo.getItemcode()));
            vo.setXmid(cfvo.getPrimaryKey());
            map.put(cfvo.getPrimaryKey(), vo);
            map1.put(vo.getRowno(), vo);
        }
        where.setLength(0);
        where.append(" select sum(nvl(Nmny,0)) nmny, pk_project ");
        where.append(" from YNT_XJLLQCYE inner join bd_corp on YNT_XJLLQCYE.pk_corp =bd_corp.pk_corp ");
        where.append(" where YNT_XJLLQCYE.pk_corp=? and nvl(YNT_XJLLQCYE.dr,0)=0  ");
        where.append(" and substr(bd_corp.begindate,0,4)=? ");
        where.append("  group by pk_project ");
        sp.clearParams();
        sp.addParam(pk_corp);
        sp.addParam(period.substring(0, 4));
        List list = (List) singleObjectBO.executeQuery(where.toString(), sp, new BeanListProcessor(XjllQcyeVO.class));
        len = list == null ? 0 : list.size();
        XjllQcyeVO xqcvo = null;
        for (int i = 0; i < len; i++) {
            xqcvo = (XjllQcyeVO) list.get(i);
            vo = map.get(xqcvo.getPk_project());
            vo.setSqje(xqcvo.getNmny());
        }
        where.setLength(0);//清空值
        where.append(" select sum(case when doperatedate>=? and doperatedate<=?  then nvl(nmny,0) else 0 end) f1,  ");
        where.append(" sum(nvl(nmny,0))  f2, pk_xjllxm ");
        where.append(" from YNT_XJLL ");
        where.append(" where substr(doperatedate,0,4) = ?  and doperatedate<=? ");
        where.append("       and pk_corp=? and nvl(dr,0)=0  ");
        where.append(" group by pk_xjllxm ");
        sp.clearParams();
        sp.addParam(DateUtils.getPeriodStartDate(period));
        sp.addParam(DateUtils.getPeriodEndDate(period));
        sp.addParam(period.substring(0, 4));
        sp.addParam(DateUtils.getPeriodEndDate(period));
        sp.addParam(pk_corp);
        list = (List) singleObjectBO.executeQuery(where.toString(), sp, new ArrayListProcessor());
        len = list == null ? 0 : list.size();
        Object[] objs = null;
        for (int i = 0; i < len; i++) {
            objs = (Object[]) list.get(i);
            vo = map.get(objs[2]);
            if (vo == null) {//表项不存在，则继续下一次循环
                continue;
            }
            if (objs[0] == null) {
                vo.setBqje(DZFDouble.ZERO_DBL);
            } else {
                vo.setBqje(new DZFDouble(objs[0].toString()));
            }
            if (xqcvo != null) {
                vo.setSqje(DZFDouble.getUFDouble(vo.getSqje() == null ? DZFDouble.ZERO_DBL : vo.getSqje()).add(new DZFDouble(objs[1] == null ? "0" : objs[1].toString())));
            } else {
                vo.setSqje(new DZFDouble(objs[1] == null ? "0" : objs[1].toString()));
            }
            map1.put(vo.getRowno(), vo);
        }
        return map1;
    }


    public XjllMxvo[] getXJllMX(String period, String pk_corp, String hc) throws DZFWarpException {

        String pk_trade_accountschema = zxkjPlatformService.getCurrentCorpAccountSchema(pk_corp);

        return getXJllMxvos(period, pk_corp, hc, pk_trade_accountschema);
    }

    private XjllMxvo[] getXJllMxvos(String period, String pk_corp, String hc,
                                    String pk_trade_accountschema) throws DZFWarpException {
        // 查询现金流量发生表
        SQLParameter sp = new SQLParameter();
        StringBuffer wpflow = new StringBuffer();
        wpflow.append(" select * from ynt_tdcashflow where  nvl(dr,0)=0 and pk_trade_accountschema= ? ");
        sp.addParam(pk_trade_accountschema);
        if (!StringUtil.isEmpty(hc)) {
            wpflow.append(" and itemcode= ?  ");
            sp.addParam(hc);
        }
//		BdTradeCashflowVO[] flow1VOs = (BdTradeCashflowVO[]) singleObjectBO.queryByCondition(BdTradeCashflowVO.class,wpflow.toString() , sp);
        List<String> ids = (List<String>) singleObjectBO.executeQuery(wpflow.toString(), sp, new ColumnListProcessor());
        if (ids != null && ids.size() > 0) {
//			BdTradeCashflowVO flow1VO = flow1VOs[0];

            StringBuffer xjllsql = new StringBuffer();
            xjllsql.append("	select h.pzh as pzh,h.doperatedate as dopedate,  ");
            xjllsql.append("    c.nmny as jffs ,h.pk_tzpz_h,td.itemname as xm ,td.itemcode as xmcode ");
            xjllsql.append("    from  ynt_tzpz_h  h  ");
            xjllsql.append("      inner join ynt_xjll c on h.pk_tzpz_h = c.pk_tzpz_h and nvl(c.dr,0)=0 ");
            xjllsql.append("    left join ynt_tdcashflow td on c.pk_xjllxm = td.pk_trade_cashflow ");
            xjllsql.append("     where      c.doperatedate like ? and c.pk_corp= ? ");
            xjllsql.append("   and  " + SqlUtil.buildSqlForIn("c.pk_xjllxm", ids.toArray(new String[0])));
            sp.clearParams();
//			sp.addParam(flow1VO.getPrimaryKey());
            sp.addParam(period + "%");
            sp.addParam(pk_corp);
            xjllsql.append("	  order by to_number(xmcode),pzh ");

            List<XjllMxvo> xjlllist = (List<XjllMxvo>) singleObjectBO.executeQuery(xjllsql.toString(), sp, new BeanListProcessor(XjllMxvo.class));

            //查询当前凭证 所属的凭证子表数据
            if (xjlllist != null && xjlllist.size() > 0) {
                StringBuffer wherepart = new StringBuffer();
                for (XjllMxvo mxvo : xjlllist) {
                    wherepart.append("'" + mxvo.getPk_tzpz_h() + "',");
                }
                StringBuffer pzsql = new StringBuffer();
                pzsql.append(" select * from ynt_tzpz_b  ");
                pzsql.append("  where nvl(dr,0)=0 and pk_tzpz_h in (" + wherepart.substring(0, wherepart.length() - 1) + ") order by pk_tzpz_h,rowno");

                List<TzpzBVO> tzpzlist = (List<TzpzBVO>) singleObjectBO.executeQuery(pzsql.toString(), new SQLParameter(), new BeanListProcessor(TzpzBVO.class));

                Map<String, String[]> pzmap = new HashMap<String, String[]>();
                Set<String> codeset = new HashSet<String>();

                if (tzpzlist != null && tzpzlist.size() > 0) {
                    for (TzpzBVO bvo : tzpzlist) {
                        if (!bvo.getVcode().startsWith("1001") &&
                                !bvo.getVcode().startsWith("1002") &&
                                !bvo.getVcode().startsWith("1012")) {
                            if (!codeset.contains(bvo.getPk_tzpz_h() + bvo.getVcode())) {
                                if (pzmap.containsKey(bvo.getPk_tzpz_h())) {
                                    String[] strs = pzmap.get(bvo.getPk_tzpz_h());
                                    String strcodetemp = strs[1] + "," + bvo.getVcode();
                                    String strnametemp = strs[2] + "," + bvo.getVname();
                                    pzmap.put(bvo.getPk_tzpz_h(), new String[]{strs[0], strcodetemp, strnametemp});
                                } else {
                                    String[] strs = new String[3];
                                    strs[0] = bvo.getZy();
                                    strs[1] = bvo.getVcode();
                                    strs[2] = bvo.getVname();
                                    pzmap.put(bvo.getPk_tzpz_h(), strs);
                                }
                                codeset.add(bvo.getPk_tzpz_h() + bvo.getVcode());
                            }
                        }
                    }
                }

                for (XjllMxvo mxvo : xjlllist) {
                    if (pzmap.get(mxvo.getPk_tzpz_h()) != null) {
                        mxvo.setZy(pzmap.get(mxvo.getPk_tzpz_h())[0]);
                        mxvo.setCode(pzmap.get(mxvo.getPk_tzpz_h())[1]);
                        mxvo.setName(pzmap.get(mxvo.getPk_tzpz_h())[2]);
                    }
                }
            }

            if (xjlllist != null && xjlllist.size() > 0) {
                return xjlllist.toArray(new XjllMxvo[0]);
            }

        }
        return null;
    }

}

