package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2014-10-20 17:07:46
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class BdtradecashflowVO extends SuperVO {
	@JsonProperty("extime")
	private DZFDateTime ts;
	@JsonProperty("xjlxmmc")
	private String itemname;
	@JsonProperty("cpsn")
	private String coperatorid;
	private Integer dr;
	@JsonProperty("dopdate")
	private DZFDate doperatedate;
	@JsonProperty("id")
	private String pk_trade_cashflow;
	private Integer category;
	@JsonProperty("fx")
	private Integer direction;
	@JsonProperty("xjlxmbm")
	private String itemcode;
	@JsonProperty("accscma")
	private String pk_trade_accountschema;
	@JsonProperty("gsid")
	private String pk_corp;
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}		
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性itemname的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return String
	 */
	public String getItemname () {
		return itemname;
	}   
	/**
	 * 属性itemname的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newItemname String
	 */
	public void setItemname (String newItemname ) {
	 	this.itemname = newItemname;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性pk_trade_cashflow的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return String
	 */
	public String getPk_trade_cashflow () {
		return pk_trade_cashflow;
	}   
	/**
	 * 属性pk_trade_cashflow的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newPk_trade_cashflow String
	 */
	public void setPk_trade_cashflow (String newPk_trade_cashflow ) {
	 	this.pk_trade_cashflow = newPk_trade_cashflow;
	} 	  
	/**
	 * 属性category的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return UFDouble
	 */
	public Integer getCategory () {
		return category;
	}   
	/**
	 * 属性category的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newCategory UFDouble
	 */
	public void setCategory (Integer newCategory ) {
	 	this.category = newCategory;
	} 	  
	/**
	 * 属性direction的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return UFDouble
	 */
	public Integer getDirection () {
		return direction;
	}   
	/**
	 * 属性direction的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newDirection UFDouble
	 */
	public void setDirection (Integer newDirection ) {
	 	this.direction = newDirection;
	} 	  
	/**
	 * 属性itemcode的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return String
	 */
	public String getItemcode () {
		return itemcode;
	}   
	/**
	 * 属性itemcode的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newItemcode String
	 */
	public void setItemcode (String newItemcode ) {
	 	this.itemcode = newItemcode;
	} 	  
	/**
	 * 属性pk_trade_accountschema的Getter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @return String
	 */
	public String getPk_trade_accountschema () {
		return pk_trade_accountschema;
	}   
	/**
	 * 属性pk_trade_accountschema的Setter方法.
	 * 创建日期:2014-10-20 17:07:46
	 * @param newPk_trade_accountschema String
	 */
	public void setPk_trade_accountschema (String newPk_trade_accountschema ) {
	 	this.pk_trade_accountschema = newPk_trade_accountschema;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-10-20 17:07:46
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-10-20 17:07:46
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_trade_cashflow";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-10-20 17:07:46
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tdcashflow";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-10-20 17:07:46
	  */
     public BdtradecashflowVO() {
		super();	
	}    
} 
