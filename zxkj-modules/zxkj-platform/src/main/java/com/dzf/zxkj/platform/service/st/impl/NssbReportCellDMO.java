package com.dzf.zxkj.platform.service.st.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.st.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.report.IFsYeReport;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.util.Formula;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 纳税申报
 * */
@Service("nssbrcelldmo")
@SuppressWarnings("all")
public class NssbReportCellDMO extends NssbBaseDMO{
	
//	private Logger dmolog = Logger.getLogger(NssbReportCellDMO.class);
	
	private SingleObjectBO sbo;
	
	private IFsYeReport kmreportsrv;//科目数据查询
	
	private IYntBoPubUtil pubutil;
	

	
	//构造新的报表用
	private String pk_corp;
	private String cyear;
	private String tradeType;
	private Object[] kmfs;//科目发生
	private Map<String, FseJyeVO> fseMap;//科目对照发生额，KEY=报表项目编号
	private Map<String,String> fm;
	private Map<String, DZFDouble> fmvalue;
	private Map<String, StBaseVO[]> reportmap;
//	private List<Map<String,DZFDouble>> fmvaluelist = new ArrayList<Map<String,DZFDouble>>();
	
	private CorpVO corpvo;
	
	public Map<String, StBaseVO[]> getReportmap() {
		return reportmap;
	}
	
	private SingleObjectBO getSbo() {
		if(sbo==null){
			sbo=(SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		}
		return sbo;
	}
	
	private IFsYeReport getKmreportsrv() {
		if(kmreportsrv==null){
			kmreportsrv=(IFsYeReport)SpringUtils.getBean("gl_rep_fsyebserv");
		}
		return kmreportsrv;
	}
	
	public IYntBoPubUtil getPubutil() {
		if(pubutil==null){
			pubutil=(IYntBoPubUtil)SpringUtils.getBean("yntBoPubUtil");
		}
		return pubutil;
	}

	public NssbReportCellDMO(){

		
	};//默认构造方法
	
	//是否预加载科目发生
	@SuppressWarnings("unchecked")
	public NssbReportCellDMO(String pk_corp,String  cyear) throws BusinessException {
		
		this.pk_corp=pk_corp;
		this.cyear=cyear;
		
			QueryParamVO pramavo = new QueryParamVO();
			pramavo.setBegindate1(new DZFDate(cyear+"-01-01"));
			pramavo.setEnddate(new DZFDate(cyear+"-12-31"));
			pramavo.setCjq(1);//查询科目级次
			pramavo.setCjz(6);
			pramavo.setIshasjz(new DZFBoolean(true));
			pramavo.setIshassh(new DZFBoolean(true));
			pramavo.setPk_corp(pk_corp);
			pramavo.setXswyewfs(new DZFBoolean(true));
			pramavo.setXsyljfs(new DZFBoolean(true));
			
			kmfs=getKmreportsrv().getFsJyeVOs1(pramavo);
			
			String condition = " pk_corp=? "; 
			SQLParameter params = new SQLParameter();
			params.addParam(pk_corp);
			List<CorpVO> list = (List<CorpVO>)getSbo().retrieveByClause(CorpVO.class, condition, params);
			if(list!=null&&list.size()>0){
				corpvo = list.get(0);
			}
		
			
			init();
	}
	
	//构造发生额MAP
	private void init(){
		
		if(kmfs!=null){
			FseJyeVO[] vos =(FseJyeVO[])kmfs[0];//科目发生额及
			
			if(vos!=null&&vos.length>0){
			
				fseMap = new HashMap<String,FseJyeVO>();
				for(FseJyeVO fsvo: vos){
					fseMap.put(fsvo.getKmbm(), fsvo);
				}
				
			}
		}
		
		if(getPubutil().is2007AccountSchema(pk_corp)){
			tradeType="2007";
		}else{
			tradeType="2013";
		}
		

		
	}
	
	
	public void setCyear(String cyear) {
		this.cyear = cyear;
	}
	
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	
	

	/**
	 * 查询单张报表
	 * */
	public StBaseVO[] queryDefaultReport(String pk_corp,String  cyear,Class btype){
		String whql = new String(" pk_corp=? and cyear=? order by vno+0");
		SQLParameter sp= new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(cyear);
		StBaseVO[] rsvos =(StBaseVO[])getSbo().queryByCondition(btype, whql, sp);
		if(rsvos!=null&&rsvos.length>0){
			return rsvos;
		}
		return null;
	}
	
	
	

	public Map<String, StBaseVO[]> genMainReprot() throws BusinessException{

		reportmap= new HashMap<String,StBaseVO[]>();
		StNssbMainVO[] mainvos= (StNssbMainVO[])genBaseReprot(NssbReportUtil.nssb, NssbReportUtil.getReportBeanType(NssbReportUtil.nssb),reportmap);
		
//		for(int i=0;i<13;i++){
//			mainvos[i].setCitemclass("利润总额计算");
//		}
//		for(int i=13;i<23;i++){
//			mainvos[i].setCitemclass("应纳税所得额计算");
//		}
//		for(int i=23;i<36;i++){
//			mainvos[i].setCitemclass("应纳税额计算");
//		}
//		for(int i=36;i<38;i++){
//			mainvos[i].setCitemclass("附列资料");
//		}
		
		reportmap.put(NssbReportUtil.nssb, mainvos);
		return reportmap;
		
	}
	
	
	/**
	 * 构造一般企业收入报表，并持久化
	 * */
	public StBaseVO[] genBaseReprot(String reportcode,Class beantype,Map<String, StBaseVO[]> reportmap)throws BusinessException{
		StBaseVO[] vos =genBaicReprotValue(reportcode,beantype,reportmap);
//		StYbqysrVO[] vos =volist.toArray(new StYbqysrVO[volist.size()]);
		
		DelteHis(reportcode);
		
		String[] pks=getSbo().insertVOArr(pk_corp, vos);
		int count=pks.length;
		for(int i=0;i<count;i++){
			vos[i].setPrimaryKey(pks[i]);
		}
		return vos;
	}
	
	
	/**
	 * 构造基础报表
	 * @throws  
	 * @throws Exception 
	 */
	
	private StBaseVO[] genBaicReprotValue(String reportcode,Class beantype,Map<String, StBaseVO[]> reportmap) throws BusinessException{
		NssbReportUtil util = new NssbReportUtil(this);
		String[] items = NssbContrastPrjToAcc.getInstanct().getReportItem(reportcode);//获取报表行名
		String[][] reportconst = NssbContrastPrjToAcc.getInstanct().getReportConst(reportcode,tradeType);//获取报表项目会计科目对照
		String[] sort= NssbContrastPrjToAcc.getInstanct().getReportConst(reportcode,tradeType)[0];//获取报表生成顺序数组
		String[] columns = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
		String[] stringvno =  NssbContrastPrjToAcc.getInstanct().getReportVno(reportcode);
		int fx=NssbContrastPrjToAcc.getInstanct().getReportKmqsfx(reportcode);
		
		int rowcount = items.length;
		
		//值列
		int colcount=reportconst.length-1;
		StBaseVO[] reportvos = (StBaseVO[]) Array.newInstance(beantype, rowcount);
		reportmap.put(reportcode, reportvos);
//				new StBaseVO[rowcount];
		for(int i=0;i<rowcount;i++){
			int s;
			try {
				s=Integer.valueOf(sort[i])-1;//从sort数组取数
				reportvos[s]=(StBaseVO)beantype.newInstance();
			} catch (Exception e) {
				throw new WiseRunException(e);
			} 
			reportvos[s].setVno(Integer.toString(s+1));
			if(stringvno!=null){
				reportvos[s].setVno(stringvno[s]);
			}
			reportvos[s].setVprojectname(items[s]);
			reportvos[s].setPk_corp(pk_corp);
			reportvos[s].setCyear(cyear);
			
//			String kmcode =kmcodes[i];
			
			for(int j=0;j<colcount;j++){//金额列计算赋值
				String kmcode =reportconst[j+1][s];
/*				if("=([A104000!4#vxsfy]+[A104000!4#vglfy])".equals(kmcode)){
					//System.out.println(kmcode);
				}*/
				if(kmcode.startsWith("=")){
					if(kmcode.startsWith("=(")||kmcode.startsWith("='")||kmcode.startsWith("=[")){//表内公式
						DZFDouble dvalue = calculateCellFu(reportvos, kmcode.replace("=", ""));
						if("A107040".equals(reportcode)&&0==j){//减免所得税优惠明细表
							dvalue = getSpecialValue(dvalue,s,j);
						}
						reportvos[s].setAttributeValue(columns[j], dvalue);
					}else if(kmcode.startsWith("=IF")){//条件判断公式
						DZFDouble dvalue = util.CalCulateMutiBl(kmcode.replace("=", ""), reportvos);
						reportvos[s].setAttributeValue(columns[j], dvalue);
					}else {//直接数字赋值
						if(columns[j].startsWith("c")){
							reportvos[s].setAttributeValue(columns[j],kmcode.replace("=", ""));
						}else{
							reportvos[s].setAttributeValue(columns[j], new DZFDouble(kmcode.replace("=", "")));
						}
					}
//					reportvos[i].setAttributeValue(columns[j], new DZFDouble(kmcode.replace("=", "")));
				}else if("NA".equals(kmcode)||NssbContrastPrjToAcc.STA.equals(kmcode)||NssbContrastPrjToAcc.EDIT.equals(kmcode)||NssbContrastPrjToAcc.FM.equals(kmcode)||fseMap==null){
					if(columns[j].startsWith("c")){
						reportvos[s].setAttributeValue(columns[j], kmcode+"_");
					}else{
						reportvos[s].setAttributeValue(columns[j], DZFDouble.ZERO_DBL);
					}
//					reportvos[i].setAttributeValue(columns[j], DZFDouble.ZERO_DBL);
//					ybsrvos[i].setVmny(DZFDouble.ZERO_DBL);
				}else if(kmcode.startsWith("FM(")){//使用公式计算Cell值
					continue;
				}else{
					
					String[] codes = kmcode.split("\\+|-|\\*|/|\\(|\\)");
					DZFDouble dvalue =DZFDouble.ZERO_DBL;
					if(codes.length>1){
						dvalue = calculateCellFu(kmcode, fx);
					}else{
						
						String[] fields = kmcode.split("_");
						if(fields.length>1){
							if(isAccountCode(fields[0])){
								fields[0] = getCurrentCode(pk_corp,fields[0]);
							}

							FseJyeVO kmfsvo = fseMap.get(fields[0]);
							if(kmfsvo!=null){
								if("DF".equals(fields[1])){//贷方
									dvalue=NssbReportUtil.getDvalue(kmfsvo.getDftotal());
		//									(kmfsvo==null?zero:(kmfsvo.getJftotal()==null?zero:kmfsvo.getJftotal()));//借方本年发生额
								}else if("JF".equals(fields[1])){//借方
									dvalue=NssbReportUtil.getDvalue(kmfsvo.getJftotal());
		//									(kmfsvo==null?zero:(kmfsvo.getJftotal()==null?zero:kmfsvo.getJftotal()));//借方本年发生额
								}else if("NMDF".equals(fields[1])){//年末贷方
									dvalue=NssbReportUtil.getDvalue(kmfsvo.getQmdf());
		//									(kmfsvo==null?zero:(kmfsvo.getDftotal()==null?zero:kmfsvo.getDftotal()));//借方本年发生额
								}else if("NMJF".equals(fields[1])){//年末借方
									dvalue=NssbReportUtil.getDvalue(kmfsvo.getQmjf());
								}
							}
						
						}else{
							if(isAccountCode(kmcode)){
								kmcode = getCurrentCode(pk_corp,kmcode);
							}							
							FseJyeVO kmfsvo = fseMap.get(kmcode);
							if(kmfsvo!=null){
								if(fx==NssbContrastPrjToAcc.JF){
									dvalue=NssbReportUtil.getDvalue(kmfsvo.getJftotal());
		//									(kmfsvo==null?zero:(kmfsvo.getJftotal()==null?zero:kmfsvo.getJftotal()));//借方本年发生额
								}else{
									dvalue=NssbReportUtil.getDvalue(kmfsvo.getDftotal());
		//									(kmfsvo==null?zero:(kmfsvo.getDftotal()==null?zero:kmfsvo.getDftotal()));//借方本年发生额
								}
							}
						}

					}
					reportvos[s].setAttributeValue(columns[j], dvalue.setScale(2, DZFDouble.ROUND_UP));
				}
			}
			

		}
		
		
//		String[] colform =NssbReportFormula.getInstanct().getCalculateFromula(reportcode);//获取列计算公式
		NssbCellFmCal cellcal= new NssbCellFmCal();
		reportvos=cellcal.calculateCell(reportcode, reportvos);
		
		reportvos=calculateColFm(reportcode, reportvos);
		reportvos=calculateRowFm(reportcode, reportvos);
		
//		String[] formuls =NssbReportFormula.getInstanct().getCalculateFromula(reportcode);//获取报表统计公式
//		int count = formuls.length;//公式的个数
//		fm = new HashMap<String,String>();//公式MAP
//		fmvalue = new HashMap<String,DZFDouble>();//公式计算值MAP
//		String[] fmnos = new String[count];//公式MAP-KEY
//		for(int i=0;i<count;i++){
//			String[] ff =formuls[i].split("=");
//			fmnos[i]=ff[0];
//			fm.put(ff[0], ff[1]);
//		}
//		
//		
//		for(int i=0;i<colcount;i++){//金额列计算赋值
//			String[] kmcodes = reportconst[i+1];
//			fmvalue = new HashMap<String,DZFDouble>();
//			for(int j=0;j<count;j++){
//				String key =fmnos[j];
//				String fu =fm.get(key);
//				DZFDouble dvalue = calculateFu(fu, kmcodes, fx);
//				
//				int index=Integer.parseInt(key.replaceAll("'", ""));
//				reportvos[index-1].setAttributeValue(columns[i], dvalue.setScale(2, DZFDouble.ROUND_UP));
//				
//			}
//		}
		
		return reportvos;
	
	}
	
	
	
	/****
	 *  减免所得税优惠明细表  特殊处理
	 * @param value  中间值
	 * @param row  行
	 * @param column  列
	 * @return
	 */
	public DZFDouble getSpecialValue(DZFDouble value,int row,int column){
		
		DZFDouble res = DZFDouble.ZERO_DBL;
		
		if(corpvo==null||corpvo.isIssmall()==null||!corpvo.isIssmall().booleanValue()|| StringUtil.isEmpty(corpvo.getEstablishtime())){
			return DZFDouble.ZERO_DBL;
		}
		
		//小于0大于30万
		if(value.doubleValue()<=0||value.sub(new DZFDouble(300000)).doubleValue()>0){
			return DZFDouble.ZERO_DBL;
		}else if(0<value.doubleValue()&&value.sub(new DZFDouble(200000)).doubleValue()<=0){//0< <=20
			res = value.multiply(new DZFDouble(0.15));
		}else{//20万< <=30万

			Map<String,Double> map = new HashMap<String,Double>();
			if(0==row){//优惠
				map = getPremiumRate();
			}else if(1==row){// 减半
				map = getSubHalf();
			}
			String time = corpvo.getEstablishtime();
			res = new DZFDouble(map.get(time)).multiply(value);			
		}
		
		return res;
	}
	
	
	/****
	 * 优惠率
	 * @return
	 */
	private Map<String,Double> getPremiumRate(){
		
		Map<String,Double> map = new HashMap<String,Double>();
		map.put("201501",0.075d);
		map.put("201502",0.0773d);
		map.put("201503",0.08d);
		map.put("201504",0.0833d);
		map.put("201505",0.0875d);
		map.put("201506",0.0929d);
		map.put("201507",0.1d);
		map.put("201508",0.11d);
		map.put("201509",0.125d);
		map.put("201510",0.15d);
		map.put("201511",0.15d);
		map.put("201512",0.15d);
		
       return map;
		
	}
	
	
	/****
	 * 减半
	 * @return
	 */
	private Map<String,Double> getSubHalf(){
		
		Map<String,Double> map = new HashMap<String,Double>();
		map.put("201501",0.0375d);
		map.put("201502",0.0409d);
		map.put("201503",0.045d);
		map.put("201504",0.05d);
		map.put("201505",0.0563d);
		map.put("201506",0.0643d);
		map.put("201507",0.0750d);
		map.put("201508",0.09d);
		map.put("201509",0.1125d);
		map.put("201510",0.15d);
		map.put("201511",0.15d);
		map.put("201512",0.15d);
				
       return map;		
	}
	
	
	
	/***
	 * 是否为科目编码
	 * @param code
	 * @return
	 */
	public boolean isAccountCode(String code){
		
		boolean is = false;
		
		if(!StringUtil.isEmpty(code)&&code.trim().length()>=0){
			try {
				if(Long.parseLong(code)>=1000){
					is = true;
				}
			} catch (Exception e) {
				//异常说明不是科目类型
			}

		}
		
		return is;
	}
	
	
	private StBaseVO[] calculateColFm(String reportcode, StBaseVO[] reportvos){//列统计公式计算

		String[] formuls = NssbReportFormula.getInstanct().getCalculateColFromula(reportcode);//获取报表统计公式
		if(formuls==null||formuls.length<=0){
			return reportvos;
		}
		
		String[][] reportconst = NssbContrastPrjToAcc.getInstanct().getReportConst(reportcode,tradeType);//获取报表项目会计科目对照
		String[] columns = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
		
		int rowcount=reportvos.length;
		int count = formuls.length;//公式的个数
		fm = new HashMap<String,String>();//公式MAP
		fmvalue = new HashMap<String,DZFDouble>();//公式计算值MAP
		String[] fmnos = new String[count];//公式MAP-KEY
		for(int i=0;i<count;i++){
			String[] ff =formuls[i].split("=");
			fmnos[i]=ff[0];
			fm.put(ff[0], ff[1]);
		}
		
		
		for(int i=0;i<rowcount;i++){//金额列计算赋值
			fmvalue = new HashMap<String,DZFDouble>();
			for(int j=0;j<count;j++){
				String key =fmnos[j];
				String fu =fm.get(key);
				int index=Integer.parseInt(key.replaceAll("'", ""));
				
				String[] kmcodes = reportconst[index];
				if(kmcodes[i].equals("STA"))
					continue;
				
				DZFDouble dvalue = calculateFu(fu, reportvos[i],columns);
				String code =columns[index-1];
				reportvos[i].setAttributeValue(code, dvalue.setScale(2, DZFDouble.ROUND_UP));
				
			}
		}
		
		return reportvos;
	}
	
	private DZFDouble calculateFu(String fu, StBaseVO reportvo,String[] beanitems) {
		
		String[] codes=fu.split("\\+|-|\\*|/|\\(|\\)");
		int len=codes.length;
		for(int i=0;i<len;i++){
			if(codes[i].length()==0&&!codes[i].startsWith("'"))//判断是否为替换变量
				continue;
			DZFDouble dvalue=DZFDouble.ZERO_DBL;
			if(fm.get(codes[i])!=null){//是否也为公式统计变量
				if(fmvalue.get(codes[i])!=null){
					dvalue=fmvalue.get(codes[i]);
				}else{
					dvalue =calculateFu(fm.get(codes[i]),reportvo,beanitems);
					fmvalue.put(codes[i], dvalue);
				}
			}else{
				String code =beanitems[Integer.parseInt(codes[i].replaceAll("'", ""))-1];
				dvalue=NssbReportUtil.getDvalue(reportvo.getAttributeValue(code));
			}
			fu=fu.replaceAll(codes[i], dvalue.toString());
		}
		
		Formula fmcal = new Formula(fu);//计算
		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
		
		return drs;
		
	}

	private StBaseVO[] calculateRowFm(String reportcode, StBaseVO[] reportvos){//行统计公式计算
		

		String[] formuls =NssbReportFormula.getInstanct().getCalculateFromula(reportcode);//获取报表统计公式
		if(formuls==null||formuls.length==0){
			return reportvos;
		}
		
		String[][] reportconst = NssbContrastPrjToAcc.getInstanct().getReportConst(reportcode,tradeType);//获取报表项目会计科目对照
		String[] columns = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
	//	int fx=NssbContrastPrjToAcc.getInstanct().getReportKmqsfx(reportcode);
		
		int colcount=columns.length;
		int count = formuls.length;//公式的个数
		fm = new HashMap<String,String>();//公式MAP
		fmvalue = new HashMap<String,DZFDouble>();//公式计算值MAP
		String[] fmnos = new String[count];//公式MAP-KEY
		for(int i=0;i<count;i++){
			String[] ff =formuls[i].split("=");
			fmnos[i]=ff[0];
			fm.put(ff[0], ff[1]);
		}
		
		
		for(int i=0;i<colcount;i++){//金额列计算赋值
			String[] kmcodes = reportconst[i+1];
			fmvalue = new HashMap<String,DZFDouble>();
			String item =columns[i];
			if(item.startsWith("c")){//非金额计算列
				continue;
			}
			for(int j=0;j<count;j++){
				String key =fmnos[j];
				String fu =fm.get(key);
				
				int index=Integer.parseInt(key.replaceAll("'", ""));
				if(kmcodes[index-1].equals("STA"))
					continue;
				
				DZFDouble dvalue = calculateFu(fu,item,reportvos);
				reportvos[index-1].setAttributeValue(item, dvalue.setScale(2, DZFDouble.ROUND_UP));
				
			}
		}
		
		return reportvos;
	}
	
	
	/**
	 * 
	 * 计算公式的值
	 * */
	private DZFDouble calculateFu(String formula,String item,StBaseVO[] reportvos){
		
		DZFDouble drs = null;
		try {
			String[] codes=formula.split("\\+|-|\\*|/|\\(|\\)");
			int len=codes.length;
			for(int i=0;i<len;i++){
				if(codes[i].length()==0&&!codes[i].startsWith("'"))//判断是否为替换变量
					continue;
				DZFDouble dvalue=DZFDouble.ZERO_DBL;
				if(fm.get(codes[i])!=null){//是否也为公式统计变量
					if(fmvalue.get(codes[i])!=null){
						dvalue=fmvalue.get(codes[i]);
					}else{
						dvalue =calculateFu(fm.get(codes[i]),item,reportvos);
						fmvalue.put(codes[i], dvalue);
					}
				}else{//直接从科目对照内获取
					int index=Integer.parseInt(codes[i].replaceAll("'", ""));
					dvalue=NssbReportUtil.getDvalue(reportvos[index-1].getAttributeValue(item));
				}
				formula=formula.replaceAll(codes[i], dvalue.toString());
			}
			
			Formula fmcal = new Formula(formula);//计算
			drs = new DZFDouble(fmcal.getResult(),2);
		} catch (Exception e) {
//			e.printStackTrace();
		}

		
		return drs;
		
	}
	
	public static void main(String[] args) {
		
//		String s = "(0.00000000+-65370097.00)";
		//s = "1+2+-5";
//		Formula fmcal = new Formula(s);//计算
		//System.out.println(fmcal.getResult());
		
		
	}
	
	
	
	/**
	 * 
	 * 计算Cell 科目公式
	 * */
	private DZFDouble calculateCellFu(String formula,int fx){
		
		String[] codes=formula.split("\\+|-|\\*|/|\\(|\\)");
		int len=codes.length;
		for (int i = 0; i < len; i++) {
			// if(codes[i].length()==0&&!codes[i].startsWith("'"))//判断是否为替换变量
			// continue;
			DZFDouble dvalue = DZFDouble.ZERO_DBL;
			String oldcode = codes[i];
			if(isAccountCode(codes[i])){
				codes[i] = getCurrentCode(pk_corp,codes[i]);
			}
		
			FseJyeVO fsvo = fseMap.get(codes[i]);

			if(fsvo!=null){
				if (fx == NssbContrastPrjToAcc.JF) {
					dvalue = NssbReportUtil.getDvalue(fsvo.getJftotal());
				} else {
					dvalue = NssbReportUtil.getDvalue(fsvo.getDftotal());
				}
			}
			formula = formula.replaceFirst(oldcode, dvalue.toString());
//					formula.replaceAll(codes[i], dvalue.toString());
		}
		
		Formula fmcal = new Formula(formula);//计算
		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
		
		return drs;
		
	}
	
	
	/**
	 * 
	 * 根据公式计算单元值
	 * */
	public DZFDouble calculateCellFu(StBaseVO[] reportvos,String formula){
		
		//NssbReportUtil util = new NssbReportUtil(this);
		
		String[] codes=formula.split("\\+|-|\\*|/|\\(|\\)");
		int len=codes.length;
		for (int i = 0; i < len; i++) {
			 if(codes[i].length()==0)
			 continue;
			 String code =codes[i];
			 DZFDouble dvalue = DZFDouble.ZERO_DBL;
			if (codes[i].startsWith("'")) {// 表内值替换('4#vmny'-'5#vmny')

				String[] as = code.split("#");
				int fi = Integer.parseInt(as[0].replaceAll("'", "")) - 1;
				Object oas = reportvos[fi].getAttributeValue(as[1].replaceAll(
						"'", ""));
				dvalue = NssbReportUtil.getDvalue(oas);
				formula = formula.replaceAll(codes[i], dvalue.toString());
				
			} else if (codes[i].startsWith("[")) {// 表间值替换(<A101010!1#vmny>)
				dvalue = getReportItemValue(codes[i].replaceAll("\\[|\\]", ""));
				formula = formula.replaceAll(codes[i].replaceAll("\\[|\\]", ""), dvalue.toString());
			}
			 
//			DZFDouble dvalue = zero;
//			formula = formula.replaceAll(codes[i], dvalue.toString());
		}
		formula=formula.replaceAll("\\[|\\]", "");
		Formula fmcal = new Formula(formula);//计算
		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
		
		return drs;
		
	}
	
	/**
	 * 表间取值
	 * */
	public DZFDouble getReportItemValue(String itemstr) {
		DZFDouble dv= DZFDouble.ZERO_DBL;
		String[] codes =itemstr.split("!");
		String reportcode = codes[0];
		String itemcode = codes[1];
		
		String[] as = itemcode.split("#");
		
//		String tablename =NssbReportUtil.getReportTableName(reportcode);
//		if(tablename==null){
//			throw new BusinessException(reportcode+"数据库表没有注册");
//		}
//		
//		String sql = " select "+as[1]+" from "+NssbReportUtil.getReportTableName(reportcode)+" where vno=? and cyear=? and pk_corp=?";
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(as[0]);
//		sp.addParam(cyear);
//		sp.addParam(pk_corp);
//		
//		Object ors =getSbo().executeQuery(sql, sp, new ColumnProcessor());
		
		
		StBaseVO[] vos = reportmap.get(reportcode);
		if(vos==null||vos.length==0){
			vos=genBaseReprot(reportcode, NssbReportUtil.getReportBeanType(reportcode), reportmap);
			reportmap.put(reportcode, vos);
		}else{
			dv=NssbReportUtil.getDvalue(as[0]);
		}
		int index = Integer.parseInt(as[0])-1;
		Object ors =vos[index].getAttributeValue(as[1]);
		dv=NssbReportUtil.getDvalue(ors);
		return dv;
	}

	/**
	 * 设置显示值
	 * */
	public<T> T setReportValue(String reportcode,StBaseVO[] reportvos){
		if(reportvos==null||reportvos.length==0)
			return null;
		String[][] reportconst = NssbContrastPrjToAcc.getInstanct().getReportConst(reportcode,tradeType);//获取报表项目会计科目对照
		String[] columns = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
		int colcount = columns.length;
		int vocount = reportvos.length;
		for(int i=0;i<vocount;i++){
			for(int j=0;j<colcount;j++){
				String kmcode =reportconst[j+1][i];
//				if("NA".equals(kmcode)||NssbContrastPrjToAcc.STA.equals(kmcode)||NssbContrastPrjToAcc.EDIT.equals(kmcode)||NssbContrastPrjToAcc.FM.equals(kmcode)||fseMap==null){
				if("NA".equals(kmcode)||NssbContrastPrjToAcc.STA.equals(kmcode)||NssbContrastPrjToAcc.EDIT.equals(kmcode)||NssbContrastPrjToAcc.FM.equals(kmcode)||fseMap==null){
					if(NssbContrastPrjToAcc.STA.equals(kmcode)){
						reportvos[i].setAttributeValue("rp_"+columns[j], kmcode+"_");
					}else{
						reportvos[i].setAttributeValue("rp_"+columns[j], reportvos[i].getAttributeValue(columns[j]));
					}
//					if(reportvos[i].getAttributeValue(columns[j])==null){
//						reportvos[i].setAttributeValue("rp_"+columns[j], kmcode+"_");
//					}else{
//						reportvos[i].setAttributeValue("rp_"+columns[j], reportvos[i].getAttributeValue(columns[j]));
//					}
				}else if(kmcode.startsWith("FM(")){//使用公式计算Cell值
					
				}else{
					reportvos[i].setAttributeValue("rp_"+columns[j], reportvos[i].getAttributeValue(columns[j]));
				}
			}
		}
		return (T)reportvos;
	}
	
	
	private void DelteHis(String reportcode){
//		String whq = new String(" pk_corp=? and cyear=?");
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_corp);
//		sp.addParam(cyear);
//		
//		List<StBaseVO> rslist =(List<StBaseVO>)getSbo().executeQuery(whq, sp, new Class[]{clazz});
//		if(rslist!=null&&rslist.size()>0){
//			getSbo().deleteVOArray(rslist.toArray(new StBaseVO[rslist.size()]));
//		}
		
		String sql = new String(" delete from "+NssbReportUtil.getReportTableName(reportcode)+" where pk_corp=? and cyear=?");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(cyear);
		
		int ncount =getSbo().executeUpdate(sql, sp);
	}
	
	
	
//	//重新计算
//	public <T> T reCalculate(String reportcode,StBaseVO[] reportvos){
//		
//		String[] vitems = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
//		reportvos = calculateSumRpColFm(reportcode,reportvos);
//		reportvos = calculateSumRpFm(reportcode,vitems,reportvos);
//		return (T)reportvos;
//		
//	}
//	
//	private StBaseVO[] calculateSumRpColFm(String reportcode, StBaseVO[] reportvos){//列统计公式计算
//
//		String[] formuls =NssbReportFormula.getInstanct().getCalculateColFromula(reportcode);//获取报表统计公式
//		if(formuls==null||formuls.length<=0){
//			return reportvos;
//		}
//		
//		String[][] reportconst = NssbContrastPrjToAcc.getInstanct().getReportConst(reportcode,tradeType);//获取报表项目会计科目对照
//		String[] columns = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
//		
//		for(String col:columns){
//			col="rp_"+col;
//		}
//		
//		int rowcount=reportvos.length;
//		int count = formuls.length;//公式的个数
//		fm = new HashMap<String,String>();//公式MAP
//		fmvalue = new HashMap<String,DZFDouble>();//公式计算值MAP
//		String[] fmnos = new String[count];//公式MAP-KEY
//		for(int i=0;i<count;i++){
//			String[] ff =formuls[i].split("=");
//			fmnos[i]=ff[0];
//			fm.put(ff[0], ff[1]);
//		}
//		
//		
//		for(int i=0;i<rowcount;i++){//金额列计算赋值
//			fmvalue = new HashMap<String,DZFDouble>();
//			for(int j=0;j<count;j++){
//				String key =fmnos[j];
//				String fu =fm.get(key);
//				int index=Integer.parseInt(key.replaceAll("'", ""));
//				
//				String[] kmcodes = reportconst[index];
//				if(kmcodes[i].equals("STA"))
//					continue;
//				
//				DZFDouble dvalue = calculateFu(fu, reportvos[i],columns);
//				String code =columns[index-1];
//				reportvos[i].setAttributeValue(code, dvalue.setScale(2, DZFDouble.ROUND_UP));
//				
//			}
//		}
//		
//		return reportvos;
//	}
//	
//	/**
//	 * 编辑后报表统计公式计算
//	 * */
//	private StBaseVO[] calculateSumRpFm(String reportcode,String[] vitems,StBaseVO[] reportvos){
//		String[] formuls =NssbReportFormula.getInstanct().getCalculateFromula(reportcode);//获取报表统计公式
//		if(formuls==null||formuls.length==0)
//			return reportvos;
//		int count = formuls.length;//公式的个数
//		fm = new HashMap<String,String>();//公式MAP
//		fmvalue = new HashMap<String,DZFDouble>();//公式计算值MAP
//		String[] fmnos = new String[count];//公式MAP-KEY
//		for(int i=0;i<count;i++){
//			String[] ff =formuls[i].split("=");
//			fmnos[i]=ff[0];
//			fm.put(ff[0], ff[1]);
//		}
//		
//		int colcount=vitems.length;
//		for(int i=0;i<colcount;i++){//金额列计算赋值
////			String[] kmcodes = reportconst[i+1];
//			fmvalue = new HashMap<String,DZFDouble>();
//			String item = "rp_"+vitems[i];
//			for(int j=0;j<count;j++){
//				String key =fmnos[j];
//				String fu =fm.get(key);
//				int index=Integer.parseInt(key.replaceAll("'", ""));
//				
//				Object o = reportvos[index-1].getAttributeValue(item);
//				if(o!=null&&o.toString().equalsIgnoreCase("STA_")){
//					continue;
//				}
//
//				DZFDouble dvalue = calculateFm(fu, reportvos,item);
//				reportvos[index-1].setAttributeValue(item, dvalue.setScale(2, DZFDouble.ROUND_UP));
//				
//			}
//		}
//		return reportvos;
//	}
//	
//	/**
//	 * 编辑后报表计算公式值
//	 * */
//	private DZFDouble calculateFm(String formula,StBaseVO[] reportvos,String item){
//		
//		String[] codes=formula.split("\\+|-|\\*|/|\\(|\\)");
//		int len=codes.length;
//		for(int i=0;i<len;i++){
//			if(codes[i].length()==0&&!codes[i].startsWith("'"))//判断是否为替换变量
//				continue;
//			DZFDouble dvalue=zero;
//			if(fm.get(codes[i])!=null){//是否也为公式统计变量
//				if(fmvalue.get(codes[i])!=null){
//					dvalue=fmvalue.get(codes[i]);
//				}else{
//					dvalue =calculateFm(fm.get(codes[i]),reportvos,item);
//					fmvalue.put(codes[i], dvalue);
//				}
//			}else{//获取变量值
//				int vindex=Integer.parseInt(codes[i].replaceAll("'", ""))-1;
//				Object v=reportvos[vindex].getAttributeValue(item);
//				if(v!=null){
//					String cv=v.toString();
//					//TODO 需要优化
//					if(cv.length()>0&&
//							!cv.equalsIgnoreCase(NssbContrastPrjToAcc.STA+"_")&&
//							!cv.equalsIgnoreCase(NssbContrastPrjToAcc.EDIT+"_")&&
//							!cv.equalsIgnoreCase(NssbContrastPrjToAcc.FM+"_")&&
//							!cv.equalsIgnoreCase("NA"+"_")){
//						dvalue=new DZFDouble(v.toString(),2);
//					}
//				}
//			}
//			formula=formula.replaceAll(codes[i], dvalue.toString());
//		}
//		
//		Formula fmcal = new Formula(formula);//计算
//		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
//		
//		return drs;
//		
//	}
	
	/**
	 * 单表持久化
	 * */
	public void updateReportVO(String reportcode,StBaseVO[] reportvos){
		
		reportvos=setItemValueFromRpValue(reportcode, reportvos);
		String[] items=null;
		if(reportcode.equalsIgnoreCase(NssbReportUtil.jzzcnstz)){
			items=new String[]{
					"vprojectname","vgyzzje","vgykcxe","vgyssje","vgynstzje","vfgyzzje","vnstzje"
			};
		}else if(reportcode.equalsIgnoreCase(NssbReportUtil.jmsds)){
			items = new String[]{"vmny","vprojectname"};
		}else{
			items=NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);
		}
		int count=getSbo().updateAry(reportvos,items);
	
		
	}
	
	
	private StBaseVO[] setItemValueFromRpValue(String reportcode,StBaseVO[] reportvos){//报表金额更新到数据库字段

		if(reportvos==null||reportvos.length==0)
			return null;
		String[] columns = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
		int colcount = columns.length;
		int vocount = reportvos.length;
		for(int i=0;i<vocount;i++){
			for(int j=0;j<colcount;j++){
				Object ov = reportvos[i].getAttributeValue("rp_"+columns[j]);
				if(columns[j].startsWith("c")){
					if(ov!=null&&ov.toString().length()>0){
						reportvos[i].setAttributeValue(columns[j],ov.toString());
					}
				}else{
					DZFDouble dvalue =NssbReportUtil.getDvalue(ov);
					reportvos[i].setAttributeValue(columns[j],dvalue);
				}
//				if(ov==null||ov.toString().length()==0)
//					continue;
//				String sv=ov.toString().replaceAll("NA|STA|EDIT|_", "");
//				if(sv.length()==0)
//					continue;
			}
		}
		return reportvos;
	
	}
	
	//保存纳税申报表总信息
	public StNssbInfoVO saveNssbInfo(StNssbInfoVO maininfo){
		
		StNssbInfoVO oldvo=getNssbInfo(maininfo);
		if(oldvo!=null){
			getSbo().deleteObject(oldvo);
		}
		maininfo =(StNssbInfoVO)getSbo().saveObject(maininfo.getPk_corp(), maininfo);
		return maininfo;
	}
	
	//更新纳税申报信息
	public void updateNssbInfo(StNssbInfoVO maininfo){
		getSbo().update(maininfo,new String[]{"cstatus","approvepsnid","approvetime"});
	}
	
	
	public String getStatus(StNssbInfoVO infovo){
		String sql = new String(" select nvl(cstatus,'0') from ynt_st_nssbinfo where pk_corp=? and  cyear=? and type=?");
		SQLParameter sp = new SQLParameter();
		sp.addParam(infovo.getPk_corp());
		sp.addParam(infovo.getCyear());
		sp.addParam(infovo.getType());
		
		String status =(String)getSbo().executeQuery(sql, sp, new ColumnProcessor());
		
		if(status==null){
			return NssbReportUtil.unapproved;
		}
		return status;
	}
	
	public StNssbInfoVO getNssbInfo(StNssbInfoVO infovo){
		String whq = new String(" pk_corp=? and cyear=? and type=?");
		SQLParameter sp = new SQLParameter();
		sp.addParam(infovo.getPk_corp());
		sp.addParam(infovo.getCyear());
		sp.addParam(infovo.getType());
		
		StNssbInfoVO[] infvo=(StNssbInfoVO[])getSbo().queryByCondition(StNssbInfoVO.class, whq, sp);
		
		if(infvo!=null&&infvo.length>0){
			return infvo[0];
		}
		return null;
	}
	
	
	public StNssbInfoVO getjdNssbInfo(StNssbInfoVO infovo){
		String whq = new String(" pk_corp=? and cyear=? and period=? and type=?");
		SQLParameter sp = new SQLParameter();
		sp.addParam(infovo.getPk_corp());
		sp.addParam(infovo.getCyear());
		sp.addParam(infovo.getPeriod());
		sp.addParam(infovo.getType());
		
		StNssbInfoVO[] infvo=(StNssbInfoVO[])getSbo().queryByCondition(StNssbInfoVO.class, whq, sp);
		
		if(infvo!=null&&infvo.length>0){
			return infvo[0];
		}
		return null;
	}
}
