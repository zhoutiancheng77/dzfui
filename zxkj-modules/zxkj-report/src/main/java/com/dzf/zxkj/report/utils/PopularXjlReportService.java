package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.enums.KmschemaCash;
import com.dzf.zxkj.report.query.cwzb.FsYeQueryVO;
import com.dzf.zxkj.report.service.cwzb.IFsYeService;
import com.dzf.zxkj.report.vo.cwbb.XjllbVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 现金流量表(民间)
 * @author zhangj
 *
 */
@SuppressWarnings("all")
public class PopularXjlReportService {

	private IFsYeService fsYeService;

	public XjllbVO[] getXjlBvos(Map<Float, XjllbVO> map, String period ,
								CorpVO cpvo , String pk_trade_accountschema, YntCpaccountVO[] cpavos) throws Exception {

		FsYeQueryVO queryParamvo =  new FsYeQueryVO();
		queryParamvo.setQjq(period);
		queryParamvo.setQjz(period);
		queryParamvo.setIshasjz(new DZFBoolean("N"));
		queryParamvo.setIshassh(DZFBoolean.TRUE);
		queryParamvo.setPk_corp(cpvo.getPk_corp());
		fsYeService = SpringUtils.getBean(IFsYeService.class);
		FseJyeVO[] fvos =  fsYeService.getFsJyeVOs(queryParamvo, 1);
		
		Map<String,FseJyeVO> mapfs=new HashMap<String, FseJyeVO>();
		if(fvos != null && fvos.length > 0){
			int len=fvos==null?0:fvos.length;
			for (int i = 0; i < len; i++) {
				mapfs.put(fvos[i].getKmbm(), fvos[i]);
			}
		}
		
		getXjllbXmVos(map);
		
		XjllbVO voWu = new XjllbVO() ;
		voWu.setXm("五、现金及现金等价物净增加额") ;	
		voWu.setHc("61") ;
		voWu.setRowno(61);
		DZFDouble bqje = DZFDouble.ZERO_DBL;
		DZFDouble sqje = DZFDouble.ZERO_DBL;
		Set<String> cashset =  KmschemaCash.getCashSubjectCode1(cpavos, cpvo.getCorptype(), DZFBoolean.TRUE);
		for(String str:cashset){
			if(mapfs.containsKey(str)){
				bqje = bqje.add(SafeCompute.sub(mapfs.get(str).getEndfsjf(), mapfs.get(str).getEndfsdf()) );
				sqje = sqje.add(SafeCompute.sub(mapfs.get(str).getJftotal(), mapfs.get(str).getDftotal()));
			}
		}
		voWu.setBqje(bqje) ;
		voWu.setSqje(sqje) ;
//		DZFDouble bqnum01= VoUtils.getDZFDouble(mapfs.get("1001").getEndfsjf()).sub(VoUtils.getDZFDouble(mapfs.get("1001").getEndfsdf()));
//		DZFDouble bqnum02= VoUtils.getDZFDouble(mapfs.get("1002").getEndfsjf()).sub(VoUtils.getDZFDouble(mapfs.get("1002").getEndfsdf()));
//		DZFDouble bqnum03= VoUtils.getDZFDouble(mapfs.get("1009").getEndfsjf()).sub(VoUtils.getDZFDouble(mapfs.get("1009").getEndfsdf()));
//		voWu.setBqje(bqnum01.add(bqnum02).add(bqnum03)) ;
		
//		DZFDouble bnnum01= VoUtils.getDZFDouble(mapfs.get("1001").getJftotal()).sub(VoUtils.getDZFDouble(mapfs.get("1001").getDftotal()));
//		DZFDouble bnnum02= VoUtils.getDZFDouble(mapfs.get("1002").getJftotal()).sub(VoUtils.getDZFDouble(mapfs.get("1002").getDftotal()));
//		DZFDouble bnnum03= VoUtils.getDZFDouble(mapfs.get("1009").getJftotal()).sub(VoUtils.getDZFDouble(mapfs.get("1009").getDftotal()));
//		voWu.setSqje(bnnum01.add(bnnum02).add(bnnum03)) ;
		map.put(voWu.getRowno(), voWu);
		
		XjllbVO[] xvos=new XjllbVO[map.size()];
		Float[] fs=	(Float[])map.keySet().toArray(new Float[0]);
		Arrays.sort(fs);
		int len=map.size();
		for (int i = 0; i < len; i++) {
			xvos[i]=map.get(fs[i]);
		}
		map=null;
		return xvos;
	}
	
	private String getFormula(float s, float e) {
		StringBuffer res = new StringBuffer();
		for (float i = s; i <= e; i += 1) {
			res.append("hc(" +  new Float(i).intValue() + ")+");
		}
		return res.toString().substring(0, res.length() - 1);
	}

	public void getXjllbXmVos(Map<Float, XjllbVO> map) {
		DZFDouble[] ds=null;
		XjllbVO vo0 = new XjllbVO() ;
		vo0.setXm("一、业务活动产生的现金流量：") ;
		vo0.setRowno(0.5f);
//		vo0.setHc("0.5");
		map.put(vo0.getRowno(), vo0);//vec.add(vo0) ;
		
		//查询行号为1-6的现金流量项目
		ds= getSumXJLLB(map,1,8);
		DZFDouble bqje1to8 = ds[0];
		DZFDouble bnje1to8 = ds[1];
		XjllbVO vo4 = new XjllbVO() ;
		vo4.setXm("　　　　现金流入小计") ;
		vo4.setHc("13") ;
		vo4.setRowno(13);
		vo4.setBqje(bqje1to8) ;
		vo4.setSqje(bnje1to8) ;
		vo4.setFormula(getFormula(1, 8));
		map.put(vo4.getRowno(), vo4);//vec.add(vo4) ;
		
		//查询行号为8-11的现金流量项目
		ds= getSumXJLLB(map,14,19);
		DZFDouble bqje8to11 = ds[0];
		DZFDouble bnje8to11 = ds[1];
		XjllbVO vo9 = new XjllbVO() ;
		vo9.setXm("　　　　现金流出小计") ;
		vo9.setHc("23") ;
		vo9.setRowno(23);
		vo9.setBqje(bqje8to11) ;
		vo9.setSqje(bnje8to11) ;
		vo9.setFormula(getFormula(14, 19));
		map.put(vo9.getRowno(), vo9);//vec.add(vo9) ;
		
		XjllbVO vo10 = new XjllbVO() ;
		vo10.setXm("　　　　业务活动产生的现金流量净额") ;
		vo10.setHc("24") ;
		vo10.setRowno(24);
		vo10.setBqje(vo4.getBqje().sub(vo9.getBqje())) ;
		vo10.setSqje(vo4.getSqje().sub(vo9.getSqje())) ;
		vo10.setFormula("hc("+vo4.getHc()+") - hc("+vo9.getHc()+")");
		map.put(vo10.getRowno(), vo10);//vec.add(vo10) ;
		
		XjllbVO voEr = new XjllbVO() ;
		voEr.setXm("二、投资活动产生的现金流量：") ;	
		voEr.setRowno(24.5f);
//		voEr.setHc("24.5");
		map.put(voEr.getRowno(), voEr);//vec.add(voEr) ;
		
		
		ds= getSumXJLLB(map,25,30);
		DZFDouble bqje14to17 = ds[0];
		DZFDouble bnje14to17 = ds[1];
		XjllbVO vo16 = new XjllbVO() ;
		vo16.setXm("　　　　现金流入小计") ;
		vo16.setHc("34") ;
		vo16.setRowno(34);
		vo16.setBqje(bqje14to17) ;
		vo16.setSqje(bnje14to17) ;
		vo16.setFormula(getFormula(25, 30));
		map.put(vo16.getRowno(), vo16);//vec.add(vo16) ;
		
		ds= getSumXJLLB(map,35,39);
		DZFDouble bqje19to21 = ds[0];
		DZFDouble bnje19to21 = ds[1];
		XjllbVO vo21 = new XjllbVO() ;
		vo21.setXm("　　　　现金流出小计") ;
		vo21.setHc("43") ;
		vo21.setRowno(43);
		vo21.setBqje(bqje19to21) ;
		vo21.setSqje(bnje19to21) ;
		vo21.setFormula(getFormula(35, 39));
		map.put(vo21.getRowno(), vo21);//vec.add(vo21) ;
		
		XjllbVO vo22 = new XjllbVO() ;
		vo22.setXm("　　　　投资活动产生的现金流量净额") ;
		vo22.setHc("44") ;
		vo22.setRowno(44);
		vo22.setBqje(vo16.getBqje().sub(vo21.getBqje())) ;
		vo22.setSqje(vo16.getSqje().sub(vo21.getSqje())) ;
		vo22.setFormula("hc("+vo16.getHc()+") - hc("+vo21.getHc()+")");
		map.put(vo22.getRowno(), vo22);//vec.add(vo22) ;
		
		
		
		XjllbVO voSan = new XjllbVO() ;
		voSan.setXm("三、筹资活动产生的现金流量：") ;
		voSan.setRowno(44.5f);
//		voSan.setHc("44.5");
		map.put(voSan.getRowno(), voSan);//vec.add(voSan) ;
		
		
		ds= getSumXJLLB(map,45,48);
		DZFDouble bqje24to25 = ds[0];
		DZFDouble bnje24to25 = ds[1];
		XjllbVO vo26 = new XjllbVO() ;
		vo26.setXm("　　　　现金流入小计") ;
		vo26.setHc("50") ;
		vo26.setRowno(50);
		vo26.setBqje(bqje24to25) ;
		vo26.setSqje(bnje24to25) ;
		vo26.setFormula(getFormula(45, 48));
		map.put(vo26.getRowno(), vo26);//vec.add(vo26) ;
		
		
		ds= getSumXJLLB(map,51,55);
		DZFDouble bqje27to29 = ds[0];
		DZFDouble bnje27to29 = ds[1];
		XjllbVO vo30 = new XjllbVO() ;
		vo30.setXm("　　　　现金流出小计") ;
		vo30.setHc("58") ;
		vo30.setRowno(58);
		vo30.setBqje(bqje27to29) ;
		vo30.setSqje(bnje27to29) ;
		vo30.setFormula(getFormula(51, 55));
		map.put(vo30.getRowno(), vo30);//vec.add(vo30) ;
		
		XjllbVO vo31 = new XjllbVO() ;
		vo31.setXm("　　　　筹资活动产生的现金流量净额") ;
		vo31.setHc("59") ;
		vo31.setRowno(59);
		vo31.setBqje(vo26.getBqje().sub(vo30.getBqje())) ;
		vo31.setSqje(vo26.getSqje().sub(vo30.getSqje())) ;
		vo31.setFormula("hc("+vo26.getHc()+") - hc("+vo30.getHc()+")");
		map.put(vo31.getRowno(), vo31);//vec.add(vo31) ;
		
		XjllbVO vohl = new XjllbVO() ;
		vohl.setXm("四、汇率变动对现金的影响额") ;
		vohl.setHc("60") ;
		vohl.setRowno(60);
		map.put(vohl.getRowno(), vohl);
	}
	
	private DZFDouble[] getSumXJLLB(Map<Float, XjllbVO> map,float s,float e){
		DZFDouble bqje = DZFDouble.ZERO_DBL;
		DZFDouble bnje = DZFDouble.ZERO_DBL;
		XjllbVO xvo=null;
		if (map != null && map.size() > 0) {
			for (float i = s; i <=e; i+=1) {
				xvo=map.get(i);
				if(xvo==null){
					continue;
				}
				bqje=bqje.add(DZFDouble.getUFDouble(xvo.getBqje()));
				bnje=bnje.add(DZFDouble.getUFDouble(xvo.getSqje()));
			}
		}
		return new DZFDouble[]{bqje,bnje};
	}
}
