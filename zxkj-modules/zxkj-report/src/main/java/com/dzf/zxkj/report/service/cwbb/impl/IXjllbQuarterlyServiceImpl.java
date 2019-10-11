package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.query.cwbb.XjllbQuarterlyQueryVO;
import com.dzf.zxkj.report.service.cwbb.IXjllbQuarterlyService;
import com.dzf.zxkj.report.service.cwzb.IFsYeService;
import com.dzf.zxkj.report.utils.*;
import com.dzf.zxkj.report.vo.cwbb.XjllbVO;
import com.dzf.zxkj.report.vo.cwbb.XjllquarterlyVO;
import com.dzf.zxkj.report.vo.cwbb.ZcFzBVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@SuppressWarnings("all")
public class IXjllbQuarterlyServiceImpl implements IXjllbQuarterlyService {

    @Autowired
    private IFsYeService fsYeService;

    @Reference(version = "1.0.0")
    private IZxkjPlatformService zxkjPlatformService;

    @Override
    public List<XjllquarterlyVO> getXjllQuartervos(XjllbQuarterlyQueryVO queryVO, String jd, CorpVO corpVO) throws Exception {
        // 获取当前的现金流量数据
        List<XjllquarterlyVO> xjqueryvo = obtainCurrXjll(queryVO, jd, corpVO);

        // 获取前一年的现金流量数据
        DZFDate bf_date = new DZFDate(DateUtils.getPreviousYear(queryVO.getBegindate1().getMillis()));
        queryVO.setBegindate1(bf_date);
        List<XjllquarterlyVO> bfxjqueryvo = obtainCurrXjll(queryVO, jd, corpVO);
        // 赋值同期上年累计值
        for (XjllquarterlyVO vo1 : xjqueryvo) {
            if (bfxjqueryvo != null && bfxjqueryvo.size() > 0) {
                for (XjllquarterlyVO vo2 : bfxjqueryvo) {
                    if (vo1.getXm().equals(vo2.getXm())) {
                        vo1.setBf_bnlj(vo2.getBnlj());// 赋值上年累计数
                        vo1.setJd1_last(vo2.getJd1());
                        vo1.setJd2_last(vo2.getJd2());
                        vo1.setJd3_last(vo2.getJd3());
                        vo1.setJd4_last(vo2.getJd4());
                    }
                }
            }
        }

        return xjqueryvo;
    }


    private List<XjllquarterlyVO> obtainCurrXjll(XjllbQuarterlyQueryVO paramvo, String jd, CorpVO corpVO) throws Exception {
        DZFDate begdate = null;//开始日期

        DZFDate endate = null;//结束日期

        String year = paramvo.getBegindate1().getYear() + "";

        String begstr = null;

        if (year.equals(corpVO.getBegindate().getYear() + "")) {
            begstr = corpVO.getBegindate().toString().substring(0, 7) + "-01";
        } else {
            begstr = year + "-01-01";
        }

        if (year.compareTo(corpVO.getBegindate().getYear() + "") < 0) {//数据不存在
            return null;
        }

        String endstr = year + "-12" + "-01";

        begdate = new DZFDate(begstr);

        endate = new DZFDate(endstr);

        List<String> periods = ReportUtil.getPeriods(begdate, endate);

        // 查询现金流量的发生数据(每一期的数据)
        Map<String, Map<Float, XjllbVO>> xjllmap = getXjllMapBvos(begdate, endate, periods, paramvo);

        Map<String, List<FseJyeVO>> fsmap = getFsYjMap(paramvo, new DZFDate(year + "-01-01"), endate, periods);

        // 根据现金流量+资产负债的数据生成
        List<XjllquarterlyVO> xjqueryvo = genXjJdVO(paramvo, jd, year, periods, xjllmap, corpVO, fsmap);

        return xjqueryvo;
    }


    /**
     * 现金流量报表取数
     *
     * @param period
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
    public XjllbVO[] getXjllbVOs(String period, CorpVO cpvo,
                                 Map<Float, XjllbVO> map, String pk_trade_accountschema,
                                 YntCpaccountVO[] cpavos, Map<String, FseJyeVO> fsmap) throws Exception {
        String pk_corp = cpvo.getPk_corp();
        if (DzfUtil.SEVENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { //07方案
            return new Qy07XjlReportService().getXJLLB2007VOs1(map, period, pk_trade_accountschema, fsmap, cpvo, cpavos);
        } else if (DzfUtil.THIRTEENSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) { //13方案
            return new XQy13XjlReportService().getXJLLB2013VOs1(map, period, pk_trade_accountschema, fsmap, cpvo, cpavos);
        } else if (DzfUtil.POPULARSCHEMA.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {//民间
            return new PopularXjlReportService().getXjlBvos(map, period, cpvo, pk_trade_accountschema, cpavos);
        } else if (DzfUtil.COMPANYACCOUNTSYSTEM.intValue() == zxkjPlatformService.getAccountSchema(pk_corp)) {//企业会计准则
            return new CompanyXjlReportService().getXjlBvos(map, period, pk_corp, pk_trade_accountschema);
        } else {
            throw new RuntimeException("该制度暂不支持现金流量表,敬请期待!");
        }
    }


    private List<XjllquarterlyVO> genXjJdVO(XjllbQuarterlyQueryVO paramvo, String jd, String year, List<String> periods,
                                            Map<String, Map<Float, XjllbVO>> xjllmap, CorpVO cpvo, Map<String, List<FseJyeVO>> fsmap) throws Exception {
        List<XjllbVO[]> xjllbvos = new ArrayList<XjllbVO[]>();
        Map<String, XjllbVO[]> xjperiodmap = new HashMap<String, XjllbVO[]>();
        List<XjllquarterlyVO> xjqueryvo = new ArrayList<XjllquarterlyVO>();
        XjllbVO[] firsttemp = null;
        //当前传入公司对应的会计科目方案
        String pk_trade_accountschema = zxkjPlatformService.getCurrentCorpAccountSchema(cpvo.getPk_corp());
        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(cpvo.getPk_corp());
        for (String period : periods) {
            List<FseJyeVO> fslist = fsmap.get(period);
            Map<String, FseJyeVO> fsmap_period = new HashMap<String, FseJyeVO>();
            if (fslist != null && fslist.size() > 0) {
                fsmap_period = ReportUtil.conVertMapFs(fslist);
            }
            XjllbVO[] bvostemp = getXjllbVOs(period, cpvo, xjllmap.get(period), pk_trade_accountschema, cpavos, fsmap_period);
            xjllbvos.add(bvostemp);
            xjperiodmap.put(period, bvostemp);

            if (firsttemp == null) {
                firsttemp = bvostemp;
            }
        }
        for (XjllbVO bvo : firsttemp) {
            XjllquarterlyVO quarvo = new XjllquarterlyVO();
            quarvo.setXm(bvo.getXm());
            quarvo.setHc(StringUtil.isEmpty(bvo.getHc()) ? "" : bvo.getHc());
            quarvo.setHc_id(bvo.getHc_id());
            xjqueryvo.add(quarvo);
        }

        //取每个季度的数据合计
        String bnqcstr = null;
        for (int i = 1; i <= Integer.parseInt(jd); i++) {
            int qcfirst = -1;
            for (int k = 1; k <= 3; k++) {
                int valuetemp = ((i - 1) * 3) + k;
                String period = year + "-" + (valuetemp < 10 ? "0" + valuetemp : valuetemp);
                if (periods.contains(period)) {
                    if (qcfirst == -1) {
                        qcfirst = k;
                    }
                    if (bnqcstr == null) {
                        bnqcstr = period;
                    }
                    XjllbVO[] bvos = xjperiodmap.get(period);
                    for (XjllbVO bvo : bvos) {
                        if (StringUtil.isEmpty(bvo.getHc())) {
                            bvo.setHc("");
                        }
                        for (XjllquarterlyVO quarvo : xjqueryvo) {
                            if (bvo.getXm().equals(quarvo.getXm()) && bvo.getHc().equals(quarvo.getHc())) {
                                if (bvo.getXm().equals("五、期初现金余额") || bvo.getXm().equals("　　加：期初现金及现金等价物余额")) {
                                    if (k == qcfirst) {
                                        quarvo.setJd(i, bvo.getBqje());
                                    }
                                    if (period.equals(bnqcstr)) {
                                        quarvo.setBnlj(bvo.getSqje());
                                    }
                                } else if (bvo.getXm().equals("六、期末现金余额") || bvo.getXm().equals("六、期末现金及现金等价物余额")) {
                                    if (k == 3) {
                                        quarvo.setJd(i, bvo.getBqje());
                                    }
                                } else {
                                    quarvo.setJd(i, SafeCompute.add((DZFDouble) quarvo.getJd(i), bvo.getBqje()));
                                }
                                if (!(bvo.getXm().equals("五、期初现金余额") || bvo.getXm().equals("　　加：期初现金及现金等价物余额"))) {
                                    quarvo.setBnlj(bvo.getSqje());
                                }
                            }
                        }
                    }
                }
            }
        }
        //非当前季度则赋值上一季度的值
        DZFDate begdate = cpvo.getBegindate();
        String qcjd = "";
        if (Integer.parseInt(year) == begdate.getYear() && begdate.getMonth() > 3) {//非第一季度
            if (begdate.getMonth() >= 10) {
                qcjd = "3";//取第三季度
            } else if (begdate.getMonth() >= 7) {
                qcjd = "2";//取第二季度
            } else if (begdate.getMonth() >= 4) {
                qcjd = "1";//取第一季度
            }
            DZFDouble qcjdvalue = DZFDouble.ZERO_DBL;
            DZFDouble begjdvalue;
            for (XjllquarterlyVO vo1 : xjqueryvo) {
                begjdvalue = DZFDouble.ZERO_DBL;
                if (!vo1.getXm().equals("五、期初现金余额") &&
                        !vo1.getXm().equals("　　加：期初现金及现金等价物余额")
                        && !vo1.getXm().equals("六、期末现金余额") &&
                        !vo1.getXm().equals("六、期末现金及现金等价物余额")) {
                    for (int i = Integer.parseInt(qcjd) + 1; i <= 4; i++) {
                        begjdvalue = SafeCompute.add(begjdvalue, (DZFDouble) vo1.getJd(i));
                    }
                    qcjdvalue = SafeCompute.sub(vo1.getBnlj(), begjdvalue);
                    vo1.setJd(Integer.parseInt(qcjd), qcjdvalue);
                }
            }
        }
        return xjqueryvo;
    }


    private Map<String, List<FseJyeVO>> getFsYjMap(XjllbQuarterlyQueryVO paramvo, DZFDate begdate, DZFDate endate, List<String> periods) throws Exception {

        Object[] objs = fsYeService.getEveryPeriodFsJyeVOs(begdate, endate,
                paramvo.getPk_corp(), null, "", DZFBoolean.FALSE);

        Map<String, List<FseJyeVO>> fsmap = (Map<String, List<FseJyeVO>>) objs[0];

        Map<String, List<FseJyeVO>> fsmapres = new HashMap<String, List<FseJyeVO>>();

        List<FseJyeVO> periodfs = null;

        ZcFzBVO[] zcfzbvos = null;

        for (String period : periods) {
            periodfs = fsmap.get(period);

            if (periodfs == null) {
                continue;
            }

            // 从年初开始取
            FseJyeVO[] restemp = fsYeService.getBetweenPeriodFs(
                    DateUtils.getPeriodStartDate(period.substring(0, 4) + "-01"), new DZFDate(period + "-01"), fsmap);

            fsmapres.put(period, Arrays.asList(restemp));
        }

        return fsmapres;
    }

    /**
     * 获取现金流量的发生数据
     *
     * @param begdate
     * @param endate
     * @param periods
     * @param paramvo
     */
    private Map<String, Map<Float, XjllbVO>> getXjllMapBvos(DZFDate begdate, DZFDate endate, List<String> periods, XjllbQuarterlyQueryVO paramvo) {

        return null;
    }

}
