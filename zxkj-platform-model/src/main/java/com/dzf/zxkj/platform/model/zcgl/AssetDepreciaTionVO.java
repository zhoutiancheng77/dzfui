package com.dzf.zxkj.platform.model.zcgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2014-11-01 16:41:34
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class AssetDepreciaTionVO extends SuperVO {
	
	// 期间
	
	private String titlePeriod;
	private String period;
	
	private String gs;
	
	private DZFDateTime ts;
	private DZFBoolean istogl;
	private String coperatorid;
	private String pk_assetcard;
	private DZFDate businessdate;//折旧日期
	private Integer dr;
	private DZFDate doperatedate;
	@JsonProperty("id_voucher")
	private String pk_voucher;
	@JsonProperty("pk_asset")
	private String pk_assetdepreciation;
	private DZFDouble originalvalue;//本期折旧
	private DZFBoolean issettle;
	private String pk_corp;
	private DZFDouble assetmny;//资产原值
	private DZFDouble depreciationmny;//累计折旧
	private DZFDouble assetnetmny;//资产净值
	
	private String assetcode;//资产编码
	
	private String pzh;
 
	private String catename;//资产类别
	private String assetproperty;//类别属性
	private String assetname;//资产名称
	private String accountdate;//使用时间
	private Integer uselimit;//预计使用期间数
	private DZFDouble salvageratio;//净残值率
	private String pk_assetcategory;//资产类别id
	private String catecode;//资产类别编码
	private Integer catelevel;//资产类别等级
	private String zy;//摘要
	
	
	public String getAssetproperty() {
		return assetproperty;
	}
	public void setAssetproperty(String assetproperty) {
		this.assetproperty = assetproperty;
	}
	public String getZy() {
		return zy;
	}
	public void setZy(String zy) {
		this.zy = zy;
	}
	public Integer getCatelevel() {
		return catelevel;
	}
	public void setCatelevel(Integer catelevel) {
		this.catelevel = catelevel;
	}
	public String getCatecode() {
		return catecode;
	}
	public void setCatecode(String catecode) {
		this.catecode = catecode;
	}
	public String getPk_assetcategory() {
		return pk_assetcategory;
	}
	public void setPk_assetcategory(String pk_assetcategory) {
		this.pk_assetcategory = pk_assetcategory;
	}
	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getGs() {
		return gs;
	}
	public void setGs(String gs) {
		this.gs = gs;
	}
	public String getPzh() {
		return pzh;
	}
	public void setPzh(String pzh) {
		this.pzh = pzh;
	}
	public String getAssetcode() {
		return assetcode;
	}
	public void setAssetcode(String assetcode) {
		this.assetcode = assetcode;
	}

	public String getCatename() {
		return catename;
	}
	public void setCatename(String catename) {
		this.catename = catename;
	}
	public String getAssetname() {
		return assetname;
	}
	public void setAssetname(String assetname) {
		this.assetname = assetname;
	}
	public String getAccountdate() {
		return accountdate;
	}
	public void setAccountdate(String accountdate) {
		this.accountdate = accountdate;
	}
	public Integer getUselimit() {
		return uselimit;
	}
	public void setUselimit(Integer uselimit) {
		this.uselimit = uselimit;
	}
	public DZFDouble getSalvageratio() {
		return salvageratio;
	}
	public void setSalvageratio(DZFDouble salvageratio) {
		this.salvageratio = salvageratio;
	}



	public static final String ISTOGL = "istogl";
	public static final String COPERATORID = "coperatorid";
	public static final String PK_ASSETCARD = "pk_assetcard";
	public static final String BUSINESSDATE = "businessdate";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String PK_VOUCHER = "pk_voucher";
	public static final String PK_ASSETDEPRECIATION = "pk_assetdepreciation";
	public static final String ORIGINALVALUE = "originalvalue";
	public static final String ISSETTLE = "issettle";

	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public DZFDouble getAssetmny() {
		return assetmny;
	}
	public void setAssetmny(DZFDouble assetmny) {
		this.assetmny = assetmny;
	}
	public DZFDouble getDepreciationmny() {
		return depreciationmny;
	}
	public void setDepreciationmny(DZFDouble depreciationmny) {
		this.depreciationmny = depreciationmny;
	}
	public DZFDouble getAssetnetmny() {
		return assetnetmny;
	}
	public void setAssetnetmny(DZFDouble assetnetmny) {
		this.assetnetmny = assetnetmny;
	}

	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性istogl的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return DZFBoolean
	 */
	public DZFBoolean getIstogl () {
		return istogl;
	}   
	/**
	 * 属性istogl的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newIstogl DZFBoolean
	 */
	public void setIstogl (DZFBoolean newIstogl ) {
	 	this.istogl = newIstogl;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性pk_assetcard的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return String
	 */
	public String getPk_assetcard () {
		return pk_assetcard;
	}   
	/**
	 * 属性pk_assetcard的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newPk_assetcard String
	 */
	public void setPk_assetcard (String newPk_assetcard ) {
	 	this.pk_assetcard = newPk_assetcard;
	} 	  
	/**
	 * 属性businessdate的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return DZFDate
	 */
	public DZFDate getBusinessdate () {
		return businessdate;
	}   
	/**
	 * 属性businessdate的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newBusinessdate DZFDate
	 */
	public void setBusinessdate (DZFDate newBusinessdate ) {
	 	this.businessdate = newBusinessdate;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return DZFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newDr DZFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性pk_voucher的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return String
	 */
	public String getPk_voucher () {
		return pk_voucher;
	}   
	/**
	 * 属性pk_voucher的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newPk_voucher String
	 */
	public void setPk_voucher (String newPk_voucher ) {
	 	this.pk_voucher = newPk_voucher;
	} 	  
	/**
	 * 属性pk_assetdepreciation的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return String
	 */
	public String getPk_assetdepreciation () {
		return pk_assetdepreciation;
	}   
	/**
	 * 属性pk_assetdepreciation的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newPk_assetdepreciation String
	 */
	public void setPk_assetdepreciation (String newPk_assetdepreciation ) {
	 	this.pk_assetdepreciation = newPk_assetdepreciation;
	} 	  
	/**
	 * 属性originalvalue的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return DZFDouble
	 */
	public DZFDouble getOriginalvalue () {
		return originalvalue;
	}   
	/**
	 * 属性originalvalue的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newOriginalvalue DZFDouble
	 */
	public void setOriginalvalue (DZFDouble newOriginalvalue ) {
	 	this.originalvalue = newOriginalvalue;
	} 	  
	/**
	 * 属性issettle的Getter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @return DZFBoolean
	 */
	public DZFBoolean getIssettle () {
		return issettle;
	}   
	/**
	 * 属性issettle的Setter方法.
	 * 创建日期:2014-11-01 16:41:34
	 * @param newIssettle DZFBoolean
	 */
	public void setIssettle (DZFBoolean newIssettle ) {
	 	this.issettle = newIssettle;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-11-01 16:41:34
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
		return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-11-01 16:41:34
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_assetdepreciation";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-11-01 16:41:34
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_depreciation";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-11-01 16:41:34
	  */
     public AssetDepreciaTionVO() {
		super();	
	}
	public String getTitlePeriod() {
		return titlePeriod;
	}
	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}   
     
     
} 
