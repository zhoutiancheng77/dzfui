package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2014-12-05 22:02:57
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class ImageIdentVO extends SuperVO {
	private DZFDateTime identdate3;
	private String pk_corp;
	private String identvalue;
	private DZFDateTime ts;
	private String pk_image_meta;
	private String identerid3;
	private String identvalue1;
	private String coperatorid;
	private String identvalue2;
	private String identerid2;
	private DZFDateTime identdate2;
	private String identerid1;
	private String identvalue3;
	private DZFDate doperatedate;
	private Integer dr;
	private DZFDateTime identdate1;
	private String pk_image_ident;
	private Integer clipkind;   // 切图类型

	public static final String IDENTDATE3 = "identdate3";
	public static final String PK_CORP = "pk_corp";
	public static final String IDENTVALUE = "identvalue";
	public static final String PK_IMAGE_META = "pk_image_meta";
	public static final String IDENTERID3 = "identerid3";
	public static final String IDENTVALUE1 = "identvalue1";
	public static final String COPERATORID = "coperatorid";
	public static final String IDENTVALUE2 = "identvalue2";
	public static final String IDENTERID2 = "identerid2";
	public static final String IDENTDATE2 = "identdate2";
	public static final String IDENTERID1 = "identerid1";
	public static final String IDENTVALUE3 = "identvalue3";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String IDENTDATE1 = "identdate1";
	public static final String PK_IMAGE_IDENT = "pk_image_ident";
			
	/**
	 * 属性identdate3的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return DZFDateTime
	 */
	public DZFDateTime getIdentdate3 () {
		return identdate3;
	}   
	/**
	 * 属性identdate3的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdentdate3 DZFDateTime
	 */
	public void setIdentdate3 (DZFDateTime newIdentdate3 ) {
	 	this.identdate3 = newIdentdate3;
	} 	  
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	/**
	 * 属性identvalue的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getIdentvalue () {
		return identvalue;
	}   
	/**
	 * 属性identvalue的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdentvalue String
	 */
	public void setIdentvalue (String newIdentvalue ) {
	 	this.identvalue = newIdentvalue;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	public Integer getClipkind() {
		return clipkind;
	}
	public void setClipkind(Integer clipkind) {
		this.clipkind = clipkind;
	}
	/**
	 * 属性pk_image_meta的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getPk_image_meta () {
		return pk_image_meta;
	}   
	/**
	 * 属性pk_image_meta的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newPk_image_meta String
	 */
	public void setPk_image_meta (String newPk_image_meta ) {
	 	this.pk_image_meta = newPk_image_meta;
	} 	  
	/**
	 * 属性identerid3的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getIdenterid3 () {
		return identerid3;
	}   
	/**
	 * 属性identerid3的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdenterid3 String
	 */
	public void setIdenterid3 (String newIdenterid3 ) {
	 	this.identerid3 = newIdenterid3;
	} 	  
	/**
	 * 属性identvalue1的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getIdentvalue1 () {
		return identvalue1;
	}   
	/**
	 * 属性identvalue1的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdentvalue1 String
	 */
	public void setIdentvalue1 (String newIdentvalue1 ) {
	 	this.identvalue1 = newIdentvalue1;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性identvalue2的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getIdentvalue2 () {
		return identvalue2;
	}   
	/**
	 * 属性identvalue2的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdentvalue2 String
	 */
	public void setIdentvalue2 (String newIdentvalue2 ) {
	 	this.identvalue2 = newIdentvalue2;
	} 	  
	/**
	 * 属性identerid2的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getIdenterid2 () {
		return identerid2;
	}   
	/**
	 * 属性identerid2的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdenterid2 String
	 */
	public void setIdenterid2 (String newIdenterid2 ) {
	 	this.identerid2 = newIdenterid2;
	} 	  
	/**
	 * 属性identdate2的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return DZFDateTime
	 */
	public DZFDateTime getIdentdate2 () {
		return identdate2;
	}   
	/**
	 * 属性identdate2的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdentdate2 DZFDateTime
	 */
	public void setIdentdate2 (DZFDateTime newIdentdate2 ) {
	 	this.identdate2 = newIdentdate2;
	} 	  
	/**
	 * 属性identerid1的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getIdenterid1 () {
		return identerid1;
	}   
	/**
	 * 属性identerid1的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdenterid1 String
	 */
	public void setIdenterid1 (String newIdenterid1 ) {
	 	this.identerid1 = newIdenterid1;
	} 	  
	/**
	 * 属性identvalue3的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getIdentvalue3 () {
		return identvalue3;
	}   
	/**
	 * 属性identvalue3的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdentvalue3 String
	 */
	public void setIdentvalue3 (String newIdentvalue3 ) {
	 	this.identvalue3 = newIdentvalue3;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性identdate1的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return DZFDateTime
	 */
	public DZFDateTime getIdentdate1 () {
		return identdate1;
	}   
	/**
	 * 属性identdate1的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newIdentdate1 DZFDateTime
	 */
	public void setIdentdate1 (DZFDateTime newIdentdate1 ) {
	 	this.identdate1 = newIdentdate1;
	} 	  
	/**
	 * 属性pk_image_ident的Getter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @return String
	 */
	public String getPk_image_ident () {
		return pk_image_ident;
	}   
	/**
	 * 属性pk_image_ident的Setter方法.
	 * 创建日期:2014-12-05 22:02:57
	 * @param newPk_image_ident String
	 */
	public void setPk_image_ident (String newPk_image_ident ) {
	 	this.pk_image_ident = newPk_image_ident;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-12-05 22:02:57
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-12-05 22:02:57
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_image_ident";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-12-05 22:02:57
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_image_ident";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-12-05 22:02:57
	  */
     public ImageIdentVO() {
		super();	
	}    
} 
