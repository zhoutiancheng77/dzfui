package com.dzf.zxkj.platform.model.image;


import com.dzf.zxkj.base.model.SuperVO;
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
 * 创建日期:2014-12-06 11:54:12
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class ImageLibraryVO extends SuperVO {
	@JsonProperty("corpId")
	private String pk_corp;
	private DZFDateTime ts;
	private String imgpath;//图片路径
	private String smallimgpath;//小图路径
	private String middleimgpath;//中图路径
	private String pdfpath;//pdf原文件路径
	private String coperatorid;
	private Integer dr;
	private DZFDate doperatedate;//上传日期
	@JsonProperty("bid")
	private String pk_image_library;
	@JsonProperty("pid")
	private String pk_image_group;
//	生成的名称
	private String imgname;
	private DZFBoolean iscliped;
	private String clipedby;
	private DZFDateTime clipedon;
	private Integer identflag;   // 识图程度
	private DZFDouble mny;//金额
	private String zy;//摘要
	private String unit;//对方单位
	private String settname;//结算方式
	@JsonProperty("gcode")
	private String groupcode;//图片组号
	
	@JsonProperty("corpname")
	private String unitname;
	@JsonProperty("isuer")
	private DZFBoolean isuer;//是否已经使用/占用
	@JsonProperty("coperatorname")
	private String user_name;//上传者的名字
	
	//
	private DZFDate cvoucherdate;//生成凭证日期
	private Integer istate;//数据库不存，仅展示使用
	private Integer sourcemode; ////数据库不存，仅展示使用
	private DZFBoolean isback;
	private String imgmd;//md5码值
	private Integer pzdt;//凭证状态(-1暂存态/8自由态)
	private OcrInvoiceVO invoice_info;
	
	// 合并前图片组
	private String old_pk_image_group;
	
	//----------------供票通使用 begin
	private String fp_hm;
	private DZFDate kprq;
	private String ghfmc;
	private String xhfmc;
	private String xhf_nsrsbh;
	private String xmmc;
	private DZFDouble kphjje;
	@JsonProperty("tickhid")
	private String pk_ticket_h;
	//-------------------------end
	
	private String pk_uploadcorp;//图片上传的公司(不变)
	public static final String PK_CORP = "pk_corp";
	public static final String IMGPATH = "imgpath";
	public static final String COPERATORID = "coperatorid";
	public static final String DOPERATEDATE = "doperatedate";
	public static final String PK_IMAGE_LIBRARY = "pk_image_library";
	public static final String PK_IMAGE_GROUP = "pk_image_group";
	public static final String IMGNAME = "imgname";
			
	public String getPk_uploadcorp() {
		return pk_uploadcorp;
	}
	public void setPk_uploadcorp(String pk_uploadcorp) {
		this.pk_uploadcorp = pk_uploadcorp;
	}
	/**
	 * 属性pk_corp的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return String
	 */
	public String getPk_corp () {
		return pk_corp;
	}   
	/**
	 * 属性pk_corp的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newPk_corp String
	 */
	public void setPk_corp (String newPk_corp ) {
	 	this.pk_corp = newPk_corp;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newTs DZFDateTime
	 */
	public void setTs (DZFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
	/**
	 * 属性imgpath的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return String
	 */
	public String getImgpath () {
		return imgpath;
	}   
	/**
	 * 属性imgpath的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newImgpath String
	 */
	public void setImgpath (String newImgpath ) {
	 	this.imgpath = newImgpath;
	} 	  
	/**
	 * 属性coperatorid的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return String
	 */
	public String getCoperatorid () {
		return coperatorid;
	}   
	/**
	 * 属性coperatorid的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newCoperatorid String
	 */
	public void setCoperatorid (String newCoperatorid ) {
	 	this.coperatorid = newCoperatorid;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return UFDouble
	 */
	public Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newDr UFDouble
	 */
	public void setDr (Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	public DZFBoolean getIscliped() {
		return iscliped;
	}
	public void setIscliped(DZFBoolean iscliped) {
		this.iscliped = iscliped;
	}
	public String getClipedby() {
		return clipedby;
	}
	public void setClipedby(String clipedby) {
		this.clipedby = clipedby;
	}
	public DZFDateTime getClipedon() {
		return clipedon;
	}
	public void setClipedon(DZFDateTime clipedon) {
		this.clipedon = clipedon;
	}
	public Integer getIdentflag() {
		return identflag;
	}
	public void setIdentflag(Integer identflag) {
		this.identflag = identflag;
	}
	/**
	 * 属性doperatedate的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return DZFDate
	 */
	public DZFDate getDoperatedate () {
		return doperatedate;
	}   
	/**
	 * 属性doperatedate的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newDoperatedate DZFDate
	 */
	public void setDoperatedate (DZFDate newDoperatedate ) {
	 	this.doperatedate = newDoperatedate;
	} 	  
	/**
	 * 属性pk_image_library的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return String
	 */
	public String getPk_image_library () {
		return pk_image_library;
	}   
	/**
	 * 属性pk_image_library的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newPk_image_library String
	 */
	public void setPk_image_library (String newPk_image_library ) {
	 	this.pk_image_library = newPk_image_library;
	} 	  
	/**
	 * 属性pk_image_group的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return String
	 */
	public String getPk_image_group () {
		return pk_image_group;
	}   
	/**
	 * 属性pk_image_group的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newPk_image_group String
	 */
	public void setPk_image_group (String newPk_image_group ) {
	 	this.pk_image_group = newPk_image_group;
	} 	  
	/**
	 * 属性imgname的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return String
	 */
	public String getImgname () {
		return imgname;
	}   
	/**
	 * 属性imgname的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newImgname String
	 */
	public void setImgname (String newImgname ) {
	 	this.imgname = newImgname;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-12-06 11:54:12
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
		return "PK_IMAGE_GROUP";
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-12-06 11:54:12
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_image_library";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-12-06 11:54:12
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_image_library";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-12-06 11:54:12
	  */
    public ImageLibraryVO() {
		super();	
	}
	public DZFDouble getMny() {
		return mny;
	}
	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}
	public String getZy() {
		return zy;
	}
	public void setZy(String zy) {
		this.zy = zy;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getSettname() {
		return settname;
	}
	public void setSettname(String settname) {
		this.settname = settname;
	}
	public String getGroupcode() {
		return groupcode;
	}
	public void setGroupcode(String groupcode) {
		this.groupcode = groupcode;
	}
	public String getUnitname() {
		return unitname;
	}
	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}
	public DZFBoolean getIsuer() {
		return isuer;
	}
	public void setIsuer(DZFBoolean isuer) {
		this.isuer = isuer;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public DZFDate getCvoucherdate() {
		return cvoucherdate;
	}
	public void setCvoucherdate(DZFDate cvoucherdate) {
		this.cvoucherdate = cvoucherdate;
	}
	public Integer getIstate() {
		return istate;
	}
	public void setIstate(Integer istate) {
		this.istate = istate;
	}
	
	public Integer getSourcemode() {
		return sourcemode;
	}
	public void setSourcemode(Integer sourcemode) {
		this.sourcemode = sourcemode;
	}
	public DZFBoolean getIsback() {
		return isback;
	}
	public void setIsback(DZFBoolean isback) {
		this.isback = isback;
	}
	public String getImgmd() {
		return imgmd;
	}
	public void setImgmd(String imgmd) {
		this.imgmd = imgmd;
	}
	public String getFp_hm() {
		return fp_hm;
	}
	public void setFp_hm(String fp_hm) {
		this.fp_hm = fp_hm;
	}
	public DZFDate getKprq() {
		return kprq;
	}
	public void setKprq(DZFDate kprq) {
		this.kprq = kprq;
	}
	public String getGhfmc() {
		return ghfmc;
	}
	public void setGhfmc(String ghfmc) {
		this.ghfmc = ghfmc;
	}
	public String getXhfmc() {
		return xhfmc;
	}
	public void setXhfmc(String xhfmc) {
		this.xhfmc = xhfmc;
	}
	public String getXhf_nsrsbh() {
		return xhf_nsrsbh;
	}
	public void setXhf_nsrsbh(String xhf_nsrsbh) {
		this.xhf_nsrsbh = xhf_nsrsbh;
	}
	public String getXmmc() {
		return xmmc;
	}
	public void setXmmc(String xmmc) {
		this.xmmc = xmmc;
	}
	public DZFDouble getKphjje() {
		return kphjje;
	}
	public void setKphjje(DZFDouble kphjje) {
		this.kphjje = kphjje;
	}
	public String getPk_ticket_h() {
		return pk_ticket_h;
	}
	public void setPk_ticket_h(String pk_ticket_h) {
		this.pk_ticket_h = pk_ticket_h;
	}
	public Integer getPzdt() {
		return pzdt;
	}
	public void setPzdt(Integer pzdt) {
		this.pzdt = pzdt;
	}
	public String getSmallimgpath() {
		return smallimgpath;
	}
	public void setSmallimgpath(String smallimgpath) {
		this.smallimgpath = smallimgpath;
	}
	public String getMiddleimgpath() {
		return middleimgpath;
	}
	public void setMiddleimgpath(String middleimgpath) {
		this.middleimgpath = middleimgpath;
	}
	public String getPdfpath() {
		return pdfpath;
	}
	public void setPdfpath(String pdfpath) {
		this.pdfpath = pdfpath;
	}
	public OcrInvoiceVO getInvoice_info() {
		return invoice_info;
	}
	public void setInvoice_info(OcrInvoiceVO invoice_info) {
		this.invoice_info = invoice_info;
	}
	public String getOld_pk_image_group() {
		return old_pk_image_group;
	}
	public void setOld_pk_image_group(String old_pk_image_group) {
		this.old_pk_image_group = old_pk_image_group;
	}
	
} 
