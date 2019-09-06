package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     行业会计科目
 * </p>
 * 创建日期:2014-09-15 17:28:23
 * @author Administrator
 * @version NCPrj 1.0
 */
public class BdTradeAccountVO extends SuperVO {
	private static final long serialVersionUID = -7438853863993055585L;
	@JsonProperty("name")
	private String accountname;
	@JsonProperty("jc")
	private Integer accountlevel;
	private DZFDateTime ts;
	@JsonProperty("vd9")
	private String vdef9;
	@JsonProperty("yz")
	private DZFBoolean isleaf;
	@JsonProperty("cpsn")
	private String coperatorid;
	@JsonProperty("vd1")
	private String vdef1;
	@JsonProperty("vd8")
	private String vdef8;
	@JsonProperty("vd10")
	private String vdef10;
	@JsonProperty("vd2")
	private String vdef2;
	@JsonProperty("hykmid")
	private String pk_trade_accountschema;
	@JsonProperty("fx")
	private Integer direction;
	@JsonProperty("vd5")
	private String vdef5;
	@JsonProperty("id")
	private String pk_trade_account;
	@JsonProperty("lx")
	private Integer accountkind;
	@JsonProperty("bz")
	private String memo;
	@JsonProperty("vd3")
	private String vdef3;
	@JsonProperty("vd6")
	private String vdef6;
	@JsonProperty("dopd")
	private DZFDate doperatedate;
	private Integer dr;
	@JsonProperty("vd4")
	private String vdef4;
	@JsonProperty("code")
	private String accountcode;
	@JsonProperty("vd7")
	private String vdef7;
	@JsonProperty("gsid")
	private String pk_corp;
	
	
	private Integer __parentId;
	private BigInteger codeid;
	
	private String fullname;
	
	public static final String ACCOUNTNAME = "accountname";
	public static final String ACCOUNTLEVEL = "accountlevel";
	public static final String VDEF9 = "vdef9";
	public static final String ISLEAF = "isleaf";
	public static final String COPERATORID = "coperatorid";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF10 = "vdef10";
	public static final String VDEF2 = "vdef2";
	public static final String PK_TRADE_ACCOUNTSCHEMA = "pk_trade_accountschema";
	public static final String DIRECTION = "direction";
	public static final String VDEF5 = "vdef5";
	public static final String PK_TRADE_ACCOUNT = "pk_trade_account";
	public static final String ACCOUNTKIND = "accountkind";
	public static final String MEMO = "memo";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String VDEF4 = "vdef4";
	public static final String ACCOUNTCODE = "accountcode";
	public static final String VDEF7 = "vdef7";
	
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}		
	/**
	 * 属性accountname的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getAccountname () {
		return accountname;
	}   
	/**
	 * 属性accountname的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newAccountname String
	 */
	public void setAccountname (String newAccountname ) {
	 	this.accountname = newAccountname;
	} 	  
	/**
	 * 属性accountlevel的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return UFDouble
	 */
	public Integer getAccountlevel () {
		return accountlevel;
	}   
	/**
	 * 属性accountlevel的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newAccountlevel UFDouble
	 */
	public void setAccountlevel (Integer newAccountlevel ) {
	 	this.accountlevel = newAccountlevel;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return UFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newTs UFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性vdef9的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef9 () {
		return vdef9;
	}   
	/**
	 * 属性vdef9的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef9 String
	 */
	public void setVdef9 (String newVdef9 ) {
	 	this.vdef9 = newVdef9;
	} 	  
	/**
	 * 属性isleaf的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return UFBoolean
	 */
	public DZFBoolean getIsleaf () {
		return isleaf;
	}   
	/**
	 * 属性isleaf的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newIsleaf UFBoolean
	 */
	public void setIsleaf (DZFBoolean newIsleaf ) {
	 	this.isleaf = newIsleaf;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性vdef1的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef1 () {
		return vdef1;
	}   
	/**
	 * 属性vdef1的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef1 String
	 */
	public void setVdef1 (String newVdef1 ) {
	 	this.vdef1 = newVdef1;
	} 	  
	/**
	 * 属性vdef8的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef8 () {
		return vdef8;
	}   
	/**
	 * 属性vdef8的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef8 String
	 */
	public void setVdef8 (String newVdef8 ) {
	 	this.vdef8 = newVdef8;
	} 	  
	/**
	 * 属性vdef10的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef10 () {
		return vdef10;
	}   
	/**
	 * 属性vdef10的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef10 String
	 */
	public void setVdef10 (String newVdef10 ) {
	 	this.vdef10 = newVdef10;
	} 	  
	/**
	 * 属性vdef2的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef2 () {
		return vdef2;
	}   
	/**
	 * 属性vdef2的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef2 String
	 */
	public void setVdef2 (String newVdef2 ) {
	 	this.vdef2 = newVdef2;
	} 	  
	/**
	 * 属性pk_trade_accountschema的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getPk_trade_accountschema () {
		return pk_trade_accountschema;
	}   
	/**
	 * 属性pk_trade_accountschema的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newPk_trade_accountschema String
	 */
	public void setPk_trade_accountschema (String newPk_trade_accountschema ) {
	 	this.pk_trade_accountschema = newPk_trade_accountschema;
	} 	  
	/**
	 * 属性direction的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return UFDouble
	 */
	public Integer getDirection () {
		return direction;
	}   
	/**
	 * 属性direction的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newDirection UFDouble
	 */
	public void setDirection (Integer newDirection ) {
	 	this.direction = newDirection;
	} 	  
	/**
	 * 属性vdef5的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef5 () {
		return vdef5;
	}   
	/**
	 * 属性vdef5的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef5 String
	 */
	public void setVdef5 (String newVdef5 ) {
	 	this.vdef5 = newVdef5;
	} 	  
	/**
	 * 属性pk_trade_account的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getPk_trade_account () {
		return pk_trade_account;
	}   
	/**
	 * 属性pk_trade_account的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newPk_trade_account String
	 */
	public void setPk_trade_account (String newPk_trade_account ) {
	 	this.pk_trade_account = newPk_trade_account;
	} 	  
	/**
	 * 属性accountkind的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return UFDouble
	 */
	public Integer getAccountkind () {
		return accountkind;
	}   
	/**
	 * 属性accountkind的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newAccountkind UFDouble
	 */
	public void setAccountkind (Integer newAccountkind ) {
	 	this.accountkind = newAccountkind;
	} 	  
	/**
	 * 属性memo的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newMemo String
	 */
	public void setMemo (String newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * 属性vdef3的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef3 () {
		return vdef3;
	}   
	/**
	 * 属性vdef3的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef3 String
	 */
	public void setVdef3 (String newVdef3 ) {
	 	this.vdef3 = newVdef3;
	} 	  
	/**
	 * 属性vdef6的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef6 () {
		return vdef6;
	}   
	/**
	 * 属性vdef6的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef6 String
	 */
	public void setVdef6 (String newVdef6 ) {
	 	this.vdef6 = newVdef6;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return UFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newDoperatedate UFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性vdef4的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef4 () {
		return vdef4;
	}   
	/**
	 * 属性vdef4的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef4 String
	 */
	public void setVdef4 (String newVdef4 ) {
	 	this.vdef4 = newVdef4;
	} 	  
	/**
	 * 属性accountcode的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getAccountcode () {
		return accountcode;
	}   
	/**
	 * 属性accountcode的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param accountcode String
	 */
	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
		this.codeid = new BigInteger(accountcode);
		if(accountcode!=null&&accountcode.length()>4){
		this.__parentId = new Integer(accountcode.substring(0, accountcode.length()-2));
		}else{
			this.__parentId =null;
		}
	} 
	/**
	 * 属性vdef7的Getter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @return String
	 */
	public String getVdef7 () {
		return vdef7;
	}   
	/**
	 * 属性vdef7的Setter方法.
	 * 创建日期:2014-09-15 17:28:23
	 * @param newVdef7 String
	 */
	public void setVdef7 (String newVdef7 ) {
	 	this.vdef7 = newVdef7;
	} 	  
 
	public Integer get__parentId() {
		return __parentId;
	}
	public void set__parentId(Integer __parentId) {
		this.__parentId = __parentId;
	}
	
	public BigInteger getCodeid() {
		return codeid;
	}
	public void setCodeid(BigInteger codeid) {
		this.codeid = codeid;
	}
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-09-15 17:28:23
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-09-15 17:28:23
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_trade_account";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-09-15 17:28:23
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_tdacc";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-09-15 17:28:23
	  */
     public BdTradeAccountVO() {
		super();	
	}
 	
	@Override
	public String toString() {
		return this.getAccountcode();
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	
} 
