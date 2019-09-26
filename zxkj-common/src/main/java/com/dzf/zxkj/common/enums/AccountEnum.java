package com.dzf.zxkj.common.enums;

/****
 * 会计核算方向
 * @author asoka
 *
 */
public enum AccountEnum {
	
	DEBIT("JF","借方"),
	CREDIT("DF","贷方"),
	END_YEAR_DEBIT("NMJF","年末借方"),
	END_YEAR_CREDIT("NMDF","年末贷方"),
	CURRENT_MONTH_DEBIT("BYJF","本月借方"),
	CURRENT_MONTH_CREDIT("BYDF","本月贷方"),
	YEAR_TOTAL_DEBIT("BNLJJF","本年累计借方"),
	YEAR_TOTAL_CREDIT("BNLJDF","本年累计贷方"),
	STANDARD_2007("2007","2007会计准则"),
	STANDARD_2013("2013","2013会计准则");
	
	
	
	private String code;
	private String msg;
	private AccountEnum(String code,String msg){
		this.code = code;
		this.msg = msg;
	}
	public String getCode(){
		return code;
	}
	public String getMsg(){
		return msg;
	}
	
	

}
