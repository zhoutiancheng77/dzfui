package com.dzf.zxkj.platform.model.st;


import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.enums.AccountEnum;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/****
 * 报表调用信息上下文
 * @author asoka
 *
 */
@Slf4j
public class CallInfoContext {

	private static IYntBoPubUtil pubutil;

	//static Map<String,CallInfoVO> callInfoMap = new ConcurrentHashMap<String,CallInfoVO>();
	
	
   /***
    * 获取报表调用信息
    * @param code  页签编码
    * @pk_corp
    * @return
    */
   @SuppressWarnings("rawtypes")
   public static CallInfoVO getCallInfoByCode(String code, String pk_corp, Map<String,Class> tabCodeClassMap){
	  
	   String key = code + "-" + pk_corp;
//	   if(callInfoMap.containsKey(key)&&callInfoMap.get(key)!=null){
//		   return callInfoMap.get(key);
//	   }
//	   
       if(!tabCodeClassMap.containsKey(code)){
    	   log.error("没有包含"+key+"编码的信息");
    	   return null;
       }
	   Class clazz = tabCodeClassMap.get(code);
       if(clazz==null){
    	   log.error(code+"对应的class对象为空");
       }
       Object callobj = null;
       try {
    	   callobj =  clazz.newInstance();
		} catch (Exception e) {
			log.error("实例化异常"+e.getMessage(),e);
		} 
       
       if(callobj  instanceof ReportCallInfo){
    	   ReportCallInfo reportcallinfo = (ReportCallInfo)callobj;
    	   CallInfoVO callInfo = new CallInfoVO();
    	   callInfo.setClazz(clazz);
	   		if(getPubutil().is2007AccountSchema(pk_corp)){
				callInfo.setAccountStandard(AccountEnum.STANDARD_2007.getCode());
			}else{
				callInfo.setAccountStandard(AccountEnum.STANDARD_2013.getCode());
			}
    	   callInfo.setConstantFieldNames(reportcallinfo.getConstantFieldNames());
    	   callInfo.setConstantFieldValues(reportcallinfo.getConstantFieldValues());
    	   callInfo.setCellformulas(reportcallinfo.getCellformulas(callInfo.getAccountStandard()));
    	   callInfo.setColformulas(reportcallinfo.getColformulas());
    	   callInfo.setRowformulas(reportcallinfo.getRowformulas());
    	   callInfo.setVariableFiledNames(reportcallinfo.getVariableFiledNames());
    	   callInfo.setTabCode(code);
    	   callInfo.setTabCodeClassMap(tabCodeClassMap);
    	  // callInfoMap.put(key, callInfo);
    	   
    	   return callInfo;
       }
       
	   return null;	   
   }
   
	public static IYntBoPubUtil getPubutil() {
		if(pubutil==null){
			pubutil=(IYntBoPubUtil) SpringUtils.getBean("yntBoPubUtil");
		}
		return pubutil;
	}
}
