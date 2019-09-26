package com.dzf.zxkj.platform.services.st.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.st.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.report.IFsYeReport;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.util.Formula;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 纳税申报
 * */
@Service("nssbcellrdmo")
public class NssbReportRCellDMO extends NssbBaseDMO{
	
	private SingleObjectBO sbo;
	
	private IFsYeReport kmreportsrv;//科目数据查询
	
	private IYntBoPubUtil pubutil;
	
	private CorpVO corpvo;
	
	private boolean isinit = false;
	

	
	//构造新的报表用
	private String pk_corp;
	private String cyear;
	private String tradeType;
	private DZFDouble zero = new DZFDouble(0.0D,2);
	private Map<String,String> fm;
	private Map<String,DZFDouble> fmvalue;
	private Map<String, StBaseVO[]> reportmap;
	private Boolean bcalmain=false;//全表计算；
	
	
	private SingleObjectBO getSbo() {
		if(sbo==null){
			sbo=(SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		}
		return sbo;
	}
	
	public IYntBoPubUtil getPubutil() {
		if(pubutil==null){
			pubutil=(IYntBoPubUtil)SpringUtils.getBean("yntBoPubUtil");
		}
		return pubutil;
	}

	public NssbReportRCellDMO(){
		reportmap=new HashMap<>();
	};//默认构造方法
	
	public void init(){
	
		String condition = " pk_corp=? "; 
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		List<CorpVO> list = (List<CorpVO>)getSbo().retrieveByClause(CorpVO.class, condition, params);
		if(list!=null&&list.size()>0){
			corpvo = list.get(0);
		}
		
		isinit = true;
	}
	
	public void setCyear(String cyear) {
		this.cyear = cyear;
	}
	
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Map<String, StBaseVO[]> getReportmap() {
		return reportmap;
	}
	
	public void setBcalmain(Boolean bcalmain) {
		this.bcalmain = bcalmain;
	}
	
	
	//重新计算
	public <T> T reCalculate(String reportcode,StBaseVO[] reportvos){
		
		if(!isinit){
			init();
		}
		
		String[] vitems = NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);//获取报表列值对照VO写入item
		reportvos = calculateSumRpColFm(reportcode,reportvos);
		reportvos = calculateSumRpFm(reportcode,vitems,reportvos);
		return (T)reportvos;
		
	}
	
	//全表重新计算
	public Map<String, StBaseVO[]> reCalculateMain(Map<String, StBaseVO[]> vosmap){
		
		if(!isinit){
			init();
		}
		
		reportmap=vosmap;
		
		StNssbMainVO[] mainvos =(StNssbMainVO[])reportmap.get(NssbReportUtil.nssb);
		caledcode.add(NssbReportUtil.nssb);
		mainvos=(StNssbMainVO[])reCalculateRpCellFm(NssbReportUtil.nssb, mainvos);//单元格公式
		mainvos=reCalculate(NssbReportUtil.nssb, mainvos);//统计公式
		
		reportmap.put(NssbReportUtil.nssb, mainvos);
		
		return reportmap;
	}
	
	public StBaseVO[] reCalculateRpCellFm(String reportcode,StBaseVO[] reportvos){
		
		if(reportmap==null){
			reportmap= new HashMap<>();
		}
		reportmap.put(reportcode, reportvos);
		
		if(!isinit){
			init();
		}
		
		setCyear(reportvos[0].getCyear());
		setPk_corp(reportvos[0].getPk_corp());
		
		NssbReportUtil util = new NssbReportUtil(this);
		List uneditcell = NssbReportEditPower.getInstance().getUnEditAble(reportcode);
		
		if("A105000".equals(reportcode)){
			uneditcell.add(33, "23_vssje");
		}
		
		if(getPubutil().is2007AccountSchema(pk_corp)){
			tradeType="2007";
		}else{
			tradeType="2013";
		}
		String[][] rconst=NssbContrastPrjToAcc.getInstanct().getReportConst(reportcode, tradeType);
		String[] items= NssbContrastPrjToAcc.getInstanct().getReportBeanItem(reportcode);
		int itemcount=items.length;
		int count=uneditcell.size();
		String[][] fmcells=new String[count][2];
		for(int i=0;i<count;i++){//不可编辑
			String cell=(String)uneditcell.get(i);
			String[] sps= cell.split("_");//不可编辑 [0]行索引 [1]列索引
			for(int j=0;j<itemcount;j++){
				if(sps[1].equals(items[j])){
					sps[1]=Integer.toString(j);//不可编辑 列索引
					fmcells[i]=sps;
					break;
				}
			}
		}
		
		
		
		for(int i=0;i<count;i++){
			String[] cell =fmcells[i];
			
			int rowindex =Integer.parseInt(cell[0])-1;
			int colindex = Integer.parseInt(cell[1]);
			
			String fmcode =rconst[colindex+1][rowindex];//不可编辑单元格用公式计算
//			if("=IF('1#vssje'*'10#vssgdkcl'<'10#vzzje','1#vssje'*'10#vssgdkcl','10#vzzje')".equals(fmcode)){
//			//System.out.println("1111");	
//			}
			
			if(fmcode.startsWith("=")){
				if(fmcode.startsWith("=(")||fmcode.startsWith("='")||fmcode.startsWith("=[")){//表内公式
					DZFDouble dvalue = calculateCellFu(reportvos, fmcode.replace("=", ""));
					if("A107040".equals(reportcode)&&0==colindex){//减免所得税优惠明细表
						dvalue = getSpecialValue(dvalue,rowindex,colindex);
					}
					reportvos[rowindex].setAttributeValue("rp_"+items[colindex], dvalue.toString());
				}else if(fmcode.startsWith("=IF")){//条件判断公式
					DZFDouble dvalue = util.CalCulateMutiBl(fmcode.replace("=", ""), reportvos);
					reportvos[rowindex].setAttributeValue("rp_"+items[colindex], dvalue.toString());
				}
			}
			
		}
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
		
		DZFDouble res = zero;
		if(corpvo==null||corpvo.isIssmall()==null||!corpvo.isIssmall().booleanValue()|| StringUtil.isEmpty(corpvo.getEstablishtime())){
			return zero;
		}
		//小于0大于30万
		if(value.doubleValue()<=0||value.sub(new DZFDouble(300000)).doubleValue()>0){
			return zero;
		}else if(0<value.doubleValue()&&value.sub(new DZFDouble(200000)).doubleValue()<=0){//0< <=20
			res = value.multiply(new DZFDouble(0.15));
		}else{//20万< <=30万

			Map<String,Double> map = null;
			if(0==row){//优惠
				map = getPremiumRate();
			}else if(1==row){// 减半
				map = getSubHalf();
			}
			String time = corpvo.getEstablishtime();
			res = new DZFDouble(map == null ? 0.00:map.get(time)).multiply(value);
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
	
	private StBaseVO[] calculateSumRpColFm(String reportcode, StBaseVO[] reportvos){//列统计公式计算

		String[] formuls =NssbReportFormula.getInstanct().getCalculateColFromula(reportcode);//获取报表统计公式
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
				String code ="rp_"+columns[index-1];
				reportvos[i].setAttributeValue(code, dvalue.setScale(2, DZFDouble.ROUND_UP));
				
			}
		}
		
		return reportvos;
	}
	
	/**
	 * 编辑后报表统计公式计算
	 * */
	private StBaseVO[] calculateSumRpFm(String reportcode,String[] vitems,StBaseVO[] reportvos){
		String[] formuls =NssbReportFormula.getInstanct().getCalculateFromula(reportcode);//获取报表统计公式
		if(formuls==null||formuls.length==0)
			return reportvos;
		int count = formuls.length;//公式的个数
		fm = new HashMap<String,String>();//公式MAP
		fmvalue = new HashMap<String,DZFDouble>();//公式计算值MAP
		String[] fmnos = new String[count];//公式MAP-KEY
		for(int i=0;i<count;i++){
			String[] ff =formuls[i].split("=");
			fmnos[i]=ff[0];
			fm.put(ff[0], ff[1]);
		}
		//hzp modify 非数值列要排除在外  行或列公式执行的时候要特别注意  如果可以放到单元格公式中
		if(reportvos[0]!=null&&StYjdNssbMainVO.class==reportvos[0].getClass()){
			vitems = new String[]{"vbqmny", "vljmny"};
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
			DZFDouble dvalue=zero;
			if(fm.get(codes[i])!=null){//是否也为公式统计变量
				if(reportvos[0]!=null&&StYjdNssbMainVO.class==reportvos[0].getClass()){
					if(("rp_vbqmny".equals(item)||"rp_vljmny".equals(item))&&"'4'".equals(codes[i])){//hzp 添加 列公式中有需要和自身计算的 比如 4=2-3-4  那么后面4的值应该直接去而不是递归调用
						Object v = reportvos[4-1].getAttributeValue("rp_vbqmny");
						dvalue=new DZFDouble(v.toString(),2);
					}
				}else{
					if(fmvalue.get(codes[i])!=null){
						dvalue=fmvalue.get(codes[i]);
					}else{
						dvalue =calculateFm(fm.get(codes[i]),reportvos,item);
						fmvalue.put(codes[i], dvalue);
					}
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

	
	private DZFDouble calculateFu(String fu, StBaseVO reportvo,String[] beanitems) {
		
		String[] codes=fu.split("\\+|-|\\*|/|\\(|\\)");
		int len=codes.length;
		for(int i=0;i<len;i++){
			if(codes[i].length()==0&&!codes[i].startsWith("'"))//判断是否为替换变量
				continue;
			DZFDouble dvalue=zero;
			if(fm.get(codes[i])!=null){//是否也为公式统计变量
				if(fmvalue.get(codes[i])!=null){
					dvalue=fmvalue.get(codes[i]);
				}else{
					dvalue =calculateFu(fm.get(codes[i]),reportvo,beanitems);
					fmvalue.put(codes[i], dvalue);
				}
			}else{
				String code ="rp_"+beanitems[Integer.parseInt(codes[i].replaceAll("'", ""))-1];
				dvalue=NssbReportUtil.getDvalue(reportvo.getAttributeValue(code));
			}
			fu=fu.replaceAll(codes[i], dvalue.toString());
		}
		
		Formula fmcal = new Formula(fu);//计算
		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
		
		return drs;
		
	}
	
	
	/**
	 * 
	 * 根据公式计算单元值
	 * */
	public DZFDouble calculateCellFu(StBaseVO[] reportvos,String formula){
		
		String[] codes=formula.split("\\+|-|\\*|/|\\(|\\)");
		int len=codes.length;
		for (int i = 0; i < len; i++) {
			 if(codes[i].length()==0)
			 continue;
			 String code =codes[i];
			 DZFDouble dvalue = zero;
			if (codes[i].startsWith("'")) {// 表内值替换('4#vmny'-'5#vmny')
                
				if(code.indexOf("#")>0){
					String[] as = code.split("#");
					int fi = Integer.parseInt(as[0].replaceAll("'", "")) - 1;
					String itemcode ="rp_"+as[1].replaceAll("'", "");
					Object oas = reportvos[fi].getAttributeValue(itemcode);
					dvalue = NssbReportUtil.getDvalue(oas);
				}else{
					dvalue = new DZFDouble(code.replaceAll("'", ""));
				}

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
	private List<String> caledcode = new ArrayList<String>();
	public DZFDouble getReportItemValue(String itemstr) {
		DZFDouble dv= zero;
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
		if(bcalmain){
			if(!caledcode.contains(reportcode)){
				vos=reCalculateRpCellFm(reportcode, vos);
				vos=reCalculate(reportcode, vos);
				
				caledcode.add(reportcode);
				reportmap.put(reportcode, vos);
			}
		}else{
			if(vos==null||vos.length==0){
				
				Class type =NssbReportUtil.getReportBeanType(reportcode);
				SQLParameter sp = new SQLParameter();
				sp.addParam(cyear);
				sp.addParam(pk_corp);
				vos=(StBaseVO[])getSbo().queryByCondition(type, "cyear=? and pk_corp=? order by vno+0", sp);
				
				reportmap.put(reportcode, vos);
			}
		}
//		if(vos==null||vos.length==0){
//			
//			Class type =NssbReportUtil.getReportBeanType(reportcode);
//			SQLParameter sp = new SQLParameter();
//			sp.addParam(cyear);
//			sp.addParam(pk_corp);
//			vos=(StBaseVO[])getSbo().queryByCondition(type, "cyear=? and pk_corp=? order by vno+0", sp);
//			
//			reportmap.put(reportcode, vos);
//		}else{
//		dv=NssbReportUtil.getDvalue(as[0]);
//		}
		int index = Integer.parseInt(as[0])-1;
		if(bcalmain){
			Object ors =vos[index].getAttributeValue("rp_"+as[1]);
			dv=NssbReportUtil.getDvalue(ors);
		}else{
			Object ors =vos[index].getAttributeValue(as[1]);
			dv=NssbReportUtil.getDvalue(ors);
		}
		return dv;
	}
	
	
}
