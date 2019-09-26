package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.base.model.SuperVO;

public class FastOcrStateInfoVO extends SuperVO {

	/**
	 * 扫描上传后 返回的状态vo
	 */
	private static final long serialVersionUID = 1L;
	private Integer istate;// 图片状态
	private String vstate;// 图片状态
	private String sourceid;// 扫描上传id
	private String pk_image_ocrlibrary;// ocr识别id
	private String pk_tzpz_h;// 凭证id
	private String pzh;// 凭证号
	private String doperatedate;// 凭证日期
	private Integer iautorecognize;// 是否识别
	private String pk_corp;// 公司
	private Integer dr;// 图片是否删除
	private String dlastrowtime;
	private String imgname;
	
	public String getImgname() {
		return imgname;
	}

	public void setImgname(String imgname) {
		this.imgname = imgname;
	}

	public Integer getIstate() {
		return istate;
	}

	public String getVstate() {
		return vstate;
	}

	public String getSourceid() {
		return sourceid;
	}

	public String getPk_image_ocrlibrary() {
		return pk_image_ocrlibrary;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public String getPzh() {
		return pzh;
	}

	public String getDoperatedate() {
		return doperatedate;
	}

	public Integer getIautorecognize() {
		return iautorecognize;
	}

	public void setIstate(Integer istate) {
		this.istate = istate;
	}

	public void setVstate(String vstate) {
		this.vstate = vstate;
	}

	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}

	public void setPk_image_ocrlibrary(String pk_image_ocrlibrary) {
		this.pk_image_ocrlibrary = pk_image_ocrlibrary;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public void setDoperatedate(String doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setIautorecognize(Integer iautorecognize) {
		this.iautorecognize = iautorecognize;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getDlastrowtime() {
		return dlastrowtime;
	}

	public void setDlastrowtime(String dlastrowtime) {
		this.dlastrowtime = dlastrowtime;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
