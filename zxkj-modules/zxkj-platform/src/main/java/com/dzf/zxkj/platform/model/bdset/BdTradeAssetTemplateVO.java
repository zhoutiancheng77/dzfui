package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     行业固定资产凭证模板HVO
 * </p>
 * 创建日期:2014-11-04 13:41:55
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class BdTradeAssetTemplateVO extends SuperVO {
	@JsonProperty("mblx")
	private Integer tempkind;
	private DZFDateTime ts;
	@JsonProperty("bz")
	private String memo;
	@JsonProperty("cpsn")
	private String coperatorid;
	private Integer dr;
	@JsonProperty("dopd")
	private DZFDate doperatedate;
	@JsonProperty("zcsx")
	private Integer assetproperty;
	@JsonProperty("hykmid")
	private String pk_trade_accountschema;
	@JsonProperty("zclbid")
	private String pk_assetcategory;
	@JsonProperty("id")
	private String pk_trade_assettemplate;
	@JsonProperty("gsid")
	private String pk_corp;

	public static final String TEMPKIND = "tempkind";
	public static final String MEMO = "memo";
	public static final String COPERATORID = "coperatorid";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String ASSETPROPERTY = "assetproperty";
	public static final String PK_TRADE_ACCOUNTSCHEMA = "pk_trade_accountschema";
	public static final String PK_ASSETCATEGORY = "pk_assetcategory";
	public static final String PK_TRADE_ASSETTEMPLATE = "pk_trade_assettemplate";
			
	
	private String prjname;
	private String zclbmc;
	
	
	
	public String getPrjname() {
		return prjname;
	}
	public void setPrjname(String prjname) {
		this.prjname = prjname;
	}
	public String getZclbmc() {
		return zclbmc;
	}
	public void setZclbmc(String zclbmc) {
		this.zclbmc = zclbmc;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	/**
	 * 属性tempkind的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return UFDouble
	 */
	public Integer getTempkind () {
		return tempkind;
	}   
	/**
	 * 属性tempkind的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newTempkind UFDouble
	 */
	public void setTempkind (Integer newTempkind ) {
	 	this.tempkind = newTempkind;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return UFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newTs UFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性memo的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return String
	 */
	public String getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newMemo String
	 */
	public void setMemo (String newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return UFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newDoperatedate UFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性assetproperty的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return UFDouble
	 */
	public Integer getAssetproperty () {
		return assetproperty;
	}   
	/**
	 * 属性assetproperty的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newAssetproperty UFDouble
	 */
	public void setAssetproperty (Integer newAssetproperty ) {
	 	this.assetproperty = newAssetproperty;
	} 	  
	/**
	 * 属性pk_trade_accountschema的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return String
	 */
	public String getPk_trade_accountschema () {
		return pk_trade_accountschema;
	}   
	/**
	 * 属性pk_trade_accountschema的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newPk_trade_accountschema String
	 */
	public void setPk_trade_accountschema (String newPk_trade_accountschema ) {
	 	this.pk_trade_accountschema = newPk_trade_accountschema;
	} 	  
	/**
	 * 属性pk_assetcategory的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return String
	 */
	public String getPk_assetcategory () {
		return pk_assetcategory;
	}   
	/**
	 * 属性pk_assetcategory的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newPk_assetcategory String
	 */
	public void setPk_assetcategory (String newPk_assetcategory ) {
	 	this.pk_assetcategory = newPk_assetcategory;
	} 	  
	/**
	 * 属性pk_trade_assettemplate的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return String
	 */
	public String getPk_trade_assettemplate () {
		return pk_trade_assettemplate;
	}   
	/**
	 * 属性pk_trade_assettemplate的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newPk_trade_assettemplate String
	 */
	public void setPk_trade_assettemplate (String newPk_trade_assettemplate ) {
	 	this.pk_trade_assettemplate = newPk_trade_assettemplate;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-11-04 13:41:55
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-11-04 13:41:55
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_trade_assettemplate";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-11-04 13:41:55
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tdmb";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-11-04 13:41:55
	  */
     public BdTradeAssetTemplateVO() {
		super();	
	}    
} 
