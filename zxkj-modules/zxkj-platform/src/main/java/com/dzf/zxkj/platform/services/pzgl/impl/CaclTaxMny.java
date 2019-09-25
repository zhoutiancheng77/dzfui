package com.dzf.zxkj.platform.services.pzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DZFValueCheck;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.bdset.PZTaxItemRadioVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.util.Combination;
import com.dzf.zxkj.platform.util.SpringUtils;
import com.dzf.zxkj.platform.util.VoUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 根据税额查找金额
 */
@Slf4j
public class CaclTaxMny {
    // 匹配时允许的误差百分比
    private static DZFDouble allow_deviation_ratio = new DZFDouble(0.001);
    public void calcmnyfromtax(TzpzHVO hvo) {
        if (hvo == null)
            return;
        TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
        if (bvos == null || bvos.length == 0)
            return;
        List<TzpzBVO> jflist = new ArrayList<TzpzBVO>();
        List<TzpzBVO> dflist = new ArrayList<TzpzBVO>();
        List<TzpzBVO> taxvo = new ArrayList<TzpzBVO>();
        for (TzpzBVO pzb : bvos) {
            if ("销项税额(与小规模共用)".equals(pzb.getVname())
                    || "销项税额".equals(pzb.getVname())) {//
                taxvo.add(pzb);
            } else if ("进项税额".equals(pzb.getVname())) {
                taxvo.add(pzb);
            } else if (pzb.getVdirect() == 0) {//借方
                jflist.add(pzb);
            } else if (pzb.getVdirect() == 1) {//贷方
                dflist.add(pzb);
            }
        }
        dopzMny(taxvo, jflist, dflist);
    }

    private void dopzMny(List<TzpzBVO> taxvo, List<TzpzBVO> jflist, List<TzpzBVO> dflist) {
        if (taxvo == null || taxvo.size() == 0)
            return;
        for (TzpzBVO vv : taxvo) {
            if ("销项税额(与小规模共用)".equals(vv.getVname())
                    || "销项税额".equals(vv.getVname())) {
                setpzMny1(vv, dflist, false);
            } else if ("进项税额".equals(vv.getVname())) {
                setpzMny1(vv, jflist, true);
            }
        }
    }

    private void setpzMny1(TzpzBVO taxpz, List<TzpzBVO> pzlist, boolean isjf) {
        if (taxpz == null || pzlist == null || pzlist.size() == 0)
            return;
        DZFDouble taxmny = null;
        if (isjf)
            taxmny = taxpz.getJfmny();
        else
            taxmny = taxpz.getDfmny();
        if (taxmny == null || taxmny.doubleValue() == 0)
            return;
        DZFDouble ntaxrate = taxpz.getTaxratio();
        if (ntaxrate == null || ntaxrate.doubleValue() == 0)
            return;
        DZFDouble calcmny = SafeCompute.div(taxmny, ntaxrate);
        DZFDouble mny = null;
        DZFDouble abc = new DZFDouble(9999);
        DZFDouble resultmny = null;
        boolean isgo = false;
        if (pzlist.size() == 1) {
            if (isjf) {
                resultmny = pzlist.get(0).getJfmny();
            } else {
                resultmny = pzlist.get(0).getDfmny();
            }
            taxpz.setTaxcalmny(resultmny);
        } else {
            for (TzpzBVO vv : pzlist) {
                if (isjf) {
                    mny = vv.getJfmny();
                } else {
                    mny = vv.getDfmny();
                }
                DZFDouble result = SafeCompute.sub(calcmny, mny).abs();
                if (abc.compareTo(result) > 0) {
                    abc = result;
                    resultmny = mny;
                }
            }
            if (abc.doubleValue() < 0.02) {
                taxpz.setTaxcalmny(resultmny);
            } else {
                isgo = true;
            }
        }
        //没有计算出来，走方法2
        if (isgo) {
            setpzMny2(taxpz, pzlist, isjf);
        }
    }

    private void setpzMny2(TzpzBVO taxpz, List<TzpzBVO> pzlist, boolean isjf) {
        if (taxpz == null || pzlist == null || pzlist.size() == 0)
            return;
        //多于5个不于计算
        if (pzlist.size() > 5)
            return;
        DZFDouble taxmny = null;
        if (isjf)
            taxmny = taxpz.getJfmny();
        else
            taxmny = taxpz.getDfmny();
        if (taxmny == null || taxmny.doubleValue() == 0)
            return;
        DZFDouble ntaxrate = taxpz.getTaxratio();
        if (ntaxrate == null || ntaxrate.doubleValue() == 0)
            return;
        boolean isnotallow = false;
        String[] source = createSource(pzlist);
        if (source == null || source.length == 0)
            return;
        for (String s : source) {
            int m = Integer.valueOf(s);
            int size = (int) Combination.getCnm(source.length, m);
            for (int i = 0; i < size; i++) {
                String data = Combination.getValue(source, m, i);
                DZFDouble calcmny = isCascadeMny(data, pzlist, ntaxrate, taxmny, isjf);
                if (calcmny != null) {
                    taxpz.setTaxcalmny(calcmny);
                    isnotallow = true;
                    break;
                }
            }
            if (isnotallow) {
                break;
            }
        }
    }

    private DZFDouble isCascadeMny(String data, List<TzpzBVO> pzlist, DZFDouble ntaxrate, DZFDouble taxmny, boolean isjf) {
        if (data == null || data.length() == 0) {
            return null;
        }
        DZFDouble calcmny = null;
        DZFDouble calctaxmny = null;
        for (int i = 0; i < data.length(); i++) {
            if (isjf) {
                DZFDouble jf1 = pzlist.get(Integer.valueOf(String.valueOf(data.charAt(i))) - 1).getJfmny();
                calcmny = SafeCompute.add(jf1, calcmny);
                jf1 = SafeCompute.multiply(jf1, ntaxrate).setScale(2, DZFDouble.ROUND_HALF_UP);
                calctaxmny = SafeCompute.add(calctaxmny, jf1);
            } else {
                DZFDouble df1 = pzlist.get(Integer.valueOf(String.valueOf(data.charAt(i))) - 1).getDfmny();
                calcmny = SafeCompute.add(df1, calcmny);
                df1 = SafeCompute.multiply(df1, ntaxrate).setScale(2, DZFDouble.ROUND_HALF_UP);
                calctaxmny = SafeCompute.add(calctaxmny, df1);
            }
        }
        if (SafeCompute.sub(taxmny, calctaxmny).doubleValue() == 0) {
            return calcmny;
        }
        return null;
    }

    private String[] createSource(List<TzpzBVO> pzlist) {
        if (pzlist == null || pzlist.size() == 0)
            return null;
        //多于5个不于计算
        if (pzlist.size() > 5)
            return null;
        String[] source = null;
        switch (pzlist.size()) {
            case 1: {
                source = new String[]{"1"};
                break;
            }
            case 2: {
                source = new String[]{"1", "2"};
                break;
            }
            case 3: {
                source = new String[]{"1", "2", "3"};
                break;
            }
            case 4: {
                source = new String[]{"1", "2", "3", "4"};
                break;
            }
            case 5: {
                source = new String[]{"1", "2", "3", "4", "5"};
                break;
            }
            default: {
                source = null;
                break;
            }
        }
        return source;
    }

    /**
     * @param taxMap
     * @param bvos
     * @return
     */
    public static List<PZTaxItemRadioVO> calTaxMny(Map<String, List<PZTaxItemRadioVO>> taxMap, TzpzBVO[] bvos) {
        List<PZTaxItemRadioVO> result = new ArrayList<PZTaxItemRadioVO>();
        for (Map.Entry<String, List<PZTaxItemRadioVO>> entry : taxMap.entrySet()) {
            List<PZTaxItemRadioVO> taxList = entry.getValue();
            if ("129".equals(entry.getKey())
                    || "136".equals(entry.getKey())
                    || "137".equals(entry.getKey())) {
                DZFDouble rate = taxList.get(0).getTaxratio();
                DZFDouble backRate = new DZFDouble(1).sub(rate);
                // 农产品收购的进项
                // 计算的金额
                DZFDouble calMny = new DZFDouble(0);
                DZFDouble actualMny = null;
                DZFDouble deviation = new DZFDouble(100);
                for (PZTaxItemRadioVO taxvo : taxList) {
                    DZFDouble mny = SafeCompute.div(taxvo.getMny(), backRate)
                            .setScale(2, DZFDouble.ROUND_HALF_UP);
                    taxvo.setMny(mny);
                    calMny = SafeCompute.add(calMny, mny);
                }
                for (TzpzBVO bvo : bvos) {
                    DZFDouble thisDeviation = SafeCompute.sub(bvo.getDfmny(), calMny).abs();
                    if (thisDeviation.compareTo(deviation) < 0) {
                        deviation = thisDeviation;
                        actualMny = bvo.getDfmny();
                    }
                }
                if (deviation.doubleValue() < 0.1) {
                    // 匹配对应金额
                    int count = taxList.size();
                    DZFDouble actualTax = new DZFDouble(0);
                    for (int i = 0; i < count - 1; i++) {
                        PZTaxItemRadioVO taxvo = taxList.get(i);
                        DZFDouble taxmny = SafeCompute.multiply(taxvo.getMny(), rate)
                                .setScale(2, DZFDouble.ROUND_HALF_UP);
                        actualTax = actualTax.add(taxmny);
                        taxvo.setTaxmny(taxmny);
                        result.add(taxvo);
                    }
                    // 最后一条金额税额消除误差
                    PZTaxItemRadioVO last = taxList.get(count - 1);
                    DZFDouble taxmny = SafeCompute.multiply(actualMny, rate)
                            .setScale(2, DZFDouble.ROUND_HALF_UP).sub(actualTax);
                    last.setTaxmny(taxmny);
                    if (deviation.doubleValue() != 0) {
                        last.setMny(last.getMny().sub(calMny.sub(actualMny)));
                    }
                    result.add(last);
                }
            } else {
                if (taxMap.size() == 1) {
                    // 只有一种税目，从分录获取税额
                    PZTaxItemRadioVO tax0 = taxList.get(0);
                    DZFDouble rate = tax0.getTaxratio();
                    // 科目匹配
                    String regex = tax0.getTaxname().indexOf("进项") > -1 ? "(21710+10+1|22210+10+1)\\d*"
                            : "(21710+10+5|22210+10+2)\\d*";
                    DZFDouble totalTax = DZFDouble.ZERO_DBL;
                    for (TzpzBVO bvo : bvos) {
                        if (bvo.getVcode().matches(regex)
                                && (bvo.getKmmchie().indexOf("进项税额") > -1
                                || bvo.getKmmchie().indexOf("销项税额(与小规模共用)") > -1
                                || bvo.getKmmchie().indexOf("销项税额") > -1)) {
                            totalTax = SafeCompute.add(totalTax, bvo.getVdirect() == 0 ? bvo.getJfmny() : bvo.getDfmny());
                        }
                    }
                    int count = taxList.size();
                    for (int i = 0; i < count - 1; i++) {
                        PZTaxItemRadioVO taxvo = taxList.get(i);
                        DZFDouble taxmny = SafeCompute.multiply(taxvo.getMny(), rate)
                                .setScale(2, DZFDouble.ROUND_HALF_UP);
                        totalTax = totalTax.sub(taxmny);
                        taxvo.setTaxmny(taxmny);
                        result.add(taxvo);
                    }
                    // 最后一条税额
                    PZTaxItemRadioVO last = taxList.get(count - 1);
                    last.setTaxmny(totalTax);
                    result.add(last);
                } else {
                    for (PZTaxItemRadioVO taxvo : taxList) {
                        if (taxvo.getMny() != null) {
                            DZFDouble taxmny = SafeCompute
                                    .multiply(taxvo.getMny(),
                                            taxvo.getTaxratio() == null ? DZFDouble.ZERO_DBL
                                                    : taxvo.getTaxratio())
                                    .setScale(2, DZFDouble.ROUND_HALF_UP);
                            taxvo.setTaxmny(taxmny);
                            result.add(taxvo);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 判断是否需要分析税目
     *
     * @param corp 公司vo
     * @param hvo 凭证
     * @return 是否需要分析税目
     */
    private boolean checkIsTaxVoucher(CorpVO corp, TzpzHVO hvo, String[] rules) {
        if (!"00000100AA10000000000BMD".equals(corp.getCorptype())
                && !"00000100AA10000000000BMF".equals(corp.getCorptype())
                && !"00000100AA10000000000BMQ".equals(corp.getCorptype())
                && !"00000100000000Ig4yfE0003".equals(corp.getCorptype())
                && !"00000100000000Ig4yfE0005".equals(corp.getCorptype())) {
            return false;
        }
        String cargoSubj = rules[0];
        String serviceSubj = rules[1];
        String purchaseSubj = rules[2];
        String trafficSubj = rules[6];
        String inTaxSubj = rules[3];
        String profitSubj = rules[5];
        boolean containsIncome = false;
        boolean containsPurchase = false;
        boolean containsTraffic = false;
        boolean containsInTax = false;
        TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
        for (TzpzBVO bvo : bvos) {
            if (profitSubj != null && bvo.getVcode().matches(profitSubj)) {
                // 本年利润凭证不分析
                return false;
            } else if (bvo.getVcode().matches(cargoSubj)
            || bvo.getVcode().matches(serviceSubj)) {
                containsIncome = true;
            } else if (bvo.getVcode().matches(purchaseSubj)) {
                containsPurchase = true;
            } else if (trafficSubj != null && bvo.getVcode().matches(trafficSubj)) {
                containsTraffic = true;
            } else if (bvo.getVcode().matches(inTaxSubj)) {
                containsInTax = true;
            }
        }
        return "一般纳税人".equals(corp.getChargedeptname())
                && containsInTax && (hvo.getFp_style() != null && hvo.getFp_style() == 1
                 && containsPurchase || containsTraffic)
                || containsIncome;
    }

    public static String checkVoucherTaxItem(CorpVO corp, PZTaxItemRadioVO[] voucherTaxs, TzpzBVO[] bvos) {
        String msg = null;
        DZFDouble itemInTaxSum = DZFDouble.ZERO_DBL;
        DZFDouble itemOutTaxSum = DZFDouble.ZERO_DBL;
        DZFDouble voucherInTaxSum = DZFDouble.ZERO_DBL;
        DZFDouble voucherOutTaxSum = DZFDouble.ZERO_DBL;
        DZFDouble itemInMnySum = DZFDouble.ZERO_DBL;
        DZFDouble itemOutMnySum = DZFDouble.ZERO_DBL;
        String[] rules = getSubjectRule(corp);
        String cargoSubj = rules[0];
        String serviceSubj = rules[1];
        String purchaseSubj = rules[2];
        String trafficSubj = rules[6];
        String inTaxSubj = rules[3];
        String outTaxSubj = rules[4];
        Map<String, DZFDouble> itemMnyMap = new HashMap<>();
        for (PZTaxItemRadioVO item: voucherTaxs) {
            if (itemMnyMap.containsKey(item.getPk_tzpz_b())) {
                itemMnyMap.put(item.getPk_tzpz_b(), SafeCompute.add(itemMnyMap.get(item.getPk_tzpz_b()),
                        item.getMny()));
            } else {
                itemMnyMap.put(item.getPk_tzpz_b(), item.getMny());
            }
            if ("1".equals(item.getTax_style())) {
                itemOutTaxSum = itemOutTaxSum.add(item.getTaxmny());
                itemOutMnySum = itemOutMnySum.add(item.getMny());
            } else if ("2".equals(item.getTax_style())) {
                itemInTaxSum = itemInTaxSum.add(item.getTaxmny());
                itemInMnySum = itemInMnySum.add(item.getMny());
            }
        }
        boolean hasIncome = false;
        boolean hasPurchase = false;
        boolean hasTraffic = false;
        boolean isMnyEqual = true;
        for (TzpzBVO bvo: bvos) {
            if (bvo.getVcode().matches(outTaxSubj)) {
                voucherOutTaxSum = voucherOutTaxSum.add(SafeCompute.sub(bvo.getDfmny(), bvo.getJfmny()));
            } else if (bvo.getVcode().matches(inTaxSubj)) {
                voucherInTaxSum = voucherInTaxSum.add(SafeCompute.sub(bvo.getJfmny(), bvo.getDfmny()));
            } else if (bvo.getVcode().matches(cargoSubj) || bvo.getVcode().matches(serviceSubj)) {
                DZFDouble mny = SafeCompute.sub(bvo.getDfmny(), bvo.getJfmny());
                if (isMnyEqual && itemMnyMap.containsKey(bvo.getPk_tzpz_b())
                        && !itemMnyMap.get(bvo.getPk_tzpz_b()).equals(mny)) {
                    isMnyEqual = false;
                }
                hasIncome = true;
//                voucherOutMnySum = voucherOutMnySum.add(mny);
            } else if (bvo.getVcode().matches(purchaseSubj)) {
                DZFDouble mny = SafeCompute.sub(bvo.getJfmny(), bvo.getDfmny());
                if (isMnyEqual && itemMnyMap.containsKey(bvo.getPk_tzpz_b())
                        && !itemMnyMap.get(bvo.getPk_tzpz_b()).equals(mny)) {
                    isMnyEqual = false;
                }
                hasPurchase = true;
//                voucherInMnySum = voucherInMnySum.add(mny);
            } else if (trafficSubj != null && bvo.getVcode().matches(trafficSubj)) {
                DZFDouble mny = SafeCompute.sub(bvo.getJfmny(), bvo.getDfmny());
                if (isMnyEqual && itemMnyMap.containsKey(bvo.getPk_tzpz_b())
                        && !itemMnyMap.get(bvo.getPk_tzpz_b()).equals(mny)) {
                    isMnyEqual = false;
                }
                hasTraffic = true;
            }
        }

        Integer fpStyle = null;
        if (voucherTaxs.length > 0) {
            fpStyle = voucherTaxs[0].getFp_style();
        }
        boolean isNeedTax = "一般纳税人".equals(corp.getChargedeptname())
                && voucherInTaxSum.doubleValue() != 0 && (fpStyle != null && fpStyle == 1
                && hasPurchase || hasTraffic)
                || hasIncome;
        if (!isNeedTax) {
            msg = "凭证不需要录入税表表项";
        } else if (!isMnyEqual) {
            msg = "凭证表体金额和税表表项金额不符";
        } else if ("一般纳税人".equals(corp.getChargedeptname())
                && hasPurchase && !itemInTaxSum.equals(voucherInTaxSum)
                || hasIncome && !itemOutTaxSum.equals(voucherOutTaxSum)) {
            msg = "凭证表体税额和税表表项税额不符";
        }
        return msg;
    }

    public List<PZTaxItemRadioVO> analyseTaxItem(CorpVO corp, TzpzHVO hvo,
                                                        List<TaxitemVO> taxitems) {
        TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
        String[] rules = getSubjectRule(corp);
        if (!checkIsTaxVoucher(corp, hvo, rules)) {
            return null;
        }
        // 分析错误标识
        hvo.setError_tax_analyse(false);
        Integer fpStyle = hvo.getFp_style();
        if (fpStyle == null) {
            fpStyle = "一般纳税人".equals(corp.getChargedeptname()) ? 2 : 1;
        }
        String cargoSubj = rules[0];
        String serviceSubj = rules[1];
        String purchaseSubj = rules[2];
        String trafficSubj = rules[6];
        String inTaxSubj = rules[3];
        String outTaxSubj = rules[4];
        // 2019 年 4 月 1 日取得旅客运输抵扣发票做账符合规则
        boolean analyseTraffic = hvo.getDoperatedate().compareTo(new DZFDate("2019-04-01")) >= 0;
        // 农产品
        List<PZTaxItemRadioVO> voucherInTaxFarmItems = new LinkedList<>();
        // 旅客交通
        List<PZTaxItemRadioVO> voucherInTaxTrafficItems = new LinkedList<>();
        // 销项
        List<PZTaxItemRadioVO> voucherOutTaxItems = new LinkedList<>();
        List<DZFDouble> inTaxList = new LinkedList<>();
        List<DZFDouble> outTaxList = new LinkedList<>();
        for (int i = 0; i < bvos.length; i++) {
            TzpzBVO bvo = bvos[i];
            PZTaxItemRadioVO voucherTaxItem = null;
            if (bvo.getVcode().matches(outTaxSubj)) {
                outTaxList.add(SafeCompute.sub(bvo.getDfmny(), bvo.getJfmny()));
            } else if (bvo.getVcode().matches(inTaxSubj)) {
                inTaxList.add(SafeCompute.sub(bvo.getJfmny(), bvo.getDfmny()));
            } else if (bvo.getVcode().matches(cargoSubj) || bvo.getVcode().matches(serviceSubj)) {
                voucherTaxItem = new PZTaxItemRadioVO();
                voucherTaxItem.setVdirect(1);
                voucherTaxItem.setCargo(bvo.getVcode().matches(cargoSubj));
                voucherTaxItem.setMny(SafeCompute.sub(bvo.getDfmny(), bvo.getJfmny()));
                voucherOutTaxItems.add(voucherTaxItem);
            } else if (bvo.getVcode().matches(purchaseSubj)) {
                voucherTaxItem = new PZTaxItemRadioVO();
                voucherTaxItem.setVdirect(0);
                voucherTaxItem.setMny(SafeCompute.sub(bvo.getJfmny(), bvo.getDfmny()));
                voucherInTaxFarmItems.add(voucherTaxItem);
            } else if (analyseTraffic && trafficSubj != null && bvo.getVcode().matches(trafficSubj)) {
                voucherTaxItem = new PZTaxItemRadioVO();
                voucherTaxItem.setVdirect(0);
                voucherTaxItem.setMny(SafeCompute.sub(bvo.getJfmny(), bvo.getDfmny()));
                voucherInTaxTrafficItems.add(voucherTaxItem);
            }
            if (voucherTaxItem != null) {
                voucherTaxItem.setFp_style(fpStyle);
                voucherTaxItem.setVcode(bvo.getVcode());
                voucherTaxItem.setEntry_index(i);
            }
        }

        List<PZTaxItemRadioVO> voucherItems = new ArrayList<>();
        // 一般人普票分析农产品进项
        if ("一般纳税人".equals(corp.getChargedeptname()) && inTaxList.size() > 0) {
            if (voucherInTaxTrafficItems.size() > 0) {
                processTrafficItems(hvo, voucherInTaxTrafficItems, inTaxList, taxitems);
                voucherItems.addAll(voucherInTaxTrafficItems);
            } else if (voucherInTaxFarmItems.size() > 0) {
                analyseTax(hvo, voucherInTaxFarmItems, inTaxList, groupTaxItemByRate(taxitems, "2"), true);
                voucherItems.addAll(voucherInTaxFarmItems);
            }
        }
        Map<DZFDouble, List<TaxitemVO>> outputItemMap = groupTaxItemByRate(taxitems, "1");
        if ("一般纳税人".equals(corp.getChargedeptname())) {
            List<TaxitemVO> items = outputItemMap.get(new DZFDouble(0.06));
            adjustTaxItemSequence(corp.getPk_corp(), items);
        }
        // 分析销项
        analyseTax(hvo, voucherOutTaxItems, outTaxList, outputItemMap, false);
        voucherItems.addAll(voucherOutTaxItems);
        return voucherItems;
    }
    private void processTrafficItems(TzpzHVO hvo, List<PZTaxItemRadioVO> voucherTaxItems,
                                     List<DZFDouble> taxList, List<TaxitemVO> taxItems) {
        DZFDouble taxMnyTotal = DZFDouble.ZERO_DBL;
        for (DZFDouble taxMny: taxList) {
            taxMnyTotal = taxMnyTotal.add(taxMny);
        }
        DZFDouble mnyTotal = DZFDouble.ZERO_DBL;
        for (PZTaxItemRadioVO item: voucherTaxItems) {
            mnyTotal = mnyTotal.add(item.getMny());
        }
        DZFDouble calRate = taxMnyTotal.div(mnyTotal);
        DZFDouble rate = calRate.setScale(2, DZFDouble.ROUND_HALF_UP);
        if (voucherTaxItems.size() == 1) {
            voucherTaxItems.get(0).setTaxmny(taxMnyTotal);
            voucherTaxItems.get(0).setTaxratio(rate);
        } else {
            int end = voucherTaxItems.size() - 1;
            for (int i = 0; i < end; i++) {
                PZTaxItemRadioVO item = voucherTaxItems.get(i);
                DZFDouble taxMny = item.getMny().multiply(calRate).setScale(2, DZFDouble.ROUND_HALF_UP);
                item.setTaxmny(taxMny);
                item.setTaxratio(rate);
                taxMnyTotal = taxMnyTotal.sub(taxMny);
            }
            voucherTaxItems.get(end).setTaxmny(taxMnyTotal);
            voucherTaxItems.get(end).setTaxratio(rate);
        }

        TaxitemVO taxItem = null;
        // 普票
        boolean isGen = hvo.getFp_style() != null && hvo.getFp_style() == 1;
        for (TaxitemVO item: taxItems) {
            if (isGen && "143".equals(item.getTaxcode())
                    || !isGen && "144".equals(item.getTaxcode())) {
                taxItem = item;
                break;
            }
        }
        if (taxItem != null) {
            for (PZTaxItemRadioVO voucherTaxItem: voucherTaxItems) {
                voucherTaxItem.setPk_taxitem(taxItem.getPk_taxitem());
                voucherTaxItem.setTaxcode(taxItem.getTaxcode());
                voucherTaxItem.setTaxname(taxItem.getTaxname());
            }
        }
    }
    private Map<DZFDouble, List<TaxitemVO>> groupTaxItemByRate(List<TaxitemVO> taxItems, String taxStyle) {
        Map<DZFDouble, List<TaxitemVO>> taxItemMap = new LinkedHashMap<>();
        for (TaxitemVO taxItem : taxItems) {
            if (!"Y".equals(taxItem.getDef1()) && taxStyle.equals(taxItem.getTaxstyle())) {
                List<TaxitemVO> itemList;
                if (taxItemMap.containsKey(taxItem.getTaxratio())) {
                    itemList = taxItemMap.get(taxItem.getTaxratio());
                } else {
                    itemList = new ArrayList<>();
                    taxItemMap.put(taxItem.getTaxratio(), itemList);
                }
                itemList.add(taxItem);
            }
        }
        return taxItemMap;
    }

    /**
     *
     * @param hvo 凭证vo
     * @param voucherTaxItems 税表表项
     * @param taxList 凭证分录中的税额
     * @param rateMap 税目map
     * @param isIntax 是否进项税
     */
    private void analyseTax(TzpzHVO hvo, List<PZTaxItemRadioVO> voucherTaxItems, List<DZFDouble> taxList,
                            Map<DZFDouble, List<TaxitemVO>> rateMap, boolean isIntax) {
        if (voucherTaxItems.size() == 0) {
            return;
        }
        // 收入分录和税额分录数相同，分别匹配
        if (voucherTaxItems.size() == taxList.size()) {
            matchTaxItemByDividedTax(voucherTaxItems, rateMap, taxList, isIntax);
        }
        DZFDouble taxSum = DZFDouble.ZERO_DBL;
        for (DZFDouble tax : taxList) {
            taxSum = taxSum.add(tax);
        }
        boolean isFind = matchTaxItemBySubject(hvo, voucherTaxItems, rateMap, taxSum, isIntax);
        if (!isFind) {
            matchTaxItemByTotalTax(hvo, voucherTaxItems, rateMap, taxSum, isIntax);
        }
        setTaxItemInfo(voucherTaxItems, rateMap);
    }

    /**
     * 根据金额税率设置税额
     *
     * @param voucherTaxItems 税表表项
     * @param taxSum 税额合计
     * @param isIntax 是否农产品进项税
     */
    private void setTaxMny(List<PZTaxItemRadioVO> voucherTaxItems, DZFDouble taxSum, boolean isIntax) {
        if (voucherTaxItems == null || voucherTaxItems.size() == 0) {
            return;
        }
        int endIndex = voucherTaxItems.size() - 1;
        for (int i = 0; i < endIndex; i++) {
            PZTaxItemRadioVO voucherTaxItem = voucherTaxItems.get(i);
            DZFDouble taxMny;
            if (isIntax) {
                taxMny = voucherTaxItem.getMny().div(1 - voucherTaxItem.getTaxratio().doubleValue())
                        .multiply(voucherTaxItem.getTaxratio())
                        .setScale(2, DZFDouble.ROUND_HALF_UP);
            } else {
                taxMny = voucherTaxItem.getMny().multiply(voucherTaxItem.getTaxratio())
                        .setScale(2, DZFDouble.ROUND_HALF_UP);
            }
            taxSum = taxSum.sub(taxMny);
            voucherTaxItem.setTaxmny(taxMny);
        }
        if (endIndex >= 0) {
            voucherTaxItems.get(endIndex).setTaxmny(taxSum);
        }
    }

    /**
     * 按单条税额匹配税目
     *
     * @param voucherTaxItems 税表表项
     * @param rateMap 税目map
     * @param taxList 税额list
     * @param isInTax 是否农产品进项税
     */
    private void matchTaxItemByDividedTax(List<PZTaxItemRadioVO> voucherTaxItems,
                                          Map<DZFDouble, List<TaxitemVO>> rateMap,
                                          List<DZFDouble> taxList, boolean isInTax) {
        if (taxList.size() == 0) {
            return;
        }
        Set<DZFDouble> rateSet = rateMap.keySet();
        // 直接用金额和税额匹配
        for (PZTaxItemRadioVO voucherTaxItem : voucherTaxItems) {
            DZFDouble mny = voucherTaxItem.getMny();
            Iterator<DZFDouble> taxIt = taxList.iterator();
            taxLoop:
            while (taxIt.hasNext()) {
                DZFDouble tax = taxIt.next();
                for (DZFDouble rate : rateSet) {
                    if (checkRate(mny, rate, tax, isInTax)) {
                        voucherTaxItem.setTaxratio(rate);
                        voucherTaxItem.setTaxmny(tax);
                        taxIt.remove();
                        break taxLoop;
                    }
                }
            }
        }
    }

    // 相同科目使用相同税率匹配
    private boolean matchTaxItemBySubject(TzpzHVO hvo, List<PZTaxItemRadioVO> voucherTaxItems,
                                          Map<DZFDouble, List<TaxitemVO>> rateMap, DZFDouble taxSum, boolean isInTax) {
        boolean isFind = false;
        if (taxSum.doubleValue() == 0) {
            return isFind;
        }
        // 金额合计
        DZFDouble mnySum = DZFDouble.ZERO_DBL;

        List<PZTaxItemRadioVO> filteredVoucherTaxItems = new ArrayList<>();
        Map<String, PZTaxItemRadioVO> groupMap = new LinkedHashMap<>();
        for (PZTaxItemRadioVO voucherTaxItem : voucherTaxItems) {
            if (voucherTaxItem.getTaxmny() == null) {
                filteredVoucherTaxItems.add(voucherTaxItem);
                mnySum = mnySum.add(voucherTaxItem.getMny());
                if (groupMap.containsKey(voucherTaxItem.getVcode())) {
                    PZTaxItemRadioVO gItem = groupMap.get(voucherTaxItem.getVcode());
                    gItem.setMny(gItem.getMny().add(voucherTaxItem.getMny()));
                } else {
                    PZTaxItemRadioVO gItem = new PZTaxItemRadioVO();
                    gItem.setVcode(voucherTaxItem.getVcode());
                    gItem.setMny(voucherTaxItem.getMny());
                    groupMap.put(voucherTaxItem.getVcode(), gItem);
                }
            }
        }
        List<PZTaxItemRadioVO> groupedTaxItems = new ArrayList<>(groupMap.values());
        if (filteredVoucherTaxItems.size() == 0) {
           // 没有未匹配项目
            return isFind;
        }

        // 先用合计金额匹配
        for (DZFDouble rate: rateMap.keySet()) {
            if (checkRate(mnySum, rate, taxSum, isInTax)) {
                for (PZTaxItemRadioVO item : filteredVoucherTaxItems) {
                    item.setTaxratio(rate);
                }
                isFind = true;
                break;
            }
        }

        if (!isFind) {
            if (groupedTaxItems.size() == filteredVoucherTaxItems.size()) {
                // 没有同科目情况，直接返回
                return isFind;
            }
            Map<Integer, DZFDouble> matchTaxMap = new HashMap<>();
            if (groupedTaxItems.size() <= 3) {
                try {
                    isFind = findRateCombo(groupedTaxItems, matchTaxMap, rateMap.keySet(),
                            DZFDouble.ZERO_DBL, taxSum,
                            0, isInTax);
                } catch (Exception e) {
                    log.error("寻找税率组合失败", e);
                }
            }
            if (isFind) {
                for (int i = 0; i < groupedTaxItems.size(); i++) {
                    groupedTaxItems.get(i).setTaxratio(matchTaxMap.get(i));
                }
                for (PZTaxItemRadioVO voucherTaxItem : filteredVoucherTaxItems) {
                    voucherTaxItem.setTaxratio(groupMap.get(voucherTaxItem.getVcode()).getTaxratio());
                }
            }
        }
        if (isFind) {
            setTaxMny(filteredVoucherTaxItems, taxSum, isInTax);
            hvo.setError_tax_analyse(!isFind);
        }
        return isFind;
    }

    /**
     * 根据税额合计匹配税目
     *
     * @param hvo 凭证vo
     * @param voucherTaxItems 税表表项
     * @param rateMap 税目map
     * @param taxSum 税额合计
     * @param isInTax 是否农产品进项税
     */
    private void matchTaxItemByTotalTax(TzpzHVO hvo, List<PZTaxItemRadioVO> voucherTaxItems,
                                        Map<DZFDouble, List<TaxitemVO>> rateMap,
                                        DZFDouble taxSum, boolean isInTax) {
        List<PZTaxItemRadioVO> filteredVoucherTaxItems = new ArrayList<>();
        for (PZTaxItemRadioVO voucherTaxItem : voucherTaxItems) {
            if (voucherTaxItem.getTaxmny() == null) {
                filteredVoucherTaxItems.add(voucherTaxItem);
            }
        }
        if (filteredVoucherTaxItems.size() > 0) {
            Set<DZFDouble> rateSet = rateMap.keySet();
            // 是否找到匹配的税率
            boolean isFind = false;
            if (taxSum.doubleValue() == 0) {
                if (rateSet.contains(DZFDouble.ZERO_DBL)) {
                    isFind = true;
                    // 税额为0，设置所有税率为0
                    for (PZTaxItemRadioVO item : filteredVoucherTaxItems) {
                        item.setTaxratio(DZFDouble.ZERO_DBL);
                    }
                } else {
                    // 没有税率为0的税目
                    isFind = false;
                    for (PZTaxItemRadioVO item: filteredVoucherTaxItems) {
                        voucherTaxItems.remove(item);
                    }
                    filteredVoucherTaxItems = null;
                }
            } else {
                DZFDouble calTaxSum = DZFDouble.ZERO_DBL;
                Map<Integer, DZFDouble> matchTaxMap = new HashMap<>();
                if (filteredVoucherTaxItems.size() <= 5) {
                    try {
                        isFind = findRateCombo(filteredVoucherTaxItems, matchTaxMap, rateSet, calTaxSum, taxSum,
                                0, isInTax);
                    } catch (Exception e) {
                        log.error("寻找税率组合失败", e);
                    }
                }
                if (isFind) {
                    for (int i = 0; i < filteredVoucherTaxItems.size(); i++) {
                        filteredVoucherTaxItems.get(i).setTaxratio(matchTaxMap.get(i));
                    }
                } else if (isInTax) {
                    // 农产品进项严格匹配，不设默认，移除没匹配到的项目
                    for (PZTaxItemRadioVO item: filteredVoucherTaxItems) {
                        voucherTaxItems.remove(item);
                    }
                    return;
                } else {
                    DZFDouble mnySum = DZFDouble.ZERO_DBL;
                    for (PZTaxItemRadioVO item : filteredVoucherTaxItems) {
                        mnySum = mnySum.add(item.getMny());
                    }
                    DZFDouble calRate;
                    if (isInTax) {
                        calRate = taxSum.div(mnySum.add(taxSum));
                    } else {
                        calRate = taxSum.div(mnySum);
                    }
                    DZFDouble similarRate = getSimilarRate(rateMap, calRate);
                    for (PZTaxItemRadioVO item : filteredVoucherTaxItems) {
                        item.setTaxratio(similarRate);
                    }
                }
            }
            setTaxMny(filteredVoucherTaxItems, taxSum, isInTax);
            hvo.setError_tax_analyse(!isFind);
        } else {
            hvo.setError_tax_analyse(false);
        }
    }

    /**
     * 设置凭证税表表项的税目信息
     *
     * @param voucherTaxItems 税表表项
     * @param rateMap 税目map
     */
    private void setTaxItemInfo(List<PZTaxItemRadioVO> voucherTaxItems,
                                Map<DZFDouble, List<TaxitemVO>> rateMap) {
        for (PZTaxItemRadioVO voucherTaxItem : voucherTaxItems) {
            // 根据税率等匹配税目
            TaxitemVO taxItem = matchTaxItem(rateMap, voucherTaxItem.getTaxratio(), voucherTaxItem.isCargo(),
                    voucherTaxItem.getFp_style(), true);
            if (taxItem != null) {
                voucherTaxItem.setPk_taxitem(taxItem.getPk_taxitem());
                voucherTaxItem.setTaxcode(taxItem.getTaxcode());
                voucherTaxItem.setTaxname(taxItem.getTaxname());
            }
        }
    }

    /**
     * 寻找符合税额合计的税率组合
     *
     * @param voucherTaxItems 税表表项
     * @param matchTaxMap 税率组合
     * @param rateSet 所有的税率
     * @param calTaxSum 组合计算的税额合计
     * @param taxSum 税额合计
     * @param index 税表表项索引
     * @param isInTax 是否农产品进项税
     * @return 是否有匹配的组合
     */
    private boolean findRateCombo(List<PZTaxItemRadioVO> voucherTaxItems, Map<Integer, DZFDouble> matchTaxMap,
                                  Set<DZFDouble> rateSet, DZFDouble calTaxSum, DZFDouble taxSum,
                                  int index, boolean isInTax) {
        PZTaxItemRadioVO voucherTaxItem = voucherTaxItems.get(index);
        DZFDouble mny = voucherTaxItem.getMny();
        if (index == voucherTaxItems.size() - 1) {
            for (DZFDouble rate : rateSet) {
                matchTaxMap.put(index, rate);
                DZFDouble taxMny;
                if (isInTax) {
                    taxMny = mny.div(1 - rate.doubleValue()).multiply(rate).setScale(2, DZFDouble.ROUND_HALF_UP);
                } else {
                    taxMny = mny.multiply(rate).setScale(2, DZFDouble.ROUND_HALF_UP);
                }
                if (isConformValue(calTaxSum.add(taxMny), taxSum, isInTax)) {
                    return true;
                }
            }
        } else {
            for (DZFDouble rate : rateSet) {
                matchTaxMap.put(index, rate);
                DZFDouble taxMny;
                if (isInTax) {
                    taxMny = mny.div(1 - rate.doubleValue()).multiply(rate).setScale(2, DZFDouble.ROUND_HALF_UP);
                } else {
                    taxMny = mny.multiply(rate).setScale(2, DZFDouble.ROUND_HALF_UP);
                }
                if (findRateCombo(voucherTaxItems, matchTaxMap, rateSet, calTaxSum.add(taxMny), taxSum,
                        index + 1, isInTax)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 匹配税目
     *
     * @param rateMap 税目map
     * @param rate 税率
     * @param isCargo 是否货物
     * @param fpStyle 发票类型
     * @param isExact 是否精确匹配
     * @return 税目
     */
    private TaxitemVO matchTaxItem(Map<DZFDouble, List<TaxitemVO>> rateMap,
                                   DZFDouble rate, boolean isCargo, Integer fpStyle, boolean isExact) {
        if (!isExact) {
            // 非精确匹配，找接近的税率
            rate = getSimilarRate(rateMap, rate);
        }
        TaxitemVO taxItem = null;
        List<TaxitemVO> items = rateMap.get(rate);
        if (items == null) {
            return null;
        } else if (items.size() == 1) {
            taxItem = items.get(0);
        } else {
            TaxitemVO matchedInvoiceStyle = null;
            for (TaxitemVO item : items) {
                if (fpStyle == null || item.getFp_style() == null
                        || fpStyle.equals(item.getFp_style())) {
                    if (matchedInvoiceStyle == null) {
                        matchedInvoiceStyle = item;
                    }
                    if (isCargo && item.getShortname().contains("货物")
                            || !isCargo && !item.getShortname().contains("货物")) {
                        taxItem = item;
                        break;
                    }
                }
            }
            if (taxItem == null) {
                // 没有找到完全相符的，默认取第一个
                taxItem = matchedInvoiceStyle == null ? items.get(0) : matchedInvoiceStyle;
            }
        }
        return taxItem;
    }

    /**
     * 
     * @param corpvo  公司
     * @param acccode 科目编码
     * @param taxItems 税目表
     * @param taxStyle 销进项标识  1  --销项  2 -进项
     * @param rate  税率
     * @param fpStyle 发票类型 
     * @return
     */
    public TaxitemVO matchTaxItem(CorpVO corpvo ,String acccode,List<TaxitemVO> taxItems,String taxStyle,
            DZFDouble rate, Integer fpStyle) {
    	
    	if(DZFValueCheck.isEmpty(taxItems)){
    		return null;
    	}
    	Map<DZFDouble, List<TaxitemVO>> rateMap =groupTaxItemByRate(taxItems, taxStyle);
    	String[] rules =getSubjectRule(corpvo);
    	boolean isCargo = acccode.matches(rules[0]); 
    	return matchTaxItem(rateMap, rate,isCargo, fpStyle,false);
    }
    /**
     * 获取最接近的税率
     *
     * @param rateMap 税目map
     * @param rate 税率
     * @return 接近的税率
     */
    private DZFDouble getSimilarRate(Map<DZFDouble, List<TaxitemVO>> rateMap, DZFDouble rate) {
        // 非精确匹配，找接近的税率
        DZFDouble diff = null;
        DZFDouble similarRate = null;
        for (DZFDouble fRate : rateMap.keySet()) {
            DZFDouble thisDiff = fRate.sub(rate).abs();
            if (diff == null || thisDiff.compareTo(diff) < 0) {
                diff = thisDiff;
                similarRate = fRate;
            }
        }
        return similarRate;
    }


    private boolean checkRate(DZFDouble mny, DZFDouble rate, DZFDouble taxMny, boolean isInTax) {
        mny = VoUtils.getDZFDouble(mny);
        rate = VoUtils.getDZFDouble(rate);
        taxMny = VoUtils.getDZFDouble(taxMny);
        boolean isMatch;
        if (isInTax) {
            isMatch = isConformValue(mny.div(1 - rate.doubleValue()).multiply(rate)
                    .setScale(2, DZFDouble.ROUND_HALF_UP), taxMny, isInTax);;
        } else {

            isMatch = isConformValue(mny.multiply(rate).setScale(2, DZFDouble.ROUND_HALF_UP), taxMny, isInTax);
        }
        return isMatch;
    }

    /**
     * 对应科目的编码正则规则
     *
     * @param corp 公司信息
     * @return 销售科目 服务科目 购买农产品 进项科目 销项科目
     */
    public static String[] getSubjectRule(CorpVO corp) {
        String corpType = corp.getCorptype();
        String codeRule = corp.getAccountcoderule();
        if (codeRule == null) {
            codeRule = DZFConstant.ACCOUNTCODERULE;
        }
        String[] levelNums = codeRule.split("/");
        // 二级科目
        int twoLevelZeroNum = Integer.valueOf(levelNums[1]) - 1;
        int threeLevelZeroNum = Integer.valueOf(levelNums[2]) - 1;
        // 购买农产品
        String purchaseSubj = null;
        // 旅客运输
        String trafficSubj = null;
        // 销售科目
        String cargoSubj = null;
        // 服务科目
        String serviceSubj = null;
        // 进项科目
        String inTaxSubj = null;
        // 销项科目
        String outTaxSubj = null;
        // 本年利润
        String profitSubj = null;
        switch (corpType) {
            // 小企业会计准则
            case "00000100AA10000000000BMD":
                purchaseSubj = "^(1403|1405).*";
                trafficSubj = "^(56010{" + (twoLevelZeroNum - 1) + "}13" +
                        "|56020{" + (twoLevelZeroNum - 1) + "}210{" + threeLevelZeroNum + "}2" +
                        "|56020{" + (twoLevelZeroNum - 1) + "}14).*";
                cargoSubj = "^(5001|5051)0{" + twoLevelZeroNum + "}1.*";
                serviceSubj = "^(5001|5051)0{" + twoLevelZeroNum + "}[^1].*";
                inTaxSubj = "22210{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}1.*";
                outTaxSubj = "22210{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}2.*";
                profitSubj = "^3103.*";
                break;
            // 企业会计准则
            case "00000100AA10000000000BMF":
                purchaseSubj = "^(1403|1405).*";
                trafficSubj = "^(66010{" + (twoLevelZeroNum - 1) + "}13" +
                        "|66020{" + (twoLevelZeroNum - 1) + "}200{" + threeLevelZeroNum + "}1" +
                        "|66020{" + (twoLevelZeroNum - 1) + "}13).*";
                cargoSubj = "^(6001|6051)0{" + twoLevelZeroNum + "}1.*";
                serviceSubj = "^(6001|6051)0{" + twoLevelZeroNum + "}[^1].*";
                inTaxSubj = "22210{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}1.*";
                outTaxSubj = "22210{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}2.*";
                profitSubj = "^4103.*";
                break;
            // 民间非营利组织会计制度
            case "00000100AA10000000000BMQ":
                purchaseSubj = "^1201.*";
                trafficSubj = "^5201.*";
                cargoSubj = "^4501.*";
                serviceSubj = "^(4301|4901).*";
                inTaxSubj = "22060{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}1.*";
                outTaxSubj = "22060{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}2.*";
                break;
            // 事业单位会计制度
            case "00000100000000Ig4yfE0003":
                purchaseSubj = "^1301.*";
                cargoSubj = "^(4401|4101).*";
                serviceSubj = "^4501.*";
                inTaxSubj = "21010{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}1.*";
                outTaxSubj = "21010{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}2.*";
                break;
            // 企业会计制度
            case "00000100000000Ig4yfE0005":
                purchaseSubj = "^(1211|1243).*";
                trafficSubj = "^(55010{" + (twoLevelZeroNum - 1) + "}13" +
                        "|55020{" + (twoLevelZeroNum - 1) + "}200{" + threeLevelZeroNum + "}1" +
                        "|55020{" + (twoLevelZeroNum - 1) + "}13).*";
                cargoSubj = "^(5101|5102)0{" + twoLevelZeroNum + "}1.*";
                serviceSubj = "^(5101|5102)0{" + twoLevelZeroNum + "}[^1].*";
                inTaxSubj = "21710{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}1.*";
                outTaxSubj = "21710{" + twoLevelZeroNum + "}10{" + threeLevelZeroNum + "}5.*";
                profitSubj = "^3131.*";
                break;
            default:
                break;
        }
        return new String[]{ cargoSubj, serviceSubj, purchaseSubj, inTaxSubj,
                outTaxSubj, profitSubj, trafficSubj };
    }

    private static boolean isConformValue(DZFDouble val1, DZFDouble val2, boolean isIntax) {
        DZFDouble allow_deviation = val2.abs().multiply(allow_deviation_ratio);
        DZFDouble minDeviation = new DZFDouble(0.01);
        if (allow_deviation.compareTo(minDeviation) < 0) {
            allow_deviation = isIntax ? minDeviation : DZFDouble.ZERO_DBL;
        }
        return val1.sub(val2).abs().compareTo(allow_deviation) <= 0;
    }

    /**
     * 最近使用的一般、简易6%税目
     *
     * @param pk_corp
     * @return
     */
    public static String getTaxItemPercent6(String pk_corp) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from (")
                .append("select pk_taxitem from ynt_pztaxitem t ")
                .append(" where t.pk_corp = ? and user_save='1' ")
                .append(" and pk_taxitem in ('abcd12345678efaabd000105','abcd12345678efaabd000106') order by ts desc ")
                .append(") where rownum = 1");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
        String itemId = (String) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor());
        if (itemId == null) {
            // 默认一般6%
            itemId = "abcd12345678efaabd000105";
        }
        return itemId;
    }

    /**
     * 调整税目顺序
     *
     * @param pk_corp
     * @param items
     */
    public static void adjustTaxItemSequence(String pk_corp, List<TaxitemVO> items) {
        if (items == null || items.size() == 0) {
            return;
        }
        String latestItemId = getTaxItemPercent6(pk_corp);
        if (latestItemId != null) {
            String itemId2 = latestItemId.equals("abcd12345678efaabd000105")
                    ? "abcd12345678efaabd000106" : "abcd12345678efaabd000105";
            int prevIndex = -1;
            for (int i = 0; i < items.size(); i++) {
                if (latestItemId.equals(items.get(i).getPk_taxitem())) {
                    if (prevIndex > -1) {
                        TaxitemVO prevItem = items.get(prevIndex);
                        items.set(prevIndex, items.get(i));
                        items.set(i, prevItem);
                    }
                    break;
                } else if (itemId2.equals(items.get(i).getPk_taxitem())) {
                    prevIndex = i;
                }
            }
        }
    }
}