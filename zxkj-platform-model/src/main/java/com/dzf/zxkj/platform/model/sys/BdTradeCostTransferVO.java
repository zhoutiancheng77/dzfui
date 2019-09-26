package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     行业成本结转模板VO
 * </p>
 * 创建日期:2014-11-04 14:33:11
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class BdTradeCostTransferVO extends SuperVO {
	@JsonProperty("jzbl")
	private DZFDouble transratio;
	private DZFDateTime ts;
	@JsonProperty("qskmid")
	private String pk_fillaccount;
	@JsonProperty("cpsn")
	private String coperatorid;
	@JsonProperty("id")
	private String pk_trade_costtransfer;
	@JsonProperty("zy")
	private String abstracts;
	private Integer dr;
	@JsonProperty("dopd")
	private DZFDate doperatedate;
	@JsonProperty("dfkmid")
	private String pk_creditaccount;
	@JsonProperty("hykmid")
	private String pk_trade_accountschema;
	@JsonProperty("jfkmid")
	private String pk_debitaccount;
	@JsonProperty("gsid")
	private String pk_corp;
	
	private String dfkmmc;
	private String jfkmmc;
	private String qskmmc;
	
	public static final String TRANSRATIO = "transratio";
	public static final String PK_FILLACCOUNT = "pk_fillaccount";
	public static final String COPERATORID = "coperatorid";
	public static final String ABSTRACTS = "abstracts";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String PK_CREDITACCOUNT = "pk_creditaccount";
	public static final String PK_TRADE_ACCOUNTSCHEMA = "pk_trade_accountschema";
	public static final String PK_DEBITACCOUNT = "pk_debitaccount";
	
	
	
	public String getDfkmmc() {
		return dfkmmc;
	}
	public void setDfkmmc(String dfkmmc) {
		this.dfkmmc = dfkmmc;
	}
	public String getJfkmmc() {
		return jfkmmc;
	}
	public void setJfkmmc(String jfkmmc) {
		this.jfkmmc = jfkmmc;
	}
	public String getQskmmc() {
		return qskmmc;
	}
	public void setQskmmc(String qskmmc) {
		this.qskmmc = qskmmc;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}		
	/**
	 * 属性transratio的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return UFDouble
	 */
	public DZFDouble getTransratio () {
		return transratio;
	}   
	/**
	 * 属性transratio的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newTransratio UFDouble
	 */
	public void setTransratio (DZFDouble newTransratio ) {
	 	this.transratio = newTransratio;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return UFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newTs UFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性pk_fillaccount的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return String
	 */
	public String getPk_fillaccount () {
		return pk_fillaccount;
	}   
	/**
	 * 属性pk_fillaccount的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newPk_fillaccount String
	 */
	public void setPk_fillaccount (String newPk_fillaccount ) {
	 	this.pk_fillaccount = newPk_fillaccount;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性abstracts的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return String
	 */
	public String getAbstracts () {
		return abstracts;
	}   
	/**
	 * 属性abstracts的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newAbstracts String
	 */
	public void setAbstracts (String newAbstracts ) {
	 	this.abstracts = newAbstracts;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return UFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newDoperatedate UFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性pk_creditaccount的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return String
	 */
	public String getPk_creditaccount () {
		return pk_creditaccount;
	}   
	/**
	 * 属性pk_creditaccount的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newPk_creditaccount String
	 */
	public void setPk_creditaccount (String newPk_creditaccount ) {
	 	this.pk_creditaccount = newPk_creditaccount;
	} 	  
	/**
	 * 属性pk_trade_accountschema的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return String
	 */
	public String getPk_trade_accountschema () {
		return pk_trade_accountschema;
	}   
	/**
	 * 属性pk_trade_accountschema的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newPk_trade_accountschema String
	 */
	public void setPk_trade_accountschema (String newPk_trade_accountschema ) {
	 	this.pk_trade_accountschema = newPk_trade_accountschema;
	} 	  
	/**
	 * 属性pk_debitaccount的Getter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @return String
	 */
	public String getPk_debitaccount () {
		return pk_debitaccount;
	}   
	/**
	 * 属性pk_debitaccount的Setter方法.
	 * 创建日期:2014-11-04 14:33:11
	 * @param newPk_debitaccount String
	 */
	public void setPk_debitaccount (String newPk_debitaccount ) {
	 	this.pk_debitaccount = newPk_debitaccount;
	} 	  
 
	public String getPk_trade_costtransfer() {
		return pk_trade_costtransfer;
	}
	public void setPk_trade_costtransfer(String pk_trade_costtransfer) {
		this.pk_trade_costtransfer = pk_trade_costtransfer;
	}
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-11-04 14:33:11
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-11-04 14:33:11
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_trade_costtransfer";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-11-04 14:33:11
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tdcosttrans";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-11-04 14:33:11
	  */
     public BdTradeCostTransferVO() {
		super();	
	}    
} 
