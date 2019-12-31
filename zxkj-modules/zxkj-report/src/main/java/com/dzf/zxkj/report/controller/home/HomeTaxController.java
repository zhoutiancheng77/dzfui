package com.dzf.zxkj.report.controller.home;

import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.entity.home.AddTaxInfoVO;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.SystemUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/home")
public class HomeTaxController {
    @Autowired
    private IKMMXZReport gl_rep_kmmxjserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @GetMapping("/addTaxInfo")
    public ReturnData<Json> queryAction(@RequestParam String year, String corpId) {
        Json json = new Json();
        List<AddTaxInfoVO> taxList = new ArrayList<>();
        CorpVO corpVO;
        if (StringUtils.isEmpty(corpId)) {
            corpVO = SystemUtil.getLoginCorpVo();
        } else {
            corpVO = zxkjPlatformService.queryCorpByPk(corpId);
        }
        Map<String, Map<String, FseJyeVO>> periodMap = getYearFseVO(year, corpVO);
        if (periodMap != null) {
            boolean isSmallCorp = !"一般纳税人".equals(corpVO.getChargedeptname());
            periodMap.forEach((period, fseMap) -> {
                AddTaxInfoVO taxInfo = new AddTaxInfoVO();
                taxList.add(taxInfo);
                taxInfo.setPeriod(period);
                DZFDouble taxMnyMonth;
                DZFDouble burdenMonth = DZFDouble.ZERO_DBL;
                DZFDouble burdenTotal = DZFDouble.ZERO_DBL;
                if (isSmallCorp) {
                    DZFDouble outTaxMonth = Optional.ofNullable(fseMap.get("销项税额"))
                            .map(vo -> vo.getFsdf()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble outTaxTotal = Optional.ofNullable(fseMap.get("销项税额"))
                            .map(vo -> vo.getDftotal()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble breakTaxMonth = Optional.ofNullable(fseMap.get("减免税款"))
                            .map(vo -> vo.getFsjf()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble breakTaxTotal = Optional.ofNullable(fseMap.get("减免税款"))
                            .map(vo -> vo.getJftotal()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble mainIncomeMonth = Optional.ofNullable(fseMap.get("主营业务收入"))
                            .map(vo -> vo.getFsdf()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble mainIncomeTotal = Optional.ofNullable(fseMap.get("主营业务收入"))
                            .map(vo -> vo.getDftotal()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble otherIncomeMonth = Optional.ofNullable(fseMap.get("其他业务收入"))
                            .map(vo -> vo.getFsdf()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble otherIncomeTotal = Optional.ofNullable(fseMap.get("其他业务收入"))
                            .map(vo -> vo.getDftotal()).orElse(DZFDouble.ZERO_DBL);
                    taxMnyMonth = outTaxMonth.sub(breakTaxMonth);
                    DZFDouble taxMnyTotal = outTaxTotal.sub(breakTaxTotal);
                    DZFDouble incomeMnyMonth = mainIncomeMonth.add(otherIncomeMonth);
                    DZFDouble incomeMnyTotal = mainIncomeTotal.add(otherIncomeTotal);
                    if (taxMnyMonth.doubleValue() > 0 && incomeMnyMonth.doubleValue() > 0) {
                        burdenMonth = taxMnyMonth.div(incomeMnyMonth).multiply(100)
                                .setScale(2, DZFDouble.ROUND_HALF_UP);
                    }
                    if (taxMnyTotal.doubleValue() > 0 && incomeMnyTotal.doubleValue() > 0) {
                        burdenTotal = taxMnyTotal.div(incomeMnyTotal).multiply(100)
                                .setScale(2, DZFDouble.ROUND_HALF_UP);
                    }
                } else {
                    DZFDouble outTaxMonth = Optional.ofNullable(fseMap.get("销项税额"))
                            .map(vo -> vo.getFsdf()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble inTaxMonth = Optional.ofNullable(fseMap.get("进项税额"))
                            .map(vo -> vo.getFsjf()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble unPayTaxBegin = Optional.ofNullable(fseMap.get("未交增值税"))
                            .map(vo -> vo.getQcdf()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble unPayTaxTotal = Optional.ofNullable(fseMap.get("未交增值税"))
                            .map(vo -> vo.getDftotal()).orElse(DZFDouble.ZERO_DBL)
                            .sub(Optional.ofNullable(fseMap.get("未交增值税"))
                                    .map(vo -> vo.getJftotal()).orElse(DZFDouble.ZERO_DBL));
                    DZFDouble mainIncomeMonth = Optional.ofNullable(fseMap.get("主营业务收入"))
                            .map(vo -> vo.getFsdf()).orElse(DZFDouble.ZERO_DBL);
                    DZFDouble mainIncomeTotal = Optional.ofNullable(fseMap.get("主营业务收入"))
                            .map(vo -> vo.getDftotal()).orElse(DZFDouble.ZERO_DBL);
                    taxMnyMonth = outTaxMonth.sub(inTaxMonth);
                    if (unPayTaxBegin.doubleValue() < 0) {
                        taxMnyMonth = taxMnyMonth.add(unPayTaxBegin);
                    }
                    DZFDouble taxMnyTotal = unPayTaxTotal.doubleValue() < 0 ? DZFDouble.ZERO_DBL : unPayTaxTotal;
                    DZFDouble incomeMnyMonth = mainIncomeMonth;
                    DZFDouble incomeMnyTotal = mainIncomeTotal;
                    if (taxMnyMonth.doubleValue() > 0 && incomeMnyMonth.doubleValue() > 0) {
                        burdenMonth = taxMnyMonth.div(incomeMnyMonth).multiply(100)
                                .setScale(2, DZFDouble.ROUND_HALF_UP);
                    }
                    if (taxMnyTotal.doubleValue() > 0 && incomeMnyTotal.doubleValue() > 0) {
                        burdenTotal = taxMnyTotal.div(incomeMnyTotal).multiply(100)
                                .setScale(2, DZFDouble.ROUND_HALF_UP);
                    }
                    taxInfo.setTaxMny(taxMnyMonth);
                    taxInfo.setMonthBurden(burdenMonth);
                    taxInfo.setAccumulatedBurden(burdenTotal);
                }
                if (taxMnyMonth.doubleValue() < 0) {
                    taxMnyMonth = DZFDouble.ZERO_DBL;
                }
                taxInfo.setTaxMny(taxMnyMonth);
                taxInfo.setMonthBurden(burdenMonth);
                taxInfo.setAccumulatedBurden(burdenTotal);
            });
        }
        taxList.sort(Comparator.comparing(AddTaxInfoVO::getPeriod));
        json.setRows(taxList);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    private Map<String, Map<String, FseJyeVO>> getYearFseVO(String year, CorpVO corpVO) {
        QueryParamVO vo = new QueryParamVO();
        vo.setBegindate1(new DZFDate(year + "-01-01"));
        vo.setEnddate(new DZFDate(year + "-12-01"));
        vo.setPk_corp(corpVO.getPk_corp());
        String accountrule = zxkjPlatformService.queryAccountRule(vo.getPk_corp());
        /** 进项税额 */
        String jxkm = "22210101";
        /** 销项税额 */
        String xxkm = "22210102";
        /** 未交增值税 */
        String wjzzs = "222109";
        /** 减免税款 */
        String jmsk = "22210106";
        if (accountrule.startsWith("4/3/3")) {
            jxkm = "2221001001";
            xxkm = "2221001002";
            wjzzs = "2221009";
            jmsk = "2221001006";
        } else if (accountrule.startsWith("4/3/2")) {
            jxkm = "222100101";
            xxkm = "222100102";
            wjzzs = "2221009";
            jmsk = "222100106";
        }
        String kms = "1001,1002,1012," + jxkm + "," + xxkm + "," + wjzzs
                + "," + jmsk + ",5001,5051,6001,6051";
        vo.setKms(kms);
        vo.setKmcodelist(Arrays.asList(kms.split(",")));

        vo.setCjq(1);
        vo.setCjz(6);
        vo.setIshasjz(DZFBoolean.FALSE);
        vo.setXswyewfs(DZFBoolean.FALSE);
        /** 验证 查询范围应该在当前登录人的权限范围内 */
        DZFDate jzdate = corpVO.getBegindate();
        int jzyear = jzdate.getYear();
        if (jzyear == Integer.valueOf(year)) {
            vo.setBegindate1(jzdate);
        }


        KmMxZVO[] kmmxvos = gl_rep_kmmxjserv.getKMMXZVOs(vo, null);
        Map<String, Map<String, FseJyeVO>> fsePeriodMap = null;
        if (kmmxvos != null && kmmxvos.length > 0) {
            Map<String, List<KmMxZVO>> periodMap = new HashMap<>();
            for (KmMxZVO kmmx : kmmxvos) {
                String period = kmmx.getRq().substring(0, 7);
                List<KmMxZVO> list = periodMap.get(period);
                if (list == null) {
                    list = new ArrayList<>();
                    periodMap.put(period, list);
                }
                list.add(kmmx);
            }
            fsePeriodMap = new HashMap<>();
            for (Map.Entry<String, List<KmMxZVO>> entry : periodMap.entrySet()) {
                String period = entry.getKey();
                List<KmMxZVO> list = entry.getValue();
                Map<String, FseJyeVO> fseMap = new HashMap<>();
                fsePeriodMap.put(period, fseMap);
                for (KmMxZVO kmvo : list) {
                    if (kmvo.getKmbm().equals(jxkm)) {
                        String name = "进项税额";
                        FseJyeVO fsevo = getFseVO(name, fseMap);
                        if ("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setFsdf(kmvo.getDf());
                            fsevo.setFsjf(kmvo.getJf());
                        }
                        if ("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setDftotal(kmvo.getDf());
                            fsevo.setJftotal(kmvo.getJf());
                        }
                    } else if (kmvo.getKmbm().equals(xxkm)) {
                        String name = "销项税额";
                        FseJyeVO fsevo = getFseVO(name, fseMap);
                        if ("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setFsdf(kmvo.getDf());
                            fsevo.setFsjf(kmvo.getJf());
                        }
                        if ("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setDftotal(kmvo.getDf());
                            fsevo.setJftotal(kmvo.getJf());
                        }
                    } else if (kmvo.getKmbm().equals(jmsk)) {
                        String name = "减免税款";
                        FseJyeVO fsevo = getFseVO(name, fseMap);
                        if ("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setFsdf(kmvo.getDf());
                            fsevo.setFsjf(kmvo.getJf());
                        }
                        if ("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setDftotal(kmvo.getDf());
                            fsevo.setJftotal(kmvo.getJf());
                        }
                    } else if (kmvo.getKmbm().equals(wjzzs)) {
                        String name = "未交增值税";
                        FseJyeVO fsevo = getFseVO(name, fseMap);
                        if ("期初余额".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setQcdf(kmvo.getYe());
                        }
                        if ("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setFsdf(kmvo.getDf());
                            fsevo.setFsjf(kmvo.getJf());
                        }
                        if ("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setQmdf(kmvo.getYe());
                            fsevo.setDftotal(kmvo.getDf());
                            fsevo.setJftotal(kmvo.getJf());
                        }
                    } else if ((kmvo.getKmbm().equals("6001") || kmvo.getKmbm().equals("5001"))) {
                        String name = "主营业务收入";
                        FseJyeVO fsevo = getFseVO(name, fseMap);
                        if ("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setFsdf(kmvo.getDf());
                            fsevo.setFsjf(kmvo.getJf());
                        }
                        if ("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setDftotal(kmvo.getDf());
                            fsevo.setJftotal(kmvo.getJf());
                        }
                    } else if ((kmvo.getKmbm().equals("6051") || kmvo.getKmbm().equals("5051"))) {
                        String name = "其他业务收入";
                        FseJyeVO fsevo = getFseVO(name, fseMap);
                        if ("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setFsdf(kmvo.getDf());
                            fsevo.setFsjf(kmvo.getJf());
                        }
                        if ("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)) {
                            fsevo.setDftotal(kmvo.getDf());
                            fsevo.setJftotal(kmvo.getJf());
                        }
                    }
                }
            }
        }
        return fsePeriodMap;
    }

    private FseJyeVO getFseVO(String key, Map<String, FseJyeVO> map) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            FseJyeVO vo = new FseJyeVO();
            map.put(key, vo);
            return vo;
        }
    }
}
