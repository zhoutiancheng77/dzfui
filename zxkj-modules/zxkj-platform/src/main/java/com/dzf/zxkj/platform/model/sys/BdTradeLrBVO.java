package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 集团利润结转模板 表体VO
 * 
 */
public class BdTradeLrBVO extends SuperVO {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DZFDateTime ts;
	@JsonProperty("pk_accsubj")
	private String pk_transferoutaccount;
	private String memo;
	@JsonProperty("pk_model_b")
	private String pk_trade_transtemplate_b;
	private String coperatorid;
	@JsonProperty("zy")
	private String abstracts;
	private Integer dr;
	private DZFDate doperatedate;
	@JsonProperty("id")
	private String pk_trade_transtemplate_h;
	@JsonProperty("kmbm")
	private String accountcode;//科目编码
	private Integer direction;
	
	private String pk_corp;
	@JsonProperty("kmmc")
	private String accname;
	
	public String getAccname() {
		return accname;
	}
	public void setAccname(String accname) {
		this.accname = accname;
	}

	public static final String PK_TRANSFEROUTACCOUNT = "pk_transferoutaccount";
	public static final String MEMO = "memo";
	public static final String PK_TRADE_TRANSTEMPLATE_B = "pk_trade_transtemplate_b";
	public static final String COPERATORID = "coperatorid";
	public static final String ABSTRACTS = "abstracts";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String PK_TRADE_TRANSTEMPLATE_H = "pk_trade_transtemplate_h";
			

	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	
	public Integer getDirection() {
		return direction;
	}
	public void setDirection(Integer direction) {
		this.direction = direction;
	}
	public String getAccountcode() {
		return accountcode;
	}
	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性pk_transferoutaccount的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return String
	 */
	public String getPk_transferoutaccount () {
		return pk_transferoutaccount;
	}   
	/**
	 * 属性pk_transferoutaccount的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newPk_transferoutaccount String
	 */
	public void setPk_transferoutaccount (String newPk_transferoutaccount ) {
	 	this.pk_transferoutaccount = newPk_transferoutaccount;
	} 	  
	/**
	 * 属性memo的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return String
	 */
	public String getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newMemo String
	 */
	public void setMemo (String newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * 属性pk_trade_transtemplate_b的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return String
	 */
	public String getPk_trade_transtemplate_b () {
		return pk_trade_transtemplate_b;
	}   
	/**
	 * 属性pk_trade_transtemplate_b的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newPk_trade_transtemplate_b String
	 */
	public void setPk_trade_transtemplate_b (String newPk_trade_transtemplate_b ) {
	 	this.pk_trade_transtemplate_b = newPk_trade_transtemplate_b;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性abstracts的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return String
	 */
	public String getAbstracts () {
		return abstracts;
	}   
	/**
	 * 属性abstracts的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newAbstracts String
	 */
	public void setAbstracts (String newAbstracts ) {
	 	this.abstracts = newAbstracts;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性pk_trade_transtemplate_h的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return String
	 */
	public String getPk_trade_transtemplate_h () {
		return pk_trade_transtemplate_h;
	}   
	/**
	 * 属性pk_trade_transtemplate_h的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newPk_trade_transtemplate_h String
	 */
	public void setPk_trade_transtemplate_h (String newPk_trade_transtemplate_h ) {
	 	this.pk_trade_transtemplate_h = newPk_trade_transtemplate_h;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-09-15 17:31:07
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
		return "pk_trade_transtemplate_h";
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-09-15 17:31:07
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_trade_transtemplate_b";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-09-15 17:31:07
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tdlrmb_b";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-09-15 17:31:07
	  */
     public BdTradeLrBVO() {
		super();	
	}    
} 
