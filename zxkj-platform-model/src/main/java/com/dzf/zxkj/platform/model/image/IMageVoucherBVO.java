package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2014-12-05 22:04:03
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class IMageVoucherBVO extends SuperVO {
	private DZFDateTime ts;
	private String vdef9;
	private String coperatorid;
	private DZFDouble debitmny;
	private String abstracts;
	private String vdef1;
	private String vdef8;
	private String vdef10;
	private String pk_account;
	private String vdef2;
	private String pk_image_voucher;
	private String vdef5;
	private String vdef3;
	private String vdef6;
	private DZFDate doperatedate;
	private Integer dr;
	private String vdef4;
	private String pk_image_voucher_b;
	private DZFDouble creditmny;
	private String vdef7;
	private String pk_corp;
	private DZFDouble ybdebitmny;
	private DZFDouble ybcreditmny;
	private DZFDouble nnumber;
	private DZFDouble nrate;
	private DZFDouble nprice;
	private String pk_currency;

	public static final String VDEF9 = "vdef9";
	public static final String COPERATORID = "coperatorid";
	public static final String DEBITMNY = "debitmny";
	public static final String ABSTRACTS = "abstracts";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF10 = "vdef10";
	public static final String PK_ACCOUNT = "pk_account";
	public static final String VDEF2 = "vdef2";
	public static final String PK_IMAGE_VOUCHER = "pk_image_voucher";
	public static final String VDEF5 = "vdef5";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String VDEF4 = "vdef4";
	public static final String PK_IMAGE_VOUCHER_B = "pk_image_voucher_b";
	public static final String CREDITMNY = "creditmny";
	public static final String VDEF7 = "vdef7";

	
	
	public String getPk_currency() {
		return pk_currency;
	}
	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}		
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性vdef9的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef9 () {
		return vdef9;
	}   
	/**
	 * 属性vdef9的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef9 String
	 */
	public void setVdef9 (String newVdef9 ) {
	 	this.vdef9 = newVdef9;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性debitmny的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return DZFDouble
	 */
	public DZFDouble getDebitmny () {
		return debitmny;
	}   
	/**
	 * 属性debitmny的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newDebitmny DZFDouble
	 */
	public void setDebitmny (DZFDouble newDebitmny ) {
	 	this.debitmny = newDebitmny;
	} 	  
	/**
	 * 属性abstracts的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getAbstracts () {
		return abstracts;
	}   
	/**
	 * 属性abstracts的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newAbstracts String
	 */
	public void setAbstracts (String newAbstracts ) {
	 	this.abstracts = newAbstracts;
	} 	  
	/**
	 * 属性vdef1的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef1 () {
		return vdef1;
	}   
	/**
	 * 属性vdef1的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef1 String
	 */
	public void setVdef1 (String newVdef1 ) {
	 	this.vdef1 = newVdef1;
	} 	  
	/**
	 * 属性vdef8的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef8 () {
		return vdef8;
	}   
	/**
	 * 属性vdef8的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef8 String
	 */
	public void setVdef8 (String newVdef8 ) {
	 	this.vdef8 = newVdef8;
	} 	  
	/**
	 * 属性vdef10的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef10 () {
		return vdef10;
	}   
	/**
	 * 属性vdef10的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef10 String
	 */
	public void setVdef10 (String newVdef10 ) {
	 	this.vdef10 = newVdef10;
	} 	  
	/**
	 * 属性pk_account的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getPk_account () {
		return pk_account;
	}   
	/**
	 * 属性pk_account的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newPk_account String
	 */
	public void setPk_account (String newPk_account ) {
	 	this.pk_account = newPk_account;
	} 	  
	/**
	 * 属性vdef2的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef2 () {
		return vdef2;
	}   
	/**
	 * 属性vdef2的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef2 String
	 */
	public void setVdef2 (String newVdef2 ) {
	 	this.vdef2 = newVdef2;
	} 	  
	/**
	 * 属性pk_image_voucher的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getPk_image_voucher () {
		return pk_image_voucher;
	}   
	/**
	 * 属性pk_image_voucher的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newPk_image_voucher String
	 */
	public void setPk_image_voucher (String newPk_image_voucher ) {
	 	this.pk_image_voucher = newPk_image_voucher;
	} 	  
	/**
	 * 属性vdef5的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef5 () {
		return vdef5;
	}   
	/**
	 * 属性vdef5的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef5 String
	 */
	public void setVdef5 (String newVdef5 ) {
	 	this.vdef5 = newVdef5;
	} 	  
	/**
	 * 属性vdef3的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef3 () {
		return vdef3;
	}   
	/**
	 * 属性vdef3的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef3 String
	 */
	public void setVdef3 (String newVdef3 ) {
	 	this.vdef3 = newVdef3;
	} 	  
	/**
	 * 属性vdef6的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef6 () {
		return vdef6;
	}   
	/**
	 * 属性vdef6的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef6 String
	 */
	public void setVdef6 (String newVdef6 ) {
	 	this.vdef6 = newVdef6;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return DZFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newDr DZFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性vdef4的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef4 () {
		return vdef4;
	}   
	/**
	 * 属性vdef4的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef4 String
	 */
	public void setVdef4 (String newVdef4 ) {
	 	this.vdef4 = newVdef4;
	} 	  
	/**
	 * 属性pk_image_voucher_b的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getPk_image_voucher_b () {
		return pk_image_voucher_b;
	}   
	/**
	 * 属性pk_image_voucher_b的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newPk_image_voucher_b String
	 */
	public void setPk_image_voucher_b (String newPk_image_voucher_b ) {
	 	this.pk_image_voucher_b = newPk_image_voucher_b;
	} 	  
	/**
	 * 属性creditmny的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return DZFDouble
	 */
	public DZFDouble getCreditmny () {
		return creditmny;
	}   
	/**
	 * 属性creditmny的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newCreditmny DZFDouble
	 */
	public void setCreditmny (DZFDouble newCreditmny ) {
	 	this.creditmny = newCreditmny;
	} 	  
	/**
	 * 属性vdef7的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVdef7 () {
		return vdef7;
	}   
	/**
	 * 属性vdef7的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVdef7 String
	 */
	public void setVdef7 (String newVdef7 ) {
	 	this.vdef7 = newVdef7;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-12-05 22:04:03
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
		return "PK_IMAGE_VOUCHER";
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-12-05 22:04:03
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_image_voucher_b";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-12-05 22:04:03
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_imgvoucher_b";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-12-05 22:04:03
	  */
     public IMageVoucherBVO() {
		super();	
	}
	public DZFDouble getYbdebitmny() {
		return ybdebitmny;
	}
	public void setYbdebitmny(DZFDouble ybdebitmny) {
		this.ybdebitmny = ybdebitmny;
	}
	public DZFDouble getYbcreditmny() {
		return ybcreditmny;
	}
	public void setYbcreditmny(DZFDouble ybcreditmny) {
		this.ybcreditmny = ybcreditmny;
	}
	public DZFDouble getNnumber() {
		return nnumber;
	}
	public void setNnumber(DZFDouble nnumber) {
		this.nnumber = nnumber;
	}
	public DZFDouble getNrate() {
		return nrate;
	}
	public void setNrate(DZFDouble nrate) {
		this.nrate = nrate;
	}
	public DZFDouble getNprice() {
		return nprice;
	}
	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}    
     
     
} 
