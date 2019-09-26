package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 现金流量表
 * 创建日期:2014-11-06 19:46:37
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class XjllVO extends SuperVO {
	private String pk_corp;
	private DZFDateTime ts;
//	分录主键
	private String pk_tzpz_b;
	private String vdef9;
//	制单人
	@JsonProperty("zdr")
	private String coperatorid;
	private String vdef1;
	private String vdef8;
	private String vdef10;
//	凭证号
	private String pzh;
//	主键
	@JsonProperty("id")
	private String pk_xjll;
	private String vdef7;
//	nmny	金额
	private DZFDouble nmny;
//	方向
	@JsonProperty("vdirect")
	private Integer vdirect;
//	科目主键
	private String pk_accsubj;
	private String vdef2;
	private String vdef5;
	private String memo;
	@JsonProperty("pz_id")
	private String pk_tzpz_h;
	private String vdef3;
	private String vdef6;
	private Integer dr;
//	制单日期
	private DZFDate doperatedate;
//	币种
	private String dcurrency;
//	pk_xjllxm	现金流量项目
	@JsonProperty("xjll_id")
	private String pk_xjllxm;
	private String vdef4;
	
	private String xjlxmmc;

	public static final String PK_CORP = "pk_corp";
	public static final String PK_TZPZ_B = "pk_tzpz_b";
	public static final String VDEF9 = "vdef9";
	public static final String COPERATORID = "coperatorid";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF10 = "vdef10";
	public static final String PZH = "pzh";
	public static final String PK_XJLL = "pk_xjll";
	public static final String VDEF7 = "vdef7";
	public static final String NMNY = "nmny";
	public static final String VDIRECT = "vdirect";
	public static final String PK_ACCSUBJ = "pk_accsubj";
	public static final String VDEF2 = "vdef2";
	public static final String VDEF5 = "vdef5";
	public static final String MEMO = "memo";
	public static final String PK_TZPZ_H = "pk_tzpz_h";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String DCURRENCY = "dcurrency";
	public static final String PK_XJLLXM = "pk_xjllxm";
	public static final String VDEF4 = "vdef4";
			
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性pk_tzpz_b的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getPk_tzpz_b () {
		return pk_tzpz_b;
	}   
	/**
	 * 属性pk_tzpz_b的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newPk_tzpz_b String
	 */
	public void setPk_tzpz_b (String newPk_tzpz_b ) {
	 	this.pk_tzpz_b = newPk_tzpz_b;
	} 	  
	/**
	 * 属性vdef9的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef9 () {
		return vdef9;
	}   
	/**
	 * 属性vdef9的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef9 String
	 */
	public void setVdef9 (String newVdef9 ) {
	 	this.vdef9 = newVdef9;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性vdef1的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef1 () {
		return vdef1;
	}   
	/**
	 * 属性vdef1的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef1 String
	 */
	public void setVdef1 (String newVdef1 ) {
	 	this.vdef1 = newVdef1;
	} 	  
	/**
	 * 属性vdef8的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef8 () {
		return vdef8;
	}   
	/**
	 * 属性vdef8的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef8 String
	 */
	public void setVdef8 (String newVdef8 ) {
	 	this.vdef8 = newVdef8;
	} 	  
	/**
	 * 属性vdef10的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef10 () {
		return vdef10;
	}   
	/**
	 * 属性vdef10的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef10 String
	 */
	public void setVdef10 (String newVdef10 ) {
	 	this.vdef10 = newVdef10;
	} 	  
	/**
	 * 属性pzh的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getPzh () {
		return pzh;
	}   
	/**
	 * 属性pzh的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newPzh String
	 */
	public void setPzh (String newPzh ) {
	 	this.pzh = newPzh;
	} 	  
	/**
	 * 属性pk_xjll的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getPk_xjll () {
		return pk_xjll;
	}   
	/**
	 * 属性pk_xjll的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newPk_xjll String
	 */
	public void setPk_xjll (String newPk_xjll ) {
	 	this.pk_xjll = newPk_xjll;
	} 	  
	/**
	 * 属性vdef7的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef7 () {
		return vdef7;
	}   
	/**
	 * 属性vdef7的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef7 String
	 */
	public void setVdef7 (String newVdef7 ) {
	 	this.vdef7 = newVdef7;
	} 	  
	/**
	 * 属性nmny的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return DZFDouble
	 */
	public DZFDouble getNmny () {
		return nmny;
	}   
	/**
	 * 属性nmny的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newNmny DZFDouble
	 */
	public void setNmny (DZFDouble newNmny ) {
	 	this.nmny = newNmny;
	} 	  
	/**
	 * 属性vdirect的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return DZFDouble
	 */
	public Integer getVdirect () {
		return vdirect;
	}   
	/**
	 * 属性vdirect的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdirect DZFDouble
	 */
	public void setVdirect (Integer newVdirect ) {
	 	this.vdirect = newVdirect;
	} 	  
	/**
	 * 属性pk_accsubj的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getPk_accsubj () {
		return pk_accsubj;
	}   
	/**
	 * 属性pk_accsubj的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newPk_accsubj String
	 */
	public void setPk_accsubj (String newPk_accsubj ) {
	 	this.pk_accsubj = newPk_accsubj;
	} 	  
	/**
	 * 属性vdef2的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef2 () {
		return vdef2;
	}   
	/**
	 * 属性vdef2的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef2 String
	 */
	public void setVdef2 (String newVdef2 ) {
	 	this.vdef2 = newVdef2;
	} 	  
	/**
	 * 属性vdef5的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef5 () {
		return vdef5;
	}   
	/**
	 * 属性vdef5的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef5 String
	 */
	public void setVdef5 (String newVdef5 ) {
	 	this.vdef5 = newVdef5;
	} 	  
	/**
	 * 属性memo的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newMemo String
	 */
	public void setMemo (String newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * 属性pk_tzpz_h的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getPk_tzpz_h () {
		return pk_tzpz_h;
	}   
	/**
	 * 属性pk_tzpz_h的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newPk_tzpz_h String
	 */
	public void setPk_tzpz_h (String newPk_tzpz_h ) {
	 	this.pk_tzpz_h = newPk_tzpz_h;
	} 	  
	/**
	 * 属性vdef3的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef3 () {
		return vdef3;
	}   
	/**
	 * 属性vdef3的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef3 String
	 */
	public void setVdef3 (String newVdef3 ) {
	 	this.vdef3 = newVdef3;
	} 	  
	/**
	 * 属性vdef6的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef6 () {
		return vdef6;
	}   
	/**
	 * 属性vdef6的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef6 String
	 */
	public void setVdef6 (String newVdef6 ) {
	 	this.vdef6 = newVdef6;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return DZFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newDr DZFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性dcurrency的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getDcurrency () {
		return dcurrency;
	}   
	/**
	 * 属性dcurrency的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newDcurrency String
	 */
	public void setDcurrency (String newDcurrency ) {
	 	this.dcurrency = newDcurrency;
	} 	  
	/**
	 * 属性pk_xjllxm的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getPk_xjllxm () {
		return pk_xjllxm;
	}   
	/**
	 * 属性pk_xjllxm的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newPk_xjllxm String
	 */
	public void setPk_xjllxm (String newPk_xjllxm ) {
	 	this.pk_xjllxm = newPk_xjllxm;
	} 	  
	/**
	 * 属性vdef4的Getter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @return String
	 */
	public String getVdef4 () {
		return vdef4;
	}   
	/**
	 * 属性vdef4的Setter方法.
	 * 创建日期:2014-11-06 19:46:37
	 * @param newVdef4 String
	 */
	public void setVdef4 (String newVdef4 ) {
	 	this.vdef4 = newVdef4;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-11-06 19:46:37
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-11-06 19:46:37
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_xjll";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-11-06 19:46:37
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "YNT_XJLL";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-11-06 19:46:37
	  */
     public XjllVO() {
		super();	
	}
	public String getXjlxmmc() {
		return xjlxmmc;
	}
	public void setXjlxmmc(String xjlxmmc) {
		this.xjlxmmc = xjlxmmc;
	}    
} 
