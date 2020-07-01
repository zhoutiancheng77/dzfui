package com.dzf.zxkj.report.service.jcsz.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.*;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.SalaryReportEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.icset.*;
import com.dzf.zxkj.platform.model.jzcl.QmLossesVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherPrintParam;
import com.dzf.zxkj.platform.model.qcset.QcYeCurrency;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.model.sys.*;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.zcgl.ValuemodifyVO;
import com.dzf.zxkj.report.service.cwbb.ILrbQuarterlyReport;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.service.jcsz.*;
import com.dzf.zxkj.report.utils.*;
import com.dzf.zxkj.secret.CorpSecretUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JcszServiceImpl implements IJcszService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IFsYeReport fsYeReport;

    @Autowired
    private IZcFzBReport zcFzBReport;

    @Autowired
    private ILrbReport lrbReport;

    @Autowired
    private IBDCurrencyService bdCurrencyService;

    @Autowired
    private IAuxiliaryAccountService auxiliaryAccountService;

    @Autowired
    private ICorpService corpService;

    @Override
    public IncomeWarningVO[] queryIncomeWarningVOs(String pk_corp) {
        SQLParameter params = new SQLParameter();
        params.addParam(pk_corp);
        params.addParam(IDefaultValue.DefaultGroup);
        StringBuffer sql = new StringBuffer();
        sql.append(" select yi.* from ynt_IncomeWarning yi ");
        sql.append(" where nvl(yi.dr,0)=0 and (yi.pk_corp = ? or yi.pk_corp = ?)");
        List<IncomeWarningVO> listVO = (List<IncomeWarningVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(IncomeWarningVO.class));

        params.clearParams();
        params.addParam(pk_corp);
        IncomeHistoryVo[] his = (IncomeHistoryVo[]) singleObjectBO.queryByCondition(IncomeHistoryVo.class,
                "pk_corp = ? and nvl(dr, 0) = 0", params);

        Map<String, List<IncomeHistoryVo>> incomeHistoryVoGroupByPkSryj = Arrays.asList(his).stream().collect(Collectors.groupingBy(IncomeHistoryVo::getPk_sryj));

        if (listVO != null && listVO.size() > 0) {
            Map<String, YntCpaccountVO> accountMap = accountService.queryMapByPk(pk_corp);
            for (IncomeWarningVO incomeWarningVO : listVO) {
                String pk_subj = incomeWarningVO.getPk_accsubj();
                if (pk_subj != null && (pk_subj.length() == 24 || pk_subj.indexOf(",") > 23)) {
                    String[] subjArray = pk_subj.split(",");
                    StringBuilder kmbm = new StringBuilder();
                    StringBuilder kmmc = new StringBuilder();
                    for (String subj : subjArray) {
                        YntCpaccountVO account = accountMap.get(subj);
                        if (account != null) {
                            if (kmbm.length() != 0) {
                                kmbm.append(",");
                            }
                            kmbm.append(account.getAccountcode());
                            if (kmmc.length() != 0) {
                                kmmc.append(",");
                            }
                            kmmc.append(account.getAccountname());
                        }
                    }
                    if (kmmc.length() > 0) {
                        incomeWarningVO.setKm(kmmc.toString());
                    }
                    if (kmbm.length() > 0) {
                        incomeWarningVO.setKmbm(kmbm.toString());
                    }
                }
                if (incomeWarningVO.getHas_history() != null && incomeWarningVO.getHas_history().booleanValue()) {
                    List<IncomeHistoryVo> incomeHistoryList = incomeHistoryVoGroupByPkSryj.get(incomeWarningVO.getPk_sryj());
                    Optional.ofNullable(incomeHistoryList).ifPresent(list -> {
                        incomeWarningVO.setChildren(list.stream().toArray(IncomeHistoryVo[]::new));
                    });
                }
            }

            listVO = specPacking(listVO, pk_corp, null, null);
        }
        return listVO.toArray(new IncomeWarningVO[listVO.size()]);
    }


    private List<IncomeWarningVO> specPacking(List<IncomeWarningVO> list, String pk_corp, String filflg, CorpTaxVo ctvo) {
        List<IncomeWarningVO> newlist = new ArrayList<>();
        if ("Y".equals(filflg)) {
            for (IncomeWarningVO vo : list) {
                if (vo.getSpeflg() != null && vo.getSpeflg().booleanValue()) {
                    continue;
                }
                newlist.add(vo);
            }
        } else {
            if (ctvo == null) {
                ctvo = this.queryCorpTaxVO(pk_corp);
            }
            String yhzc = ctvo.getVyhzc();
            if (!"0".equals(yhzc)) {//判断  如果不是
                for (IncomeWarningVO vo : list) {
                    if (IDefaultValue.DefaultGroup.equals(vo.getPk_corp())) {
                        continue;
                    }
                    newlist.add(vo);
                }
            } else {
                Map<String, IncomeWarningVO> map = new HashMap<String, IncomeWarningVO>();
                String name;
                for (IncomeWarningVO vo : list) {
                    name = vo.getXmmc();
                    if (map.containsKey(name)) {
                        if (IDefaultValue.DefaultGroup.equals(vo.getPk_corp())) {
                            continue;
                        }
                    }

                    map.put(name, vo);
                }

                for (Map.Entry<String, IncomeWarningVO> entry : map.entrySet()) {
                    newlist.add(entry.getValue());
                }
            }
        }

        VOUtil.ascSort(newlist, new String[]{"ts"});

        return newlist;
    }


    private DZFDouble getZcValueByMc(ZcFzBVO[] zcvos, String mc) {
        DZFDouble result = null;
        if (zcvos == null || zcvos.length == 0) {
            return result;
        }

        for (ZcFzBVO zcvo : zcvos) {
            if (mc.equals(zcvo.getZc())) {
                result = zcvo.getQmye1();
                break;
            } else if (mc.equals(zcvo.getFzhsyzqy())) {
                result = zcvo.getQmye2();
                break;
            }
        }

        return result;
    }


    private DZFDouble getLrValueByMc(LrbVO[] lrbvos, String mc) {
        DZFDouble result = null;
        if (lrbvos == null || lrbvos.length == 0) {
            return result;
        }

        for (LrbVO lrvo : lrbvos) {
            if (mc.equals(lrvo.getXm())) {
                result = lrvo.getBnljje();
                break;
            }
        }

        return result;
    }

    private DZFDouble getSpecFsValue(String beginPeriod, String endPeriod,
                                     String pk_corp, IncomeWarningVO vo) throws DZFWarpException {
        CorpVO corpvo = corpService.queryCorpByPk(pk_corp);
        DZFDate begindate = corpvo.getBegindate();//建账日期
        String corptype = corpvo.getCorptype();
        if (!(TaxRptConst.KJQJ_2013.equals(corptype)
                || TaxRptConst.KJQJ_2007.equals(corptype)
                || TaxRptConst.KJQJ_QYKJZD.equals(corptype))) {
            return null;
        }

        //开始取数
        DZFDouble result = null;
        try {
            if (IIncomeWarningConstants.QYZCZE.equals(vo.getXmmc())) {

                beginPeriod = DateUtils.getPreviousPeriod(beginPeriod);
                boolean flag = false;
                DZFDouble bf = null;
                DZFDouble ef = null;
                ZcFzBVO[] zfbvos = null;
                if (beginPeriod.compareTo(DateUtils.getPeriod(begindate)) >= 0) {
                    zfbvos = zcFzBReport.getZCFZBVOsConXmids(beginPeriod, pk_corp, "N",
                            new String[]{"N", "N", "N", "N", "N"}, null);
                    bf = getZcValueByMc(zfbvos, "资产总计");
                    flag = true;
                }

                zfbvos = zcFzBReport.getZCFZBVOsConXmids(endPeriod, pk_corp, "N",
                        new String[]{"N", "N", "N", "N", "N"}, null);
                ef = getZcValueByMc(zfbvos, "资产总计");

                result = SafeCompute.div(SafeCompute.add(bf, ef), flag ? new DZFDouble(2) : new DZFDouble(1));
            } else if (IIncomeWarningConstants.QYNDNS.equals(vo.getXmmc())) {
                QueryParamVO paramVO = new QueryParamVO();
                paramVO.setPk_corp(pk_corp);
                paramVO.setIshasjz(new DZFBoolean("N"));//
                DZFDate beginDate = new DZFDate(endPeriod + "-01");
                DZFDate enddate = new DZFDate(endPeriod + "-" + beginDate.getDaysMonth());
                paramVO.setBegindate1(beginDate);
                paramVO.setEnddate(enddate);
                paramVO.setQjq(endPeriod);
                paramVO.setQjz(endPeriod);
                LrbVO[] lrbvos = lrbReport.getLRBVOsConXm(paramVO, null);

                CorpTaxVo ctvo = this.queryCorpTaxVO(pk_corp);
                Integer type = ctvo.getTaxlevytype();
                if (type == null || type == TaxRptConstPub.TAXLEVYTYPE_CZZS) {
                    result = getLrValueByMc(lrbvos, "三、利润总额（亏损总额以“-”号填列）");
                } else {
                    DZFDouble bf = getLrValueByMc(lrbvos, "一、营业收入");
                    DZFDouble ef = getLrValueByMc(lrbvos, "加：营业外收入");
                    DZFDouble rate = ctvo.getIncometaxrate();//应纳税所得率
                    result = SafeCompute.multiply(SafeCompute.add(bf, ef), SafeCompute.div(rate, new DZFDouble(100)));
                }

            }
        } catch (Exception e) {
            log.error("错误", e);
        }

        return result;
    }

    @Override
    public IncomeWarningVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp, String enddate) {
        if (ivos == null || ivos.length == 0) {
            return null;
        }

        Map<Integer, List<IncomeWarningVO>> periodMap = new HashMap<Integer, List<IncomeWarningVO>>();
        for (IncomeWarningVO ivo : ivos) {
            Integer period_type = ivo.getPeriod_type() == null ? 3 : ivo.getPeriod_type();
            List<IncomeWarningVO> list = periodMap.get(period_type);
            if (list == null) {
                list = new ArrayList<IncomeWarningVO>();
                periodMap.put(period_type, list);
            }
            list.add(ivo);
        }

        CorpVO corpvo = corpService.queryCorpByPk(pk_corp);
        DZFDate jzdate = corpvo.getBegindate();
        for (Map.Entry<Integer, List<IncomeWarningVO>> entry : periodMap.entrySet()) {
            DZFDate endDate = new DZFDate(enddate);
            DZFDate beginDate = null;
            Integer period_type = entry.getKey();
            List<IncomeWarningVO> warningList = entry.getValue();
            if (period_type == 0) {
                beginDate = new DZFDate(DateUtils.getPeriod(endDate) + "-01");
            } else if (period_type == 1) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate.toDate());
                int mon = calendar.get(Calendar.MONTH);
                int quarter = mon / 3 + 1;
                int endMon = 3 * quarter - 1;
                int beginMon = endMon - 2;
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, beginMon);
                beginDate = new DZFDate(calendar.getTime());
                calendar.set(Calendar.MONTH, endMon);
                endDate = new DZFDate(calendar.getTime());
            } else if (period_type == 2) {
                int year = endDate.getYear();
                beginDate = new DZFDate(year + "-01-01");
                endDate = new DZFDate(year + "-12-31");
            } else if (period_type == 3) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate.toDate());
                calendar.add(Calendar.MONTH, -11);
                beginDate = new DZFDate(calendar.getTime());
            }
            String beginPeriod = DateUtils.getPeriod(beginDate);
            String endPeriod = DateUtils.getPeriod(endDate);

            // 开始日期在建账日期前则取建账日期为开始日期
            if (beginDate.before(jzdate)) {
                beginDate = jzdate;
            }

            List<String> kmList = new ArrayList<String>();
            for (IncomeWarningVO vo : warningList) {
                if (!StringUtil.isEmpty(vo.getKmbm())) {
                    kmList.addAll(Arrays.asList(vo.getKmbm().split(",")));
                }
            }

            FseJyeVO[] fsVos = null;
            if (kmList.size() > 0) {
                QueryParamVO pramavo = new ReportUtil().getFseQueryParamVO(corpvo, beginDate,
                        endDate, kmList.toArray(new String[0]), true);
                fsVos = fsYeReport.getFsJyeVOs(pramavo, 1);
            }

            for (IncomeWarningVO ivo : warningList) {
                DZFDouble fsTotal = null;
                if (ivo.getSpeflg() != null && ivo.getSpeflg().booleanValue()) {
                    fsTotal = getSpecFsValue(beginPeriod, endPeriod, pk_corp, ivo);
                } else if (fsVos != null) {
                    for (FseJyeVO fseJyeVO : fsVos) {
                        if (!StringUtil.isEmpty(ivo.getKmbm())
                                && ivo.getKmbm().matches("(^|.*,)" + fseJyeVO.getKmbm() + "($|,.*)")) {
                            fsTotal = SafeCompute.add(fsTotal, "借".equals(fseJyeVO.getFx()) ? fseJyeVO.getFsjf()
                                    : fseJyeVO.getFsdf());
                        }
                    }
                }
                if (fsTotal == null) {
                    fsTotal = DZFDouble.ZERO_DBL;
                }
                if (ivo.getHas_history() != null
                        && ivo.getHas_history().booleanValue()) {
                    IncomeHistoryVo[] hisVos = (IncomeHistoryVo[]) ivo
                            .getChildren();
                    if (hisVos != null) {
                        DZFDouble his_occur = DZFDouble.ZERO_DBL;
                        for (IncomeHistoryVo incomeHistoryVo : hisVos) {
                            if (incomeHistoryVo.getPeriod().compareTo(beginPeriod) > -1
                                    && incomeHistoryVo.getPeriod().compareTo(
                                    endPeriod) < 1) {
                                his_occur = his_occur.add(incomeHistoryVo
                                        .getOccur_mny());
                            }
                        }
                        fsTotal = fsTotal.add(his_occur);
                    }
                }
                ivo.setFstotal(fsTotal);
                ivo.setInfonumber(new DZFDouble(ivo.getSrsx()).sub(fsTotal));
            }
        }
        return ivos;
    }

    @Override
    public String getCurrentCorpAccountSchema(String pk_corp) {
        CorpVO corpVO = corpService.queryCorpByPk(pk_corp);
        if (corpVO != null) {
            if (!StringUtil.isEmptyWithTrim(corpVO.getCorptype())) {
                return corpVO.getCorptype();
            }
        }
        return null;
    }

    //本年利润 利润分配     小企业会计准则/企业会计准则/企业会计制度
    private final String[] corpType = {"00000100AA10000000000BMD", "00000100AA10000000000BMF", "00000100000000Ig4yfE0005"};
    private final String[][] lrkm = {{"3103", "3104"}, {"4103", "4104"}, {"3131", "3141"}};
    private final String[][] srkm = {{"5001", "5051", "5111", "5301"}, {"6001", "6051", "6101", "6111", "6301"}, {"5101", "5102", "5201", "5203", "5301"}};
    private final String[][] fykm = {{"5401", "5402", "5403", "5601", "5602", "5603", "5711", "5801"}, {"6401", "6402", "6403", "6601", "6602", "6603", "6701", "6711", "6801"}, {"5401", "5402", "5405", "5501", "5502", "5503", "5601", "5701"}};


    @Override
    public SsphRes qcyeSsph(String pk_corp) {


        List<QcYeVO> qcvos = queryAllQcInfo(pk_corp, DZFConstant.ZHBWB, null);

        CorpVO cpvo = corpService.queryCorpByPk(pk_corp);

        if (qcvos == null || qcvos.size() == 0) {
            return new SsphRes();
        }
        DZFDouble z1 = null;
        DZFDouble z2 = null;
        DZFDouble z3 = null;
        DZFDouble z4 = null;
        //“本年利润+利润分配”本年累计=净利润本年累计
        DZFDouble z5 = new DZFDouble(0);
        DZFDouble z6 = new DZFDouble(0);
        int positon = searchIndexForArray(corpType, cpvo.getCorptype());

        for (QcYeVO c : qcvos) {
//			if(c.getVlevel() != null && c.getVlevel()==1){
//			if (c.getIsleaf() != null && c.getIsleaf().booleanValue()) {
            if (c.getVcode() != null && c.getVcode().length() == 4) {//代表一级
                //统计末级的时候，存在进项税的问题。(应收税费一级为贷方，进项税为3级为借方)
                if (c.getDirect() == 0) {// 借方
                    z1 = SafeCompute.add(c.getYearqc(), z1);
                    z2 = SafeCompute.add(c.getThismonthqc(), z2);
                } else {
                    z3 = SafeCompute.add(c.getYearqc(), z3);
                    z4 = SafeCompute.add(c.getThismonthqc(), z4);
                }
            }

            if (positon != -1) {
                //本年利润+利润分配”本年累计=【本年利润】（本年贷方发生额-本年借方发生
                //额）+【利润分配】（本年贷方发生金额-本年借方发生金额）
                if (searchIndexForArray(lrkm[positon], c.getVcode()) >= 0) {
                    z5 = SafeCompute.add(SafeCompute.sub(c.getYeardffse(), c.getYearjffse()), z5);
                }
//				净利润本年累计=【收入利得科目】本年贷方发生额-【费用损失科目】本年借方发
//						生额
                //加收入  一级科目
                if (c.getVcode().length() == 4 && c.getAccountkind() == 5) {

                    if ((cpvo.getCorptype().equals("00000100AA10000000000BMF") && c.getVcode().startsWith("6901")) || (cpvo.getCorptype().equals("00000100000000Ig4yfE0005") && c.getVcode().startsWith("5801"))) {
                        continue;
                    }

                    if (c.getDirect() == 0) { //借方
                        z6 = SafeCompute.sub(z6, c.getYearjffse());
                    } else {
                        z6 = SafeCompute.add(c.getYeardffse(), z6);
                    }
                }
            }
        }
        return setResult(z1, z2, z3, z4, z5, z6);

    }

    public SsphRes setResult(DZFDouble z1, DZFDouble z2, DZFDouble z3,
                             DZFDouble z4, DZFDouble z5, DZFDouble z6) {
        SsphRes ss = new SsphRes();
        DZFDouble ce = SafeCompute.sub(z1.setScale(2, DZFDouble.ROUND_HALF_UP), z3.setScale(2, DZFDouble.ROUND_HALF_UP));
        DZFDouble ce1 = SafeCompute.sub(z2.setScale(2, DZFDouble.ROUND_HALF_UP), z4.setScale(2, DZFDouble.ROUND_HALF_UP));
        ss.setYearjf(z1);
        ss.setYeardf(z3);
        ss.setYearce(ce);
        ss.setYearres(ce.doubleValue() == 0 ? "平衡" : "不平衡");
        ss.setMonthjf(z2);
        ss.setMonthdf(z4);
        ss.setMonthce(ce1);
        ss.setMonthres(ce1.doubleValue() == 0 ? "平衡" : "不平衡");

        DZFDouble lr = SafeCompute.sub(z5.setScale(2, DZFDouble.ROUND_HALF_UP), z6.setScale(2, DZFDouble.ROUND_HALF_UP));
        ss.setYearlr(z5);
        ss.setYearlrlj(z6);
        ss.setYearlrce(lr);
        ss.setYearlrres(lr.doubleValue() == 0 ? "平衡" : "不平衡");
        return ss;
    }

    private int searchIndexForArray(String[] arr, String obj) {
        int result = -1;

        for (int i = 0; i < arr.length; i++) {
            if (obj.equals(arr[i])) {
                result = i;
                break;
            }
        }

        return result;
    }


    //此方法增加了重载 多一个参数查询是否停用，修改业务注意同步
    private List<QcYeVO> queryAllQcInfo(String pk_corp, String pk_currence,
                                        String rmb) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(pk_corp);
        StringBuffer sf = new StringBuffer();
        sf.append(" select ynt_qcye.pk_qcye,acc.pk_corp,ynt_qcye.coperatorid,ynt_qcye.doperatedate,acc.isfzhs,acc.isverification,");
        sf.append(" acc.pk_corp_account pk_accsubj,ynt_qcye.pk_currency,acc.accountcode vcode,acc.accountname vname,acc.accountkind, ");
        sf.append("  acc.direction direct,acc.accountlevel vlevel,ynt_qcye.thismonthqc,ynt_qcye.yearjffse,  ");
        sf.append("  ynt_qcye.yeardffse,ynt_qcye.yearqc,ynt_qcye.memo,ynt_qcye.ybyearjffse,  ");
        sf.append("  ynt_qcye.ybyeardffse,ynt_qcye.ybyearqc,ynt_qcye.ybthismonthqc ,acc.isleaf,acc.exc_pk_currency , ");
        sf.append("  ynt_qcye.bnqcnum bnqcnum,ynt_qcye.bnfsnum bnfsnum,ynt_qcye.bndffsnum bndffsnum,ynt_qcye.monthqmnum monthqmnum ,acc.measurename jldw ,acc.isnum isnum  ");
        sf.append("  from    ynt_cpaccount acc ");
        sf.append("  left join ynt_qcye  on acc.pk_corp_account = ynt_qcye.pk_accsubj ");
        sf.append("  and nvl(ynt_qcye.dr,0) = 0 and ynt_qcye.pk_corp = ?  ");
        if (!DZFConstant.ZHBWB.equals(pk_currence)) {
            if (rmb.equals(pk_currence)) {// 人民币
                sf.append(" and nvl(ynt_qcye.pk_currency,'" + pk_currence
                        + "') = '" + pk_currence + "'  ");
            } else {
                sf.append(" and ynt_qcye.pk_currency = '" + pk_currence + "'  ");
            }
        }
        sf.append("  where nvl(acc.dr,0) = 0 and acc.pk_corp = ? order by acc.accountcode ");
        List<QcYeVO> list = (List<QcYeVO>) singleObjectBO.executeQuery(
                sf.toString(), sp, new BeanListProcessor(QcYeVO.class));
        return list;
    }

    @Override
    public DZFDouble getTaxValue(CorpVO cpvo, String rptname, String period, int[][] zbs) {
        return null;
    }

    @Override
    public BdCurrencyVO queryCurrencyVOByPk(String pk_currency) {
        if(StringUtil.isEmpty(pk_currency))
            return null;
        BdCurrencyVO vo = (BdCurrencyVO)singleObjectBO.queryByPrimaryKey(BdCurrencyVO.class, pk_currency);
        return vo;
    }

    private List<YntCpaccountVO> queryCurrence(SQLParameter sp)
            throws DZFWarpException {
        StringBuffer sf = new StringBuffer();
        sf.append(" select exc_pk_currency from ynt_cpaccount where pk_corp = ? and  exc_pk_currency is not null and nvl(dr,0) = 0  ");
        List<YntCpaccountVO> list = (List<YntCpaccountVO>) singleObjectBO
                .executeQuery(sf.toString(), sp, new BeanListProcessor(
                        YntCpaccountVO.class));
        return list;
    }

    public QcYeCurrency getZHbwb() {
        QcYeCurrency vo = new QcYeCurrency();
        vo.setPk_currency(DZFConstant.ZHBWB);
        vo.setCurrencyname("综合本位币");
        vo.setConvmode(0);
        vo.setExrate(new DZFDouble(1));
        return vo;
    }

    public QcYeCurrency getRMB() {
        QcYeCurrency vo = new QcYeCurrency();
        vo.setPk_currency(IGlobalConstants.RMB_currency_id);
        vo.setCurrencyname("人民币");
        vo.setConvmode(0);
        vo.setExrate(new DZFDouble(1));
        return vo;
    }

    @Override
    public QcYeCurrency[] queryCurrencyByPkCorp(String pk_corp) {

        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        Map<String, String> map = new HashMap<String, String>();
        List<YntCpaccountVO> list1 = queryCurrence(sp);
        if (list1 != null && list1.size() > 0) {
            for (YntCpaccountVO c : list1) {
                String pks = c.getExc_pk_currency();
                String[] rows = pks.split(",");
                for (String s : rows) {
                    map.put(s, s);
                }
            }
        }// 得到币种主键
        List<QcYeCurrency> zlist = new ArrayList<QcYeCurrency>();
        zlist.add(getZHbwb());
        zlist.add(getRMB());
        String[] pks = map.values().toArray(new String[0]);
        if (pks != null && pks.length > 0) {
            BdCurrencyVO[] currencyVOS = bdCurrencyService.queryCurrency();
            Map<String, BdCurrencyVO> currencyVOMap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(currencyVOS),
                    new String[]{"pk_currency"});
            Map<String, QcYeCurrency> maps = queryQcYeCurrency(sp, pks);
            for (String pk : pks) {
                QcYeCurrency v = maps.get(pk);
                if (v == null) {
                    QcYeCurrency vo = new QcYeCurrency();
                    vo.setPk_currency(pk);
                    if (currencyVOMap.containsKey(pk)) {
                        vo.setCurrencyname(currencyVOMap.get(pk).getCurrencyname());
                    }
                    vo.setConvmode(0);
                    vo.setExrate(new DZFDouble(1));
                    zlist.add(vo);
                } else {
                    zlist.add(v);
                }
            }
        }
        return qcyeCommon.outRepeat(zlist);

    }

    private QcYeCommon qcyeCommon = new QcYeCommon();

    public Map<String, QcYeCurrency> queryQcYeCurrency(SQLParameter sp,
                                                       String[] args) {
        String where = qcyeCommon.getInWhereClauseVO(args);
        Map<String, QcYeCurrency> map = null;
        if (where != null && where.length() > 0) {
            StringBuffer sf = new StringBuffer();
            sf.append(" select t1.pk_currency,t1.currencyname,t2.exrate,t2.convmode from ynt_bd_currency t1 ");
            sf.append(" join ynt_exrate t2 on t1.pk_currency = t2.pk_currency ");
            sf.append(" where t2.pk_corp = ? and nvl(t1.dr,0)= 0 and nvl(t2.dr,0)=0 ");
            sf.append(" and t1.pk_currency  " + where);
            List<QcYeCurrency> list = (List<QcYeCurrency>) singleObjectBO
                    .executeQuery(sf.toString(), sp, new BeanListProcessor(
                            QcYeCurrency.class));
            map = qcyeCommon.hashlizeObject(list);
        }
        return map;
    }

    @Override
    public List<InventoryVO> queryInventoryVOs(String pk_corp) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        StringBuffer sf = new StringBuffer();
        sf.append(" select  ry.*,fy.name invclassname,re.name measurename,nt.accountname kmname ,"
                + " nt.accountcode kmcode,ry.pk_subject from ynt_inventory  ry  ");
        sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
        sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
        sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
        sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? ");

        List<InventoryVO> ancevos = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(InventoryVO.class));
        if (ancevos == null || ancevos.size() == 0)
            return null;

        ancevos.sort(Comparator.comparing(InventoryVO::getCode,Comparator.nullsFirst(LetterNumberSortUtil.letterNumberOrder())));
        return ancevos;
    }

    private List<YntParameterSet> queryParamters(String pk_corp, String parameterbm) throws DZFWarpException {

        List<YntParameterSet> parameterSetList = queryParamters(pk_corp);

        if (!StringUtil.isEmpty(parameterbm)) {
            return ObjectUtils.notEmpty(parameterSetList) ? parameterSetList.stream().filter(v -> parameterbm.equalsIgnoreCase(v.getParameterbm())).collect(Collectors.toList()) : new ArrayList<YntParameterSet>();
        }
        return parameterSetList;
    }

    private List<YntParameterSet> queryParamters(String pk_corp) throws DZFWarpException {
        String where = " select pk_corp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior fathercorp and nvl(dr,0) = 0 ";
        StringBuffer sf = new StringBuffer();
        sf.append("select * from ynt_parameter where nvl(dr,0) = 0 and pk_corp in(");
        sf.append(where);
        sf.append(")  ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        return (List<YntParameterSet>)
                singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(YntParameterSet.class));
    }

    @Override
    public YntParameterSet queryParamterbyCode(String pk_corp, String paramcode) {

        if (StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(paramcode)) {
            throw new BusinessException("参数为空，请确认参数!");
        }
        YntParameterSet set = null;
        List<YntParameterSet> ancevos = queryParamters(pk_corp, paramcode);
        List<String> lista = querycorpsByorder(pk_corp);
        if (ancevos == null || ancevos.size() == 0)
            return set;
        List<YntParameterSet> z1 = setGroup(ancevos, lista);
        if (z1 != null && z1.size() > 0) {
            set = z1.get(0);
            set.setPk_corp(pk_corp);
            set.setPk_parameter(null);
        }
        return set;
    }


    private List<YntParameterSet> setGroup(List<YntParameterSet> ancevos, List<String> lista) {
        Map<String, List<YntParameterSet>> map = (Map<String, List<YntParameterSet>>)
                DZfcommonTools.hashlizeObject(ancevos, new String[]{"parameterbm"});
        Iterator<String> it = map.keySet().iterator();
        List<YntParameterSet> z1 = new ArrayList<YntParameterSet>();
//		sortParameter sort = new sortParameter();
        while (it.hasNext()) {
            String key = it.next();
            List<YntParameterSet> z = map.get(key);
            if (z != null && z.size() > 0) {
                //排序，取pk_corp 最大的
//				Collections.sort(z,sort);
                YntParameterSet set = getYntParameterSet(z, lista);
                if (set != null) {
                    z1.add(set);
                }
            }
        }
        return z1;
    }


    private YntParameterSet getYntParameterSet(List<YntParameterSet> z1, List<String> lista) {
        if (z1 == null || z1.size() == 0) {
            return null;
        }
        YntParameterSet set = null;
        for (String s : lista) {
            for (YntParameterSet s1 : z1) {
                if (s.equals(s1.getPk_corp())) {
                    set = s1;
                    break;
                }
            }
            if (set != null) {
                break;
            }
        }
        return set;
    }


    private List<String> querycorpsByorder(String pk_corp) throws DZFWarpException {
        String sql = " select pk_corp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior fathercorp and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<String> ancevos = (List<String>)
                singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor() {

                    @Override
                    public Object handleResultSet(ResultSet resultset) throws SQLException {
                        List<String> list = new ArrayList<String>();
                        while (resultset.next()) {
                            list.add(resultset.getString("pk_corp"));
                        }
                        return list;
                    }

                });
        return ancevos;
    }

    private List<YntParameterSet> queryParamter(String pk_corp) throws DZFWarpException {
        List<YntParameterSet> ancevos = queryParamters(pk_corp, "");
        List<String> lista = querycorpsByorder(pk_corp);
        if (ancevos == null || ancevos.size() == 0)
            return null;

        List<YntParameterSet> z1 = setGroup(ancevos, lista);
        return z1;
    }

    @Override
    public GxhszVO queryGxhszVOByPkCorp(String pk_corp) {
        GxhszVO gvo = new GxhszVO();
        List<YntParameterSet> list = queryParamter(pk_corp);
        Map<String, YntParameterSet> map = DZfcommonTools.hashlizeObjectByPk(
                list, new String[]{ "parameterbm" });
        //赋值
        String[][] fields = {
                {IParameterConstants.DZF015, "printType", "0"}, {IParameterConstants.DZF016, "subjectShow", "0"},
                {IParameterConstants.DZF017, "pzSubject", "2"}, {IParameterConstants.DZF018, "balanceShow", "0"},
                {IParameterConstants.DZF019, "subject_num_show", "1"}, {IParameterConstants.DZF020, "isshowlastmodifytime", "1"},
                {IParameterConstants.DZF021, "yjqp_gen_vch", "1"},
        };

        YntParameterSet setvo;
        Integer value = null;
        for(String[] arr : fields){
            setvo = map.get(arr[0]);
            if(setvo != null){
                value = setvo.getPardetailvalue();
            }

            if(value == null){
                value = Integer.parseInt(arr[2]);
            }

            gvo.setAttributeValue(arr[1], value);
        }

        return gvo;
    }

    @Override
    public CorpTaxVo queryCorpTaxVO(String pk_corp) {
        CorpTaxVo vo = new CorpTaxVo();
        vo.setPk_corp(pk_corp);

        if (StringUtil.isEmpty(pk_corp))
            return vo;
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        CorpTaxVo[] bodyvos = (CorpTaxVo[]) singleObjectBO.queryByCondition(CorpTaxVo.class, " pk_corp = ? and nvl(dr,0) = 0  ", sp);
        if (bodyvos != null && bodyvos.length > 0) {
            vo = bodyvos[0];
        }
        setCorpTaxDefaultValue(vo, pk_corp);
        return vo;
    }


    //默认值
    private void setCorpTaxDefaultValue(CorpTaxVo vo, String pk_corp) {
        CorpVO corpVO = (CorpVO) singleObjectBO.queryVOByID(pk_corp,
                CorpVO.class);

        if (corpVO == null)
            return;

        String yhzc = vo.getVyhzc();//优惠政策
        if (StringUtil.isEmpty(yhzc)) {
            vo.setVyhzc("0");//默认为小型微利
        }

        DZFDouble citytax = vo.getCitybuildtax();//城建税
        if (citytax == null) {
            vo.setCitybuildtax(new DZFDouble(0.07));
        }
        DZFDouble localtax = vo.getLocaleducaddtax();//地方教育附加费
        if (localtax == null) {
            vo.setLocaleducaddtax(new DZFDouble(0.02));
        }
//		DZFDouble educaddtax = vo.getEducaddtax();//教育费附加
//		if(educaddtax == null){
//			vo.setEducaddtax(new DZFDouble(0.03));
//		}

        String corptype = corpVO.getCorptype();
        if (!"00000100AA10000000000BMD".equals(corptype)) {//不是科目方案13不设默认值
            return;
        }

        Integer intype = vo.getIncomtaxtype();
        if (intype == null) {
            Integer comtype = corpVO.getIcompanytype();
            if (comtype == null ||
                    (comtype != 2 && comtype != 20 && comtype != 21)) {
                vo.setIncomtaxtype(TaxRptConstPub.INCOMTAXTYPE_QY);
            } else {
                vo.setIncomtaxtype(TaxRptConstPub.INCOMTAXTYPE_GR);
            }
        }

        Integer taxletype = vo.getTaxlevytype();
        if (taxletype == null) {
            vo.setTaxlevytype(TaxRptConstPub.TAXLEVYTYPE_CZZS);//查账征收
        }
    }

    @Override
    public Map<String, IcbalanceVO> queryLastBanlanceVOs_byMap1(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn) {
        String lastdate = getBalanceDate(pk_corp,currentenddate);
        Map<String,IcbalanceVO> map = queryLastBanlanceVOs_byMap2(lastdate, currentenddate, pk_corp,pk_invtory,isafternonn);
        return map;
    }


    private String getBalanceDate(String pk_corp,String currentenddate)throws DZFWarpException {
        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sp.addParam(currentenddate);
        sp.addParam(pk_corp);
        sf.append(" select max(dbilldate) dbilldate  from  ynt_icbalance where  dbilldate <= ? and pk_corp = ? and nvl(dr,0)=0 ");
        String dbilldate = (String)singleObjectBO.executeQuery(sf.toString(), sp, new ObjectProcessor());
        if(StringUtil.isEmptyWithTrim(dbilldate))
            dbilldate="1900-01-01";
        return dbilldate;
    }

    /**
     * 查询截止日期最新库存
     */
    private Map<String,IcbalanceVO> queryLastBanlanceVOs_byMap2(String lastdate,String currentenddate,String pk_corp,String pk_invtory,boolean isafternonn)throws  DZFWarpException{
        Map<String,IcbalanceVO> map = new java.util.HashMap<String, IcbalanceVO>();
        List<IcbalanceVO> ancevos =queryLastBanlanceVOs_byList2(lastdate,currentenddate,pk_corp,pk_invtory,null,isafternonn);
        if(ancevos != null && ancevos.size() > 0){
            for(IcbalanceVO v : ancevos){
                map.put(v.getPk_inventory(), v);
            }
        }
        return map;
    }


    private List<IcbalanceVO> queryLastBanlanceVOs_byList2(String lastdate,String currentenddate,String pk_corp,String pk_invtory,String pk_invclass,boolean isafternonn)throws  DZFWarpException{
        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sf.append(" select sum(nnum) nnum ,sum(ncost) ncost,sum(nymny) nymny,pk_inventory,inventorycode,inventoryname,invspec,invtype,measurename,inventorytype,pk_subjectname,pk_subjectcode from ( ");
        sf.append("  select yy.pk_inventory,yy.code inventorycode,yy.name inventoryname,yy.invspec,yy.invtype,ms.name measurename,cl.name inventorytype ,ct.accountname pk_subjectname,ct.accountcode pk_subjectcode,nnum,ncost,nymny from ynt_icbalance bal ");
        sf.append("   join ynt_inventory yy on bal.pk_inventory = yy.pk_inventory   ");
        sf.append(" left join ynt_measure ms on ms.pk_measure  = yy.pk_measure ");
        sf.append(" left join ynt_invclassify cl on cl.pk_invclassify =  yy.pk_invclassify ");
        sf.append(" left join ynt_cpaccount ct on ct.pk_corp_account = yy.pk_subject  ");
        sf.append("  where bal.dbilldate = ? and bal.pk_corp = ? and nvl(bal.dr,0)=0");
        sp.addParam(lastdate);
        sp.addParam(pk_corp);
        String wheInv = null;
        if (!StringUtil.isEmptyWithTrim(pk_invtory)) {
            String[] spris = pk_invtory.split(",");
            wheInv = SqlUtil.buildSqlConditionForIn(spris);
            sf.append(" and bal.pk_inventory in ( ");
            sf.append(wheInv);
            sf.append(" ) ");
        }
        String wheInvcl = null;
        if (!StringUtil.isEmptyWithTrim(pk_invclass)) {
            String[] spris =pk_invclass.split(",");
            wheInvcl = SqlUtil.buildSqlConditionForIn(spris);
            sf.append(" and cl.pk_invclassify in ( ");
            sf.append(wheInvcl);
            sf.append(" ) ");
        }

        sf.append("  union all ");
        sf.append("  (select yy.pk_inventory,yy.code inventorycode, yy.name inventoryname,yy.invspec,yy.invtype,ms.name measurename,cl.name inventorytype ,ct.accountname pk_subjectname,ct.accountcode pk_subjectcode,nnum,ncost,nymny from ynt_icbalance_view viw ");
        sf.append("   join ynt_inventory yy on viw.pk_inventory = yy.pk_inventory   ");
        sf.append(" left join ynt_measure ms on ms.pk_measure  = yy.pk_measure ");
        sf.append(" left join ynt_invclassify cl on cl.pk_invclassify =  yy.pk_invclassify ");
        sf.append(" left join ynt_cpaccount ct on ct.pk_corp_account = yy.pk_subject  ");
        if(isafternonn){//截止当天晚上
            sf.append("   where viw.dbilldate >= ? and viw.dbilldate <=? and viw.pk_corp =  ? ");
        }else{//截止当天早上
            sf.append("   where viw.dbilldate >= ? and viw.dbilldate < ? and viw.pk_corp = ? ");
        }
        sp.addParam(lastdate);
        sp.addParam(currentenddate);
        sp.addParam(pk_corp);

        if (!StringUtil.isEmptyWithTrim(wheInv)) {
            sf.append(" and viw.pk_inventory in ( ");
            sf.append(wheInv);
            sf.append(" ) ");
        }
        if (!StringUtil.isEmptyWithTrim(wheInvcl)) {
            sf.append(" and cl.pk_invclassify in ( ");
            sf.append(wheInvcl);
            sf.append(" ) ");
        }
        sf.append("  )) group by pk_inventory,inventorycode,inventoryname,invspec,invtype,measurename,inventorytype,pk_subjectname,pk_subjectcode ");
        List<IcbalanceVO> ancevos = (List<IcbalanceVO>)singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(IcbalanceVO.class));
        return ancevos;
    }

    @Override
    public String queryParamterValueByCode(String pk_corp, String paramcode) {
        YntParameterSet setvo = queryParamterbyCode(pk_corp, paramcode);

        String value = null;
        if (setvo != null) {
            String paravalue = setvo.getParametervalue();
            Integer detailvalue = setvo.getPardetailvalue();
            String[] arr = StringUtil.isEmpty(paravalue) ? null : paravalue.split(";");
            if (arr != null && arr.length > 0 && detailvalue != null) {
                value = arr[detailvalue];
            }
        }

        return value;
    }

    private YntCpaccountVO queryAccountByid(String pkid) {
        return (YntCpaccountVO) singleObjectBO.queryVOByID(pkid, YntCpaccountVO.class);
    }

    @Override
    public void checkQjsy(TzpzHVO headVO) {
        if (headVO.getIsqxsy() == null || !headVO.getIsqxsy().booleanValue()) {
            boolean hasProfitAndLoss = false;
            if (headVO.getChildren() == null) {
                hasProfitAndLoss = true;
            } else {
                // 已经经过期间损益的月份修改或新增凭证时，如果凭证不涉及损益类科目的话，不给提示：当期已经损益结转
                Map<String, YntCpaccountVO> accountMap = accountService.queryMapByPk(headVO.getPk_corp());
                TzpzBVO[] bvos = (TzpzBVO[]) headVO.getChildren();
                for (TzpzBVO tzpzBVO : bvos) {
                    YntCpaccountVO account = accountMap.get(tzpzBVO.getPk_accsubj());
                    if (account == null) {// [如果缓存不存在，按主键查询]
                        account = queryAccountByid(tzpzBVO.getPk_accsubj());
                    }
                    if (account != null && account.getAccountkind() == 5) {
                        hasProfitAndLoss = true;
                        break;
                    }
                }
            }

            if (hasProfitAndLoss) {
                StringBuilder sbCode = new StringBuilder(
                        "select count(1) from  ynt_qmcl where pk_corp=? and period=? and nvl(dr,0) = 0 and nvl(isqjsyjz,'N')='Y'");
                SQLParameter sp = new SQLParameter();
                sp.addParam(headVO.getPk_corp());
                headVO.setPeriod(DateUtils.getPeriod(headVO.getDoperatedate()));
                sp.addParam(headVO.getPeriod());
                BigDecimal repeatCodeNum = (BigDecimal) singleObjectBO.executeQuery(sbCode.toString(), sp,
                        new ColumnProcessor());

                if (repeatCodeNum.intValue() > 0) {
                    String errCodeStr = IVoucherConstants.EXE_RECONFM_CODE;// "-150"
                    throw new BusinessException(errCodeStr);
                }
            }
        }
    }

    @Override
    public UserVO queryUserById(String userId) {
        UserVO user = (UserVO)singleObjectBO.queryVOByID(userId, UserVO.class);
        user.setUser_name(CorpSecretUtil.deCode(user.getUser_name()));
        return user;
    }



    @Override
    public DZFDouble getQuarterlySdsShui(String pk_corp, String period) {

        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司不能为空！");
        }

        if (StringUtil.isEmpty(period)) {
            throw new BusinessException("期间不能为空！");
        }

        int month = new DZFDate(period + "-01").getMonth();

        DZFDouble shuikuan = DZFDouble.ZERO_DBL;
        // 查询利润表季报
        QueryParamVO paramVO = new QueryParamVO();
        paramVO.setPk_corp(pk_corp);
        paramVO.setIshasjz(new DZFBoolean("N")); // 此处含义是仅包含记账，跟客户端含义相反
        // 按季度查询是，开始日期也传递季度末月份的首日期
        DZFDate beginDate = new DZFDate(period + "-01");
        DZFDate enddate = new DZFDate(period + "-" + beginDate.getDaysMonth());
        paramVO.setBegindate1(beginDate);
        paramVO.setEnddate(enddate);
        paramVO.setQjq(period);
        paramVO.setQjz(period);
        LrbquarterlyVO[] vos = lrbQuarterlyReport.getLRBquarterlyVOs(paramVO);
        shuikuan = getQuarterlySdsShui1(pk_corp,period, vos);
        return shuikuan;

    }

    private QmLossesVO queryLossmny(DZFDate dateq, String copid) throws DZFWarpException {
        if (StringUtil.isEmpty(copid)) {
            throw new BusinessException("公司为空!");
        }
        if (dateq == null) {
            throw new BusinessException("期间为空!");
        }
        int year = dateq.getYear();
        String condition = " nvl(dr,0)=0 and pk_corp = ? and period =? ";
        SQLParameter params = new SQLParameter();
        params.addParam(copid);
        params.addParam(year);
        QmLossesVO[] vos = (QmLossesVO[]) singleObjectBO.queryByCondition(QmLossesVO.class, condition, params);
        QmLossesVO  vo= null;
        if(vos != null && vos.length>0){
            vo = vos[0];
        }else{
            vo = new QmLossesVO();
            vo.setPeriod(String.valueOf(year));
            vo.setPk_corp(copid);
            vo.setNlossmny(DZFDouble.ZERO_DBL);
            vo.setDr(0);
        }
        return vo;
    }

    private DZFDouble getQuarterlySdsShui1(String pk_corp, String period, LrbquarterlyVO[] vos) {
        DZFDouble shuikuan = DZFDouble.ZERO_DBL;
        int month = new DZFDate(period + "-01").getMonth();
        DZFDate beginDate = new DZFDate(period + "-01");
        LrbquarterlyVO qvo = null;
        LrbquarterlyVO sdsvo = null;
        if(vos !=null && vos.length > 0){
            for (LrbquarterlyVO vo : vos) {
                if ("三、利润总额（亏损总额以“-”填列）".equals(vo.getXm()) || "三、利润总额（亏损总额以“-”号填列）".equals(vo.getXm())
                        || "四、利润总额（亏损以“-”号填列）".equals(vo.getXm())) {
                    qvo = vo;
                } else if ("减：所得税费用".equals(vo.getXm())
                        || "减：所得税".equals(vo.getXm())) {
                    sdsvo = vo;
                }
            }
        }
        if (qvo == null)
            return shuikuan;
        // 计算总额(不包含期初的本年累计)
        DZFDouble first = qvo.getQuarterFirst();
        DZFDouble second = qvo.getQuarterSecond();
        DZFDouble third = qvo.getQuarterThird();
        DZFDouble fourth = qvo.getQuarterFourth();

        DZFDouble firstsds = DZFDouble.ZERO_DBL;
        DZFDouble secondsds = DZFDouble.ZERO_DBL;
        DZFDouble thirdsds = DZFDouble.ZERO_DBL;
        if (sdsvo != null) {
            firstsds = sdsvo.getQuarterFirst();
            secondsds = sdsvo.getQuarterSecond();
            thirdsds = sdsvo.getQuarterThird();
        }

        DZFDouble bnlj = qvo.getBnlj();
        DZFDouble nlossmny = DZFDouble.ZERO_DBL;
        // zpm start
        CorpVO pvo = corpService.queryCorpByPk(pk_corp);
        DZFDate jzdate = pvo.getBegindate();
        if (jzdate == null)
            return shuikuan;
        QmLossesVO lossvo = queryLossmny(beginDate, pk_corp);

        if (lossvo != null) {
            nlossmny = lossvo.getNlossmny();
            if (nlossmny == null)
                nlossmny = DZFDouble.ZERO_DBL;
        }

        // zpm end

        // 计算 3 6 9 12 月份的税额
        switch (month) {
            case 3: {
                if (first != null && first.compareTo(DZFDouble.ZERO_DBL) > 0) {
                    shuikuan = calcshuikuan2(period, bnlj, nlossmny);
                }
                break;
            }
            case 6: {
                if (second != null && second.compareTo(DZFDouble.ZERO_DBL) > 0) {
                    shuikuan = calcshuikuan2(period, bnlj, nlossmny);
                    shuikuan = SafeCompute.sub(shuikuan, firstsds);
                }
                break;
            }
            case 9: {
                if (third != null && third.compareTo(DZFDouble.ZERO_DBL) > 0) {
                    shuikuan = calcshuikuan2(period, bnlj, nlossmny);
                    shuikuan = SafeCompute.sub(shuikuan, SafeCompute.add(firstsds, secondsds));
                }
                break;
            }
            case 12: {
                if (fourth != null && fourth.compareTo(DZFDouble.ZERO_DBL) > 0) {
                    shuikuan = calcshuikuan2(period, bnlj, nlossmny);
                    shuikuan = SafeCompute.sub(shuikuan, SafeCompute.add(thirdsds, SafeCompute.add(firstsds, secondsds)));
                }
                break;
            }
            default:
                break;

        }
        return shuikuan;
    }


    private DZFDouble calcshuikuan2(String period, DZFDouble bnlj, DZFDouble nlossmny) {
        DZFDouble shuikuan1 = SafeCompute.sub(bnlj, nlossmny);
        if (shuikuan1.compareTo(DZFDouble.ZERO_DBL) < 0) {
            shuikuan1 = DZFDouble.ZERO_DBL;
        } else {
            if (period.compareTo("2019-01") < 0) {
                if (shuikuan1.compareTo(new DZFDouble(1000000)) <= 0) {
                    shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.1));
                    shuikuan1 = shuikuan1.setScale(2, DZFDouble.ROUND_HALF_UP);
                } else {
                    shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.25));
                    shuikuan1 = shuikuan1.setScale(2, DZFDouble.ROUND_HALF_UP);
                }
            } else {
                if (shuikuan1.compareTo(new DZFDouble(1000000)) <= 0) {
                    shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.05));
                } else if (shuikuan1.compareTo(new DZFDouble(3000000)) <= 0) {
                    shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.1));
                } else {
                    shuikuan1 = SafeCompute.multiply(shuikuan1, new DZFDouble(0.25));
                }
                shuikuan1 = shuikuan1.setScale(2, DZFDouble.ROUND_HALF_UP);
            }
        }
        if (shuikuan1.compareTo(DZFDouble.ZERO_DBL) < 0) {
            shuikuan1 = DZFDouble.ZERO_DBL;
        }
        return shuikuan1;
    }

    @Autowired
    private ILrbQuarterlyReport lrbQuarterlyReport;

    /**
     * 拼装我的客户查询信息
     *
     * @param queryvo
     * @return
     */
    private String getQuerySql(QueryParamVO queryvo, UserVO uservo) {
        // 根据查询条件查询公司的信息
        StringBuffer corpsql = new StringBuffer();
        corpsql.append("select b1.*,a.innercode,a.ischannel,a.begindate,a.createdate,a.vsuperaccount, ");
        corpsql.append(" a.pk_corp,a.vsoccrecode,a.isxrq,a.drdsj, ");
        corpsql.append(" a.legalbodycode,a.vcorporatephone, t.tradename as indusname,a.industry,a.unitname,a.chargedeptname, ");
        corpsql.append(" a.icostforwardstyle, a.bbuildic, a.ishasaccount, a.holdflag, a.busibegindate, a.icbegindate, a.corptype, ");
        corpsql.append(" a.vcustsource,a.vprovince,a.vcity,a.varea,a.vbankname,a.fathercorp,a.isseal,a.icompanytype, ");
        corpsql.append(" b.unitname as def1 ,");
        corpsql.append(" b.def3      		,");
        corpsql.append(" b.def2     		,");
        corpsql.append(" y.accname as ctypename      ,");
        corpsql.append(" t.tradename as indusname    ,");
        corpsql.append(" u.user_name as pcountname   ,");//
        corpsql.append(" us.user_name as wqcountname , ");//
        corpsql.append(" t.tradecode as vtradecode	");   //国家标准行业编码
        corpsql.append(" from bd_corp a ");
        corpsql.append(" left join bd_account b on a.fathercorp = b.pk_corp");
        corpsql.append(" left join bd_corp_tax b1 on a.pk_corp = b1.pk_corp ");
        corpsql.append(" left join ynt_tdaccschema y on a.corptype = y.pk_trade_accountschema");// 查询科目方案的名称
        corpsql.append(" left join ynt_bd_trade t on a.industry = t.pk_trade");
        corpsql.append(" left join sm_user u on a.vsuperaccount = u.cuserid");// 关联用户表--主管会计
        corpsql.append(" left join sm_user us on a.vwqaccount = us.cuserid");// 关联用户表--外勤会计
        if(!StringUtil.isEmpty(queryvo.getAsname())){
            corpsql.append(" join ynt_corpimpress cip on cip.pk_corp = a.pk_corp and cip.vname = '"+queryvo.getAsname()+"'");
        }
        // 添加权限过滤
//		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
//		corpsql.append(" where nvl(a.dr,0) =0 and a.fathercorp = ?");
        corpsql.append(" where nvl(a.dr,0) =0 ");

        if(queryvo.getIshassh()==null||!queryvo.getIshassh().booleanValue()){
            corpsql.append(" and nvl(a.isseal,'N') = 'N' ");
        }
        if(queryvo.getIshasjz() != null && queryvo.getIshasjz().booleanValue()){
            corpsql.append(" and nvl(a.ishasaccount,'N') = 'Y' ");
        }
        if(!StringUtil.isEmpty(queryvo.getCorpid())){
            corpsql.append(" and a.pk_corp = ? ");
        }
        if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
            corpsql.append(" and a.innercode like ? ");
        }
        if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
            if (queryvo.getBegindate1().compareTo(queryvo.getEnddate()) == 0) {
                corpsql.append(" and a.createdate = ? ");
            } else {
                corpsql.append(" and (a.createdate >= ? and a.createdate <= ? )");
            }
        }else if(queryvo.getBegindate1() != null){
            corpsql.append(" and a.createdate >= ? ");
        }else if(queryvo.getEnddate() != null){
            corpsql.append(" and a.createdate <= ? ");
        }
        if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
            if (queryvo.getBcreatedate().compareTo(queryvo.getEcreatedate()) == 0) {
                corpsql.append(" and a.begindate = ? ");
            } else {
                corpsql.append(" and (a.begindate >= ? and a.begindate <= ? )");
            }
        } else if (queryvo.getBcreatedate() != null) {// 建账日期只录入开始日期或结束日期
            corpsql.append(" and a.begindate >= ? ");
        } else if (queryvo.getEcreatedate() != null) {
            corpsql.append(" and a.begindate <= ? ");
        }
        corpsql.append(" and  nvl(a.isaccountcorp,'N') = 'N' ");
        if (queryvo.getXswyewfs() != null) {
            corpsql.append(" and nvl(a.ispersonal,'N') = ? ");
        }
        // 添加权限过滤
//		if (!ismanager.booleanValue()) {
        corpsql.append(" and (a.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
        corpsql.append(" or a.vsuperaccount = ? or a.coperatorid = ?)");
//		}

        if(!StringUtil.isEmpty(queryvo.getVprovince())){//报税地区
            corpsql.append(" and b1.tax_area=? ");
        }
        if(queryvo.getMaintainedtax() != null){
            if(queryvo.getMaintainedtax() == 2){
                corpsql.append(" and b1.ismaintainedtax = 'Y' ");
            }else if(queryvo.getMaintainedtax() == 3){
                corpsql.append(" and nvl(b1.ismaintainedtax,'N')='N' ");
            }
        }
        if(!StringUtil.isEmpty(queryvo.getKms_id())){//公司主键
            corpsql.append(" and a.pk_corp=? ");
        }
        //是否激活dzfAPP
        if(queryvo.getIsdzfapp() != null ){
            if(queryvo.getIsdzfapp() == 0){
                corpsql.append(" and exists (select * from ynt_corp_user where a.pk_corp = pk_corp and nvl(dr,0) = 0 and nvl(istate,2) = 2 and fathercorp = ?) ");
            }else if(queryvo.getIsdzfapp() == 1){
                corpsql.append(" and not exists (select * from ynt_corp_user where a.pk_corp = pk_corp and nvl(dr,0) = 0 and nvl(istate,2) = 2 and fathercorp = ?) ");
            }
        }
        if(queryvo.getIsywskp() != null){
            if(queryvo.getIsywskp() == 0){
                corpsql.append(" and nvl(a.isywskp,'N')= 'Y' ");
            }else if(queryvo.getIsywskp() == 1){
                corpsql.append(" and nvl(a.isywskp,'N')= 'N' ");
            }
        }
        if(queryvo.getIfwgs() != null && queryvo.getIfwgs() != -1){
            corpsql.append(" and nvl(a.ifwgs,0)= ? ");
        }
        if(queryvo.getIsformal() != null){
            corpsql.append(" and nvl(a.isformal,'N') = ?");
        }
        Integer type = queryvo.getLevelq() == null ? 0 : queryvo.getLevelq();
        if(type == 0){

        }else if(type == 1){//代账客户
            corpsql.append(" and nvl(a.ishasaccount,'N') = 'Y'");
        }else if(type == 2){//非代账客户
            corpsql.append(" and nvl(a.ishasaccount,'N') = 'N'");
        }else if(type == 3){//潜在客户
            corpsql.append(" and nvl(a.isformal,'N') = 'N'");
        }else if(type == 4){//一般纳税人
            corpsql.append(" and a.chargedeptname = '一般纳税人'");
        }else if(type == 5){//小规模纳税人
            corpsql.append(" and a.chargedeptname = '小规模纳税人'");
        }else if(type == 6){//未核定纳税人性质
            corpsql.append(" and a.chargedeptname is null");
        }else if(type == 7){//个人客户
            corpsql.append(" and nvl(a.ispersonal,'N') = 'Y'");
        }else if(type == 8){//待分配客户
            corpsql.append(" and (a.pk_corp not in (select pk_corp from sm_user_role where pk_role = '"+ IRoleCodeCont.jms07_ID+"') ");
            corpsql.append(" or a.pk_corp not in (select pk_corp from sm_user_role where pk_role = '"+IRoleCodeCont.jms08_ID+"'))");
        }
        corpsql.append(" order by a.innercode");
        return corpsql.toString();
    }


    /**
     * 获取我的客户查询参数
     *
     * @param queryvo
     * @param uservo
     * @return
     */
    private SQLParameter getQueryParam(QueryParamVO queryvo, UserVO uservo) {
        SQLParameter param = new SQLParameter();
//		param.addParam(queryvo.getPk_corp());

        if (!StringUtil.isEmpty(queryvo.getCorpid())){
            param.addParam(queryvo.getCorpid());
        }
        if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
            param.addParam("%" + queryvo.getCorpcode() + "%");
        }
        if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
            if (queryvo.getBegindate1().compareTo(queryvo.getEnddate()) == 0) {
                param.addParam(queryvo.getBegindate1());
            } else {
                param.addParam(queryvo.getBegindate1());
                param.addParam(queryvo.getEnddate());
            }
        }else if(queryvo.getBegindate1() != null){
            param.addParam(queryvo.getBegindate1());
        }else if(queryvo.getEnddate() != null){
            param.addParam(queryvo.getEnddate());
        }
        if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
            if (queryvo.getBcreatedate().compareTo(queryvo.getEcreatedate()) == 0) {
                param.addParam(queryvo.getBcreatedate());
            } else {
                param.addParam(queryvo.getBcreatedate());
                param.addParam(queryvo.getEcreatedate());
            }
        } else if (queryvo.getBcreatedate() != null) {// 建账日期只录入开始日期或结束日期
            param.addParam(queryvo.getBcreatedate());
        } else if (queryvo.getEcreatedate() != null) {
            param.addParam(queryvo.getEcreatedate());
        }
        if (queryvo.getXswyewfs() != null) {
            param.addParam(queryvo.getXswyewfs());
        }
//		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
//		if (!ismanager.booleanValue()) {
        param.addParam(uservo.getCuserid());
        param.addParam(uservo.getCuserid());
        param.addParam(uservo.getCuserid());
//		}
        if(!StringUtil.isEmpty(queryvo.getVprovince())){
            param.addParam(Integer.parseInt(queryvo.getVprovince()));
        }
        if(!StringUtil.isEmpty(queryvo.getKms_id())){//公司主键
            param.addParam(queryvo.getKms_id());
        }
        //是否激活dzfAPP
        if(queryvo.getIsdzfapp() != null){
            if(queryvo.getIsdzfapp() == 0){
                param.addParam(queryvo.getPk_corp());
            }else if(queryvo.getIsdzfapp() == 1){
                param.addParam(queryvo.getPk_corp());
            }
        }
        if(queryvo.getIfwgs() != null && queryvo.getIfwgs() != -1){
            param.addParam(queryvo.getIfwgs());
        }
        if(queryvo.getIsformal() != null){
            param.addParam(queryvo.getIsformal());
        }
        if(queryvo.getCjz() != null && queryvo.getCjz() != -1){
            param.addParam(queryvo.getCjz());
        }
        return param;
    }

    //默认值
    public void buildCorpTaxDefaultValue(CorpTaxVo vo, CorpVO corpVO){
//		CorpVO corpVO = (CorpVO) singleObjectBO.queryVOByID(pk_corp,
//				CorpVO.class);

        boolean flag = corpVO == null;

        String yhzc = vo.getVyhzc();//优惠政策
        Integer comtype = flag ? vo.getIcompanytype() : corpVO.getIcompanytype();
        if(StringUtil.isEmpty(yhzc)
                && (comtype == null ||
                (comtype != 2 && comtype != 20 && comtype != 21))){
            vo.setVyhzc("0");//默认为小型微利
        }

        //设置附加税税目默认税率
        DZFDouble citytax = vo.getCitybuildtax();//城建税
        if(citytax == null){
            vo.setCitybuildtax(new DZFDouble(0.07));
        }
        DZFDouble localtax = vo.getLocaleducaddtax();
        if(localtax == null){
            vo.setLocaleducaddtax(new DZFDouble(0.02));
        }
        DZFDouble educaddtax = vo.getEducaddtax();//教育费附加
        if(educaddtax == null){
            vo.setEducaddtax(new DZFDouble(0.03));
        }

        //设置报税地区默认值
        if (vo.getTax_area() == null) {
            Integer city = flag ? vo.getVcity() : corpVO.getVcity();
            Integer vprovince = flag ? vo.getVprovince() : corpVO.getVcity();
            if (city != null && (city == 151 || city == 171 || city == 234)) { //3个单独申报的地区/市：厦门、青岛、深圳
                vo.setDefTaxArea(city); //默认值仅在tax_area为空时才有值和起作用
            } else if (vprovince != null) { //省
                vo.setDefTaxArea(vprovince);
            }
        }

        Integer taxletype = vo.getTaxlevytype();
        if(taxletype == null){
            vo.setTaxlevytype(TaxRptConstPub.TAXLEVYTYPE_CZZS);//查账征收
        }

        Integer intype = vo.getIncomtaxtype();
        if(intype == null){
            vo.setIncomtaxtype(TaxRptConstPub.INCOMTAXTYPE_QY);
        }

        String corptype = flag ? vo.getCorptype() : corpVO.getCorptype();
        if(!"00000100AA10000000000BMD".equals(corptype)){//不是13小企业 不设默认值
            return;
        }

//		intype = vo.getIncomtaxtype();
        if(intype == null){//用上边的字段
            if(comtype == null ||
                    ( comtype != 2 && comtype != 20 && comtype != 21)){
                vo.setIncomtaxtype(TaxRptConstPub.INCOMTAXTYPE_QY);
            }else{
                vo.setIncomtaxtype(TaxRptConstPub.INCOMTAXTYPE_GR);
            }
        }

        taxletype = vo.getTaxlevytype();
        intype = vo.getIncomtaxtype();
        if(vo.getIsbegincom() == null
                && taxletype == TaxRptConstPub.TAXLEVYTYPE_CZZS
                && intype == TaxRptConstPub.INCOMTAXTYPE_GR){
            vo.setIsbegincom(DZFBoolean.TRUE);
        }

        if(taxletype == TaxRptConstPub.TAXLEVYTYPE_HDZS
                && vo.getVerimethod() == null){
            vo.setVerimethod(0);//核定应税所得率(能核算收入总额的)
        }

    }

    @Override
    public List<CorpTaxVo> queryTaxVoByParam(QueryParamVO queryvo, UserVO uservo) {
        String sql = getQuerySql(queryvo, uservo);
        SQLParameter param = getQueryParam(queryvo, uservo);
        List<CorpTaxVo> vos = (List<CorpTaxVo>) singleObjectBO.executeQuery(sql.toString(), param,
                new BeanListProcessor(CorpTaxVo.class));
        QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname",
                "pcountname", "wqcountname", "vcorporatephone", "unitdistinction" }, vos, 1);
        //赋值办税人员
        if(vos !=null && vos.size()>0){
            Set<String> uids = new HashSet<String>();
            Set<String> corpids = new HashSet<String>();
            for(CorpTaxVo vo:vos){
                if(!StringUtil.isEmpty(vo.getVtaxofficer())){
                    uids.add(vo.getVtaxofficer());
                }

                if(!StringUtil.isEmpty(vo.getPk_corp())){
                    corpids.add(vo.getPk_corp());
                }
            }
            if(uids!=null && uids.size()>0){
                UserVO[] uvos = (UserVO[]) singleObjectBO.queryByCondition(UserVO.class, "nvl(dr,0)=0 and "+ SqlUtil.buildSqlForIn("cuserid", uids.toArray(new String[0])), new SQLParameter());
                Map<String, String> namemap = new HashMap<String,String>();
                if(uvos!=null && uvos.length>0){
                    for(UserVO uvo:uvos){
                        namemap.put(uvo.getCuserid(), CorpSecretUtil.deCode(uvo.getUser_name()));
                    }
                }
                for(CorpTaxVo vo:vos){
                    if(!StringUtil.isEmpty(vo.getVtaxofficer())
                            && namemap.containsKey(vo.getVtaxofficer())){
                        vo.setVtaxofficernm(namemap.get(vo.getVtaxofficer()));
                    }
                }
            }

            if(corpids != null && corpids.size() > 0){
                List<CorpVO> cvos = (List<CorpVO>) singleObjectBO.executeQuery(" select * from bd_corp where nvl(dr,0)=0 and " + SqlUtil.buildSqlForIn("pk_corp", corpids.toArray(new String[0])),
                        new SQLParameter(), new BeanListProcessor(CorpVO.class));
                Map<String, CorpVO> corpmap = new HashMap<String, CorpVO>();
                if(cvos != null && cvos.size() > 0){
                    corpmap = DZfcommonTools.hashlizeObjectByPk(cvos, new String[]{"pk_corp"});
                    CorpVO corpvo;
                    //赋值所得税类型
                    for(CorpTaxVo vo : vos){
                        corpvo = corpmap.get(vo.getPk_corp());
                        buildCorpTaxDefaultValue(vo, corpvo);
                    }
                }
            }

        }

        return vos;
    }

    @Override
    public SalaryReportVO[] queryGzb(String pk_corp, String beginPeriod, String endPeriod, String billtype) {
        String wheresql = " pk_corp = ? and qj >= ? and  qj <= ? and nvl(dr,0) = 0 and nvl(billtype,'01') = ?";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(beginPeriod);
        sp.addParam(endPeriod);
        sp.addParam(billtype);
        SalaryReportVO[] srvos = (SalaryReportVO[]) singleObjectBO.queryByCondition(SalaryReportVO.class, wheresql, sp);
        return querySalaryReportVO(srvos, pk_corp, false);
    }


    private List<SalaryReportVO> sortByYgbm(List<SalaryReportVO> listall) {

        if (listall == null || listall.size() == 0)
            return listall;

        List<SalaryReportVO> list1 = new ArrayList<SalaryReportVO>();
        List<SalaryReportVO> list2 = new ArrayList<SalaryReportVO>();

        for (SalaryReportVO vo : listall) {
            if (StringUtil.isEmpty(vo.getYgbm())) {
                list1.add(vo);
            } else {
                list2.add(vo);
            }
        }
        list2.sort(Comparator.comparing(SalaryReportVO::getYgbm,Comparator.nullsFirst(LetterNumberSortUtil.letterNumberOrder())));

        list1.sort(Comparator.comparing(SalaryReportVO::getZjbm,Comparator.nullsFirst(LetterNumberSortUtil.letterNumberOrder())));
        for (SalaryReportVO vo : list2) {
            list1.add(vo);
        }
        return list1;
    }


    private DZFDouble getPeriodZxkc(SalaryReportVO vo) {
        DZFDouble ntotalzxkc = DZFDouble.ZERO_DBL;
        String[] columns = new String[] { "zfgjj", "yanglaobx", "yiliaobx", "shiyebx" };
        ntotalzxkc = addByColumn(columns, vo);
        return ntotalzxkc;
    }


    private DZFDouble getPeriodZxdc(SalaryReportVO vo) {
        DZFDouble ntotalzxdc = DZFDouble.ZERO_DBL;
        String[] columns = new String[] { "znjyzc", "jxjyzc", "zfdkzc", "zfzjzc", "sylrzc" };
        ntotalzxdc = addByColumn(columns, vo);
        return ntotalzxdc;
    }

    private DZFDouble addByColumn(String[] columns, SuperVO vo) {
        DZFDouble temp = DZFDouble.ZERO_DBL;
        if (columns == null || columns.length == 0)
            return temp;

        List<DZFDouble> list = new ArrayList<>();
        for (String column : columns) {
            list.add((DZFDouble) vo.getAttributeValue(column));
        }
        temp = addByDZFDouble(list.toArray(new DZFDouble[list.size()]));
        return temp;
    }


    private DZFDouble addByDZFDouble(DZFDouble[] dous) {
        DZFDouble temp = DZFDouble.ZERO_DBL;
        if (dous == null || dous.length == 0)
            return temp;

        for (DZFDouble dou : dous) {
            temp = SafeCompute.add(temp, dou);
        }
        return temp;
    }

    private void setTotalData(SalaryReportVO[] vos) {

        if (DZFValueCheck.isEmpty(vos)) {
            return;
        }

        for (SalaryReportVO vo : vos) {
            vo.setZxkcxj(getPeriodZxkc(vo));
            vo.setZxkcfjxj(getPeriodZxdc(vo));
        }
    }

    private SalaryReportVO[] querySalaryReportVO(SalaryReportVO[] srvos, String pk_corp, boolean isAll) {

        if (srvos == null || srvos.length == 0)
            return new SalaryReportVO[0];

        SalaryCipherHandler.handlerSalaryVO(srvos, 1);// 解密处理
        List<SalaryReportVO> listall = new ArrayList<SalaryReportVO>();
        if (srvos != null && srvos.length > 0) {
            setTotalData(srvos);
            listall = Arrays.asList(srvos);
        }
        GxhszVO gxh = queryGxhszVOByPkCorp(pk_corp);
        Integer kmShow = gxh.getSubjectShow();
        if (listall != null && listall.size() > 0) {
            Map map = accountService.queryMapByPk(pk_corp);
            Map<String, AuxiliaryAccountBVO> aumap = auxiliaryAccountService.queryMap(pk_corp);
            for (SalaryReportVO vo : listall) {
                if (!StringUtil.isEmpty(vo.getFykmid())) {
                    YntCpaccountVO accvo = (YntCpaccountVO) map.get(vo.getFykmid());
                    if (accvo != null) {
                        if (kmShow == 0) {
                            vo.setFykmname(accvo.getAccountname());
                        } else if (kmShow == 1) {
                            String[] fullname = accvo.getFullname().split("/");
                            if (fullname.length > 1) {
                                vo.setFykmname(fullname[0] + "/" + fullname[fullname.length - 1]);
                            } else {
                                vo.setFykmname(accvo.getFullname());
                            }
                        } else {
                            vo.setFykmname(accvo.getFullname());
                        }
                        vo.setFykmkind(accvo.getAccountkind());
                        vo.setFykmcode(accvo.getAccountcode());
                    }
                }

                if (map != null && map.size() > 0) {
                    AuxiliaryAccountBVO personvo = aumap.get(vo.getCpersonid());
                    if (personvo != null) {
                        vo.setCdeptid(personvo.getCdeptid());
                        vo.setYgbm(personvo.getCode());
                        vo.setYgname(personvo.getName());
                        vo.setZjbm(personvo.getZjbm());
                        vo.setZjlx(personvo.getZjlx());
                        vo.setVphone(personvo.getVphone());
                        if (StringUtil.isEmpty(personvo.getVarea())) {
                            if (isAll) {
                                SalaryReportEnum typeEnum =SalaryReportEnum.getTypeEnumByValue(personvo.getZjlx());
                                if(typeEnum != null)
                                    vo.setVarea(typeEnum.getArea());
                            }
                        } else {
                            vo.setVarea(personvo.getVarea());
                        }
                        if (personvo.getBirthdate() != null) {
                            vo.setVdef3(personvo.getBirthdate().toString());
                        }

                        if (personvo.getIsex() != null) {
                            if (personvo.getIsex().intValue() == 1) {
                                vo.setVdef4("男");
                            } else if (personvo.getIsex().intValue() == 2) {
                                vo.setVdef4("女");
                            }
                        }
                        vo.setVdef5(personvo.getVbirtharea());

                        if (personvo.getEntrydate() != null)
                            vo.setVdef21(personvo.getEntrydate().toString());

                        if (personvo.getLeavedate() != null)
                            vo.setVdef22(personvo.getLeavedate().toString());

                        if (personvo.getEmployedate() != null)
                            vo.setVdef1(personvo.getEmployedate().toString());

                        vo.setLhtype(personvo.getLhtype());
                        vo.setLhdate(personvo.getLhdate());

                        if (personvo.getSffc() != null)
                            vo.setSffc(personvo.getSffc());
                    }

                    AuxiliaryAccountBVO deptvo = aumap.get(vo.getCdeptid());
                    if (deptvo != null) {
                        vo.setVdeptname(deptvo.getName());
                        vo.setCdeptid(deptvo.getPk_auacount_b());
                    }
                }
            }
        }
        listall = sortByYgbm(listall);
        return listall.toArray(new SalaryReportVO[listall.size()]);
    }

    private StringBuffer buidTradeInSql(){
        StringBuffer sb = new StringBuffer();
        sb.append(" select ");
        String[] joinFields = new String[]{
                "ynt_inventory.name as Invname",
                "ynt_inventory.code as Invcode",
                "ynt_inventory.invspec as Invspec",
                "ynt_inventory.invtype as Invtype",
                "ynt_measure.name as Measure"
        };;
        for(String filed : joinFields){
            sb.append(filed)
                    .append(",");
        }
        sb.append(" ynt_ictradein.*,ynt_cpaccount.accountname kmmc ");
        sb.append(" from ynt_ictradein ynt_ictradein ");
        sb.append(" left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = ynt_ictradein.pk_inventory");
        sb.append(" left join ynt_measure ynt_measure on ynt_measure.pk_measure = ynt_inventory.pk_measure");
//		sb.append(" left join ynt_tzpz_b ynt_tzpz_b on ynt_tzpz_b.pk_tzpz_b = ynt_ictradein.pk_voucher_b");
//		sb.append(" left join ynt_tzpz_h ynt_tzpz_h on ynt_tzpz_h.pk_tzpz_h = ynt_tzpz_b.pk_tzpz_h");
        sb.append(" left join ynt_cpaccount ynt_cpaccount on ynt_inventory.pk_subject = ynt_cpaccount.pk_corp_account");

        return sb;
    }

    @Override
    public List<IctradeinVO> queryTradeIn(QueryParamVO paramvo) {
        StringBuffer sb = buidTradeInSql();
        SQLParameter sp=new SQLParameter();
        sp.addParam(paramvo.getPk_corp());
        sb.append(" where nvl(ynt_ictradein.dr,0)=0 ");
        sb.append(" and ynt_ictradein.pk_corp=? ");
        if(paramvo.getBegindate1() != null && paramvo.getEnddate() != null){
            if(paramvo.getBegindate1().equals(paramvo.getEnddate())){
                sp.addParam(paramvo.getBegindate1());
                sb.append(" and ynt_ictradein.dbilldate = ? ");
            }else{
                sp.addParam(paramvo.getBegindate1());
                sp.addParam(paramvo.getEnddate());
                sb.append(" and (ynt_ictradein.dbilldate >=? and ynt_ictradein.dbilldate <=?)");
            }
        }
        if(StringUtil.isEmptyWithTrim(paramvo.getPk_inventory())==false){
            sp.addParam(paramvo.getPk_inventory());
            sb.append(" and ynt_ictradein.pk_inventory = ?");
        }
//		sb.append(" order by  ynt_ictradein.dbilldate ");

        List<IctradeinVO> listVO = (List<IctradeinVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(IctradeinVO.class));
        //排序
        Collections.sort(listVO, new Comparator<IctradeinVO>() {
            @Override
            public int compare(IctradeinVO o1, IctradeinVO o2) {
                int i = o2.getDbilldate().compareTo(o1.getDbilldate());
                return i;
            }
        });
        return listVO;
    }

    private StringBuffer buildTradeOutSql(){
        StringBuffer sb = new StringBuffer();
        sb.append(" select ");
        String[] joinFields = new String[]{
                "ynt_inventory.name as Invname",
                "ynt_inventory.code as Invcode",
                "ynt_inventory.invspec as Invspec",
                "ynt_inventory.invtype as Invtype",
                "ynt_measure.name as Measure"
        };
        for(String filed : joinFields){
            sb.append(filed)
                    .append(",");
        }
        joinFields=null;
        sb.append(" ynt_ictradeout.*,ynt_cpaccount.accountname kmmc ");
        sb.append(" from ynt_ictradeout ynt_ictradeout ");
        sb.append(" left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = ynt_ictradeout.pk_inventory");
        sb.append(" left join ynt_measure ynt_measure on ynt_measure.pk_measure = ynt_inventory.pk_measure");
//		sb.append(" left join ynt_tzpz_b ynt_tzpz_b on ynt_tzpz_b.pk_tzpz_b = ynt_ictradeout.pk_voucher_b");
//		sb.append(" left join ynt_tzpz_h ynt_tzpz_h on ynt_tzpz_h.pk_tzpz_h = ynt_tzpz_b.pk_tzpz_h");
        sb.append(" left join ynt_cpaccount ynt_cpaccount on ynt_inventory.pk_subject = ynt_cpaccount.pk_corp_account");
        return sb;
    }

    @Override
    public List<IntradeoutVO> queryTradeOut(QueryParamVO paramvo) {

        StringBuffer sb = buildTradeOutSql();
        SQLParameter sp=new SQLParameter();
        sp.addParam(paramvo.getPk_corp());
//		sp.addParam(paramvo.getPk_corp());
//		sp.addParam(paramvo.getPk_corp());
        sb.append(" where nvl(ynt_ictradeout.dr,0)=0 ");
        sb.append(" and ynt_ictradeout.pk_corp=?  ");
//		sp.addParam(paramvo.getPk_corp());
//		sp.addParam(paramvo.getPk_corp());
//		sb.append(" and ynt_tzpz_h.pk_corp=?");
//		sb.append(" and ynt_tzpz_b.pk_corp=? ");
        if(paramvo.getBegindate1() != null && paramvo.getEnddate() != null){
            if(paramvo.getBegindate1().equals(paramvo.getEnddate())){
                sp.addParam(paramvo.getBegindate1());
                sb.append(" and ynt_ictradeout.dbilldate = ?");
            }else{
                sp.addParam(paramvo.getBegindate1());
                sp.addParam(paramvo.getEnddate());
                sb.append(" and (ynt_ictradeout.dbilldate >= ? and ynt_ictradeout.dbilldate <= ?)");
            }
        }

        if(StringUtil.isEmptyWithTrim(paramvo.getPk_inventory())==false){
            sp.addParam(paramvo.getPk_inventory());
            sb.append(" and ynt_ictradeout.pk_inventory = ?");
        }
//		sb.append(" order by  ynt_ictradeout.dbilldate ");

        List<IntradeoutVO> listVO = (List<IntradeoutVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(IntradeoutVO.class));
        //排序
        Collections.sort(listVO, new Comparator<IntradeoutVO>(){
            @Override
            public int compare(IntradeoutVO o1, IntradeoutVO o2) {
                int i = o2.getDbilldate().compareTo(o1.getDbilldate());
                return i;
            }

        });
        return listVO;
    }


    private void addDbillidWhere(StringBuffer buffer, IntradeParamVO paramvo, SQLParameter sp) {

        if (!StringUtil.isEmpty(paramvo.getDjh1()) && !StringUtil.isEmpty(paramvo.getDjh2())) {
            String djh1 = getdjh(paramvo.getDjh1());
            String djh2 = getdjh(paramvo.getDjh2());
            int len1 = djh1.length();
            int len2 = djh2.length();
            if (len1 < 5 && len2 < 5) {
                buffer.append(" and substr(dbillid,10,4) between ? and ? ");
                sp.addParam(djh1);
                sp.addParam(djh2);
            } else {
                buffer.append(" and dbillid between ? and ? ");
                sp.addParam(djh1);
                sp.addParam(djh2);
            }

        } else if (!StringUtil.isEmpty(paramvo.getDjh1())) {
            String djh1 = getdjh(paramvo.getDjh1());
            int len1 = djh1.length();
            if (len1 < 5) {
                buffer.append(" and substr(dbillid,10,4) >= ? ");
                sp.addParam(djh1);
            } else {
                buffer.append(" and dbillid >= ?");
                sp.addParam(djh1);
            }
        } else if (!StringUtil.isEmpty(paramvo.getDjh2())) {
            String djh2 = getdjh(paramvo.getDjh2());
            int len2 = djh2.length();
            if (len2 < 5) {
                buffer.append(" and substr(dbillid,10,4) <= ? ");
                sp.addParam(djh2);
            } else {
                buffer.append(" and dbillid <= ?");
                sp.addParam(djh2);
            }
        }

    }


    private String getdjh(String djh, DZFDate ddate) {

        if (StringUtil.isEmpty(djh) || ddate == null) {
            return null;
        }

        Object month = ddate.getMonth() >= 10 ? ((Object) (Integer.valueOf(ddate.getMonth())))
                : ((Object) ((new StringBuilder("0")).append(ddate.getMonth()).toString()));

        String prefix = "XS-" + ddate.getYear() + month;
        int len = djh.length();
        if (len > 4) {
            return prefix + djh;
        }

        try {
            Integer.parseInt(djh);
        } catch (Exception e) {
            return prefix + djh;
        }

        djh = addZeroForNum(djh, 4);

        return prefix + djh;
    }

    private String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                // sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }

    private String getdjh(String djh) {

        if (StringUtil.isEmpty(djh)) {
            return null;
        }
        try {
            Integer.parseInt(djh);
        } catch (Exception e) {
            return djh;
        }

        djh = addZeroForNum(djh, 4);

        return djh;
    }

    private void addCondition(StringBuffer buffer, IntradeParamVO paramvo, SQLParameter sp) {

        if (paramvo.getBegindate() != null) {
            buffer.append(" and dbilldate >= ?");
            sp.addParam(paramvo.getBegindate());

        }

        if (StringUtils.isNotEmpty(paramvo.getQinvid())) {
            buffer.append(
                    "and exists (select 1 from ynt_ictradeout b where y.PK_ICTRADE_H = b.PK_ICTRADE_H and b.pk_inventory = ?)");
            sp.addParam(paramvo.getQinvid());
        }

        if (paramvo.getEnddate() != null) {
            buffer.append(" and  dbilldate <= ?");
            sp.addParam(paramvo.getEnddate());
        }
        if (!StringUtil.isEmpty(paramvo.getCbusitype())) {
            buffer.append(" and  nvl(cbusitype,'" + IcConst.XSTYPE + "')  = ? ");
            sp.addParam(paramvo.getCbusitype());
        }

        if (!StringUtil.isEmpty(paramvo.getQcorpid())) {
            buffer.append(" and  pk_cust = ?");
            sp.addParam(paramvo.getQcorpid());
        } else {
            if (!StringUtil.isEmpty(paramvo.getQcorpname())) {
                buffer.append(" and code like ? ");
                sp.addParam("%" + paramvo.getQcorpname() + "%");
            }
        }

        if (paramvo.getMny1() != null) {
            buffer.append(" and  nvl(nmny,0) >= ?");
            sp.addParam(paramvo.getMny1());
        }

        if (paramvo.getMny2() != null) {
            buffer.append(" and  nvl(nmny,0) <= ?");
            sp.addParam(paramvo.getMny2());
        }

        addDbillidWhere(buffer, paramvo, sp);
    }

    @Override
    public List<IntradeHVO> queryIntradeHVOOut(IntradeParamVO paramvo) {

        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sf.append(" Select y.* ,fb.name custname ");
        sf.append("   From ynt_ictrade_h y ");
        sf.append("   left join ynt_fzhs_b fb ");
        sf.append("     on y.pk_cust = fb.pk_auacount_b and fb.pk_auacount_h = ? ");
        sf.append("  Where y.pk_corp = ? ");
        sf.append("    and nvl(y.dr, 0) = 0 ");
        sf.append("    and cbilltype = ? ");
        sp.addParam(AuxiliaryConstant.ITEM_CUSTOMER);// 客户
        sp.addParam(paramvo.getPk_corp());
        sp.addParam(IBillTypeCode.HP75);

        addCondition(sf, paramvo, sp);

        List<IntradeHVO> listVO = (List<IntradeHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(IntradeHVO.class));
        IntradeHVO[] vos = null;
        if (listVO != null && listVO.size() > 0) {

            Set<String> set = new HashSet<>();

            for (IntradeHVO hvo : listVO) {
                set.add(hvo.getPk_ictrade_h());
            }
            sp.clearParams();
            sp.addParam(listVO.get(0).getPk_corp());
            String idwhere = SqlUtil.buildSqlForIn(" pk_ictrade_h ", set.toArray(new String[set.size()]));
            IntradeoutVO[] bvos = (IntradeoutVO[]) singleObjectBO.queryByCondition(IntradeoutVO.class,
                    " nvl(dr,0) = 0 and pk_corp = ? and " + idwhere, sp);

            if (bvos != null && bvos.length > 0) {
                List<IntradeoutVO> list = null;
                Map<String, List<IntradeoutVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(bvos),
                        new String[] { "pk_ictrade_h" });
                for (IntradeHVO hvo : listVO) {
                    if(!StringUtil.isEmpty(hvo.getPk_cust())){
                        boolean b = auxiliaryAccountService.isExistFz(hvo.getPk_corp(),hvo.getPk_cust(),AuxiliaryConstant.ITEM_CUSTOMER);
                        if(!b){
                            hvo.setPk_cust(null);
                        }
                    }
                    DZFDouble nnum = DZFDouble.ZERO_DBL; // 数量
                    DZFDouble ncost = DZFDouble.ZERO_DBL; // 成本金额
                    sp.clearParams();
                    sp.addParam(hvo.getPk_corp());
                    sp.addParam(hvo.getPrimaryKey());

                    list = map.get(hvo.getPrimaryKey());
                    if (list != null && list.size() > 0) {
                        for (SuperVO child : list) {
                            IntradeoutVO invo = (IntradeoutVO) child;
                            nnum = SafeCompute.add(nnum, invo.getNnum());
                            ncost = SafeCompute.add(ncost, invo.getNcost());
                        }
                        hvo.setVdef2(SafeCompute.div(ncost, nnum));
                        hvo.setVdef3(ncost);
                        hvo.setVdef4(SafeCompute.div(hvo.getNmny(), nnum));
                        hvo.setVdef5(nnum);
                    }
                }
            }

            vos = listVO.toArray(new IntradeHVO[listVO.size()]);
            VOUtil.ascSort(vos, new String[] { "dbilldate", "dbillid" });
        }
        return vos == null || vos.length == 0 ? null : Arrays.asList(vos);
    }

    @Override
    public IntradeHVO queryIntradeHVOByID(String pk_ictrade_h, String pk_corp) {
        if (StringUtil.isEmpty(pk_ictrade_h)) {
            return null;
        }
        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sf.append(" select y.* ,fb.name custname");
        sf.append(" From ynt_ictrade_h y ");
        sf.append(" left join ynt_fzhs_b fb ");
        sf.append(" on y.pk_cust = fb.pk_auacount_b and fb.pk_auacount_h = ? ");
        sf.append(" where y.pk_corp = ? ");
        sf.append(" ");
        sf.append(" and nvl(y.dr, 0) = 0 ");
        // sf.append(" and nvl(fb.dr, 0) = 0 ");
        sf.append(" and pk_ictrade_h = ? ");
        sp.addParam(AuxiliaryConstant.ITEM_SUPPLIER);// 供应商
        sp.addParam(pk_corp);
        sp.addParam(pk_ictrade_h);
        List<IntradeHVO> listVO = (List<IntradeHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(IntradeHVO.class));
        IntradeHVO hvo = null;
        if (listVO != null && listVO.size() > 0) {

            hvo = listVO.get(0);

            sf.setLength(0);

            IctradeinVO[] bvos = queryIctradeinVO(pk_ictrade_h, pk_corp);
            hvo.setChildren(bvos);
        }
        return hvo;
    }

    private IctradeinVO[] queryIctradeinVO(String pk_ictrade_h, String pk_corp) throws DZFWarpException {
        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sf.append(" select y.* ,fb.name invname,fb.code invcode, fb.invtype,fb.invspec");
        sf.append(" ,me.name measure ,fb.pk_subject,ct.accountname kmmc,ct.accountcode kmbm,fy.name invclassname");
        sf.append(" From ynt_ictradein y ");
        sf.append(" left join ynt_inventory fb ");
        sf.append(" on y.pk_inventory = fb.pk_inventory and nvl(fb.dr, 0) = 0 ");
        sf.append(" left join ynt_cpaccount ct on fb.pk_subject = ct.pk_corp_account and nvl(ct.dr, 0) = 0 ");
        sf.append(" left join ynt_measure me on fb.pk_measure = me.pk_measure and nvl(me.dr, 0) = 0 ");
        sf.append(" left join ynt_invclassify fy on fb.pk_invclassify = fy.pk_invclassify and  nvl(fy.dr, 0) = 0  ");
        sf.append(" where y.pk_corp = ? ");
        sf.append(" and nvl(y.dr, 0) = 0 ");

        sf.append(" and pk_ictrade_h = ? ");
        sp.addParam(pk_corp);
        sp.addParam(pk_ictrade_h);
        List<IctradeinVO> listBVO = (List<IctradeinVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(IctradeinVO.class));
        if (listBVO == null || listBVO.size() == 0) {
            return null;
        }
        listBVO.sort(Comparator.comparing(IctradeinVO::getInvcode,Comparator.nullsFirst(LetterNumberSortUtil.letterNumberOrder())));
        return listBVO.toArray(new IctradeinVO[listBVO.size()]);
    }

    @Override
    public IntradeHVO queryIntradeHVOByIDIn(String pk_ictrade_h, String pk_corp) {
        return this.queryIntradeHVOByID(pk_ictrade_h,pk_corp);
    }

    @Override
    public List<IntradeHVO> queryIntradeHVOIn(IntradeParamVO paramvo) {
        // 根据查询条件查询公司的信息
        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sf.append(" select y.* ,fb.name custname");
        sf.append(" From ynt_ictrade_h y ");
        sf.append(" left join ynt_fzhs_b fb ");
        sf.append(" on y.pk_cust = fb.pk_auacount_b and fb.pk_auacount_h = ?");
        sf.append(" where y.pk_corp = ? ");
        sf.append(" and nvl(y.dr, 0) = 0 ");
        sf.append(" and cbilltype = ? ");
        sp.addParam(AuxiliaryConstant.ITEM_SUPPLIER);// 客户
        sp.addParam(paramvo.getPk_corp());
        sp.addParam(IBillTypeCode.HP70);

        addCondition(sf, paramvo, sp);

        List<IntradeHVO> listVO = (List<IntradeHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(IntradeHVO.class));
        IntradeHVO[] vos = null;
        if (listVO != null && listVO.size() > 0) {

            Set<String> set = new HashSet<>();

            for (IntradeHVO hvo : listVO) {
                set.add(hvo.getPk_ictrade_h());
            }
            sp.clearParams();
            sp.addParam(listVO.get(0).getPk_corp());
            String idwhere = SqlUtil.buildSqlForIn(" pk_ictrade_h ", set.toArray(new String[set.size()]));
            IctradeinVO[] bvos = (IctradeinVO[]) singleObjectBO.queryByCondition(IctradeinVO.class,
                    " nvl(dr,0) = 0 and pk_corp = ? and " + idwhere, sp);
            if (bvos != null && bvos.length > 0) {
                List<IctradeinVO> list = null;
                Map<String, List<IctradeinVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(bvos),
                        new String[] { "pk_ictrade_h" });
                for (IntradeHVO hvo : listVO) {
                    DZFDouble nnum = DZFDouble.ZERO_DBL; // 金额
                    list = map.get(hvo.getPrimaryKey());
                    if(!StringUtil.isEmpty(hvo.getPk_cust())){
                        boolean b = auxiliaryAccountService.isExistFz(hvo.getPk_corp(),hvo.getPk_cust(),AuxiliaryConstant.ITEM_SUPPLIER);
                        if(!b){
                            hvo.setPk_cust(null);
                        }
                    }
                    if (list != null && list.size() > 0) {
                        for (SuperVO child : list) {
                            IctradeinVO invo = (IctradeinVO) child;
                            nnum = SafeCompute.add(nnum, invo.getNnum());
                        }
                        hvo.setVdef4(SafeCompute.div(hvo.getNmny(), nnum));
                        hvo.setVdef5(nnum);
                    }
                }
            }
            vos = listVO.toArray(new IntradeHVO[listVO.size()]);
            VOUtil.ascSort(vos, new String[] { "dbilldate", "dbillid" });
        }
        return vos == null || vos.length == 0 ? null : Arrays.asList(vos);
    }

    @Override
    public AssetDepreciaTionVO[] getZczjMxVOs(QueryParamVO vo) {
        String asset_id = null;
        StringBuffer qrysql = new StringBuffer();
        if(vo.getBegindate1()==null && StringUtil.isEmpty(asset_id)){
            throw new BusinessException("查询失败:开始日期不能为空!");
        }

        if(vo.getBegindate1()!=null && vo.getEnddate()!=null){
            if(vo.getBegindate1().after(vo.getEnddate())){
                throw new BusinessException("查询开始日期，应在结束日期之前!");
            }
        }

        String zxlb = vo.getPk_assetcategory();
        String zcsx = vo.getZcsx();
        String zcbm = vo.getZccode();
        SQLParameter sp = new SQLParameter();
        qrysql.append("select d.catename,d.assetproperty,b.assetcode,b.assetname,b.accountdate,b.uselimit,b.salvageratio,");
        qrysql.append("a.businessdate,b.assetmny, a.depreciationmny, a.assetnetmny,d.catecode,d.catelevel,");
        qrysql.append("a.originalvalue, a.istogl, a.issettle,c.pzh, a.pk_corp ,a.pk_voucher,a.pk_assetdepreciation,a.pk_assetcard,d.pk_assetcategory,c.doperatedate  as doperatedate ");
        qrysql.append("from ynt_depreciation a  left join ynt_assetcard b on a.pk_assetcard = b.pk_assetcard ");
        qrysql.append("left join ynt_category d on b.assetcategory=d.pk_assetcategory ");
        qrysql.append("left join ynt_tzpz_h  c on c.pk_tzpz_h = a.pk_voucher  and  nvl(c.dr,0)=0");
        qrysql.append(" where a.pk_corp =? and  nvl(a.dr,0)=0  and nvl(b.dr,0)=0 ");
        sp.addParam(vo.getPk_corp());
        if(vo.getBegindate1()!=null){
            qrysql.append(" and a.businessdate >=? ");
            sp.addParam(vo.getBegindate1());
        }
        if(vo.getEnddate()!=null){
            qrysql.append(" and a.businessdate <=? ");
            sp.addParam(DateUtils.getPeriodEndDate(DateUtils.getPeriod(vo.getEnddate())) );//默认查询最后一个月份
        }

        if(!StringUtil.isEmpty(zxlb)){
            qrysql.append("  and b.assetcategory ='"+zxlb+"'");
        }
        if(!StringUtil.isEmpty(zcsx)){
            if(!"2".equals(zcsx)){//2位显示全部
                qrysql.append("  and d.assetproperty="+ zcsx);
            }
        }
        if(!StringUtil.isEmpty(zcbm)){
            qrysql.append(" and b.assetcode like '%"+zcbm+"%'");
            //qrysql.append("  and b.assetname like '%"+zcbm+"%'");
        }
        if(!StringUtil.isEmpty(asset_id)){
            qrysql.append(" and a.pk_assetcard = ? ");
            sp.addParam(asset_id);
        }
        qrysql.append(" order by d.catecode,b.assetcode,b.assetname,a.businessdate");
        List<AssetDepreciaTionVO> result1 = (List<AssetDepreciaTionVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(AssetDepreciaTionVO.class));

        //查询当前卡片变更过多少次取最近的日期
        StringBuffer bgsql = new StringBuffer();
        bgsql.append( " select a.originalvalue,a.newvalue,a.pk_assetcard,a.businessdate as doperatedate  from ynt_valuemodify a  inner join  ynt_assetcard b ");
        bgsql.append(" 	on a.pk_assetcard = b.pk_assetcard  and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 ");
        bgsql.append(" 	where nvl(a.istogl,'N')='Y'  ");
        bgsql.append("  and a.pk_corp =  ? " );
        bgsql.append(" 	order by b.assetcode ,a.businessdate desc ");
        sp.clearParams();
        sp.addParam(vo.getPk_corp());
        List<ValuemodifyVO> result2 = (List<ValuemodifyVO>) singleObjectBO.executeQuery(bgsql.toString(), sp, new BeanListProcessor(ValuemodifyVO.class));

        for(AssetDepreciaTionVO  resvo:result1){
            String pk_asset = resvo.getPk_assetcard();
            DZFDate dopedate = resvo.getBusinessdate();
            DZFBoolean isasset = DZFBoolean.FALSE;
            DZFDouble resdouble = DZFDouble.ZERO_DBL;
            Integer cont = 0;
            for(ValuemodifyVO modivo:result2){
                if(modivo.getPk_assetcard().equals(pk_asset)){
                    if(!isasset.booleanValue() && (modivo.getDoperatedate().before(dopedate) || modivo.getDoperatedate().compareTo(dopedate) ==0)){
                        isasset = DZFBoolean.TRUE;
                        resvo.setAssetmny(modivo.getNewvalue());
                    }
                    resdouble = modivo.getOriginalvalue();
                    cont++;
                }
            }
            if(cont>0 && !isasset.booleanValue()){
                resvo.setAssetmny(resdouble);
            }
        }
        AssetDepreciaTionVO[]  zczjarray= result1.toArray(new AssetDepreciaTionVO[0]);

        return zczjarray;
    }

    private String getQueryOwnCorpSql () {
        return " select cp.pk_corp from bd_corp cp" +
                "  join sm_user_role re on re.pk_corp = cp.pk_corp " +
                " where re.cuserid = ? and nvl(re.dr,0) = 0 and nvl(cp.isaccountcorp,'N') = 'N' " +
                " and nvl(cp.isseal,'N') = 'N' and nvl(cp.ishasaccount,'N') = 'Y' ";
    }

    @Override
    public Set<String> querypowercorpSet(String userid){
        Set<String> set = new HashSet<>();
        if(!StringUtil.isEmpty(userid)) {
            SQLParameter sp = new SQLParameter();
            sp.addParam(userid);
            List<String> list = (List<String>) singleObjectBO.executeQuery(getQueryOwnCorpSql(),
                    sp, new ColumnListProcessor());
            set.addAll(list);
        }
        return set;
    }

    @Override
    public BdCurrencyVO[] queryCurrency() {
        SQLParameter sp = new SQLParameter();
        sp.addParam(IDefaultValue.DefaultGroup);
        String condition = " pk_corp = ? and nvl(dr,0) = 0 order by currencycode  ";
        BdCurrencyVO[] vos= (BdCurrencyVO[]) singleObjectBO.queryByCondition(BdCurrencyVO.class, condition, sp);
        if(vos == null || vos.length == 0)
            return null;
        return vos;
    }
}
