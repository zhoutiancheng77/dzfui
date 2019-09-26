package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2015-01-25 21:58:49
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class ImageTurnMsgVO extends SuperVO {
	private String pk_corp;
	private DZFDateTime ts;
	private String pk_image_librarys;
	private String coperatorid;
	private Integer dr;
	private DZFDate doperatedate;
	private String message;
	private String pk_image_group;
	private String pk_image_returnmsg;
	private String settle;//结算方式
	private String bread;//已读标记
	private String updatetime;//图片上传时间，不存库的
	private String zy;//图片退回摘要

	public static final String PK_CORP = "pk_corp";
	public static final String PK_IMAGE_LIBRARYS = "pk_image_librarys";
	public static final String COPERATORID = "coperatorid";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String MESSAGE = "message";
	public static final String PK_IMAGE_GROUP = "pk_image_group";
	public static final String PK_IMAGE_RETURNMSG = "pk_image_returnmsg";
			
	
	public String getBread() {
		return bread;
	}
	public void setBread(String bread) {
		this.bread = bread;
	}
	public String getSettle() {
		return settle;
	}
	public void setSettle(String settle) {
		this.settle = settle;
	}
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性pk_image_librarys的Getter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @return String
	 */
	public String getPk_image_librarys () {
		return pk_image_librarys;
	}   
	/**
	 * 属性pk_image_librarys的Setter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @param newPk_image_librarys String
	 */
	public void setPk_image_librarys (String newPk_image_librarys ) {
	 	this.pk_image_librarys = newPk_image_librarys;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性message的Getter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @return String
	 */
	public String getMessage () {
		return message;
	}   
	/**
	 * 属性message的Setter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @param newMessage String
	 */
	public void setMessage (String newMessage ) {
	 	this.message = newMessage;
	} 	  
	/**
	 * 属性pk_image_group的Getter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @return String
	 */
	public String getPk_image_group () {
		return pk_image_group;
	}   
	/**
	 * 属性pk_image_group的Setter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @param newPk_image_group String
	 */
	public void setPk_image_group (String newPk_image_group ) {
	 	this.pk_image_group = newPk_image_group;
	} 	  
	/**
	 * 属性pk_image_returnmsg的Getter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @return String
	 */
	public String getPk_image_returnmsg () {
		return pk_image_returnmsg;
	}   
	/**
	 * 属性pk_image_returnmsg的Setter方法.
	 * 创建日期:2015-01-25 21:58:49
	 * @param newPk_image_returnmsg String
	 */
	public void setPk_image_returnmsg (String newPk_image_returnmsg ) {
	 	this.pk_image_returnmsg = newPk_image_returnmsg;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2015-01-25 21:58:49
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2015-01-25 21:58:49
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_image_returnmsg";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2015-01-25 21:58:49
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_imgreturnmsg";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2015-01-25 21:58:49
	  */
     public ImageTurnMsgVO() {
		super();	
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getZy() {
		return zy;
	}
	public void setZy(String zy) {
		this.zy = zy;
	}    
	
	
	
	
     
} 
