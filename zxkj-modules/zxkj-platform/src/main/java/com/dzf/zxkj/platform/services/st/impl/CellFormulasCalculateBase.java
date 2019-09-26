package com.dzf.zxkj.platform.services.st.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.enums.CellTypeEnum;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.st.CallInfoContext;
import com.dzf.zxkj.platform.model.st.CallInfoVO;
import com.dzf.zxkj.platform.model.st.NssbReportUtil;
import com.dzf.zxkj.platform.services.report.IFsYeReport;
import com.dzf.zxkj.platform.util.Formula;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/****
 * 公式计算基类
 * @author asoka
 *
 */
@Slf4j
public abstract class CellFormulasCalculateBase {

	//字符字段  区别于金额  VO中的字段以c开头
	public static  final String STRING_FIELD_FLAG = "c";
	
	public DZFDouble zero = new DZFDouble(0.0D,2);
	private SingleObjectBO sbo;
	
	private IFsYeReport kmreportsrv;//科目数据查询
	
    /***
     * 年  -->科目对照发生额，KEY=报表项目编号
     */
	private Map<String, FseJyeVO> fseMap_year;
		
	//外部数据源   key：页签编码  value：行数据
    private Map<String,Map<String,Object>> outdatasource=new HashMap<String,Map<String,Object>>();
	
	public IFsYeReport getKmreportsrv() {
		if(kmreportsrv==null){
			kmreportsrv=(IFsYeReport) SpringUtils.getBean("gl_rep_fsyebserv");
		}
		return kmreportsrv;
	}
	
	@SuppressWarnings("rawtypes")
	public SuperVO[] doCalculate(CallInfoVO callInfo)throws BusinessException {
		
		SuperVO[]  vos = genBaseReprot(callInfo,callInfo.getTabCode());
		callInfo.getAllReportvos().put(callInfo.getTabCode(), vos);
		
		return vos;
	}
	
	
	/**
	 * 构造一般企业收入报表，并持久化
	 * */
	@SuppressWarnings("rawtypes")
	public SuperVO[] genBaseReprot(CallInfoVO callInfo,String reportcode)throws BusinessException{
		
		SuperVO[] vos =genBaicReprotValue(callInfo,reportcode);
		
	    if(!callInfo.isUseUserDataCalcute()){
			deleteOldDatas(callInfo,reportcode);
			
			String pk_corp = callInfo.getQueryParamVO().getPk_corp();
			String[] pks=getSbo().insertVOArr(pk_corp, vos);
			int count=pks.length;
			for(int i=0;i<count;i++){
				vos[i].setPrimaryKey(pks[i]);
			}
	    }

		return vos;
	}
	
	
	/****
	 * 获取每个页签和Class的对照
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public abstract Map<String,Class> getTabCodeClassMap();
	
	
	/****
	 * 合并前后台调用信息
	 * @param callInfo
	 */
	public CallInfoVO mergeCallInfo(CallInfoVO frontCallInfo,String tabcode){
    	
    	String pk_corp = frontCallInfo.getQueryParamVO().getPk_corp();
    	CallInfoVO backCallInfo = CallInfoContext.getCallInfoByCode(tabcode,pk_corp,getTabCodeClassMap());
    	backCallInfo.setQueryParamVO(frontCallInfo.getQueryParamVO());
    	backCallInfo.setOtherParam(frontCallInfo.getOtherParam());
    	backCallInfo.setUseUserDataCalcute(frontCallInfo.isUseUserDataCalcute());
    	
    	//把一些信息回设置到前台传过来的frontCallInfo变量中 用作格式显示和能够编辑判断
    	frontCallInfo.setVariableFiledNames(backCallInfo.getVariableFiledNames());
    	frontCallInfo.setCellformulas(backCallInfo.getCellformulas());
    	   	
    	return backCallInfo;
    }
	
	
	/****
	 * 删除旧数据
	 * 子类需要扩展
	 * @param callInfos
	 * @param reportcode
	 */
	public abstract void deleteOldDatas(CallInfoVO callInfo,String reportcode);
	
	
	public SingleObjectBO getSbo() {
		if(sbo==null){
			sbo=(SingleObjectBO)SpringUtils.getBean("singleObjectBO");
		}
		return sbo;
	}
	
	
	/***
	 * 年度发生额
	 * @param callInfo
	 * @return
	 */
	public Map<String,FseJyeVO> getYearFse(CallInfoVO callInfo){
		
		if(fseMap_year!=null){
			
			return fseMap_year;
		}
		fseMap_year = new HashMap<String, FseJyeVO>();
		String cyear = callInfo.getQueryParamVO().getYear();
		String pk_corp = callInfo.getQueryParamVO().getPk_corp();
		DZFDate begin = new DZFDate(cyear+"-01-01");
		DZFDate  end = new DZFDate(cyear+"-12-31");
		initFseMap(fseMap_year,cyear,pk_corp,begin,end);
		
		return fseMap_year;
	}
	
	/****
	 * 获得当前计算的CallInfoVO信息
	 * @param callInfo  用户请求的调用信息
	 * @param reportcode 当前计算的页签编码  主要根据这个编码获取对应的CallInfoVO信息
	 * @return
	 */
	public CallInfoVO getCurrentCalculateCallInfo(CallInfoVO callInfo,String reportcode){
		
		CallInfoVO currentCalCallInfo = callInfo;
		if(reportcode!=null&&reportcode.trim().length()>0
				&&callInfo.getTabCode()!=null&&callInfo.getTabCode().trim().length()>0
				&&!callInfo.getTabCode().equals(reportcode)){
			
			String pk_corp = callInfo.getQueryParamVO().getPk_corp();
			currentCalCallInfo = CallInfoContext.getCallInfoByCode(reportcode,pk_corp,callInfo.getTabCodeClassMap());
			currentCalCallInfo.setQueryParamVO(callInfo.getQueryParamVO());
			currentCalCallInfo.setOtherParam(callInfo.getOtherParam());
		}
		
		return currentCalCallInfo;
	}
	
	
	/**
	 * 构造基础报表
	 * @throws  
	 * @throws Exception 
	 */	
	@SuppressWarnings("rawtypes")
	private SuperVO[] genBaicReprotValue(CallInfoVO callInfo,String reportcode) throws BusinessException{
		
		//当前计算tab信息
		CallInfoVO calCallInfoVO = getCurrentCalculateCallInfo(callInfo,reportcode);

		String[][] cellformulas =  calCallInfoVO.getCellformulas();//获取报表项目会计科目对照
		String[] columns = calCallInfoVO.getVariableFiledNames();//获取报表列值对照VO写入item
		
		SuperVO[] reportvos = null;
		if(!callInfo.isUseUserDataCalcute()){
			reportvos = setConstValues(calCallInfoVO);
			
			callInfo.getAllReportvos().put(reportcode, reportvos);
		}else{
			reportvos = callInfo.getCurrentReportVOs();
		}

	    beforeCalcuteCell(callInfo,reportcode);


		Map<String,Object> cvmap =getOutdatasource().get(reportcode);
	    
		for(int i=0;i<reportvos.length;i++){
			
			for(int j=0;j<cellformulas.length;j++){//金额列计算赋值
				String formula =cellformulas[j][i];
				if(formula.startsWith("=")){
					
					eqStartFormulaCalculate(reportvos,i,columns[j],formula,callInfo);
					
				}else if(CellTypeEnum.NA.getCode().equals(formula)||CellTypeEnum.STA.getCode().equals(formula)
						  ||CellTypeEnum.FM.getCode().equals(formula)){
					if(!callInfo.isUseUserDataCalcute()){
						if(!columns[j].startsWith(STRING_FIELD_FLAG)){
							reportvos[i].setAttributeValue(columns[j], zero);	
						}
						
						if(cvmap!=null){
							String key =(i+1)+"_"+columns[j];
							Object ov = cvmap.get(key);
							if(ov!=null){
								reportvos[i].setAttributeValue(columns[j], new DZFDouble(ov.toString()));
							}
						}
						
					}
				}else if(CellTypeEnum.FM.getCode().equals(formula)){//使用公式计算Cell值
					continue;
				}else{
					if(!callInfo.isUseUserDataCalcute()){
					   calAcccountItemFormula(reportvos,i,columns[j],formula,callInfo);
					}
				}
			}
		}	
		
		afterCalcuteCell(callInfo,reportcode);
		  
		try {
			calculateColFm(callInfo, reportcode);
		} catch (Exception e) {
           log.error("计算列公式时出错"+e.getMessage(),e);
           throw new BusinessException("计算列公式时出错"+e.getMessage());
		}
		try {
			calculateRowFm(callInfo, reportcode);
		} catch (Exception e) {
           log.error("计算行公式时出错"+e.getMessage(),e);
           throw new BusinessException("计算行公式时出错"+e.getMessage());
		}
	
		return reportvos;	
	}
	
	
	/****
	 * 执行单元格公式计算之前处理
	 * @param callInfo
	 * @param reportcode
	 */
	public void  beforeCalcuteCell(CallInfoVO callInfo,String reportcode){
		try {
			loadOutDataSource(callInfo,reportcode);			
		} catch (Exception e) {
		    log.error("加载外部数据源发生异常"+e.getMessage(),e);
		    throw new BusinessException("加载外部数据源发生异常"+e.getMessage());
		}
	
	}
	
	
	/****
	 * 执行单元格公式计算之后处理
	 * @param callInfo
	 * @param reportcode
	 */
	public void  afterCalcuteCell(CallInfoVO callInfo,String reportcode){
		
	}
	
	
	/****
	 * 加载外部数据源
	 * 
	 * @param callInfo
	 * @param reportcode
	 * @throws BusinessException
	 */
	public void loadOutDataSource(CallInfoVO callInfo,String reportcode)throws BusinessException{
	}
	
	
	/****
	 * 初始化发生额MAP
	 * @param fseMap
	 * @param year
	 * @param pk_corp
	 * @param begin
	 * @param end
	 */
	public void initFseMap(Map<String,FseJyeVO> fseMap,String year,String pk_corp,DZFDate begin,DZFDate end){
		
		QueryParamVO pramavo = new QueryParamVO();
		pramavo.setBegindate1(begin);
		pramavo.setEnddate(end);
		pramavo.setCjq(1);//查询科目级次
		pramavo.setCjz(6);
		pramavo.setIshasjz(new DZFBoolean(true));
		pramavo.setIshassh(new DZFBoolean(true));
		pramavo.setPk_corp(pk_corp);
		pramavo.setXswyewfs(new DZFBoolean(true));
		pramavo.setXsyljfs(new DZFBoolean(true));
		//科目发生
		Object[] kmfs_month = getKmreportsrv().getFsJyeVOs1(pramavo);
		
		if(kmfs_month!=null){
			FseJyeVO[] vos =(FseJyeVO[])kmfs_month[0];//科目发生额及			
			if(vos!=null&&vos.length>0){			
				for(FseJyeVO fsvo: vos){
					fseMap.put(fsvo.getKmbm(), fsvo);
				//	System.out.println("---->"+fsvo.getKmbm()+" , "+fsvo.getKmmc()+" , "+ fsvo.getFsdf()+" , "+ fsvo.getFsjf());
				}			
			}
		}
	}
	
	
	/****
	 * 设置常量字段值
	 * @param callInfo
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public SuperVO[] setConstValues(CallInfoVO callInfo) throws DZFWarpException {
		
		String[] items = callInfo.getConstantFieldValues()[0];
		int rowcount = items.length;
		SuperVO[] reportvos = (SuperVO[]) Array.newInstance(callInfo.getClazz(), rowcount);
		String[] constFieldnames = callInfo.getConstantFieldNames();
		String[][] constFieldValues = callInfo.getConstantFieldValues();
		for(int i=0;i<rowcount;i++){
			try{
			    reportvos[i]=(SuperVO)callInfo.getClazz().newInstance();
			    for(int col=0;col<constFieldnames.length;col++){
			    	reportvos[i].setAttributeValue(constFieldnames[col], constFieldValues[col][i]);
			    }
			    setUserValues(reportvos[i],i,callInfo);
			}catch(Exception e){
				throw new WiseRunException(e);
			}
		}
		
	   return reportvos;
	}
	
	
	/****
	 * 设置用户自定义的一些值
	 * @param vo
	 * @param index
	 * @param callInfo
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public void setUserValues(SuperVO vo,int index,CallInfoVO callInfo)throws BusinessException{
		
		QueryParamVO param = callInfo.getQueryParamVO();
		vo.setAttributeValue("pk_corp", param.getPk_corp());
		vo.setAttributeValue("cyear", param.getYear());
		vo.setAttributeValue("cmonth", param.getYmonth());
		vo.setAttributeValue("vno", index+1);		
	}
	
	
	/****
	 * = 号开始公式的计算
	 * 单表计算 如果有引用别的页签的字段可能有问题
	 * @param reportvos
	 * @param row
	 * @param fieldname
	 * @param formula
	 */
	@SuppressWarnings("rawtypes")
	public void eqStartFormulaCalculate(SuperVO[] reportvos,int row,String fieldname,String formula,CallInfoVO callInfo){
		
		if(formula.startsWith("=(")||formula.startsWith("='")||formula.startsWith("=[")){//表内公式
			DZFDouble dvalue = calculateCellFu(reportvos, formula.replace("=", ""),callInfo);
			reportvos[row].setAttributeValue(fieldname, dvalue);
		}else if(formula.startsWith("=IF")){//条件判断公式
			DZFDouble dvalue = CalCulateMutiBl(formula.replace("=", ""), reportvos,callInfo);
			reportvos[row].setAttributeValue(fieldname, dvalue);
		}else {//直接数字赋值
			reportvos[row].setAttributeValue(fieldname, new DZFDouble(formula.replace("=", "")));
		}
	}
	
	
	/***
	 * 计算会计科目公式
	 * @param formula
	 * @param row
	 * @param fieldname
	 */
	@SuppressWarnings("rawtypes")
	public abstract void calAcccountItemFormula(SuperVO[] reportvos,int row,String fieldname,String formula,CallInfoVO callInfo);
	
	
	/****
	 * 執行列公式	
	 * @param callinfo
	 */
	public void calculateColFm(CallInfoVO callInfo,String reportcode){//列统计公式计算
		
		CallInfoVO currentcalinfo = getCurrentCalculateCallInfo(callInfo, reportcode);
	
		String[] formuls =currentcalinfo.getColformulas();//获取报表统计公式
		if(formuls==null||formuls.length<=0){
			return ;
		}
		
		String[][] reportconst = currentcalinfo.getCellformulas();//获取报表项目会计科目对照
		String[] columns = currentcalinfo.getVariableFiledNames();//获取报表列值对照VO写入item
		
		
		int rowcount=callInfo.getAllReportvos().get(reportcode).length;
		int count = formuls.length;//公式的个数
		Map<String,String> fm = new HashMap<String,String>();//公式MAP
		Map<String,DZFDouble> fmvalue = new HashMap<String,DZFDouble>();//公式计算值MAP
		String[] fmnos = new String[count];//公式MAP-KEY
		String[] ff =null;
		for(int i=0;i<count;i++){
			ff =formuls[i].split("=");
			fmnos[i]=ff[0];
			fm.put(ff[0], ff[1]);
		}	
		String key =null;
		String fu =null;
		int index=0;
		DZFDouble dvalue =null;
		String code =null;
		String[] kmcodes = null;//zpm修改
		for(int i=0;i<rowcount;i++){//金额列计算赋值
			fmvalue = new HashMap<String,DZFDouble>();
			for(int j=0;j<count;j++){
				 key =fmnos[j];
				 fu =fm.get(key);
				 index=Integer.parseInt(key.replaceAll("'", ""));
				
				 kmcodes = reportconst[index-1];
				if(CellTypeEnum.STA.getCode().equals(kmcodes[i]))
					continue;
				
				 dvalue = calculateFu(fu, callInfo.getAllReportvos().get(reportcode)[i],columns,fm,fmvalue);
				 code =columns[index-1];
				callInfo.getAllReportvos().get(reportcode)[i].setAttributeValue(code, dvalue.setScale(2, DZFDouble.ROUND_UP));			
			}
		}
	}
	
	
	/****
	 * 行统计公式计算
	 * @param callinfo
	 */
	public void calculateRowFm(CallInfoVO callInfo,String reportcode){
		
		CallInfoVO currentcalinfo = getCurrentCalculateCallInfo(callInfo, reportcode);
		String[] formuls = currentcalinfo.getRowformulas();//获取报表统计公式
		if(formuls==null||formuls.length==0){
			return ;
		}
		
		String[][] reportconst = currentcalinfo.getCellformulas(); //获取报表项目会计科目对照
		String[] columns = currentcalinfo.getVariableFiledNames();//获取报表列值对照VO写入item
		
		int colcount=columns.length;
		int count = formuls.length;//公式的个数
		Map<String,String> fm = new HashMap<String,String>();//公式MAP
		Map<String,DZFDouble> fmvalue = new HashMap<String,DZFDouble>();//公式计算值MAP
		String[] fmnos = new String[count];//公式MAP-KEY
		String[] ff =null;
		for(int i=0;i<count;i++){
 			 ff =formuls[i].split("=");
			fmnos[i]=ff[0];
			fm.put(ff[0], ff[1]);
		}	
		String key =null;
		String fu =null;
		int index=0;
		DZFDouble dvalue =null;
		String[] kmcodes =null;
		for(int i=0;i<colcount;i++){//金额列计算赋值
			kmcodes = reportconst[i];
			fmvalue = new HashMap<String,DZFDouble>();
			String item =columns[i];
			for(int j=0;j<count;j++){
				 key =fmnos[j];
				 fu =fm.get(key);
				
				 index=Integer.parseInt(key.replaceAll("'", ""));
				if(CellTypeEnum.STA.getCode().equals(kmcodes[index-1])){
					continue;
				}
				
				 dvalue = calculateFu(fu,item,callInfo.getAllReportvos().get(reportcode),fm,fmvalue);
				callInfo.getAllReportvos().get(reportcode)[index-1].setAttributeValue(item, dvalue.setScale(2, DZFDouble.ROUND_UP));			
			}
		}
	}
	
	
	/****
	 * if 公式计算
	 * @param fomula
	 * @param reportvos
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public DZFDouble CalCulateMutiBl(String fomula,SuperVO[] reportvos,CallInfoVO callInfo){
		DZFDouble dvalue = zero;
	
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
		int iStart =0;
		String formulaStr = null;
		String[] keys=null;
		DZFDouble dv =null;
		int iEnd = 0;
		for(int i=0;i<leftBracket;i++){
			 iStart = fomula.lastIndexOf("(") + 1;
			// 获得最里层括号里的内容
			 formulaStr = fomula.substring(iStart,
					iStart + fomula.substring(iStart).indexOf(")")).trim();
			
			if(formulaStr.contains(",")){
				
				 keys=vm.keySet().toArray(new String[0]);
				if(keys!=null&&keys.length>0){
					for(String key:keys){
						formulaStr=formulaStr.replace(key, vm.get(key));
					}
					vm.clear();
				}
				
				 dv = CalCulateBlFm("IF("+formulaStr+")", reportvos,callInfo);

				iStart = fomula.lastIndexOf("(");
				 iEnd = fomula.substring(iStart).indexOf(")") + 1;
				fomula = fomula.substring(0, iStart-2).trim()
						+ dv.toString()
						+ fomula.substring(iStart + iEnd, fomula.length())
								.trim();
				i++;
			}else{
				fomula=fomula.replace("("+formulaStr+")", "k"+i);
				vm.put("k"+i, "("+formulaStr+")");			
			}
		}
		if(fomula.contains(",")){
			dvalue=CalCulateBlFm(fomula, reportvos,callInfo);
		}else{
			dvalue=getDvalue(fomula);
		}
		
		return dvalue;
	}
	
	
	public  DZFDouble getDvalue(Object repvalue){
		if(repvalue!=null&&repvalue.toString().length()>0){
			String svalue=repvalue.toString();
			svalue=svalue.replaceAll("NA|STA|EDIT|FM|_", "");
			if(svalue.length()==0){
				return zero;
			}else{
				return new DZFDouble(svalue.toString());
			}
		}else{
			return zero;
		}
	}
	
	
	//IF(A<B,C,D) if A<B RETURN C ELSE RETURN D
	@SuppressWarnings("rawtypes")
	public DZFDouble CalCulateBlFm(String fm,SuperVO[] reportvos,CallInfoVO callInfo){
		log.info("本次计算公式："+fm);
		fm=fm.replaceAll("IF|\\(|\\)", "");
		String[] ems=fm.split(",");
		String[] cpstr = ems[0].split("==|>=|<=|<|>");
		int start =ems[0].indexOf(cpstr[0])+cpstr[0].length();
		int end =ems[0].indexOf(cpstr[1], start);

		String symbol=ems[0].substring(start,end);
				
		DZFDouble da=getItemValue(cpstr[0], reportvos,callInfo);
		DZFDouble db=getItemValue(cpstr[1], reportvos,callInfo);
		DZFDouble dc=getItemValue(ems[1], reportvos,callInfo);
		DZFDouble dd=getItemValue(ems[2], reportvos,callInfo);
		
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

	
	@SuppressWarnings("rawtypes")
	private DZFDouble getItemValue(String itemstr,SuperVO[] reportvos,CallInfoVO callInfo){
		
		String[] codes=itemstr.split("\\+|-|\\*|/|\\(|\\)");
		if(codes.length<=1&&!codes[0].contains("'")&&!codes[0].startsWith("[")){
			return NssbReportUtil.getDvalue(codes[0].replaceAll("'", ""));
		}
		for(String code:codes){
			if(code.startsWith("'")){
				String[] as =code.split("#");
				int fi=Integer.parseInt(as[0].replaceAll("'", ""))-1;
				Object oas=null;
				oas = reportvos[fi].getAttributeValue(as[1].replaceAll("'", ""));
				DZFDouble das=NssbReportUtil.getDvalue(oas);
				itemstr=itemstr.replaceAll(code, das.toString());
			}else if (code.startsWith("[")) {// 表间值替换(<A101010!1#vmny>)
				DZFDouble das= getReportItemValue(code.replaceAll("\\[|\\]", ""),callInfo);
				itemstr=itemstr.replaceAll(code.replaceAll("\\[|\\]",""),das.toString());
			}
		}
		
		itemstr=itemstr.replaceAll("\\[|\\]", "");
		
		Formula f= new Formula(itemstr);
		DZFDouble drs = new DZFDouble(f.getResult(),2);
		return drs;
	}
	
	
	/**
	 * 
	 * 计算公式的值
	 * */
	@SuppressWarnings("rawtypes")
	private  DZFDouble calculateFu(String fu, SuperVO reportvo,String[] beanitems,
								Map<String,String> fm,Map<String,DZFDouble> fmvalue) {
		
		String[] codes=fu.split("\\+|-|\\*|/|\\(|\\)");
		int len=codes.length;
		for(int i=0;i<len;i++){
			if(codes[i].length()==0&&!codes[i].startsWith("'"))//判断是否为替换变量
				continue;
			DZFDouble dvalue= new DZFDouble(0.0D,2);
			if(fm.get(codes[i])!=null){//是否也为公式统计变量
				if(fmvalue.get(codes[i])!=null){
					dvalue=fmvalue.get(codes[i]);
				}else{
					dvalue =calculateFu(fm.get(codes[i]),reportvo,beanitems,fm,fmvalue);
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
	

	/**
	 * 
	 * 计算公式的值
	 * */
	@SuppressWarnings("rawtypes")
	private DZFDouble calculateFu(String formula,String item,SuperVO[] reportvos,Map<String,String> fm,Map<String,DZFDouble> fmvalue){
		
		String[] codes=formula.split("\\+|-|\\*|/|\\(|\\)");
		int len=codes.length;
		for(int i=0;i<len;i++){
			if(codes[i].length()==0&&!codes[i].startsWith("'"))//判断是否为替换变量
				continue;
			DZFDouble dvalue= new DZFDouble(0.0D,2);
			if(fm.get(codes[i])!=null){//是否也为公式统计变量
				if(fmvalue.get(codes[i])!=null){
					dvalue=fmvalue.get(codes[i]);
				}else{
					dvalue =calculateFu(fm.get(codes[i]),item,reportvos,fm,fmvalue);
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

	
	/**
	 * 
	 * 根据公式计算单元值
	 * */
	@SuppressWarnings("rawtypes")
	public DZFDouble calculateCellFu(SuperVO[] reportvos,String formula,CallInfoVO callInfo){
			
		String[] codes=formula.split("\\+|-|\\*|/|\\(|\\)");
		int len=codes.length;
		for (int i = 0; i < len; i++) {
			 if(codes[i].length()==0)
			 continue;
			 String code =codes[i];
			 DZFDouble dvalue = zero;
			if (codes[i].startsWith("'")) {// 表内值替换('4#vmny'-'5#vmny')

				String[] as = code.split("#");
				int fi = Integer.parseInt(as[0].replaceAll("'", "")) - 1;
				Object oas = reportvos[fi].getAttributeValue(as[1].replaceAll(
						"'", ""));
				dvalue = getDvalue(oas);
				formula = formula.replaceAll(codes[i], dvalue.toString());
				
			} else if (codes[i].startsWith("[")) {// 表间值替换(<A101010!1#vmny>)
				dvalue = getReportItemValue(codes[i].replaceAll("\\[|\\]", ""),callInfo);
				formula = formula.replaceAll(codes[i].replaceAll("\\[|\\]", ""), dvalue.toString());
			}
			 
		}
		formula=formula.replaceAll("\\[|\\]", "");
		Formula fmcal = new Formula(formula);//计算
		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
		
		return drs;		
	}
	
	
	/**
	 * 表间取值
	 * */
	@SuppressWarnings("rawtypes")
	public DZFDouble getReportItemValue(String itemstr,CallInfoVO callInfo) {
		DZFDouble dv= zero;
		String[] codes =itemstr.split("!");
		String reportcode = codes[0];
		String itemcode = codes[1];
		
		String[] as = itemcode.split("#");		
		
		SuperVO[] vos = callInfo.getAllReportvos().get(reportcode);
		if(vos==null||vos.length==0){
			vos=genBaseReprot(callInfo,reportcode);
			callInfo.getAllReportvos().put(reportcode, vos);
		}else{
			dv=getDvalue(as[0]);
		}
		int index = Integer.parseInt(as[0])-1;
		Object ors =vos[index].getAttributeValue(as[1]);
		dv=getDvalue(ors);
		return dv;
	}

	public Map<String, FseJyeVO> getFseMap_year() {
		return fseMap_year;
	}

	public void setFseMap_year(Map<String, FseJyeVO> fseMap_year) {
		this.fseMap_year = fseMap_year;
	}

	public Map<String,Map<String, Object>> getOutdatasource() {
		return outdatasource;
	}

	public void setOutdatasource(
			Map<String, Map<String, Object>> outdatasource) {
		this.outdatasource = outdatasource;
	}

	
	

}
