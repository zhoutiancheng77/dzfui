package com.dzf.zxkj.platform.model.zcgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class AssetCleanVO extends SuperVO {
	@JsonProperty("dap_date")
	private DZFDate dapprovedate;
	@JsonProperty("corp_id")
	private String pk_corp;
	@JsonProperty("tms")
	private DZFDateTime ts;
	@JsonProperty("vd9")
	private String vdef9;
	@JsonProperty("copid")
	private String coperatorid;
	@JsonProperty("vd1")
	private String vdef1;
	@JsonProperty("bsdt")
	public DZFDate businessdate;
	@JsonProperty("asscd_id")
	private String pk_assetcard;
	@JsonProperty("vd8")
	private String vdef8;
	@JsonProperty("vd10")
	private String vdef10;
	@JsonProperty("vd2")
	private String vdef2;
	@JsonProperty("asscls_id")
	private String pk_assetclear;
	@JsonProperty("vd5")
	private String vdef5;
	@JsonProperty("vappr_id")
	private String vapproveid;
	@JsonProperty("billtp_id")
	private String pk_billtype;
	@JsonProperty("vblst")
	private Integer vbillstatus;
	@JsonProperty("vappr_note")
	private String vapprovenote;
	@JsonProperty("vd3")
	private String vdef3;
	@JsonProperty("vd6")
	private String vdef6;
	@JsonProperty("vblno")
	private String vbillno;
	@JsonProperty("dopdt")
	private DZFDate doperatedate;
	@JsonProperty("dr")
	private Integer dr;
	@JsonProperty("vd4")
	private String vdef4;
	@JsonProperty("vd7")
	private String vdef7;
	@JsonProperty("bzzz")
	public DZFBoolean istogl;
	@JsonProperty("vch_id")
	private String pk_voucher;
	@JsonProperty("voucherno")
	public String voucherno;
	@JsonProperty("bsttl")
	private DZFBoolean issettle;
	@JsonProperty("asscd_nm")
	public String pk_assetcard_name;
	
	// 期间
	public String period;
	//打印时  标题显示的区间区间
	public String titlePeriod;
	public String gs;
	
	public static final String DAPPROVEDATE = "dapprovedate";
	public static final String PK_CORP = "pk_corp";
	public static final String VDEF9 = "vdef9";
	public static final String COPERATORID = "coperatorid";
	public static final String VDEF1 = "vdef1";
	public static final String BUSINESSDATE = "businessdate";
	public static final String PK_ASSETCARD = "pk_assetcard";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF10 = "vdef10";
	public static final String VDEF2 = "vdef2";
	public static final String PK_ASSETCLEAR = "pk_assetclear";
	public static final String VDEF5 = "vdef5";
	public static final String VAPPROVEID = "vapproveid";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String VBILLSTATUS = "vbillstatus";
	public static final String VAPPROVENOTE = "vapprovenote";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String VBILLNO = "vbillno";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String VDEF4 = "vdef4";
	public static final String VDEF7 = "vdef7";
			
	
	public String getVoucherno() {
		return voucherno;
	}
	public void setVoucherno(String voucherno) {
		this.voucherno = voucherno;
	}
	public String getPk_assetcard_name() {
		return pk_assetcard_name;
	}
	public void setPk_assetcard_name(String pk_assetcard_name) {
		this.pk_assetcard_name = pk_assetcard_name;
	}
	/**
	 * 属性dapprovedate的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return DZFDate
	 */
	public DZFDate getDapprovedate () {
		return dapprovedate;
	}   
	/**
	 * 属性dapprovedate的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newDapprovedate DZFDate
	 */
	public void setDapprovedate (DZFDate newDapprovedate ) {
	 	this.dapprovedate = newDapprovedate;
	} 	  
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	public DZFBoolean getIstogl() {
		return istogl;
	}
	public void setIstogl(DZFBoolean istogl) {
		this.istogl = istogl;
	}
	public String getPk_voucher() {
		return pk_voucher;
	}
	public void setPk_voucher(String pk_voucher) {
		this.pk_voucher = pk_voucher;
	}
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性vdef9的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef9 () {
		return vdef9;
	}   
	/**
	 * 属性vdef9的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef9 String
	 */
	public void setVdef9 (String newVdef9 ) {
	 	this.vdef9 = newVdef9;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	public DZFBoolean getIssettle() {
		return issettle;
	}
	public void setIssettle(DZFBoolean issettle) {
		this.issettle = issettle;
	}
	/**
	 * 属性vdef1的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef1 () {
		return vdef1;
	}   
	/**
	 * 属性vdef1的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef1 String
	 */
	public void setVdef1 (String newVdef1 ) {
	 	this.vdef1 = newVdef1;
	} 	  
	/**
	 * 属性businessdate的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return DZFDate
	 */
	public DZFDate getBusinessdate () {
		return businessdate;
	}   
	/**
	 * 属性businessdate的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newBusinessdate DZFDate
	 */
	public void setBusinessdate (DZFDate newBusinessdate ) {
	 	this.businessdate = newBusinessdate;
	} 	  
	/**
	 * 属性pk_assetcard的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getPk_assetcard () {
		return pk_assetcard;
	}   
	/**
	 * 属性pk_assetcard的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newPk_assetcard String
	 */
	public void setPk_assetcard (String newPk_assetcard ) {
	 	this.pk_assetcard = newPk_assetcard;
	} 	  
	/**
	 * 属性vdef8的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef8 () {
		return vdef8;
	}   
	/**
	 * 属性vdef8的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef8 String
	 */
	public void setVdef8 (String newVdef8 ) {
	 	this.vdef8 = newVdef8;
	} 	  
	/**
	 * 属性vdef10的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef10 () {
		return vdef10;
	}   
	/**
	 * 属性vdef10的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef10 String
	 */
	public void setVdef10 (String newVdef10 ) {
	 	this.vdef10 = newVdef10;
	} 	  
	/**
	 * 属性vdef2的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef2 () {
		return vdef2;
	}   
	/**
	 * 属性vdef2的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef2 String
	 */
	public void setVdef2 (String newVdef2 ) {
	 	this.vdef2 = newVdef2;
	} 	  
	/**
	 * 属性pk_assetclear的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getPk_assetclear () {
		return pk_assetclear;
	}   
	/**
	 * 属性pk_assetclear的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newPk_assetclear String
	 */
	public void setPk_assetclear (String newPk_assetclear ) {
	 	this.pk_assetclear = newPk_assetclear;
	} 	  
	/**
	 * 属性vdef5的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef5 () {
		return vdef5;
	}   
	/**
	 * 属性vdef5的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef5 String
	 */
	public void setVdef5 (String newVdef5 ) {
	 	this.vdef5 = newVdef5;
	} 	  
	/**
	 * 属性vapproveid的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVapproveid () {
		return vapproveid;
	}   
	/**
	 * 属性vapproveid的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVapproveid String
	 */
	public void setVapproveid (String newVapproveid ) {
	 	this.vapproveid = newVapproveid;
	} 	  
	/**
	 * 属性pk_billtype的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getPk_billtype () {
		return pk_billtype;
	}   
	/**
	 * 属性pk_billtype的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newPk_billtype String
	 */
	public void setPk_billtype (String newPk_billtype ) {
	 	this.pk_billtype = newPk_billtype;
	} 	  
	/**
	 * 属性vbillstatus的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return UFDouble
	 */
	public Integer getVbillstatus () {
		return vbillstatus;
	}   
	/**
	 * 属性vbillstatus的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVbillstatus UFDouble
	 */
	public void setVbillstatus (Integer newVbillstatus ) {
	 	this.vbillstatus = newVbillstatus;
	} 	  
	/**
	 * 属性vapprovenote的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVapprovenote () {
		return vapprovenote;
	}   
	/**
	 * 属性vapprovenote的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVapprovenote String
	 */
	public void setVapprovenote (String newVapprovenote ) {
	 	this.vapprovenote = newVapprovenote;
	} 	  
	/**
	 * 属性vdef3的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef3 () {
		return vdef3;
	}   
	/**
	 * 属性vdef3的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef3 String
	 */
	public void setVdef3 (String newVdef3 ) {
	 	this.vdef3 = newVdef3;
	} 	  
	/**
	 * 属性vdef6的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef6 () {
		return vdef6;
	}   
	/**
	 * 属性vdef6的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef6 String
	 */
	public void setVdef6 (String newVdef6 ) {
	 	this.vdef6 = newVdef6;
	} 	  
	/**
	 * 属性vbillno的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVbillno () {
		return vbillno;
	}   
	/**
	 * 属性vbillno的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVbillno String
	 */
	public void setVbillno (String newVbillno ) {
	 	this.vbillno = newVbillno;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性vdef4的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef4 () {
		return vdef4;
	}   
	/**
	 * 属性vdef4的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef4 String
	 */
	public void setVdef4 (String newVdef4 ) {
	 	this.vdef4 = newVdef4;
	} 	  
	/**
	 * 属性vdef7的Getter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @return String
	 */
	public String getVdef7 () {
		return vdef7;
	}   
	/**
	 * 属性vdef7的Setter方法.
	 * 创建日期:2014-10-23 16:31:09
	 * @param newVdef7 String
	 */
	public void setVdef7 (String newVdef7 ) {
	 	this.vdef7 = newVdef7;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-10-23 16:31:09
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-10-23 16:31:09
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_assetclear";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-10-23 16:31:09
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_assetclear";
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getTitlePeriod() {
		return titlePeriod;
	}
	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}
	public String getGs() {
		return gs;
	}
	public void setGs(String gs) {
		this.gs = gs;
	}  
	
    
} 
