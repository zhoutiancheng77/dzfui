package com.dzf.zxkj.platform.model.st;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.services.st.impl.NssbBaseDMO;
import com.dzf.zxkj.platform.services.st.impl.NssbReportRCellDMO;
import com.dzf.zxkj.platform.util.Formula;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class NssbReportUtil {
	
	public static final String approved="1";
	public static final String unapproved="0";
	
	public static String[] reportcodes =new String[]{"A100000","A101010","A102010","A104000","A105050","A105080","A105060","A105070","A106000","A107040","A105000"};
//		,"A105080","A106000","A107040","A105000"};
	
	public static String[] yjdreportcodes =new String[]{"B100000"};
	
	public static String yjdnssb="B100000";//月季度纳税申报
	
	public static String nssb="A100000";//纳税申报表
	
	public static String ybqysr="A101010";//一般企业收入
	
	public static String ybqycb="A102010";//一般企业成本
	
	public static String qjfy="A104000";//期间费用
	
	public static String zgxc="A105050";//职工薪酬
	
	public static String ggywf="A105060";//广告费和业务宣传费跨年度纳税调整明细表
	
	public static String jzzcnstz="A105070";//捐赠支出纳税调整明细表
	
	public static String zjtxnstz="A105080";//资产折旧、摊销情况及纳税调整明细表
	
	public static String mbks="A106000";//企业所得税弥补亏损明细表
	
	public static String jmsds="A107040";//减免所得税优惠明细表
	
	public static String nstzmx="A105000";//纳税调整项目明细表
	
	public static final String ERROR_CHK="error_chk";//金额检查有错
	public static final String ERROR_CAL="error_cal";//计算出错
	
	public static final DZFDouble zero = new DZFDouble(0.0D);
	
	private NssbBaseDMO dmo;
	public NssbReportUtil(NssbBaseDMO rdmo){
		dmo=rdmo;
	}
	
	public NssbReportUtil(){
	}

	private SingleObjectBO sbo;
	
	public SingleObjectBO getSbo() {
		if(sbo==null){
			sbo=(SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		}
		return sbo;
	}
	
	public static DZFDouble getDvalue(Object repvalue){
		if(repvalue!=null&&repvalue.toString().length()>0){
			String svalue=repvalue.toString();
			svalue=svalue.replaceAll("bq|lj|NA|STA|EDIT|FM|_", "");
			if(svalue.length()==0){
				return zero;
			}else{
				return new DZFDouble(svalue.toString());
			}
		}else{
			return zero;
		}
	}
	
	public static Class getReportBeanType(String reportcode){
		Class beantype=null;
		switch (reportcode) {
		case "A100000"://纳税申报表
			beantype= StNssbMainVO.class;
			break;
		case "B100000"://月季度纳税申报表
			beantype= StYjdNssbMainVO.class;
			break;
		case "A101010"://一般企业收入明细表
			beantype= StYbqysrVO.class;
			break;
		case "A102010"://一般企业成本支出明细表
			beantype= StYbqycbVO.class;
			break;
		case "A104000"://期间费用明细表
			beantype= StqjfyVO.class;
			break;
		case "A105050"://职工薪酬纳税调整明细表
			beantype= StZgxcNstzVO.class;
			break;
		case "A105060"://广告费和业务宣传费跨年度纳税调整明细表
			beantype= StGgywfVO.class;
			break;
		case "A105070"://捐赠支出明细
			beantype= StJzzcVO.class;
			break;
		case "A105080"://资产折旧、摊销情况及纳税调整明细表
			beantype= StZjtxNstzVO.class;
			break;
		case "A106000"://企业所得税弥补亏损明细表
			beantype= StMbksVO.class;
			break;
		case "A107040"://减免所得税优惠明细表
			beantype= StJmsdsVO.class;
			break;
		case "A105000"://纳税调整项目明细表
			beantype= StNstzmxVO.class;
			break;
		}
		
		if(beantype==null){
			throw new BusinessException("报表:"+reportcode+"没有定义实体类型,请前往NssbReportUtil注册");
		}
		return beantype;
	}
	
	public static String getReportTableName(String reportcode){
		String tablename =null;
		switch (reportcode) {
		case "A100000"://纳税申报表
			tablename= "ynt_st_nssbmain";
			break;
		case "B100000"://纳税申报表
			tablename= "ynt_st_yjdnssbmain";
			break;
		case "A101010"://一般企业收入明细表
			tablename= "ynt_st_ybqysr";
			break;
		case "A102010"://一般企业成本支出明细表
			tablename= "ynt_st_ybqycb";
			break;
		case "A104000"://期间费用明细表
			tablename= "ynt_st_qjfy";
			break;
		case "A105050"://职工薪酬纳税调整明细表
			tablename= "ynt_st_zgxcnstz";
			break;
		case "A105060"://广告费和业务宣传费跨年度纳税调整明细表
			tablename= "ynt_st_ggywf";
			break;
		case "A105070"://捐赠支出纳税调整明细表
			tablename= "ynt_st_jzzcnstz";
			break;
		case "A105080"://资产折旧、摊销情况及纳税调整明细表
			tablename= "ynt_st_zjtxnstz";
			break;
		case "A106000"://企业所得税弥补亏损明细表
			tablename= "ynt_st_mbks";
			break;
		case "A107040"://减免所得税优惠明细表
			tablename= "ynt_st_jmsds";
			break;
		case "A105000"://纳税调整项目明细表
			tablename= "ynt_st_nstzmx";
			break;
		}
		
		if(tablename==null){
			throw new BusinessException("报表:"+reportcode+"没有定义数据库表,请前往NssbReportUtil注册");
		}
		return tablename;
	}
	
	
	public DZFDouble CalCulateMutiBl(String fomula,StBaseVO[] reportvos){
		DZFDouble dvalue = zero;
//		String ff ="IF(F6*D8<=C8,IF(F8+2<100,20,IF(A<B,(C+D),E)),IF(ABC,DEF,GHI))";
		
		int leftBracket = 0;
		int startL = fomula.indexOf("(");
		String fm = fomula;
		if (startL != -1) {
			fm = fm.substring(startL+1, fm.length());
		}
		while (startL != -1) {
			leftBracket++;
			startL = fm.indexOf("(");
			fm = fm.substring(startL+1, fm.length());
		}
		
		Map<String,String> vm=new HashMap<>();
		for(int i=0;i<leftBracket;i++){
			int iStart = fomula.lastIndexOf("(") + 1;
			// 获得最里层括号里的内容
			String formulaStr = fomula.substring(iStart,
					iStart + fomula.substring(iStart).indexOf(")")).trim();
			
			if(formulaStr.contains(",")){
				
				String[] keys=vm.keySet().toArray(new String[0]);
				if(keys!=null&&keys.length>0){
					for(String key:keys){
						formulaStr=formulaStr.replace(key, vm.get(key));
					}
					vm.clear();
				}
				
				DZFDouble dv = CalCulateBlFm("IF("+formulaStr+")", reportvos);

				iStart = fomula.lastIndexOf("(");
				int iEnd = fomula.substring(iStart).indexOf(")") + 1;
				fomula = fomula.substring(0, iStart-2).trim()
						+ dv.toString()
						+ fomula.substring(iStart + iEnd, fomula.length())
								.trim();
				i++;
			}else{

				fomula=fomula.replace("("+formulaStr+")", "k"+i);
				vm.put("k"+i, "("+formulaStr+")");
			
			}
			
			
//			dvalue=CalCulateBlFm(fomula, reportvos);
		}
		if(fomula.contains(",")){
			dvalue=CalCulateBlFm(fomula, reportvos);
		}else{
			dvalue=getDvalue(fomula);
		}
//		dvalue=getDvalue(fomula);
//				CalCulateBlFm(fomula, reportvos);
		
		return dvalue;
	}
	
	//IF(A<B,C,D) if A<B RETURN C ELSE RETURN D
	public DZFDouble CalCulateBlFm(String fm,StBaseVO[] reportvos){
		log.info("本次计算公式："+fm);
//		fm ="IF(F6*D8<C8,F6*D8,C8)";
//		fm1="IF(a<b,IF(c<d,e,f),(2+3))";
		fm=fm.replaceAll("IF|\\(|\\)", "");
		String[] ems=fm.split(",");
		String[] cpstr = ems[0].split("==|>=|<=|<|>");
		int start =ems[0].indexOf(cpstr[0])+cpstr[0].length();
		int end =ems[0].indexOf(cpstr[1], start);
//				.indexOf(cpstr[1]);
		String symbol=ems[0].substring(start,end);
		
//		cpstr[0]=replaceItemValue(cpstr[0], reportvos);
//		cpstr[1]=replaceItemValue(cpstr[1], reportvos);
//		ems[1]=replaceItemValue(ems[1], reportvos);
//		ems[2]=replaceItemValue(ems[2], reportvos);
		
		DZFDouble da=getItemValue(cpstr[0], reportvos);
		DZFDouble db=getItemValue(cpstr[1], reportvos);
		DZFDouble dc=getItemValue(ems[1], reportvos);
		DZFDouble dd=getItemValue(ems[2], reportvos);
		
		int r = da.compareTo(db);//=1 a>b
		
		Boolean b=false;
		if("==".equals(symbol)&&r==0){
			b=true;
		}else if(">".equals(symbol)&&r>0){
			b=true;
		}else if("<".equals(symbol)&&r<0){
			b=true;
		}else if(">=".equals(symbol)&&r>=0){
			b=true;
		}else if("<=".equals(symbol)&&r<=0){
			b=true;
		}
		
		if(b){
			return dc;
		}else{
			return dd;
		}
	}
//	
//	private String replaceItemValue(String itemstr,StBaseVO[] reportvos){
//		String[] codes=itemstr.split("\\+|-|\\*|/|\\(|\\)");
//		for(String code:codes){
//			String[] as =code.split("_");
//			int fi=Integer.parseInt(as[0])-1;
////			String fc="rp_"+front[1];
//			Object oas = reportvos[fi].getAttributeValue(as[1]);
//			DZFDouble das=NssbReportUtil.getDvalue(oas);
//			itemstr=itemstr.replaceAll(code, das.toString());
//		}
//		return itemstr;
//	}
	
	private DZFDouble getItemValue(String itemstr,StBaseVO[] reportvos){
		
		String[] codes=itemstr.split("\\+|-|\\*|/|\\(|\\)");
		if(codes.length<=1&&!codes[0].contains("'")&&!codes[0].startsWith("[")){
			return NssbReportUtil.getDvalue(codes[0].replaceAll("'", ""));
		}
		int fi=-1;
		DZFDouble das=DZFDouble.ZERO_DBL;
		Object oas=null;
		
		for(String code:codes){
			if(code.startsWith("'")){
//				String[] as =code.split("_");
				String[] as =code.split("#");
				fi=Integer.parseInt(as[0].replaceAll("'", ""))-1;
//				String fc="rp_"+front[1];
				if(reportvos[fi]==null){
					oas = "";
				}else{
					if(dmo instanceof NssbReportRCellDMO){
						 oas = reportvos[fi].getAttributeValue("rp_"+as[1].replaceAll("'", ""));
					}else{
						 oas = reportvos[fi].getAttributeValue(as[1].replaceAll("'", ""));
					}
				}
				

//				Object oas = reportvos[fi].getAttributeValue(as[1].replaceAll("'", ""));
				das=NssbReportUtil.getDvalue(oas);
				itemstr=itemstr.replaceAll(code, das.toString());
			}else if (code.startsWith("[")) {// 表间值替换(<A101010!1#vmny>)
				das=dmo.getReportItemValue(code.replaceAll("\\[|\\]", ""));
				itemstr=itemstr.replaceAll(code.replaceAll("\\[|\\]",""),das.toString());
			}
		}
		
		itemstr=itemstr.replaceAll("\\[|\\]", "");
		
		Formula f= new Formula(itemstr);
		DZFDouble drs = new DZFDouble(f.getResult(),2);
		return drs;
	}


	
	
//	
//	/**
//	 * 表间取值
//	 * */
//	private DZFDouble getReportItemValue(String itemstr,String cyear,String pk_corp) {
//		DZFDouble dv= zero;
//		String[] codes =itemstr.split("!");
//		String reportcode = codes[0];
//		String itemcode = codes[1];
//		
//		String[] as = itemcode.split("#");
//		
//		String sql = " select "+as[1]+" from "+NssbReportUtil.getReportTableName(reportcode)+" where vno=? and cyear=? and pk_corp=?";
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(as[0]);
//		sp.addParam(cyear);
//		sp.addParam(pk_corp);
//		
//		Object ors =getSbo().executeQuery(sql, sp, new ColumnProcessor());
//		dv=NssbReportUtil.getDvalue(ors);
//		return dv;
//	}
}
