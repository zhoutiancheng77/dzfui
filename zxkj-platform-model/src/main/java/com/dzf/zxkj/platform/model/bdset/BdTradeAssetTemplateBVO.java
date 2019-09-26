package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     行业固定资产凭证模板BVO
 * </p>
 * 创建日期:2014-11-04 13:41:55
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class BdTradeAssetTemplateBVO extends SuperVO {
	private DZFDateTime ts;
	@JsonProperty("id")
	private String pk_trade_assettemplate_b;
	@JsonProperty("fx")
	private Integer accountdirect;
	@JsonProperty("lx")
	private Integer accountkind;
	@JsonProperty("cpsn")
	private String coperatorid;
	@JsonProperty("zy")
	private String abstracts;
	private Integer dr;
	@JsonProperty("dopd")
	private DZFDate doperatedate;
	@JsonProperty("kmid")
	private String pk_account;
	@JsonProperty("hid")
	private String pk_trade_assettemplate;
	@JsonProperty("gskmid")
	private String pk_corpaccount;
	@JsonProperty("gsid")
	private String pk_corp;
	
	private String accname;
	private String acccode;
	
	public static final String PK_TRADE_ASSETTEMPLATE_B = "pk_trade_assettemplate_b";
	public static final String ACCOUNTDIRECT = "accountdirect";
	public static final String ACCOUNTKIND = "accountkind";
	public static final String COPERATORID = "coperatorid";
	public static final String ABSTRACTS = "abstracts";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String PK_ACCOUNT = "pk_account";
	public static final String PK_TRADE_ASSETTEMPLATE = "pk_trade_assettemplate";
	
	
	
	
	public String getAccname() {
		return accname;
	}
	public void setAccname(String accname) {
		this.accname = accname;
	}
	public String getAcccode() {
		return acccode;
	}
	public void setAcccode(String acccode) {
		this.acccode = acccode;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	/**
	 * 科目方向-借方
	 */
	public static final int DIRECT_DEBIT=0;
	/**
	 * 科目方向-贷方
	 */
	public static final int DIRECT_CREDIT=1;
	
	/**
	 * 取数类别-固定资产
	 */
	public static final int KIND_GDZC=0;
	/**
	 * 取数类别-固定资产清理
	 */
	public static final int KIND_GDZCQL=1;
	/**
	 * 取数类别-累计折旧
	 */
	public static final int KIND_LJZJ=2;
	/**
	 * 取数类别-费用科目
	 */
	public static final int KIND_FYKM=3;
	
	public String getPk_corpaccount(){
		return pk_corpaccount;
	}
	
	public void setPk_corpaccount(String pk_corpaccount){
		this.pk_corpaccount = pk_corpaccount;
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
	 * 属性pk_trade_assettemplate_b的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return String
	 */
	public String getPk_trade_assettemplate_b () {
		return pk_trade_assettemplate_b;
	}   
	/**
	 * 属性pk_trade_assettemplate_b的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newPk_trade_assettemplate_b String
	 */
	public void setPk_trade_assettemplate_b (String newPk_trade_assettemplate_b ) {
	 	this.pk_trade_assettemplate_b = newPk_trade_assettemplate_b;
	} 	  
	/**
	 * 属性accountdirect的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return UFDouble
	 */
	public Integer getAccountdirect () {
		return accountdirect;
	}   
	/**
	 * 属性accountdirect的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newAccountdirect UFDouble
	 */
	public void setAccountdirect (Integer newAccountdirect ) {
	 	this.accountdirect = newAccountdirect;
	} 	  
	/**
	 * 属性accountkind的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return UFDouble
	 */
	public Integer getAccountkind () {
		return accountkind;
	}   
	/**
	 * 属性accountkind的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newAccountkind UFDouble
	 */
	public void setAccountkind (Integer newAccountkind ) {
	 	this.accountkind = newAccountkind;
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
	 * 属性abstracts的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return String
	 */
	public String getAbstracts () {
		return abstracts;
	}   
	/**
	 * 属性abstracts的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newAbstracts String
	 */
	public void setAbstracts (String newAbstracts ) {
	 	this.abstracts = newAbstracts;
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
	 * 属性pk_account的Getter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @return String
	 */
	public String getPk_account () {
		return pk_account;
	}   
	/**
	 * 属性pk_account的Setter方法.
	 * 创建日期:2014-11-04 13:41:55
	 * @param newPk_account String
	 */
	public void setPk_account (String newPk_account ) {
	 	this.pk_account = newPk_account;
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
		return "PK_TRADE_ASSETTEMPLATE";
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-11-04 13:41:55
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_trade_assettemplate_b";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-11-04 13:41:55
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tdmb_b";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-11-04 13:41:55
	  */
     public BdTradeAssetTemplateBVO() {
		super();	
	}    
} 
