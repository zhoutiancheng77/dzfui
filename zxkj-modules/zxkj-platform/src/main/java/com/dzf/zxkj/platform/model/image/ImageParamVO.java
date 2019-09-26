package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.base.model.SuperVO;

public class ImageParamVO extends SuperVO {

	public ImageParamVO() {
	}
	
	private String pic_status;
	private String begindate;
	private String enddate;
	private String begindate2;
	private String enddate2;
	private String serdate;
	private String pk_corp;
	private String group1;
	private String group2;
	private String nowpage;
	private String pagesize;
	private String pjlxzt;//票据类型
	//智能识别类型 1入库发票,2销项发票,3银行票据,4其他票据
	private String recognition_type;
	// 图片id
	private String imgIds;
	// 图片组id
	private String imgGroupIds;

	public String getImgIds() {
		return imgIds;
	}

	public void setImgIds(String imgIds) {
		this.imgIds = imgIds;
	}

	public String getImgGroupIds() {
		return imgGroupIds;
	}

	public void setImgGroupIds(String imgGroupIds) {
		this.imgGroupIds = imgGroupIds;
	}

	public String getPic_status() {
		return pic_status;
	}

	public void setPic_status(String pic_status) {
		this.pic_status = pic_status;
	}

	public String getBegindate() {
		return begindate;
	}

	public void setBegindate(String begindate) {
		this.begindate = begindate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getGroup1() {
		return group1;
	}

	public void setGroup1(String group1) {
		this.group1 = group1;
	}

	public String getGroup2() {
		return group2;
	}

	public void setGroup2(String group2) {
		this.group2 = group2;
	}

	public String getNowpage() {
		return nowpage;
	}

	public void setNowpage(String nowpage) {
		this.nowpage = nowpage;
	}

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	public String getPjlxzt() {
		return pjlxzt;
	}

	public void setPjlxzt(String pjlxzt) {
		this.pjlxzt = pjlxzt;
	}

	public String getRecognition_type() {
		return recognition_type;
	}

	public void setRecognition_type(String recognition_type) {
		this.recognition_type = recognition_type;
	}

	public String getBegindate2() {
		return begindate2;
	}

	public String getEnddate2() {
		return enddate2;
	}

	public String getSerdate() {
		return serdate;
	}

	public void setBegindate2(String begindate2) {
		this.begindate2 = begindate2;
	}

	public void setEnddate2(String enddate2) {
		this.enddate2 = enddate2;
	}

	public void setSerdate(String serdate) {
		this.serdate = serdate;
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
