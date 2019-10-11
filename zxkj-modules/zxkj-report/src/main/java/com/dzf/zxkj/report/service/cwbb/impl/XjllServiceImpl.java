package com.dzf.zxkj.report.service.cwbb.impl;

import com.alibaba.druid.sql.ast.SQLParameter;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BdtradecashflowVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.qcset.YntXjllqcyePageVO;
import com.dzf.zxkj.platform.model.report.XjllQcyeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.mapper.XjllMapper;
import com.dzf.zxkj.report.query.cwbb.XjllQueryVO;
import com.dzf.zxkj.report.query.cwbb.XjllbQuarterlyQueryVO;
import com.dzf.zxkj.report.query.cwzb.FsYeQueryVO;
import com.dzf.zxkj.report.query.cwzb.KmmxQueryVO;
import com.dzf.zxkj.report.service.cwbb.IXjllService;
import com.dzf.zxkj.report.service.cwbb.IXjllbQuarterlyService;
import com.dzf.zxkj.report.service.cwzb.IFsYeService;
import com.dzf.zxkj.report.service.cwzb.IKmMxZService;
import com.dzf.zxkj.report.utils.*;
import com.dzf.zxkj.report.vo.cwbb.XjllMxvo;
import com.dzf.zxkj.report.vo.cwbb.XjllbVO;
import com.dzf.zxkj.report.vo.cwbb.XjllquarterlyVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressWarnings("all")
public class XjllServiceImpl implements IXjllService {
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private IFsYeService fsYeService;
    @Autowired
    private XjllMapper xjllMapper;
    @Autowired
    private IKmMxZService kmMxZService;
    @Autowired
    private IXjllbQuarterlyService xjllbQuarterlyService;

    @Override
    public XjllbVO[] query(XjllQueryVO vo, CorpVO corpVO) throws Exception {
        //当前传入公司对应的会计科目方案
        String pk_corp = vo.getPk_corp();
        String period = vo.getQjq();
        String pk_trade_accountschema = zxkjPlatformService.getCurrentCorpAccountSchema(pk_corp);
        Map<Float, XjllbVO> map = getXJLLXMBVOs(pk_trade_accountschema, period, pk_corp);
        //获取发生额的数据
        Map<String, FseJyeVO> fsmap = getSinglePeriodFs(period, pk_corp);

        Integer kmfa = zxkjPlatformService.getAccountSchema(pk_corp);

        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(pk_corp);
        XjllbVO[] xjllbvos;
        if (DzfUtil.SEVENSCHEMA.intValue() == kmfa) { //07方案
            xjllbvos = new Qy07XjlReportService().getXJLLB2007VOs1(map, period, pk_trade_accountschema, fsmap, corpVO, cpavos);
        } else if (DzfUtil.THIRTEENSCHEMA.intValue() == kmfa) { //13方案
            xjllbvos = new XQy13XjlReportService().getXJLLB2013VOs1(map, period, pk_trade_accountschema, fsmap, corpVO, cpavos);
        } else if (DzfUtil.POPULARSCHEMA.intValue() == kmfa) {//民间
            xjllbvos = new PopularXjlReportService().getXjlBvos(map, period, corpVO, pk_trade_accountschema, cpavos);
        } else if (DzfUtil.COMPANYACCOUNTSYSTEM.intValue() == kmfa) {//企业会计制度
            xjllbvos = new CompanyXjlReportService().getXjlBvos(map, period, pk_corp, pk_trade_accountschema);
        } else {
            throw new RuntimeException("该制度暂不支持现金流量表,敬请期待!");
        }
        if (xjllbvos != null && xjllbvos.length > 0) {
            for (XjllbVO xjllbVO : xjllbvos) {
                xjllbVO.setKmfa(kmfa + "");
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
    private Map<String, FseJyeVO> getSinglePeriodFs(String period, String pk_corp) throws Exception {
        Map<String, FseJyeVO> fsmap = new HashMap<String, FseJyeVO>();
        FsYeQueryVO vo = new FsYeQueryVO();
        vo.setPk_corp(pk_corp);
        vo.setQjq(period.substring(0, 4) + "-01");
        vo.setQjz(period);
        vo.setIshasjz(DZFBoolean.FALSE);
        vo.setXswyewfs(DZFBoolean.FALSE);
        vo.setBtotalyear(DZFBoolean.TRUE);// 本年累计
        vo.setKmsx("0");//资产科目
        FseJyeVO[] fvos = fsYeService.getFsJyeVOs(vo, 1);
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

    private Map<Float, XjllbVO> getXJLLXMBVOs(String pk_trade_accountschema, String period, String pk_corp) throws Exception {
        BdtradecashflowVO[] flow1VOs = zxkjPlatformService.queryBdtradecashflowVOList(pk_trade_accountschema, null);
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
        List<XjllQcyeVO> list = zxkjPlatformService.queryXjllQcyeVOList(pk_corp, period.substring(0, 4));
        len = list == null ? 0 : list.size();
        XjllQcyeVO xqcvo = null;
        for (int i = 0; i < len; i++) {
            xqcvo = (XjllQcyeVO) list.get(i);
            vo = map.get(xqcvo.getPk_project());
            vo.setSqje(xqcvo.getNmny());
        }

        final boolean flag = xqcvo == null;

        String beginDate = DateUtils.getPeriodStartDate(period).toString();
        String endDate = DateUtils.getPeriodEndDate(period).toString();
        List<XjllbVO> xjllbVOList = xjllMapper.queryXjllbVOList(pk_corp, beginDate, endDate, period.substring(0, 4));

        len = xjllbVOList == null ? 0 : list.size();

        if (xjllbVOList != null && xjllbVOList.size() != 0) {
            xjllbVOList.stream().filter(v -> map.containsKey(v.getXm())).forEach(v -> {
                if (v.getBqje() == null) {
                    v.setBqje(DZFDouble.ZERO_DBL);
                }
                if (!flag) {
                    v.setSqje(DZFDouble.getUFDouble(v.getSqje() == null ? DZFDouble.ZERO_DBL : v.getSqje()).add((v.getSqje() == null ? DZFDouble.ZERO_DBL : v.getSqje())));
                } else {
                    v.setSqje(v.getSqje() == null ? DZFDouble.ZERO_DBL : v.getSqje());
                }
                map1.put(v.getRowno(), v);
            });
        }
        return map1;
    }

    @Override
    public Map<String, XjllbVO[]> queryEveryPeriod(XjllQueryVO vo, CorpVO corpVO) throws Exception {

        String pk_corp = vo.getPk_corp();
        String period_beg = vo.getQjq();
        String period_end = vo.getQjz();

        Map<String, XjllbVO[]> everymap = new LinkedHashMap<String, XjllbVO[]>();
        String pk_trade_accountschema = null;
        pk_trade_accountschema = zxkjPlatformService.getCurrentCorpAccountSchema(pk_corp);
        List<String> periodlists = ReportUtil.getPeriodsByPeriod(period_beg, period_end);
        //获取科目明细表的数据
        int year = Integer.parseInt(period_beg.substring(0, 4));
        if (corpVO.getBegindate().getYear() == year && corpVO.getBegindate().getMonth() != 1) {//取建账的月份(为了不显示多余的月份)
            period_beg = DateUtils.getPeriod(corpVO.getBegindate());
        }
        Map<String, Map<Float, XjllbVO>> map = getXJLLXMBVOsYear(pk_trade_accountschema, period_beg, period_end, pk_corp);
        Object[] kmmxobjs = getBaseData(period_end, corpVO);

        FsYeQueryVO paramvo = new FsYeQueryVO();
        Map<String, FseJyeVO> tfsmap = new HashMap<String, FseJyeVO>();
        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(pk_corp);
        for (String everyperiod : periodlists) {
//			everyperiod = period.substring(0, 4) +"-"+ String.format("%02d", i);// 每个期间的值
            paramvo.setQjq(everyperiod.substring(0, 4) + "-01");
            paramvo.setQjz(everyperiod);
            FseJyeVO[] fvos = fsYeService.getFsJyeVOs(paramvo, kmmxobjs);//当前期间的
            transformMap(tfsmap, fvos);
            Map<Float, XjllbVO> map1 = map.get(everyperiod);
            if (map1 == null) {//现金流量不存在，返回
                continue;
            }
            XjllbVO[] vos = null;
            if (DzfUtil.SEVENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { // 07方案
                vos = new Qy07XjlReportService().getXJLLB2007VOs1(map1, everyperiod, pk_trade_accountschema, tfsmap, corpVO, cpavos);
            } else if (DzfUtil.THIRTEENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { // 13方案
                vos = new XQy13XjlReportService().getXJLLB2013VOs1(map1, everyperiod, pk_trade_accountschema, tfsmap, corpVO, cpavos);
            } else if (DzfUtil.POPULARSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {// 民间
                vos = new PopularXjlReportService().getXjlBvos(map1, everyperiod, corpVO, pk_trade_accountschema, cpavos);
            } else if (DzfUtil.COMPANYACCOUNTSYSTEM.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {//企业会计制度
                vos = new CompanyXjlReportService().getXjlBvos(map1, everyperiod, pk_corp, pk_trade_accountschema);
            } else {
                vos = new XjllbVO[0];
            }
            everymap.put(everyperiod, vos);
        }
        return everymap;
    }

    private Object[] getBaseData(String period, CorpVO cpvo) throws Exception {
        Object[] obj;
        DZFDate begdate = cpvo.getBegindate();
        KmmxQueryVO paramvo = new KmmxQueryVO();
        paramvo.setPk_corp(cpvo.getPk_corp());
        paramvo.setBegindate1(new DZFDate(begdate.getYear() + "-01-01"));
        paramvo.setEnddate(DateUtils.getPeriodEndDate(period));
        paramvo.setIshasjz(DZFBoolean.FALSE);
        paramvo.setXswyewfs(DZFBoolean.FALSE);
        paramvo.setBtotalyear(DZFBoolean.TRUE);//本年累计
        paramvo.setKmsx("0");//资产科目
        paramvo.setCjq(1);
        paramvo.setCjz(6);
        obj = kmMxZService.getKMMXZVOs1(paramvo, false);//获取基础数据(科目明细账)
        return obj;
    }

    private Map<String, Map<Float, XjllbVO>> getXJLLXMBVOsYear(String pk_trade_accountschema, String period_beg, String period_end, String pk_corp) throws Exception {
        period_beg = period_beg.substring(0, 4) + "-01";//默认从1月开始查询
        //获取现金流量项目
        StringBuffer where = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        //查询现金流量项目
        BdtradecashflowVO[] flow1VOs = zxkjPlatformService.queryBdtradecashflowVOList(pk_trade_accountschema, null);

        //获取现金流量期初
        List<XjllQcyeVO> list_qc = zxkjPlatformService.queryXjllQcyeVOList(pk_corp, null);

        //获取现金流量发生
        List<XjllbVO> list_fs = xjllMapper.queryFsXjll(pk_corp, DateUtils.getPeriodStartDate(period_beg).toString(), DateUtils.getPeriodEndDate(period_end).toString());

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
            for (int i = 0; i < len; i++) {
                XjllbVO xjllbVO = list_fs.get(i);

                if (periodlists.get(m).equals(xjllbVO.getYear())) {
                    vo = map.get(xjllbVO.getXm());
                    if (xjllbVO.getBqje() == null) {
                        vo.setBqje(DZFDouble.ZERO_DBL);
                    } else {
                        vo.setBqje(xjllbVO.getBqje());
                    }

                    vo.setSqje(SafeCompute.add(totalmap.get(vo.getPk_project() + "_" + year), xjllbVO.getBqje() == null ? DZFDouble.ZERO_DBL : xjllbVO.getBqje()));//取合计值

                    totalmap.put(vo.getPk_project() + "_" + year, vo.getSqje());//每次计算合计值

                    map1.put(vo.getRowno(), vo);
                }
            }
            yearmap.put(periodlists.get(m), map1);
        }

        return yearmap;
    }

    @Override
    public XjllMxvo[] getXJllMX(String period, String pk_corp, String hc) throws Exception {
        String pk_trade_accountschema = zxkjPlatformService.getCurrentCorpAccountSchema(pk_corp);

        return getXJllMxvos(period, pk_corp, hc, pk_trade_accountschema);
    }


    private XjllMxvo[] getXJllMxvos(String period, String pk_corp, String hc,
                                    String pk_trade_accountschema) throws Exception {


        List<XjllMxvo> xjlllist = xjllMapper.queryXjllMxList(pk_corp, period + "%", pk_trade_accountschema, hc);

        //查询当前凭证 所属的凭证子表数据
        if (xjlllist != null && xjlllist.size() > 0) {
            List<String> pzhpkList = xjlllist.stream().map(XjllMxvo::getPzh).collect(Collectors.toList());

            List<TzpzBVO> tzpzlist = zxkjPlatformService.queryTzpzBVObyHVOPk(pzhpkList);

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
        return null;
    }

    @Override
    public XjllbVO[] getXjllDataForCwBs(String qj, String corpIds, String qjlx, CorpVO corpVO) throws Exception {
        if ("0".equals(qjlx)) {
            XjllQueryVO paramvo = new XjllQueryVO();
            paramvo.setPk_corp(corpIds);
            paramvo.setQjq(qj.substring(0, 7));
            paramvo.setQjz(qj.substring(0, 7));
            paramvo.setIshasjz(DZFBoolean.FALSE);
            paramvo.setBegindate1(DateUtils.getPeriodStartDate(qj.substring(0, 7)));
            XjllbVO[] xjllbvos = this.query(paramvo, corpVO);

            //如果是一般企业则取上年数据
            Integer corpschema = zxkjPlatformService.getAccountSchema(corpIds);
            if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {//07科目体系(一般企业) 需要查询上年同期数
                //查询上年数据
                String bef_period = DateUtils.getPreviousYearPeriod(qj.substring(0, 7));
                if (DateUtils.getPeriod(corpVO.getBegindate()).compareTo(bef_period) <= 0) {//公司建账日期在查询日期后时
                    paramvo = new XjllQueryVO();
                    paramvo.setPk_corp(corpIds);
                    paramvo.setQjq(bef_period);
                    paramvo.setQjz(bef_period);
                    paramvo.setIshasjz(DZFBoolean.FALSE);
                    paramvo.setBegindate1(DateUtils.getPeriodStartDate(bef_period));
                    XjllbVO[] bef_xjllbvos = this.query(paramvo, corpVO);
                    for (XjllbVO xjllvo : xjllbvos) {
                        for (XjllbVO lastxjllvo : bef_xjllbvos) {
                            if (!StringUtil.isEmpty(xjllvo.getXm())
                                    && xjllvo.getXm().equals(lastxjllvo.getXm())) {
                                xjllvo.setBqje_last(lastxjllvo.getBqje());
                                xjllvo.setSqje_last(lastxjllvo.getSqje());
                                break;
                            }
                        }
                    }
                }
            }
            return xjllbvos;
        } else {
            XjllbQuarterlyQueryVO paramvo = new XjllbQuarterlyQueryVO();
            paramvo.setBegindate1(DateUtils.getPeriodStartDate(qj.substring(0, 7)));
            paramvo.setPk_corp(corpIds);
            String[] resvalue = getJdValue(paramvo.getBegindate1());
            List<XjllquarterlyVO> xjllbvos_jd = xjllbQuarterlyService.getXjllQuartervos(paramvo, resvalue[1], corpVO);
            XjllbVO[] xjllbvos = converXjll(xjllbvos_jd, qj);
            return xjllbvos;
        }
    }


    private String[] getJdValue(DZFDate date) {
        String month = date.toString().substring(5, 7);
        String res = null;
        String count = "0";
        if (month.equals("02") || month.equals("01") || month.equals("03")) {
            res = "第一季度";
            count = "1";
        } else if (month.equals("04") || month.equals("05") || month.equals("06")) {
            res = "第二季度";
            count = "2";
        } else if (month.equals("07") || month.equals("08") || month.equals("09")) {
            res = "第三季度";
            count = "3";
        } else if (month.equals("10") || month.equals("11") || month.equals("12")) {
            res = "第四季度";
            count = "4";
        }
        return new String[]{res, count};
    }


    private XjllbVO[] converXjll(List<XjllquarterlyVO> xjllbjb, String qj) {
        if (xjllbjb == null || xjllbjb.size() == 0) {
            throw new RuntimeException("现金流量季报数据为空");
        }

        List<XjllbVO> list = new ArrayList<>();

        String month = qj.substring(5, 7);

        XjllbVO xjllvo;
        for (XjllquarterlyVO jbvo : xjllbjb) {
            xjllvo = new XjllbVO();
            xjllvo.setXm(jbvo.getXm());
            xjllvo.setHc(jbvo.getHc());
            xjllvo.setSqje(jbvo.getBnlj());

            if ("03".equals(month)) {
                xjllvo.setBqje(jbvo.getJd1());
                xjllvo.setBqje_last(jbvo.getJd1_last());
            } else if ("06".equals(month)) {
                xjllvo.setBqje(jbvo.getJd2());
                xjllvo.setBqje_last(jbvo.getJd2_last());
            } else if ("09".equals(month)) {
                xjllvo.setBqje(jbvo.getJd3());
                xjllvo.setBqje_last(jbvo.getJd3_last());
            } else {
                xjllvo.setBqje(jbvo.getJd4());
                xjllvo.setBqje_last(jbvo.getJd4_last());
            }
            xjllvo.setSqje(jbvo.getBnlj());
            xjllvo.setSqje_last(jbvo.getBf_bnlj());

            list.add(xjllvo);
        }

        return list.toArray(new XjllbVO[0]);
    }

    @Override
    public List<YntXjllqcyePageVO> bulidXjllQcData(List<YntXjllqcyePageVO> listvo, String pk_corp) throws Exception {
        if (listvo == null || listvo.size() == 0) {
            return listvo;
        }

        Map<Float, XjllbVO> map = new LinkedHashMap<Float, XjllbVO>();

        if (DzfUtil.SEVENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { // 07方案
            new Qy07XjlReportService().getXjllB2007Xm(map);
        } else if (DzfUtil.THIRTEENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { // 13方案
            new XQy13XjlReportService().getXjllB2013Xm(map);
        } else if (DzfUtil.POPULARSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {// 民间
            new PopularXjlReportService().getXjllbXmVos(map);
        } else if (DzfUtil.COMPANYACCOUNTSYSTEM.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {// 企业会计制度
            new CompanyXjlReportService().getXjllXmVos(map);
        }

        // 季度map
        Map<String, DZFDouble[]> jdmap = new HashMap<String, DZFDouble[]>();
        // YntXjllqcyePageVO 转换成 XjllbVO
        for (YntXjllqcyePageVO vo : listvo) {
            vo.setProjectname("　　" +vo.getProjectname());
            jdmap.put("hc("+vo.getVcode()+")", new DZFDouble[] { vo.getQ1(), vo.getQ2(), vo.getQ3(), vo.getQ4(),vo.getNmny() });
        }
        if (map.size() > 0) {
            for (Map.Entry<Float, XjllbVO> entry : map.entrySet()) {
                if(StringUtil.isEmpty(entry.getValue().getHc())){
                    entry.getValue().setHc(""+entry.getValue().getRowno());
                }
                YntXjllqcyePageVO vo = new YntXjllqcyePageVO();
                vo.setVcode(entry.getValue().getHc());
                vo.setVname(entry.getValue().getXm());
                vo.setProjectname(entry.getValue().getXm());
                vo.setIsSet("1");// 不可编辑
                String formula = entry.getValue().getFormula();
                if (!StringUtil.isEmpty(formula)) {
                    DZFDouble[] values = execFormula(formula, jdmap);
                    vo.setQ1(values[0]);
                    vo.setQ2(values[1]);
                    vo.setQ3(values[2]);
                    vo.setQ4(values[3]);
                    vo.setNmny(values[4]);
//					vo.setNmny(values[0].add(values[1]).add(values[2]).add(values[3]));
                    jdmap.put("hc("+entry.getValue().getHc()+")",values);
                }
                listvo.add(vo);
            }
        }

        //按照编码排序
        listvo.sort(new Comparator<YntXjllqcyePageVO>() {
            @Override
            public int compare(YntXjllqcyePageVO o1, YntXjllqcyePageVO o2) {
                Float f1 = Float.parseFloat(o1.getVcode() == null ? "0" : o1.getVcode()) ;
                Float f2 = Float.parseFloat(o2.getVcode() == null ? "0" : o2.getVcode());
                return f1.compareTo(f2) ;
            }
        });
        return listvo;
    }


    private DZFDouble[] execFormula(String formula, Map<String, DZFDouble[]> jdmap) {
        DZFDouble[] ress = new DZFDouble[] { DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL,
                DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL };
        String splitstr = "\\+|\\-";
        String[] strs = formula.split(splitstr);
        //四个季度的公式
        String[] formulas = new String[]{formula,formula,formula,formula,formula};
        if (strs != null && strs.length > 0) {
            for (int i = 0; i < formulas.length; i++) {
                for (String str : strs) {
                    DZFDouble[] jdvalues = jdmap.get(str.trim());
                    if (jdvalues == null) {
                        jdvalues = new DZFDouble[] { DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL,
                                DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL, };
                    }
                    formulas[i] = formulas[i].replace(str, jdvalues[i] == null ? "0" : jdvalues[i].toString());
                }
                ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
                log.info("公式:" + formulas[i]);
                try {
                    Object value = jse.eval(formulas[i]);
                    ress[i] = new DZFDouble(value.toString());
                } catch (ScriptException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return ress;
    }
}
