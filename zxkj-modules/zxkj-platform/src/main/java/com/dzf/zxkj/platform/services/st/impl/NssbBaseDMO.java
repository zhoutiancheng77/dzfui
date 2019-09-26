package com.dzf.zxkj.platform.services.st.impl;

import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.services.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class NssbBaseDMO {
	
	public abstract DZFDouble getReportItemValue(String itemstr);
	
	@Autowired
	ICpaccountCodeRuleService cpaccountCodeRuleService;
	
	@Autowired
	ICpaccountService cpaccountService;
	
	
	/****
	 * 获取新编码规则下的编码
	 * @param pk_corp
	 * @param oldCode
	 * @return
	 */
	public String getCurrentCode(String pk_corp,String oldCode){
		
		String currrentrule = getCpaccountService().queryAccountRule(pk_corp);
	    if(DZFConstant.ACCOUNTCODERULE.trim().equals(currrentrule.trim())){
	    	return oldCode;
	    }
	    
		return getCpaccountCodeRuleService().getNewRuleCode(oldCode, DZFConstant.ACCOUNTCODERULE, currrentrule);
	}


	public ICpaccountCodeRuleService getCpaccountCodeRuleService() {
		if(cpaccountCodeRuleService==null){
			cpaccountCodeRuleService =(ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");
		}
		return cpaccountCodeRuleService;
	}


	public void setCpaccountCodeRuleService(
			ICpaccountCodeRuleService cpaccountCodeRuleService) {
		this.cpaccountCodeRuleService = cpaccountCodeRuleService;
	}


	public ICpaccountService getCpaccountService() {
		
		if(cpaccountService==null){
			cpaccountService = (ICpaccountService)SpringUtils.getBean("gl_cpacckmserv");
		}
		return cpaccountService;
	}


	public void setCpaccountService(ICpaccountService cpaccountService) {
		this.cpaccountService = cpaccountService;
	}
	
	
	

}
