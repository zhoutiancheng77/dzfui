package com.dzf.zxkj.report.utils;


import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.vo.cwbb.ZcFzBVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 民间非盈利组织
 *
 * @author zhangj
 */
@SuppressWarnings("all")
public class PopularRpForZcfzImpl {

    private IZxkjPlatformService zxkjPlatformService;


    public PopularRpForZcfzImpl(IZxkjPlatformService zxkjPlatformService) {
        this.zxkjPlatformService = zxkjPlatformService;
    }

    public ZcFzBVO[] getPopularZCFZBVOs(Map<String, FseJyeVO> map, String[] hasyes, Map<String, YntCpaccountVO> mapc, String pk_corp) throws Exception {
        ZcFzBVO votemp = new ZcFzBVO();

        ZcFzBVO vo0 = new ZcFzBVO();
        vo0.setZc("流动资产：");
        vo0.setFzhsyzqy("流动负债：");


        ZcFzBVO vo1 = new ZcFzBVO();
        vo1 = getZCFZBVO(map, vo1, true, "1001", "1002", "1009"); //1001（借）+1002（借）+1009（借）
        vo1 = getZCFZBVO(map, vo1, false, "2101");  //2101（贷）
        vo1.setZc("　货币资金");
        vo1.setFzhsyzqy("　短期借款");
        vo1.setHc1("1");
        vo1.setHc2("61");

        ZcFzBVO vo2 = new ZcFzBVO();
        clearTempVO(votemp);
        votemp = getZCFZBVO(map, votemp, true, "1102");
        vo2 = getZCFZBVO(map, vo2, true, "1101");  //1101（借）-（贷）
        vo2.setNcye1(VoUtils.getDZFDouble(vo2.getNcye1()).sub(VoUtils.getDZFDouble(votemp.getNcye1())));
        vo2.setQmye1(VoUtils.getDZFDouble(vo2.getQmye1()).sub(VoUtils.getDZFDouble(votemp.getQmye1())));
        vo2.setZcconkms(vo2.getZcconkms() == null ? votemp.getZcconkms() : vo2.getZcconkms() + "," + votemp.getZcconkms());
        if ("Y".equals(hasyes[2])) {
            // 应付款项=2202贷方金额+1141贷方金额+2209贷方金额
            vo2 = getZCFZBVO1(map, vo2, false, mapc, "2201", "2202", "2209", "1141");
        } else {
            vo2 = getZCFZBVO(map, vo2, false, "2201", "2202", "2209");//2201（贷）+2202（贷）+2203（贷）+2209（贷）
        }
        vo2.setZc("　短期投资");
        vo2.setFzhsyzqy("　应付款项");
        vo2.setHc1("2");
        vo2.setHc2("62");

        ZcFzBVO vo3 = new ZcFzBVO();
        //获取临时的votemp
        clearTempVO(votemp);
        if ("Y".equals(hasyes[1])) {
            //应收款项=1121借方金额+2203借方金额+1122借方金额-1131期末余额
            votemp = getZCFZBVO(map, votemp, true, "1131");
            vo3 = getZCFZBVO1(map, vo3, true, mapc, "1111", "1121", "1122", "2203");//1111（借）+1121（借）+1122（借）-1131（贷）
            vo3.setZcconkms(vo3.getZcconkms() == null ? votemp.getZcconkms() : vo3.getZcconkms() + "," + votemp.getZcconkms());
            vo3.setNcye1(VoUtils.getDZFDouble(vo3.getNcye1()).sub(VoUtils.getDZFDouble(votemp.getNcye1())));
            vo3.setQmye1(VoUtils.getDZFDouble(vo3.getQmye1()).sub(VoUtils.getDZFDouble(votemp.getQmye1())));
        } else {
            votemp = getZCFZBVO(map, votemp, true, "1131");
            vo3 = getZCFZBVO(map, vo3, true, "1111", "1121", "1122");//1111（借）+1121（借）+1122（借）-1131（贷）
            vo3.setZcconkms(vo3.getZcconkms() == null ? votemp.getZcconkms() : vo3.getZcconkms() + "," + votemp.getZcconkms());
            vo3.setNcye1(VoUtils.getDZFDouble(vo3.getNcye1()).sub(VoUtils.getDZFDouble(votemp.getNcye1())));
            vo3.setQmye1(VoUtils.getDZFDouble(vo3.getQmye1()).sub(VoUtils.getDZFDouble(votemp.getQmye1())));
        }
        vo3 = getZCFZBVO(map, vo3, false, "2204");//2204（贷）
        vo3.setZc("　  应收款项");
        vo3.setFzhsyzqy("　应付工资");
        vo3.setHc1("3");
        vo3.setHc2("63");

        ZcFzBVO vo4 = new ZcFzBVO();
        if ("Y".equals(hasyes[2])) {
            //预付账款=1141借方金额+2202借方金额
            vo4 = getZCFZBVO1(map, vo4, true, mapc, "1141", "2202", "2201", "2209");//1141（借）+2202
        } else {
            vo4 = getZCFZBVO(map, vo4, true, "1141");//1141（借）
        }
        vo4 = getZCFZBVO(map, vo4, false, "2206");//2206（贷）
        vo4.setZc("　预付账款");
        vo4.setFzhsyzqy("　应交税金");
        vo4.setHc1("4");
        vo4.setHc2("65");

        ZcFzBVO vo5 = new ZcFzBVO();
        clearTempVO(votemp);
        votemp = getZCFZBVO(map, votemp, true, "1202");
        vo5 = getZCFZBVO(map, vo5, true, "1201");//存  货 1201（借）-1202（贷）
        vo5.setZcconkms(vo5.getZcconkms() == null ? votemp.getZcconkms() : vo5.getZcconkms() + "," + votemp.getZcconkms());
        vo5.setNcye1(VoUtils.getDZFDouble(vo5.getNcye1()).sub(VoUtils.getDZFDouble(votemp.getNcye1())));
        vo5.setQmye1(VoUtils.getDZFDouble(vo5.getQmye1()).sub(VoUtils.getDZFDouble(votemp.getQmye1())));
        if ("Y".equals(hasyes[1])) {
            //预收账款=1121贷方金额+2203贷方金额
            vo5 = getZCFZBVO1(map, vo5, false, mapc, "1111", "2203", "1122", "1121");//预收账款2203（贷）
        } else {
            vo5 = getZCFZBVO(map, vo5, false, "2203");//预收账款2203（贷）
        }

        vo5.setZc("　存货");
        vo5.setFzhsyzqy("　预收账款");
        vo5.setHc1("8");
        vo5.setHc2("66");

        ZcFzBVO vo6 = new ZcFzBVO();
        vo6 = getZCFZBVO(map, vo6, true, "1301");//1301（借）
        vo6 = getZCFZBVO(map, vo6, false, "2301");//2301（贷）
        vo6.setZc("　待摊费用");
        vo6.setFzhsyzqy("　预提费用");
        vo6.setHc1("9");
        vo6.setHc2("71");

        ZcFzBVO vo7 = new ZcFzBVO();
        vo7 = getZCFZBVO(map, vo7, false, "2401");//2401（贷）
        vo7.setZc("　一年内到期的长期债权投资");
        vo7.setFzhsyzqy("　预计负债");
        vo7.setHc1("15");
        vo7.setHc2("72");

        ZcFzBVO vo8 = new ZcFzBVO();
        vo8 = getZCFZBVO(map, vo8, false, "2232");
        vo8.setZc("　其他流动资产");
        vo8.setFzhsyzqy("　一年内到期的长期负债");
        vo8.setHc1("18");
        vo8.setHc2("74");


        ZcFzBVO vo9 = new ZcFzBVO();
        vo9.setZc("流动资产合计");
        vo9.setNcye1(VoUtils.getDZFDouble(vo1.getNcye1()).add(VoUtils.getDZFDouble(vo2.getNcye1())).add(VoUtils.getDZFDouble(vo3.getNcye1()))
                .add(VoUtils.getDZFDouble(vo4.getNcye1())).add(VoUtils.getDZFDouble(vo5.getNcye1())).add(VoUtils.getDZFDouble(vo6.getNcye1()))
                .add(VoUtils.getDZFDouble(vo7.getNcye1())).add(VoUtils.getDZFDouble(vo8.getNcye1()))
        );
        vo9.setQmye1(VoUtils.getDZFDouble(vo1.getQmye1()).add(VoUtils.getDZFDouble(vo2.getQmye1())).add(VoUtils.getDZFDouble(vo3.getQmye1()))
                .add(VoUtils.getDZFDouble(vo4.getQmye1())).add(VoUtils.getDZFDouble(vo5.getQmye1())).add(VoUtils.getDZFDouble(vo6.getQmye1()))
                .add(VoUtils.getDZFDouble(vo7.getQmye1())).add(VoUtils.getDZFDouble(vo8.getQmye1()))
        );
        vo9.setFzhsyzqy("其他流动负债");
        vo9.setHc1("20");
        vo9.setHc2("78");


        ZcFzBVO vo10 = new ZcFzBVO();
        vo10.setNcye2(VoUtils.getDZFDouble(vo1.getNcye2()).add(VoUtils.getDZFDouble(vo2.getNcye2())).add(VoUtils.getDZFDouble(vo3.getNcye2()))
                .add(VoUtils.getDZFDouble(vo4.getNcye2())).add(VoUtils.getDZFDouble(vo5.getNcye2())).add(VoUtils.getDZFDouble(vo6.getNcye2()))
                .add(VoUtils.getDZFDouble(vo7.getNcye2())).add(VoUtils.getDZFDouble(vo8.getNcye2()))
        );
        vo10.setQmye2(VoUtils.getDZFDouble(vo1.getQmye2()).add(VoUtils.getDZFDouble(vo2.getQmye2())).add(VoUtils.getDZFDouble(vo3.getQmye2()))
                .add(VoUtils.getDZFDouble(vo4.getQmye2())).add(VoUtils.getDZFDouble(vo5.getQmye2())).add(VoUtils.getDZFDouble(vo6.getQmye2()))
                .add(VoUtils.getDZFDouble(vo7.getQmye2())).add(VoUtils.getDZFDouble(vo8.getQmye2()))
        );
        vo10.setFzhsyzqy("流动负债合计");
        vo10.setHc2("80");


        ZcFzBVO vo11 = new ZcFzBVO();
        vo11.setZc("长期投资：");


        ZcFzBVO vo12 = new ZcFzBVO();
        clearTempVO(votemp);
        String queryAccountRule = zxkjPlatformService.queryAccountRule(pk_corp);
        String newRuleCode = zxkjPlatformService.getNewRuleCode("142101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        votemp = getZCFZBVO(map, votemp, true, newRuleCode);
        vo12 = getZCFZBVO(map, vo12, true, "1401");
        vo12.setZcconkms(vo12.getZcconkms() == null ? newRuleCode : vo12.getZcconkms() + "," + newRuleCode);
        vo12.setNcye1(VoUtils.getDZFDouble(vo12.getNcye1()).sub(VoUtils.getDZFDouble(votemp.getNcye1())));
        vo12.setQmye1(VoUtils.getDZFDouble(vo12.getQmye1()).sub(VoUtils.getDZFDouble(votemp.getQmye1())));
        vo12.setZc("　长期股权投资");
        vo12.setFzhsyzqy("长期负债：");
        vo12.setHc1("21");
        vo12.setHc2(null);

        ZcFzBVO vo13 = new ZcFzBVO();
        clearTempVO(votemp);
        String newRuleCode_c = zxkjPlatformService.getNewRuleCode("142102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
        votemp = getZCFZBVO(map, votemp, true, newRuleCode_c);
        vo13 = getZCFZBVO(map, vo13, true, "1402");
        vo13.setZcconkms(vo13.getZcconkms() == null ? newRuleCode_c : vo13.getZcconkms() + "," + newRuleCode_c);
        vo13.setNcye1(VoUtils.getDZFDouble(vo13.getNcye1()).sub(VoUtils.getDZFDouble(votemp.getNcye1())));
        vo13.setQmye1(VoUtils.getDZFDouble(vo13.getQmye1()).sub(VoUtils.getDZFDouble(votemp.getQmye1())));
        vo13 = getZCFZBVO(map, vo13, false, "2501");
        vo13.setZc("　长期债权投资");
        vo13.setFzhsyzqy("　长期借款");
        vo13.setHc1("24");
        vo13.setHc2("81");


        ZcFzBVO vo14 = new ZcFzBVO();
        //vo14=getZCFZBVO(map,vo14,true ,"1901");
        vo14.setNcye1(VoUtils.getDZFDouble(vo12.getNcye1()).add(VoUtils.getDZFDouble(vo13.getNcye1())));
        vo14.setQmye1(VoUtils.getDZFDouble(vo12.getQmye1()).add(VoUtils.getDZFDouble(vo13.getQmye1())));
        vo14 = getZCFZBVO(map, vo14, false, "2502");
        vo14.setZc("长期投资合计");
        vo14.setFzhsyzqy("　长期应付款");
        vo14.setHc1("30");
        vo14.setHc2("84");

        ZcFzBVO vo15 = new ZcFzBVO();
        vo15.setZc(null);
        vo15.setFzhsyzqy("　其他长期负债");
        vo15.setHc1(null);
        vo15.setHc2("88");

        ZcFzBVO vo16 = new ZcFzBVO();
        //vo16=getZCFZBVO(map,vo16,true, "1501");
        vo16.setQmye2((VoUtils.getDZFDouble(vo13.getQmye2())).add(VoUtils.getDZFDouble(vo14.getQmye2())).add(VoUtils.getDZFDouble(vo15.getQmye2())));
        vo16.setNcye2((VoUtils.getDZFDouble(vo13.getNcye2())).add(VoUtils.getDZFDouble(vo14.getNcye2())).add(VoUtils.getDZFDouble(vo15.getNcye2())));
        vo16.setZc("固定资产：");
        vo16.setFzhsyzqy("长期负债合计");
        vo16.setHc2("90");


        ZcFzBVO vo17 = new ZcFzBVO();
        vo17 = getZCFZBVO(map, vo17, true, "1501");
        vo17.setZc("　固定资产原价");
        vo17.setHc1("31");

        ZcFzBVO vo18 = new ZcFzBVO();
        vo18 = getZCFZBVO(map, vo18, true, "1502");
        vo18.setZc("　减：累计折旧");
        vo18.setFzhsyzqy("受托代理负债：	");
        vo18.setHc1("32");
        vo18.setHc2(null);

        ZcFzBVO vo19 = new ZcFzBVO();
        vo19.setNcye1(VoUtils.getDZFDouble(vo17.getNcye1()).sub(VoUtils.getDZFDouble(vo18.getNcye1())));
        vo19.setQmye1(VoUtils.getDZFDouble(vo17.getQmye1()).sub(VoUtils.getDZFDouble(vo18.getQmye1())));
        vo19 = getZCFZBVO(map, vo19, false, "2601");
        vo19.setZc("　固定资产净值");
        vo19.setFzhsyzqy("　受托代理负债");
        vo19.setHc1("33");
        vo19.setHc2("91");

        ZcFzBVO vo20 = new ZcFzBVO();
        vo20 = getZCFZBVO(map, vo20, true, "1505");
        vo20.setZc("　在建工程");
        vo20.setFzhsyzqy(null);
        vo20.setHc1("34");
        vo20.setHc2(null);

//		
//		ZcFzBVO vo21 = new ZcFzBVO() ;
//		vo21=getZCFZBVO(map,vo21,true,"1604");
//		vo21.setZc("　在建工程") ;
//		vo21.setFzhsyzqy(null) ;
//		vo21.setHc1("21") ;
//		vo21.setHc2(null) ;
//		
        ZcFzBVO vo22 = new ZcFzBVO();
        vo22 = getZCFZBVO(map, vo22, true, "1506");
        vo22.setNcye2(VoUtils.getDZFDouble(vo10.getNcye2()).add(VoUtils.getDZFDouble(vo16.getNcye2()).add(VoUtils.getDZFDouble(vo19.getNcye2()))));
        vo22.setQmye2(VoUtils.getDZFDouble(vo10.getQmye2()).add(VoUtils.getDZFDouble(vo16.getQmye2())).add(VoUtils.getDZFDouble(vo19.getQmye2())));
        vo22.setZc("　文物文化资产");
        vo22.setFzhsyzqy("负债合计");
        vo22.setHc1("35");
        vo22.setHc2("100");

        ZcFzBVO vo23 = new ZcFzBVO();
        vo23 = getZCFZBVO(map, vo23, true, "1509");
        vo23.setZc("　固定资产清理");
        vo23.setFzhsyzqy(null);
        vo23.setHc1("38");
        vo23.setHc2(null);


        ZcFzBVO vo24 = new ZcFzBVO();
        //vo24=getZCFZBVO(map,vo24,true, "1622");
        vo24.setQmye1(VoUtils.getDZFDouble(vo19.getQmye1()).add(VoUtils.getDZFDouble(vo20.getQmye1())).add(VoUtils.getDZFDouble(vo22.getQmye1())).add(VoUtils.getDZFDouble(vo23.getQmye1())));
        vo24.setNcye1(VoUtils.getDZFDouble(vo19.getNcye1()).add(VoUtils.getDZFDouble(vo20.getNcye1())).add(VoUtils.getDZFDouble(vo22.getNcye1())).add(VoUtils.getDZFDouble(vo23.getNcye1())));
        vo24.setZc("固定资产合计");
        vo24.setFzhsyzqy("");
        vo24.setHc1("40");
        vo24.setHc2(null);

        ZcFzBVO vo25 = new ZcFzBVO();
        vo25.setZc("");
        vo25.setFzhsyzqy(" ");
        vo25.setHc1(null);
        vo25.setHc2(null);

        ZcFzBVO vo26 = new ZcFzBVO();
        vo26.setZc("无形资产：");
        vo26.setFzhsyzqy("");
        vo26.setHc1(null);
        vo26.setHc2(null);


        ZcFzBVO vo27 = new ZcFzBVO();
        vo27 = getZCFZBVO(map, vo27, true, "1601");
        vo27.setZc("　无形资产");
        vo27.setFzhsyzqy("净资产：");
        vo27.setHc1("41");
        vo27.setHc2(null);

        ZcFzBVO vo28 = new ZcFzBVO();
        vo28 = getZCFZBVO(map, vo28, false, "3101");
        vo28.setFzhsyzqy("　非限定性净资产");
        vo28.setHc2("101");


        ZcFzBVO vo29 = new ZcFzBVO();
        vo29 = getZCFZBVO(map, vo29, false, "3102");
        vo29.setFzhsyzqy("　限定性净资产");
        vo29.setHc2("105");

        ZcFzBVO vo30 = new ZcFzBVO();
        vo30.setZc("受托代理资产");
        vo30 = getZCFZBVO(map, vo30, true, "1701");
        vo30.setNcye2(VoUtils.getDZFDouble(vo28.getNcye2()).add(VoUtils.getDZFDouble(vo29.getNcye2())));
        vo30.setQmye2(VoUtils.getDZFDouble(vo28.getQmye2()).add(VoUtils.getDZFDouble(vo29.getQmye2())));
        vo30.setFzhsyzqy("净资产合计");
        vo30.setHc1("51");
        vo30.setHc2("110");

        ZcFzBVO vo31 = new ZcFzBVO();
        vo31.setZc("");
        vo31.setFzhsyzqy(" ");
        vo31.setHc1(null);
        vo31.setHc2(null);

        //			20+30+40+41+51				100+110
        ZcFzBVO vo32 = new ZcFzBVO();
        vo32.setNcye1(VoUtils.getDZFDouble(vo9.getNcye1()).add(VoUtils.getDZFDouble(vo14.getNcye1())).add(VoUtils.getDZFDouble(vo24.getNcye1()))
                .add(VoUtils.getDZFDouble(vo27.getNcye1())).add(VoUtils.getDZFDouble(vo30.getNcye1())));
        vo32.setQmye1(VoUtils.getDZFDouble(vo9.getQmye1()).add(VoUtils.getDZFDouble(vo14.getQmye1())).add(VoUtils.getDZFDouble(vo24.getQmye1()))
                .add(VoUtils.getDZFDouble(vo27.getQmye1())).add(VoUtils.getDZFDouble(vo30.getQmye1())));
        vo32.setQmye2(VoUtils.getDZFDouble(vo22.getQmye2()).add(VoUtils.getDZFDouble(vo30.getQmye2())));
        vo32.setNcye2(VoUtils.getDZFDouble(vo22.getNcye2()).add(VoUtils.getDZFDouble(vo30.getNcye2())));
        vo32.setZc("资产总计");
        vo32.setFzhsyzqy("负债和净资产总计");
        vo32.setHc1("60");
        vo32.setHc2("120");

        return new ZcFzBVO[]{vo0, vo1, vo2, vo3, vo4, vo5, vo6, vo7, vo8, vo9, vo10, vo11, vo12, vo13, vo14, vo15, vo16, vo17, vo18, vo19, vo20
                , vo22, vo23, vo24, vo25, vo26, vo27, vo28, vo29, vo30, vo31, vo32};
    }


    public void clearTempVO(ZcFzBVO votemp) {
        votemp.setNcye1(DZFDouble.ZERO_DBL);
        votemp.setQmye1(DZFDouble.ZERO_DBL);
        votemp.setNcye2(DZFDouble.ZERO_DBL);
        votemp.setQmye2(DZFDouble.ZERO_DBL);
        votemp.setZcconkms("");
        votemp.setFzconkms("");
    }

    private ZcFzBVO getZCFZBVO1(Map<String, FseJyeVO> map, ZcFzBVO vo, boolean is1, Map<String, YntCpaccountVO> mapc, String... kms) {
        List<FseJyeVO> ls = null;
        DZFDouble ufd = null;
        int len = kms == null ? 0 : kms.length;
        StringBuffer appkms = new StringBuffer();
        if (is1) {
            appkms = new StringBuffer(StringUtil.isEmpty(vo.getZcconkms()) ? "" : vo.getZcconkms() + ",");
        } else {
            appkms = new StringBuffer(StringUtil.isEmpty(vo.getFzconkms()) ? "" : vo.getFzconkms() + ",");
        }
        for (int i = 0; i < len; i++) {
            ls = getData1(map, kms[i], mapc);
            appkms.append(kms[i] + ",");
            if (ls == null) continue;
            for (FseJyeVO fvo : ls) {
                if (is1) {//资产
                    ufd = VoUtils.getDZFDouble(vo.getQmye1());
                    DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmjf());
                    DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmdf());
                    if ("借".equals(fvo.getFx()) && qmjf.doubleValue() > 0) {
                        ufd = ufd.add(qmjf);
                    } else if (qmdf.doubleValue() < 0) {
                        ufd = ufd.sub(qmdf);
                    }
                    vo.setQmye1(ufd);
                    //
                    ufd = VoUtils.getDZFDouble(vo.getNcye1());
                    DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcjf());
                    DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcdf());
                    if ("借".equals(fvo.getFx()) && qcjf.doubleValue() > 0) {
                        ufd = ufd.add(qcjf);
                    } else if (qcdf.doubleValue() < 0) {
                        ufd = ufd.sub(qcdf);
                    }
                    vo.setNcye1(ufd);
                } else {//负债
                    ufd = VoUtils.getDZFDouble(vo.getQmye2());
                    DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmjf());
                    DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmdf());
                    if ("贷".equals(fvo.getFx()) && qmdf.doubleValue() > 0) {
                        ufd = ufd.add(qmdf);
                    } else if (qmjf.doubleValue() < 0) {
                        ufd = ufd.sub(qmjf);
                    }
                    vo.setQmye2(ufd);
                    //
                    ufd = VoUtils.getDZFDouble(vo.getNcye2());
                    DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcjf());
                    DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcdf());
                    if ("贷".equals(fvo.getFx()) && qcdf.doubleValue() > 0) {
                        ufd = ufd.add(qcdf);
                    } else if (qcjf.doubleValue() < 0) {
                        ufd = ufd.sub(qcjf);
                    }
                    vo.setNcye2(ufd);
                }
            }

            if (is1) {
                vo.setZcconkms(appkms.toString().substring(0, appkms.length() - 1));
            } else {
                vo.setFzconkms(appkms.toString().substring(0, appkms.length() - 1));
            }
        }
        return vo;
    }

    private ZcFzBVO getZCFZBVO(Map<String, FseJyeVO> map, ZcFzBVO vo, boolean is1, String... kms) {
        List<FseJyeVO> ls = null;
        DZFDouble ufd = null;
        int len = kms == null ? 0 : kms.length;
        StringBuffer appkms = new StringBuffer();
        if (is1) {
            appkms = new StringBuffer(StringUtil.isEmpty(vo.getZcconkms()) ? "" : vo.getZcconkms() + ",");
        } else {
            appkms = new StringBuffer(StringUtil.isEmpty(vo.getFzconkms()) ? "" : vo.getFzconkms() + ",");
        }
        for (int i = 0; i < len; i++) {
            ls = getData(map, kms[i]);
            appkms.append(kms[i] + ",");
            if (ls == null) continue;
            for (FseJyeVO fvo : ls) {
                if (is1) {
                    ufd = VoUtils.getDZFDouble(vo.getQmye1());
                    DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmjf());
                    DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmdf());
                    ufd = ufd.add(qmjf).add(qmdf);

                    vo.setQmye1(ufd);
                    ufd = VoUtils.getDZFDouble(vo.getNcye1());
                    DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcjf());
                    DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcdf());
                    ufd = ufd.add(qcjf).add(qcdf);

                    vo.setNcye1(ufd);

                    //本月期初
                    ufd = VoUtils.getDZFDouble(vo.getQcye1());
                    DZFDouble byqcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getLastmqcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getLastmqcjf());
                    DZFDouble bqqcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getLastmqcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getLastmqcdf());
                    ufd = ufd.add(byqcjf).add(bqqcdf);
                    vo.setQcye1(ufd);
                } else {
                    ufd = VoUtils.getDZFDouble(vo.getQmye2());
                    ufd = ufd.add(VoUtils.getDZFDouble(fvo.getQmjf())).add(VoUtils.getDZFDouble(fvo.getQmdf()));//只有一方有值
                    vo.setQmye2(ufd);
                    ufd = VoUtils.getDZFDouble(vo.getNcye2());
                    ufd = ufd.add(VoUtils.getDZFDouble(fvo.getQcjf())).add(VoUtils.getDZFDouble(fvo.getQcdf()));//只有一方有值
                    vo.setNcye2(ufd);

                    //本月期初
                    ufd = VoUtils.getDZFDouble(vo.getQcye2());
                    ufd = ufd.add(VoUtils.getDZFDouble(fvo.getLastmqcjf())).add(VoUtils.getDZFDouble(fvo.getLastmqcdf()));//只有一方有值
                    vo.setQcye2(ufd);
                }
            }

            if (is1) {
                vo.setZcconkms(appkms.toString().substring(0, appkms.length() - 1));
            } else {
                vo.setFzconkms(appkms.toString().substring(0, appkms.length() - 1));
            }
        }
        return vo;
    }


    private List<FseJyeVO> getData1(Map<String, FseJyeVO> map, String km, Map<String, YntCpaccountVO> mapc) {
        List<FseJyeVO> list = new ArrayList<FseJyeVO>();
        for (FseJyeVO fsejyevo : map.values()) {
            String kmbm = null;
//			String kmbm = fsejyevo.getKmbm();
//			if(kmbm.startsWith(km)){
//				YntCpaccountVO xs = mapc.get(kmbm);
//				if(xs.getIsleaf() != null && xs.getIsleaf().booleanValue()){
//					list.add(fsejyevo);
//				}
//			}
            if (fsejyevo.getKmbm().indexOf("_") > 0) {
                kmbm = fsejyevo.getKmbm().split("_")[0];
            } else {
                kmbm = fsejyevo.getKmbm();
            }
            if (kmbm.startsWith(km)) {
                YntCpaccountVO xs = mapc.get(kmbm);
                if (xs.getIsleaf() != null && xs.getIsleaf().booleanValue()) {
                    if (xs.getIsfzhs().indexOf("1") >= 0 && fsejyevo.getKmbm().indexOf("_") >= 0) {//包含辅助项目
                        list.add(fsejyevo);
                    } else if (xs.getIsfzhs().indexOf("1") < 0) {//如果不包含辅助项目处理
                        list.add(fsejyevo);
                    }
                }
            }
        }
        return list;
    }

    private List<FseJyeVO> getData(Map<String, FseJyeVO> map, String km) {
        List<FseJyeVO> list = new ArrayList<FseJyeVO>();
        for (FseJyeVO fsejyevo : map.values()) {
            if (fsejyevo.getKmbm().equals(km)) {//已经合计过了，不要用startwidh
                list.add(fsejyevo);
                break;
            }
        }
        return list;
    }


}
