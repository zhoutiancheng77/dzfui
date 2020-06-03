package com.dzf.zxkj.app.model.ticket;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 
 * 票通生成凭证中间表主表
 *
 */
public class ImageTicketHVO extends SuperVO {
	
	private String pk_ticket_h;//主键
	private String coperatorid;
	private DZFDate doperatedate;
	private Integer dr;
	private String pk_corp;
	private String pk_image_group;//图片组PK
	private DZFBoolean ishasimg;//是否生成图片
	private DZFBoolean istogl;//是否生成凭证
	private String yfp_dm;
	private String yfp_hm;
	private String fp_dm;//发票代码
	private String fp_hm;//发票号码
	private String jym;//校验码
	private String ghf_nsrsbh;//购货方识别号
	private String ghfmc;//购货方名称
	private String xhf_nsrsbh;//销货方识别号
	private String xhfmc;//销货方名称
	private String kplx;//开票类型
	private DZFDate kprq;//开票日期
	private DZFDouble hjbhsje;//不含税金额
	private DZFDouble kphjse;//税额
	private DZFDouble kphjje;//开票合计金额
	private String imgpath;
	private String pdfpath;
	private DZFDateTime ts;
	private Integer iverion;
	private String curtimestamp;
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	private String vdef6;
	private String vdef7;
	private String vdef8;
	private String vdef9;
	private String vdef10;
	
	public String getPk_ticket_h() {
		return pk_ticket_h;
	}

	public void setPk_ticket_h(String pk_ticket_h) {
		this.pk_ticket_h = pk_ticket_h;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public DZFBoolean getIshasimg() {
		return ishasimg;
	}

	public void setIshasimg(DZFBoolean ishasimg) {
		this.ishasimg = ishasimg;
	}

	public DZFBoolean getIstogl() {
		return istogl;
	}

	public void setIstogl(DZFBoolean istogl) {
		this.istogl = istogl;
	}

	public String getYfp_dm() {
		return yfp_dm;
	}

	public void setYfp_dm(String yfp_dm) {
		this.yfp_dm = yfp_dm;
	}

	public String getYfp_hm() {
		return yfp_hm;
	}

	public void setYfp_hm(String yfp_hm) {
		this.yfp_hm = yfp_hm;
	}

	public String getFp_dm() {
		return fp_dm;
	}

	public void setFp_dm(String fp_dm) {
		this.fp_dm = fp_dm;
	}

	public String getFp_hm() {
		return fp_hm;
	}

	public void setFp_hm(String fp_hm) {
		this.fp_hm = fp_hm;
	}

	public String getJym() {
		return jym;
	}

	public void setJym(String jym) {
		this.jym = jym;
	}

	public String getGhf_nsrsbh() {
		return ghf_nsrsbh;
	}

	public void setGhf_nsrsbh(String ghf_nsrsbh) {
		this.ghf_nsrsbh = ghf_nsrsbh;
	}

	public String getGhfmc() {
		return ghfmc;
	}

	public void setGhfmc(String ghfmc) {
		this.ghfmc = ghfmc;
	}

	public String getXhf_nsrsbh() {
		return xhf_nsrsbh;
	}

	public void setXhf_nsrsbh(String xhf_nsrsbh) {
		this.xhf_nsrsbh = xhf_nsrsbh;
	}

	public String getXhfmc() {
		return xhfmc;
	}

	public void setXhfmc(String xhfmc) {
		this.xhfmc = xhfmc;
	}

	public String getKplx() {
		return kplx;
	}

	public void setKplx(String kplx) {
		this.kplx = kplx;
	}

	public DZFDate getKprq() {
		return kprq;
	}

	public void setKprq(DZFDate kprq) {
		this.kprq = kprq;
	}

	public DZFDouble getHjbhsje() {
		return hjbhsje;
	}

	public void setHjbhsje(DZFDouble hjbhsje) {
		this.hjbhsje = hjbhsje;
	}

	public DZFDouble getKphjse() {
		return kphjse;
	}

	public void setKphjse(DZFDouble kphjse) {
		this.kphjse = kphjse;
	}

	public DZFDouble getKphjje() {
		return kphjje;
	}

	public void setKphjje(DZFDouble kphjje) {
		this.kphjje = kphjje;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getIverion() {
		return iverion;
	}

	public void setIverion(Integer iverion) {
		this.iverion = iverion;
	}

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public String getVdef6() {
		return vdef6;
	}

	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}

	public String getVdef7() {
		return vdef7;
	}

	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}

	public String getVdef8() {
		return vdef8;
	}

	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}

	public String getVdef9() {
		return vdef9;
	}

	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
	}

	public String getVdef10() {
		return vdef10;
	}

	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}
	
	public String getCurtimestamp() {
		return curtimestamp;
	}

	public void setCurtimestamp(String curtimestamp) {
		this.curtimestamp = curtimestamp;
	}
	
	public String getImgpath() {
		return imgpath;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}

	public String getPdfpath() {
		return pdfpath;
	}

	public void setPdfpath(String pdfpath) {
		this.pdfpath = pdfpath;
	}

	@Override
	public String getPKFieldName() {
		return "pk_ticket_h";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_ticket_h";
	}

}
