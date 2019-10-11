package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.report.query.cwzb.FsYeQueryVO;
import com.dzf.zxkj.report.service.cwzb.IFsYeService;
import com.dzf.zxkj.report.vo.cwbb.XjllbVO;
import com.dzf.zxkj.report.vo.cwzb.FseJyeVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 现金流量表(民间)
 * @author zhangj
 *
 */
@SuppressWarnings("all")
@Slf4j
public class CompanyXjlReportService {
	
	private IFsYeService fsYeService;

	public XjllbVO[] getXjlBvos(Map<Float, XjllbVO> map, String period , String pk_corp , String pk_trade_accountschema) throws Exception {

		FsYeQueryVO queryParamvo =  new FsYeQueryVO();
		queryParamvo.setQjq(period);
		queryParamvo.setQjz(period);
		queryParamvo.setIshasjz(new DZFBoolean("N"));
		queryParamvo.setIshassh(DZFBoolean.TRUE);
		queryParamvo.setPk_corp(pk_corp);
		fsYeService = SpringUtils.getBean(IFsYeService.class);
		FseJyeVO[] fvos =  fsYeService.getFsJyeVOs(queryParamvo, 1);
		
		Map<String,FseJyeVO> mapfs=new HashMap<String, FseJyeVO>();
		if(fvos != null && fvos.length > 0){
			int len=fvos==null?0:fvos.length;
			for (int i = 0; i < len; i++) {
				mapfs.put(fvos[i].getKmbm(), fvos[i]);
			}
		}
		
		getXjllXmVos(map);
		
		XjllbVO vo56 = new XjllbVO() ;
		vo56.setXm("五、现金及现金等价物净增加额") ;
		vo56.setRowno(56f);
		vo56.setHc("56") ;
		map.put(vo56.getRowno(), vo56);//vec.add(voSan) ;
		
		XjllbVO[] xvos=new XjllbVO[map.size()];
		Float[] fs=	(Float[])map.keySet().toArray(new Float[0]);
		Arrays.sort(fs);
		int len=map.size();
		for (int i = 0; i < len; i++) {
			xvos[i]=map.get(fs[i]);
			if(!StringUtil.isEmpty(xvos[i].getHc())){
				try {
					xvos[i].setHc_id("XJLL-"+ String.format("%03d", Integer.parseInt(xvos[i].getHc())));
				} catch (NumberFormatException e) {
					log.error("现金流量表:"+e.getMessage());
				}
			}
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

	public void getXjllXmVos(Map<Float, XjllbVO> map) {
		DZFDouble[] ds=null;
		XjllbVO vo0 = new XjllbVO() ;
		vo0.setXm("一、经营活动产生的现金流量：") ;
		vo0.setRowno(0.5f);
//		vo0.setHc("0.5");
		map.put(vo0.getRowno(), vo0);//vec.add(vo0) ;
		
		//查询行号为1-8的现金流量项目
		ds= getSumXJLLB(map,1,8);
		DZFDouble bqje1to8 = ds[0];
		DZFDouble bnje1to8 = ds[1];
		XjllbVO vo9 = new XjllbVO() ;
		vo9.setXm("　　现金流入小计") ;
		vo9.setHc("9") ;
		vo9.setRowno(9);
		vo9.setBqje(bqje1to8) ;
		vo9.setSqje(bnje1to8) ;
		vo9.setFormula(getFormula(1, 8));
		map.put(vo9.getRowno(), vo9);//vec.add(vo4) ;
		
		//查询行号为10-18的现金流量项目
		ds= getSumXJLLB(map,10,18);
		DZFDouble bqje10to18 = ds[0];
		DZFDouble bnje10to18 = ds[1];
		XjllbVO vo20 = new XjllbVO() ;
		vo20.setXm("　　现金流出小计") ;
		vo20.setHc("20") ;
		vo20.setRowno(20);
		vo20.setBqje(bqje10to18) ;
		vo20.setSqje(bnje10to18) ;
		vo20.setFormula(getFormula(10, 18));
		map.put(vo20.getRowno(), vo20);//vec.add(vo9) ;
		
		XjllbVO vo21 = new XjllbVO() ;
		vo21.setXm("　　经营活动产生的现金流量净额") ;
		vo21.setHc("21") ;
		vo21.setRowno(21);
		vo21.setBqje(vo9.getBqje().sub(vo20.getBqje())) ;
		vo21.setSqje(vo9.getSqje().sub(vo20.getSqje())) ;
		vo21.setFormula("hc("+vo9.getHc()+") - hc("+vo20.getHc()+")");
		map.put(vo21.getRowno(), vo21);//vec.add(vo10) ;
		
		XjllbVO voEr = new XjllbVO() ;
		voEr.setXm("二、投资活动产生的现金流量：") ;	
		voEr.setRowno(21.5f);
//		voEr.setHc("21.5");
		map.put(voEr.getRowno(), voEr);//vec.add(voEr) ;
		
		
		ds= getSumXJLLB(map,22,28);
		DZFDouble bqje22to28 = ds[0];
		DZFDouble bnje22to28 = ds[1];
		XjllbVO vo29 = new XjllbVO() ;
		vo29.setXm("　　现金流入小计") ;
		vo29.setHc("29") ;
		vo29.setRowno(29);
		vo29.setBqje(bqje22to28) ;
		vo29.setSqje(bnje22to28) ;
		vo29.setFormula(getFormula(22, 28));
		map.put(vo29.getRowno(), vo29);//vec.add(vo16) ;
		
		
		ds= getSumXJLLB(map,30,35);
		DZFDouble bqje30to35 = ds[0];
		DZFDouble bnje30to35 = ds[1];
		XjllbVO vo36 = new XjllbVO() ;
		vo36.setXm("　　现金流出小计") ;
		vo36.setHc("36") ;
		vo36.setRowno(36);
		vo36.setBqje(bqje30to35) ;
		vo36.setSqje(bnje30to35) ;
		vo36.setFormula(getFormula(30, 35));
		map.put(vo36.getRowno(), vo36);//vec.add(vo21) ;
		
		XjllbVO vo37 = new XjllbVO() ;
		vo37.setXm("　　投资活动产生的现金流量净额") ;
		vo37.setHc("37") ;
		vo37.setRowno(37);
		vo37.setBqje(vo29.getBqje().sub(vo36.getBqje())) ;
		vo37.setSqje(vo29.getSqje().sub(vo36.getSqje())) ;
		vo37.setFormula("hc("+vo29.getHc()+") - hc("+vo36.getHc()+")");
		map.put(vo37.getRowno(), vo37);//vec.add(vo22) ;
		
		
		
		XjllbVO voSan = new XjllbVO() ;
		voSan.setXm("三、筹资活动产生的现金流量：") ;
		voSan.setRowno(37.5f);
//		voSan.setHc("37.5");
		map.put(voSan.getRowno(), voSan);//vec.add(voSan) ;
		
		
		ds= getSumXJLLB(map,38,43);
		DZFDouble bqje38to43 = ds[0];
		DZFDouble bnje38to43 = ds[1];
		XjllbVO vo44 = new XjllbVO() ;
		vo44.setXm("　　现金流入小计") ;
		vo44.setHc("44") ;
		vo44.setRowno(44);
		vo44.setBqje(bqje38to43) ;
		vo44.setSqje(bnje38to43) ;
		vo44.setFormula(getFormula(38, 43));
		map.put(vo44.getRowno(), vo44);//vec.add(vo26) ;
		
		
		ds= getSumXJLLB(map,45,52);
		DZFDouble bqje45to52 = ds[0];
		DZFDouble bnje45to52 = ds[1];
		XjllbVO vo53 = new XjllbVO() ;
		vo53.setXm("　　现金流出小计") ;
		vo53.setHc("53") ;
		vo53.setRowno(53);
		vo53.setBqje(bqje45to52) ;
		vo53.setSqje(bnje45to52) ;
		vo53.setFormula(getFormula(45, 52));
		map.put(vo53.getRowno(), vo53);//vec.add(vo30) ;
		
		XjllbVO vo54 = new XjllbVO() ;
		vo54.setXm("　　筹资活动产生的现金流量净额") ;
		vo54.setHc("54") ;
		vo54.setRowno(54);
		vo54.setBqje(vo44.getBqje().sub(vo53.getBqje())) ;
		vo54.setSqje(vo44.getSqje().sub(vo53.getSqje())) ;
		vo54.setFormula("hc("+vo44.getHc()+") - hc("+vo53.getHc()+")");
		map.put(vo54.getRowno(), vo54);//vec.add(vo31) ;
		
		
		XjllbVO vo55 = new XjllbVO() ;
		vo55.setXm("四、汇率变动对现金的影响") ;
		vo55.setRowno(55f);
		vo55.setHc("55") ;
		map.put(vo55.getRowno(), vo55);//vec.add(voSan) ;
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
