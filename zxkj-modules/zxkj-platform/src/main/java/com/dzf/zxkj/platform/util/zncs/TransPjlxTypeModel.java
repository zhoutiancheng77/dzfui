package com.dzf.zxkj.platform.util.zncs;

import java.util.HashMap;
import java.util.Map;
import com.dzf.zxkj.common.constant.FieldConstant;

public class TransPjlxTypeModel {

	public final static String vbstype = "vbstype";//业务类型
	public final static String szstyle = "szstyle";//结算方式
	public final static String fpstyle = "fpstyle";//发票类型
	
//	public final static String YBR = "一般纳税人";
	
	public static Map<String, String> tranPjlx(Integer pjlxtype, String chargedeptname){
		Map<String, String> map = new HashMap<String, String>();
		String suffix = "";
//		if(YBR.equals(chargedeptname)){
//			suffix="（一般人）";
//		}
		switch(pjlxtype){
			case 0://自开销售发票
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_22, 
						FieldConstant.SZSTYLE_05,
						FieldConstant.FPSTYLE_02);
				break;
			}
			case 1://代开销售发票
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_22, 
						FieldConstant.SZSTYLE_05, 
						FieldConstant.FPSTYLE_01);
				break;
			}
			case 2://采购专用发票
			{
					fillMap(map, 
							FieldConstant.YWSTYLE_24+suffix, 
							FieldConstant.SZSTYLE_06, 
							FieldConstant.FPSTYLE_01);
				
				break;
			}
			case 3://采购普通发票
			{
					fillMap(map, 
							FieldConstant.YWSTYLE_24+suffix, 
							FieldConstant.SZSTYLE_06, 
							FieldConstant.FPSTYLE_02);
				break;
			}
			case 4://银行收款单
			{
				
				fillMap(map, 
						FieldConstant.YWSTYLE_25, 
						FieldConstant.SZSTYLE_03, 
						FieldConstant.FPSTYLE_20);
				break;
			}
			case 5://银行付款单
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_26, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_20);
				break;
			}
			case 6://银行手续费
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_27, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_20);
				break;
			}
			case 7://银行利息回单
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_28, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_21);
				break;
			}
			case 8://社保、公积金缴存单
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_08, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_20);
				break;
			}
			case 9://缴纳税款
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_07, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_20);
				break;
			}
			case 10://工资单
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_10, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_21);
				break;
			}
			case 11://交通费
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_29, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_21);
				break;
			}
			case 12://办公费
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_02, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_21);
				break;
			}
			case 13://招待费
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_30, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_21);
				break;
			}
			case 14://差旅费
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_31, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_21);
				break;
			}
			case 15://其他单据
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_02, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_21);
				break;
			}
			case 17://自开劳务发票
			{
				fillMap(map, 
						FieldConstant.YWSTYLE_15, 
						FieldConstant.SZSTYLE_05, 
						FieldConstant.FPSTYLE_02);
				break;
			}
			case 18://代开劳务发票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_15, 
						FieldConstant.SZSTYLE_05, 
						FieldConstant.FPSTYLE_01);
				break;
			}
			case 101://对公现金存款回单
			{
				fillMap(map,
						FieldConstant.YWSTYLE_09, 
						FieldConstant.SZSTYLE_03, 
						FieldConstant.FPSTYLE_20);
				break;
			}
			case 102://费用报销单
			{
				fillMap(map,
						FieldConstant.YWSTYLE_21, 
						FieldConstant.SZSTYLE_04, 
						FieldConstant.FPSTYLE_21);
				break;
			}
			
			/**********************************************/
			
			case 110://劳务费专票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_32+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_01);
				break;
			}
			
			case 111://劳务费普票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_32+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_02);
				break;
			}
			
			case 120://咨询顾问费专票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_33+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_01);
				break;
			}
			
			case 121://咨询顾问费普票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_33+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_02);
				break;
			}
			
			case 130://报办公费专票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_02+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_01);
				break;
			}
			
			case 131://报办公费普票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_02+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_02);
				break;
			}
			
			case 140://租赁费专票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_34+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_01);
				break;
			}
			
			case 141://租赁费普票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_34+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_02);
				break;
			}
			
			case 150://研究费用专票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_35+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_01);
				break;
			}
			
			case 151://研究费用普票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_35+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_02);
				break;
			}
			
			case 160://业务招待费专票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_36+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_01);	
				break;
				
			}
			
			case 161://业务招待费普票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_36+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_02);	
				break;
				
			}
			
			
			case 170://差旅费专票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_37+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_01);	
				break;
				
			}
			
			case 171://差旅费普票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_37+suffix, 
						FieldConstant.SZSTYLE_06, 
						FieldConstant.FPSTYLE_02);	
				break;
				
			}
			case 180://加油票专票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_38, 
						FieldConstant.SZSTYLE_02, 
						FieldConstant.FPSTYLE_01);	
				break;
				
			}
			
			case 181://加油票普票
			{
				fillMap(map,
						FieldConstant.YWSTYLE_38, 
						FieldConstant.SZSTYLE_02, 
						FieldConstant.FPSTYLE_02);	
				break;
				
			}
			
		}
				
		return map;
	}
	
	private static void fillMap(Map<String, String> map, 
			String vb, String sz, String fp){
		
		map.put(vbstype, vb);
		map.put(szstyle, sz);
		map.put(fpstyle, fp);
	}
}
