package com.dzf.zxkj.platform.model.sys;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     行业期末损益结转模板VO
 * </p>
 * 创建日期:2014-09-15 17:31:07
 * @author Administrator
 * @version NCPrj 1.0
 */
public class BdTradeTranstemplateHVO extends SuperVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("hykmid")
	private String pk_trade_accountschema;
	private DZFDateTime ts;
	@JsonProperty("zrkmid")
	private String pk_transferinaccount;
	@JsonProperty("bz")
	private String memo;
	@JsonProperty("cpsn")
	private String coperatorid;
	@JsonProperty("zy")
	private String abstracts;
	private Integer dr;
	@JsonProperty("dopd")
	private DZFDate doperatedate;
	@JsonProperty("id")
	private String pk_trade_transtemplate_h;
	@JsonProperty("kmbm")
	private String accountcode;//科目编码
	@JsonProperty("hs")
	private Integer childcount;//行数
	@JsonProperty("fx")
	private Integer direction;
	@JsonProperty("gsid")
	private String pk_corp;
	@JsonProperty("xh")
	private Integer iordernum;//序号
	
	private String prjcode;//行业方案编码
	private String prjname;//行业方案名称
	private String accname;//转入科目名称
	


	public static final String TRANSFERNAME = "transfername";
	public static final String PK_TRADE_ACCOUNTSCHEMA = "pk_trade_accountschema";
	public static final String PK_TRANSFERINACCOUNT = "pk_transferinaccount";
	public static final String MEMO = "memo";
	public static final String COPERATORID = "coperatorid";
	public static final String ABSTRACTS = "abstracts";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String TRANSFERCODE = "transfercode";
	public static final String PK_TRADE_TRANSTEMPLATE_H = "pk_trade_transtemplate_h";
			
	
	public String getPrjcode() {
		return prjcode;
	}
	public void setPrjcode(String prjcode) {
		this.prjcode = prjcode;
	}
	public String getPrjname() {
		return prjname;
	}
	public void setPrjname(String prjname) {
		this.prjname = prjname;
	}
	
	public String getAccname() {
		return accname;
	}
	public void setAccname(String accname) {
		this.accname = accname;
	}
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
	public Integer getChildcount() {
		return childcount;
	}
	public void setChildcount(Integer childcount) {
		this.childcount = childcount;
	}
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return UFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newTs UFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性pk_transferinaccount的Getter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @return String
	 */
	public String getPk_transferinaccount () {
		return pk_transferinaccount;
	}   
	/**
	 * 属性pk_transferinaccount的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newPk_transferinaccount String
	 */
	public void setPk_transferinaccount (String newPk_transferinaccount ) {
	 	this.pk_transferinaccount = newPk_transferinaccount;
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
	 * @return UFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-09-15 17:31:07
	 * @param newDoperatedate UFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	public String getPk_trade_accountschema() {
		return pk_trade_accountschema;
	}
	public void setPk_trade_accountschema(String pk_trade_accountschema) {
		this.pk_trade_accountschema = pk_trade_accountschema;
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
 
	public Integer getIordernum() {
		return iordernum;
	}
	public void setIordernum(Integer iordernum) {
		this.iordernum = iordernum;
	}
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-09-15 17:31:07
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-09-15 17:31:07
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_trade_transtemplate_h";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-09-15 17:31:07
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tdtransmb";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-09-15 17:31:07
	  */
     public BdTradeTranstemplateHVO() {
		super();	
	}    
} 
