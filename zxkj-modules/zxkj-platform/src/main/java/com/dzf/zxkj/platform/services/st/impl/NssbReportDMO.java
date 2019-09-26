package com.dzf.zxkj.platform.services.st.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.st.*;
import com.dzf.zxkj.platform.services.report.IFsYeReport;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.util.Formula;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 纳税申报
 * */
@Slf4j
public class NssbReportDMO {
	
	private SingleObjectBO sbo;
	
	private IFsYeReport kmreportsrv;//科目数据查询
	
	private IYntBoPubUtil pubutil;
	

	//构造新的报表用
	private String pk_corp;
	private String cyear;
	private String tradeType;
	private Object[] kmfs;//科目发生
	private Map<String, FseJyeVO> fseMap;//科目对照发生额，KEY=报表项目编号
	//private DZFDouble zero = new DZFDouble(0.0D,2);
	private Map<String,String> fm;
	private Map<String, DZFDouble> fmvalue;
//	private List<Map<String,DZFDouble>> fmvaluelist = new ArrayList<Map<String,DZFDouble>>();
	
	
	
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

	public NssbReportDMO(){};//默认构造方法
	
	//是否预加载科目发生
	public NssbReportDMO(String pk_corp,String  cyear) throws BusinessException {
		
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
	
	
	

	/**
	 * 查询单张报表
	 * */
	public StBaseVO[] queryDefaultReport(String pk_corp, String  cyear, Class btype){
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
	
	
	
	/**
	 * 构造一般企业收入报表，并持久化
	 * */
	public StBaseVO[] genBaseReprot(String reportcode,Class beantype)throws BusinessException{
		StBaseVO[] vos =genBaicReprotValue(reportcode,beantype);
//		StYbqysrVO[] vos =volist.toArray(new StYbqysrVO[volist.size()]);
		
		DelteHis(beantype);
		
		String[] pks=getSbo().insertVOArr(pk_corp, vos);
		int count=pks.length;
		for(int i=0;i<count;i++){
			vos[i].setPrimaryKey(pks[i]);
		}
		return vos;
	}
	
	
//	/**
//	 * 构造一般企业收入报表，并持久化
//	 * */
//	public StYbqysrVO[] genYbqysrReprot()throws BusinessException{
//		List<StYbqysrVO> volist =genBaicReprotValue("A101010",StYbqysrVO.class);
//		StYbqysrVO[] vos =volist.toArray(new StYbqysrVO[volist.size()]);
//		
//		DelteHis(StYbqysrVO.class);
//		
//		String[] pks=getSbo().insertVOArr(pk_corp, vos);
//		int count=pks.length;
//		for(int i=0;i<count;i++){
//			vos[i].setPrimaryKey(pks[i]);
//		}
//		return vos;
//	}
//	
//	
//	/**
//	 * 构造一般企业成本报表，并持久化
//	 * */
//	public StYbqycbVO[] genYbqycbReprot()throws BusinessException{
//		List<StYbqycbVO> volist =genBaicReprotValue("A102010",StYbqycbVO.class);
//		StYbqycbVO[] vos =volist.toArray(new StYbqycbVO[volist.size()]);
//		
//
//		DelteHis(StYbqycbVO.class);
//		
//		String[] pks=getSbo().insertVOArr(pk_corp, vos);
//		int count=pks.length;
//		for(int i=0;i<count;i++){
//			vos[i].setPrimaryKey(pks[i]);
//		}
//		return vos;
//	}
//	
//	
//	/**
//	 * 构造企业期间费用明细表
//	 * */
//	public StqjfyVO[] genQyjffymxReprot()throws BusinessException{
//		List<StqjfyVO> volist =genBaicReprotValue("A104000",StqjfyVO.class);
//		StqjfyVO[] vos =volist.toArray(new StqjfyVO[volist.size()]);
//		
//		DelteHis(StqjfyVO.class);
//		
//		String[] pks=getSbo().insertVOArr(pk_corp, vos);
//		int count=pks.length;
//		for(int i=0;i<count;i++){
//			vos[i].setPrimaryKey(pks[i]);
//		}
//		return vos;
//	}
	
	
	/**
	 * 构造基础报表
	 * @throws  
	 * @throws Exception 
	 */
	
	private StBaseVO[] genBaicReprotValue(String reportcode,Class beantype) throws BusinessException{
		String[] items = NssbContrastPrjToAcc.getInstanct().getReportItem(reportcode);//获取报表行名
		String[][] reportconst = NssbContrastPrjToAcc.getInstanct().getReportConst(reportcode,tradeType);//获取报表项目会计科目对照
		String[] columns = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
		int fx=NssbContrastPrjToAcc.getInstanct().getReportKmqsfx(reportcode);

		int rowcount = items.length;
		
		//值列
		int colcount=reportconst.length-1;
		StBaseVO[] reportvos = (StBaseVO[]) Array.newInstance(beantype, rowcount);
//				new StBaseVO[rowcount];
		for(int i=0;i<rowcount;i++){
			try{
			reportvos[i]=(StBaseVO)beantype.newInstance();
			}catch(Exception e){
				log.error("错误", e);
				throw new BusinessException(e.getMessage());
			}
			reportvos[i].setVno(Integer.toString(i+1));
			reportvos[i].setVprojectname(items[i]);
			reportvos[i].setPk_corp(pk_corp);
			reportvos[i].setCyear(cyear);
			
//			String kmcode =kmcodes[i];
			
			for(int j=0;j<colcount;j++){//金额列计算赋值
				String kmcode =reportconst[j+1][i];
				if(kmcode.startsWith("=")){
					reportvos[i].setAttributeValue(columns[j], new DZFDouble(kmcode.replace("=", "")));
				}else if("NA".equals(kmcode)||NssbContrastPrjToAcc.STA.equals(kmcode)||NssbContrastPrjToAcc.EDIT.equals(kmcode)||NssbContrastPrjToAcc.FM.equals(kmcode)||fseMap==null){
					reportvos[i].setAttributeValue(columns[j], DZFDouble.ZERO_DBL);
//					ybsrvos[i].setVmny(zero);
				}else if(kmcode.startsWith("FM(")){//使用公式计算Cell值
					
				}else{
					
					String[] codes = kmcode.split("\\+|-|\\*|/|\\(|\\)");
					DZFDouble dvalue =DZFDouble.ZERO_DBL;
					if(codes.length>1){
						dvalue = calculateCellFu(kmcode, fx);
					}else{
						FseJyeVO kmfsvo = fseMap.get(kmcode);
						if(kmfsvo!=null){
							if(fx==NssbContrastPrjToAcc.JF){
								dvalue= NssbReportUtil.getDvalue(kmfsvo.getJftotal());
	//									(kmfsvo==null?zero:(kmfsvo.getJftotal()==null?zero:kmfsvo.getJftotal()));//借方本年发生额
							}else{
								dvalue=NssbReportUtil.getDvalue(kmfsvo.getDftotal());
	//									(kmfsvo==null?zero:(kmfsvo.getDftotal()==null?zero:kmfsvo.getDftotal()));//借方本年发生额
							}
						}
					}
					reportvos[i].setAttributeValue(columns[j], dvalue.setScale(2, DZFDouble.ROUND_UP));
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
				reportvos[i].setAttributeValue(key, dvalue.setScale(2, DZFDouble.ROUND_UP));
				
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
		int fx=NssbContrastPrjToAcc.getInstanct().getReportKmqsfx(reportcode);
		
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
		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
		
		return drs;
		
	}
	
//	private<T> List<T> genBaicReprotValue(String reportcode,Class beantype) throws BusinessException{
//		String[] items = NssbContrastPrjToAcc.getInstanct().getReportItem(reportcode);//获取报表行名
//		String[][] reportconst = NssbContrastPrjToAcc.getInstanct().getReportConst(reportcode,tradeType);//获取报表项目会计科目对照
//		String[] columns = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
//		int fx=NssbContrastPrjToAcc.getInstanct().getReportKmqsfx(reportcode);
//
//		int rowcount = items.length;
//		
//		//值列
//		int colcount=reportconst.length-1;
//		StBaseVO[] reportvos = new StBaseVO[rowcount];
//		for(int i=0;i<rowcount;i++){
//			try{
//			reportvos[i]=(StBaseVO)beantype.newInstance();
//			}catch(Exception e){
//				throw new BusinessException(e);
//			}
//			reportvos[i].setVno(Integer.toString(i+1));
//			reportvos[i].setVprojectname(items[i]);
//			reportvos[i].setPk_corp(pk_corp);
//			reportvos[i].setCyear(cyear);
//			
////			String kmcode =kmcodes[i];
//			
//			for(int j=0;j<colcount;j++){//金额列计算赋值
//				String kmcode =reportconst[j+1][i];
//				if("NA".equals(kmcode)||NssbContrastPrjToAcc.STA.equals(kmcode)||NssbContrastPrjToAcc.EDIT.equals(kmcode)||NssbContrastPrjToAcc.FM.equals(kmcode)||fseMap==null){
////					ybsrvos[i].setVmny(zero);
//				}else if(kmcode.startsWith("FM(")){//使用公式计算Cell值
//					
//				}else{
//					FseJyeVO kmfsvo = fseMap.get(kmcode);
//					DZFDouble dvalue =zero;
//					if(fx==NssbContrastPrjToAcc.JF){
//						dvalue=(kmfsvo==null?zero:(kmfsvo.getJftotal()==null?zero:kmfsvo.getJftotal()));//借方本年发生额
//					}else{
//						dvalue=(kmfsvo==null?zero:(kmfsvo.getDftotal()==null?zero:kmfsvo.getDftotal()));//借方本年发生额
//					} 
//					reportvos[i].setAttributeValue(columns[j], dvalue.setScale(2, DZFDouble.ROUND_UP));
//				}
//			}
//			
//
//		}
//		
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
//		
//		return (List<T>)Arrays.asList(reportvos);
//	
//	}
	
	
	
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
			FseJyeVO fsvo = fseMap.get(codes[i]);

			if(fsvo!=null){
				if (fx == NssbContrastPrjToAcc.JF) {
					dvalue = NssbReportUtil.getDvalue(fsvo.getJftotal());
				} else {
					dvalue = NssbReportUtil.getDvalue(fsvo.getDftotal());
				}
			}
			formula = formula.replaceAll(codes[i], dvalue.toString());
		}
		
		Formula fmcal = new Formula(formula);//计算
		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
		
		return drs;
		
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
	
	
	private void DelteHis(Class clazz){
		String whq = new String(" pk_corp=? and cyear=?");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(cyear);
		
		List<StBaseVO> rslist =(List<StBaseVO>)getSbo().executeQuery(whq, sp, new Class[]{clazz});
		if(rslist!=null&&rslist.size()>0){
			getSbo().deleteVOArray(rslist.toArray(new StBaseVO[rslist.size()]));
		}
	}
	
	
	
	//重新计算
	public <T> T reCalculate(String reportcode,StBaseVO[] reportvos){
		
		String[] vitems = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
		reportvos = calculateSumRpFm(reportcode,vitems,reportvos);
		return (T)reportvos;
		
	}
	
	/**
	 * 编辑后报表统计公式计算
	 * */
	private StBaseVO[] calculateSumRpFm(String reportcode,String[] vitems,StBaseVO[] reportvos){
		String[] formuls =NssbReportFormula.getInstanct().getCalculateFromula(reportcode);//获取报表统计公式
		int count = formuls.length;//公式的个数
		fm = new HashMap<String,String>();//公式MAP
		fmvalue = new HashMap<String,DZFDouble>();//公式计算值MAP
		String[] fmnos = new String[count];//公式MAP-KEY
		for(int i=0;i<count;i++){
			String[] ff =formuls[i].split("=");
			fmnos[i]=ff[0];
			fm.put(ff[0], ff[1]);
		}
		
		int colcount=vitems.length;
		for(int i=0;i<colcount;i++){//金额列计算赋值
//			String[] kmcodes = reportconst[i+1];
			fmvalue = new HashMap<String,DZFDouble>();
			String item = "rp_"+vitems[i];
			for(int j=0;j<count;j++){
				String key =fmnos[j];
				String fu =fm.get(key);
				int index=Integer.parseInt(key.replaceAll("'", ""));
				
				Object o = reportvos[index-1].getAttributeValue(item);
				if(o!=null&&o.toString().equalsIgnoreCase("STA_")){
					continue;
				}

				DZFDouble dvalue = calculateFm(fu, reportvos,item);
				reportvos[index-1].setAttributeValue(item, dvalue.setScale(2, DZFDouble.ROUND_UP));
				
			}
		}
		return reportvos;
	}
	
	/**
	 * 编辑后报表计算公式值
	 * */
	private DZFDouble calculateFm(String formula,StBaseVO[] reportvos,String item){
		
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
					dvalue =calculateFm(fm.get(codes[i]),reportvos,item);
					fmvalue.put(codes[i], dvalue);
				}
			}else{//获取变量值
				int vindex=Integer.parseInt(codes[i].replaceAll("'", ""))-1;
				Object v=reportvos[vindex].getAttributeValue(item);
				if(v!=null){
					String cv=v.toString();
					//TODO 需要优化
					if(cv.length()>0&&
							!cv.equalsIgnoreCase(NssbContrastPrjToAcc.STA+"_")&&
							!cv.equalsIgnoreCase(NssbContrastPrjToAcc.EDIT+"_")&&
							!cv.equalsIgnoreCase(NssbContrastPrjToAcc.FM+"_")&&
							!cv.equalsIgnoreCase("NA"+"_")){
						dvalue=new DZFDouble(v.toString(),2);
					}
				}
			}
			formula=formula.replaceAll(codes[i], dvalue.toString());
		}
		
		Formula fmcal = new Formula(formula);//计算
		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
		
		return drs;
		
	}
	
	/**
	 * 单表持久化
	 * */
	public void updateReportVO(String reportcode,StBaseVO[] reportvos){
		
		reportvos=setItemValueFromRpValue(reportcode, reportvos);
		int count=getSbo().updateAry(reportvos, NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode));
		
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
//				if(ov==null||ov.toString().length()==0)
//					continue;
//				String sv=ov.toString().replaceAll("NA|STA|EDIT|_", "");
//				if(sv.length()==0)
//					continue;
				DZFDouble dvalue =NssbReportUtil.getDvalue(ov);
				reportvos[i].setAttributeValue(columns[j],dvalue);
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
		String sql = new String(" select nvl(cstatus,'0') from ynt_st_nssbinfo where pk_corp=? and  cyear=?");
		SQLParameter sp = new SQLParameter();
		sp.addParam(infovo.getPk_corp());
		sp.addParam(infovo.getCyear());
		
		String status =(String)getSbo().executeQuery(sql, sp, new ColumnProcessor());
		
		if(status==null){
			return NssbReportUtil.unapproved;
		}
		return status;
	}
	
	public StNssbInfoVO getNssbInfo(StNssbInfoVO infovo){
		String whq = new String(" pk_corp=? and cyear=?");
		SQLParameter sp = new SQLParameter();
		sp.addParam(infovo.getPk_corp());
		sp.addParam(infovo.getCyear());
		
		StNssbInfoVO[] infvo=(StNssbInfoVO[])getSbo().queryByCondition(StNssbInfoVO.class, whq, sp);
		
		if(infvo!=null&&infvo.length>0){
			return infvo[0];
		}
		return null;
	}
	
}
