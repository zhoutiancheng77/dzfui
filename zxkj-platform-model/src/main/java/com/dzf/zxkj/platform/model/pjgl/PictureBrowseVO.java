package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 图片浏览VO
 * 
 * @author lbj
 *
 */
public class PictureBrowseVO extends SuperVO {
	@JsonProperty("corpId")
	private String pk_corp;
	private DZFDateTime ts;
	private String imgpath;
	private String smallimgpath;
	private String middleimgpath;
	private String coperatorid;
	private Integer dr;
	private DZFDate doperatedate;
	@JsonProperty("bid")
	private String pk_image_library;
	@JsonProperty("pid")
	private String pk_image_group;
	private String imgname;
	private DZFBoolean iscliped;
	private String clipedby;
	private DZFDateTime clipedon;
	private Integer identflag;
	private DZFDouble mny;
	private String zy;
	private String unit;
	private String settname;
	@JsonProperty("gcode")
	private String groupcode;
	@JsonProperty("corpname")
	private String unitname;
	@JsonProperty("corpcode")
	private String unitcode;
	@JsonProperty("isuer")
	private DZFBoolean isuer;
	@JsonProperty("coperatorname")
	private String user_name;
	private DZFDate cvoucherdate;
	private Integer istate;
	private Integer sourcemode;
	private DZFBoolean isback;
	private String imgmd;
	private Integer pzdt;
	private String fp_hm;
	private DZFDate kprq;
	private String ghfmc;
	private String xhfmc;
	private String xhf_nsrsbh;
	private String xmmc;
	private DZFDouble kphjje;
	@JsonProperty("tickhid")
	private String pk_ticket_h;
	// 票据类型
	@JsonProperty("pjlxzt")
	private Integer pjlxstatus;
	// 结算方式
	@JsonProperty("smode")
	private String settlemode;
	// 摘要（票据类型）
	private String memo;
	// 摘要
	private String memo1;

	// 0-- 非识别  1----识别
	private int iautorecognize;
	
	private OcrInvoiceVO invoice_info;
	
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
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

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getPk_image_library() {
		return pk_image_library;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public String getImgname() {
		return imgname;
	}

	public void setImgname(String imgname) {
		this.imgname = imgname;
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

	public Integer getPzdt() {
		return pzdt;
	}

	public void setPzdt(Integer pzdt) {
		this.pzdt = pzdt;
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

	public Integer getPjlxstatus() {
		return pjlxstatus;
	}

	public void setPjlxstatus(Integer pjlxstatus) {
		this.pjlxstatus = pjlxstatus;
	}

	public String getSettlemode() {
		return settlemode;
	}

	public void setSettlemode(String settlemode) {
		this.settlemode = settlemode;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getMemo1() {
		return memo1;
	}

	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}

	public int getIautorecognize() {
		return iautorecognize;
	}

	public void setIautorecognize(int iautorecognize) {
		this.iautorecognize = iautorecognize;
	}

	public OcrInvoiceVO getInvoice_info() {
		return invoice_info;
	}

	public void setInvoice_info(OcrInvoiceVO invoice_info) {
		this.invoice_info = invoice_info;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}
}
