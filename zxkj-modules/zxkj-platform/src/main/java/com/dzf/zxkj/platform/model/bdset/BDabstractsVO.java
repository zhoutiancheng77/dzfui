package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  摘要
 */

public class BDabstractsVO extends SuperVO {

	private static final long serialVersionUID = -7876796564904417680L;
	
	private String mnemonic;
	private DZFDateTime ts;
	@JsonProperty("memo")
	private String memo;
	@JsonProperty("pabsid")
	private String pk_abstracts;
	@JsonProperty("cpid")
	private String coperatorid;
	private Integer dr;
	@JsonProperty("date")
	private DZFDate doperatedate;
	@JsonProperty("name")
	private String abstractsname;
	@JsonProperty("code")
	private String abstractscode;
	@JsonProperty("corp")
	private String pk_corp;

	public static final String MNEMONIC = "mnemonic";
	public static final String MEMO = "memo";
	public static final String PK_ABSTRACTS = "pk_abstracts";
	public static final String COPERATORID = "coperatorid";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String ABSTRACTSNAME = "abstractsname";
	public static final String ABSTRACTSCODE = "abstractscode";
	/**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-09-15 17:24:34
	  */
    public BDabstractsVO() {
		super();	
	}   		
	/**
	 * 属性mnemonic的Getter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @return String
	 */
	public String getMnemonic () {
		return mnemonic;
	}   
	/**
	 * 属性mnemonic的Setter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @param newMnemonic String
	 */
	public void setMnemonic (String newMnemonic ) {
	 	this.mnemonic = newMnemonic;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性memo的Getter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @return String
	 */
	public String getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @param newMemo String
	 */
	public void setMemo (String newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * 属性pk_abstracts的Getter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @return String
	 */
	public String getPk_abstracts () {
		return pk_abstracts;
	}   
	/**
	 * 属性pk_abstracts的Setter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @param newPk_abstracts String
	 */
	public void setPk_abstracts (String newPk_abstracts ) {
	 	this.pk_abstracts = newPk_abstracts;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性abstractsname的Getter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @return String
	 */
	public String getAbstractsname () {
		return abstractsname;
	}   
	/**
	 * 属性abstractsname的Setter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @param newAbstractsname String
	 */
	public void setAbstractsname (String newAbstractsname ) {
	 	this.abstractsname = newAbstractsname;
	} 	  
	/**
	 * 属性abstractscode的Getter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @return String
	 */
	public String getAbstractscode () {
		return abstractscode;
	}   
	/**
	 * 属性abstractscode的Setter方法.
	 * 创建日期:2014-09-15 17:24:34
	 * @param newAbstractscode String
	 */
	public void setAbstractscode (String newAbstractscode ) {
	 	this.abstractscode = newAbstractscode;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-09-15 17:24:34
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-09-15 17:24:34
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_abstracts";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-09-15 17:24:34
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_bd_abstracts";
	}    
    
    public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
 
} 
