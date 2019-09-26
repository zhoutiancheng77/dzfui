package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2014-09-15 17:27:47
 * @author Administrator
 * @version NCPrj 1.0
 */
public class BdtradeAccountSchemaVO extends SuperVO {
	private static final long serialVersionUID = 2613821808640713345L;
	@JsonProperty("tdid")
	private String pk_trade;
	private DZFDateTime ts;
	@JsonProperty("cpsn")
	private String coperatorid;
	private Integer dr;
	@JsonProperty("dopd")
	private DZFDate doperatedate;
	@JsonProperty("bmgz")
	private String coderule;
	@JsonProperty("id")
	private String pk_trade_accountschema;
	@JsonProperty("kjbz")
	private Integer accountstandard;
	@JsonProperty("name")
	private String accname;
	@JsonProperty("code")
	private String acccode;
	@JsonProperty("inacc")
	private DZFBoolean isinportaccount;
	@JsonProperty("incash")
	private DZFBoolean isinportcashprj;
	@JsonProperty("gsid")
	private String pk_corp;
	public static final String PK_TRADE = "pk_trade";
	public static final String COPERATORID = "coperatorid";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String CODERULE = "coderule";
	public static final String PK_TRADE_ACCOUNTSCHEMA = "pk_trade_accountschema";
	public static final String ACCOUNTSTANDARD = "accountstandard";
	public static final String ACCNAME = "accname";
	public static final String ACCCODE = "acccode";
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}		
	/**
	 * 属性pk_trade的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return String
	 */
	public String getPk_trade () {
		return pk_trade;
	}   
	/**
	 * 属性pk_trade的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newPk_trade String
	 */
	public void setPk_trade (String newPk_trade ) {
	 	this.pk_trade = newPk_trade;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性coderule的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return String
	 */
	public String getCoderule () {
		return coderule;
	}   
	/**
	 * 属性coderule的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newCoderule String
	 */
	public void setCoderule (String newCoderule ) {
	 	this.coderule = newCoderule;
	} 	  
	/**
	 * 属性pk_trade_accountschema的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return String
	 */
	public String getPk_trade_accountschema () {
		return pk_trade_accountschema;
	}   
	/**
	 * 属性pk_trade_accountschema的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newPk_trade_accountschema String
	 */
	public void setPk_trade_accountschema (String newPk_trade_accountschema ) {
	 	this.pk_trade_accountschema = newPk_trade_accountschema;
	} 	  
	/**
	 * 属性accountstandard的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return UFDouble
	 */
	public Integer getAccountstandard () {
		return accountstandard;
	}   
	/**
	 * 属性accountstandard的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newAccountstandard UFDouble
	 */
	public void setAccountstandard (Integer newAccountstandard ) {
	 	this.accountstandard = newAccountstandard;
	} 	  
	/**
	 * 属性accname的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return String
	 */
	public String getAccname () {
		return accname;
	}   
	/**
	 * 属性accname的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newAccname String
	 */
	public void setAccname (String newAccname ) {
	 	this.accname = newAccname;
	} 	  
	/**
	 * 属性acccode的Getter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @return String
	 */
	public String getAcccode () {
		return acccode;
	}   
	/**
	 * 属性acccode的Setter方法.
	 * 创建日期:2014-09-15 17:27:47
	 * @param newAcccode String
	 */
	public void setAcccode (String newAcccode ) {
	 	this.acccode = newAcccode;
	} 	  
 
	public DZFBoolean getIsinportaccount() {
		return isinportaccount;
	}
	public void setIsinportaccount(DZFBoolean isinportaccount) {
		this.isinportaccount = isinportaccount;
	}
	public DZFBoolean getIsinportcashprj() {
		return isinportcashprj;
	}
	public void setIsinportcashprj(DZFBoolean isinportcashprj) {
		this.isinportcashprj = isinportcashprj;
	}
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-09-15 17:27:47
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-09-15 17:27:47
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_trade_accountschema";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-09-15 17:27:47
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tdaccschema";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-09-15 17:27:47
	  */
     public BdtradeAccountSchemaVO() {
		super();	
	}    
} 
