package com.dzf.zxkj.platform.model.st;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 纳税申报单元格编辑权限
 * */
public class NssbReportEditPower {

	private static NssbReportEditPower editpower;
	
	public static NssbReportEditPower getInstance(){
		if(editpower==null){
			editpower=new NssbReportEditPower();
		}
		return editpower;
	}
	
	/**
	 * 不可编辑项目
	 * */
	public List<String> getUnEditAble(String reportcode){
		String[] formula=null;
		List<String> ls = new ArrayList<String>();
		switch (reportcode) {
		case "A100000"://中华人民共和国企业所得税年度纳税申报表（A类）
			formula= nssbunedit;
			break;
		case "B100000"://中华人民共和国企业所得税年度纳税申报表（A类）
			formula= yjdnssbunedit;
			break;
		case "A101010"://一般企业收入明细表
			formula= ybqysrunedit;
			break;
		case "A102010"://一般企业成本支出明细表
			formula= ybqycbunedit;
			break;
		case "A104000"://期间费用明细表
			formula= qjfyunedit;
			break;
		case "A105050"://职工薪酬纳税调整明细表
			formula= zgxcunedit;
			break;
		case "A105060"://广告费和业务宣传费跨年度纳税调整明细表
			formula= ggywfunedit;
			break;
		case "A105070"://捐赠支出纳税调整明细表
			formula= jzzcnstzunedit;
			break;
		case "A105080"://资产折旧、摊销情况及纳税调整明细表
			formula= zjtxtzunedit;
			break;
		case "A106000"://企业所得税弥补亏损明细表
			formula= mbksunedit;
			break;
		case "A107040"://减免所得税优惠明细表
			formula= jmsdsunedit;
			break;
		case "A105000"://纳税调整明细表
			formula= nstzmxunedit;
			break;
		}
		if(formula!=null && formula.length>0){
			for(String fm:formula){
				ls.add(fm);
			}
		}
		return ls;
	}
	
	private Map<String,List<String>> uempa=null;
	public Map<String,List<String>> getUnEditAbleMap(){
		if(uempa==null){
			uempa=new HashMap<String,List<String>>(); 
			uempa.put("A100000", getUnEditAble("A100000"));
			uempa.put("A101010", getUnEditAble("A101010"));
			uempa.put("A102010", getUnEditAble("A102010"));
			uempa.put("A104000", getUnEditAble("A104000"));
			uempa.put("A105050", getUnEditAble("A105050"));
			uempa.put("A105060", getUnEditAble("A105060"));
			uempa.put("A105070", getUnEditAble("A105070"));
			uempa.put("A105080", getUnEditAble("A105080"));
			uempa.put("A106000", getUnEditAble("A106000"));
			uempa.put("A107040", getUnEditAble("A107040"));
			uempa.put("A105000", getUnEditAble("A105000"));
			
			uempa.put("B100000", getUnEditAble("B100000"));
		}
		return uempa;
	}
	
	private static String[] ybqysrunedit = new String[]{
		"1_vmny","2_vmny","9_vmny","16_vmny"
	};
	private static String[] ybqycbunedit = new String[]{
		"1_vmny","2_vmny","9_vmny","16_vmny"
	};
	private static String[] qjfyunedit = new String[]{
		"25_vxsfy","25_vxsfyjwzf","25_vcwfy","25_vcwfyjwzf","25_vglfy","25_vglfyjwzf"
	};
	private static String[] zgxcunedit = new String[]{
		"4_vzzje","13_vzzje",
		"3_vssgdkcl","5_vssgdkcl","6_vssgdkcl","7_vssgdkcl","10_vssgdkcl","11_vssgdkcl",
		"4_vljjzkc","13_vljjzkc",
		"3_vssje","5_vssje","6_vssje","4_vssje","7_vssje","8_vssje","9_vssje","10_vssje","11_vssje","12_vssje","13_vssje",
		"4_vnstzje","5_vnstzje","6_vnstzje","7_vnstzje","8_vnstzje","9_vnstzje","10_vnstzje","13_vnstzje",
		"5_vjzkcje","6_vjzkcje","12_vjzkcje","13_vjzkcje"	
	};
	
	private static String[] ggywfunedit= new String[]{
		"1_vmny","3_vmny","4_vmny","6_vmny","7_vmny","9_vmny","12_vmny","13_vmny"
	};
	
	private static String[] zjtxtzunedit= new String[]{
		"1_vzzmny","1_vbnzjmny","1_vljzjmny","1_vjsjcmny","1_vbnzj2mny","1_vjszjmny","1_vjszj2mny","1_vljzj2mny","1_vnstzmny","1_ctzyy",
		"8_vzzmny","8_vbnzjmny","8_vljzjmny","8_vjsjcmny","8_vbnzj2mny","8_vjszjmny","8_vjszj2mny","8_vljzj2mny","8_vnstzmny","8_ctzyy",
		"11_vzzmny","11_vbnzjmny","11_vljzjmny","11_vjsjcmny","11_vbnzj2mny","11_vjszjmny","11_vjszj2mny","11_vljzj2mny","11_vnstzmny","11_ctzyy",
		"19_vzzmny","19_vbnzjmny","19_vljzjmny","19_vjsjcmny","19_vbnzj2mny","19_vjszjmny","19_vjszj2mny","19_vljzj2mny","19_vnstzmny","19_ctzyy",
		"27_vzzmny","27_vbnzjmny","27_vljzjmny","27_vjsjcmny","27_vbnzj2mny","27_vjszjmny","27_vjszj2mny","27_vljzj2mny","27_vnstzmny","27_ctzyy"
		,
		"2_vnstzmny","3_vnstzmny","4_vnstzmny","5_vnstzmny","6_vnstzmny","7_vnstzmny","9_vnstzmny","10_vnstzmny","12_vnstzmny","13_vnstzmny","14_vnstzmny",
		"15_vnstzmny","16_vnstzmny","17_vnstzmny","18_vnstzmny","20_vnstzmny","21_vnstzmny","22_vnstzmny","23_vnstzmny","24_vnstzmny","25_vnstzmny","26_vnstzmny",
		
//		"2_vjsjcmny","3_vjsjcmny","4_vjsjcmny","5_vjsjcmny","6_vjsjcmny","7_vjsjcmny","9_vjsjcmny","10_vjsjcmny","12_vjsjcmny","13_vjsjcmny","14_vjsjcmny",
//		"15_vjsjcmny","16_vjsjcmny","17_vjsjcmny","18_vjsjcmny","20_vjsjcmny","21_vjsjcmny","22_vjsjcmny","23_vjsjcmny","24_vjsjcmny","25_vjsjcmny","26_vjsjcmny",
//		
//		"2_vbnzj2mny","3_vbnzj2mny","4_vbnzj2mny","5_vbnzj2mny","6_vbnzj2mny","7_vbnzj2mny","9_vbnzj2mny","10_vbnzj2mny","12_vbnzj2mny","13_vbnzj2mny","14_vbnzj2mny",
//		"15_vbnzj2mny","16_vbnzj2mny","17_vbnzj2mny","18_vbnzj2mny","20_vbnzj2mny","21_vbnzj2mny","22_vbnzj2mny","23_vbnzj2mny","24_vbnzj2mny","25_vbnzj2mny","26_vbnzj2mny",
//		
//		"2_vljzj2mny","3_vljzj2mny","4_vljzj2mny","5_vljzj2mny","6_vljzj2mny","7_vljzj2mny","9_vljzj2mny","10_vljzj2mny","12_vljzj2mny","13_vljzj2mny","14_vljzj2mny",
//		"15_vljzj2mny","16_vljzj2mny","17_vljzj2mny","18_vljzj2mny","20_vljzj2mny","21_vljzj2mny","22_vljzj2mny","23_vljzj2mny","24_vljzj2mny","25_vljzj2mny","26_vljzj2mny"
	};
	
	private static String[] mbksunedit= new String[]{
		"1_vnstzsd",
		"2_vdnkmbks","2_vtotal","2_vsjksmb",
		"3_vdnkmbks","3_vtotal","3_vsjksmb","3_vkjzksmb",
		"4_vdnkmbks","4_vtotal","4_vsjksmb","4_vkjzksmb",
		"5_vdnkmbks","5_vtotal","5_vsjksmb","5_vkjzksmb",
		"6_vdnkmbks","6_vsjksmb","6_vkjzksmb",
		"7_vnstzsd","7_vdnkmbks","7_vsjksmb","7_vkjzksmb",
		"8_vkjzksmb",
	};
	
	private static String[] jmsdsunedit= new String[]{
		"1_vmny","2_vmny",
		"3_vmny","6_vmny","7_vmny","11_vmny","15_vmny","19_vmny","26_vmny","29_vmny","50_vmny"
	};
	
	private static String[] nstzmxunedit= new String[]{
		"1_vtzje","1_vtjje",
		"7_vtzje","7_vtjje",
		"9_vtzje","9_vtjje",
		"10_vtzje","10_vtjje",
		"11_vtzje","11_vtjje",
		"12_vtzje","12_vtjje",
		"14_vzzje","14_vssje","14_vtzje","14_vtjje",
		"15_vzzje","15_vssje","15_vtzje",
		"16_vzzje","16_vtzje","16_vtjje",
		"17_vzzje","17_vssje","17_vtzje",
		"18_vtzje","18_vtjje",
		"19_vtzje",
		"20_vtzje",
		"21_vtzje",
		"22_vtzje","22_vtjje",
		"23_vzzje","23_vtzje","23_vtzje",
		"26_vtzje","26_vtjje",
		"27_vtzje",
		"29_vtzje","29_vtjje",
		"30_vtzje","30_vtjje",
		"31_vzzje","31_vssje","31_vtzje","31_vtjje",
		"32_vtzje","32_vtjje",
		"34_vtzje","34_vtjje",
		"35_vtzje","35_vtjje",
		"43_vtzje","43_vtjje",
		
	};
	
	private static  String[] jzzcnstzunedit= new String[]{
		"20_vgyzzje","20_vgykcxe","20_vgyssje","20_vgynstzje","20_vfgyzzje","20_vnstzje"
	};
	
	private static String[] nssbunedit= new String[]{
		"1_vmny","2_vmny","4_vmny","5_vmny","6_vmny","10_vmny","11_vmny","12_vmny","13_vmny","15_vmny","16_vmny","19_vmny","22_vmny","23_vmny"
		,"25_vmny","26_vmny","28_vmny","31_vmny","33_vmny"
	};
	private static String[] yjdnssbunedit=new String[]{
		"2_vbqmny","3_vbqmny","4_vbqmny","5_vbqmny","6_vbqmny","8_vbqmny","9_vbqmny","9_vbqmny","12_vbqmny","13_vbqmny","14_vbqmny","17_vbqmny","22_vbqmny",
		"2_vljmny","3_vljmny","4_vljmny","5_vljmny","6_vljmny","9_vljmny","12_vljmny","13_vljmny","14_vljmny","22_vljmny"
	};
}
