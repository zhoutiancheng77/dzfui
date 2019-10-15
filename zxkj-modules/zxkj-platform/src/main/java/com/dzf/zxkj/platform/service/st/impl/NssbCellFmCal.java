package com.dzf.zxkj.platform.service.st.impl;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.st.StBaseVO;
import com.dzf.zxkj.platform.model.st.StZgxcNstzVO;
import com.dzf.zxkj.platform.model.st.StqjfyVO;

import java.util.Map;

/**
 * 单元格公式计算
 * */
public class NssbCellFmCal {
	
	public static final String RERPCAL="1";
	public static final String CAL="0";
	
	//private static DZFDouble zero = new DZFDouble(0.0D);
	
	public Map<String, StBaseVO> calculateCell(){
		return null;
	} 
	
	public NssbCellFmCal(){}
	
	public StBaseVO[] calculateCell(String reportcode,StBaseVO[] reportvos){
//		if("A104000".equalsIgnoreCase(reportcode)){
//			reportvos = CalculateQjfy((StqjfyVO[])reportvos);
//		}else 
		if("A105050".equalsIgnoreCase(reportcode)){
			reportvos = CalculateZgxc((StZgxcNstzVO[])reportvos);
		}
		
		return reportvos;
	}
	
	/**
	 * 根据前台值重新计算
	 * */
	public StBaseVO[] reCalculateRpCell(String reportcode,StBaseVO[] reportvos){
//		if("A104000".equalsIgnoreCase(reportcode)){
//			reportvos = CalculateQjfy((StqjfyVO[])reportvos);
//		}else 
		if("A105050".equalsIgnoreCase(reportcode)){
			reportvos = reCalculateZgxc((StZgxcNstzVO[])reportvos);
		}
		
		return reportvos;
	}
	
	
	private StqjfyVO[] CalculateQjfy(StqjfyVO[] qjfyvos){
		
		String c_6_cwfy=qjfyvos[5].getRp_vcwfy();
		DZFDouble d_6_cwfy=DZFDouble.ZERO_DBL;
		if(c_6_cwfy!=null&&c_6_cwfy.length()>0){
			d_6_cwfy=new DZFDouble(c_6_cwfy);
		}
		
		String c_24_cwfy=qjfyvos[23].getRp_vcwfy();
		DZFDouble d_24_cwfy=DZFDouble.ZERO_DBL;
		if(c_24_cwfy!=null&&c_24_cwfy.length()>0){
			d_24_cwfy=new DZFDouble(c_24_cwfy);
		}
		
		d_24_cwfy=d_24_cwfy.sub(d_6_cwfy);
		qjfyvos[23].setRp_vcwfy(d_24_cwfy.toString());
		
		return qjfyvos;
	}
	
	private StZgxcNstzVO[] CalculateZgxc(StZgxcNstzVO[] reportvos){
		NssbReportUtil util = new NssbReportUtil();
		String fm_1="IF('1#vssje'*'3#vssgdkcl'<'3#vzzje','1#vssje'*'3#vssgdkcl','3#vzzje')";//3_税收金额
		DZFDouble dssje3 = util.CalCulateBlFm(fm_1, reportvos);
		reportvos[2].setVssje(dssje3);
		
		String fm_2="IF(('5#vzzje'+'5#vljjzkc')<'1#vssje'*'5#vssgdkcl',('5#vzzje'+'5#vljjzkc'),'1#vssje'*'5#vssgdkcl')";//5_税收金额
		DZFDouble dssje5 = util.CalCulateBlFm(fm_2, reportvos);
		reportvos[4].setVssje(dssje5);
		
		if(reportvos[5].getVzzje()!=null){
			DZFDouble dssje6 =reportvos[5].getVzzje().multiply(reportvos[5].getVssgdkcl());//6_税收金额
			reportvos[5].setVssje(dssje6);
		}
		
		String fm_3="IF('7#vzzje'<('1#vssje'*'7#vssgdkcl'),'7#vzzje',('1#vssje'*'7#vssgdkcl'))";//7_税收金额
		DZFDouble dssje7 = util.CalCulateBlFm(fm_3, reportvos);
		reportvos[6].setVssje(dssje7);
		
		return reportvos;
	}
	
	//** 重新计算薪酬
	private StZgxcNstzVO[] reCalculateZgxc(StZgxcNstzVO[] reportvos){
		NssbReportUtil util = new NssbReportUtil();
		String fm_1="IF('1#rp_vssje'*'3#rp_vssgdkcl'<'3#rp_vzzje','1#rp_vssje'*'3#rp_vssgdkcl','3#rp_vzzje')";//3_税收金额
		DZFDouble dssje3 = util.CalCulateBlFm(fm_1, reportvos);
		reportvos[2].setRp_vssje(dssje3.toString());
		
		String fm_2="IF(('5#rp_vzzje'+'5#rp_vljjzkc')<'1#rp_vssje'*'5#rp_vssgdkcl',('5#rp_vzzje'+'5#rp_vljjzkc'),'1#rp_vssje'*'5#rp_vssgdkcl')";//5_税收金额
		DZFDouble dssje5 = util.CalCulateBlFm(fm_2, reportvos);
		reportvos[4].setRp_vssje(dssje5.toString());
		
		if(reportvos[5].getVzzje()!=null){
			DZFDouble dssje6 =reportvos[5].getVzzje().multiply(reportvos[5].getVssgdkcl());//6_税收金额
			reportvos[5].setRp_vssje(dssje6.toString());
		}
		
		String fm_3="IF('7#rp_vzzje'<('1#rp_vssje'*'7#rp_vssgdkcl'),'7#rp_vzzje',('1#rp_vssje'*'7#rp_vssgdkcl'))";//7_税收金额
		DZFDouble dssje7 = util.CalCulateBlFm(fm_3, reportvos);
		reportvos[6].setRp_vssje(dssje7.toString());
		
		return reportvos;
	}

}
