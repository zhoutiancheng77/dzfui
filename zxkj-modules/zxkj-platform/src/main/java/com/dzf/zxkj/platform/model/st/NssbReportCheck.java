package com.dzf.zxkj.platform.model.st;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.util.Formula;

import java.util.HashMap;
import java.util.Map;

public class NssbReportCheck {

	private static NssbReportCheck reportcheck;
	
	public static NssbReportCheck getInstance(){
		if(reportcheck==null){
			reportcheck= new NssbReportCheck();
		}
		return reportcheck;
	}
	
	/**
	 * 编辑后检查
	 * @param vkey 检查的单元格(4_vmny) -> vno_item
	 * @param reportcode 报表代码
	 * @param reportvos 报表页面数据
	 * */
	public Object[] CheckCellEdit(String vkey,String reportcode,StBaseVO[] reportvos){

		DZFDouble zero =new DZFDouble(0.0D);
		Map<String,String[]> cm = getCheckRulMap(reportcode);
		String keys[]=cm.keySet().toArray(new String[0]);
		
		for(String key:keys){
			
			if(key.contains(vkey)){
				String[] fcodes=key.split("\\+|-|\\*|/|\\(|\\)");//前
				String[] after = cm.get(key)[0].split("_");//后
				String warnmessg=cm.get(key)[1];//提示消息 
				
				for(String code:fcodes){
					String[] front =code.split("_");
					int fi=Integer.parseInt(front[0])-1;
					String fc="rp_"+front[1];
					
//					DZFDouble dfront=null;
					Object ov = reportvos[fi].getAttributeValue(fc);
					DZFDouble dv=NssbReportUtil.getDvalue(ov);
					key=key.replaceFirst(code, dv.toString());
				}
				
				Formula fform=new Formula(key);
				DZFDouble dfront = new DZFDouble(fform.getResult());
				
				DZFDouble dafter = zero;
				if("%".equals(after[0])){
					dafter = new DZFDouble(1);
				}else{
					//后
					int ai=Integer.parseInt(after[0])-1;
					String ac="rp_"+after[1];
					
//					DZFDouble dafter=null;
					Object oafter = reportvos[ai].getAttributeValue(ac);
					 dafter=NssbReportUtil.getDvalue(oafter);
//					if(oafter!=null&&oafter.toString().length()>0){
//						dafter=new DZFDouble(oafter.toString());
//					}else{
//						dafter=zero;
//					}

				}
				if(dfront.compareTo(dafter)>0){//dfront>dafter 验证失败
					Object[] rs = new Object[2];
					rs[0]=false;
					rs[1]=warnmessg;
					return rs;
				}

				
				break;
			}
			
		}
		return null;
	}
	
	
	/**
	 * 保存前金额合规校验（全部校验）
	 * @param reportcode 报表代码
	 * @param reportvos 报表页面数据
	 * */
	public String CheckCellEdit(String reportcode,StBaseVO[] reportvos){
		DZFDouble zero =new DZFDouble(0.0D);
		Map<String,String[]> cm = getCheckRulMap(reportcode);
		
		String[] keys = cm.keySet().toArray(new String[0]);
		if(keys==null||keys.length==0){
			return null;
		}
//		StringBuffer errormsg = new StringBuffer();
		for(String key:keys){

			//前
			String[] front =key.split("_");
			int fi=Integer.parseInt(front[0])-1;
			String fc="rp_"+front[1];
			
//			DZFDouble dfront=null;
			Object ofront = reportvos[fi].getAttributeValue(fc);
			DZFDouble dfront=NssbReportUtil.getDvalue(ofront);
//			if(ofront!=null&&ofront.toString().length()>0){
//				dfront=new DZFDouble(ofront.toString());
//			}else{
//				dfront=zero;
//			}
			
			DZFDouble dafter =zero;
			//后
			String[] after = cm.get(key)[0].split("_");
			if("%".equals(after[0])){
				dafter = new DZFDouble(1);
			}else{
				int ai=Integer.parseInt(after[0])-1;
				String ac="rp_"+after[1];
				
//				DZFDouble dafter=null;
				Object oafter = reportvos[ai].getAttributeValue(ac);
				dafter=NssbReportUtil.getDvalue(oafter);
			}

//			if(oafter!=null&&oafter.toString().length()>0){
//				dafter=new DZFDouble(oafter.toString());
//			}else{
//				dafter=zero;
//			}
			
			if(dfront.compareTo(dafter)>0){//dfront>dafter 验证失败
//				errormsg.append(cm.get(key)[1]+"\n");
				return cm.get(key)[1];
			}
		
		}
		return null;
//		if(errormsg.length()>0){
//			return errormsg.toString();
//		}else{
//			return null;
//		}
	}
	
	
	/**
	 * 比较规则
	 * */
	private Map<String,String[]> getCheckRulMap(String reportcode){
		Map<String,String[]> cm = new HashMap<String,String[]>();
		String[][] checkruls = null;
		switch (reportcode) {
		case "A100000" :
			checkruls= check_nssb;
			break;
		case "A101010"://一般企业收入明细表
			checkruls= check_ybqysr;
			break;
		case "A102010"://一般企业成本支出明细表
			checkruls= check_ybqycb;
			break;
		case "A104000"://期间费用明细表
			checkruls= check_qjfy;
			break;
		case "A105000"://纳税调整项目明细表
			checkruls= check_nstz;
			break;
		case "A105050"://职工薪酬纳税调整明细表
			checkruls= check_zgxc;
			break;
		case "A105080"://资产折旧、摊销情况及纳税调整明细表
			checkruls= check_zjtxtz;
			break;
		case "B100000"://季度申报表
			checkruls=check_jdsbb;
			break;
		case "A107040"://减免所得税优惠明细表
			checkruls=check_jmsds;
			break;
		}
		if(checkruls!=null&&checkruls.length>0){
			for(String[] rul:checkruls){
				cm.put(rul[0], new String[]{rul[1],rul[2]});
			}
		}
		return cm;
	}
	
	//大小比较参数{"a","b","msg"};a<=b合法 msg=错误提示
	private static String[][] check_ybqysr= new String[][]{//一般企业收入
		{"4_vmny","3_vmny","金额不能大于[销售商品收入]!"},{"11_vmny","10_vmny","金额不能大于[销售材料收入]!"}
	};
	private static String[][] check_ybqycb= new String[][]{//一般企业成本支出
		{"4_vmny","3_vmny","金额不能大于[销售商品成本]!"},{"11_vmny","10_vmny","金额不能大于[材料销售成本]!"}
	};
	private static String[][] check_qjfy= new String[][]{//期间费用
		//财务费用
		{"2_vxsfyjwzf","2_vxsfy","金额不能大于[劳务费-销售费用]!"},{"3_vxsfyjwzf","3_vxsfy","金额不能大于[咨询顾问费-销售费用]!"},{"6_vxsfyjwzf","6_vxsfy","金额不能大于[佣金和手续费-销售费用]!"},{"11_vxsfyjwzf","11_vxsfy","金额不能大于[租赁费-销售费用]!"},{"15_vxsfyjwzf","15_vxsfy","金额不能大于[运输、仓储费-销售费用]!"},{"16_vxsfyjwzf","16_vxsfy","金额不能大于[修理费-销售费用]!"},{"18_vxsfyjwzf","18_vxsfy","金额不能大于[技术转让费-销售费用]!"},{"19_vxsfyjwzf","19_vxsfy","金额不能大于[研究费用-销售费用]!"},{"24_vxsfyjwzf","24_vxsfy","金额不能大于[其他-销售费用]!"},
		//管理费用
		{"2_vglfyjwzf","2_vglfy","金额不能大于[劳务费-管理费用]!"},{"3_vglfyjwzf","3_vglfy","金额不能大于[咨询顾问费-管理费用]!"},{"6_vglfyjwzf","6_vglfy","金额不能大于[佣金和手续费-管理费用]!"},{"11_vglfyjwzf","11_vglfy","金额不能大于[租赁费-管理费用]!"},{"15_vglfyjwzf","15_vglfy","金额不能大于[运输、仓储费-管理费用]!"},{"16_vglfyjwzf","16_vglfy","金额不能大于[修理费-管理费用]!"},{"18_vglfyjwzf","18_vglfy","金额不能大于[技术转让费-管理费用]!"},{"19_vglfyjwzf","19_vglfy","金额不能大于[研究费用-管理费用]!"},{"24_vglfyjwzf","24_vglfy","金额不能大于[其他-销售费用]!"},
		//财务费用
		{"6_vcwfyjwzf","6_vcwfy","金额不能大于[佣金和手续费-财务费用]!"},
		//{"21_vcwfyjwzf","21_vcwfy","金额不能大于[利息收支-财务费用]!"},{"22_vcwfyjwzf","22_vcwfy","金额不能大于[汇兑差额-财务费用]!"},
		{"24_vcwfyjwzf","24_vcwfy","金额不能大于[其他-财务费用]!"}
	};
	private static String[][] check_zgxc= new String[][]{//职工薪酬纳税调整表
		//账载金额
		{"2_vzzje","1_vzzje","金额不能大于[工资薪金支出-账载金额]"},
//		{"4_vzzje","14_vzzje","账载金额[职工教育经费支出]大于已计提的职工教育经费支出,是否继续?"},
		{"5_vzzje+6_vzzje","4_vzzje","五、六行之和不能大于[职工教育经费支出]"},
		//税收金额
		{"2_vssje","1_vssje","金额不能大于[工资薪金支出-税收金额]"}
	};
	private static String[][] check_nstz= new String[][]{//纳税调整项目明细表
		//账载金额
		{"9_vzzje","8_vzzje","金额不能大于[不征税收入-账载金额]"},
		{"9_vtzje","8_vtzje","金额不能大于[不征税收入-调增金额]"},
		{"9_vtjje","8_vtjje","金额不能大于[不征税收入-调减金额]"},
		{"25_vzzje","24_vzzje","金额不能大于[不征税收入用于支出所形成的费用-账载金额]"},
		{"25_vtzje","24_vtzje","金额不能大于[不征税收入用于支出所形成的费用-调增金额]"}
	};
	private static String[][] check_nssb=new String[][]{//纳税申报主表
		//{"34_vmny+35_vmny+36_vmny","33_vmny","金额不能大于[本年应补（退）所得税额-金额]"}
	};
	private static String[][] check_zjtxtz=new String[][]{//纳税申报主表
		{"2_vjszj2mny","2_vjszjmny","金额不能大于[本年加速折旧额]"},
		{"3_vjszj2mny","3_vjszjmny","金额不能大于[本年加速折旧额]"},
		{"4_vjszj2mny","4_vjszjmny","金额不能大于[本年加速折旧额]"},
		{"5_vjszj2mny","5_vjszjmny","金额不能大于[本年加速折旧额]"},
		{"6_vjszj2mny","6_vjszjmny","金额不能大于[本年加速折旧额]"},
		{"7_vjszj2mny","7_vjszjmny","金额不能大于[本年加速折旧额]"}
	};
	
	private static String[][] check_jdsbb=new String[][]{
		{"7_vbqmny","6_vbqmny","减免所得税额 不能大于应纳所得税额"},
		{"7_vljmny","6_vljmny","减免所得税额 不能大于应纳所得税额"},
		{"21_vbqmny","%","百分比不能大于1"},
		{"21_vljmny","%","百分比不能大于1"}
	};
	
	private static String[][] check_jmsds=new String[][]{
		{"2_vmny","1_vmny","减半征税 不能大于符合条件的小型微利企业"}
	};
}
