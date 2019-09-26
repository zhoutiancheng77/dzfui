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
public class ImageGroupVO extends SuperVO {
//	图片组
	@JsonProperty("corp")
	private String pk_corp;
	private DZFDateTime ts;
	private String sessionflag;
	private String coperatorid;
	private Integer dr;
	@JsonProperty("ddate")
	private DZFDate doperatedate;
	@JsonProperty("pid")
	private String pk_image_group;
	@JsonProperty("gcode")
//	图片组编码（也是目录  /ImageUpload/公司编码/图片组编码/uuid.后缀）
	private String groupcode;
//	是否修剪过
	private DZFBoolean iscliped;
//	修剪用户ID
	private String clipedby;
//	修剪日期
	private DZFDateTime clipedon;
//	是否跳过
	private DZFBoolean isskiped;
//	跳过日期
	private String skipedby;
//	跳过操作人
	private DZFDateTime skipedon;
	@JsonProperty("smode")
	private String settlemode;//付款方式
	@JsonProperty("ct")
	private DZFBoolean cert;//证件图片上传标识
	@JsonProperty("ctx")
	private String certtx;//证件说明
	private DZFBoolean isuer;//是否已经使用/占用
	private String zdrmc;//制单人名称
	
	private DZFDouble mny;//金额
	private String memo;//摘要
	
	
	private String certbusitype;//业务类型
	private String certctnum;//联系方式
	private String certmsg;//业务合作留言
	private Integer imagecounts;//图片数量
	
	private String otcorp;//对方单位
	
	private DZFBoolean  ishd;//是否华道
	private DZFBoolean isfj;//是否分检
	private DZFBoolean isdb;//是否打包
	private Integer fpstyle;//发票类型索引
	private String fpstylecode;//发票类型编码
	private String  fpstylename;//发票类型名称
	private DZFDate fjdate;//分检日期
	private String fjr;//分检人
	private DZFDate dbdate;//打包日期
	private String cdbcorpid;//打包人
	private String szstylecode;//收支编码
	private String szstylename;//收支名称
	private Integer sourcemode;//图片来源
	private String pk_ticket_h;//票通主键
	
	private Integer istate;//单据状态[0---代表直接生成凭证,1--------------代表已生成图片预凭证]
	private DZFDate cvoucherdate;//生成凭证日期
	private String vapprovetor;//审批人---------大账房app走审批流使用--------
	private String memo1;//备注
	
	@JsonProperty("ppfid")
	private String picflowid;//存储图片流程主表编号
	@JsonProperty("pjlxzt")
	private Integer pjlxstatus;//票据类型状态
	private String pzh;//ocr识别先预占的凭证号 （传值）
	private OcrInvoiceVO invoice_info;// 发票识别信息
	private String corpcode;//（传值）
	
	public String getOtcorp() {
		return otcorp;
	}
	public void setOtcorp(String otcorp) {
		this.otcorp = otcorp;
	}
	public Integer getImagecounts() {
		return imagecounts;
	}
	public void setImagecounts(Integer imagecounts) {
		this.imagecounts = imagecounts;
	}
	
	public String getCertbusitype() {
		return certbusitype;
	}
	public void setCertbusitype(String certbusitype) {
		this.certbusitype = certbusitype;
	}
	public String getCertctnum() {
		return certctnum;
	}
	public void setCertctnum(String certctnum) {
		this.certctnum = certctnum;
	}
	public String getCertmsg() {
		return certmsg;
	}
	public void setCertmsg(String certmsg) {
		this.certmsg = certmsg;
	}
	
	public DZFDouble getMny() {
		return mny;
	}
	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getCerttx() {
		return certtx;
	}
	public void setCerttx(String certtx) {
		this.certtx = certtx;
	}
	public DZFBoolean getCert() {
		return cert;
	}
	public void setCert(DZFBoolean cert) {
		this.cert = cert;
	}
	public String getSettlemode() {
		return settlemode;
	}
	public void setSettlemode(String settlemode) {
		this.settlemode = settlemode;
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
	 * 属性sessionflag的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return String
	 */
	public String getSessionflag () {
		return sessionflag;
	}   
	/**
	 * 属性sessionflag的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newSessionflag String
	 */
	public void setSessionflag (String newSessionflag ) {
	 	this.sessionflag = newSessionflag;
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
	public DZFBoolean getIsskiped() {
		return isskiped;
	}
	public void setIsskiped(DZFBoolean isskiped) {
		this.isskiped = isskiped;
	}
	public String getSkipedby() {
		return skipedby;
	}
	public void setSkipedby(String skipedby) {
		this.skipedby = skipedby;
	}
	public DZFDateTime getSkipedon() {
		return skipedon;
	}
	public void setSkipedon(DZFDateTime skipedon) {
		this.skipedon = skipedon;
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
	 * 属性groupcode的Getter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @return String
	 */
	public String getGroupcode () {
		return groupcode;
	}   
	/**
	 * 属性groupcode的Setter方法.
	 * 创建日期:2014-12-06 11:54:12
	 * @param newGroupcode String
	 */
	public void setGroupcode (String newGroupcode ) {
	 	this.groupcode = newGroupcode;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2014-12-06 11:54:12
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	}

	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2014-12-06 11:54:12
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_image_group";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2014-12-06 11:54:12
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_image_group";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2014-12-06 11:54:12
	  */
     public ImageGroupVO() {
		super();	
	}
	public DZFBoolean getIsuer() {
		return isuer;
	}
	public void setIsuer(DZFBoolean isuer) {
		this.isuer = isuer;
	}
	public String getZdrmc() {
		return zdrmc;
	}
	public void setZdrmc(String zdrmc) {
		this.zdrmc = zdrmc;
	}
	public DZFBoolean getIshd() {
		return ishd;
	}
	public void setIshd(DZFBoolean ishd) {
		this.ishd = ishd;
	}
	public DZFBoolean getIsfj() {
		return isfj;
	}
	public void setIsfj(DZFBoolean isfj) {
		this.isfj = isfj;
	}
	public DZFBoolean getIsdb() {
		return isdb;
	}
	public void setIsdb(DZFBoolean isdb) {
		this.isdb = isdb;
	}
	public Integer getFpstyle() {
		return fpstyle;
	}
	public void setFpstyle(Integer fpstyle) {
		this.fpstyle = fpstyle;
	}
	public String getFpstylename() {
		return fpstylename;
	}
	public void setFpstylename(String fpstylename) {
		this.fpstylename = fpstylename;
	}
	public String getFpstylecode() {
		return fpstylecode;
	}
	public void setFpstylecode(String fpstylecode) {
		this.fpstylecode = fpstylecode;
	}
	public DZFDate getDbdate() {
		return dbdate;
	}
	public void setDbdate(DZFDate dbdate) {
		this.dbdate = dbdate;
	}
	public String getCdbcorpid() {
		return cdbcorpid;
	}
	public void setCdbcorpid(String cdbcorpid) {
		this.cdbcorpid = cdbcorpid;
	}
	public DZFDate getFjdate() {
		return fjdate;
	}
	public void setFjdate(DZFDate fjdate) {
		this.fjdate = fjdate;
	}
	public String getFjr() {
		return fjr;
	}
	public void setFjr(String fjr) {
		this.fjr = fjr;
	}
	public String getSzstylecode() {
		return szstylecode;
	}
	public void setSzstylecode(String szstylecode) {
		this.szstylecode = szstylecode;
	}
	public String getSzstylename() {
		return szstylename;
	}
	public void setSzstylename(String szstylename) {
		this.szstylename = szstylename;
	}
	public Integer getIstate() {
		return istate;
	}
	public void setIstate(Integer istate) {
		this.istate = istate;
	}
	public DZFDate getCvoucherdate() {
		return cvoucherdate;
	}
	public void setCvoucherdate(DZFDate cvoucherdate) {
		this.cvoucherdate = cvoucherdate;
	}
	public Integer getSourcemode() {
		return sourcemode;
	}
	public void setSourcemode(Integer sourcemode) {
		this.sourcemode = sourcemode;
	}
	public String getPk_ticket_h() {
		return pk_ticket_h;
	}
	public void setPk_ticket_h(String pk_ticket_h) {
		this.pk_ticket_h = pk_ticket_h;
	}
	public String getVapprovetor() {
		return vapprovetor;
	}
	public void setVapprovetor(String vapprovetor) {
		this.vapprovetor = vapprovetor;
	}
	public String getMemo1() {
		return memo1;
	}
	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}
	public String getPicflowid() {
		return picflowid;
	}
	public void setPicflowid(String picflowid) {
		this.picflowid = picflowid;
	}
	public Integer getPjlxstatus() {
		return pjlxstatus;
	}
	public void setPjlxstatus(Integer pjlxstatus) {
		this.pjlxstatus = pjlxstatus;
	}
	public String getPzh() {
		return pzh;
	}
	public void setPzh(String pzh) {
		this.pzh = pzh;
	}
	public OcrInvoiceVO getInvoice_info() {
		return invoice_info;
	}
	public void setInvoice_info(OcrInvoiceVO invoice_info) {
		this.invoice_info = invoice_info;
	}
	public String getCorpcode() {
		return corpcode;
	}
	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}
	
} 
