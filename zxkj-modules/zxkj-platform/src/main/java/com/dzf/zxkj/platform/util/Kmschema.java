package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.IAccountService;

import java.util.*;

/**
 * 根据科目方案查询相应的科目
 * @author zpm
 *
 */
public class Kmschema {
	
	private static String[][] bennianlirun = new String[][]{
		{"00000100000000Ig4yfE0005",	"3131"},
		{"00000100AA10000000000BMD","3103"},
		{"00000100AA10000000000BMF",	"4103"}
	};
	
	private static String[][] chengben = new String[][]{
		{"00000100AA10000000000BMF",	"6401"},
		{"00000100AA10000000000BMF",	"6402"},
		{"00000100AA10000000000BMD",	"5401"},
		{"00000100AA10000000000BMD",	"5402"},
		{"00000100000000Ig4yfE0005",	"5401"}
	};

	private static String[][] kcsps = new String[][]{
		{"00000100000000Ig4yfE0005","1243"},
		{"00000100AA10000000000BMD","1405"},
		{"00000100AA10000000000BMF","1405"},
		{"00000100000000Ig4yfE0003","1301"},
		{"00000100AA10000000000BMQ","1201"}
	};
	
	private static String[][] ycls = new String[][]{
		{"00000100000000Ig4yfE0005","1211"},
		{"00000100AA10000000000BMD","1403"},
		{"00000100AA10000000000BMF","1403"}
	};
	
	private static String[][]  shouru = new String[][]{
		{"00000100000000Ig4yfE0005"	,"510101","5101001"},
		{"00000100000000Ig4yfE0005"	,"510201","5102001"},
		{"00000100AA10000000000BMF"	,"600101","6001001"},
		{"00000100AA10000000000BMF"	,"605101","6051001"},
		{"00000100AA10000000000BMQ"	,"4501","4501"},
		{"00000100AA10000000000BMD"	,"500101","5001001"},
		{"00000100AA10000000000BMD"	,"505101","5051001"},
	};
	
	//出库科目，收入类科目..仅商品销售收入(2019-07-15去掉材料销售收入)
	private static String[][]  shouru1 = new String[][]{
		{"00000100000000Ig4yfE0005"	,"510101","5101001"},//企业会计制度
		{"00000100AA10000000000BMF"	,"600101","6001001"},//企业会计准则
		{"00000100AA10000000000BMQ"	,"4501","4501"},//民间非营利组织会计制度
		{"00000100AA10000000000BMD"	,"500101","5001001"},//小企业会计准则
	};
	
	private static  String[][] yingshouzk = new String[][]{
		{"00000100000000Ig4yfE0005","1131"},	//应收账款
		{"00000100AA10000000000BMD","1122"},	//应收账款
		{"00000100000000Ig4yfE0003","1212"},	//应收账款
		{"00000100AA10000000000BMQ","1121"},	//应收账款
		{"00000100AA10000000000BMF","1122"}	//应收账款
		
	};
	
	private static  String[][] yingfuzk = new String[][]{
		{"00000100000000Ig4yfE0003","2302"},	//应付账款
		{"00000100000000Ig4yfE0005","2121"},	//应付账款
		{"00000100AA10000000000BMD","2202"},	//应付账款
		{"00000100AA10000000000BMQ","2202"},	//应付账款
		{"00000100AA10000000000BMF","2202"}	//应付账款
	};

	private static  String[][] kucunxj = new String[][]{
		{"00000100000000Ig4yfE0005","1001"},	//现金
		{"00000100000000Ig4yfE0004","1001"},	//现金
		{"00000100000000Ig4yfE0006","1001"},	//库存现金
		{"00000100AA10000000000BMD","1001"},	//库存现金
		{"00000100AA10000000000BMQ","1001"},	//现金
		{"00000100AA10000000000BMF","1001"},	//库存现金
		{"00000100000000Ig4yfE0003","1001"}	//库存现金
	};

	private static  String[][] yinhangcunkuan = new String[][]{
		{"00000100000000Ig4yfE0005","1002"},	//银行存款
		{"00000100000000Ig4yfE0004","1002"},	//银行存款
		{"00000100000000Ig4yfE0006","1002"},	//银行存款
		{"00000100AA10000000000BMD","1002"},	//银行存款
		{"00000100000000Ig4yfE0003","1002"},	//银行存款
		{"00000100AA10000000000BMQ","1002"},	//银行存款
		{"00000100AA10000000000BMF","1002"}	//银行存款
	};
	
	private static  String[][] jinxiangshuie = new String[][]{
		{"00000100000000Ig4yfE0003","21010101","210100101","2101001001"},	//进项税额
		{"00000100AA10000000000BMQ","22060101","220600101","2206001001"},	//进项税额
		{"00000100000000Ig4yfE0005","21710101","217100101","2171001001"},	//进项税额
		{"00000100AA10000000000BMF","22210101","222100101","2221001001"},	//进项税额
		{"00000100AA10000000000BMD","22210101","222100101","2221001001"}	//进项税额
	};

	private static  String[][] xiaoshushuie = new String[][]{
		{"00000100000000Ig4yfE0003","21010102","210100102","2101001002"},	//销项税额
		{"00000100AA10000000000BMQ","22060102","220600102","2206001002"},	//销项税额
		{"00000100000000Ig4yfE0005","21710105","217100105","2171001005"},	//销项税额
		{"00000100AA10000000000BMF","22210102","222100102","2221001002"},	//销项税额
		{"00000100AA10000000000BMD","22210102","222100102","2221001002"}	//销项税额
	};
	
	private static  String[][] zhijiecailiao = new String[][]{
		{"00000100000000Ig4yfE0005","41010101","400100101","4001001001"},	//直接材料
		{"00000100AA10000000000BMF","50010101","500100101","5001001001"},	//直接材料
		{"00000100AA10000000000BMD","40010101","400100101","4001001001"}	//直接材料
	};

	
	private static  String[][] laowuchengben = new String[][]{
		{"00000100000000Ig4yfE0005","540102","5401002"},	//提供劳务成本
		{"00000100AA10000000000BMF","640102","6401002"},	//提供劳务成本
		{"00000100AA10000000000BMD","540102","5401002"}	//提供劳务成本
	};

	
	private static  String[][] laowushouru = new String[][]{
		{"00000100000000Ig4yfE0005","510102","5101002"},	//提供劳务收入
		{"00000100AA10000000000BMF","600102","6001002"},	//提供劳务收入
		{"00000100AA10000000000BMD","500102","5001002"}//提供劳务收入
	};
	
	//损益科目以 X 开头
	private static  String[][]  sunyikmstart = new String[][]{
		{"00000100AA10000000000BMD","5"},
		{"00000100000000Ig4yfE0005","5"},
		{"00000100AA10000000000BMF","6"}
	};
	
	public static String style_sp ="1";
	public static String style_ycliao ="2";
	public static String style_shouru ="3";
	public static String style_ys ="4";
	public static String style_yf ="5";
	public static String style_kcxj ="6";
	public static String style_yhck ="7";
	public static String style_jxse ="8";
	public static String style_xxse ="9";
	public static String style_zjcl ="10";
	public static String style_lwcb ="11";
	public static String style_lwsr ="12";
	
	private static String[][] getKmstyle(String kmstyle){
		String[][] args = null;
		if(style_sp.equals(kmstyle)){
			args = kcsps;
		}else if(style_ycliao.equals(kmstyle)){
			args = ycls;
		}else if(style_shouru.equals(kmstyle)){
			args = shouru;
		}else if(style_ys.equals(kmstyle)){
			args = yingshouzk;
		}else if(style_yf.equals(kmstyle)){
			args = yingfuzk;
		}else if(style_kcxj.equals(kmstyle)){
			args = kucunxj;
		}else if(style_yhck.equals(kmstyle)){
			args = yinhangcunkuan;
		}else if(style_jxse.equals(kmstyle)){//进项税额
			args = jinxiangshuie;
		}else if(style_xxse.equals(kmstyle)){
			args = xiaoshushuie;
		}else if(style_zjcl.equals(kmstyle)){
			args = zhijiecailiao;
		}else if(style_lwcb.equals(kmstyle)){
			args = laowuchengben;
		}else if(style_lwsr.equals(kmstyle)){
			args = laowushouru;
		}
		return args;
	}
	
	
	/**
	 * 根据行业+科目种类（进项，销项类科目等）+编码规则(4/2/2 4/3/2 4/3/3)
	 * @param kmstyle
	 * @param corptype
	 * @param coderule编码规则(4/2/2 4/3/2 4/3/3),目前只是支持3种类型
	 * @return
	 */
	public static String getKmCode(String kmstyle,String corptype,String coderule){
		if(StringUtil.isEmpty(kmstyle) || StringUtil.isEmpty(corptype)
				|| StringUtil.isEmpty(coderule)){
			return "";	
		}
		String[][] kms =  getKmstyle(kmstyle);
		if(kms!=null && kms.length>0){
			for(String[] str:kms){
				if(corptype.equals(str[0])){//默认第一个是行业
					if(coderule.startsWith("4/2/2")){
						return str[1];
					}else if(coderule.startsWith("4/3/2")){
						return str[2];
					}else if(coderule.startsWith("4/3/3")){
						return str[3];
					}
				}
			}
		}
		return "";
	}
	
	//得到所有出库的科目，校验用
	public static List<String> getAllChuku_KM(String corptype, YntCpaccountVO[] vos){
		if(StringUtil.isEmpty(corptype) || vos == null || vos.length ==0)
			return null;
		Set<String> set = new HashSet<String>();
		for(String[] sps : shouru1){
			if(sps[0].equals(corptype)){
				for(int i=1;i<sps.length;i++){
					set.add(sps[i]);
				}
			}
		}
		List<String> list = new ArrayList<String>();
		for(YntCpaccountVO kmvo : vos){
			if(!StringUtil.isEmpty(kmvo.getAccountcode())){
				for(String v : set){
					if(kmvo.getAccountcode().startsWith(v)
							&& kmvo.getIsleaf()!=null 
							&& kmvo.getIsleaf().booleanValue()){
						list.add(kmvo.getAccountcode());
					}
				}
			}
		}
		return list;
	}
	//得到存货分类 ，即1403,1405 的以下所有科目包括1403,1405，校验用
	public static List<String> getAllCunhuo_KM(String corptype,YntCpaccountVO[] vos){
		if(StringUtil.isEmpty(corptype) || vos == null || vos.length ==0)
			return null;
		Set<String> set = new HashSet<String>();
		//zpm 这版先注掉 2018.08 ~ 09
//		for(String[] sps : ycls){
//			if(sps[0].equals(corptype)){
//				for(int i=1;i<sps.length;i++){
//					set.add(sps[i]);
//				}
//			}
//		}
		for(String[] sps : kcsps){
			if(sps[0].equals(corptype)){
				for(int i=1;i<sps.length;i++){
					set.add(sps[i]);
				}
			}
		}
		List<String> list = new ArrayList<String>();
		for(YntCpaccountVO kmvo : vos){
			if(!StringUtil.isEmpty(kmvo.getAccountcode())){
				for(String v : set){
					if(kmvo.getAccountcode().startsWith(v)){
						list.add(kmvo.getAccountcode());
					}
				}
			}
		}
		return list;
	}
	
	//得到存货分类，即1403,1405的二级
	public static List<String> getKmclassify(String corptype,YntCpaccountVO[] vos){
		if(StringUtil.isEmpty(corptype) || vos == null || vos.length ==0)
			return null;
		Set<String> set = new HashSet<String>();
		for(String[] sps : ycls){
			if(sps[0].equals(corptype)){
				for(int i=1;i<sps.length;i++){
					set.add(sps[i]);
				}
			}
		}
		for(String[] sps : kcsps){
			if(sps[0].equals(corptype)){
				for(int i=1;i<sps.length;i++){
					set.add(sps[i]);
				}
			}
		}
		List<String> list = new ArrayList<String>();
		for(YntCpaccountVO kmvo : vos){
			if(!StringUtil.isEmpty(kmvo.getAccountcode())){
				for(String v : set){
					if(kmvo.getAccountcode().startsWith(v)
							&& (kmvo.getAccountcode().length() == 6 
							|| kmvo.getAccountcode().length() == 7)
							&& kmvo.getIsleaf()!=null 
							&& kmvo.getIsleaf().booleanValue()){
						list.add(kmvo.getAccountcode());
					}
				}
			}
		}
		return list;
	}
	
	//得到出库科目
	public static List<String> getChukuKm(String corptype,YntCpaccountVO[] vos){
		if(StringUtil.isEmpty(corptype) || vos == null || vos.length ==0)
			return null;
		Set<String> set = new HashSet<String>();
		for(String[] sps : shouru){
			if(sps[0].equals(corptype)){
				for(int i=1;i<sps.length;i++){
					set.add(sps[i]);
				}
			}
		}
		List<String> list = new ArrayList<String>();
		for(YntCpaccountVO kmvo : vos){
			if(!StringUtil.isEmpty(kmvo.getAccountcode())){
				for(String v : set){
					if(kmvo.getAccountcode().startsWith(v)
							&& kmvo.getIsleaf()!=null 
							&& kmvo.getIsleaf().booleanValue()){
						list.add(kmvo.getAccountcode());
					}
				}
			}
		}
		return list;
	}
	
	public static String getKmid(String corptype,String kmstyle, Map<String,YntCpaccountVO> kmmap){
		String kmid = null;
		if(StringUtil.isEmpty(corptype) || StringUtil.isEmpty(kmstyle))
			return kmid;
		String[][] strs = getKmstyle(kmstyle);
		if(strs == null || strs.length == 0)
			return kmid;
		for(String[] sps : strs){
			if(sps[0].equals(corptype)){
				for(int i =1;i<sps.length;i++){
					if(kmmap.containsKey(sps[i])){
						YntCpaccountVO vo = kmmap.get(sps[i]);
						kmid = vo.getPk_corp_account();
						break;
					}
				}
				if(!StringUtil.isEmpty(kmid)){
					break;
				}
			}
		}
		return kmid;
	}
	
	public static boolean isChengben(String corptype,String kmcode){
		if(StringUtil.isEmpty(corptype) || StringUtil.isEmpty(kmcode))
			return false;
		boolean iskcsp = false;
		for(String[] sps : chengben){
			if(sps[0].equals(corptype)){
				if(kmcode.startsWith(sps[1])){
					iskcsp = true;
					break;
				}
			}
		}
		return iskcsp;
	}

	public static boolean isSunyikm(String corptype,String kmcode){
		if(StringUtil.isEmpty(corptype) || StringUtil.isEmpty(kmcode))
			return false;
		boolean issunyi = false;
		for(String[] sps : sunyikmstart){
			if(sps[0].equals(corptype)){
				if(kmcode.startsWith(sps[1])){
					issunyi = true;
					break;
				}
			}
		}
		return issunyi;
	}
	
	
	public static boolean isbennianlirunpz(String corptype,String kmcode){
		if(StringUtil.isEmpty(corptype) || StringUtil.isEmpty(kmcode))
			return false;
		boolean iskcsp = false;
		for(String[] sps : bennianlirun){
			if(sps[0].equals(corptype)){
				if(kmcode.startsWith(sps[1])){
					iskcsp = true;
					break;
				}
			}
		}
		return iskcsp;
	}
	
	
	public static boolean isshouru(String corptype,String kmcode){
		if(StringUtil.isEmpty(corptype) || StringUtil.isEmpty(kmcode))
			return false;
		boolean isshouru = false;
		for(String[] sps : shouru){
			if(sps[0].equals(corptype)){
				if(kmcode.startsWith(sps[1]) || kmcode.startsWith(sps[2])){
					isshouru = true;
					break;
				}
			}
		}
		return isshouru;
	}
	public static boolean isKcspbm(String corptype,String kmcode){
		if(StringUtil.isEmpty(corptype) || StringUtil.isEmpty(kmcode))
			return false;
		boolean iskcsp = false;
		for(String[] sps : kcsps){
			if(sps[0].equals(corptype)){
				if(kmcode.startsWith(sps[1])){
					iskcsp = true;
					break;
				}
			}
		}
		return iskcsp;
	}
	public static boolean isYclbm(String corptype,String kmcode){
		if(StringUtil.isEmpty(corptype)|| StringUtil.isEmpty(kmcode))
			return false;
		boolean isycl = false;
		for(String[] sps : ycls){
			if(sps[0].equals(corptype)){
				if(kmcode.startsWith(sps[1])){
					isycl = true;
					break;
				}
			}
		}
		return isycl;
	}
	// 是否为存货大类
	public static boolean isKmclassify(String pk_corp, String corptype, String kmid){
		boolean isKmclassify = false;
		if (StringUtil.isEmpty(pk_corp)
				|| StringUtil.isEmpty(corptype) || StringUtil.isEmpty(kmid)) {
			return isKmclassify;
		}

		IAccountService accountService = (IAccountService) SpringUtils.getBean("AccountServiceImpl");

		YntCpaccountVO[] vos = accountService.queryByPk(pk_corp);
		String kmCode = null;
		for (YntCpaccountVO yntCpaccountVO : vos) {
			if (kmid.equals(yntCpaccountVO.getPk_corp_account())) {
				kmCode = yntCpaccountVO.getAccountcode();
				break;
			}
		}
		if (kmCode != null) {
			List<String> codes = getKmclassify(corptype, vos);
			if (codes.contains(kmCode)) {
				isKmclassify = true;
			}
		}
		
		return isKmclassify;
	}
	
	
	/**
	 * 判断是不是成本结转凭证
	 */
	public  static boolean ischengbenpz(CorpVO corpvo, TzpzBVO[] bodyvos){
		if(bodyvos == null || bodyvos.length == 0)
			return false;
		boolean falg = false;
		for(TzpzBVO v : bodyvos){
			if(Kmschema.isChengben(corpvo.getCorptype(), v.getVcode())){
				falg = true;
				break;
			}
		}
		return falg;
	}
	
	/**
	 * 判断是不是损益凭证
	 */
	public static boolean isSunYipz(CorpVO corpvo, TzpzBVO[] bodyvos){
		if(bodyvos == null || bodyvos.length == 0)
			return false;
		boolean falg = false;
		for(TzpzBVO v : bodyvos){
			if(Kmschema.isSunyikm(corpvo.getCorptype(), v.getVcode())){
				falg = true;
				break;
			}
		}
		return falg;
	}
	
	/**
	 * 判断是不是本年利润凭证
	 */
	public  static boolean isbennianlirunpz(CorpVO corpvo,TzpzBVO[] bodyvos){
		if(bodyvos == null || bodyvos.length == 0)
			return false;
		boolean falg = false;
		for(TzpzBVO v : bodyvos){
			if(Kmschema.isbennianlirunpz(corpvo.getCorptype(), v.getVcode())){
				falg = true;
				break;
			}
		}
		return falg;
	}
}