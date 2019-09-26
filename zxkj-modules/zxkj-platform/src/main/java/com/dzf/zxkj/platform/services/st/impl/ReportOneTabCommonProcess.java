package com.dzf.zxkj.platform.services.st.impl;

import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.platform.model.st.StNssbInfoVO;
import com.dzf.zxkj.platform.services.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
@SuppressWarnings("all")
public abstract class ReportOneTabCommonProcess extends  CellFormulasCalculateBase{

	@Autowired
	ICpaccountCodeRuleService cpaccountCodeRuleService;
	
	@Autowired
	ICpaccountService cpaccountService;
	
	
	/****
	 * 记载默认数据
	 * @param callInfo
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, SuperVO[]> commonLoadDefaultData(CallInfoVO callInfo)throws BusinessException {
		
		try {
			Map<String,SuperVO[]> map = new HashMap<String,SuperVO[]>();
			if(isNeedApprove()){
				StNssbInfoVO infvo = getNssbInfo(callInfo);
				if(infvo==null){
					throw new BusinessException("没有税表信息，请先生成申报表!");
				}
				map.put("maininfo", new SuperVO[]{infvo});
			}
		
			CallInfoVO mergeCallInfo = mergeCallInfo(callInfo,callInfo.getTabCode());
			SuperVO[]  resvos = queryOneTabData(mergeCallInfo);
			if(resvos!=null&&resvos.length>0){				
				transFrom2ReportVos(mergeCallInfo,resvos);
				 map.put("data", resvos);
			}else{
				throw new BusinessException("没有税表信息，请先生成申报表!");
			}
			return map;
		} catch (Exception e) {
			log.error("查询报表数据异常",e);
			throw new BusinessException(e.getMessage());
		}		
	}
	
	
	/****
	 * 生成报表数据(所有页签)
	 * @param callInfo
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public SuperVO[] commonGenerateReportData(CallInfoVO callInfo) throws BusinessException {
		
	    try {
	    	if(isNeedApprove()){
				String status = getReportStatus(callInfo);
				if(status.equals(NssbReportUtil.approved)){
					throw new BusinessException("当前申报表已被审核，如需重新生成，请先取消审核！");
				}
	    	}

			CallInfoVO mergeCallInfo = mergeCallInfo(callInfo,callInfo.getTabCode());
			Map<String,Class> tabClassMap = mergeCallInfo.getTabCodeClassMap();
			SuperVO[] calvos = doCalculate(mergeCallInfo);
			String  currentTabcode = mergeCallInfo.getTabCode();
			List<String> nedTabcodes = new ArrayList<String>(tabClassMap.keySet());
			nedTabcodes.remove(currentTabcode);
			recursionCalcute(mergeCallInfo,nedTabcodes);			
			transFrom2ReportVos(mergeCallInfo,calvos);
			if(isNeedApprove()){
				saveNssbInfo(mergeCallInfo);
			}
			
			return calvos;
		} catch (Exception e) {
			log.error("加载数据发生异常",e);
			throw new BusinessException(e.getMessage());
		}	    
	}
	
	
	/****
	 * 递归计算页签中数据
	 * @param loopCallInfo
	 * @param nedTabcodes
	 */
	@SuppressWarnings("rawtypes")
	public void recursionCalcute(CallInfoVO loopCallInfo,List<String> nedTabcodes){
		
		Map<String, SuperVO[]> reportvos = loopCallInfo.getAllReportvos();
		Iterator<String> it = reportvos.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			if(reportvos.get(key)!=null&&reportvos.get(key).length>0&&nedTabcodes.contains(key)){
				nedTabcodes.remove(key);
			}			
		}
		while(nedTabcodes.size()>0){
					
	    	String pk_corp = loopCallInfo.getQueryParamVO().getPk_corp();
	    	CallInfoVO backCallInfo = CallInfoContext.getCallInfoByCode(nedTabcodes.get(0),pk_corp,loopCallInfo.getTabCodeClassMap());
	    	backCallInfo.setQueryParamVO(loopCallInfo.getQueryParamVO());
	    	backCallInfo.setOtherParam(loopCallInfo.getOtherParam());
	    	backCallInfo.setUseUserDataCalcute(loopCallInfo.isUseUserDataCalcute());
	    	backCallInfo.setTabCode(nedTabcodes.get(0));
	    	doCalculate(backCallInfo);		
			recursionCalcute(backCallInfo,nedTabcodes);
		}
	}
	
	
	/****
	 * 不保存的重新计算 （单个页签数据）
	 *    需要设置UseUserDataCalcute为true 并且把对应的vo放到CallInfoVO中
	 * @param callInfo
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public SuperVO[] commonReCalculate(CallInfoVO callInfo) throws BusinessException {
		
		try {
			callInfo.setUseUserDataCalcute(true);
			CallInfoVO mergeCallInfo = mergeCallInfo(callInfo,callInfo.getTabCode());
			SuperVO[] vos = getCalcuteVos(callInfo);
			mergeCallInfo.getAllReportvos().put(callInfo.getTabCode(), vos);
			SuperVO[] calvos = doCalculate(mergeCallInfo);
			transFrom2ReportVos(mergeCallInfo,calvos);
			
			return calvos;
		} catch (Exception e) {
			log.error("重新计算时发生异常",e);
			throw new BusinessException("重新计算时发生异常");
		}
	}
	
	
	/****
	 * 所有页签重新计算
	 * @param callInfo
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public SuperVO[] commonCalculateAll(CallInfoVO callInfo) throws BusinessException {
		try {
			callInfo.setUseUserDataCalcute(true);
			CallInfoVO mergeCallInfo = mergeCallInfo(callInfo,callInfo.getTabCode());
			SuperVO[] currentTabvos = queryOneTabData(mergeCallInfo);
			mergeCallInfo.getAllReportvos().clear();
			mergeCallInfo.getAllReportvos().put(callInfo.getTabCode(), currentTabvos);
			SuperVO[] calvos = doCalculate(mergeCallInfo);
			Map<String, Class> tabclassmap = mergeCallInfo.getTabCodeClassMap();
			Iterator<String> it = tabclassmap.keySet().iterator();
			while(it.hasNext()){
				String tabcode = it.next();
				Class clazz = tabclassmap.get(tabcode);
				if(mergeCallInfo.getTabCode().equals(tabcode)){
					continue;
				}
				Map<String, SuperVO[]> allvos = mergeCallInfo.getAllReportvos();
				if(allvos.containsKey(tabcode)){
					SuperVO[] tabvos = allvos.get(tabcode);
					if(tabvos!=null&&tabvos.length>0){
						continue;
					}
				}
				CallInfoVO notCalcuteCallInfo = mergeCallInfo(callInfo,tabcode);
				SuperVO[] notCalcuteTabvos = queryOneTabData(notCalcuteCallInfo);
				notCalcuteCallInfo.getAllReportvos().clear();
				notCalcuteCallInfo.getAllReportvos().put(tabcode, notCalcuteTabvos);
				doCalculate(notCalcuteCallInfo);
				Map<String, SuperVO[]> notaddvos = notCalcuteCallInfo.getAllReportvos();				
				mergeCallInfo.getAllReportvos().putAll(notaddvos);				
				
			}
			transFrom2ReportVos(mergeCallInfo,calvos);
			
			return calvos;
		} catch (Exception e) {
			log.error("全表计算时发生异常",e);
			throw new BusinessException("全表计算时发生异常");
		}
	}
	
	
	/****
	 * 更新单个页签报表数据
	 * @param callInfo
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public void commonUpdateSingleReport(CallInfoVO callInfo)
			throws BusinessException {
		
		try {
			callInfo.setUseUserDataCalcute(true);
			CallInfoVO mergeCallInfo = mergeCallInfo(callInfo,callInfo.getTabCode());
			SuperVO[] vos = getCalcuteVos(callInfo);
			mergeCallInfo.getAllReportvos().put(callInfo.getTabCode(), vos);
			SuperVO[] calvos = doCalculate(mergeCallInfo);
			
			String[] updateFieldnames = mergeCallInfo.getVariableFiledNames();
			
			int updatecount = getSbo().updateAry(calvos,updateFieldnames);
			log.info("成功更新"+updatecount+"行记录");
		} catch (Exception e) {
			log.error("更新数据时发生异常",e);
			throw new BusinessException("更新数据时发生异常");
		}
	}
	
	/****
	 * 审核
	 * @param callInfo
	 * @throws BusinessException
	 */
	public void commonApproveRpinfo(CallInfoVO callInfo) throws BusinessException {
		
		try {
			StNssbInfoVO oldvo = getNssbInfo(callInfo);
			if(oldvo==null){
				throw new BusinessException("没有需要审核的税表信息!");
			}
			oldvo.setCstatus(NssbReportUtil.approved);
			QueryParamVO param = callInfo.getQueryParamVO();
			oldvo.setApprovepsnid(param.getUserid());
			oldvo.setApprovetime(new DZFDateTime());
			
			getSbo().update(oldvo,new String[]{"cstatus","approvepsnid","approvetime"});
		} catch (Exception e) {
			log.error("审核数据时发生异常",e);
			throw new BusinessException("审核数据时发生异常");
		}		
	}
		
	
    /****
     * 反审核
     * @param callInfo
     * @throws BusinessException
     */
	public void commonUnaApproveRpinfo(CallInfoVO callInfo) throws BusinessException {
		
		try {
			StNssbInfoVO oldvo = getNssbInfo(callInfo);
			if(oldvo==null){
				throw new BusinessException("没有需要审核的税表信息!");
			}
			
			oldvo.setCstatus(NssbReportUtil.unapproved);
			QueryParamVO param = callInfo.getQueryParamVO();
			oldvo.setApprovepsnid(param.getUserid());
			oldvo.setApprovetime(new DZFDateTime());
			
			getSbo().update(oldvo,new String[]{"cstatus","approvepsnid","approvetime"});
		} catch (Exception e) {
			log.error("反审核数据时发生异常",e);
			throw new BusinessException("反审核数据时发生异常");
		}		
	}
	
	
	/****
	 * 获取需要的页签数据
	 * @param callInfo
	 * @param loadTabId
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,SuperVO[]> commondLoadReportVos(CallInfoVO callInfo,List<String> loadTabId)throws BusinessException{
		
		try {
			Map<String,SuperVO[]> map = new HashMap<String,SuperVO[]>();
			for(String tabid : loadTabId){
				CallInfoVO mergeCallInfo = mergeCallInfo(callInfo,tabid);
				SuperVO[] vos = queryOneTabData(mergeCallInfo);
				transFrom2ReportVos(mergeCallInfo,vos);
				map.put(tabid, vos);
			}

			return map;		
		} catch (Exception e) {
			log.error("加载数据时发生异常",e);
			throw new BusinessException("加载核数据时发生异常");
		}

	}
	
	
	/****
	 * 删除旧数据
	 * 子类需要扩展
	 * @param callInfos
	 * @param reportcode
	 */
	@SuppressWarnings("rawtypes")
	public void deleteOldDatas(CallInfoVO callInfo,String reportcode){
		
		CallInfoVO currentCalCallinfo =  getCurrentCalculateCallInfo(callInfo, reportcode);
		Class clazz = currentCalCallinfo.getClazz();
		SuperVO vo = null;
		try {
			vo = (SuperVO)clazz.newInstance();
		} catch (Exception e) {
           log.error("删除旧数据时实例化异常"+e.getMessage());
           throw new BusinessException("删除旧数据时实例化异常"+e.getMessage());
		}
		String tablename = vo.getTableName();
		String sql = new String(" delete from "+tablename+" where "+getCommonTabWhereSql());
		SQLParameter sp = getCommonTabWhereSqlParameter(callInfo);
		
		int ncount =getSbo().executeUpdate(sql, sp);
		log.info("成功删除"+ncount+"行数据");
	}

	
	/***
	 * 获取审核信息
	 * @param callInfo
	 * @return
	 */
	public  StNssbInfoVO getNssbInfo(CallInfoVO callInfo){
		
		String whq = getNssbWhereSql();
		SQLParameter sp = getNssbWhereSqlParameter(callInfo);
		
		StNssbInfoVO[] infvo=(StNssbInfoVO[])getSbo().queryByCondition(StNssbInfoVO.class, whq, sp);
		
		if(infvo!=null&&infvo.length>0){
			return infvo[0];
		}
		return null;
	}
	
	
	/****
	 * 获取一个页签的数据
	 * @param callInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public  SuperVO[] queryOneTabData(CallInfoVO callInfo){
		String sql  = getCommonTabWhereSql() + " " + getCommonTabOrderbySql();
		return getSbo().queryByCondition(callInfo.getClazz(), sql, getCommonTabWhereSqlParameter(callInfo));
	}
	
	
	/***
	 * 获取页签查询条件
	 * @return
	 */
	public abstract String getCommonTabWhereSql();
	
	/***
	 * 获取页签 order by
	 * @return
	 */
	public abstract String getCommonTabOrderbySql();
	
	
	/***
	 * 获取页签查询条件对应的参数
	 * @return
	 */
	public abstract SQLParameter getCommonTabWhereSqlParameter(CallInfoVO callInfo);
	
	
	/****
	 * 把从数据库中查询出来的数据转化为报表数据
	 * @param callInfo
	 * @param calvos
	 */
	@SuppressWarnings("rawtypes")
	public abstract void transFrom2ReportVos(CallInfoVO callInfo,SuperVO[] calvos);
	
	
	/****
	 * 获取审核状态
	 * @param callInfo
	 * @return
	 */
	public  String getReportStatus(CallInfoVO callInfo){
		String sql = "select nvl(cstatus,'0') from ynt_st_nssbinfo where "+getNssbWhereSql();
		SQLParameter sp = getNssbWhereSqlParameter(callInfo);
		String status =(String)getSbo().executeQuery(sql, sp, new ColumnProcessor());
		
		if(status==null){
			return NssbReportUtil.unapproved;
		}
		return status;
	}
	
	
	/****
	 * 保存审核信息
	 * @param callInfo
	 */
	public abstract void saveNssbInfo(CallInfoVO callInfo);
	
	/****
	 * 从报表字段值设置到计算字段的值
	 */
	@SuppressWarnings("rawtypes")
	public abstract SuperVO[] getCalcuteVos(CallInfoVO callInfo);
	
	/****
	 * 获取审核查询条件
	 * @return
	 */
	public abstract String getNssbWhereSql();
	
	
	/***
	 * 获取审核查询条件对应的查询参数
	 * @param callInfo
	 * @return
	 */
	public abstract SQLParameter getNssbWhereSqlParameter(CallInfoVO callInfo);
	
	
	/***
	 * 是否需要审核
	 * @return
	 */
	public boolean isNeedApprove(){
		
		return true;
	}
	
	
	/****
	 * 获取新编码规则下的编码
	 * @param pk_corp
	 * @param oldCode
	 * @return
	 */
	public String getCurrentCode(String pk_corp,String oldCode){
		
		String currrentrule = cpaccountService.queryAccountRule(pk_corp);
	    if(DZFConstant.ACCOUNTCODERULE.trim().equals(currrentrule.trim())){
	    	return oldCode;
	    }
	    
		return cpaccountCodeRuleService.getNewRuleCode(oldCode, DZFConstant.ACCOUNTCODERULE, currrentrule);
	}


	public ICpaccountCodeRuleService getCpaccountCodeRuleService() {
		return cpaccountCodeRuleService;
	}


	public void setCpaccountCodeRuleService(
			ICpaccountCodeRuleService cpaccountCodeRuleService) {
		this.cpaccountCodeRuleService = cpaccountCodeRuleService;
	}


	public ICpaccountService getCpaccountService() {
		return cpaccountService;
	}


	public void setCpaccountService(ICpaccountService cpaccountService) {
		this.cpaccountService = cpaccountService;
	}
	
	
	
	
}
