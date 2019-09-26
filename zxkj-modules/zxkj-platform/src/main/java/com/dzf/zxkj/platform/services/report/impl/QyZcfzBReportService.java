package com.dzf.zxkj.platform.services.report.impl;

import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.services.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.util.VoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class QyZcfzBReportService {

	private ICpaccountCodeRuleService gl_accountcoderule ;
	
	
	public QyZcfzBReportService(ICpaccountCodeRuleService gl_accountcoderule) {
		super();
		this.gl_accountcoderule = gl_accountcoderule;
	}

	public ZcFzBVO[] getZCFZB2007VOs(Map<String, FseJyeVO> map, String[] hasyes, Map<String, YntCpaccountVO> mapc
			, String queryAccountRule)
			throws DZFWarpException {
		if (hasyes == null || hasyes.length != 5 || !"Y".equals(hasyes[0])) {
			hasyes = new String[] { "N", "N", "N", "N","N" };
		}
		ZcFzBVO vo0 = new ZcFzBVO();
		vo0.setZc("流动资产：");
		vo0.setFzhsyzqy("流动负债：");

		ZcFzBVO vo1 = new ZcFzBVO();
		vo1 = getZCFZBVO(map, vo1, true, "1001", "1002", "1012");
		vo1 = getZCFZBVO(map, vo1, false, "2001");
		vo1.setZc("　货币资金");
		vo1.setFzhsyzqy("　短期借款");
		vo1.setHc1("1");
		vo1.setHc2("34");
		vo1.setHc1_id("ZCFZ-001");
		vo1.setHc2_id("ZCFZ-034");
		vo1.setZcconkms("1001,1002,1012");

		ZcFzBVO vo2 = new ZcFzBVO();
		vo2 = getZCFZBVO(map, vo2, true, "1101", "1111");// add by zhangj  添加1111科目
		vo2 = getZCFZBVO(map, vo2, false, "2101");
		vo2.setZc("　以公允价值计量且其变动计入当期损益的金融资产");
		vo2.setFzhsyzqy("　以公允价值计量且其变动计入当期损益的金融负债");
		vo2.setHc1("2");
		vo2.setHc2("35");
		vo2.setHc1_id("ZCFZ-002");
		vo2.setHc2_id("ZCFZ-035");

		ZcFzBVO vo2_1 = new ZcFzBVO();
		// vo2_1=getZCFZBVO(map,vo2_1,true,"1121");
		// vo2_1=getZCFZBVO(map,vo2_1,false,"2201");
		vo2_1.setZc("　衍生金融资产");
		vo2_1.setFzhsyzqy("　衍生金融负债");
		vo2_1.setHc1("3");
		vo2_1.setHc2("36");
		vo2_1.setHc1_id("ZCFZ-003");
		vo2_1.setHc2_id("ZCFZ-036");

		ZcFzBVO vo3 = new ZcFzBVO();
		vo3 = getZCFZBVO(map, vo3, true, "1121");
		vo3 = getZCFZBVO(map, vo3, false, "2201");
		vo3.setZc("　应收票据");
		vo3.setFzhsyzqy("　应付票据");
		vo3.setHc1("4");
		vo3.setHc2("37");
		vo3.setHc1_id("ZCFZ-004");
		vo3.setHc2_id("ZCFZ-037");

		ZcFzBVO vo4 = new ZcFzBVO();
		if ("Y".equals(hasyes[0])) {
			// 应收账款=1122借方金额+2203借方金额-1231期末余额
			// 应付账款=2202贷方金额+1123贷方金额
			vo4 = getZCFZBVO(map, vo4, true, "1231");
			vo4.setQmye1(VoUtils.getDZFDouble(vo4.getQmye1()).multiply(new DZFDouble(-1)));
			vo4.setNcye1(VoUtils.getDZFDouble(vo4.getNcye1()).multiply(new DZFDouble(-1)));
			if ("Y".equals(hasyes[1])) {// 应收和预收
				vo4 = getZCFZBVO1(map, vo4, true, mapc,false, "1122", "2203");
			} else {
				vo4 = getZCFZBVO(map, vo4, true, "1122");
			}
			if ("Y".equals(hasyes[2])) {// 应付和预付
				vo4 = getZCFZBVO1(map, vo4, false, mapc,false, "2202", "1123");
			} else {
				vo4 = getZCFZBVO(map, vo4, false, "2202");
			}
			vo4.setZc("　应收账款");
			vo4.setFzhsyzqy("　应付账款");
		} else {
			vo4 = getZCFZBVO(map, vo4, true, "1231");
			vo4.setQmye1(VoUtils.getDZFDouble(vo4.getQmye1()).multiply(new DZFDouble(-1)));
			vo4.setNcye1(VoUtils.getDZFDouble(vo4.getNcye1()).multiply(new DZFDouble(-1)));
			vo4 = getZCFZBVO(map, vo4, true, "1122");
			vo4 = getZCFZBVO(map, vo4, false, "2202");
			vo4.setZc("　应收账款");
			vo4.setFzhsyzqy("　应付账款");
		}
		vo4.setHc1("5");
		vo4.setHc2("38");
		vo4.setHc1_id("ZCFZ-005");
		vo4.setHc2_id("ZCFZ-038");

		ZcFzBVO vo5 = new ZcFzBVO();
		if ("Y".equals(hasyes[0])) {
			vo5.setZc("　预付款项");
			vo5.setFzhsyzqy("　预收款项");
			// 2007新会计 预付账款=1123借方金额+2202借方金额
			// 2007新会计 预收账款=1122贷方金额+2203贷方金额
			if ("Y".equals(hasyes[1])) {// 应收和预收
				vo5 = getZCFZBVO1(map, vo5, false, mapc,false, "2203", "1122", "5401", "5402");
			} else {
				vo5 = getZCFZBVO(map, vo5, false, "2203", "5401", "5402");
			}
			if ("Y".equals(hasyes[2])) {// 应付和预付
				vo5 = getZCFZBVO1(map, vo5, true, mapc,false, "1123", "2202");
			} else {
				vo5 = getZCFZBVO(map, vo5, true, "1123", "1611");
			}
			vo5.setHc1("6");
			vo5.setHc2("39");
		} else {
			vo5.setZc("　预付款项");
			vo5.setFzhsyzqy("　预收款项");
			vo5 = getZCFZBVO(map, vo5, true, "1123", "1611");
			vo5 = getZCFZBVO(map, vo5, false, "2203", "5401", "5402");
			vo5.setHc1("6");
			vo5.setHc2("39");
		}
		vo5.setHc1_id("ZCFZ-006");
		vo5.setHc2_id("ZCFZ-039");

		ZcFzBVO vo6 = new ZcFzBVO();
		vo6 = getZCFZBVO(map, vo6, true, "1132");
		vo6 = getZCFZBVO(map, vo6, false, "2211");
		vo6.setZc("　应收利息");
		vo6.setFzhsyzqy("　应付职工薪酬");
		vo6.setHc1("7");
		vo6.setHc2("40");
		vo6.setHc1_id("ZCFZ-007");
		vo6.setHc2_id("ZCFZ-040");

		ZcFzBVO vo7 = new ZcFzBVO();
		vo7 = getZCFZBVO(map, vo7, true, "1131");
		if ("Y".equals(hasyes[4])) {
			//应交税费重分类取数
			vo7 = getZcFzBVO_YJSF_CFL(map, mapc, queryAccountRule, vo7);
		}else{
			vo7 = getZCFZBVO(map, vo7, false, "2221");
		}
		vo7.setZc("　应收股利");
		vo7.setFzhsyzqy("　应交税费");
		vo7.setHc1("8");
		vo7.setHc2("41");
		vo7.setHc1_id("ZCFZ-008");
		vo7.setHc2_id("ZCFZ-041");

		ZcFzBVO vo8 = new ZcFzBVO();
		if ("Y".equals(hasyes[3])) {
			vo8 = getZCFZBVO1(map, vo8, true, mapc,false, "1221", "1200", "2241", "2012");
		} else {
			vo8 = getZCFZBVO(map, vo8, true, "1221", "1200");
		}
		vo8 = getZCFZBVO(map, vo8, false, "2231");
		vo8.setZc("　其他应收款");
		vo8.setFzhsyzqy("　应付利息");
		vo8.setHc1("9");
		vo8.setHc2("42");
		vo8.setHc1_id("ZCFZ-009");
		vo8.setHc2_id("ZCFZ-042");

		ZcFzBVO vo9 = new ZcFzBVO();
		// vo9=getZCFZBVO(map,vo9,true,"1401","1402","1403","1404","1405","1406","1407","1408","1409","1411","1421","5001","5201","5301","5401","5402","5403","1471","5101","1321","2314");
		// 研发支出，2007,zpm去掉 "5301"，不确定，在说
		vo9 = getZCFZBVO(map, vo9, true, "1471");
		vo9.setZc("　存货");
		vo9.setQmye1(VoUtils.getDZFDouble(vo9.getQmye1()).multiply(new DZFDouble(-1)));
		vo9.setNcye1(VoUtils.getDZFDouble(vo9.getNcye1()).multiply(new DZFDouble(-1)));
		vo9 = getZCFZBVO(map, vo9, true, "1401", "1402", "1403", "1404", "1405", "1406", "1407", "1408", "1409", "1411",
				"1421", "5001", "5201", "5401", "5402", "5403", "5101", "1321", "2314");
		vo9 = getZCFZBVO(map, vo9, false, "2232");
		vo9.setFzhsyzqy("　应付股利");
		vo9.setHc1("10");
		vo9.setHc2("43");
		vo9.setHc1_id("ZCFZ-010");
		vo9.setHc2_id("ZCFZ-043");

		ZcFzBVO vo9_1 = new ZcFzBVO();
		vo9_1 = getZCFZBVO(map, vo9_1, true, "1482");
		vo9_1.setQmye1(VoUtils.getDZFDouble(vo9_1.getQmye1()).multiply(new DZFDouble(-1)));
		vo9_1.setNcye1(VoUtils.getDZFDouble(vo9_1.getNcye1()).multiply(new DZFDouble(-1)));
		vo9_1 = getZCFZBVO(map, vo9_1, true, "1481");
		if ("Y".equals(hasyes[3])) {
			vo9_1 = getZCFZBVO1(map, vo9_1, false, mapc,false, "1221", "1200", "2241", "2012");
		} else {
			vo9_1 = getZCFZBVO(map, vo9_1, false, "2241", "2012");
		}
		vo9_1.setZc("　持有待售资产");
		vo9_1.setFzhsyzqy("　其他应付款");
		vo9_1.setHc1("11");
		vo9_1.setHc2("44");
		vo9_1.setHc1_id("ZCFZ-011");
		vo9_1.setHc2_id("ZCFZ-044");

		ZcFzBVO vo10 = new ZcFzBVO();
		// if("Y".equals(hasyes[3])){
		// vo10=getZCFZBVO1(map,vo10,false,mapc,"1221","2241","2012");
		// }else{
		// vo10=getZCFZBVO(map,vo10,false,"2241","2012");
		// }
		vo10 = getZCFZBVO(map, vo10, false, "2245");
		vo10.setZc("　一年内到期的非流动资产");
		vo10.setFzhsyzqy("　持有待售负债");
		vo10.setHc1("12");
		vo10.setHc2("45");
		vo10.setHc1_id("ZCFZ-012");
		vo10.setHc2_id("ZCFZ-045");

		ZcFzBVO vo11 = new ZcFzBVO();
		if("Y".equals(hasyes[4])){
			vo11=getZCFZBVO1(map, vo11,true, mapc,true, getYSJFKmList(queryAccountRule).toArray(new String[0]));
		}
		vo11.setZc("　其他流动资产");
		// vo11=getZCFZBVO(map,vo11,true,"1901");
		vo11.setFzhsyzqy("　一年内到期的非流动负债");
		vo11.setHc1("13");
		vo11.setHc2("46");
		vo11.setHc1_id("ZCFZ-013");
		vo11.setHc2_id("ZCFZ-046");

		ZcFzBVO vo12 = new ZcFzBVO();
		// VO1-VO11
		vo12.setQmye1(VoUtils.getDZFDouble(vo1.getQmye1()).add(VoUtils.getDZFDouble(vo2.getQmye1()))
				.add(VoUtils.getDZFDouble(vo2_1.getQmye1())).add(VoUtils.getDZFDouble(vo3.getQmye1()))
				.add(VoUtils.getDZFDouble(vo4.getQmye1())).add(VoUtils.getDZFDouble(vo5.getQmye1()))
				.add(VoUtils.getDZFDouble(vo6.getQmye1())).add(VoUtils.getDZFDouble(vo7.getQmye1()))
				.add(VoUtils.getDZFDouble(vo8.getQmye1())).add(VoUtils.getDZFDouble(vo9.getQmye1()))
				.add(VoUtils.getDZFDouble(vo9_1.getQmye1())).add(VoUtils.getDZFDouble(vo10.getQmye1()))
				.add(VoUtils.getDZFDouble(vo11.getQmye1())));
		vo12.setNcye1(VoUtils.getDZFDouble(vo1.getNcye1()).add(VoUtils.getDZFDouble(vo2.getNcye1()))
				.add(VoUtils.getDZFDouble(vo2_1.getNcye1())).add(VoUtils.getDZFDouble(vo3.getNcye1()))
				.add(VoUtils.getDZFDouble(vo4.getNcye1())).add(VoUtils.getDZFDouble(vo5.getNcye1()))
				.add(VoUtils.getDZFDouble(vo6.getNcye1())).add(VoUtils.getDZFDouble(vo7.getNcye1()))
				.add(VoUtils.getDZFDouble(vo8.getNcye1())).add(VoUtils.getDZFDouble(vo9.getNcye1()))
				.add(VoUtils.getDZFDouble(vo9_1.getNcye1())).add(VoUtils.getDZFDouble(vo10.getNcye1()))
				.add(VoUtils.getDZFDouble(vo11.getNcye1())));
		vo12.setZc("流动资产合计");
		if ("Y".equals(hasyes[4])) {
			String filtercode = gl_accountcoderule.getNewRuleCode("222114", DZFConstant.ACCOUNTCODERULE,
					queryAccountRule);
			vo12= getZCFZBVO(map, vo12, false, filtercode);
		}
		vo12.setFzhsyzqy("　其他流动负债");
		vo12.setHc1("14");
		vo12.setHc2("47");
		vo12.setHc1_id("ZCFZ-014");
		vo12.setHc2_id("ZCFZ-047");

		ZcFzBVO vo00 = new ZcFzBVO();
		vo00.setZc("非流动资产：");
		vo00.setQmye2(z(vo1.getQmye2()).add(z(vo2.getQmye2())).add(z(vo2_1.getQmye2())).add(z(vo3.getQmye2()))
				.add(z(vo4.getQmye2())).add(z(vo5.getQmye2())).add(z(vo6.getQmye2())).add(z(vo7.getQmye2()))
				.add(z(vo8.getQmye2())).add(VoUtils.getDZFDouble(vo9.getQmye2()))
				.add(VoUtils.getDZFDouble(vo9_1.getQmye2())).add(VoUtils.getDZFDouble(vo10.getQmye2()))
				.add(VoUtils.getDZFDouble(vo12.getQmye2())));
		vo00.setNcye2(z(vo1.getNcye2()).add(z(vo2.getNcye2())).add(z(vo2_1.getNcye2())).add(z(vo3.getNcye2()))
				.add(z(vo4.getNcye2())).add(z(vo5.getNcye2())).add(z(vo6.getNcye2())).add(z(vo7.getNcye2()))
				.add(z(vo8.getNcye2())).add(VoUtils.getDZFDouble(vo9.getNcye2()))
				.add(VoUtils.getDZFDouble(vo9_1.getNcye2())).add(VoUtils.getDZFDouble(vo10.getNcye2()))
				.add(VoUtils.getDZFDouble(vo12.getNcye2())));

		vo00.setFzhsyzqy("流动负债合计");
		// vo00.setHc1("14") ;
		vo00.setHc2("48");
		vo00.setHc2_id("ZCFZ-048");

		ZcFzBVO vo13 = new ZcFzBVO();
		vo13 = getZCFZBVO(map, vo13, true, "1503");
		vo13.setZc("　可供出售金融资产");
		vo13.setFzhsyzqy("非流动负债：");
		vo13.setHc1("15");
		// vo13.setHc2("48") ;
		vo13.setHc1_id("ZCFZ-015");

		ZcFzBVO vo14 = new ZcFzBVO();
		vo14 = getZCFZBVO(map, vo14, true, "1502");
		vo14.setQmye1(VoUtils.getDZFDouble(vo14.getQmye1()).multiply(new DZFDouble(-1)));
		vo14.setNcye1(VoUtils.getDZFDouble(vo14.getNcye1()).multiply(new DZFDouble(-1)));
		vo14 = getZCFZBVO(map, vo14, true, "1501");
		vo14 = getZCFZBVO(map, vo14, false, "2501");
		vo14.setZc("　持有至到期投资");
		vo14.setFzhsyzqy("　长期借款");
		vo14.setHc1("16");
		vo14.setHc2("49");
		vo14.setHc1_id("ZCFZ-016");
		vo14.setHc2_id("ZCFZ-049");

		ZcFzBVO vo15 = new ZcFzBVO();
		vo15 = getZCFZBVO(map, vo15, true, "1532");
		vo15.setQmye1(VoUtils.getDZFDouble(vo15.getQmye1()).multiply(new DZFDouble(-1)));
		vo15.setNcye1(VoUtils.getDZFDouble(vo15.getNcye1()).multiply(new DZFDouble(-1)));
		vo15 = getZCFZBVO(map, vo15, true, "1531");
		vo15 = getZCFZBVO(map, vo15, false, "2502");
		vo15.setZc("　长期应收款");
		vo15.setFzhsyzqy("　应付债券");
		vo15.setHc1("17");
		vo15.setHc2("50");
		vo15.setHc1_id("ZCFZ-017");
		vo15.setHc2_id("ZCFZ-050");

		ZcFzBVO vo16 = new ZcFzBVO();
		vo16 = getZCFZBVO(map, vo16, true, "1512");
		vo16.setQmye1(VoUtils.getDZFDouble(vo16.getQmye1()).multiply(new DZFDouble(-1)));
		vo16.setNcye1(VoUtils.getDZFDouble(vo16.getNcye1()).multiply(new DZFDouble(-1)));
		vo16 = getZCFZBVO(map, vo16, true, "1511");
		vo16.setQmye1(VoUtils.getDZFDouble(vo16.getQmye1()));
		vo16.setNcye1(VoUtils.getDZFDouble(vo16.getNcye1()));
		// vo16=getZCFZBVO(map,vo16,false,"2701","2702");
		vo16.setZc("　长期股权投资");
		vo16.setFzhsyzqy("　　其中：优先股");
		// vo16.setFzhsyzqy(" 长期应付款") ;
		vo16.setHc1("18");
		vo16.setHc2("51");
		vo16.setHc1_id("ZCFZ-018");
		vo16.setHc2_id("ZCFZ-051");

		ZcFzBVO vo17 = new ZcFzBVO();
		vo17 = getZCFZBVO(map, vo17, true, "1521");
		// vo17=getZCFZBVO(map,vo17,false,"2711");
		vo17.setZc("　投资性房地产");
		vo17.setFzhsyzqy("　　　　　永续债");
		// vo17.setFzhsyzqy(" 专项应付款") ;
		vo17.setHc1("19");
		vo17.setHc2("52");
		vo17.setHc1_id("ZCFZ-019");
		vo17.setHc2_id("ZCFZ-052");

		ZcFzBVO vo18 = new ZcFzBVO();
		vo18 = getZCFZBVO(map, vo18, true, "1602", "1603");
		vo18.setQmye1(VoUtils.getDZFDouble(vo18.getQmye1()).multiply(new DZFDouble(-1)));
		vo18.setNcye1(VoUtils.getDZFDouble(vo18.getNcye1()).multiply(new DZFDouble(-1)));
		vo18 = getZCFZBVO(map, vo18, true, "1601");
		vo18 = getZCFZBVO(map, vo18, false, "2702");
		vo18.setQmye2(VoUtils.getDZFDouble(vo18.getQmye2()).multiply(new DZFDouble(-1)));
		vo18.setNcye2(VoUtils.getDZFDouble(vo18.getNcye2()).multiply(new DZFDouble(-1)));
		vo18 = getZCFZBVO(map, vo18, false, "2701");
		// vo18=getZCFZBVO(map,vo18,false,"2801");
		vo18.setZc("　固定资产");
		vo18.setFzhsyzqy("　长期应付款");
		// vo18.setFzhsyzqy(" 预计负债") ;
		vo18.setHc1("20");
		vo18.setHc2("53");
		vo18.setHc1_id("ZCFZ-020");
		vo18.setHc2_id("ZCFZ-053");

		ZcFzBVO vo19 = new ZcFzBVO();
		vo19 = getZCFZBVO(map, vo19, true, "1604");
		vo19 = getZCFZBVO(map, vo19, false, "2711");
		// vo19=getZCFZBVO(map,vo19,false,"2901");
		vo19.setZc("　在建工程");
		vo19.setFzhsyzqy("　专项应付款");
		// vo19.setFzhsyzqy(" 递延所得税负债") ;
		vo19.setHc1("21");
		vo19.setHc2("54");
		vo19.setHc1_id("ZCFZ-021");
		vo19.setHc2_id("ZCFZ-054");

		ZcFzBVO vo20 = new ZcFzBVO();
		vo20 = getZCFZBVO(map, vo20, true, "1605");
		vo20 = getZCFZBVO(map, vo20, false, "2801");
		vo20.setZc("　工程物资");
		vo20.setFzhsyzqy("　预计负债");
		// vo20.setFzhsyzqy(" 其他非流动负债 ") ;
		vo20.setHc1("22");
		vo20.setHc2("55");
		vo20.setHc1_id("ZCFZ-022");
		vo20.setHc2_id("ZCFZ-055");

		ZcFzBVO vo21 = new ZcFzBVO();
		vo21 = getZCFZBVO(map, vo21, true, "1606");
		/*
		 * vo21.setQmye2(VoUtils.getDZFDouble(vo14.getQmye2())
		 * .add(VoUtils.getDZFDouble(vo15.getQmye2()))
		 * .add(VoUtils.getDZFDouble(vo16.getQmye2()))
		 * .add(VoUtils.getDZFDouble(vo17.getQmye2()))
		 * .add(VoUtils.getDZFDouble(vo18.getQmye2()))
		 * .add(VoUtils.getDZFDouble(vo19.getQmye2()))
		 * .add(VoUtils.getDZFDouble(vo20.getQmye2()))) ;
		 * vo21.setNcye2(VoUtils.getDZFDouble(vo14.getNcye2()).add(VoUtils.
		 * getDZFDouble(vo15.getNcye2())).add(VoUtils.getDZFDouble(vo16.getNcye2
		 * ())).add(VoUtils.getDZFDouble(vo17.getNcye2())).
		 * add(VoUtils.getDZFDouble(vo18.getNcye2())).add(VoUtils.getDZFDouble(
		 * vo19.getNcye2())).add(VoUtils.getDZFDouble(vo20.getNcye2()))) ;
		 */
		vo21 = getZCFZBVO(map, vo21, false, "2401");
		vo21.setZc("　固定资产清理");
		vo21.setFzhsyzqy("　递延收益");
		// vo21.setFzhsyzqy("非流动负债合计") ;
		vo21.setHc1("23");
		vo21.setHc2("56");
		vo21.setHc1_id("ZCFZ-023");
		vo21.setHc2_id("ZCFZ-056");

		ZcFzBVO vo22 = new ZcFzBVO();
		vo22 = getZCFZBVO(map, vo22, true, "1622");
		vo22.setQmye1(VoUtils.getDZFDouble(vo22.getQmye1()).multiply(new DZFDouble(-1)));
		vo22.setNcye1(VoUtils.getDZFDouble(vo22.getNcye1()).multiply(new DZFDouble(-1)));
		vo22 = getZCFZBVO(map, vo22, true, "1621");// ,"1623"
		vo22 = getZCFZBVO(map, vo22, false, "2901");
		/*
		 * vo22.setQmye2(VoUtils.getDZFDouble(vo00.getQmye2()).add(VoUtils.
		 * getDZFDouble(vo21.getQmye2()))) ;
		 * vo22.setNcye2(VoUtils.getDZFDouble(vo00.getNcye2()).add(VoUtils.
		 * getDZFDouble(vo21.getNcye2()))) ;
		 */
		vo22.setZc("　生产性生物资产");
		vo22.setFzhsyzqy("　递延所得税负债");
		// vo22.setFzhsyzqy("负债合计") ;
		vo22.setHc1("24");
		vo22.setHc2("57");
		vo22.setHc1_id("ZCFZ-024");
		vo22.setHc2_id("ZCFZ-057");

		ZcFzBVO vo23 = new ZcFzBVO();
		vo23.setZc("　油气资产");
		vo23.setFzhsyzqy("　其他非流动负债 ");
		// vo23.setFzhsyzqy("所有者权益(或股东权益)：") ;
		vo23.setHc1("25");
		vo23.setHc2("58");
		vo23.setHc1_id("ZCFZ-025");
		vo23.setHc2_id("ZCFZ-058");

		ZcFzBVO vo24 = new ZcFzBVO();
		vo24 = getZCFZBVO(map, vo24, true, "1702", "1703");
		vo24.setQmye1(VoUtils.getDZFDouble(vo24.getQmye1()).multiply(new DZFDouble(-1)));
		vo24.setNcye1(VoUtils.getDZFDouble(vo24.getNcye1()).multiply(new DZFDouble(-1)));
		vo24 = getZCFZBVO(map, vo24, true, "1701");
		vo24.setQmye2(VoUtils.getDZFDouble(vo14.getQmye2()).add(VoUtils.getDZFDouble(vo15.getQmye2()))
				.add(VoUtils.getDZFDouble(vo18.getQmye2())).add(VoUtils.getDZFDouble(vo19.getQmye2()))
				.add(VoUtils.getDZFDouble(vo20.getQmye2())).add(VoUtils.getDZFDouble(vo21.getQmye2()))
				.add(VoUtils.getDZFDouble(vo22.getQmye2())).add(VoUtils.getDZFDouble(vo23.getQmye2())));
		vo24.setNcye2(VoUtils.getDZFDouble(vo14.getNcye2()).add(VoUtils.getDZFDouble(vo15.getNcye2()))
				.add(VoUtils.getDZFDouble(vo18.getNcye2())).add(VoUtils.getDZFDouble(vo19.getNcye2()))
				.add(VoUtils.getDZFDouble(vo20.getNcye2())).add(VoUtils.getDZFDouble(vo21.getNcye2()))
				.add(VoUtils.getDZFDouble(vo22.getNcye2())).add(VoUtils.getDZFDouble(vo23.getNcye2())));
		// vo24=getZCFZBVO(map,vo24,false,"4001");

		vo24.setZc("　无形资产");
		vo24.setFzhsyzqy("非流动负债合计");
		// vo24.setFzhsyzqy(" 实收资本(或股本)") ;
		vo24.setHc1("26");
		vo24.setHc2("59");
		vo24.setHc1_id("ZCFZ-026");
		vo24.setHc2_id("ZCFZ-059");

		ZcFzBVO vo25 = new ZcFzBVO();
		vo25 = getZCFZBVO(map, vo25, true, "5301");
		vo25.setQmye2(VoUtils.getDZFDouble(vo00.getQmye2()).add(VoUtils.getDZFDouble(vo24.getQmye2())));
		vo25.setNcye2(VoUtils.getDZFDouble(vo00.getNcye2()).add(VoUtils.getDZFDouble(vo24.getNcye2())));
		// vo25=getZCFZBVO(map,vo25,false,"4002");
		vo25.setZc("　开发支出");
		vo25.setFzhsyzqy("负债合计");
		// vo25.setFzhsyzqy(" 资本公积") ;
		vo25.setHc1("27");
		vo25.setHc2("60");
		vo25.setHc1_id("ZCFZ-027");
		vo25.setHc2_id("ZCFZ-060");

		ZcFzBVO vo26 = new ZcFzBVO();
		vo26 = getZCFZBVO(map, vo26, true, "1711");
		// vo26=getZCFZBVO(map,vo26,false,"4201");
		vo26.setZc("　商誉");
		vo26.setFzhsyzqy("所有者权益(或股东权益)：");
		// vo26.setFzhsyzqy(" 减：库存股") ;
		vo26.setHc1("28");
		vo26.setHc1_id("ZCFZ-028");
		// vo26.setHc2("58") ;

		ZcFzBVO vo27 = new ZcFzBVO();
		vo27 = getZCFZBVO(map, vo27, true, "1801");
		vo27 = getZCFZBVO(map, vo27, false, "4001");
		// vo27=getZCFZBVO(map,vo27,false,"4301");
		vo27.setZc("　长期待摊费用");
		vo27.setFzhsyzqy("　实收资本(或股本)");
		// vo27.setFzhsyzqy(" 专项储备") ;
		vo27.setHc1("29");
		vo27.setHc2("61");
		vo27.setHc1_id("ZCFZ-029");
		vo27.setHc2_id("ZCFZ-061");

		ZcFzBVO vo28 = new ZcFzBVO();
		vo28 = getZCFZBVO(map, vo28, true, "1811");
		// vo28=getZCFZBVO(map,vo28,false,"4101");
		// vo28.setFzhsyzqy(" 盈余公积") ;
		vo28.setZc("　递延所得税资产");
		vo28.setFzhsyzqy("　其他权益工具");
		vo28.setHc1("30");
		vo28.setHc2("62");
		vo28.setHc1_id("ZCFZ-030");
		vo28.setHc2_id("ZCFZ-062");

		ZcFzBVO vo29 = new ZcFzBVO();
		vo29 = getZCFZBVO(map, vo29, true, "1623", "1901");
		vo29.setZc("　其他非流动资产");
		// vo29=getZCFZBVO(map,vo29,false,"4103","4104");
		// vo29.setFzhsyzqy(" 未分配利润") ;
		vo29.setFzhsyzqy("　　其中：优先股");
		vo29.setHc1("31");
		vo29.setHc2("63");
		vo29.setHc1_id("ZCFZ-031");
		vo29.setHc2_id("ZCFZ-063");

		ZcFzBVO vo30 = new ZcFzBVO();
		vo30.setQmye1(VoUtils.getDZFDouble(vo13.getQmye1()).add(VoUtils.getDZFDouble(vo14.getQmye1()))
				.add(VoUtils.getDZFDouble(vo15.getQmye1())).add(VoUtils.getDZFDouble(vo16.getQmye1()))
				.add(VoUtils.getDZFDouble(vo17.getQmye1()))
				.add(VoUtils.getDZFDouble(vo18.getQmye1()).add(VoUtils.getDZFDouble(vo19.getQmye1())))
				.add(VoUtils.getDZFDouble(vo20.getQmye1())).add(VoUtils.getDZFDouble(vo21.getQmye1()))
				.add(VoUtils.getDZFDouble(vo22.getQmye1())).add(VoUtils.getDZFDouble(vo23.getQmye1()))
				.add(VoUtils.getDZFDouble(vo24.getQmye1())).add(VoUtils.getDZFDouble(vo25.getQmye1())
						.add(z(vo26.getQmye1()).add(z(vo27.getQmye1()))).add(VoUtils.getDZFDouble(vo28.getQmye1())))
				.add(VoUtils.getDZFDouble(vo29.getQmye1())));
		vo30.setNcye1(VoUtils.getDZFDouble(vo13.getNcye1()).add(VoUtils.getDZFDouble(vo14.getNcye1()))
				.add(VoUtils.getDZFDouble(vo15.getNcye1())).add(VoUtils.getDZFDouble(vo16.getNcye1()))
				.add(VoUtils.getDZFDouble(vo17.getNcye1()))
				.add(VoUtils.getDZFDouble(vo18.getNcye1()).add(z(vo19.getNcye1())))
				.add(VoUtils.getDZFDouble(vo20.getNcye1())).add(VoUtils.getDZFDouble(vo21.getNcye1()))
				.add(VoUtils.getDZFDouble(vo22.getNcye1())).add(VoUtils.getDZFDouble(vo23.getNcye1()))
				.add(VoUtils.getDZFDouble(vo24.getNcye1())).add(VoUtils.getDZFDouble(vo25.getNcye1())
						.add(z(vo26.getNcye1()).add(z(vo27.getNcye1()))).add(VoUtils.getDZFDouble(vo28.getNcye1())))
				.add(VoUtils.getDZFDouble(vo29.getNcye1())));

		// vo30.setQmye2(VoUtils.getDZFDouble(vo24.getQmye2()).add(VoUtils.getDZFDouble(vo25.getQmye2())).sub(VoUtils.getDZFDouble(vo26.getQmye2())).add(VoUtils.getDZFDouble(vo27.getQmye2())).add(VoUtils.getDZFDouble(vo28.getQmye2())).add(VoUtils.getDZFDouble(vo29.getQmye2())))
		// ;
		// vo30.setNcye2(VoUtils.getDZFDouble(vo24.getNcye2()).add(VoUtils.getDZFDouble(vo25.getNcye2())).sub(VoUtils.getDZFDouble(vo26.getNcye2())).add(VoUtils.getDZFDouble(vo27.getNcye2())).add(VoUtils.getDZFDouble(vo28.getNcye2())).add(VoUtils.getDZFDouble(vo29.getNcye2())))
		// ;

		vo30.setZc("非流动资产合计");
		vo30.setFzhsyzqy("　　　　　永续债");
		// vo30.setFzhsyzqy("所有者权益(或股东权益)合计") ;
		vo30.setHc1("32");
		vo30.setHc2("64");
		vo30.setHc1_id("ZCFZ-032");
		vo30.setHc2_id("ZCFZ-064");

		ZcFzBVO vo30_1 = new ZcFzBVO();
		vo30_1.setZc("");
		vo30_1 = getZCFZBVO(map, vo30_1, false, "4002");
		vo30_1.setFzhsyzqy("　资本公积");
		vo30_1.setHc1("");
		vo30_1.setHc2("65");
		vo30_1.setHc2_id("ZCFZ-065");

		ZcFzBVO vo30_2 = new ZcFzBVO();
		vo30_2.setZc("");
		vo30_2 = getZCFZBVO(map, vo30_2, false, "4201");
		vo30_2.setFzhsyzqy("　减：库存股");
		vo30_2.setHc1("");
		vo30_2.setHc2("66");
		vo30_2.setHc2_id("ZCFZ-066");

		ZcFzBVO vo30_3 = new ZcFzBVO();
		vo30_3.setZc("");
		// vo30_3=getZCFZBVO(map,vo30_3,false,"4201");
		vo30_3.setFzhsyzqy("　其他综合收益");
		vo30_3.setHc1("");
		vo30_3.setHc2("67");
		vo30_3.setHc2_id("ZCFZ-067");

		ZcFzBVO vo30_4 = new ZcFzBVO();
		vo30_4.setZc("");
		vo30_4 = getZCFZBVO(map, vo30_4, false, "4101");
		vo30_4.setFzhsyzqy("　盈余公积");
		vo30_4.setHc1("");
		vo30_4.setHc2("68");
		vo30_4.setHc2_id("ZCFZ-068");

		ZcFzBVO vo30_5 = new ZcFzBVO();
		vo30_5.setZc("");
		vo30_5 = getZCFZBVO(map, vo30_5, false, "4103", "4104");
		vo30_5.setFzhsyzqy("　未分配利润");
		vo30_5.setHc1("");
		vo30_5.setHc2("69");
		vo30_5.setHc2_id("ZCFZ-069");

		ZcFzBVO vo30_6 = new ZcFzBVO();
		vo30_6.setZc("");
		vo30_6.setQmye2(VoUtils.getDZFDouble(vo27.getQmye2()).add(VoUtils.getDZFDouble(vo28.getQmye2()))
				.sub(VoUtils.getDZFDouble(vo30_2.getQmye2())).add(VoUtils.getDZFDouble(vo30_1.getQmye2()))
				.add(VoUtils.getDZFDouble(vo30_3.getQmye2())).add(VoUtils.getDZFDouble(vo30_4.getQmye2()))
				.add(VoUtils.getDZFDouble(vo30_5.getQmye2())));
		vo30_6.setNcye2(VoUtils.getDZFDouble(vo27.getNcye2()).add(VoUtils.getDZFDouble(vo28.getNcye2()))
				.sub(VoUtils.getDZFDouble(vo30_2.getNcye2())).add(VoUtils.getDZFDouble(vo30_1.getNcye2()))
				.add(VoUtils.getDZFDouble(vo30_3.getNcye2())).add(VoUtils.getDZFDouble(vo30_4.getNcye2()))
				.add(VoUtils.getDZFDouble(vo30_5.getNcye2())));
		vo30_6.setFzhsyzqy("所有者权益(或股东权益)合计");
		vo30_6.setHc1("");
		vo30_6.setHc2("70");
		vo30_6.setHc2_id("ZCFZ-070");

		ZcFzBVO vo31 = new ZcFzBVO();
		// ?C18+?C37 ?D18+?D37 --vo12+vo30
		vo31.setQmye1(VoUtils.getDZFDouble(vo12.getQmye1()).add(VoUtils.getDZFDouble(vo30.getQmye1())));
		vo31.setNcye1(VoUtils.getDZFDouble(vo12.getNcye1()).add(VoUtils.getDZFDouble(vo30.getNcye1())));
		// ?G29+?G36 ?H29+?H36 ---vo22+vo29
		vo31.setQmye2(VoUtils.getDZFDouble(vo25.getQmye2()).add(VoUtils.getDZFDouble(vo30_6.getQmye2())));
		vo31.setNcye2(VoUtils.getDZFDouble(vo25.getNcye2()).add(VoUtils.getDZFDouble(vo30_6.getNcye2())));

		vo31.setZc("资产总计");
		vo31.setFzhsyzqy("负债和所有者权益(或股东权益)总计");
		vo31.setHc1("33");
		vo31.setHc2("71");
		vo31.setHc1_id("ZCFZ-033");
		vo31.setHc2_id("ZCFZ-071");

		ZcFzBVO[] zcfzvos = new ZcFzBVO[] { vo0, vo1, vo2, vo2_1, vo3, vo4, vo5, vo6, vo7, vo8, vo9, vo9_1, vo10, vo11,
				vo12, vo00, vo13, vo14, vo15, vo16, vo17, vo18, vo19, vo20, vo21, vo22, vo23, vo24, vo25, vo26, vo27,
				vo28, vo29, vo30, vo30_1, vo30_2, vo30_3, vo30_4, vo30_5, vo30_6, vo31 };
		// for(ZcFzBVO vo:zcfzvos){
		// System.out.println(vo.toString());
		// }
		return zcfzvos;
	}
	
	private ZcFzBVO getZcFzBVO_YJSF_CFL(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mapc,
			String queryAccountRule, ZcFzBVO vo6) {
		List<String> list = getYSJFKmList(queryAccountRule);
		ZcFzBVO vo6temp = new ZcFzBVO();
		vo6temp = getZCFZBVO1(map, vo6temp, false, mapc,true, list.toArray(new String[0]));
		//2221下的所有科目
		List<String> orderlist = new ArrayList<String>();
		String filtercode = gl_accountcoderule.getNewRuleCode("222114",DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		for (Entry<String, YntCpaccountVO> entry : mapc.entrySet()) {
			if (entry.getValue() != null && entry.getValue().getAccountcode().startsWith("2221")
					&& entry.getValue().getAccountlevel() ==2 
					) {//只是核算2级科目
				if (!list.contains(entry.getValue().getAccountcode())
						&& !filtercode.equals(entry.getValue().getAccountcode()) ) {
					orderlist.add(entry.getValue().getAccountcode());
				}
			}
		}
		// 不需要重分类的科目
		vo6 = getZCFZBVO(map, vo6, false, orderlist.toArray(new String[0]));
		vo6.setNcye2(SafeCompute.add(vo6temp.getNcye2(), vo6.getNcye2()));
		vo6.setQmye2(SafeCompute.add(vo6temp.getQmye2(), vo6.getQmye2()));
		vo6.setFzconkms(vo6.getFzconkms()+","+vo6temp.getFzconkms());
		return vo6;
	}
	
	/**
	 * 应交税费科目list
	 * @param queryAccountRule
	 * @return
	 */
	private List<String> getYSJFKmList(String queryAccountRule) {
		// 需要重分类的科目
		String[] kms = new String[] { "222101", "222109", "222110", "222113", "222115" };
		//转换成新的list
		List<String> list  = new ArrayList<String>();
		String[] kms1  = gl_accountcoderule.getNewCodes(kms,  DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		list = Arrays.asList(kms1);
		return list;
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
		// 获取工程施工，工程结算差额
		DZFDouble[] cefrom0102 = new DZFDouble[] { DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL };// getCeFrom0102(map,"4401","4402");
		for (int i = 0; i < len; i++) {
			ls = getData(map, kms[i]);
			appkms.append(kms[i] + ",");
			if (ls == null)
				continue;
			if ("　存货".equals(vo.getZc()) && (kms[i].equals("4401") || kms[i].equals("4402"))) {// 13工程结算单独计算
				cefrom0102 = getCeFrom0102(map, "4401", "4402");
				continue;
			}
			if ("　存货".equals(vo.getZc()) && (kms[i].equals("5401") || kms[i].equals("5402"))) {// 07工程结算
				cefrom0102 = getCeFrom0102(map, "5401", "5402");
				continue;
			}
			if ("　预收账款".equals(vo.getFzhsyzqy()) && (kms[i].equals("4401") || kms[i].equals("4402"))) {// 13预收账款
				cefrom0102 = getCeFrom0102(map, "4401", "4402");
				continue;
			}
			if ("　预收款项".equals(vo.getFzhsyzqy()) && (kms[i].equals("5401") || kms[i].equals("5402"))) {// 07
																										// 工程结算
				cefrom0102 = getCeFrom0102(map, "5401", "5402");
				continue;
			}
			for (FseJyeVO fvo : ls) {
				if (is1) {
					ufd = VoUtils.getDZFDouble(vo.getQmye1());
					DZFDouble qmjf = VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf = VoUtils.getDZFDouble(fvo.getQmdf());
					if (fvo.getKmbm().equals("1407") || fvo.getKmbm().equals("2314")) {
						qmjf = VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1);
						qmdf = VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1);
					}
					// DZFDouble qmjf = ?
					// VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1) :
					// VoUtils.getDZFDouble(fvo.getQmjf());
					// DZFDouble qmdf = ?
					// VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1) :
					// VoUtils.getDZFDouble(fvo.getQmdf());
					ufd = ufd.add(qmjf).add(qmdf);

					vo.setQmye1(ufd);
					ufd = VoUtils.getDZFDouble(vo.getNcye1());
					DZFDouble qcjf = VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = VoUtils.getDZFDouble(fvo.getQcdf());
					if (fvo.getKmbm().equals("1407") || fvo.getKmbm().equals("2314")) {
						qcjf = VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1);
						qcdf = VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1);
					}
					// DZFDouble qcjf = fvo.getKmbm().equals("1407") ?
					// VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1) :
					// VoUtils.getDZFDouble(fvo.getQcjf());
					// DZFDouble qcdf = fvo.getKmbm().equals("1407") ?
					// VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1) :
					// VoUtils.getDZFDouble(fvo.getQcdf());
					ufd = ufd.add(qcjf).add(qcdf);

					vo.setNcye1(ufd);
					// 本月期初
					ufd = VoUtils.getDZFDouble(vo.getQcye1());
					DZFDouble byqcjf = VoUtils.getDZFDouble(fvo.getLastmqcjf());
					DZFDouble bqqcdf = VoUtils.getDZFDouble(fvo.getLastmqcdf());
					if (fvo.getKmbm().equals("1407") || fvo.getKmbm().equals("2314")) {
						byqcjf = VoUtils.getDZFDouble(fvo.getLastmqcjf()).multiply(-1);
						bqqcdf = VoUtils.getDZFDouble(fvo.getLastmqcdf()).multiply(-1);
					}

					// DZFDouble byqcjf = fvo.getKmbm().equals("1407") ?
					// VoUtils.getDZFDouble(fvo.getLastmqcjf()).multiply(-1) :
					// VoUtils.getDZFDouble(fvo.getLastmqcjf());
					// DZFDouble bqqcdf = fvo.getKmbm().equals("1407") ?
					// VoUtils.getDZFDouble(fvo.getLastmqcdf()).multiply(-1) :
					// VoUtils.getDZFDouble(fvo.getLastmqcdf());
					ufd = ufd.add(byqcjf).add(bqqcdf);
					vo.setQcye1(ufd);
				} else {
					ufd = VoUtils.getDZFDouble(vo.getQmye2());
					ufd = ufd.add(VoUtils.getDZFDouble(fvo.getQmjf())).add(VoUtils.getDZFDouble(fvo.getQmdf()));// 只有一方有值
					vo.setQmye2(ufd);
					ufd = VoUtils.getDZFDouble(vo.getNcye2());
					ufd = ufd.add(VoUtils.getDZFDouble(fvo.getQcjf())).add(VoUtils.getDZFDouble(fvo.getQcdf()));// 只有一方有值
					vo.setNcye2(ufd);

					// 本月期初
					ufd = VoUtils.getDZFDouble(vo.getQcye2());
					ufd = ufd.add(VoUtils.getDZFDouble(fvo.getLastmqcjf()))
							.add(VoUtils.getDZFDouble(fvo.getLastmqcdf()));// 只有一方有值
					vo.setQcye2(ufd);
				}
			}
		}

		// 07 13 写在了一块了
		if ("　存货".equals(vo.getZc())) {
			// 获取工程施工，工程结算差额
			if (cefrom0102[0].doubleValue() > 0) {
				vo.setNcye1(SafeCompute.add(vo.getNcye1(), cefrom0102[0]));
			}
			if (cefrom0102[1].doubleValue() > 0) {
				vo.setQmye1(SafeCompute.add(vo.getQmye1(), cefrom0102[1]));
			}
		}
		if ("　预收款项".equals(vo.getFzhsyzqy()) || "　预收账款".equals(vo.getFzhsyzqy())) {
			// 获取工程施工，工程结算差额
			if (cefrom0102[0].doubleValue() < 0) {
				vo.setNcye2(SafeCompute.add(vo.getNcye2(), cefrom0102[0].multiply(-1)));
			}
			if (cefrom0102[1].doubleValue() < 0) {
				vo.setQmye2(SafeCompute.add(vo.getQmye2(), cefrom0102[1].multiply(-1)));
			}
		}

		if (is1) {
			vo.setZcconkms(appkms.toString().substring(0, appkms.length() - 1));
		} else {
			vo.setFzconkms(appkms.toString().substring(0, appkms.length() - 1));
		}
		return vo;
	}

	// 往来分析
	private ZcFzBVO getZCFZBVO1(Map<String, FseJyeVO> map, ZcFzBVO vo, boolean is1, Map<String, YntCpaccountVO> mapc,boolean bthislevel,
			String... kms) {

		List<FseJyeVO> ls = null;
		DZFDouble ufd = null;
		int len = kms == null ? 0 : kms.length;
		StringBuffer appkms = null;
		if (is1) {
			appkms = new StringBuffer(vo.getZcconkms() == null ? "" : vo.getZcconkms() + ",");
		} else {
			appkms = new StringBuffer(vo.getFzconkms() == null ? "" : vo.getFzconkms() + ",");
		}
		DZFDouble[] cefrom0102 = new DZFDouble[] { DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL };
		for (int i = 0; i < len; i++) {
			ls = getData1(map, kms[i], mapc,bthislevel);
			appkms.append(kms[i] + ",");
			if ("　预收账款".equals(vo.getFzhsyzqy()) && (kms[i].equals("4401") || kms[i].equals("4402"))) {// 13预收账款
				cefrom0102 = getCeFrom0102(map, "4401", "4402");
				continue;
			}
			if ("　预收款项".equals(vo.getFzhsyzqy()) && (kms[i].equals("5401") || kms[i].equals("5402"))) {// 07
																										// 工程结算
				cefrom0102 = getCeFrom0102(map, "5401", "5402");
				continue;
			}
			if (ls == null)
				continue;
			for (FseJyeVO fvo : ls) {
				if (is1) {// 资产
					ufd = VoUtils.getDZFDouble(vo.getQmye1());
					DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQmdf());
					if ("借".equals(fvo.getFx()) && qmjf.doubleValue() > 0) {
						ufd = ufd.add(qmjf);
					} else if (qmdf.doubleValue() < 0) {
						ufd = ufd.sub(qmdf);
					}
					vo.setQmye1(ufd);
					//
					ufd = VoUtils.getDZFDouble(vo.getNcye1());
					DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQcdf());
					if ("借".equals(fvo.getFx()) && qcjf.doubleValue() > 0) {
						ufd = ufd.add(qcjf);
					} else if (qcdf.doubleValue() < 0) {
						ufd = ufd.sub(qcdf);
					}
					vo.setNcye1(ufd);
				} else {// 负债
					ufd = VoUtils.getDZFDouble(vo.getQmye2());
					DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQmdf());
					if ("贷".equals(fvo.getFx()) && qmdf.doubleValue() > 0) {
						ufd = ufd.add(qmdf);
					} else if (qmjf.doubleValue() < 0) {
						ufd = ufd.sub(qmjf);
					}
					vo.setQmye2(ufd);
					//
					ufd = VoUtils.getDZFDouble(vo.getNcye2());
					DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQcdf());
					if ("贷".equals(fvo.getFx()) && qcdf.doubleValue() > 0) {
						ufd = ufd.add(qcdf);
					} else if (qcjf.doubleValue() < 0) {
						ufd = ufd.sub(qcjf);
					}
					vo.setNcye2(ufd);
				}
			}
		}
		if (is1) {
			vo.setZcconkms(appkms.toString().substring(0, appkms.length() - 1));
		} else {
			vo.setFzconkms(appkms.toString().substring(0, appkms.length() - 1));
		}
		if ("　预收款项".equals(vo.getFzhsyzqy()) || "　预收账款".equals(vo.getFzhsyzqy())) {
			// 获取工程施工，工程结算差额
			if (cefrom0102[0].doubleValue() < 0) {
				vo.setNcye2(SafeCompute.add(vo.getNcye2(), cefrom0102[0].multiply(-1)));
			}
			if (cefrom0102[1].doubleValue() < 0) {
				vo.setQmye2(SafeCompute.add(vo.getQmye2(), cefrom0102[1].multiply(-1)));
			}
		}

		return vo;
	}

	private List<FseJyeVO> getData(Map<String, FseJyeVO> map, String km) {
		List<FseJyeVO> list = new ArrayList<FseJyeVO>();
		for (FseJyeVO fsejyevo : map.values()) {
			if (fsejyevo.getKmbm().equals(km)) {// 已经合计过了，不要用startwidh
				list.add(fsejyevo);
				break;
			}
		}
		return list;
	}

	public DZFDouble z(DZFDouble x) {
		if (x == null)
			return DZFDouble.ZERO_DBL;
		return x;
	}

	private DZFDouble[] getCeFrom0102(Map<String, FseJyeVO> map, String str1, String str2) {
		DZFDouble qcd1 = DZFDouble.ZERO_DBL;
		DZFDouble qcd2 = DZFDouble.ZERO_DBL;
		DZFDouble qmd1 = DZFDouble.ZERO_DBL;
		DZFDouble qmd2 = DZFDouble.ZERO_DBL;
		FseJyeVO fsevo1 = null;
		FseJyeVO fsevo2 = null;
		if (!StringUtil.isEmpty(str1)) {
			fsevo1 = map.get(str1);
		}
		if (!StringUtil.isEmpty(str2)) {
			fsevo2 = map.get(str2);
		}
		if (fsevo1 != null) {
			qcd1 = SafeCompute.add(fsevo1.getQcjf(), fsevo1.getQcdf());
			qmd1 = SafeCompute.add(fsevo1.getQmjf(), fsevo1.getQmdf());
		}
		if (fsevo2 != null) {
			qcd2 = SafeCompute.add(fsevo2.getQcjf(), fsevo2.getQcdf());
			qmd2 = SafeCompute.add(fsevo2.getQmjf(), fsevo2.getQmdf());
		}

		return new DZFDouble[] { qcd1.sub(qcd2), qmd1.sub(qmd2) };
	}

	private List<FseJyeVO> getData1(Map<String, FseJyeVO> map, String km, Map<String, YntCpaccountVO> mapc,boolean bthislevel) {
		List<FseJyeVO> list = new ArrayList<FseJyeVO>();
		for (FseJyeVO fsejyevo : map.values()) {
			String kmbm = null;
			if (fsejyevo.getKmbm().indexOf("_") > 0) {
				kmbm = fsejyevo.getKmbm().split("_")[0];
			} else {
				kmbm = fsejyevo.getKmbm();
			}
			if(bthislevel){
				if(kmbm.equals(km)){
					list.add(fsejyevo);
					return list;
				}
			}else{
				if (kmbm.startsWith(km)) {
					YntCpaccountVO xs = mapc.get(kmbm);
					if (xs.getIsleaf() != null && xs.getIsleaf().booleanValue()) {
						if (xs.getIsfzhs().indexOf("1") >= 0 && fsejyevo.getKmbm().indexOf("_") >= 0) {// 包含辅助项目
							list.add(fsejyevo);
						} else if (xs.getIsfzhs().indexOf("1") < 0) {// 如果不包含辅助项目处理
							list.add(fsejyevo);
						}
					}
				}
			}
		}
		return list;
	}

}
