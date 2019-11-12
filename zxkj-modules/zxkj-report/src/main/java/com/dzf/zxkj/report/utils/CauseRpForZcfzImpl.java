package com.dzf.zxkj.report.utils;


import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 资产负债取数(事业单位)
 * @author zhangj
 *
 */
@SuppressWarnings("all")
public class CauseRpForZcfzImpl {
	
	public ZcFzBVO[] getCauseZCFZBVOs(Map<String, FseJyeVO> map, String[] hasyes, Map<String, YntCpaccountVO> mapc) throws BusinessException {
		ZcFzBVO votemp = new ZcFzBVO();
		
		ZcFzBVO vo0 = new ZcFzBVO() ;
		vo0.setZc("流动资产：") ;
		vo0.setFzhsyzqy("流动负债：") ;
	
		ZcFzBVO vo1 = new ZcFzBVO() ;
		vo1=getZCFZBVO(map,vo1,true,"1001","1002","1011");
		vo1=getZCFZBVO(map,vo1,false,"2001");
		vo1.setZc("　货币资金") ;
		vo1.setFzhsyzqy("　短期借款") ;
		vo1.setHc1("1") ;
		vo1.setHc2("31") ;
		
		
		ZcFzBVO vo2 = new ZcFzBVO() ;
		vo2=getZCFZBVO(map,vo2,true,"1101");
		vo2=getZCFZBVO(map,vo2,false,"2101");
		vo2.setZc("　短期投资") ;
		vo2.setFzhsyzqy("　应缴税费") ;
		vo2.setHc1("2") ;
		vo2.setHc2("32") ;
		
		
		ZcFzBVO vo3 = new ZcFzBVO() ;
		vo3=getZCFZBVO(map,vo3,true,"1201");
		vo3=getZCFZBVO(map,vo3,false, "2102");
		vo3.setZc("　财政应返还额度") ;
		vo3.setFzhsyzqy("　应缴国库款") ;
		vo3.setHc1("3") ;
		vo3.setHc2("33") ;
		
		ZcFzBVO vo4 = new ZcFzBVO() ;
		vo4=getZCFZBVO(map,vo4,true, "1211");
		vo4=getZCFZBVO(map,vo4,false, "2103");
		vo4.setZc("　应收票据") ;
		vo4.setFzhsyzqy("　应缴财政专户款") ;
		vo4.setHc1("4") ;
		vo4.setHc2("34") ;
	
		ZcFzBVO vo5 = new ZcFzBVO() ;
		if("Y".equals(hasyes[1])){
			vo5=getZCFZBVO1(map,vo5,true,mapc,"1212","2303");
		}else{
			vo5=getZCFZBVO(map,vo5,true, "1212");
		}
		vo5=getZCFZBVO(map,vo5,false, "2201");
		vo5.setZc("　应收账款") ;
		vo5.setFzhsyzqy("　应付职工薪酬") ;
		vo5.setHc1("5") ;
		vo5.setHc2("35") ;
		
	
		ZcFzBVO vo6 = new ZcFzBVO() ;
		if("Y".equals(hasyes[2])){
			// 预付账款=1213借方金额+2302借方金额
			vo6=getZCFZBVO1(map,vo6,true,mapc,"1213","2302");
		}else{
			vo6=getZCFZBVO(map,vo6,true, "1213");
		}
		vo6=getZCFZBVO(map,vo6,false, "2301");
		vo6.setZc("　预付账款") ;
		vo6.setFzhsyzqy("　应付票据") ;
		vo6.setHc1("6") ;
		vo6.setHc2("36") ;
		
		
		ZcFzBVO vo7 = new ZcFzBVO() ;
		
//		if("Y".equals(ishasye)){
//			vo7=getZCFZBVO1(map,vo7,true,mapc, "1215","2305");
//			//应付账款=2302贷方金额+1213贷方金额
//			vo7=getZCFZBVO1(map,vo7,false,mapc,"2302","1213");
//		}else{
//			vo7=getZCFZBVO(map,vo7,true, "1215");
//			vo7=getZCFZBVO(map,vo7,false,"2302");
//		}
		if("Y".equals(hasyes[3])){//其他应收和其他应付
			vo7=getZCFZBVO1(map,vo7,true,mapc, "1215","2305");
		}else{
			vo7=getZCFZBVO(map,vo7,true, "1215");
		}
		if("Y".equals(hasyes[2])){//应付和预付
			vo7=getZCFZBVO1(map,vo7,false,mapc,"2302","1213");
		}else{
			vo7=getZCFZBVO(map,vo7,false,"2302");
		}
		vo7.setZc("　其他应收款") ;
		vo7.setFzhsyzqy("　应付账款") ;
		vo7.setHc1("7") ;
		vo7.setHc2("37") ;
		

		ZcFzBVO vo8 = new ZcFzBVO() ;
		vo8=getZCFZBVO(map,vo8,true, "1301");
		if("Y".equals(hasyes[1])){
			// 预收账款=1212贷方金额+2303贷方金额
			vo8=getZCFZBVO1(map,vo8,false,mapc,"1212","2303");
		}else{
			vo8=getZCFZBVO(map,vo8,false, "2303");
		}
		vo8.setZc("　存货") ;
		vo8.setFzhsyzqy("　预收账款") ;
		vo8.setHc1("8") ;
		vo8.setHc2("38") ;
		
		ZcFzBVO vo9 = new ZcFzBVO() ;
//		vo9=getZCFZBVO(map,vo9,true,"1401");
		vo9.setZc("　其他流动资产") ;
		vo9.setFzhsyzqy("　其他应付款") ;
		if("Y".equals(hasyes[3])){
			vo9=getZCFZBVO1(map,vo9,false, mapc,"1215","2305");
		}else{
			vo9=getZCFZBVO(map,vo9,false, "2305");
		}
		vo9.setHc1("9") ;
		vo9.setHc2("39") ;
	
		
		ZcFzBVO vo10 = new ZcFzBVO() ;
		vo10.setNcye1(VoUtils.getDZFDouble(vo1.getNcye1()).add(VoUtils.getDZFDouble(vo2.getNcye1())).add(VoUtils.getDZFDouble(vo3.getNcye1())).add(VoUtils.getDZFDouble(vo4.getNcye1()))
				.add(VoUtils.getDZFDouble(vo5.getNcye1())).add(VoUtils.getDZFDouble(vo6.getNcye1())).add(VoUtils.getDZFDouble(vo7.getNcye1())).add(VoUtils.getDZFDouble(vo8.getNcye1()))
				.add(VoUtils.getDZFDouble(vo9.getNcye1()))
				);
		vo10.setQmye1(VoUtils.getDZFDouble(vo1.getQmye1()).add(VoUtils.getDZFDouble(vo2.getQmye1())).add(VoUtils.getDZFDouble(vo3.getQmye1())).add(VoUtils.getDZFDouble(vo4.getQmye1()))
				.add(VoUtils.getDZFDouble(vo5.getQmye1())).add(VoUtils.getDZFDouble(vo6.getQmye1())).add(VoUtils.getDZFDouble(vo7.getQmye1())).add(VoUtils.getDZFDouble(vo8.getQmye1()))
				.add(VoUtils.getDZFDouble(vo9.getQmye1()))
				);
//		vo10=getZCFZBVO(map,vo10,false,"2401","2402");
		vo10.setZc("流动资产合计") ;
		vo10.setFzhsyzqy("　其他流动负债") ;
		vo10.setHc1("10") ;
		vo10.setHc2("40") ;
		
		

		ZcFzBVO vo11 = new ZcFzBVO() ;
//		vo11=getZCFZBVO(map,vo11,true,"4001");
		vo11.setQmye2(VoUtils.getDZFDouble(vo1.getQmye2()).add(VoUtils.getDZFDouble(vo2.getQmye2())).add(VoUtils.getDZFDouble(vo3.getQmye2())).add(VoUtils.getDZFDouble(vo4.getQmye2())).
				add(VoUtils.getDZFDouble(vo5.getQmye2())).add(VoUtils.getDZFDouble(vo6.getQmye2())).add(VoUtils.getDZFDouble(vo7.getQmye2()))
				.add(VoUtils.getDZFDouble(vo8.getQmye2())).add(VoUtils.getDZFDouble(vo9.getQmye2())).add(VoUtils.getDZFDouble(vo10.getQmye2()))) ;
		vo11.setNcye2(VoUtils.getDZFDouble(vo1.getNcye2()).add(VoUtils.getDZFDouble(vo2.getNcye2())).add(VoUtils.getDZFDouble(vo3.getNcye2())).add(VoUtils.getDZFDouble(vo4.getNcye2())).
				add(VoUtils.getDZFDouble(vo5.getNcye2())).add(VoUtils.getDZFDouble(vo6.getNcye2())).add(VoUtils.getDZFDouble(vo7.getNcye2()))
				.add(VoUtils.getDZFDouble(vo8.getNcye2())).add(VoUtils.getDZFDouble(vo9.getNcye2())).add(VoUtils.getDZFDouble(vo10.getNcye2()))) ;
		vo11.setZc("非流动资产：") ;
		vo11.setFzhsyzqy("流动负债合计") ;
		vo11.setHc1("11") ;
		vo11.setHc2("41") ;
		
		
		ZcFzBVO vo12 = new ZcFzBVO() ;
		vo12=getZCFZBVO(map,vo12,true, "1401");
		vo12.setZc("　长期投资") ;
		vo12.setFzhsyzqy("非流动负债：") ;
		vo12.setHc1("12") ;
		vo12.setHc2(null) ;
		
		
		ZcFzBVO vo13 = new ZcFzBVO() ;
		clearTempVO(votemp);
		votemp = getZCFZBVO(map, votemp, true, "1502");
		vo13=getZCFZBVO(map,vo13,true, "1501");
		vo13.setNcye1(VoUtils.getDZFDouble(vo13.getNcye1()).sub(votemp.getNcye1()));
		vo13.setQmye1(VoUtils.getDZFDouble(vo13.getQmye1()).sub(votemp.getQmye1()));
		vo13=getZCFZBVO(map,vo13,false ,"2401");
		vo13.setZcconkms("");;
		vo13.setZc("　固定资产") ;
		vo13.setFzhsyzqy("　长期借款") ;
		vo13.setHc1("13") ;
		vo13.setHc2("42") ;
		
		ZcFzBVO vo14 = new ZcFzBVO() ;
		vo14=getZCFZBVO(map,vo14,true ,"1501");
		vo14=getZCFZBVO(map,vo14,false ,"2402");
		vo14.setZc("　　固定资产原价") ;
		vo14.setFzhsyzqy("　长期应付款") ;
		vo14.setHc1("14") ;
		vo14.setHc2("43") ;
		
		
		ZcFzBVO vo15 = new ZcFzBVO() ;
		vo15=getZCFZBVO(map,vo15,true ,"1502");
		vo15.setNcye2(VoUtils.getDZFDouble(vo13.getNcye2()).add(VoUtils.getDZFDouble(vo14.getNcye2())));
		vo15.setQmye2(VoUtils.getDZFDouble(vo13.getQmye2()).add(VoUtils.getDZFDouble(vo14.getQmye2())));
		vo15.setZc("　　减：累计折旧") ;
		vo15.setFzhsyzqy("非流动负债合计") ;
		vo15.setHc1("15") ;
		vo15.setHc2("44") ;
		
		ZcFzBVO vo16 = new ZcFzBVO() ;
		vo16=getZCFZBVO(map,vo16,true ,"1511");
		vo16.setQmye2(VoUtils.getDZFDouble(vo11.getQmye2()).add(VoUtils.getDZFDouble(vo15.getQmye2()))) ;
		vo16.setNcye2(VoUtils.getDZFDouble(vo11.getNcye2()).add(VoUtils.getDZFDouble(vo15.getNcye2()))) ;
		vo16.setZc("　在建工程") ;
		vo16.setFzhsyzqy("负债合计") ;
		vo16.setHc1("16") ;
		vo16.setHc2("46") ;
		
		ZcFzBVO vo17 = new ZcFzBVO() ;
		clearTempVO(votemp);
		votemp = getZCFZBVO(map, votemp, true, "1602");
		vo17=getZCFZBVO(map,vo17,true ,"1601");
		vo17.setNcye1(VoUtils.getDZFDouble(vo17.getNcye1()).sub(VoUtils.getDZFDouble(votemp.getNcye1())));
		vo17.setQmye1(VoUtils.getDZFDouble(vo17.getQmye1()).sub(VoUtils.getDZFDouble(votemp.getQmye1())));
		vo17.setZc("　无形资产") ;
		vo17.setZcconkms("");
		vo17.setFzhsyzqy("净资产：") ;
		vo17.setHc1("17") ;
		vo17.setHc2("47") ;
		
		ZcFzBVO vo18 = new ZcFzBVO() ;
		vo18=getZCFZBVO(map,vo18,true ,"1601");
		vo18=getZCFZBVO(map,vo18,false ,"3001");
		vo18.setZc("　　无形资产原价") ;
		vo18.setFzhsyzqy("　事业基金") ;
		vo18.setHc1("18") ;
		vo18.setHc2("48") ;
		
		ZcFzBVO vo19 = new ZcFzBVO() ;
		vo19=getZCFZBVO(map,vo19,true ,"1602");
		vo19=getZCFZBVO(map,vo19,false ,"3101");
		vo19.setZc("　　减：累计摊销") ;
		vo19.setFzhsyzqy("　非流动资产基金") ;
		vo19.setHc1("19") ;
		vo19.setHc2("49") ;

		ZcFzBVO vo20 = new ZcFzBVO() ;
		vo20=getZCFZBVO(map,vo20,true ,"1701");
		vo20=getZCFZBVO(map,vo20,false ,"3201");
		vo20.setZc("　待处理资产损溢") ;
		vo20.setFzhsyzqy("　专用基金") ;
		vo20.setHc1("20") ;
		vo20.setHc2("50") ;
		
		ZcFzBVO vo21 = new ZcFzBVO() ;
		vo21.setNcye1(VoUtils.getDZFDouble(vo12.getNcye1()).add(VoUtils.getDZFDouble(vo13.getNcye1()))
				.add(VoUtils.getDZFDouble(vo16.getNcye1())).add(VoUtils.getDZFDouble(vo17.getNcye1()))
				.add(VoUtils.getDZFDouble(vo20.getNcye1()))
				);
		vo21.setQmye1(VoUtils.getDZFDouble(vo12.getQmye1()).add(VoUtils.getDZFDouble(vo13.getQmye1()))
				.add(VoUtils.getDZFDouble(vo16.getQmye1())).add(VoUtils.getDZFDouble(vo17.getQmye1()))
				.add(VoUtils.getDZFDouble(vo20.getQmye1()))
				);
		vo21=getZCFZBVO(map,vo21,false ,"3301");
		vo21.setZc("非流动资产合计") ;
		vo21.setFzhsyzqy("　财政补助结转") ;
		vo21.setHc1("21") ;
		vo21.setHc2("51") ;
		
		ZcFzBVO vo22 = new ZcFzBVO() ;
		vo22=getZCFZBVO(map,vo22,false ,"3302");
		vo22.setZc(null) ;
		vo22.setFzhsyzqy("　财政补助结余") ;
		vo22.setHc1(null) ;
		vo22.setHc2("52") ;
		
		ZcFzBVO vo23 = new ZcFzBVO() ;
		vo23=getZCFZBVO(map,vo23,false ,"3401");
		vo23.setZc(null) ;
		vo23.setFzhsyzqy("　非财政补助结转") ;
		vo23.setHc1(null) ;
		vo23.setHc2("53") ;
		
		ZcFzBVO vo24 = new ZcFzBVO() ;
		vo24=getZCFZBVO(map,vo24,false ,"3402","3403");
		vo24.setFzhsyzqy("　非财政补助结余") ;
		vo24.setHc1(null) ;
		vo24.setHc2("54") ;
		
		ZcFzBVO vo25 = new ZcFzBVO() ;
		vo25=getZCFZBVO(map,vo25,false, "3402");
		vo25.setZc(null) ;
		vo25.setFzhsyzqy("　　1.事业结余	（贷）	") ;
		vo25.setHc1(null) ;
		vo25.setHc2("55") ;
				
		ZcFzBVO vo26 = new ZcFzBVO() ;
		vo26=getZCFZBVO(map,vo26,false, "3403");
		vo26.setFzhsyzqy("　　2.经营结余") ;
		vo26.setHc1(null) ;
		vo26.setHc2("56") ;
		
		
//		ZcFzBVO vo27 = new ZcFzBVO() ;
//		vo27=getZCFZBVO(map,vo27,false,"3101");
//		vo27.setZc(null) ;
//		vo27.setFzhsyzqy("净资产合计") ;
//		vo27.setHc1(null) ;
//		vo27.setHc2("57") ;
		
		ZcFzBVO vo28 = new ZcFzBVO() ;
		vo28.setNcye2(VoUtils.getDZFDouble(vo18.getNcye2()).add(VoUtils.getDZFDouble(vo19.getNcye2()))
				.add(VoUtils.getDZFDouble(vo20.getNcye2())).add(VoUtils.getDZFDouble(vo21.getNcye2()))
				.add(VoUtils.getDZFDouble(vo22.getNcye2())).add(VoUtils.getDZFDouble(vo23.getNcye2())
				.add(VoUtils.getDZFDouble(vo24.getNcye2()))
				));
		vo28.setQmye2(VoUtils.getDZFDouble(vo18.getQmye2()).add(VoUtils.getDZFDouble(vo19.getQmye2()))
				.add(VoUtils.getDZFDouble(vo20.getQmye2())).add(VoUtils.getDZFDouble(vo21.getQmye2()))
				.add(VoUtils.getDZFDouble(vo22.getQmye2())).add(VoUtils.getDZFDouble(vo23.getQmye2())
				.add(VoUtils.getDZFDouble(vo24.getQmye2()))
				));
		vo28.setZc(null) ;
		vo28.setFzhsyzqy("净资产合计") ;
		vo28.setHc1(null ) ;
		vo28.setHc2("58") ;

		ZcFzBVO vo29 = new ZcFzBVO() ;
		DZFDouble qmye29_1 = VoUtils.getDZFDouble(vo21.getQmye1())
				.add(VoUtils.getDZFDouble(vo10.getQmye1()));
		
		DZFDouble ncye29_1 =  VoUtils.getDZFDouble(vo21.getNcye1())
				.add(VoUtils.getDZFDouble(vo10.getNcye1()));
		vo29.setQmye1(qmye29_1) ;
		vo29.setNcye1(ncye29_1) ;
		vo29.setQmye2(VoUtils.getDZFDouble(vo16.getQmye2()).add(VoUtils.getDZFDouble(vo28.getQmye2()))) ;
		vo29.setNcye2(VoUtils.getDZFDouble(vo16.getNcye2()).add(VoUtils.getDZFDouble(vo28.getNcye2()))) ;
		vo29.setZc("资产总计") ;
		vo29.setFzhsyzqy("负债和净资产总计") ;
		vo29.setHc1("29") ;
		vo29.setHc2("59") ;
		
		
		return new ZcFzBVO[]{vo0,vo1,vo2,vo3,vo4,vo5,vo6,vo7,vo8,vo9,vo10,vo11,vo12,vo13,vo14,vo15,vo16,vo17,vo18,vo19,vo20
				,vo21,vo22,vo23,vo24,vo25,vo26,vo28,vo29};
	}
	
	public void clearTempVO(ZcFzBVO votemp){
		votemp.setNcye1(DZFDouble.ZERO_DBL);
		votemp.setQmye1(DZFDouble.ZERO_DBL);
		votemp.setNcye2(DZFDouble.ZERO_DBL);
		votemp.setQmye2(DZFDouble.ZERO_DBL);
	}
	
	
	private ZcFzBVO getZCFZBVO(Map<String,FseJyeVO> map, ZcFzBVO vo,boolean is1,String...kms ){
		
		List<FseJyeVO> ls=null;
		DZFDouble ufd=null;
		int len=kms==null?0:kms.length;
		
		StringBuffer appkms = new StringBuffer();
		if(is1){
			appkms= new StringBuffer(StringUtil.isEmpty(vo.getZcconkms()) ? "" : vo.getZcconkms()+",");
		}else{
			appkms= new StringBuffer(StringUtil.isEmpty(vo.getFzconkms()) ? "" : vo.getFzconkms()+",");
		}
		for (int i = 0; i < len; i++) {
			ls=getData(map, kms[i]);
			appkms.append(kms[i]+",");
			if(ls==null)continue;
			for (FseJyeVO fvo : ls) {
				if(is1){
					ufd=VoUtils.getDZFDouble(vo.getQmye1());
					DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmdf());
					ufd=ufd.add(qmjf).add(qmdf);
					
					vo.setQmye1(ufd);
					ufd=VoUtils.getDZFDouble(vo.getNcye1());
					DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcdf());
					ufd=ufd.add(qcjf).add(qcdf);

					vo.setNcye1(ufd) ;
					
					
					//本月期初
					ufd=VoUtils.getDZFDouble(vo.getQcye1());
					DZFDouble byqcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getLastmqcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getLastmqcjf());
					DZFDouble bqqcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getLastmqcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getLastmqcdf());
					ufd=ufd.add(byqcjf).add(bqqcdf);
					vo.setQcye1(ufd);
				}else{
					ufd=VoUtils.getDZFDouble(vo.getQmye2());
					ufd=ufd.add(VoUtils.getDZFDouble(fvo.getQmjf())).add(VoUtils.getDZFDouble(fvo.getQmdf()));//只有一方有值
					vo.setQmye2(ufd);
					ufd=VoUtils.getDZFDouble(vo.getNcye2());
					ufd=ufd.add(VoUtils.getDZFDouble(fvo.getQcjf())).add(VoUtils.getDZFDouble(fvo.getQcdf()));//只有一方有值
					vo.setNcye2(ufd) ;
					
					//本月期初
					ufd=VoUtils.getDZFDouble(vo.getQcye2());
					ufd=ufd.add(VoUtils.getDZFDouble(fvo.getLastmqcjf())).add(VoUtils.getDZFDouble(fvo.getLastmqcdf()));//只有一方有值
					vo.setQcye2(ufd) ;
				}
			}
			
			if(is1){
				vo.setZcconkms(appkms.toString().substring(0,appkms.length()-1));
			}else{
				vo.setFzconkms(appkms.toString().substring(0,appkms.length()-1));
			}
		}
		return vo;
	}
	
	private ZcFzBVO getZCFZBVO1(Map<String,FseJyeVO> map, ZcFzBVO vo,boolean is1,Map<String,YntCpaccountVO> mapc, String...kms ){
		List<FseJyeVO> ls=null;
		DZFDouble ufd=null;
		int len=kms==null?0:kms.length;
		
		StringBuffer appkms = new StringBuffer();
		if(is1){
			appkms= new StringBuffer(vo.getZcconkms() == null ? "" : vo.getZcconkms());
		}else{
			appkms= new StringBuffer(vo.getFzconkms() == null ? "" : vo.getFzconkms());
		}
		for (int i = 0; i < len; i++) {
			ls=getData1(map, kms[i],mapc);
			appkms.append(kms[i]+",");
			if(ls==null)continue;
			for (FseJyeVO fvo : ls) {
				if(is1){//资产
					ufd=VoUtils.getDZFDouble(vo.getQmye1());
					DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmdf());
					if("借".equals(fvo.getFx()) && qmjf.doubleValue() > 0){
						ufd=ufd.add(qmjf);
					}else if(qmdf.doubleValue() < 0){
						ufd=ufd.sub(qmdf);
					}
					vo.setQmye1(ufd);
					//
					ufd=VoUtils.getDZFDouble(vo.getNcye1());
					DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcdf());
					if("借".equals(fvo.getFx()) && qcjf.doubleValue() > 0){
						ufd=ufd.add(qcjf);
					}else if(qcdf.doubleValue() < 0){
						ufd=ufd.sub(qcdf);
					}
					vo.setNcye1(ufd);
					
					//往来分析不计算本月期初
				}else{//负债
					ufd=VoUtils.getDZFDouble(vo.getQmye2());
					DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQmdf());
					if("贷".equals(fvo.getFx()) && qmdf.doubleValue()>0){
						ufd=ufd.add(qmdf);
					}else if(qmjf.doubleValue() <0){
						ufd=ufd.sub(qmjf);
					}
					vo.setQmye2(ufd);
					//
					ufd=VoUtils.getDZFDouble(vo.getNcye2());
					DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getQcdf());
					if("贷".equals(fvo.getFx()) && qcdf.doubleValue() >0){
						ufd=ufd.add(qcdf);
					}else if(qcjf.doubleValue() < 0){
						ufd=ufd.sub(qcjf);
					}
					vo.setNcye2(ufd) ;
					
					//往来分析不计算本月期初
				}
			}
			
			if(is1){
				vo.setZcconkms(appkms.toString().substring(0,appkms.length()-1));
			}else{
				vo.setFzconkms(appkms.toString().substring(0,appkms.length()-1));
			}
		}
		return vo;
	}
	private List<FseJyeVO> getData1(Map<String,FseJyeVO> map,String km,Map<String,YntCpaccountVO> mapc){
		List<FseJyeVO> list=new ArrayList<FseJyeVO>();
		for (FseJyeVO fsejyevo : map.values()) {
//			String kmbm = fsejyevo.getKmbm();
//			if(kmbm.startsWith(km)){
//				YntCpaccountVO xs = mapc.get(kmbm);
//				if(xs.getIsleaf() != null && xs.getIsleaf().booleanValue()){
//					list.add(fsejyevo);
//				}
//			}
			
			String kmbm = null;
			if(fsejyevo.getKmbm().indexOf("_")>0){
				kmbm = fsejyevo.getKmbm().split("_")[0];
			}else{
				kmbm = fsejyevo.getKmbm();
			}
			if(kmbm.startsWith(km)){
				YntCpaccountVO xs = mapc.get(kmbm);
				if(xs.getIsleaf() != null && xs.getIsleaf().booleanValue()){
					if(xs.getIsfzhs().indexOf("1")>=0 && fsejyevo.getKmbm().indexOf("_")>=0){//包含辅助项目
						list.add(fsejyevo);
					}else if(xs.getIsfzhs().indexOf("1")<0){//如果不包含辅助项目处理
						list.add(fsejyevo);
					}
				}
			}
		}
		return list;
	}
	
	private List<FseJyeVO> getData(Map<String,FseJyeVO> map,String km){
		List<FseJyeVO> list=new ArrayList<FseJyeVO>();
		for (FseJyeVO fsejyevo : map.values()) {
			if(fsejyevo.getKmbm().equals(km)){//已经合计过了，不要用startwidh
				list.add(fsejyevo);
				break;
			}
		}
		return list;
	}


}
