package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
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
public class IMageVoucherVO extends SuperVO {
	private String pk_corp;
	private DZFDateTime ts;
	private String vdef9;
	private String vbillcode;
	private String coperatorid;
	private DZFDouble debitmny;
	private String vdef1;
	private String vdef8;
	private String vdef10;
	private String vdef2;
	private String pk_image_voucher;
	private String vdef5;
	private String vdef3;
	private String vdef6;
	private DZFDate doperatedate;
	private Integer dr;
	private String vdef4;
	private String pk_image_library;
	private DZFDouble creditmny;
	private String vdef7;
	private DZFBoolean istogl;
	private String glvoucherno;
	private Integer iverion;

	public static final String PK_CORP = "pk_corp";
	public static final String VDEF9 = "vdef9";
	public static final String VBILLCODE = "vbillcode";
	public static final String COPERATORID = "coperatorid";
	public static final String DEBITMNY = "debitmny";
	public static final String VDEF1 = "vdef1";
	public static final String VDEF8 = "vdef8";
	public static final String VDEF10 = "vdef10";
	public static final String VDEF2 = "vdef2";
	public static final String PK_IMAGE_VOUCHER = "pk_image_voucher";
	public static final String VDEF5 = "vdef5";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String VDEF4 = "vdef4";
	public static final String PK_IMAGE_LIBRARY = "pk_image_library";
	public static final String CREDITMNY = "creditmny";
	public static final String VDEF7 = "vdef7";
			
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
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
	 * 属性vbillcode的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getVbillcode () {
		return vbillcode;
	}   
	/**
	 * 属性vbillcode的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newVbillcode String
	 */
	public void setVbillcode (String newVbillcode ) {
	 	this.vbillcode = newVbillcode;
	} 	  
	public DZFBoolean getIstogl() {
		return istogl;
	}
	public void setIstogl(DZFBoolean istogl) {
		this.istogl = istogl;
	}
	public String getGlvoucherno() {
		return glvoucherno;
	}
	public void setGlvoucherno(String glvoucherno) {
		this.glvoucherno = glvoucherno;
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
	 * 属性pk_image_library的Getter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @return String
	 */
	public String getPk_image_library () {
		return pk_image_library;
	}   
	/**
	 * 属性pk_image_library的Setter方法.
	 * 创建日期:2014-12-05 22:04:03
	 * @param newPk_image_library String
	 */
	public void setPk_image_library (String newPk_image_library ) {
	 	this.pk_image_library = newPk_image_library;
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
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-12-05 22:04:03
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_image_voucher";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-12-05 22:04:03
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_image_voucher";
	}
    
    public Integer getIverion() {
    	if(iverion == null ){
    		iverion = 0;
    	}
		return iverion;
	}
	public void setIverion(Integer iverion) {
		this.iverion = iverion;
	}
	/**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-12-05 22:04:03
	  */
     public IMageVoucherVO() {
		super();	
	}    
} 
