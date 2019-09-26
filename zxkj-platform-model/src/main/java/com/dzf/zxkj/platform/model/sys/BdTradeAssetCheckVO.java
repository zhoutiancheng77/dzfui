package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     行业资产与总账对照表VO
 * </p>
 * 创建日期:2014-11-07 11:22:28
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class BdTradeAssetCheckVO extends SuperVO {
	private DZFDateTime ts;
	@JsonProperty("cpsn")
	private String coperatorid;
	private Integer dr;
	@JsonProperty("accountcode")
	private String accountcode;
	@JsonProperty("dopd")
	private DZFDate doperatedate;
	@JsonProperty("zzkmid")
	private String pk_glaccount;
	@JsonProperty("zcsx")
	private Integer assetproperty;
	@JsonProperty("hykmid")
	private String pk_trade_accountschema;
	@JsonProperty("id")
	private String pk_trade_assetcheck;
	@JsonProperty("zclbid")
	private String pk_assetcategory;
	@JsonProperty("zckm")
	private Integer assetaccount;
	@JsonProperty("gsid")
	private String pk_corp;
	
	private String zclbmc;
	private String zzkmmc;
	
	@JsonProperty("zjfykmid")
	private String pk_zjfykm;//折旧费用科目
	private String zjfykmcode;//(不存库)
	private String zjfykmmc;//折旧费用名称(不存库)
	@JsonProperty("jskmid")
	private String pk_jskm;//结算科目
	private String jskmmc;//结算科目名称(不存库)
	private String jskmcode;//结算编码(不存库)
	private String zzkmcode;//资产科目编码
	@JsonProperty("zjfs")
	private Integer  zjtype;//折旧方式
	@JsonProperty("czl")
	private DZFDouble salvageratio;//残值率
	
	public static final String COPERATORID = "coperatorid";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String PK_GLACCOUNT = "pk_glaccount";
	public static final String ASSETPROPERTY = "assetproperty";
	public static final String PK_TRADE_ACCOUNTSCHEMA = "pk_trade_accountschema";
	public static final String PK_TRADE_ASSETCHECK = "pk_trade_assetcheck";
	public static final String PK_ASSETCATEGORY = "pk_assetcategory";
	public static final String ASSETACCOUNT = "assetaccount";

	
	
	public String getZclbmc() {
		return zclbmc;
	}
	public void setZclbmc(String zclbmc) {
		this.zclbmc = zclbmc;
	}
	public String getZzkmmc() {
		return zzkmmc;
	}
	public void setZzkmmc(String zzkmmc) {
		this.zzkmmc = zzkmmc;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}		
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return UFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newTs UFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return UFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newDoperatedate UFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性pk_glaccount的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return String
	 */
	public String getPk_glaccount () {
		return pk_glaccount;
	}   
	/**
	 * 属性pk_glaccount的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newPk_glaccount String
	 */
	public void setPk_glaccount (String newPk_glaccount ) {
	 	this.pk_glaccount = newPk_glaccount;
	} 	  
	/**
	 * 属性assetproperty的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return UFDouble
	 */
	public Integer getAssetproperty () {
		return assetproperty;
	}   
	/**
	 * 属性assetproperty的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newAssetproperty UFDouble
	 */
	public void setAssetproperty (Integer newAssetproperty ) {
	 	this.assetproperty = newAssetproperty;
	} 	  
	/**
	 * 属性pk_trade_accountschema的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return String
	 */
	public String getPk_trade_accountschema () {
		return pk_trade_accountschema;
	}   
	/**
	 * 属性pk_trade_accountschema的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newPk_trade_accountschema String
	 */
	public void setPk_trade_accountschema (String newPk_trade_accountschema ) {
	 	this.pk_trade_accountschema = newPk_trade_accountschema;
	} 	  
	/**
	 * 属性pk_trade_assetcheck的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return String
	 */
	public String getPk_trade_assetcheck () {
		return pk_trade_assetcheck;
	}   
	/**
	 * 属性pk_trade_assetcheck的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newPk_trade_assetcheck String
	 */
	public void setPk_trade_assetcheck (String newPk_trade_assetcheck ) {
	 	this.pk_trade_assetcheck = newPk_trade_assetcheck;
	} 	  
	/**
	 * 属性pk_assetcategory的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return String
	 */
	public String getPk_assetcategory () {
		return pk_assetcategory;
	}   
	/**
	 * 属性pk_assetcategory的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newPk_assetcategory String
	 */
	public void setPk_assetcategory (String newPk_assetcategory ) {
	 	this.pk_assetcategory = newPk_assetcategory;
	} 	  
	/**
	 * 属性assetaccount的Getter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @return UFDouble
	 */
	public Integer getAssetaccount () {
		return assetaccount;
	}   
	/**
	 * 属性assetaccount的Setter方法.
	 * 创建日期:2014-11-07 11:22:28
	 * @param newAssetaccount UFDouble
	 */
	public void setAssetaccount (Integer newAssetaccount ) {
	 	this.assetaccount = newAssetaccount;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-11-07 11:22:28
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-11-07 11:22:28
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_trade_assetcheck";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-11-07 11:22:28
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tdcheck";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-11-07 11:22:28
	  */
     public BdTradeAssetCheckVO() {
		super();	
	}
	public String getAccountcode() {
		return accountcode;
	}
	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}
	public String getPk_zjfykm() {
		return pk_zjfykm;
	}
	public void setPk_zjfykm(String pk_zjfykm) {
		this.pk_zjfykm = pk_zjfykm;
	}
	public String getZjfykmcode() {
		return zjfykmcode;
	}
	public void setZjfykmcode(String zjfykmcode) {
		this.zjfykmcode = zjfykmcode;
	}
	public String getZjfykmmc() {
		return zjfykmmc;
	}
	public void setZjfykmmc(String zjfykmmc) {
		this.zjfykmmc = zjfykmmc;
	}
	public String getPk_jskm() {
		return pk_jskm;
	}
	public void setPk_jskm(String pk_jskm) {
		this.pk_jskm = pk_jskm;
	}
	public String getJskmmc() {
		return jskmmc;
	}
	public void setJskmmc(String jskmmc) {
		this.jskmmc = jskmmc;
	}
	public String getJskmcode() {
		return jskmcode;
	}
	public void setJskmcode(String jskmcode) {
		this.jskmcode = jskmcode;
	}
	public String getZzkmcode() {
		return zzkmcode;
	}
	public void setZzkmcode(String zzkmcode) {
		this.zzkmcode = zzkmcode;
	}
	public Integer getZjtype() {
		return zjtype;
	}
	public void setZjtype(Integer zjtype) {
		this.zjtype = zjtype;
	}
	public DZFDouble getSalvageratio() {
		return salvageratio;
	}
	public void setSalvageratio(DZFDouble salvageratio) {
		this.salvageratio = salvageratio;
	}  
	
	
} 
