package com.dzf.zxkj.platform.services.st.impl;


import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.enums.AccountEnum;
import com.dzf.zxkj.common.enums.CellTypeEnum;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.st.CallInfoVO;
import com.dzf.zxkj.platform.model.st.NssbReportUtil;
import com.dzf.zxkj.platform.model.st.ReportTabClassMapping;
import com.dzf.zxkj.platform.model.st.StNssbInfoVO;
import com.dzf.zxkj.platform.services.st.INssbCltcReportSrv;
import com.dzf.zxkj.platform.util.Formula;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 文化事业建设费申报表
 * */
@Service("nssbcltcsrv")
@Slf4j
@SuppressWarnings("all")
public class NssbReportCltcSrvImpl extends ReportOneTabCommonProcess implements INssbCltcReportSrv {

	/****
	 * 月  --> 科目对照发生额，KEY=报表项目编号
	 */
	
		
	
	@SuppressWarnings("rawtypes")
	public Map<String, SuperVO[]> loadDefaultData(CallInfoVO callInfo)throws DZFWarpException {
		
		return commonLoadDefaultData(callInfo);
	}
		

	@SuppressWarnings("rawtypes")
	@Override
	public SuperVO[] generateReportData(CallInfoVO callInfo) throws DZFWarpException {
		
	    return commonGenerateReportData(callInfo);
	}
	
	
	@SuppressWarnings("rawtypes")
	public SuperVO[] calcuteAll(CallInfoVO callInfo)throws DZFWarpException{
		
		return commonCalculateAll(callInfo);
	}
		
	
	//保存纳税申报表总信息
	public void saveNssbInfo(CallInfoVO callInfo){
		
		StNssbInfoVO oldvo=getNssbInfo(callInfo);
		if(oldvo!=null){
			getSbo().deleteObject(oldvo);
		}else{
			oldvo = new StNssbInfoVO();
		}		
		QueryParamVO parms = callInfo.getQueryParamVO();
		oldvo.setCstatus(NssbReportUtil.unapproved);
		oldvo.setCyear(parms.getYear());
		oldvo.setCmonth(parms.getYmonth());
		oldvo.setCreatetime(new DZFDateTime());
		oldvo.setPk_corp(parms.getPk_corp());
		oldvo.setCreatepsnid(parms.getUserid());
		oldvo.setType(StNssbInfoVO.TYPE00003);
		getSbo().saveObject(parms.getPk_corp(), oldvo);

	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public SuperVO[] reCalculate(CallInfoVO callInfo) throws DZFWarpException {
		
          return commonReCalculate(callInfo);
	}
	
	
	@Override
	public void updateSingleReport(CallInfoVO callInfo) throws DZFWarpException {
		
	    commonUpdateSingleReport(callInfo);
	}
	

	@Override
	public void approveRpinfo(CallInfoVO callInfo) throws DZFWarpException {
          
		commonApproveRpinfo(callInfo);
	}


	@Override
	public void unaApproveRpinfo(CallInfoVO callInfo) throws DZFWarpException {
		
		 commonUnaApproveRpinfo(callInfo);
		
	}
	
	
	@SuppressWarnings("rawtypes")
	public Map<String,SuperVO[]> getUnLoadReportVos(CallInfoVO callInfo, List<String> loadTabId)throws DZFWarpException{
		
		return commondLoadReportVos(callInfo,loadTabId);
	}
	
	
	/****
	 * 从报表字段值设置到计算字段的值
	 */
	@SuppressWarnings("rawtypes")
	public SuperVO[] getCalcuteVos(CallInfoVO callInfo){
		
		DZFDouble zero = new DZFDouble(0.0D,2);
		SuperVO[] reportvos = callInfo.getCurrentReportVOs();
		String[] varfieldnames = callInfo.getVariableFiledNames();
		for(SuperVO vo : reportvos){
			for(String field : varfieldnames){
				Object value = vo.getAttributeValue("rp_"+field);
				if(value==null||value.toString().trim().length()<0){
					value = zero;
				}
			 vo.setAttributeValue(field, value);
           }
		}
		
		return reportvos;
	}
	
	
	@SuppressWarnings("rawtypes")
	public void transFrom2ReportVos(CallInfoVO callInfo,SuperVO[] calvos){
		
		String[] varfieldnames = callInfo.getVariableFiledNames();
		List<String> stacells = Arrays.asList(callInfo.getSTAcells());
		for(int i=0,len=calvos.length;i<len;i++){
			for(String field : varfieldnames){
				String key = (i+1)+"#"+field;
				if(stacells.contains(key)){
					calvos[i].setAttributeValue("rp_"+field, CellTypeEnum.STA.getCode());
					continue;
				}
				calvos[i].setAttributeValue("rp_"+field, calvos[i].getAttributeValue(field));
			}
		}
	}
	

	/***
	 * 计算会计科目公式
	 * @param formula
	 * @param row
	 * @param fieldname
	 */
	@SuppressWarnings("rawtypes")
	public void calAcccountItemFormula(SuperVO[] reportvos,int row,String fieldname,String formula,CallInfoVO callInfo){
		
		String[] codes = formula.split("\\+|-|\\*|/|\\(|\\)");
		DZFDouble dvalue =zero;
		if(codes.length>1){
			dvalue = calculateCellFu(formula, callInfo);
		}else{
			dvalue = calculateAccountFormula(formula,callInfo);
		}

		reportvos[row].setAttributeValue(fieldname, dvalue.setScale(2, DZFDouble.ROUND_UP));
	}
	
	@Override
	public void afterCalcuteCell(CallInfoVO callInfo, String reportcode) {
		
		//fseMap_month = null;
		setFseMap_year(null);
	}
	
	
	@Override
	public void loadOutDataSource(CallInfoVO callInfo, String reportcode) throws DZFWarpException {
	
		Map<String, Map<String,Object>> outdatasource = getOutdatasource();
		
		if(outdatasource==null){
			outdatasource=new HashMap<String, Map<String,Object>>();
			setOutdatasource(outdatasource);
		}
				
		QueryParamVO queryvo = callInfo.getQueryParamVO();
		String pk_corp = queryvo.getPk_corp();
		String month = queryvo.getYmonth();
		String year = queryvo.getYear();

		if(StringUtil.isEmpty(pk_corp)||StringUtil.isEmpty(year)||StringUtil.isEmpty(month)){
			throw new BusinessException("参数非空检查异常");
		}
		

		String sql = new String(" select mny1 from ynt_cltc_tab1 where pk_corp=? and cyear=? and cmonth=? and itemno ='16=10+11-12'");

		Calendar cl = Calendar.getInstance();
		cl.set(Integer.parseInt(year),Integer.parseInt(month), 1);
		cl.add(Calendar.MONTH, -2);
		DZFDate date =new DZFDate(cl.getTime());
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(Integer.toString(date.getYear()));
		sp.addParam(date.getStrMonth());
		
		
		Object ov =getSbo().executeQuery(sql, sp, new ColumnProcessor());

		String keycode ="11_mny1";

		Map<String,Object> vmap = new HashMap<String,Object>();
		if(ov==null){
			vmap.put(keycode, 0.00D);
		}else{
			String dv=ov.toString();
			if(dv.length()==0){
				vmap.put(keycode, 0.00D);
			}else{
				vmap.put(keycode, new DZFDouble(dv));
			}
			
		}
		
		outdatasource.put(reportcode, vmap);
		
	}
		
	
	/***
	 * 计算会计科目办公室 只包含一个科目 不包含运算 且要标明科目的借贷方向	
	 * @param formula
	 * @param callInfo
	 * @return
	 */
    private DZFDouble calculateAccountFormula(String formula,CallInfoVO callInfo){
    	
    	DZFDouble dvalue =zero;
		String[] fields = formula.split("_");
		if(fields.length>1){
			fields[0] = getCurrentCode(callInfo.getQueryParamVO().getPk_corp(),fields[0]);
			if(AccountEnum.CURRENT_MONTH_CREDIT.getCode().equals(fields[1])){//本月贷方
				Map<String, FseJyeVO> tempfse = getMonthFse(callInfo);
				if(tempfse!=null&&tempfse.containsKey(fields[0])&&tempfse.get(fields[0])!=null){
					FseJyeVO kmfsvo = tempfse.get(fields[0]);
					dvalue = getDvalue(kmfsvo.getFsdf());
				}
			}else if(AccountEnum.CURRENT_MONTH_DEBIT.getCode().equals(fields[1])){//本月借方
				Map<String,FseJyeVO> tempfse = getMonthFse(callInfo);
				if(tempfse!=null&&tempfse.containsKey(fields[0])&&tempfse.get(fields[0])!=null){
					FseJyeVO kmfsvo = tempfse.get(fields[0]);
					dvalue = getDvalue(kmfsvo.getFsjf());
				}
			}else if(AccountEnum.YEAR_TOTAL_CREDIT.getCode().equals(fields[1])){//本年累计贷方
				Map<String,FseJyeVO> tempfse = getYearFse(callInfo);
				if(tempfse!=null&&tempfse.containsKey(fields[0])&&tempfse.get(fields[0])!=null){
					FseJyeVO kmfsvo = tempfse.get(fields[0]);
					dvalue = getDvalue(kmfsvo.getDftotal());
				}
			}else if(AccountEnum.YEAR_TOTAL_DEBIT.getCode().equals(fields[1])){//本年累计借方
				Map<String,FseJyeVO> tempfse = getYearFse(callInfo);
				if(tempfse!=null&&tempfse.containsKey(fields[0])&&tempfse.get(fields[0])!=null){
					FseJyeVO kmfsvo = tempfse.get(fields[0]);
					dvalue = getDvalue(kmfsvo.getJftotal());
				}
			}			
		}else{
               //每个科目都要标明借方还是贷方这里不再根据页签去判断
		}
		
		return dvalue;
    }
	
    
	/**
	 * 
	 * 计算Cell 科目公式
	 * */
	private DZFDouble calculateCellFu(String formula,CallInfoVO callInfo){
		
		String[] codes=formula.split("\\+|-|\\*|/|\\(|\\)");
		int len=codes.length;
		for (int i = 0; i < len; i++) {

			DZFDouble dvalue = calculateAccountFormula(codes[i],callInfo);
			formula = formula.replaceFirst(codes[i], dvalue.toString());
		}
		
		Formula fmcal = new Formula(formula);//计算
		DZFDouble drs = new DZFDouble(fmcal.getResult(),2);
		
		return drs;		
	}
	
	
    /****
     * 月度发生额
     * @param callInfo
     * @return
     */
	private Map<String,FseJyeVO> getMonthFse(CallInfoVO callInfo){
		
	
		HashMap<String, FseJyeVO> fseMap_month = new HashMap<String, FseJyeVO>();
	    String month = callInfo.getQueryParamVO().getYmonth();
		String cyear = callInfo.getQueryParamVO().getYear();
		String pk_corp = callInfo.getQueryParamVO().getPk_corp();
		//月份的最后一天
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,Integer.parseInt(cyear));
        //设置月份
        cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
		DZFDate  begin = new DZFDate(cyear+"-"+month+"-01");
		DZFDate  end = new DZFDate(cal.getTime());
		initFseMap(fseMap_month,cyear,pk_corp,begin,end);
		
		return fseMap_month;
	}


	public  SQLParameter getCommonTabWhereSqlParameter(CallInfoVO callInfo){
		
		QueryParamVO param = callInfo.getQueryParamVO();
		SQLParameter sp = new SQLParameter();
		sp.addParam(param.getPk_corp());
		sp.addParam(param.getYear());
		sp.addParam(param.getYmonth());
		
		return sp;
	}
	
	public String getCommonTabWhereSql(){
		
		return " pk_corp=? and cyear=? and cmonth=?";
	}
	
	public  String getCommonTabOrderbySql(){
		
		return " order by vno";
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Class> getTabCodeClassMap() {
		return ReportTabClassMapping.cltcTax;
	}


	@Override
	public String getNssbWhereSql() {
		return "pk_corp=? and  cyear=? and cmonth=? and type=?";
	}


	@Override
	public SQLParameter getNssbWhereSqlParameter(CallInfoVO callInfo) {
		
		QueryParamVO param = callInfo.getQueryParamVO();
		SQLParameter sp = new SQLParameter();
		sp.addParam(param.getPk_corp());
		sp.addParam(param.getYear());
		sp.addParam(param.getYmonth());
		sp.addParam(StNssbInfoVO.TYPE00003);
		
		return sp;
	}

}
