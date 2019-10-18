package com.dzf.zxkj.platform.service.gzgl.impl;

import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 工资密文处理
 * @author zhangj
 *
 */
@Slf4j
public class SalaryCipherHandler {

	public static String[] en_number_columns = new String[] { "yfgz_en", "yanglaobx_en", "yiliaobx_en", "shiyebx_en", "zfgjj_en",
			"ynssde_en", "shuilv_en", "grsds_en", "sfgz_en" , "znjyzc_en", "jxjyzc_en", "zfdkzc_en", "zfzjzc_en", "sylrzc_en", 
			"ljsre_en", "ljznjyzc_en", "ljjxjyzc_en", "ljzfdkzc_en", "ljzfzjzc_en", "ljsylrzc_en", "ljynse_en", "yyjse_en","ljjcfy_en","ljzxkc_en",
			"qyyanglaobx_en","qyyiliaobx_en","qyshiyebx_en","qyzfgjj_en","qygsbx_en","qyshybx_en"};
	
	public static String[] en_str_columns = new String[]{
			"zjbm_en","ygname_en"
	};
	
	/**
	 * 加密，解密处理vo
	 * @param salaryvos
	 * @param model 0 加密，1解密
	 */

	public static void handlerSalaryVO(SalaryReportVO[] salaryvos, Integer model){
		if(salaryvos == null ||  salaryvos.length ==0){
			return ;
		}
		if(model == null){
			return;
		}
		
		try {
			if(model == 0){
				entryVo(salaryvos);
			}else{
				detryvo(salaryvos);
			}
		} catch (Exception e) {
			log.error("解密失败!",e);
		}
	}


	private static void detryvo(SalaryReportVO[] salaryvos) throws Exception {
		String columntemp = "";//解密字段
		
	    Object objtemp = null;
	    DesUtils desutil = new DesUtils();
		for(SalaryReportVO vo:salaryvos){
			
			//数字类型
			for(String str:en_number_columns){
				
				columntemp = str.split("_")[0];
				
				objtemp= vo.getAttributeValue(str);
				
				if (objtemp != null) {
					
					objtemp = desutil.decrypt(objtemp.toString());
					
					vo.setAttributeValue(columntemp, new DZFDouble(objtemp.toString()));// 加密字段

					// 原有字段清空
					vo.setAttributeValue(str, null);
				}
				
			}
			
			//字符串类型
			for(String str:en_str_columns){

				columntemp = str.split("_")[0];
				
				objtemp= vo.getAttributeValue(str);
				
				if (objtemp != null) {

					objtemp = desutil.decrypt(objtemp.toString());
					
					
					vo.setAttributeValue(columntemp,objtemp.toString());// 加密字段

					// 原有字段清空
					vo.setAttributeValue(str, null);
				}
			}
		}
	}


	private static void entryVo(SalaryReportVO[] salaryvos) throws Exception {
		String columntemp = "";
		
	    Object objtemp = null;
	    
	    String[] rescolumns =  ArrayUtils.addAll(en_number_columns, en_str_columns);
		
	    DesUtils desutil = new DesUtils();
	    
		for(SalaryReportVO vo:salaryvos){
			
			for(String str:rescolumns){
				columntemp = str.split("_")[0];
				
				objtemp= vo.getAttributeValue(columntemp);
				
				if(objtemp!=null){
					vo.setAttributeValue(str, desutil.encrypt(String.valueOf(objtemp)));//加密字段
					//原有字段清空
					vo.setAttributeValue(columntemp, null);
				}
				
			}
		}
	}
	
}
