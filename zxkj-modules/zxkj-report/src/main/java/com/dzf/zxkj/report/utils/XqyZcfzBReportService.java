package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


@SuppressWarnings("all")
public class XqyZcfzBReportService {
	
	private IZxkjPlatformService zxkjPlatformService ;
	

	public XqyZcfzBReportService(IZxkjPlatformService zxkjPlatformService) {
		super();
		this.zxkjPlatformService = zxkjPlatformService;
	}

	public ZcFzBVO[] getZCFZB2013VOs(Map<String, FseJyeVO> map, String[] hasyes,
									 Map<String, YntCpaccountVO> mapc, String queryAccountRule) throws DZFWarpException {
		if (hasyes == null || hasyes.length != 5 || !"Y".equals(hasyes[0])) {
			hasyes = new String[] { "N", "N", "N", "N","N" };
		}
		
		ZcFzBVO vo0 = new ZcFzBVO() ;
		vo0.setZc("流动资产：") ;
		vo0.setFzhsyzqy("流动负债：") ;
	
		ZcFzBVO vo1 = new ZcFzBVO() ;
		vo1=getZCFZBVO(map,vo1,true,"1001","1002","1012");
		vo1=getZCFZBVO(map,vo1,false,"2001");
		vo1.setZc("　货币资金") ;
		vo1.setFzhsyzqy("　短期借款") ;
		vo1.setHc1("1") ;
		vo1.setHc2("31") ;
		vo1.setHc1_id("ZCFZ-001");
		vo1.setHc2_id("ZCFZ-031");
	
		
		ZcFzBVO vo2 = new ZcFzBVO() ;
		vo2=getZCFZBVO(map,vo2,true,"1101");
		vo2=getZCFZBVO(map,vo2,false,"2201");
		vo2.setZc("　短期投资") ;
		vo2.setFzhsyzqy("　应付票据") ;
		vo2.setHc1("2") ;
		vo2.setHc2("32") ;
		vo2.setHc1_id("ZCFZ-002");
		vo2.setHc2_id("ZCFZ-032");
		
		
		ZcFzBVO vo3 = new ZcFzBVO();
		 vo3=getZCFZBVO(map,vo3,true,"1121");
		 if("Y".equals(hasyes[2])){
			 //2013小会计     应付账款=2202贷方金额+1123贷方金额
			 vo3=getZCFZBVO1(map,vo3,false,mapc,false,"2202","1123");
		 }else{
			 vo3=getZCFZBVO(map,vo3,false,"2202");
		 }
		vo3.setZc("　应收票据");
		vo3.setFzhsyzqy("　应付账款");
		vo3.setHc1("3") ;
		vo3.setHc2("33") ;
		vo3.setHc1_id("ZCFZ-003");
		vo3.setHc2_id("ZCFZ-033");
		
		ZcFzBVO vo4 = new ZcFzBVO() ;
		vo4.setZc("　应收账款") ;
		vo4.setFzhsyzqy("　预收账款") ;
		//2013小会计     应收账款=1122借方金额+2203借方金额
		//2013小会计     预收账款=1122贷方金额+2203贷方金额
		if("Y".equals(hasyes[1])){//应收和预收
			vo4=getZCFZBVO1(map,vo4,true,mapc,false,"1122","2203");
			vo4=getZCFZBVO1(map,vo4,false,mapc,false,"2203","1122","4401","4402");
		}else{
			vo4=getZCFZBVO(map,vo4,true,"1122");
			vo4=getZCFZBVO(map,vo4,false,"2203","4401","4402");
		}
		vo4.setHc1("4") ;
		vo4.setHc2("34") ;
		vo4.setHc1_id("ZCFZ-004");
		vo4.setHc2_id("ZCFZ-034");
	
		ZcFzBVO vo5 = new ZcFzBVO() ;
		if("Y".equals(hasyes[2])){
			//2013小会计     预付账款=1123借方金额+2202借方金额
			 vo5=getZCFZBVO1(map,vo5,true,mapc,false,"1123","2202");
		}else{
			 vo5=getZCFZBVO(map,vo5,true,"1123");
		}
		vo5=getZCFZBVO(map,vo5,false,"2211");
		vo5.setZc("　预付款项") ;
		vo5.setFzhsyzqy("　应付职工薪酬") ;
		vo5.setHc1("5") ;
		vo5.setHc2("35") ;
		vo5.setHc1_id("ZCFZ-005");
		vo5.setHc2_id("ZCFZ-035");
	
		ZcFzBVO vo6 = new ZcFzBVO() ;
		vo6=getZCFZBVO(map,vo6,true,"1131");
		//应交税费重分类
		if ("Y".equals(hasyes[4])) {
			//应交税费重分类取数
			vo6 = getZcFzBVO_YJSF_CFL(map, mapc, queryAccountRule, vo6);
		}else{
			vo6=getZCFZBVO(map,vo6,false,"2221");
		}
		vo6.setZc("　应收股利") ;
		vo6.setFzhsyzqy("　应交税费") ;
		vo6.setHc1("6") ;
		vo6.setHc2("36") ;
		vo6.setHc1_id("ZCFZ-006");
		vo6.setHc2_id("ZCFZ-036");
		
		
		ZcFzBVO vo7 = new ZcFzBVO() ;
		vo7=getZCFZBVO(map,vo7,true,"1132");
		vo7=getZCFZBVO(map,vo7,false,"2231");
		vo7.setZc("　应收利息") ;
		vo7.setFzhsyzqy("　应付利息") ;
		vo7.setHc1("7") ;
		vo7.setHc2("37") ;
		vo7.setHc1_id("ZCFZ-007");
		vo7.setHc2_id("ZCFZ-037");
		

		ZcFzBVO vo8 = new ZcFzBVO() ;
		if("Y".equals(hasyes[3])){
			vo8=getZCFZBVO1(map,vo8,true,mapc,false,"1221","2241","1200");
		}else{
			vo8=getZCFZBVO(map,vo8,true,"1221","1200");
		}
		vo8=getZCFZBVO(map,vo8,false,"2232");
		vo8.setZc("　其他应收款") ;
		vo8.setFzhsyzqy("　应付利润") ;
		vo8.setHc1("8") ;
		vo8.setHc2("38") ;
		vo8.setHc1_id("ZCFZ-008");
		vo8.setHc2_id("ZCFZ-038");
		
		
		ZcFzBVO vo9 = new ZcFzBVO() ;
		vo9.setZc("　存货") ;
		vo9.setFzhsyzqy("　其他应付款") ;
		vo9=getZCFZBVO(map,vo9,true,"1401","1402","1403","1404","1405","1407","1408","1409", 
				 "1411","1421","4001","4002","4101","4401","4402","4410","4403","1321","2314");
		if("Y".equals(hasyes[3])){
			vo9=getZCFZBVO1(map,vo9,false,mapc,false,"1221","2241","1200");
		}else{
			vo9=getZCFZBVO(map,vo9,false,"2241");
		}
		vo9.setHc1("9") ;
		vo9.setHc2("39") ;
		vo9.setHc1_id("ZCFZ-009");
		vo9.setHc2_id("ZCFZ-039");
	
		ZcFzBVO vo10 = new ZcFzBVO() ;
		vo10=getZCFZBVO(map,vo10,true,"1403");
		if ("Y".equals(hasyes[4])) {
			String filtercode = zxkjPlatformService.getNewRuleCode("222114", DZFConstant.ACCOUNTCODERULE,
					queryAccountRule);
			vo10 = getZCFZBVO(map,vo10,false,filtercode);
		}
		vo10.setZc("　其中：原材料") ;
		vo10.setFzhsyzqy("　其他流动负债") ;
		vo10.setHc1("10") ;
		vo10.setHc2("40") ;
		vo10.setHc1_id("ZCFZ-010");
		vo10.setHc2_id("ZCFZ-040");
		
		ZcFzBVO vo11 = new ZcFzBVO() ;
		vo11=getZCFZBVO(map,vo11,true,"4001");
		vo11.setQmye2(VoUtils.getDZFDouble(vo1.getQmye2()).add(VoUtils.getDZFDouble(vo2.getQmye2())).add(VoUtils.getDZFDouble(vo3.getQmye2())).add(VoUtils.getDZFDouble(vo4.getQmye2())).
				add(VoUtils.getDZFDouble(vo5.getQmye2())).add(VoUtils.getDZFDouble(vo6.getQmye2())).add(VoUtils.getDZFDouble(vo7.getQmye2()))
				.add(VoUtils.getDZFDouble(vo8.getQmye2())).add(VoUtils.getDZFDouble(vo9.getQmye2())).add(VoUtils.getDZFDouble(vo10.getQmye2()))) ;
		vo11.setNcye2(VoUtils.getDZFDouble(vo1.getNcye2()).add(VoUtils.getDZFDouble(vo2.getNcye2())).add(VoUtils.getDZFDouble(vo3.getNcye2())).add(VoUtils.getDZFDouble(vo4.getNcye2())).
				add(VoUtils.getDZFDouble(vo5.getNcye2())).add(VoUtils.getDZFDouble(vo6.getNcye2())).add(VoUtils.getDZFDouble(vo7.getNcye2()))
				.add(VoUtils.getDZFDouble(vo8.getNcye2())).add(VoUtils.getDZFDouble(vo9.getNcye2())).add(VoUtils.getDZFDouble(vo10.getNcye2()))) ;
		vo11.setZc("　　　　在产品") ;
		vo11.setFzhsyzqy("流动负债合计") ;
		vo11.setHc1("11") ;
		vo11.setHc2("41") ;
		vo11.setHc1_id("ZCFZ-011");
		vo11.setHc2_id("ZCFZ-041");
		
		ZcFzBVO vo12 = new ZcFzBVO() ;
		vo12=getZCFZBVO(map,vo12,true,"1405");
		vo12.setZc("　　　　库存商品") ;
		vo12.setFzhsyzqy("非流动负债：") ;
		vo12.setHc1("12") ;
		vo12.setHc2(null) ;
		vo12.setHc1_id("ZCFZ-012");
		vo12.setHc2_id("");
		
		ZcFzBVO vo13 = new ZcFzBVO() ;
		vo13=getZCFZBVO(map,vo13,true,"1411");
		vo13=getZCFZBVO(map,vo13,false,"2501");
		vo13.setZc("　　　　周转材料") ;
		vo13.setFzhsyzqy("　长期借款") ;
		vo13.setHc1("13") ;
		vo13.setHc2("42") ;
		vo13.setHc1_id("ZCFZ-013");
		vo13.setHc2_id("ZCFZ-042");
		
		ZcFzBVO vo14 = new ZcFzBVO() ;
		//QM("2701",月,,,年,,)	QC("2701",全年,,,年,,)
//		vo14=getZCFZBVO(map,vo14,true,"1901");
		if("Y".equals(hasyes[4])){
			vo14=getZCFZBVO1(map, vo14	,true, mapc,true, getYSJFKmList(queryAccountRule).toArray(new String[0]));
		}
		vo14=getZCFZBVO(map,vo14,false,"2701");
		vo14.setZc("　其他流动资产") ;
		vo14.setFzhsyzqy("　长期应付款") ;
		vo14.setHc1("14") ;
		vo14.setHc2("43") ;
		vo14.setHc1_id("ZCFZ-014");
		vo14.setHc2_id("ZCFZ-043");
		
		ZcFzBVO vo15 = new ZcFzBVO() ;
		//流动资产合计=货币资金1+短期投资2+应收票据3+预付款项5+应收股利6+应收利息7++应收账款4+其他应收款8+存货9+其他流动资产14
		DZFDouble qmye15_1 = VoUtils.getDZFDouble(vo1.getQmye1())
				.add(VoUtils.getDZFDouble(vo2.getQmye1()))
				.add(VoUtils.getDZFDouble(vo3.getQmye1()))
				.add(VoUtils.getDZFDouble(vo5.getQmye1()))
				.add(VoUtils.getDZFDouble(vo6.getQmye1()))
				.add(VoUtils.getDZFDouble(vo7.getQmye1()))
				.add(VoUtils.getDZFDouble(vo4.getQmye1()))
				.add(VoUtils.getDZFDouble(vo8.getQmye1()))
				.add(VoUtils.getDZFDouble(vo9.getQmye1()))
				.add(VoUtils.getDZFDouble(vo14.getQmye1())) ;
		DZFDouble ncye15_1 =VoUtils.getDZFDouble( vo1.getNcye1())
				.add(VoUtils.getDZFDouble(vo2.getNcye1()))
				.add(VoUtils.getDZFDouble(vo3.getNcye1()))
				.add(VoUtils.getDZFDouble(vo5.getNcye1()))
				.add(VoUtils.getDZFDouble(vo6.getNcye1()))
				.add(VoUtils.getDZFDouble(vo7.getNcye1()))
				.add(VoUtils.getDZFDouble(vo4.getNcye1()))
				.add(VoUtils.getDZFDouble(vo8.getNcye1()))
				.add(VoUtils.getDZFDouble(vo9.getNcye1()))
				.add(VoUtils.getDZFDouble(vo14.getNcye1())) ;
		vo15.setQmye1(qmye15_1) ;
		vo15.setNcye1(ncye15_1) ;
		vo15=getZCFZBVO(map,vo15,false,"2401");
		vo15.setZc("流动资产合计") ;
		vo15.setFzhsyzqy("　递延收益") ;
		vo15.setHc1("15") ;
		vo15.setHc2("44") ;
		vo15.setHc1_id("ZCFZ-015");
		vo15.setHc2_id("ZCFZ-044");
		
		ZcFzBVO vo00 = new ZcFzBVO() ;
		vo00.setZc("非流动资产：") ;
		vo00.setFzhsyzqy("　其他非流动负债 ") ;
		vo00.setHc1(null) ;
		vo00.setHc2("45") ;
		vo00.setHc1_id("");
		vo00.setHc2_id("ZCFZ-045");
		
		ZcFzBVO vo16 = new ZcFzBVO() ;
		vo16=getZCFZBVO(map,vo16,true,"1501");
		vo16.setQmye2(VoUtils.getDZFDouble(vo13.getQmye2()).add(VoUtils.getDZFDouble(vo14.getQmye2())).add(VoUtils.getDZFDouble(vo15.getQmye2()))) ;
		vo16.setNcye2(VoUtils.getDZFDouble(vo13.getNcye2()).add(VoUtils.getDZFDouble(vo14.getNcye2())).add(VoUtils.getDZFDouble(vo15.getNcye2()))) ;
		vo16.setZc("　长期债券投资") ;
		vo16.setFzhsyzqy("非流动负债合计") ;
		vo16.setHc1("16") ;
		vo16.setHc2("46") ;
		vo16.setHc1_id("ZCFZ-016");
		vo16.setHc2_id("ZCFZ-046");
		
		ZcFzBVO vo17 = new ZcFzBVO() ;
		vo17=getZCFZBVO(map,vo17,true,"1511");
		vo17.setQmye2(vo11.getQmye2().add(vo16.getQmye2())) ;
		vo17.setNcye2(vo11.getNcye2().add(vo16.getNcye2())) ;
		vo17.setZc("　长期股权投资") ;
		vo17.setFzhsyzqy("负债合计") ;
		vo17.setHc1("17") ;
		vo17.setHc2("47") ;
		vo17.setHc1_id("ZCFZ-017");
		vo17.setHc2_id("ZCFZ-047");
		
		ZcFzBVO vo18 = new ZcFzBVO() ;
		vo18=getZCFZBVO(map,vo18,true,"1601");
		vo18.setZc("　固定资产原价") ;
		vo18.setFzhsyzqy(null) ;
		vo18.setHc1("18") ;
		vo18.setHc2(null) ;
		vo18.setHc1_id("ZCFZ-018");
		vo18.setHc2_id("");
		
		ZcFzBVO vo19 = new ZcFzBVO() ;
		//QM("1602",月,,,年,,)	QC("1602",全年,,,年,,)
		vo19=getZCFZBVO(map,vo19,true,"1602");
//		vo19.setQmye1(getBQQMSByKm("1602", pk_corp, period)) ;
//		vo19.setNcye1(getBNQCSByKm("1602", pk_corp, period)) ;
		vo19.setZc("　减：累计折旧") ;
		vo19.setFzhsyzqy(null) ;
		vo19.setHc1("19") ;
		vo19.setHc2(null) ;
		vo19.setHc1_id("ZCFZ-019");
		vo19.setHc2_id("");

		ZcFzBVO vo20 = new ZcFzBVO() ;
		//2013年小企业制度下固定资产的账面价值年初余额=固定资产年初余额-累计折旧年初余额
		vo20.setQmye1(VoUtils.getDZFDouble(vo18.getQmye1()).sub(VoUtils.getDZFDouble(vo19.getQmye1()))) ;
		vo20.setNcye1(VoUtils.getDZFDouble(vo18.getNcye1()).sub(vo19.getNcye1()==null?DZFDouble.ZERO_DBL:vo19.getNcye1())) ;
		vo20.setZc("　固定资产账面价值") ;
		vo20.setFzhsyzqy(null) ;
		vo20.setHc1("20") ;
		vo20.setHc2(null) ;
		vo20.setHc1_id("ZCFZ-020");
		vo20.setHc2_id("");
		
		ZcFzBVO vo21 = new ZcFzBVO() ;
		//QM("1604",月,,,年,,)	QC("1604",全年,,,年,,)
		vo21=getZCFZBVO(map,vo21,true,"1604");
		vo21.setZc("　在建工程") ;
		vo21.setFzhsyzqy(null) ;
		vo21.setHc1("21") ;
		vo21.setHc2(null) ;
		vo21.setHc1_id("ZCFZ-021");
		vo21.setHc2_id("");
		
		ZcFzBVO vo22 = new ZcFzBVO() ;
		//QM("1605",月,,,年,,)	QC("1605",全年,,,年,,)
		vo22=getZCFZBVO(map,vo22,true,"1605");
		vo22.setZc("　工程物资") ;
		vo22.setFzhsyzqy(null) ;
		vo22.setHc1("22") ;
		vo22.setHc2(null) ;
		vo22.setHc1_id("ZCFZ-022");
		vo22.setHc2_id("");
		
		ZcFzBVO vo23 = new ZcFzBVO() ;
		//QM("1606",月,,,年,,)	QC("1606",全年,,,年,,)
		vo23=getZCFZBVO(map,vo23,true,"1606");
		vo23.setZc("　固定资产清理") ;
		vo23.setFzhsyzqy(null) ;
		vo23.setHc1("23") ;
		vo23.setHc2(null) ;
		vo23.setHc1_id("ZCFZ-023");
		vo23.setHc2_id("");
		
		ZcFzBVO vo24 = new ZcFzBVO() ;
		//QM("1621",月,,,年,,)-QM("1622",月,,,年,,)	QC("1621",全年,,,年,,)-QC("1622",全年,,,年,,)
		vo24=getZCFZBVO(map,vo24,true,"1622");
		vo24.setQmye1(VoUtils.getDZFDouble(vo24.getQmye1()).multiply(new DZFDouble(-1)));
		vo24.setNcye1(VoUtils.getDZFDouble(vo24.getNcye1()).multiply(new DZFDouble(-1)));
		vo24=getZCFZBVO(map,vo24,true,"1621");
		vo24.setZc("　生产性生物资产") ;
		vo24.setFzhsyzqy("所有者权益（或股东权益)：") ;
		vo24.setHc1("24") ;
		vo24.setHc2(null) ;
		vo24.setHc1_id("ZCFZ-024");
		vo24.setHc2_id("");
		
		ZcFzBVO vo25 = new ZcFzBVO() ;
		//QM("1701",月,,,年,,)-QM("1702",月,,,年,,)	QC("1701",全年,,,年,,)-QC("1702",全年,,,年,,)
		vo25=getZCFZBVO(map,vo25,true,"1702");
		vo25.setQmye1(VoUtils.getDZFDouble(vo25.getQmye1()).multiply(new DZFDouble(-1)));
		vo25.setNcye1(VoUtils.getDZFDouble(vo25.getNcye1()).multiply(new DZFDouble(-1)));
		vo25=getZCFZBVO(map,vo25,true,"1701");
		vo25=getZCFZBVO(map,vo25,false,"3001");
		vo25.setZc("　无形资产") ;
		vo25.setFzhsyzqy("　实收资本（或股本）") ;
		vo25.setHc1("25") ;
		vo25.setHc2("48") ;
		vo25.setHc1_id("ZCFZ-025");
		vo25.setHc2_id("ZCFZ-048");
				
		ZcFzBVO vo26 = new ZcFzBVO() ;
		//QM("4301",月,,,年,,)	QC("4301",全年,,,年,,)
		vo26=getZCFZBVO(map,vo26,true,"4301");
		vo26=getZCFZBVO(map,vo26,false,"3002");
		vo26.setZc("　开发支出") ;
		vo26.setFzhsyzqy("　资本公积") ;
		vo26.setHc1("26") ;
		vo26.setHc2("49") ;
		vo26.setHc1_id("ZCFZ-026");
		vo26.setHc2_id("ZCFZ-049");
		
		ZcFzBVO vo27 = new ZcFzBVO() ;
		//QM("1801",月,,,年,,)	QC("1801",全年,,,年,,)
		vo27=getZCFZBVO(map,vo27,true,"1801");
		vo27=getZCFZBVO(map,vo27,false,"3101");
		vo27.setZc("　长期待摊费用") ;
		vo27.setFzhsyzqy("　盈余公积") ;
		vo27.setHc1("27") ;
		vo27.setHc2("50") ;
		vo27.setHc1_id("ZCFZ-027");
		vo27.setHc2_id("ZCFZ-050");
		
		ZcFzBVO vo28 = new ZcFzBVO() ;
		vo28=getZCFZBVO(map,vo28,false,"3103","3104");
		vo28=getZCFZBVO(map,vo28,true,"1901");
		vo28.setZc("　其他非流动资产") ;
		vo28.setFzhsyzqy("　未分配利润") ;
		vo28.setHc1("28") ;
		vo28.setHc2("51") ;
		vo28.setHc1_id("ZCFZ-028");
		vo28.setHc2_id("ZCFZ-051");
		
		ZcFzBVO vo29 = new ZcFzBVO() ;
		//ptotal(?C23:?C24)+ptotal(?C27:?C35)	ptotal(?D23:?D24)+ptotal(?D27:?D35) vo16+vo17+vo20+vo28
//		2013年小企业会计制度下非流动资产下应该设置的公式=长期债券投资16+长期股权投资17+固定资产账面价值20+在建工程21+工程物资22+固定资产清理23+生物资产24+无形资产25+开发支出26+长期待摊费用27+其他非流动资产28
		DZFDouble qmye29_1 = VoUtils.getDZFDouble(vo16.getQmye1())
				.add(VoUtils.getDZFDouble(vo17.getQmye1()))
				.add(VoUtils.getDZFDouble(vo20.getQmye1()))
				.add(VoUtils.getDZFDouble(vo21.getQmye1()))
				.add(VoUtils.getDZFDouble(vo22.getQmye1()))
				.add(VoUtils.getDZFDouble(vo23.getQmye1()))
				.add(VoUtils.getDZFDouble(vo24.getQmye1()))
				.add(VoUtils.getDZFDouble(vo25.getQmye1()))
				.add(VoUtils.getDZFDouble(vo26.getQmye1()))
				.add(VoUtils.getDZFDouble(vo27.getQmye1()))
				.add(VoUtils.getDZFDouble(vo28.getQmye1()));
		
		DZFDouble ncye29_1 = VoUtils.getDZFDouble(vo16.getNcye1())
				.add(VoUtils.getDZFDouble(vo17.getNcye1()))
				.add(VoUtils.getDZFDouble(vo20.getNcye1()))
				.add(VoUtils.getDZFDouble(vo21.getNcye1()))
				.add(VoUtils.getDZFDouble(vo22.getNcye1()))
				.add(VoUtils.getDZFDouble(vo23.getNcye1()))
				.add(VoUtils.getDZFDouble(vo24.getNcye1()))
				.add(VoUtils.getDZFDouble(vo25.getNcye1()))
				.add(VoUtils.getDZFDouble(vo26.getNcye1()))
				.add(VoUtils.getDZFDouble(vo27.getNcye1()))
				.add(VoUtils.getDZFDouble(vo28.getNcye1())) ;
		
		vo29.setQmye1(qmye29_1) ;
		vo29.setNcye1(ncye29_1) ;
		//vo29.setQmye1(vo16.getQmye1().add(vo17.getQmye1()).add(vo20.getQmye1()).add(vo28.getQmye1()==null?DZFDouble.ZERO_DBL:vo28.getQmye1())) ;
		//vo29.setNcye1(vo16.getNcye1().add(vo17.getNcye1()).add(vo20.getNcye1()).add(vo28.getNcye1()==null?DZFDouble.ZERO_DBL:vo28.getNcye1())) ;
		//ptotal(?G32:?G35)	ptotal(?H32:?H35) vo25-vo28
		vo29.setQmye2(VoUtils.getDZFDouble(vo25.getQmye2()).add(VoUtils.getDZFDouble(vo26.getQmye2())).add(VoUtils.getDZFDouble(vo27.getQmye2())).add(VoUtils.getDZFDouble(vo28.getQmye2()))) ;
		vo29.setNcye2(VoUtils.getDZFDouble(vo25.getNcye2()).add(VoUtils.getDZFDouble(vo26.getNcye2())).add(VoUtils.getDZFDouble(vo27.getNcye2())).add(VoUtils.getDZFDouble(vo28.getNcye2()))) ;
		
		
		vo29.setZc("非流动资产合计") ;
		vo29.setFzhsyzqy("所有者权益（或股东权益)合计") ;
		vo29.setHc1("29") ;
		vo29.setHc2("52") ;
		vo29.setHc1_id("ZCFZ-029");
		vo29.setHc2_id("ZCFZ-052");
		
		ZcFzBVO vo30 = new ZcFzBVO() ;
		//?C21+?C36	?D21+?D36 vo15+vo29
		vo30.setQmye1(VoUtils.getDZFDouble(vo15.getQmye1()).add(vo29.getQmye1())) ;
		vo30.setNcye1(VoUtils.getDZFDouble(vo15.getNcye1()).add(vo29.getNcye1())) ;
		//?G24+?G36	?H24+?H36 vo17+vo29
		vo30.setQmye2(vo17.getQmye2().add(vo29.getQmye2())) ;
		vo30.setNcye2(vo17.getNcye2().add(vo29.getNcye2())) ;
		vo30.setZc("资产总计") ;
		vo30.setFzhsyzqy("负债和所有者权益(或股东权益)总计") ;
		vo30.setHc1("30") ;
		vo30.setHc2("53") ;
		vo30.setHc1_id("ZCFZ-030");
		vo30.setHc2_id("ZCFZ-053");
		
		return new ZcFzBVO[]{vo0,vo1,vo2,vo3,vo4,vo5,vo6,vo7,vo8,vo9,vo10,vo11,vo12,vo13,vo14,vo15,vo00,vo16,vo17,vo18,vo19,vo20
				,vo21,vo22,vo23,vo24,vo25,vo26,vo27,vo28,vo29,vo30};
	}

	private ZcFzBVO getZcFzBVO_YJSF_CFL(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mapc,
			String queryAccountRule, ZcFzBVO vo6) {
		List<String> list = getYSJFKmList(queryAccountRule);
		ZcFzBVO vo6temp = new ZcFzBVO();
		vo6temp = getZCFZBVO1(map, vo6temp, false, mapc,true, list.toArray(new String[0]));
		//2221下的所有科目
		List<String> orderlist = new ArrayList<String>();
		String filtercode = zxkjPlatformService.getNewRuleCode("222114",DZFConstant.ACCOUNTCODERULE, queryAccountRule);
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
		String[] kms1  = zxkjPlatformService.getNewCodes(kms,  DZFConstant.ACCOUNTCODERULE, queryAccountRule);
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
					ufd = ufd.add(qmjf).add(qmdf);

					vo.setQmye1(ufd);
					ufd = VoUtils.getDZFDouble(vo.getNcye1());
					DZFDouble qcjf = VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = VoUtils.getDZFDouble(fvo.getQcdf());
					if (fvo.getKmbm().equals("1407") || fvo.getKmbm().equals("2314")) {
						qcjf = VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1);
						qcdf = VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1);
					}
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
	/**
	 * 
	 * @param map
	 * @param vo
	 * @param is1
	 * @param mapc
	 * @param bthislevel 是否只是针对本级
	 * @param kms
	 * @return
	 */
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
