package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2015-01-25 21:58:50
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class ImageMsgreaderVO extends SuperVO {
	private String pk_image_msgreader;
	private DZFDateTime ts;
	private String coperatorid;
	private DZFDateTime readtime;
	private String cuserid;
	private Integer dr;
	private DZFDate doperatedate;
	private String pk_image_returnmsg;
	private String pk_corp;
	//已读标记
	private DZFBoolean bread;
	
	public static final String PK_IMAGE_MSGREADER = "pk_image_msgreader";
	public static final String COPERATORID = "coperatorid";
	public static final String READTIME = "readtime";
	public static final String CUSERID = "cuserid";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String PK_IMAGE_RETURNMSG = "pk_image_returnmsg";
			
	
	public DZFBoolean getBread() {
		return bread;
	}
	public void setBread(DZFBoolean bread) {
		this.bread = bread;
	}
	/**
	 * 属性pk_image_msgreader的Getter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @return String
	 */
	public String getPk_image_msgreader () {
		return pk_image_msgreader;
	}   
	/**
	 * 属性pk_image_msgreader的Setter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @param newPk_image_msgreader String
	 */
	public void setPk_image_msgreader (String newPk_image_msgreader ) {
	 	this.pk_image_msgreader = newPk_image_msgreader;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性readtime的Getter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @return DZFDateTime
	 */
	public DZFDateTime getReadtime () {
		return readtime;
	}   
	/**
	 * 属性readtime的Setter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @param newReadtime DZFDateTime
	 */
	public void setReadtime (DZFDateTime newReadtime ) {
	 	this.readtime = newReadtime;
	} 	  
	/**
	 * 属性cuserid的Getter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @return String
	 */
	public String getCuserid () {
		return cuserid;
	}   
	/**
	 * 属性cuserid的Setter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @param newCuserid String
	 */
	public void setCuserid (String newCuserid ) {
	 	this.cuserid = newCuserid;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性pk_image_returnmsg的Getter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @return String
	 */
	public String getPk_image_returnmsg () {
		return pk_image_returnmsg;
	}   
	/**
	 * 属性pk_image_returnmsg的Setter方法.
	 * 创建日期:2015-01-25 21:58:50
	 * @param newPk_image_returnmsg String
	 */
	public void setPk_image_returnmsg (String newPk_image_returnmsg ) {
	 	this.pk_image_returnmsg = newPk_image_returnmsg;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2015-01-25 21:58:50
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
		return "PK_IMAGE_RETURNMSG";
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2015-01-25 21:58:50
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_image_msgreader";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2015-01-25 21:58:50
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_imgmsgreader";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2015-01-25 21:58:50
	  */
     public ImageMsgreaderVO() {
		super();	
	}  
 	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
} 
