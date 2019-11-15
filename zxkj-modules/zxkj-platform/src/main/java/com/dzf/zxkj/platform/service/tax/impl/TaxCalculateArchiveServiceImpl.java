package com.dzf.zxkj.platform.service.tax.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.enums.IFpStyleEnum;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.enums.SurTaxEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.jzcl.QmLossesVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.SurTaxTemplate;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.model.tax.*;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.impl.IncomeTaxCalculator;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.service.tax.ITaxCalculateArchiveService;
import com.dzf.zxkj.platform.service.tax.ITaxitemsetService;
import com.dzf.zxkj.platform.util.KmbmUpgrade;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.report.service.IZxkjReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gl_taxarchive")
@SuppressWarnings("all")
public class TaxCalculateArchiveServiceImpl implements
        ITaxCalculateArchiveService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IZxkjReportService zxkjReportService;

    @Autowired
    private IQmclService gl_qmclserv;
    @Autowired
    private ITaxitemsetService sys_taxsetserv;
    @Autowired
    private ICorpTaxService sys_corp_tax_serv;
    @Autowired
    private IParameterSetService sys_parameteract;
    @Autowired
    private IYntBoPubUtil yntBoPubUtil;
    @Autowired
    private IVoucherService gl_tzpzserv;

    @Autowired
    private ICorpService corpService;

    @Autowired
    private IAccountService accountService;

    @Override
    public TaxCalculateArchiveVO[] query(String pk_corp)
            throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        TaxCalculateArchiveVO[] rs = (TaxCalculateArchiveVO[]) singleObjectBO
                .queryByCondition(TaxCalculateArchiveVO.class,
                        " pk_corp = ? and nvl(dr, 0) = 0 order by snumber", sp);
        if (rs == null || rs.length == 0) {
            List<TaxCalculateArchiveVO> list = getDefaultTax();
            rs = list.toArray(new TaxCalculateArchiveVO[0]);
        }
        // 城建税取纳税信息维护的税率
        String sql = "select citybuildtax, localeducaddtax from bd_corp_tax where pk_corp = ? and nvl(dr,0)=0";
        CorpTaxVo taxVO = (CorpTaxVo) singleObjectBO.executeQuery(sql, sp,
                new BeanProcessor(CorpTaxVo.class));
        if (taxVO != null) {
            for (TaxCalculateArchiveVO vo : rs) {
                if (vo.getTax_name().equals("城建税")
                        && taxVO.getCitybuildtax() != null) {
                    vo.setRate(taxVO.getCitybuildtax().multiply(100));
                } else if (vo.getTax_name().equals("地方教育费附加")
                        && taxVO.getLocaleducaddtax() != null) {
                    vo.setRate(taxVO.getLocaleducaddtax().multiply(100));
                }
            }
        }
        return rs;
    }

    /**
     * 获取预置的税种
     *
     * @return
     */
    private List<TaxCalculateArchiveVO> getDefaultTax() {
        List<TaxCalculateArchiveVO> list = new ArrayList<TaxCalculateArchiveVO>();
        TaxCalculateArchiveVO vo = new TaxCalculateArchiveVO();
        vo.setSnumber(1);
        vo.setTax_name("增值税");
        vo.setRate(new DZFDouble(100));
        list.add(vo);

        vo = new TaxCalculateArchiveVO();
        vo.setSnumber(2);
        vo.setTax_name("城建税");
        vo.setRate(new DZFDouble(7));
        list.add(vo);

        vo = new TaxCalculateArchiveVO();
        vo.setSnumber(3);
        vo.setTax_name("教育费附加");
        vo.setRate(new DZFDouble(3));
        list.add(vo);

        vo = new TaxCalculateArchiveVO();
        vo.setSnumber(4);
        vo.setTax_name("地方教育费附加");
        vo.setRate(new DZFDouble(2));
        list.add(vo);

        vo = new TaxCalculateArchiveVO();
        vo.setSnumber(5);
        vo.setTax_name("企业所得税");
        vo.setRate(new DZFDouble(25));
        list.add(vo);
        return list;
    }

    @Override
    public void save(String pk_corp, TaxCalculateArchiveVO[] taxs)
            throws DZFWarpException {
        for (TaxCalculateArchiveVO taxCalculateArchiveVO : taxs) {
            taxCalculateArchiveVO.setPk_corp(pk_corp);
            taxCalculateArchiveVO.setDr(0);
        }
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        singleObjectBO.executeUpdate(
                "delete from ynt_taxarchive where pk_corp = ? and nvl(dr,0)=0",
                sp);
        singleObjectBO.insertVOArr(pk_corp, taxs);
    }

    @Override
    public List<FseJyeVO> getTaxAmount(CorpVO corpVO, String period)
            throws DZFWarpException {
        DZFDate beginDate = DateUtils.getPeriodStartDate(period);
        DZFDate corpBegin = corpVO.getBegindate();
        DZFDate endDate = null;
        String[] kms = new String[]{
                "22210101",
                "22210102",
                "222109",
                "222110",
                "00000100AA10000000000BMF".equals(corpVO.getCorptype()) ? "6001"
                        : "5001"};

        List<FseJyeVO> rs = new ArrayList<FseJyeVO>();
        if ("一般纳税人".equals(corpVO.getChargedeptname())) {
            endDate = DateUtils.getPeriodEndDate(period);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(beginDate.toDate());
            cal.add(Calendar.MONTH, 2);
            endDate = new DZFDate(cal.getTime());
        }
        if (beginDate.before(corpBegin)) {
            beginDate = corpBegin;
        }
        QueryParamVO paramvo = ReportUtil.getFseQueryParamVO(corpVO, beginDate,
                endDate, kms, false);

        FseJyeVO[] fsejyevos = zxkjReportService.getFsJyeVOs(paramvo, 1);
        if (fsejyevos != null) {
            rs.addAll(Arrays.asList(fsejyevos));
        }
        if (!"一般纳税人".equals(corpVO.getChargedeptname())) {
            DZFDouble[] vatMny = gl_qmclserv.getVatMnyByInvoiceType(corpVO,
                    DateUtils.getPeriod(beginDate),
                    DateUtils.getPeriod(endDate));
            FseJyeVO sds = new FseJyeVO();
            sds.setKmmc("税收优惠");
            sds.setFsdf(vatMny[0]);
            rs.add(sds);

            sds = new FseJyeVO();
            sds.setKmmc("销项税额");
            sds.setFsdf(vatMny[0].add(vatMny[1]));
            rs.add(sds);
        }

        int month = endDate.getMonth();
        if (month % 3 == 0) {
            paramvo.setQjq(period);
            paramvo.setQjz(DateUtils.getPeriod(endDate));
            paramvo.setPk_corp(corpVO.getPk_corp());
            paramvo.setIshasjz(DZFBoolean.FALSE);
            LrbquarterlyVO[] lrVos = zxkjReportService
                    .getLRBquarterlyVOs(paramvo);
            DZFDouble lrzz = DZFDouble.ZERO_DBL;
            if (lrVos != null) {
                for (LrbquarterlyVO lrbquarterlyVO : lrVos) {
                    if ("三、利润总额（亏损总额以“-”填列）".equals(lrbquarterlyVO.getXm())
                            || "三、利润总额（亏损总额以“-”号填列）".equals(lrbquarterlyVO
                            .getXm())) {
                        // 利润总额-本年累计
                        lrzz = lrbquarterlyVO.getBnlj();
                    }
                }
            }
            // 弥补以前年度亏损数
            QmLossesVO lsVo = gl_qmclserv.queryLossmny(new DZFDate(period
                    + "-01"), corpVO.getPk_corp());
            if (lsVo != null) {
                lrzz = SafeCompute.sub(lrzz, lsVo.getNlossmny());
            }

            FseJyeVO sds = new FseJyeVO();
            sds.setKmmc("企业所得税");
            sds.setFsdf(lrzz);
            rs.add(sds);
        }
        return rs;
    }

    @Override
    public TaxCalculateVO getTax(String pk_corp, String period, boolean reFetch) {
        String[] quarterRange = getQuarterRange(period);
        String quarterStart = quarterRange[0];
        String quarterEnd = quarterRange[1];

        CorpVO corpVO = corpService.queryByPk(pk_corp);
        TaxCalculateVO taxVO = new TaxCalculateVO();
        taxVO.setPk_corp(pk_corp);
        taxVO.setPeriod(period);
        TaxSettingVO setting = getTaxSettings(corpVO, period);
        taxVO.setSettings(setting);
        String periodStart = null;
        String periodEnd = null;
        DZFDateTime lastModifyTime = null;
        if (setting.getAddTaxPeriodType() == TaxSettingVO.PERIOD_MONTH
                || setting.getAddTaxPeriodType() == TaxSettingVO.PERIOD_QUARTER
                && quarterEnd.equals(period)) {
            if (setting.getAddTaxPeriodType() == TaxSettingVO.PERIOD_MONTH) {
                periodStart = period;
                periodEnd = period;
            } else {
                periodStart = quarterStart;
                periodEnd = quarterEnd;
            }
            getSavedAddTax(taxVO, periodEnd);
            if (reFetch || taxVO.getAddtax_info() == null) {
                getAddTax(taxVO, corpVO, periodStart, periodEnd);
            } else {
                lastModifyTime = taxVO.getAddtax_info().getModifyDate();
            }
            taxVO.setIncomeMny(getIncomeMny(corpVO, periodStart, periodEnd));
        }
        taxVO.setSurtax(getSurTax(corpVO, period, quarterStart, quarterEnd, setting, reFetch));
        if (setting.getIncomeTaxPeriodType() == TaxSettingVO.PERIOD_MONTH
                || setting.getIncomeTaxPeriodType() == TaxSettingVO.PERIOD_QUARTER
                && quarterEnd.equals(period)) {
            if (setting.getIncomeTaxPeriodType() == TaxSettingVO.PERIOD_MONTH) {
                periodStart = period;
                periodEnd = period;
            } else {
                periodStart = quarterStart;
                periodEnd = quarterEnd;
            }
            taxVO.setIncometax(getIncomeTax(corpVO, periodEnd, setting, reFetch));
        }
        getTaxStatus(pk_corp, taxVO);
        taxVO.setShouldRefetch(checkShouldRefetch(pk_corp, lastModifyTime));
        return taxVO;
    }

    private boolean checkShouldRefetch(String pk_corp, DZFDateTime time) {
        if (time == null) {
            return false;
        }
        String sql = "select 1 from ynt_logrecord where pk_corp = ? and nvl(dr,0)=0 " +
                " and doperatedate > ? and iopetype in (?,?,?) ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(time);
        sp.addParam(LogRecordEnum.OPE_KJ_ADDVOUCHER.getValue());
        sp.addParam(LogRecordEnum.OPE_KJ_DELVOUCHER.getValue());
        sp.addParam(LogRecordEnum.OPE_KJ_EDITVOUCHER.getValue());
        return singleObjectBO.isExists(pk_corp, sql, sp);
    }

    private void getTaxStatus(String pk_corp, TaxCalculateVO taxVO) {
        taxVO.setAddValueTaxStatus(new TaxStatusVO());
        taxVO.setSurtaxStatus(new TaxStatusVO());
        taxVO.setIncomeTaxStatus(new TaxStatusVO());
        String period = taxVO.getPeriod();
        QmclVO qmcl = null;//gl_qmclserv.queryQmclVO(pk_corp, period);
        if (qmcl != null) {
            // 结转状态
            taxVO.getAddValueTaxStatus().setCarryover(qmcl.getZzsjz() != null && qmcl.getZzsjz().booleanValue());
            taxVO.getSurtaxStatus().setCarryover(qmcl.getIsjtsj() != null && qmcl.getIsjtsj().booleanValue());
            taxVO.getIncomeTaxStatus().setCarryover(qmcl.getQysdsjz() != null && qmcl.getQysdsjz().booleanValue());
        }
        getVoucherInfo(pk_corp, taxVO);
    }

    private TaxSettingVO getTaxSettings(CorpVO corpVO, String period) {
        TaxSettingVO setting = new TaxSettingVO();
        CorpTaxVo corpTax = getCorpTaxInfo(corpVO.getPk_corp(), period);

        if ("一般纳税人".equals(corpVO.getChargedeptname())) {
            setting.setAddTaxPeriodType(0);
        } else {
            // 增值税结转参数设置
            YntParameterSet paramSet = sys_parameteract.queryParamterbyCode(corpVO.getPk_corp(), "dzf007");
            if (paramSet != null && paramSet.getPardetailvalue() != 0) {
                setting.setAddTaxPeriodType(0);
            } else {
                setting.setAddTaxPeriodType(1);
            }
        }
        // 所得税结转参数设置
        YntParameterSet incomeTaxSet = sys_parameteract.queryParamterbyCode(corpVO.getPk_corp(), "dzf013");

        setting.setIncomeTaxLevyType(corpTax.getTaxlevytype());
        setting.setIncomeTaxType(corpTax.getIncomtaxtype());
        setting.setIncomeTaxPeriodType(incomeTaxSet == null || incomeTaxSet.getPardetailvalue() == 0 ? 1 : 0);
        setting.setStart_production_date(corpTax.getBegprodate());
        setting.setIncomeTaxPreferPolicy(corpTax.getVyhzc());
        setting.setCityBuildRate(corpTax.getCitybuildtax());
        setting.setLocalEduRate(corpTax.getLocaleducaddtax());
        setting.setIncomeTaxFixedRate(corpTax.getIncometaxrate());
        return setting;
    }

    private void getVoucherInfo(String pk_corp, TaxCalculateVO taxVO) {
        boolean addTaxCarryover = taxVO.getAddValueTaxStatus().getCarryover() != null
                && taxVO.getAddValueTaxStatus().getCarryover();
        boolean suraxCarryover = taxVO.getSurtaxStatus().getCarryover() != null
                && taxVO.getSurtaxStatus().getCarryover();
        boolean incomeTaxCarryover = taxVO.getIncomeTaxStatus().getCarryover() != null
                && taxVO.getIncomeTaxStatus().getCarryover();
        if (addTaxCarryover || suraxCarryover || incomeTaxCarryover) {
            SQLParameter sp = new SQLParameter();
            sp.addParam(pk_corp);
            sp.addParam(taxVO.getPeriod());
            List<String> types = new ArrayList<>();
            if (addTaxCarryover) {
                types.add(IBillTypeCode.HP120);
                sp.addParam(IBillTypeCode.HP120);
            }
            if (suraxCarryover) {
                types.add(IBillTypeCode.HP39);
                sp.addParam(IBillTypeCode.HP39);
            }
            if (incomeTaxCarryover) {
                types.add(IBillTypeCode.HP125);
                sp.addParam(IBillTypeCode.HP125);
            }
            String inSql = SQLHelper.getInSQL(types);
            StringBuilder sql = new StringBuilder();
            sql.append("select pk_tzpz_h, pzh, sourcebilltype from ynt_tzpz_h where pk_corp = ? ")
                    .append("and period = ? and sourcebilltype in ").append(inSql)
                    .append(" and nvl(dr,0)=0 ");
            List<TzpzHVO> vouchers = (List<TzpzHVO>) singleObjectBO
                    .executeQuery(sql.toString(), sp, new BeanListProcessor(TzpzHVO.class));
            for (TzpzHVO voucher : vouchers) {
                switch (voucher.getSourcebilltype()) {
                    case IBillTypeCode.HP120:
                        taxVO.getAddValueTaxStatus().setVoucherID(voucher.getPk_tzpz_h());
                        taxVO.getAddValueTaxStatus().setVoucherNumber(voucher.getPzh());
                        break;
                    case IBillTypeCode.HP39:
                        taxVO.getSurtaxStatus().setVoucherID(voucher.getPk_tzpz_h());
                        taxVO.getSurtaxStatus().setVoucherNumber(voucher.getPzh());
                        break;
                    case IBillTypeCode.HP125:
                        taxVO.getIncomeTaxStatus().setVoucherID(voucher.getPk_tzpz_h());
                        taxVO.getIncomeTaxStatus().setVoucherNumber(voucher.getPzh());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    // 计算
    private void calculateAddValueTax(AddValueTaxCalVO taxInfo,
                                      List<AddValueTaxVO> outTaxes, List<AddValueTaxVO> inTaxes) {
        // 应纳税额 = 专票税额+普票税额+未开票税额-本期认证抵扣税额-上期留抵税额+进项税额转出
        DZFDouble payable = DZFDouble.ZERO_DBL;
        if (outTaxes != null) {
            for (AddValueTaxVO addValueTaxVO : outTaxes) {
                payable = SafeCompute.add(payable,
                        addValueTaxVO.getTaxmny_spec());
                payable = SafeCompute.add(payable,
                        addValueTaxVO.getTaxmny_gen());
                payable = SafeCompute.add(payable,
                        addValueTaxVO.getTaxmny_not());
            }
        }
        payable = SafeCompute.sub(payable, inTaxes.get(0).getTaxmny_spec());
        payable = SafeCompute.sub(payable, taxInfo.getSqld());
        payable = SafeCompute.add(payable, taxInfo.getJxszc());
        taxInfo.setYnse(payable);
        // 本 期 应 补（退）税额 = 应纳税额-税控设备及维修费-本期已缴税额
        DZFDouble realPayable = payable;
        realPayable = SafeCompute.sub(realPayable, taxInfo.getSksb());
        realPayable = SafeCompute.sub(realPayable, taxInfo.getYjse());
        taxInfo.setYbtse(realPayable);

    }

    private List<AddValueTaxVO> getOutTax(TaxCalculateVO taxVO, CorpVO corpVO,
                                          String periodStart, String periodEnd) {
        TaxitemVO[] items = getAllOutTaxitems(corpVO.getChargedeptname());
        Map<String, TaxitemVO> itemMap = getAllOutTaxitemMap(items);
        Map<String, AddValueTaxVO> outMap = getOutTaxFromVoucher(corpVO,
                periodStart, periodEnd, itemMap);
        List<AddValueTaxVO> rs = new ArrayList<>(outMap.values());
        if (rs.size() == 0) {
            rs = getDefaultOutTax(corpVO, items, periodEnd);
        }
        if (!"一般纳税人".equals(corpVO.getChargedeptname())) {
            rs = dealOutTaxSmall(rs);
        }
        Collections.sort(rs);
        return rs;
    }

    private List<AddValueTaxVO> getDefaultOutTax(CorpVO corpVO, TaxitemVO[] items, String period) {
        Set<String> defautCodes = new HashSet<>();
        if ("一般纳税人".equals(corpVO.getChargedeptname())) {
            defautCodes.add(period.compareTo("2019-04") < 0 ? "130" : "138");
            defautCodes.add("105");
        } else {
            defautCodes.add("125");
        }
        List<AddValueTaxVO> list = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            TaxitemVO item = items[i];
            if (defautCodes.contains(item.getTaxcode())) {
                AddValueTaxVO addValueTaxVO = new AddValueTaxVO();
                addValueTaxVO.setTax_name(item.getShortname());
                addValueTaxVO.setTax_type(AddValueTaxVO.TYPE_OUTTAX);
                addValueTaxVO.setSnumber(item.getIorder());
                addValueTaxVO.setRate(item.getTaxratio());
                list.add(addValueTaxVO);
            }
        }
        return list;
    }

    // 查询已保存的进项和销项
    private List<AddValueTaxVO> getSavedOutAndInTax(String pk_corp, String period) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(period);
        AddValueTaxVO[] vos = (AddValueTaxVO[]) singleObjectBO.queryByCondition(AddValueTaxVO.class,
                " pk_corp = ? and nvl(dr,0)=0 and period = ? ", sp);
        return Arrays.asList(vos);
    }

    private void getSavedAddTax(TaxCalculateVO taxVO, String period) {
        AddValueTaxCalVO info = getSavedAddValueTaxInfo(taxVO.getPk_corp(), period);
        if (info == null) {
            return;
        }
        taxVO.setAddtax_info(info);
        List<AddValueTaxVO> addTaxList = getSavedOutAndInTax(taxVO.getPk_corp(), period);
        List<AddValueTaxVO> inTaxList = new ArrayList<>();
        List<AddValueTaxVO> outTaxList = new ArrayList<>();
        for (AddValueTaxVO tax : addTaxList) {
            if (tax.getTax_type() == AddValueTaxVO.TYPE_OUTTAX) {
                outTaxList.add(tax);
            } else {
                inTaxList.add(tax);
            }
        }
        Collections.sort(inTaxList);
        Collections.sort(outTaxList);
        taxVO.setIntax(inTaxList);
        taxVO.setOuttax(outTaxList);
    }

    private void getAddTax(TaxCalculateVO taxVO, CorpVO corpVO, String periodStart, String periodEnd) {
        taxVO.setOuttax(getOutTax(taxVO, corpVO, periodStart, periodEnd));
        if ("一般纳税人".equals(corpVO.getChargedeptname())) {
            List<AddValueTaxVO> inTaxes = getInTax(corpVO, periodStart, periodEnd);
            taxVO.setIntax(inTaxes);
        }
        AddValueTaxCalVO addTax = getAddValueTaxInfo(corpVO, periodStart, periodEnd, taxVO.getOuttax());
        if (taxVO.getAddtax_info() != null) {
            addTax.setPk_tax(taxVO.getAddtax_info().getPk_tax());
        }
        taxVO.setAddtax_info(addTax);
    }

    private Map<String, AddValueTaxVO> getOutTaxFromInvoice(CorpVO corpVO,
                                                            String periodStart, String periodEnd, TaxitemVO[] taxItems,
                                                            Map<String, TaxitemVO> itemMap) {
        StringBuilder sql = new StringBuilder();
        SQLParameter param = new SQLParameter();
        sql.append(
                "select b.bhjje as mny, b.bspse as taxmny, b.bspmc,b.bspsl as rate, ")
                .append("h.iszhuan, m.busitypetempname from ynt_vatsaleinvoice_b b ")
                .append("left join ynt_vatsaleinvoice h on b.pk_vatsaleinvoice = h.pk_vatsaleinvoice ")
                .append("left join ynt_dcmodel_h m on m.pk_model_h = h.pk_model_h")
                .append(" where b.pk_corp = ? and h.period >= ? and h.period <= ? and nvl(b.dr,0)=0");

        param.addParam(corpVO.getPk_corp());
        param.addParam(periodStart);
        param.addParam(periodEnd);
        List<AddValueTaxVO> invoices = (List<AddValueTaxVO>) singleObjectBO
                .executeQuery(sql.toString(), param, new BeanListProcessor(
                        AddValueTaxVO.class));

        Map<String, AddValueTaxVO> outTaxMap = new HashMap<String, AddValueTaxVO>();
        Map<DZFDouble, List<TaxitemVO>> rateMap = getTaxItemRateMap(taxItems);
        for (AddValueTaxVO invoice : invoices) {
            DZFDouble rate = invoice.getRate();
            if (rate != null) {
                rate = rate.div(100);
                invoice.setRate(rate);
                TaxitemVO matchItem = getTaxByInvoice(invoice,
                        rateMap.get(rate), corpVO.getChargedeptname());
                if (matchItem == null) {
                    continue;
                }
                String pk_taxitem = matchItem.getPk_taxitem();
                invoice.setPk_taxitem(pk_taxitem);
                invoice.setFp_style(invoice.getIsZhuan() != null
                        && invoice.getIsZhuan().booleanValue() ? IFpStyleEnum.SPECINVOICE
                        .getValue() : IFpStyleEnum.COMMINVOICE.getValue());
                AddValueTaxVO existVO = outTaxMap.get(pk_taxitem);
                if (existVO == null) {
                    TaxitemVO item = itemMap.get(pk_taxitem);
                    if (item == null) {
                        continue;
                    }
                    existVO = invoice;
                    outTaxMap.put(pk_taxitem, existVO);
                    invoice.setTax_name(item.getShortname());
                    invoice.setTax_type(AddValueTaxVO.TYPE_OUTTAX);
                    invoice.setSnumber(item.getIorder());
                    invoice.setRate(item.getTaxratio());
                }
                if ("一般纳税人".equals(corpVO.getChargedeptname())) {
                    setValueByInvoiceType(existVO, invoice,
                            invoice.getFp_style());
                    invoice.setTaxmny(null);
                    invoice.setMny(null);
                } else {
                    setValueByInvoiceType(existVO, invoice, null);
                }

            }

        }
        return outTaxMap;
    }

    /**
     * 销项
     *
     * @param corpVO
     * @param periodStart
     * @param periodEnd
     * @return
     */
    private Map<String, AddValueTaxVO> getOutTaxFromVoucher(CorpVO corpVO,
                                                            String periodStart, String periodEnd, Map<String, TaxitemVO> itemMap) {
        StringBuilder sql = new StringBuilder();
        sql.append(
                "select sum(mny) mny, sum(taxmny) as taxmny,pk_taxitem, fp_style from ynt_pztaxitem ")
                .append("where pk_corp = ? and period >= ? and period <= ? and nvl(vdirect, 0) = ? and nvl(dr,0) = 0")
                .append(" group by fp_style, pk_taxitem");
        SQLParameter sp = new SQLParameter();
        sp.addParam(corpVO.getPk_corp());
        sp.addParam(periodStart);
        sp.addParam(periodEnd);
        sp.addParam(1);
        List<AddValueTaxVO> outTaxes = (List<AddValueTaxVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(
                        AddValueTaxVO.class));

        Map<String, AddValueTaxVO> outMap = new HashMap<String, AddValueTaxVO>();
        for (AddValueTaxVO addValueTaxVO : outTaxes) {
            String pk_taxitem = addValueTaxVO.getPk_taxitem();
            AddValueTaxVO existVO = outMap.get(pk_taxitem);
            if (existVO == null) {
                TaxitemVO item = itemMap.get(pk_taxitem);
                if (item == null) {
                    continue;
                }
                existVO = addValueTaxVO;
                outMap.put(pk_taxitem, existVO);
                addValueTaxVO.setTax_name(item.getShortname());
                addValueTaxVO.setTax_type(AddValueTaxVO.TYPE_OUTTAX);
                addValueTaxVO.setSnumber(item.getIorder());
                addValueTaxVO.setRate(item.getTaxratio());
            }
            if ("一般纳税人".equals(corpVO.getChargedeptname())) {
                setValueByInvoiceType(existVO, addValueTaxVO,
                        addValueTaxVO.getFp_style());
                addValueTaxVO.setTaxmny(null);
                addValueTaxVO.setMny(null);
            } else {
                setValueByInvoiceType(existVO, addValueTaxVO, null);
            }
        }
        return outMap;
    }

    // 进项
    private List<AddValueTaxVO> getInTax(CorpVO corp, String periodStart,
                                         String periodEnd) {
        return getInTaxFromVoucher(corp, periodStart, periodEnd);
    }

    private List<AddValueTaxVO> getInTaxFromVoucher(CorpVO corp,
                                                    String periodStart, String periodEnd) {
        List<AddValueTaxVO> rs = new ArrayList<AddValueTaxVO>();
        // 本期认证抵扣
        StringBuffer sb = new StringBuffer();
        sb.append(
                "select * from ynt_tzpz_b where pk_corp = ? and nvl(dr, 0) = 0 ")
                .append(" and pk_tzpz_h in (select b.pk_tzpz_h from ynt_tzpz_b b")
                .append(" left join ynt_tzpz_h h on h.pk_tzpz_h = b.pk_tzpz_h")
                .append(" where b.pk_corp = ? and nvl(b.dr, 0) = 0 and b.vcode like ? ")
                .append(" and h.period >= ? and h.period <= ? and b.jfmny <> 0 and nvl(h.fp_style, ?) = ? )");
        String defaultInvoice = "一般纳税人".equals(corp.getChargedeptname()) ? Integer
                .toString(IFpStyleEnum.SPECINVOICE.getValue()) : Integer
                .toString(IFpStyleEnum.COMMINVOICE.getValue());
        String inTaxCode = getNewAccountCode("22210101", corp);
        SQLParameter param = new SQLParameter();
        param.addParam(corp.getPk_corp());
        param.addParam(corp.getPk_corp());
        param.addParam(inTaxCode + "%");
        param.addParam(periodStart);
        param.addParam(periodEnd);
        param.addParam(defaultInvoice);
        param.addParam(IFpStyleEnum.SPECINVOICE.getValue());
        List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(
                sb.toString(), param, new BeanListProcessor(TzpzBVO.class));

        // 份数
        int count = 0;
        AddValueTaxVO inTax = new AddValueTaxVO();
        for (TzpzBVO tzpzBVO : bvos) {
            if (tzpzBVO.getJfmny() != null
                    && tzpzBVO.getJfmny().doubleValue() != 0) {
                if (tzpzBVO.getVcode().startsWith(inTaxCode)) {
                    inTax.setTaxmny_spec(SafeCompute.add(
                            inTax.getTaxmny_spec(), tzpzBVO.getJfmny()));
                    count++;
                } else {
                    inTax.setMny_spec(SafeCompute.add(inTax.getMny_spec(),
                            tzpzBVO.getJfmny()));
                }
            }
        }
        if (count > 0) {
            inTax.setNum_count(count);
        }
        inTax.setSnumber(1);
        inTax.setTax_type(AddValueTaxVO.TYPE_INTAX);
        inTax.setTax_name("本期认证抵扣");
        rs.add(inTax);
        rs.add(getInTaxOtherFromVoucher(corp.getPk_corp(), periodStart,
                periodEnd));
        if (periodEnd.compareTo("2019-04") >= 0) {
            rs.add(getTrafficInTax(corp.getPk_corp(), periodStart,
                    periodEnd));
        }
        return rs;
    }

    // 其他扣税凭证
    private AddValueTaxVO getInTaxOtherFromVoucher(String pk_corp,
                                                   String periodStart, String periodEnd) {
        StringBuilder sql = new StringBuilder();
        sql.append(
                "select count(1) as num_count, sum(mny) mny, sum(taxmny) as taxmny, fp_style from ynt_pztaxitem ")
                .append("where pk_corp = ? and period >= ? and period <= ? and nvl(vdirect, 0) = ? and nvl(dr,0) = 0")
                .append(" and pk_taxitem in ('abcd12345678efaabd000129','abcd12345678efaabd000136', ")
                .append(" 'abcd12345678efaabd000137', 'abcd12345678efaabd000142'");
        if (periodEnd.compareTo("2019-04") >= 0) {
            sql.append(", 'abcd12345678efaabd000143'");
        }
        sql.append(") ")
                .append(" group by fp_style");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(periodStart);
        sp.addParam(periodEnd);
        sp.addParam(0);
        List<AddValueTaxVO> rs = (List<AddValueTaxVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(
                        AddValueTaxVO.class));
        AddValueTaxVO taxVO = new AddValueTaxVO();
        taxVO.setSnumber(2);
        taxVO.setTax_name("其他扣税凭证");
        taxVO.setTax_type(AddValueTaxVO.TYPE_INTAX);
        if (rs != null && rs.size() > 0) {
            for (AddValueTaxVO addValueTaxVO : rs) {
                setValueByInvoiceType(taxVO, addValueTaxVO,
                        addValueTaxVO.getFp_style());
            }
        }
        return taxVO;
    }

    private AddValueTaxVO getTrafficInTax(String pk_corp,
                                          String periodStart, String periodEnd) {
        StringBuilder sql = new StringBuilder();
        sql.append(
                "select count(1) as num_count, sum(mny) mny, sum(taxmny) as taxmny, fp_style from ynt_pztaxitem ")
                .append("where pk_corp = ? and period >= ? and period <= ? and nvl(vdirect, 0) = ? and nvl(dr,0) = 0")
                .append(" and pk_taxitem in ('abcd12345678efaabd000143','abcd12345678efaabd000144') ")
                .append(" group by fp_style");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(periodStart);
        sp.addParam(periodEnd);
        sp.addParam(0);
        List<AddValueTaxVO> rs = (List<AddValueTaxVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(
                        AddValueTaxVO.class));
        AddValueTaxVO taxVO = new AddValueTaxVO();
        taxVO.setSnumber(3);
        taxVO.setTax_name("旅客运输服务扣除凭证");
        taxVO.setTax_type(AddValueTaxVO.TYPE_INTAX);
        if (rs != null && rs.size() > 0) {
            for (AddValueTaxVO addValueTaxVO : rs) {
                setValueByInvoiceType(taxVO, addValueTaxVO,
                        addValueTaxVO.getFp_style());
            }
        }
        return taxVO;
    }

    private List<SurtaxVO> getSurTax(CorpVO corp, String period, String quarterStart,
                                     String quarterEnd, TaxSettingVO setting, boolean reFetch) {
        List<SurtaxVO> taxes = getSavedSurTax(corp.getPk_corp(), period);
        boolean hasSavedData = taxes.size() > 0;
        if (!hasSavedData) {
            taxes = getPreviousPeriodSurTax(corp.getPk_corp(), period);
            if (taxes.size() == 0) {
                // 取预置税种
                SurtaxArchiveVO[] taxArchives = getPresetTaxArchives();
                for (SurtaxArchiveVO archive : taxArchives) {
                    SurtaxVO taxVO = new SurtaxVO();
                    taxVO.setPeriod(period);
                    taxVO.setTax_name(archive.getTax_name());
                    taxVO.setPk_archive(archive.getPk_archive());
                    taxVO.setRate(archive.getRate());
                    taxVO.setIs_surtax(archive.getIs_surtax());
                    taxVO.setSnumber(archive.getShow_order());
                    taxVO.setPk_corp(corp.getPk_corp());
                    taxes.add(taxVO);
                }
            }
        }
        if (!hasSavedData || reFetch) {
            SurTaxTemplate[] temps = querySurtaxTemplate(corp.getPk_corp(),
                    setting.getCityBuildRate(), setting.getLocalEduRate());
            Map<String, SurTaxTemplate> tempMap = DZfcommonTools
                    .hashlizeObjectByPk(Arrays.asList(temps), new String[]{"pk_archive"});

            for (SurtaxVO tax : taxes) {
                SurTaxTemplate temp = tempMap.get(tax.getPk_archive());
                if (temp != null && temp.getRate() != null) {
                    tax.setRate(temp.getRate());
                }
            }
        }
        for (SurtaxVO tax : taxes) {
            if (tax.getIs_surtax() != null && tax.getIs_surtax()) {
                tax.setPeriod_type(setting.getAddTaxPeriodType());
            }
        }
        setSurtaxBaseMny(taxes, corp.getPk_corp(), quarterStart, quarterEnd, reFetch);
        return taxes;
    }

    private List<SurtaxVO> getPreviousPeriodSurTax(String pk_corp, String period) {
        StringBuilder sql = new StringBuilder();
        sql.append("select tax.pk_archive, tax.rate, ach.tax_name, ach.show_order as snumber, ")
                .append(" ach.is_surtax, temp.period_type ")
                .append(" from ynt_taxcal_surtax tax ")
                .append(" left join ynt_taxcal_surtaxarchive ach ")
                .append(" on tax.pk_archive = ach.pk_archive ")
                .append(" left join ynt_taxcal_surtax_template temp ")
                .append(" on temp.pk_corp = ? and tax.pk_archive = temp.pk_archive ")
                .append(" where tax.pk_corp = ? and nvl(tax.dr,0)=0 and tax.period = ( ")
                .append(" select max(period) from ynt_taxcal_surtax where pk_corp = ? and period < ? ")
                .append(" and nvl(dr, 0) = 0 ) ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(pk_corp);
        sp.addParam(pk_corp);
        sp.addParam(period);
        List<SurtaxVO> rs = (List<SurtaxVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(SurtaxVO.class));
        for (SurtaxVO vo : rs) {
            vo.setPk_corp(pk_corp);
            vo.setPeriod(period);
        }
        Collections.sort(rs);
        return rs;
    }

    private List<SurtaxVO> getSavedSurTax(String pk_corp, String period) {
        StringBuilder sql = new StringBuilder();
        sql.append("select tax.*, ach.tax_name, ach.show_order as snumber, ")
                .append(" ach.is_surtax, pz.pzh as voucher_num, temp.period_type, ")
                .append(" temp.pk_base_subject ")
                .append(" from ynt_taxcal_surtax tax ")
                .append(" left join ynt_taxcal_surtaxarchive ach ")
                .append(" on tax.pk_archive = ach.pk_archive ")
                .append(" left join ynt_tzpz_h pz ")
                .append(" on pz.pk_tzpz_h = tax.pk_voucher and nvl(pz.dr,0)=0")
                .append(" left join ynt_taxcal_surtax_template temp ")
                .append(" on temp.pk_corp = ? and tax.pk_archive = temp.pk_archive ")
                .append(" where tax.pk_corp = ? and nvl(tax.dr,0)=0 and tax.period = ? ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(pk_corp);
        sp.addParam(period);
        List<SurtaxVO> rs = (List<SurtaxVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(SurtaxVO.class));
        if (rs.size() > 0) {
            filterSurtax(rs);
            Collections.sort(rs);
        }
        return rs;
    }

    private void filterSurtax(List<SurtaxVO> vos) {
        Map<String, SurtaxVO> map = new HashMap<>();
        Iterator<SurtaxVO> it = vos.iterator();
        while (it.hasNext()) {
            SurtaxVO vo = it.next();
            if (map.containsKey(vo.getPk_archive())) {
                it.remove();
            } else {
                map.put(vo.getPk_archive(), vo);
            }
        }
    }

    private void setSurtaxBaseMny(List<SurtaxVO> taxes,
                                  String pk_corp, String quarterStart, String quarterEnd, boolean reFetch) {
        List<String> subjList = new ArrayList<>();
        for (SurtaxVO tax : taxes) {
            if ((reFetch || tax.getPk_tax() == null) && !StringUtil.isEmpty(tax.getPk_base_subject())) {
                subjList.add(tax.getPk_base_subject());
            }
        }
        if (subjList.size() == 0) {
            return;
        }
        String subjInSql = SQLHelper.getInSQL(subjList);
        StringBuilder sql = new StringBuilder();
        sql.append(" select pzh.period as rq, cp.direction as fx, ")
                .append(" pz.pk_accsubj as pk_km, sum(pz.jfmny) fsjf, sum(pz.dfmny) fsdf ")
                .append(" from ynt_tzpz_b pz ")
                .append(" join ynt_tzpz_h pzh on pzh.pk_corp = ? and pzh.pk_tzpz_h = pz.pk_tzpz_h ")
                .append(" left join ynt_cpaccount cp on pz.pk_accsubj = cp.pk_corp_account ")
                .append(" where pz.pk_corp = ? and pzh.period >= ? and period <= ? and pz.pk_accsubj in ")
                .append(subjInSql)
                .append(" and nvl(pzh.dr,0)=0 and nvl(pz.dr,0)=0")
                .append(" group by cp.direction, pzh.period, pz.pk_accsubj ");

        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(pk_corp);
        sp.addParam(quarterStart);
        sp.addParam(quarterEnd);
        for (String subj : subjList) {
            sp.addParam(subj);
        }
        List<FseJyeVO> fseVOs = (List<FseJyeVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(FseJyeVO.class));
        for (SurtaxVO tax : taxes) {
            if ((reFetch || tax.getPk_tax() == null) && !StringUtil.isEmpty(tax.getPk_base_subject())) {
                if (tax.getPeriod_type() == null || tax.getPeriod_type() == TaxSettingVO.PERIOD_MONTH) {
                    for (FseJyeVO fse : fseVOs) {
                        if (fse.getRq().equals(tax.getPeriod())
                                && fse.getPk_km().equals(tax.getPk_base_subject())) {
                            tax.setBase_tax("0".equals(fse.getFx()) ? fse.getFsjf() : fse.getFsdf());
                            break;
                        }
                    }
                } else {
                    DZFDouble mny = DZFDouble.ZERO_DBL;
                    for (FseJyeVO fse : fseVOs) {
                        if (fse.getPk_km().equals(tax.getPk_base_subject())) {
                            mny = SafeCompute.add(mny, "0".equals(fse.getFx()) ? fse.getFsjf() : fse.getFsdf());
                        }
                    }
                    tax.setBase_tax(mny);
                }
            }

        }
    }

    private AddValueTaxCalVO getAddValueTaxInfo(CorpVO corp,
                                                String periodStart, String periodEnd,
                                                List<AddValueTaxVO> outTaxList) {
//        AddValueTaxCalVO vo = getSavedAddValueTaxInfo(corp.getPk_corp(), periodEnd);
//        if (vo == null) {
//        }
        AddValueTaxCalVO vo;
        if ("一般纳税人".equals(corp.getChargedeptname())) {
            vo = getAddValueTaxInfoGenCorp(corp, periodStart, periodEnd);
        } else {
            vo = getAddValueTaxInfoSmallCorp(corp, periodStart, periodEnd, outTaxList);
        }
        vo.setPk_corp(corp.getPk_corp());
        vo.setPeriod(periodEnd);
        return vo;
    }

    // 增值税-税费计算 一般人
    private AddValueTaxCalVO getAddValueTaxInfoGenCorp(CorpVO corp,
                                                       String periodStart, String periodEnd) {
        // 未交增值税
        String wjzzs;
        // 进项税额转出
        String jxszc;
        // 减免税款
        String jmsk;
        // 已交税金
        String yjsj;
        // 出口退税
        String ckts;
        // 转出多交增值税
        String zcdjzzs;
        // 出口抵减内销产品应纳税额
        String ckdj;
        // 销项税额抵减
        String xxsedj;
        // 税控设备及维护费
        String sksb;
        if ("00000100000000Ig4yfE0005".equals(corp.getCorptype())) {
            wjzzs = "217102";
            jxszc = "21710107";
            jmsk = "21710104";
            yjsj = "21710102";
            ckts = "21710106";
            zcdjzzs = "21710109";
            ckdj = "21710108";
            xxsedj = null;
            sksb = "21710104";
        } else {
            wjzzs = "222109";
            jxszc = "22210104";
            jmsk = "22210106";
            yjsj = "22210107";
            ckts = "22210105";
            zcdjzzs = "22210108";
            ckdj = "22210109";
            xxsedj = "22210110";
            sksb = "22210106";
        }
        List<String> kmList = new ArrayList<>();
        wjzzs = getNewAccountCode(wjzzs, corp);
        kmList.add(wjzzs);
        jxszc = getNewAccountCode(jxszc, corp);
        kmList.add(jxszc);
        jmsk = getNewAccountCode(jmsk, corp);
        kmList.add(jmsk);
        yjsj = getNewAccountCode(yjsj, corp);
        kmList.add(yjsj);
        ckts = getNewAccountCode(ckts, corp);
        kmList.add(ckts);
        zcdjzzs = getNewAccountCode(zcdjzzs, corp);
        kmList.add(zcdjzzs);
        ckdj = getNewAccountCode(ckdj, corp);
        kmList.add(ckdj);
        if (xxsedj != null) {
            xxsedj = getNewAccountCode(xxsedj, corp);
            kmList.add(xxsedj);
        }
        sksb = getNewAccountCode(sksb, corp);
        kmList.add(sksb);
        String[] kms = kmList.toArray(new String[0]);
        QueryParamVO paramvo = ReportUtil
                .getFseQueryParamVO(corp, new DZFDate(periodStart + "-01"),
                        new DZFDate(periodEnd + "-01"), kms, true);
        FseJyeVO[] fsejyevos = zxkjReportService.getFsJyeVOs(paramvo, 1);
        AddValueTaxCalVO taxVO = new AddValueTaxCalVO();
        if (fsejyevos != null && fsejyevos.length > 0) {
            for (FseJyeVO fseVO : fsejyevos) {
                if (wjzzs.equals(fseVO.getKmbm())) {
                    if (fseVO.getQcdf() != null
                            && fseVO.getQcdf().doubleValue() < 0) {
                        taxVO.setSqld(fseVO.getQcdf().multiply(-1));
                    }
                } else if (jxszc.equals(fseVO.getKmbm())) {
                    taxVO.setJxszc(fseVO.getFsdf());
                } else if (jmsk.equals(fseVO.getKmbm())) {
                    taxVO.setSksb(fseVO.getFsjf());
                } else if (yjsj.equals(fseVO.getKmbm())) {
                    taxVO.setYjse(fseVO.getFsjf());
                } else if (ckts.equals(fseVO.getKmbm())) {
                    taxVO.setCkts(fseVO.getFsdf());
                } else if (zcdjzzs.equals(fseVO.getKmbm())) {
                    taxVO.setZcdjzzs(fseVO.getFsdf());
                } else if (ckdj.equals(fseVO.getKmbm())) {
                    taxVO.setCkdj(fseVO.getFsdf());
                } else if (xxsedj != null && xxsedj.equals(fseVO.getKmbm())) {
                    taxVO.setXxdj(fseVO.getFsdf());
                } else if (sksb.equals(fseVO.getKmbm())) {
                    taxVO.setSksb(fseVO.getFsjf());
                }
            }
        }
        return taxVO;
    }

    // 增值税-税费计算 小规模
    private AddValueTaxCalVO getAddValueTaxInfoSmallCorp(CorpVO corp,
                                                         String periodStart, String periodEnd,
                                                         List<AddValueTaxVO> outTaxList) {
        // 税控设备及维护费
        String sksb;
//        String yjsj;
        if ("00000100000000Ig4yfE0005".equals(corp.getCorptype())) {
            sksb = "21710104";
//            yjsj = "21710102";
        } else {
            sksb = "22210106";
//            yjsj = "22210107";
        }
        sksb = getNewAccountCode(sksb, corp);
//        yjsj = getNewAccountCode(yjsj, corp);

        String[] kms = new String[]{sksb};
        QueryParamVO paramvo = ReportUtil
                .getFseQueryParamVO(corp, new DZFDate(periodStart + "-01"),
                        new DZFDate(periodEnd + "-01"), kms, true);
        FseJyeVO[] fsejyevos = zxkjReportService.getFsJyeVOs(paramvo, 1);
        AddValueTaxCalVO taxVO = new AddValueTaxCalVO();
        if (fsejyevos != null && fsejyevos.length > 0) {
            for (FseJyeVO fseVO : fsejyevos) {
                if (sksb.equals(fseVO.getKmbm())) {
                    taxVO.setSksb(fseVO.getFsjf());
                }
//                else if (yjsj.equals(fseVO.getKmbm())) {
//                    taxVO.setYjse(fseVO.getFsjf());
//                }
            }
        }
        DZFDouble paidTax = DZFDouble.ZERO_DBL;
        for (AddValueTaxVO outTax : outTaxList) {
            if (outTax.getFp_style() != null
                    && outTax.getFp_style() == IFpStyleEnum.SPECINVOICE.getValue()) {
                paidTax = SafeCompute.add(paidTax, outTax.getTaxmny());
            }
        }
        if (paidTax.doubleValue() != 0) {
            taxVO.setYjse(paidTax);
        }
        return taxVO;
    }

    private DZFDouble getIncomeMny(CorpVO corpVO, String periodStart, String periodEnd) {
        DZFDouble incomeMny = DZFDouble.ZERO_DBL;
        String incomeSubj1 = null;
        String incomeSubj2 = null;
        if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
            incomeSubj1 = "6001";
            incomeSubj2 = "6051";
        } else {
            incomeSubj1 = "5001";
            incomeSubj2 = "5051";
        }
        String[] kms = new String[]{incomeSubj1, incomeSubj2};
        QueryParamVO paramvo = ReportUtil
                .getFseQueryParamVO(corpVO, new DZFDate(periodStart + "-01"),
                        new DZFDate(periodEnd + "-01"), kms, true);
        FseJyeVO[] vos = zxkjReportService.getFsJyeVOs(paramvo, 1);
        if (vos != null && vos.length > 0) {
            for (FseJyeVO vo : vos) {
                if (incomeSubj1.equals(vo.getKmbm()) || incomeSubj2.equals(vo.getKmbm())) {
                    incomeMny = SafeCompute.add(incomeMny, vo.getFsdf());
                }
            }
        }
        return incomeMny;
    }

    private AddValueTaxCalVO getSavedAddValueTaxInfo(String pk_corp, String period) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(period);
        AddValueTaxCalVO[] vos = (AddValueTaxCalVO[]) singleObjectBO.queryByCondition(AddValueTaxCalVO.class,
                " pk_corp = ? and nvl(dr,0)=0 and period = ? ", sp);
        AddValueTaxCalVO taxVO = null;
        if (vos != null && vos.length > 0) {
            taxVO = vos[0];
        }
        return taxVO;
    }

    private String getNewAccountCode(String code, CorpVO corp) {
        if (corp.getAccountcoderule() == null
                || DZFConstant.ACCOUNTCODERULE
                .equals(corp.getAccountcoderule())) {
            return code;
        } else {
            return KmbmUpgrade.getNewCode(code, DZFConstant.ACCOUNTCODERULE,
                    corp.getAccountcoderule());
        }
    }

    private TaxitemVO getTaxByInvoice(AddValueTaxVO invoice,
                                      List<TaxitemVO> taxItems, String chargename) {
        TaxitemVO item = null;
        if (taxItems == null || taxItems.size() == 0) {
            return item;
        }
        if (taxItems.size() == 1) {
            item = taxItems.get(0);
        } else {
            boolean isService = false;
            if (invoice.getBusitypetempname() != null
                    && ("提供劳务收入".equals(invoice.getBusitypetempname()) || "技术服务费"
                    .equals(invoice.getBusitypetempname()))
                    || invoice.getBspmc() != null
                    && invoice.getBspmc().contains("服务")) {
                isService = true;
            }
            for (TaxitemVO taxitemVO : taxItems) {
                if (isService) {
                    if (taxitemVO.getTaxname().contains("服务")) {
                        item = taxitemVO;
                    }
                } else if (taxitemVO.getTaxname().contains("货物")) {
                    item = taxitemVO;
                }
            }
            if (item == null) {
                item = taxItems.get(0);
            }
        }
        return item;
    }


    private Map<String, TaxitemVO> getAllOutTaxitemMap(TaxitemVO[] items) {
        Map<String, TaxitemVO> map = new HashMap<String, TaxitemVO>();
        for (TaxitemVO taxitemVO : items) {
            map.put(taxitemVO.getPk_taxitem(), taxitemVO);
        }
        return map;
    }

    // 所有销项税目
    private TaxitemVO[] getAllOutTaxitems(String chargename) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(chargename);
        sp.addParam("1");
        sp.addParam("000001");
        String where = " nvl(chargedeptname,'小规模纳税人') = ? and  taxstyle = ? and nvl(dr,0) = 0 and nvl(pk_corp,'000001') = ? ";
        TaxitemVO[] vos = (TaxitemVO[]) singleObjectBO.queryByCondition(
                TaxitemVO.class, where, sp);
        return vos;
    }

    private Map<DZFDouble, List<TaxitemVO>> getTaxItemRateMap(TaxitemVO[] vos) {
        Map<DZFDouble, List<TaxitemVO>> map = new HashMap<DZFDouble, List<TaxitemVO>>();
        for (TaxitemVO taxitemVO : vos) {
            DZFDouble rate = taxitemVO.getTaxratio();
            if (map.containsKey(rate)) {
                map.get(rate).add(taxitemVO);
            } else {
                List<TaxitemVO> list = new ArrayList<TaxitemVO>();
                list.add(taxitemVO);
                map.put(rate, list);
            }
        }
        return map;
    }

    private void setValueByInvoiceType(AddValueTaxVO dest, AddValueTaxVO src,
                                       Integer fp_style) {
        if (fp_style == null) {
            if (dest != src) {
                dest.setMny(SafeCompute.add(dest.getMny(), src.getMny()));
                dest.setTaxmny(SafeCompute.add(dest.getTaxmny(), src.getTaxmny()));
            }
        } else if (fp_style == IFpStyleEnum.SPECINVOICE.getValue()) {
            dest.setMny_spec(SafeCompute.add(dest.getMny_spec(), src.getMny()));
            dest.setTaxmny_spec(SafeCompute.add(dest.getTaxmny_spec(),
                    src.getTaxmny()));
        } else if (fp_style == IFpStyleEnum.COMMINVOICE.getValue()) {
            dest.setMny_gen(SafeCompute.add(dest.getMny_gen(), src.getMny()));
            dest.setTaxmny_gen(SafeCompute.add(dest.getTaxmny_gen(),
                    src.getTaxmny()));
        } else if (fp_style == IFpStyleEnum.NOINVOICE.getValue()) {
            dest.setMny_not(SafeCompute.add(dest.getMny_not(), src.getMny()));
            dest.setTaxmny_not(SafeCompute.add(dest.getTaxmny_not(),
                    src.getTaxmny()));
        }
        if (src.getNum_count() != null) {
            dest.setNum_count(dest.getNum_count() != null ? (dest.getNum_count() + src.getNum_count())
                    : src.getNum_count());
        }
    }

    // 处理小规模纳税人vo
    private List<AddValueTaxVO> dealOutTaxSmall(List<AddValueTaxVO> vos) {
        Map<String, AddValueTaxVO> map = new HashMap<>();
        for (AddValueTaxVO addValueTaxVO : vos) {
            boolean isService = addValueTaxVO.getTax_name().indexOf("服务") == 0;
            String taxName = addValueTaxVO.getTax_name().replaceAll("^服务|货物",
                    "");
            AddValueTaxVO vo = null;
            if (map.containsKey(taxName)) {
                vo = map.get(taxName);
                if (isService) {
                    vo.setMny_service(SafeCompute.add(vo.getMny_service(),
                            addValueTaxVO.getMny()));
                } else {
                    vo.setMny_cargo(SafeCompute.add(vo.getMny_cargo(),
                            addValueTaxVO.getMny()));
                }
                vo.setTaxmny(SafeCompute.add(vo.getTaxmny(),
                        addValueTaxVO.getTaxmny()));
            } else {
                vo = addValueTaxVO;
                vo.setTax_name(taxName);
                if (isService) {
                    vo.setMny_service(addValueTaxVO.getMny());
                } else {
                    vo.setMny_cargo(addValueTaxVO.getMny());
                }
                vo.setMny(null);
                map.put(taxName, vo);
            }
        }
        List<AddValueTaxVO> smallVOs = new ArrayList<AddValueTaxVO>();
        smallVOs.addAll(map.values());
        return smallVOs;
    }

    /**
     * 取凭证和清单较大值
     *
     * @param voucher
     * @param invoice
     * @return 凭证和清单差异
     */
    private List<AddValueTaxDiffVO> mergeVoucherAndInvoice(
            AddValueTaxVO voucher, AddValueTaxVO invoice, String taxpayerNature) {
        List<AddValueTaxDiffVO> diffList = new ArrayList<AddValueTaxDiffVO>();
        AddValueTaxVO dest = voucher == null ? invoice : voucher;
        if ("一般纳税人".equals(taxpayerNature)) {
            // 专票
            int compare = compareDouble(
                    voucher == null ? null : voucher.getMny_spec(),
                    invoice == null ? null : invoice.getMny_spec());
            if (compare != 0) {
                AddValueTaxDiffVO diff = new AddValueTaxDiffVO();
                diff.setPk_taxitem(dest.getPk_taxitem());
                diff.setTax_name(dest.getTax_name() + "-专票");
                if (compare > 0) {
                    dest.setMny_spec(voucher.getMny_spec());
                    dest.setTaxmny_spec(voucher.getTaxmny_spec());
                } else if (compare < 0) {
                    dest.setMny_spec(invoice.getMny_spec());
                    dest.setTaxmny_spec(invoice.getTaxmny_spec());
                }
                if (voucher != null) {
                    diff.setMny_voucher(voucher.getMny_spec());
                }
                if (invoice != null) {
                    diff.setMny_invoice(invoice.getMny_spec());
                }
                diffList.add(diff);
            }

            // 普票
            compare = compareDouble(
                    voucher == null ? null : voucher.getMny_gen(),
                    invoice == null ? null : invoice.getMny_gen());
            if (compare != 0) {
                AddValueTaxDiffVO diff = new AddValueTaxDiffVO();
                diff.setPk_taxitem(dest.getPk_taxitem());
                diff.setTax_name(dest.getTax_name() + "-普票");
                if (compare > 0) {
                    dest.setMny_gen(voucher.getMny_gen());
                    dest.setTaxmny_gen(voucher.getTaxmny_gen());
                } else if (compare < 0) {
                    dest.setMny_gen(invoice.getMny_gen());
                    dest.setTaxmny_gen(invoice.getTaxmny_gen());
                }
                if (voucher != null) {
                    diff.setMny_voucher(voucher.getMny_gen());
                }
                if (invoice != null) {
                    diff.setMny_invoice(invoice.getMny_gen());
                }
                diffList.add(diff);
            }
            // 未开票
            if (voucher != null) {
                dest.setMny_not(voucher.getMny_not());
                dest.setTaxmny_not(voucher.getTaxmny_not());
            }
        } else {
            // 小规模纳税人
            int compare = compareDouble(
                    voucher == null ? null : voucher.getMny(),
                    invoice == null ? null : invoice.getMny());
            if (compare != 0) {
                AddValueTaxDiffVO diff = new AddValueTaxDiffVO();
                diff.setPk_taxitem(dest.getPk_taxitem());
                diff.setTax_name(dest.getTax_name());
                if (compare > 0) {
                    dest.setMny_spec(voucher.getMny());
                    dest.setTaxmny_spec(voucher.getTaxmny());
                } else if (compare < 0) {
                    dest.setMny_spec(invoice.getMny());
                    dest.setTaxmny_spec(invoice.getTaxmny());
                }
                if (voucher != null) {
                    diff.setMny_voucher(voucher.getMny());
                }
                if (invoice != null) {
                    diff.setMny_invoice(invoice.getMny());
                }
                diffList.add(diff);
            }
        }

        return diffList;
    }

    private int compareDouble(DZFDouble val1, DZFDouble val2) {
        if (val1 == null) {
            val1 = DZFDouble.ZERO_DBL;
        }
        if (val2 == null) {
            val2 = DZFDouble.ZERO_DBL;
        }
        return val1.compareTo(val2);
    }

    private void calSumVO(AddValueTaxVO sumVO, List<AddValueTaxVO> list) {
        for (AddValueTaxVO addValueTaxVO : list) {
            sumVO.setMny_spec(SafeCompute.add(sumVO.getMny_spec(),
                    addValueTaxVO.getMny_spec()));
            sumVO.setTaxmny_spec(SafeCompute.add(sumVO.getTaxmny_spec(),
                    addValueTaxVO.getTaxmny_spec()));
            sumVO.setMny_gen(SafeCompute.add(sumVO.getMny_gen(),
                    addValueTaxVO.getMny_gen()));
            sumVO.setTaxmny_gen(SafeCompute.add(sumVO.getTaxmny_gen(),
                    addValueTaxVO.getTaxmny_gen()));
            sumVO.setMny_not(SafeCompute.add(sumVO.getMny_not(),
                    addValueTaxVO.getMny_not()));
            sumVO.setTaxmny_not(SafeCompute.add(sumVO.getTaxmny_not(),
                    addValueTaxVO.getTaxmny_not()));
            if (addValueTaxVO.getNum_count() != null) {
                sumVO.setNum_count(sumVO.getNum_count() != null ? (sumVO.getNum_count() + addValueTaxVO
                        .getNum_count()) : addValueTaxVO.getNum_count());
            }
        }
    }

    private IncomeTaxVO getIncomeTax(CorpVO corp, String period,
                                     TaxSettingVO setting, boolean reFetch) {
        IncomeTaxCalculator incomeCal = new IncomeTaxCalculator(singleObjectBO, zxkjReportService);
        IncomeTaxVO tax = null;
        IncomeTaxVO savedTax = getSavedIncomeTax(corp.getPk_corp(), period);
        if (reFetch || savedTax == null || !isSameIncomeTaxType(savedTax, setting)) {
            tax = incomeCal.getIncomeTaxVO(setting, corp.getPk_corp(), period,
                    setting.getIncomeTaxPeriodType() == 1);
            tax.setPk_corp(corp.getPk_corp());
            tax.setPeriod(period);
            if (savedTax != null) {
                tax.setPk_tax(savedTax.getPk_tax());
                if (setting.getIncomeTaxType() == null
                        || setting.getIncomeTaxType() != 1) {
                    tax.setBzssr(savedTax.getBzssr());
                    tax.setMssr(savedTax.getMssr());
                    tax.setGdzczj(savedTax.getGdzczj());
                    tax.setBzsmssr(savedTax.getBzsmssr());
                }
            }
        } else {
            tax = savedTax;
        }
        return tax;
    }

    private boolean isSameIncomeTaxType(IncomeTaxVO tax, TaxSettingVO setting) {
        int taxType1 = tax.getTax_type() == null ? 0 : tax.getTax_type();
        int taxType2 = setting.getIncomeTaxType() == null ? 0 : setting.getIncomeTaxType();
        int levyType1 = tax.getTax_levy_type() == null ? 1 : tax.getTax_levy_type();
        int levyType2 = setting.getIncomeTaxLevyType() == null ? 1 : setting.getIncomeTaxLevyType();
        return taxType1 == taxType2 && levyType1 == levyType2;

    }

    // 查询已保存所得税
    private IncomeTaxVO getSavedIncomeTax(String pk_corp, String period) {
        String sql = " pk_corp = ? and nvl(dr,0)=0 and period = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(period);
        IncomeTaxVO[] data = (IncomeTaxVO[]) singleObjectBO.queryByCondition(IncomeTaxVO.class, sql, sp);
        IncomeTaxVO taxVO = null;
        if (data != null && data.length > 0) {
            taxVO = data[0];
        }
        return taxVO;
    }

    private CorpTaxVo getCorpTaxInfo(String pk_corp, String period) {
        CorpTaxVo taxInfo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
        TaxEffeHistVO effvo = sys_corp_tax_serv.queryTaxEffHisVO(pk_corp, period);
        taxInfo.setTaxlevytype(effvo.getTaxlevytype());
        taxInfo.setIncomtaxtype(effvo.getIncomtaxtype());
        taxInfo.setIncometaxrate(effvo.getIncometaxrate());
        taxInfo.setSxbegperiod(effvo.getSxbegperiod());
        taxInfo.setSxendperiod(effvo.getSxendperiod());
        return taxInfo;
    }

    // 进项税额转出
    private String getIntaxCode(String corptype) {
        String code = null;
        if ("00000100AA10000000000BMD".equals(corptype)
                || "00000100AA10000000000BMD".equals(corptype)) {
            code = "22210104";
        } else if ("00000100000000Ig4yfE0005".equals(corptype)) {
            code = "21710107";
        }
        return code;
    }

    @Override
    public TaxCalculateVO saveTax(TaxCalculateVO taxCalVO, String pk_corp, String userID)
            throws DZFWarpException {
        String period = taxCalVO.getPeriod();
        String[] quarterRange = getQuarterRange(period);
        if (taxCalVO.getSettings().getAddTaxPeriodType() == TaxSettingVO.PERIOD_MONTH) {
            period = taxCalVO.getPeriod();
        } else {
            period = quarterRange[1];
        }

        saveAddTax(taxCalVO, pk_corp, period, userID);

        // 附加税
        List<SurtaxVO> surtaxes = taxCalVO.getSurtax();
        List<SurtaxVO> toSaveSurtax = new ArrayList<>();
        for (SurtaxVO vo : surtaxes) {
            if (vo.getCarryover() == null || !vo.getCarryover()) {
                toSaveSurtax.add(vo);
            }
        }
        saveSurTax(toSaveSurtax.toArray(new SurtaxVO[0]), pk_corp, period, userID);
        // 所得税
        if (taxCalVO.getSettings().getIncomeTaxPeriodType() == TaxSettingVO.PERIOD_MONTH) {
            period = taxCalVO.getPeriod();
        } else {
            period = quarterRange[1];
        }
        saveIncomeTax(taxCalVO.getIncometax(), pk_corp, period, userID);
        return taxCalVO;
    }

    @Override
    public SurtaxArchiveVO[] getOtherTaxArchives() {
        String sql = " pk_corp = ? and nvl(dr,0) = 0 order by show_order ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(IDefaultValue.DefaultGroup);
        SurtaxArchiveVO[] vos = (SurtaxArchiveVO[]) singleObjectBO
                .queryByCondition(SurtaxArchiveVO.class, sql, sp);
        return vos;
    }

    private SurtaxArchiveVO[] getPresetTaxArchives() {
        String sql = " pk_corp = ? and nvl(dr,0) = 0 and is_preset = '1' order by show_order ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(IDefaultValue.DefaultGroup);
        SurtaxArchiveVO[] vos = (SurtaxArchiveVO[]) singleObjectBO
                .queryByCondition(SurtaxArchiveVO.class, sql, sp);
        return vos;
    }

    @Override
    public SurTaxTemplate[] querySurtaxTemplate(String pk_corp) {
        return querySurtaxTemplate(pk_corp, null, null);
    }

    private SurTaxTemplate[] querySurtaxTemplate(String pk_corp, DZFDouble urbanRate, DZFDouble localEduRate) {
        String sql = " select temp.*, ach.tax_code, ach.tax_name " +
                " from ynt_taxcal_surtax_template temp " +
                " left join ynt_taxcal_surtaxarchive ach " +
                " on temp.pk_archive = ach.pk_archive" +
                " where temp.pk_corp = ? and nvl(temp.dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<SurTaxTemplate> temps = (List<SurTaxTemplate>) singleObjectBO
                .executeQuery(sql, sp, new BeanListProcessor(SurTaxTemplate.class));

        List<SurTaxTemplate> defaultTemps = getDefaultSurtaxTemplate(null,
                temps);
        temps.addAll(defaultTemps);
        if (temps.size() > 0) {
            CorpTaxVo corpTax = null;
            for (SurTaxTemplate temp : temps) {
                if (SurTaxEnum.URBAN_CONSTRUCTION_TAX.getName().equals(temp.getTax_name())) {
                    if (urbanRate != null) {
                        temp.setRate(urbanRate);
                    } else if (corpTax == null) {
                        corpTax = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
                        temp.setRate(corpTax.getCitybuildtax());
                    }

                } else if (SurTaxEnum.LOCAL_EDUCATION_SURTAX.getName().equals(temp.getTax_name())) {
                    if (localEduRate != null) {
                        temp.setRate(localEduRate);
                    } else if (corpTax == null) {
                        corpTax = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
                        temp.setRate(corpTax.getLocaleducaddtax());
                    }
                }
            }
        }
        return temps.toArray(new SurTaxTemplate[0]);
    }

    private SurTaxTemplate getSurtaxTemplateByTax(String pk_archive, String pk_corp) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(pk_archive);
        SurTaxTemplate[] rs = (SurTaxTemplate[]) singleObjectBO.queryByCondition(SurTaxTemplate.class,
                "pk_corp = ? and pk_archive = ? and nvl(dr,0)=0", sp);
        SurTaxTemplate temp = null;
        if (rs.length > 0) {
            temp = rs[0];
        }
        return temp;
    }

    private SurTaxTemplate[] getSurtaxTemplate(String pk_corp, SurtaxVO[] taxes) {
        String sql = " pk_corp = ? and nvl(dr,0) = 0 and pk_tax in ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        SurTaxTemplate[] temps = (SurTaxTemplate[]) singleObjectBO
                .queryByCondition(SurTaxTemplate.class, sql, sp);
        return temps;
    }

    @Override
    public SurTaxTemplate saveSurtaxTemplate(String pk_corp, SurTaxTemplate temp) {
        temp.setPk_corp(pk_corp);
        singleObjectBO.saveObject(pk_corp, temp);
        if (temp.getRate() != null && temp.getRate().doubleValue() != 0) {
            if (SurTaxEnum.URBAN_CONSTRUCTION_TAX.getName().equals(temp.getTax_name())) {
                updateCorpTax(pk_corp, temp.getRate(), null);
            } else if (SurTaxEnum.LOCAL_EDUCATION_SURTAX.getName().equals(temp.getTax_name())) {
                updateCorpTax(pk_corp, null, temp.getRate());
            }
        }
        return temp;
    }

    private void updateCorpTax(String pk_corp, DZFDouble urbanTaxRate, DZFDouble localEduTaxRate) {
        if ((urbanTaxRate == null || urbanTaxRate.doubleValue() == 0)
                && (localEduTaxRate == null || localEduTaxRate.doubleValue() == 0)) {
            return;
        }
        SQLParameter sp = new SQLParameter();

        StringBuilder sql = new StringBuilder();
        sql.append("update bd_corp_tax set ");
        boolean addSplit = false;
        if (urbanTaxRate != null && urbanTaxRate.doubleValue() != 0) {
            sql.append(" citybuildtax = ? ");
            sp.addParam(urbanTaxRate);
            addSplit = true;
        }
        if (localEduTaxRate != null && localEduTaxRate.doubleValue() != 0) {
            if (addSplit) {
                sql.append(",");
            }
            sql.append(" localeducaddtax = ? ");
            sp.addParam(localEduTaxRate);
        }
        sql.append("where pk_corp = ? and nvl(dr,0)=0");
        sp.addParam(pk_corp);
        singleObjectBO.executeUpdate(sql.toString(), sp);
    }

    // 保存增值税
    private void saveAddTax(TaxCalculateVO taxCalVO,
                            String pk_corp, String period, String userID) {
        List<AddValueTaxVO> taxes = new ArrayList<>();
        if (taxCalVO.getOuttax() != null) {
            taxes.addAll(taxCalVO.getOuttax());
        }
        if (taxCalVO.getIntax() != null) {
            taxes.addAll(taxCalVO.getIntax());
        }
        if (taxes.size() > 0) {
            SQLParameter sp = new SQLParameter();
            sp.addParam(pk_corp);
            sp.addParam(period);
            singleObjectBO.executeUpdate(
                    " delete from ynt_taxcal_addtax where pk_corp = ? and period = ? ",
                    sp);
            for (AddValueTaxVO tax : taxes) {
                setDefaultValueBeforeSave(tax, pk_corp, period, userID);
            }
            singleObjectBO.insertVOArr(pk_corp, taxes.toArray(new AddValueTaxVO[0]));
        }

        AddValueTaxCalVO taxInfo = taxCalVO.getAddtax_info();
        if (taxInfo != null) {
            if (StringUtil.isEmpty(taxInfo.getPk_tax())) {
                AddValueTaxCalVO existVO = getSavedAddValueTaxInfo(pk_corp, period);
                if (existVO != null) {
                    taxInfo.setPk_tax(existVO.getPk_tax());
                }
            }
            setDefaultValueBeforeSave(taxInfo, pk_corp, period, userID);
            taxInfo.setModifyDate(new DZFDateTime());
            singleObjectBO.saveObject(pk_corp, taxInfo);
        }
    }

    // 保存附加税
    private void saveSurTax(SurtaxVO[] taxVOs,
                            String pk_corp, String period, String userID) {
        if (taxVOs.length == 0) {
            return;
        }
        DZFDouble urbanRate = null;
        DZFDouble eduRate = null;
        List<String> ids = new ArrayList<>();
        for (SurtaxVO vo : taxVOs) {
            if (SurTaxEnum.URBAN_CONSTRUCTION_TAX.getName().equals(vo.getTax_name())) {
                urbanRate = vo.getRate();
            } else if (SurTaxEnum.LOCAL_EDUCATION_SURTAX.getName().equals(vo.getTax_name())) {
                eduRate = vo.getRate();
            }
            ids.add(vo.getPk_archive());
        }
        String inSql = SQLHelper.getInSQL(ids);
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(period);
        for (String id : ids) {
            sp.addParam(id);
        }
        singleObjectBO.executeUpdate(
                " delete from ynt_taxcal_surtax where pk_corp = ? and period = ? and pk_archive in " + inSql,
                sp);
        for (SurtaxVO tax : taxVOs) {
            setDefaultValueBeforeSave(tax, pk_corp, period, userID);
        }
        singleObjectBO.insertVOArr(pk_corp, taxVOs);
        updateCorpTax(pk_corp, urbanRate, eduRate);

    }

    @Override
    public SurtaxVO saveOtherTax(SurtaxVO taxVO,
                                 String pk_corp, String period, String userID) {
        setDefaultValueBeforeSave(taxVO, pk_corp, period, userID);
        singleObjectBO.saveObject(pk_corp, taxVO);
        return taxVO;
    }

    @Override
    public void updateOtherTaxOnVoucherDelete(String id) throws DZFWarpException {
        if (!StringUtil.isEmpty(id)) {
            SQLParameter sp = new SQLParameter();
            sp.addParam(Boolean.FALSE);
            sp.addParam(id);
            String sql = "update ynt_taxcal_surtax set carryover = ?, pk_voucher = null where pk_tax = ? ";
            singleObjectBO.executeUpdate(sql, sp);
        }
    }

    @Override
    public void updateAddTaxUnCarryover(String pk_corp, String period) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(Boolean.FALSE);
        sp.addParam(pk_corp);
        sp.addParam(period);
        String sql = "update ynt_taxcal_addtax_info set carryover = ? " +
                " where pk_corp = ? and nvl(dr, 0) = 0 and period = ? ";
        singleObjectBO.executeUpdate(sql, sp);
    }

    @Override
    public void updateSurtaxUnCarryover(String pk_corp, String period) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(Boolean.FALSE);
        sp.addParam(pk_corp);
        sp.addParam(period);
        sp.addParam(Boolean.TRUE);
        String sql = "update ynt_taxcal_surtax set carryover = ? " +
                " where pk_corp = ? and nvl(dr, 0) = 0 and period = ? and pk_archive in (" +
                " select pk_archive from ynt_taxcal_surtaxarchive where nvl(dr,0)=0 and is_surtax = ?)";
        singleObjectBO.executeUpdate(sql, sp);
    }

    @Override
    public void updateIncomeTaxUnCarryover(String pk_corp, String period) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(Boolean.FALSE);
        sp.addParam(pk_corp);
        sp.addParam(period);
        String sql = "update ynt_taxcal_incometax set carryover = ? " +
                " where pk_corp = ? and nvl(dr, 0) = 0 and period = ? ";
        singleObjectBO.executeUpdate(sql, sp);
    }

    // 保存所得税
    private void saveIncomeTax(IncomeTaxVO taxVO,
                               String pk_corp, String period, String userID) {
        if (taxVO != null) {
            if (StringUtil.isEmpty(taxVO.getPk_tax())) {
                IncomeTaxVO existVO = getSavedIncomeTax(pk_corp, period);
                if (existVO != null) {
                    taxVO.setPk_tax(existVO.getPk_tax());
                }
            }
            setDefaultValueBeforeSave(taxVO, pk_corp, period, userID);
            taxVO.setModifyDate(new DZFDateTime());
            singleObjectBO.saveObject(pk_corp, taxVO);
        }
    }

    private SurtaxVO queryOtherTax(String pk_corp, String period, String pk_archive) {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(period);
        sp.addParam(pk_archive);
        String sql = " pk_corp = ? and period = ? and nvl(dr,0)=0 and pk_archive = ? ";
        SurtaxVO[] vos = (SurtaxVO[]) singleObjectBO.queryByCondition(SurtaxVO.class, sql, sp);
        SurtaxVO tax = null;
        if (vos != null && vos.length > 0) {
            tax = vos[0];
        }
        return tax;
    }

    private void setDefaultValueBeforeSave(SuperVO vo,
                                           String pk_corp, String period, String userID) {
        vo.setAttributeValue("pk_corp", pk_corp);
        vo.setAttributeValue("period", period);
        vo.setAttributeValue("userID", userID);
    }

    @Override
    public TaxCalculateVO createVoucher(TaxCalculateVO taxCalVO, Integer taxType,
                                        String pk_corp, String userID) throws DZFWarpException {
        CorpVO corpVO = corpService.queryByPk(pk_corp);
        String period = null;
        if (taxType == TaxCalculateVO.TYPE_ADDTAX || taxType == TaxCalculateVO.TYPE_SURTAX) {
            period = taxCalVO.getAddtax_info().getPeriod();
        } else if (taxType == TaxCalculateVO.TYPE_INCOMETAX) {
            period = taxCalVO.getIncometax().getPeriod();
        } else {
            return taxCalVO;
        }
        QmclVO qmvo = gl_qmclserv.queryQmclVO(corpVO.getPk_corp(), period);
        if (qmvo == null) {
            qmvo = createQmclVO(pk_corp, period, userID);
        } else {
            if (taxType == TaxCalculateVO.TYPE_ADDTAX) {
                if (qmvo.getZzsjz() != null && qmvo.getZzsjz().booleanValue()) {
                    throw new BusinessException("增值税已结转，不能重复操作");
                }
            } else if (taxType == TaxCalculateVO.TYPE_SURTAX) {
                if (qmvo.getIsjtsj() != null && qmvo.getIsjtsj().booleanValue()) {
                    throw new BusinessException("附加税已结转，不能重复操作");
                }
            } else if (taxType == TaxCalculateVO.TYPE_INCOMETAX) {
                if (qmvo.getQysdsjz() != null && qmvo.getQysdsjz().booleanValue()) {
                    throw new BusinessException("所得税已结转，不能重复操作");
                }
            }
        }
        TzpzHVO voucher = gl_qmclserv.createVoucherByTaxCalculator(corpVO, taxCalVO, taxType, qmvo, userID);
        if (taxType == TaxCalculateVO.TYPE_ADDTAX) {
            taxCalVO.getAddtax_info().setCarryover(true);
            saveAddTax(taxCalVO, pk_corp, period, userID);
            taxCalVO.getAddValueTaxStatus().setCarryover(true);
            taxCalVO.getAddValueTaxStatus().setSaved(true);
            if (voucher != null) {
                taxCalVO.getAddValueTaxStatus().setVoucherNumber(voucher.getPzh());
                taxCalVO.getAddValueTaxStatus().setVoucherID(voucher.getPk_tzpz_h());
            }
        } else if (taxType == TaxCalculateVO.TYPE_SURTAX) {
            List<SurtaxVO> taxVOs = new ArrayList<>();
            for (SurtaxVO vo : taxCalVO.getSurtax()) {
                if (vo.getIs_surtax() != null && vo.getIs_surtax()) {
                    taxVOs.add(vo);
                }
            }
            createVoucherBySurtax(taxVOs.toArray(new SurtaxVO[0]), qmvo, corpVO, userID);
            if (taxVOs.get(0).getPk_voucher() != null) {
                taxCalVO.getSurtaxStatus().setVoucherNumber(taxVOs.get(0).getVoucher_num());
                taxCalVO.getSurtaxStatus().setVoucherID(taxVOs.get(0).getPk_voucher());
            }
            taxCalVO.getSurtaxStatus().setCarryover(true);
            taxCalVO.getSurtaxStatus().setSaved(true);
            qmvo.setIsjtsj(DZFBoolean.TRUE);
            singleObjectBO.update(qmvo, new String[]{"isjtsj"});
        } else if (taxType == TaxCalculateVO.TYPE_INCOMETAX) {
            taxCalVO.getIncometax().setCarryover(true);
            saveIncomeTax(taxCalVO.getIncometax(), pk_corp, period, userID);
            taxCalVO.getIncomeTaxStatus().setCarryover(true);
            taxCalVO.getIncomeTaxStatus().setSaved(true);
            if (voucher != null) {
                taxCalVO.getIncomeTaxStatus().setVoucherNumber(voucher.getPzh());
                taxCalVO.getIncomeTaxStatus().setVoucherID(voucher.getPk_tzpz_h());
            }
        }
        return taxCalVO;
    }

    @Override
    public SurtaxVO createVoucherByOtherTax(SurtaxVO taxVO, String pk_corp, String userID) throws DZFWarpException {
        SurTaxTemplate temp = getSurtaxTemplateByTax(taxVO.getPk_archive(), pk_corp);
        if (temp == null) {
            throw new BusinessException("未设置模板");
        }
        SurtaxVO savedVO = queryOtherTax(pk_corp, taxVO.getPeriod(), taxVO.getPk_archive());
        if (savedVO != null) {
            if (savedVO.getCarryover() != null && savedVO.getCarryover()) {
                throw new BusinessException("已生成凭证");
            }
            taxVO.setPk_tax(savedVO.getPk_tax());
            processBeforeCreateVoucher(taxVO.getPk_corp(), taxVO.getPeriod(),
                    IBillTypeCode.HP141, taxVO.getPk_tax());
        }
        Map<String, SurTaxTemplate> tempMap = new HashMap<>();
        tempMap.put(temp.getPk_archive(), temp);
        SurtaxVO[] taxVOs = new SurtaxVO[]{taxVO};
        TzpzHVO voucher = getVoucherVO(taxVOs, tempMap, userID);
        if (voucher != null) {
            CorpVO corpVO = corpService.queryByPk(pk_corp);
            gl_tzpzserv.saveVoucher(corpVO, voucher);
            TzpzHVO deductVoucher = getDeductionVoucher(taxVOs, tempMap, corpVO, userID);
            if (deductVoucher != null) {
                gl_tzpzserv.saveVoucher(corpVO, deductVoucher);
            }
            taxVO.setPk_voucher(voucher.getPk_tzpz_h());
            taxVO.setVoucher_num(voucher.getPzh());
            taxVO.setCarryover(true);
        }
        saveOtherTax(taxVO, pk_corp, taxVO.getPeriod(), userID);
        return taxVO;
    }

    private void processBeforeCreateVoucher(String pk_corp, String period, String sourceType, String sourceId) {
        TzpzHVO[] vouchers = queryVoucher(pk_corp, period, sourceType, sourceId);
        if (vouchers != null && vouchers.length > 0) {
            for (TzpzHVO hvo : vouchers) {
                hvo.setIsqxsy(DZFBoolean.TRUE);
                gl_tzpzserv.deleteVoucher(hvo);
            }
        }
    }

    private TzpzHVO[] queryVoucher(String pk_corp, String period, String sourceType, String sourceId) {
        if (StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(period)
                || StringUtil.isEmpty(sourceType)) {
            return null;
        }
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(period);
        sp.addParam(sourceType);
        String sql = " pk_corp = ? and nvl(dr,0) = 0 and period = ?" +
                " and sourcebilltype = ? ";
        if (!StringUtil.isEmpty(sourceId)) {
            sql += " and sourcebillid = ? ";
            sp.addParam(sourceId);
        }
        TzpzHVO[] vos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, sql, sp);
        return vos;
    }

    private SurtaxVO[] createVoucherBySurtax(SurtaxVO[] taxVOs,
                                             QmclVO qmclVO, CorpVO corpVO, String userID) {
        boolean isCarryover = false;
        for (SurtaxVO taxVO : taxVOs) {
            if (taxVO.getCarryover() != null && taxVO.getCarryover()) {
                isCarryover = true;
                break;
            }
        }
        if (isCarryover) {
            return taxVOs;
        }
        processBeforeCreateVoucher(corpVO.getPk_corp(), taxVOs[0].getPeriod(), IBillTypeCode.HP39, null);
        SurTaxTemplate[] temps = querySurtaxTemplate(corpVO.getPk_corp());
        Map<String, SurTaxTemplate> tempMap = DZfcommonTools
                .hashlizeObjectByPk(Arrays.asList(temps), new String[]{"pk_archive"});
        TzpzHVO voucher = getVoucherVO(taxVOs, tempMap, userID);
        if (voucher != null) {
            voucher.setSourcebillid(qmclVO.getPk_qmcl());
            gl_tzpzserv.saveVoucher(corpVO, voucher);
            TzpzHVO deductVoucher = getDeductionVoucher(taxVOs, tempMap, corpVO, userID);
            if (deductVoucher != null) {
                deductVoucher.setSourcebillid(qmclVO.getPk_qmcl());
                gl_tzpzserv.saveVoucher(corpVO, deductVoucher);
            }
        }
        for (SurtaxVO taxVO : taxVOs) {
            taxVO.setCarryover(true);
            if (voucher != null) {
                taxVO.setPk_voucher(voucher.getPk_tzpz_h());
                taxVO.setVoucher_num(voucher.getPzh());
            }
        }
        saveSurTax(taxVOs, corpVO.getPk_corp(), taxVOs[0].getPeriod(), userID);
        return taxVOs;
    }

    private List<SurTaxTemplate> getDefaultSurtaxTemplate(CorpVO corpVO,
                                                          List<SurTaxTemplate> temps) {
        List<SurTaxTemplate> defaultTemps = new ArrayList<>();
        Set<String> codeSet = new HashSet<>();
        for (SurTaxTemplate temp : temps) {
            codeSet.add(temp.getTax_code());
        }
        SurtaxArchiveVO[] taxArchives = getPresetTaxArchives();
        for (SurtaxArchiveVO tax : taxArchives) {
            if (!codeSet.contains(tax.getTax_code())) {
                SurTaxTemplate temp = new SurTaxTemplate();
                /*String debitSubjCode = "";
                String creditSubjCode = "";
                if (SurTaxEnum.URBAN_CONSTRUCTION_TAX.getCode().equals(tax.getTax_code())) {
                    // 城建税
                    if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {
                        debitSubjCode = "5402";
                        creditSubjCode = "217108";
                    } else {
                        debitSubjCode = "00000100AA10000000000BMF".equals(corpVO.getCorptype()) ? "640303" : "540303";
                        creditSubjCode = "222102";
                    }
                } else if (SurTaxEnum.EDUCATION_SURTAX.getCode().equals(tax.getTax_code())) {
                    // 教育费附加
                    if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {
                        debitSubjCode = "640306";
                        creditSubjCode = "222103";
                    } else {
                        debitSubjCode = "00000100AA10000000000BMF".equals(corpVO.getCorptype()) ? "640306" : "540306";
                        creditSubjCode = "222103";
                    }
                } else if (SurTaxEnum.LOCAL_EDUCATION_SURTAX.getCode().equals(tax.getTax_code())) {
                    // 地方教育费附加
                    if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {
                        debitSubjCode = "640307";
                        creditSubjCode = "222104";
                    } else {
                        debitSubjCode = "00000100AA10000000000BMF".equals(corpVO.getCorptype()) ? "640307" : "540307";
                        creditSubjCode = "222104";
                    }
                }*/
                temp.setPk_archive(tax.getPk_archive());
                temp.setSummary(tax.getTax_name());
                temp.setTax_code(tax.getTax_code());
                temp.setTax_name(tax.getTax_name());
                temp.setRate(tax.getRate());
//                temp.setPk_debit_subject(getSubjectByCode(corpVO, debitSubjCode));
//                temp.setPk_credit_subject(getSubjectByCode(corpVO, creditSubjCode));
//                if (temp.getPk_credit_subject() != null && temp.getPk_credit_subject() != null) {
//                }
                defaultTemps.add(temp);
            }
        }
        return defaultTemps;
    }

    private List<TzpzBVO> getVoucerEntries(SurtaxVO[] taxVOs, Map<String, SurTaxTemplate> temps) {
        List<TzpzBVO> entries = new ArrayList<>();
        for (int i = 0; i < taxVOs.length; i++) {
            DZFDouble taxMny = taxVOs[i].getYnse();
            if (taxMny != null && taxMny.doubleValue() > 0) {
                SurTaxTemplate temp = temps.get(taxVOs[i].getPk_archive());
                if (temp == null) {
                    throw new BusinessException(taxVOs[i].getTax_name() + "未设置模板");
                }
                TzpzBVO debitVO = new TzpzBVO();
                debitVO.setPk_accsubj(temp.getPk_debit_subject());
                debitVO.setJfmny(taxMny);
                debitVO.setPk_currency(IGlobalConstants.RMB_currency_id);
                debitVO.setZy(temp.getSummary());
                entries.add(debitVO);
            }
        }
        for (int i = 0; i < taxVOs.length; i++) {
            DZFDouble taxMny = taxVOs[i].getYnse();
            if (taxMny != null && taxMny.doubleValue() > 0) {
                SurTaxTemplate temp = temps.get(taxVOs[i].getPk_archive());
                TzpzBVO creditVO = new TzpzBVO();
                creditVO.setPk_accsubj(temp.getPk_credit_subject());
                creditVO.setDfmny(taxMny);
                creditVO.setPk_currency(IGlobalConstants.RMB_currency_id);
                creditVO.setZy(temp.getSummary());
                entries.add(creditVO);
            }
        }
        return entries;
    }

    private List<TzpzBVO> getDeductionVoucerEntries(SurtaxVO[] taxVOs, Map<String, SurTaxTemplate> temps,
                                                    CorpVO corpVO) {
        List<TzpzBVO> entries = new ArrayList<>();
        for (int i = 0; i < taxVOs.length; i++) {
            DZFDouble taxMny = SafeCompute.add(taxVOs[i].getJmse(), taxVOs[i].getXgmjze());
            if (taxMny != null && taxMny.doubleValue() > 0) {
                SurTaxTemplate temp = temps.get(taxVOs[i].getPk_archive());
                TzpzBVO creditVO = new TzpzBVO();
                creditVO.setPk_accsubj(temp.getPk_credit_subject());
                creditVO.setJfmny(taxMny);
                creditVO.setPk_currency(IGlobalConstants.RMB_currency_id);
                creditVO.setZy(temp.getSummary());
                entries.add(creditVO);
            }
        }
        if (entries.size() > 0) {
            DZFDouble mny = DZFDouble.ZERO_DBL;
            for (TzpzBVO tzpzBVO : entries) {
                mny = mny.add(tzpzBVO.getJfmny());
            }
            TzpzBVO subsidyVo = new TzpzBVO();
            String subsidyKm = null;
            if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())) {
                // 政府补助
                subsidyKm = getSubjectByCode(corpVO, "530104");
            } else if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
                subsidyKm = getSubjectByCode(corpVO, "6117");
                if (subsidyKm == null) {
                    subsidyKm = getSubjectByCode(corpVO, "530104");
                }
            }
            if (subsidyKm != null) {
                subsidyVo.setPk_accsubj(subsidyKm);
                subsidyVo.setDfmny(mny);
                subsidyVo.setPk_currency(IGlobalConstants.RMB_currency_id);
                subsidyVo.setZy("政府补助");
                entries.add(subsidyVo);
            }
        }
        return entries;
    }

    private TzpzHVO getVoucherVO(SurtaxVO[] vos, Map<String, SurTaxTemplate> tempMap, String userid) throws DZFWarpException {
        String period = vos[0].getPeriod();
        String pk_corp = vos[0].getPk_corp();

        List<TzpzBVO> entries = getVoucerEntries(vos, tempMap);
        if (entries.size() < 2) {
            return null;
        }

        // 凭证表头VO
        TzpzHVO pzHeadVO = getVoucherHead(pk_corp, period, userid,
                entries.toArray(new TzpzBVO[0]));
        // 记录来源单据
        boolean isSurtax = vos[0].getIs_surtax() != null && vos[0].getIs_surtax();
        pzHeadVO.setSourcebilltype(isSurtax ? IBillTypeCode.HP39 : IBillTypeCode.HP141);
        pzHeadVO.setSourcebillid(vos[0].getPk_tax());
        return pzHeadVO;
    }

    private TzpzHVO getDeductionVoucher(SurtaxVO[] vos, Map<String, SurTaxTemplate> tempMap,
                                        CorpVO corpVO, String userid) {
        String period = vos[0].getPeriod();
        String pk_corp = corpVO.getPk_corp();
        List<TzpzBVO> entries = getDeductionVoucerEntries(vos, tempMap, corpVO);
        if (entries.size() < 2) {
            return null;
        }
        // 凭证表头VO
        TzpzHVO pzHeadVO = getVoucherHead(pk_corp, period, userid,
                entries.toArray(new TzpzBVO[0]));
        // 记录来源单据
        boolean isSurtax = vos[0].getIs_surtax() != null && vos[0].getIs_surtax();
        pzHeadVO.setSourcebilltype(isSurtax ? IBillTypeCode.HP39 : IBillTypeCode.HP141);
        pzHeadVO.setSourcebillid(vos[0].getPk_tax());
        return pzHeadVO;
    }

    private TzpzHVO getVoucherHead(String pk_corp, String period, String userID, TzpzBVO[] bvos) {
        DZFDouble jfTotal = DZFDouble.ZERO_DBL;
        DZFDouble dfTotal = DZFDouble.ZERO_DBL;
        for (TzpzBVO bvo : bvos) {
            jfTotal = SafeCompute.add(jfTotal, bvo.getJfmny());
            dfTotal = SafeCompute.add(dfTotal, bvo.getJfmny());
        }
        TzpzHVO pzHeadVO = new TzpzHVO();
        pzHeadVO.setChildren(bvos);
        pzHeadVO.setJfmny(jfTotal);
        pzHeadVO.setDfmny(dfTotal);
        pzHeadVO.setPk_corp(pk_corp);
        pzHeadVO.setVyear(Integer.parseInt(period.substring(0, 4)));
        pzHeadVO.setPzlb(0);
        pzHeadVO.setPeriod(period);
        pzHeadVO.setPk_currency(IGlobalConstants.RMB_currency_id);
        pzHeadVO.setCoperatorid(userID);
        pzHeadVO.setIshasjz(DZFBoolean.FALSE);

        DZFDate doperatedate = DateUtils.getPeriodEndDate(period);
        pzHeadVO.setDoperatedate(doperatedate);
        pzHeadVO.setPzh(yntBoPubUtil.getNewVoucherNo(pk_corp, doperatedate));
        pzHeadVO.setVbillstatus(8);
        return pzHeadVO;
    }

    private QmclVO createQmclVO(String pk_corp, String period, String userID) {
        QmclVO qmcl = new QmclVO();
        qmcl.setPk_corp(pk_corp);
        qmcl.setCoperatorid(userID);
        qmcl.setDoperatedate(DateUtils.getPeriodEndDate(period));
        qmcl.setPeriod(period);
        singleObjectBO.saveObject(pk_corp, qmcl);
        return qmcl;
    }

    private String getSubjectByCode(CorpVO corpVO, String code) {
        String rule = corpVO.getAccountcoderule();
        if (rule != null && !DZFConstant.ACCOUNTCODERULE.equals(rule)) {
            code = KmbmUpgrade.getNewCode(code, DZFConstant.ACCOUNTCODERULE, rule);
        }
        StringBuilder sf = new StringBuilder();
        sf.append(" select pk_corp_account from ynt_cpaccount where pk_corp = ? ")
                .append(" and nvl(dr,0) = 0 and isleaf = 'Y' and accountcode like ? order by accountcode");
        SQLParameter sp = new SQLParameter();
        sp.addParam(corpVO.getPk_corp());
        sp.addParam(code + "%");
        String pkSubj = (String) singleObjectBO.executeQuery(sf.toString(), sp,
                new ColumnProcessor());

        return pkSubj;
    }

    private String[] getQuarterRange(String period) {
        String year = period.substring(0, 4);
        int month = Integer.valueOf(period.substring(5, 7));
        int quarter = (month - 1) / 3 + 1;
        int endMonth = quarter * 3;
        int startMonth = endMonth - 2;
        String[] range = new String[2];
        range[0] = year + "-" + (startMonth < 10 ? "0" + startMonth : startMonth);
        range[1] = year + "-" + (endMonth < 10 ? "0" + endMonth : endMonth);
        return range;
    }
}
