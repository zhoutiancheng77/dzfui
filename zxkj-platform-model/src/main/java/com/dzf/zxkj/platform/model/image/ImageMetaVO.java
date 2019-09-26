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
 * 创建日期:2014-12-05 22:03:29
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class ImageMetaVO extends SuperVO {
	private String pk_corp;
	private Integer zy;
	private String pk_image_meta;
	private DZFDateTime ts;
	private Integer clipkind;
	private String coperatorid;
	private String metacode;
	private Integer zx;
	private Integer zprop;
	private Integer zwidth;
	private Integer zheight;
	private DZFDate doperatedate;
	private Integer dr;
	private String pk_image_library;
	private DZFBoolean isindent;
	private String imgpath;   // 图片路径
	private String imgname;  // 图片名称

	public static final String PK_CORP = "pk_corp";
	public static final String ZY = "zy";
	public static final String PK_IMAGE_META = "pk_image_meta";
	public static final String CLIPKIND = "clipkind";
	public static final String COPERATORID = "coperatorid";
	public static final String METACODE = "metacode";
	public static final String ZX = "zx";
	public static final String ZPROP = "zprop";
	public static final String ZWIDTH = "zwidth";
	public static final String ZHEIGHT = "zheight";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String PK_IMAGE_LIBRARY = "pk_image_library";
			
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	public DZFBoolean getIsindent() {
		return isindent;
	}
	public void setIsindent(DZFBoolean isindent) {
		this.isindent = isindent;
	}
	/**
	 * 属性zy的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return UFDouble
	 */
	public Integer getZy () {
		return zy;
	}   
	/**
	 * 属性zy的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newZy UFDouble
	 */
	public void setZy (Integer newZy ) {
	 	this.zy = newZy;
	} 	  
	/**
	 * 属性pk_image_meta的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return String
	 */
	public String getPk_image_meta () {
		return pk_image_meta;
	}   
	/**
	 * 属性pk_image_meta的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newPk_image_meta String
	 */
	public void setPk_image_meta (String newPk_image_meta ) {
	 	this.pk_image_meta = newPk_image_meta;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性clipkind的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return UFDouble
	 */
	public Integer getClipkind () {
		return clipkind;
	}   
	/**
	 * 属性clipkind的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newClipkind UFDouble
	 */
	public void setClipkind (Integer newClipkind ) {
	 	this.clipkind = newClipkind;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性metacode的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return String
	 */
	public String getMetacode () {
		return metacode;
	}   
	/**
	 * 属性metacode的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newMetacode String
	 */
	public void setMetacode (String newMetacode ) {
	 	this.metacode = newMetacode;
	} 	  
	/**
	 * 属性zx的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return UFDouble
	 */
	public Integer getZx () {
		return zx;
	}   
	/**
	 * 属性zx的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newZx UFDouble
	 */
	public void setZx (Integer newZx ) {
	 	this.zx = newZx;
	} 	  
	/**
	 * 属性zprop的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return UFDouble
	 */
	public Integer getZprop () {
		return zprop;
	}   
	/**
	 * 属性zprop的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newZprop UFDouble
	 */
	public void setZprop (Integer newZprop ) {
	 	this.zprop = newZprop;
	} 	  
	/**
	 * 属性zwidth的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return UFDouble
	 */
	public Integer getZwidth () {
		return zwidth;
	}   
	/**
	 * 属性zwidth的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newZwidth UFDouble
	 */
	public void setZwidth (Integer newZwidth ) {
	 	this.zwidth = newZwidth;
	} 	  
	/**
	 * 属性zheight的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return UFDouble
	 */
	public Integer getZheight () {
		return zheight;
	}   
	/**
	 * 属性zheight的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newZheight UFDouble
	 */
	public void setZheight (Integer newZheight ) {
	 	this.zheight = newZheight;
	} 	  
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	
	public String getImgpath() {
		return imgpath;
	}
	
	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}
	
	public String getImgname() {
		return imgname;
	}
	public void setImgname(String imgname) {
		this.imgname = imgname;
	}
	/**
	 * 属性pk_image_library的Getter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @return String
	 */
	public String getPk_image_library () {
		return pk_image_library;
	}   
	/**
	 * 属性pk_image_library的Setter方法.
	 * 创建日期:2014-12-05 22:03:29
	 * @param newPk_image_library String
	 */
	public void setPk_image_library (String newPk_image_library ) {
	 	this.pk_image_library = newPk_image_library;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-12-05 22:03:29
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-12-05 22:03:29
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_image_meta";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-12-05 22:03:29
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_image_meta";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-12-05 22:03:29
	  */
     public ImageMetaVO() {
		super();	
	}    
} 
